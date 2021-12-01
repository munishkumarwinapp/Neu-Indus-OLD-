package com.winapp.catalog;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.winapp.adapter.Attribute;
import com.winapp.adapter.CustomerAdapter;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.SoapAccessTask;
import com.winapp.offline.SoapAccessTask.CallbackInterface;
import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Catalog;
import com.winapp.helper.Constants;
import com.winapp.helper.SquareImageView;
import com.winapp.model.Customer;
import com.winapp.printer.UIHelper;
import com.winapp.sot.ColorAttributeDialog;
import com.winapp.sot.OfflineSalesOrderWebService;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SOTSummaryWebService;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.sotdetails.DBCatalog;
import com.winapp.sotdetails.SummaryEditDialogFragment;
import com.winapp.sotdetails.SummaryEditDialogFragment.SummaryEditDialogListener;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class CatalogCartFragment extends BaseFragment {
	private String mCustomerJsonString="",mCompanyCode="",mVanCode="";
	private ListView mCartListView;
	private Cursor mCursor;
	private CartAdapter mCartAdapter;
	private View view;
	private TextView mCartTotal, mCartTax, mCartNetTotal;
	private LinearLayout mCartLayout, mEmptyCartLayout;
	private EditText mCartCustomerName, mCartCustomerCode;
	private int mTextlength = 0, cartSize = 0;
	private AlertDialog mCustomerDialog = null;
	private CustomAlertAdapterSupp mCustomerAdapter;
	private ArrayList<HashMap<String, String>> customerArrHm,
			customerSearchArrHm, customerDataArrHm;
	private ArrayList<Customer> mCustomerArrList;
	private List<PropertyInfo> params;
	private String mCustomerCodeStr, mCustomerNameStr, mValidUrl,mobileHaveOfflineMode="",
			mSaveOrder = "", newProductCode = "", newPrice = "",
			newCprice = "", newTotal = "", newSubtotal = "", newTax = "",
			newNettotal = "", priceflag = "", itemDiscount = "", taxType = "",
			taxValue = "",  mDialogStatus="";
	private double tt, itmDisc = 0, netTtal = 0, taxAmount = 0;
	private DBCatalog dbcatalog;
	//private ImageButton mCartSaveIcon,mCartClearAll;
	private ImageView mDialogUpdateImgV,mDialogCancelImgV;
	private ViewPager mPager;
	private CarouselFragment mCarouselFragment;
	private OfflineDatabase offlineDatabase;
	private AlertDialog alert;
	private HashMap<String, ImageButton> mHashMapIcon;
	private JSONObject mCustomerJSONObject;
	private JSONArray mCustomerJSONArray;
	private HashMap<String, String> mHashMap;
	private UIHelper helper;
	private FrameLayout sMainLayout;
	 public CatalogCartFragment() {
	      // Required empty public constructor
	    }

	public static CatalogCartFragment newInstance(/*HashMap<String, ImageButton> mHashMapIcon,ArrayList<Customer> customerArrList, ArrayList<HashMap<String, String>> customerArrhm*/) {
		CatalogCartFragment frag = new CatalogCartFragment();
//		Bundle args = new Bundle();
//		args.putSerializable("CartIcons", mHashMapIcon);
//		args.putSerializable("CustomerObjArr", customerArrList);
//		args.putSerializable("CustomerArrHm", customerArrhm);
//		frag.setArguments(args);
		return frag;

	}
/*	public CatalogCartFragment(ImageButton mCartSaveIcon,ImageButton mCartClearAll,ArrayList<Customer> customerArrList, ArrayList<HashMap<String, String>> customerArrhm) {
		   	mCustomerArrList = new ArrayList<Customer>();
		   	customerDataArrHm = new ArrayList<HashMap<String,String>>();
		   	this.mCartSaveIcon = mCartSaveIcon;
			this.mCartClearAll = mCartClearAll;
		   	this.customerDataArrHm = customerArrhm;		
			this.mCustomerArrList = customerArrList;
		   }*/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_cart, container, false);
//		mCustomerArrList = new ArrayList<Customer>();
//		customerDataArrHm = new ArrayList<HashMap<String,String>>();
//		HashMap<String ,ImageButton> hm= (HashMap<String ,ImageButton>) getArguments().getSerializable("CartIcons");
//		mCustomerArrList = (ArrayList<Customer>)  getArguments().getSerializable("CustomerObjArr");
//		customerDataArrHm = (ArrayList<HashMap<String, String>>)  getArguments().getSerializable("CustomerArrHm");
//		mCartSaveIcon = hm.get("CartSaveIcon");
//		mCartClearAll = hm.get("CartClearAll");
		return view;
	}

	@Override
	public void onResume() {
		// View ID
		mPager = (ViewPager) getActivity().findViewById(R.id.viewpager);		
		mCartListView = (ListView) view.findViewById(R.id.cart_list);
	
		mEmptyCartLayout = (LinearLayout) view
				.findViewById(R.id.no_data);
		mCartLayout = (LinearLayout) view
				.findViewById(R.id.shopcart_layout);
		 mCartCustomerName = (EditText) view.findViewById(R.id.cart_customer);
	     mCartCustomerCode = (EditText) view.findViewById(R.id.cart_customer_code);
		mCartTotal = (TextView) view.findViewById(R.id.cart_total);
		mCartTax = (TextView) view.findViewById(R.id.cart_tax);
		mCartNetTotal = (TextView) view.findViewById(R.id.cart_nettotal);
		sMainLayout = (FrameLayout) view.findViewById(R.id.fragment_mainLayout);

		// Variable Object Initialization
		mHashMap = new HashMap<>();
		customerArrHm = new ArrayList<HashMap<String, String>>();
		dbcatalog = new DBCatalog(getActivity());
		helper = new UIHelper(getActivity());
		mCustomerArrList = new ArrayList<Customer>();
		customerDataArrHm = new ArrayList<>();

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();

		// DB Initialization
		DBCatalog.init(getActivity());   
        FWMSSettingsDatabase.init(getActivity());
        mCarouselFragment = new CarouselFragment();
	    offlineDatabase = new OfflineDatabase(getActivity());
	    mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());
		new OfflineSalesOrderWebService(getActivity());


        // Get VanCode from DB
        mVanCode = SOTDatabase.getVandriver();
    	//URL From DB;
    	mValidUrl = FWMSSettingsDatabase.getUrl();
    	
    	new SOTSummaryWebService(mValidUrl);
    	
    	//Get Catalog cart products from DB
        mCursor = DBCatalog.getCursor();
        
      //Get Cart size From cursor
        cartSize = mCursor.getCount();
        
      //Get CartonPrice from Pojo class
        priceflag = SalesOrderSetGet.getCartonpriceflag();

		mCartAdapter = new CartAdapter(getActivity(),
				R.layout.shoppingcart_item, mCursor);
		mCartListView.setAdapter(mCartAdapter);
		cartTotal();
		 mCartCustomerName.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
	    		   mCartCustomerName) {
				@Override
				public boolean onDrawableClick() {
                    Log.d("customerDataArrHm","-->"+customerDataArrHm.size());
					if(customerDataArrHm.size()>0){
						customerDialogAction();
					}else{
						new GetCustomerData().execute();
					}
					return true;
				}
			});
		SOCatalogActivity.mCartSaveIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveOrderDialog();			
			}
		});
		SOCatalogActivity.mCartClearAll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cartClearAllDialog();		
				}
			});
		super.onResume();
	}

	/*@Override
	public void refreshAdapter() {
		mCursor.requery();
		setValue();
		mCartAdapter.notifyDataSetChanged();
	}*/
    private void setValue(){
		double cartTotal = DBCatalog.getSubTotal();
		double cartTax = DBCatalog.getTax();
		double cartNetTotal = DBCatalog.getNetTotal();
		mCartTotal.setText(twoDecimalPoint(Double
				.valueOf(cartTotal)));
		mCartTax.setText(fourDecimalPoint(Double
				.valueOf(cartTax)));
		mCartNetTotal
				.setText(twoDecimalPoint(Double
						.valueOf(cartNetTotal)));
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

							customerDataArrHm.add(customerhm);

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
				if(customerDataArrHm.size()>0){
					customerDialogAction();
				}
				helper.dismissProgressView(sMainLayout);
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
	public void cartTotal() {
		mCursor = DBCatalog.getCursor();
		if (mCursor.getCount() > 0) {
			SOCatalogActivity.mCartSaveIcon.setVisibility(View.VISIBLE);
			mCartLayout.setVisibility(View.VISIBLE);			
			mEmptyCartLayout.setVisibility(View.GONE);	
			mCartCustomerCode.setText(Catalog.getCustomerCode());
			mCartCustomerName.setText(Catalog.getCustomerName());

			mCartTotal.setText(twoDecimalPoint(Double.valueOf(DBCatalog
					.getSubTotal())));
			mCartTax.setText(fourDecimalPoint(Double.valueOf(DBCatalog.getTax())));
			mCartNetTotal.setText(twoDecimalPoint(Double.valueOf(DBCatalog
					.getNetTotal())));

		} else {
			SOCatalogActivity.mCartSaveIcon.setVisibility(View.GONE);
			SOCatalogActivity.mCartClearAll.setVisibility(View.GONE);
			mEmptyCartLayout.setVisibility(View.VISIBLE);
			mCartLayout.setVisibility(View.GONE);
			
		}

	}
	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}
	private void cartClearAllDialog(){

		AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		builder.setTitle("Confirm Delete");
		builder.setIcon(R.mipmap.delete);
		builder.setMessage("Do you want to clear all the cart items?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				DBCatalog.deleteAllProduct();
				SOTDatabase.deleteAttribute();
				mCursor.requery();
				mCartAdapter.notifyDataSetChanged();			
				cartTotal();
				Catalog.setCustomerCode("");
				Catalog.setCustomerName("");				

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
    private void hideKeyboard(EditText text){
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(text.getWindowToken(), 0);
    }
	private void customerDialogAction() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
		final EditText editText = new EditText(getActivity());
		final ListView listview = new ListView(getActivity());
		LinearLayout layout = new LinearLayout(getActivity());
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

		mCustomerAdapter = new CustomAlertAdapterSupp(getActivity(), customerDataArrHm);
		listview.setAdapter(mCustomerAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
                hideKeyboard(editText);
				mCustomerDialog.dismiss();
				customerArrHm = mCustomerAdapter.getArrayList();
				HashMap<String, String> datavalue = customerArrHm.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					String values = (String) mapEntry.getValue();				
					
					
					if(values.contains("/")){
						String[] parts = values.split("/");
						String name = parts[0];

						mCartCustomerCode.setText(keyValues);
						mCartCustomerName.setText(name);
						Catalog.setCustomerCode(keyValues);
						Catalog.setCustomerName(name);
					}else{
						mCartCustomerCode.setText(keyValues);
						mCartCustomerName.setText(values);
						Catalog.setCustomerCode(keyValues);
						Catalog.setCustomerName(values);
					}					
					
					
					mCartCustomerCode.addTextChangedListener(new TextWatcher() {
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
							mTextlength = mCartCustomerCode.getText().length();
						}
					});					
				}
				
				loadCustomerTaxValue();
			}
		});

		customerSearchArrHm = new ArrayList<HashMap<String, String>>(customerDataArrHm);
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
						if (customerName.toLowerCase(Locale.getDefault()).contains(
								editText.getText().toString().toLowerCase(Locale.getDefault())
										.trim()))
							customerSearchArrHm.add(customerDataArrHm.get(i));
					}
				}

				mCustomerAdapter = new CustomAlertAdapterSupp(
						getActivity(), customerSearchArrHm);
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
	private void saveOrderDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Confirm Delete");
		builder.setIcon(R.mipmap.delete);
		builder.setMessage("Do you want to Save?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				if (cartSize > 0) {
					new SaveCartOrder().execute();
				} else {
					Toast.makeText(getActivity(),
							"Shopping cart is empty.", Toast.LENGTH_LONG)
							.show();
				}

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
	private void loadCustomerTaxValue() 
	{		
	Loop: for(int i=0;i<mCustomerArrList.size();i++){
		Customer customer = mCustomerArrList.get(i);
		if(customer.getCustomerCode().matches(Catalog.getCustomerCode())){
		mCustomerCodeStr = customer.getCustomerCode();
		mCustomerNameStr = customer.getCustomerName();
		String HaveTax = customer.getHaveTax();
		String TaxType = customer.getTaxType();
		String TaxValue = customer.getTaxValue();
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
		//For price update set update price as true
		Catalog.setUpdatedPrice(true);			
		Catalog.setSearchVisible(false);
		getCustomerPrice();
		break Loop;
		}
		}
		
	}
	private void getCustomerPrice() {

		//if price flag show null,default value 0
		String customerGroupCode = Catalog.getCustomerGroupCode();
		if (priceflag.matches("null") || priceflag.matches("")) {
			priceflag = "0";
		}
		if (customerGroupCode != null && !customerGroupCode.isEmpty()) {

			Cursor cartCursor = DBCatalog.getCursor();
			// cartSize = cursor.getCount();
			if (cartCursor != null && cartCursor.getCount() > 0) {

				if (cartCursor.moveToFirst()) {
					do {

						String ProductCode = cartCursor.getString(cartCursor
								.getColumnIndex(Constants.COLUMN_PRODUCT_CODE));
						String cartonPrice = cartCursor.getString(cartCursor
								.getColumnIndex(Constants.COLUMN_CARTONPRICE));
						String productPrice = cartCursor.getString(cartCursor
								.getColumnIndex(Constants.COLUMN_PRICE));
						String qty = cartCursor.getString(cartCursor
								.getColumnIndex(Constants.COLUMN_QUANTITY));
						String cQty = cartCursor.getString(cartCursor
								.getColumnIndex(Constants.COLUMN_CARTON_QTY));
						String lQty = cartCursor.getString(cartCursor
								.getColumnIndex(Constants.COLUMN_LOOSE_QTY));
						itemDiscount = cartCursor
								.getString(cartCursor
										.getColumnIndex(Constants.COLUMN_ITEM_DISCOUNT));
						taxType = cartCursor.getString(cartCursor
								.getColumnIndex(Constants.COLUMN_TAXTYPE));
						taxValue = cartCursor.getString(cartCursor
								.getColumnIndex(Constants.COLUMN_TAXVALUE));

						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("productCode", ProductCode);
						hm.put("cartonPrice", cartonPrice);
						hm.put("productPrice", productPrice);
						hm.put("qty", qty);
						hm.put("cQty", cQty);
						hm.put("lQty", lQty);
						// hm.put("pcsPerCarton", "pcsPerCarton");

						String cmpnyCode = SupplierSetterGetter
								.getCompanyCode();
						String customerCode = Catalog.getCustomerCode();
						// String customerGroupCode =
						// Catalog.getCustomerGroupCode();

						params = new ArrayList<PropertyInfo>();
						params.clear();
						params.add(newPropertyInfo("CompanyCode", cmpnyCode));
						params.add(newPropertyInfo("FormCode", "SO"));
						params.add(newPropertyInfo("CustomerGroupCode",customerGroupCode));
						params.add(newPropertyInfo("CustomerCode", customerCode));
						params.add(newPropertyInfo("ProductCode", ProductCode));

						Log.d("cmpnyCode", "aa" + cmpnyCode);
						Log.d("customerGroupCode", "bb" + customerGroupCode);
						Log.d("customerCode", "cc" + customerCode);
						Log.d("ProductCode", "dd" + ProductCode);

						new SoapAccessTask(getActivity(),mValidUrl, "fncGetProductPriceForSales",params, new GetPrice(hm)).execute();

					} while (cartCursor.moveToNext());
					cartCursor.requery();
				}
			}
		}

	
	}
	private class GetPrice implements CallbackInterface {

		String gCode, gPrice, gCprice, gQty, gCqty, gLqty;
		String Price = "", Cprice = "";

		public GetPrice(HashMap<String, String> hm) {
			this.gPrice = hm.get("productPrice");
			this.gCprice = hm.get("cartonPrice");
			this.gQty = hm.get("qty");
			this.gCqty = hm.get("cQty");
			this.gLqty = hm.get("lQty");
			this.gCode = hm.get("productCode");

		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {
				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);

					Price = object.getString("Price");
					Cprice = object.getString("CartonPrice");
					 Log.d("Price", "pppp"+Price);
					 Log.d("Cprice", "cpppp"+Cprice);

					if (Price != null && !Price.isEmpty()
							&& !Price.matches("0")) {
						newPrice = Price;
					} else {
						newPrice = gPrice;
					}

					if (Cprice != null && !Cprice.isEmpty()
							&& !Cprice.matches("0")) {
						newCprice = Cprice;
					} else {
						newCprice = gCprice;
					}
					newProductCode = gCode;					 

					double qtyCalc = Double.parseDouble(gQty);
					if (priceflag.matches("0")) {
						productTotal(qtyCalc);
					} else if (priceflag.matches("1")) {
						productTotalNew(gCqty, gLqty);
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onFailure(Exception error) {
			//onError(error);
		}
	}

	public static PropertyInfo newPropertyInfo(String name, String value) {
		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(name);
		propertyInfo.setValue(value);
		return propertyInfo;
	}

	public void productTotal(double qty) {		
		try {

			double taxAmount = 0.0, netTotal = 0.0;
			double taxAmount1 = 0.0, netTotal1 = 0.0;

			if (newPrice.matches("")) {
				newPrice = "0";
			}

			if (!newPrice.matches("")) {

				double slPriceCalc = Double.parseDouble(newPrice);
				String itmDscnt = itemDiscount;
				if (!itmDscnt.matches("")) {

					tt = (qty * slPriceCalc);

				} else {

					tt = qty * slPriceCalc;

				}

				newTotal = twoDecimalPoint(tt);

				double subTotal = 0.0;

				String itemDisc = itemDiscount;
				if (!itemDisc.matches("")) {
					itmDisc = Double.parseDouble(itemDisc);
					subTotal = tt - itmDisc;
				} else {
					subTotal = tt;
				}

				newSubtotal = twoDecimalPoint(subTotal);
				

				if (!taxType.matches("") && !taxValue.matches("")) {

					double taxValueCalc = Double.parseDouble(taxValue);

					if (taxType.matches("E")) {

						if (!itemDisc.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc) / 100;
							String prodTax = fourDecimalPoint(taxAmount1);
							newTax = prodTax;

							netTotal1 = subTotal + taxAmount1;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							newNettotal = ProdNetTotal;
						} else {

							taxAmount = (tt * taxValueCalc) / 100;
							String prodTax = fourDecimalPoint(taxAmount);
							newTax = prodTax;

							netTotal = tt + taxAmount;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							newNettotal = ProdNetTotal;
						}

					} else if (taxType.matches("I")) {
						if (!itemDisc.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount1);
							newTax = prodTax;

							// netTotal1 = subTotal + taxAmount;
							netTotal1 = subTotal;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							newNettotal = ProdNetTotal;
						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount);
							newTax = prodTax;

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							newNettotal = ProdNetTotal;
						}

					} else if (taxType.matches("Z")) {
						newTax = "0.0";
						if (!itemDisc.matches("")) {
							// netTotal1 = subTotal + taxAmount;
							netTotal1 = subTotal;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							newNettotal = ProdNetTotal;
						} else {
							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							newNettotal = ProdNetTotal;
						}

					} else {
						newTax = "0.0";
						newNettotal = newTotal;
					}

				} else if (taxValue.matches("")) {
					newTax = "0.0";
					newNettotal = newTotal;
				}
			}

			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("productcode", newProductCode);
			hm.put("carton_price", newCprice);
			hm.put("price", newPrice);
			hm.put("total", newTotal);
			hm.put("subtotal", newSubtotal);
			hm.put("tax", newTax);
			hm.put("nettotal", newNettotal);
			

			dbcatalog.updateNewPrice(hm);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		onResume();
		newProductCode = "";
		newPrice = "";
		newCprice = "";
		itemDiscount = "";
		newTotal = "";
		newSubtotal = "";
		newTax = "";
		newNettotal = "";
	}

	public void productTotalNew(String cqty, String lqty) {
		Log.d("productTotalNew", "productTotalNew");
		try {
			double taxAmount = 0.0, netTotal = 0.0;
			double taxAmount1 = 0.0, netTotal1 = 0.0;

			String lPrice = newPrice;
			String cPrice = newCprice;

			if (lPrice.matches("")) {
				lPrice = "0";
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

			double lPriceCalc = Double.parseDouble(lPrice);
			double cPriceCalc = Double.parseDouble(cPrice);

			double cqtyCalc = Double.parseDouble(cqty);
			double lqtyCalc = Double.parseDouble(lqty);

			tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

			newTotal = twoDecimalPoint(tt);

			double subTotal = 0.0;

			String itemDisc = itemDiscount;
			if (!itemDisc.matches("")) {
				itmDisc = Double.parseDouble(itemDisc);
				subTotal = tt - itmDisc;
			} else {
				subTotal = tt;
			}

			newSubtotal = twoDecimalPoint(subTotal);

			if (!taxType.matches("") && !taxValue.matches("")) {

				double taxValueCalc = Double.parseDouble(taxValue);

				if (taxType.matches("E")) {

					if (!itemDisc.matches("")) {
						taxAmount1 = (subTotal * taxValueCalc) / 100;
						String prodTax = fourDecimalPoint(taxAmount1);
						newTax = prodTax;

						netTotal1 = subTotal + taxAmount1;
						String ProdNetTotal = twoDecimalPoint(netTotal1);
						newNettotal = ProdNetTotal;
					} else {

						taxAmount = (tt * taxValueCalc) / 100;
						String prodTax = fourDecimalPoint(taxAmount);
						newTax = prodTax;

						netTotal = tt + taxAmount;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						newNettotal = ProdNetTotal;
					}

				} else if (taxType.matches("I")) {
					if (!itemDisc.matches("")) {
						taxAmount1 = (subTotal * taxValueCalc)
								/ (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount1);
						newTax = prodTax;

//						netTotal1 = subTotal + taxAmount1;
						netTotal1 = subTotal;
						String ProdNetTotal = twoDecimalPoint(netTotal1);
						newNettotal = ProdNetTotal;
					} else {
						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount);
						newTax = prodTax;

//						netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						newNettotal = ProdNetTotal;
					}

				} else if (taxType.matches("Z")) {

					newTax = "0.0";
					if (!itemDisc.matches("")) {
//						netTotal1 = subTotal + taxAmount;
						netTotal1 = subTotal;
						String ProdNetTotal = twoDecimalPoint(netTotal1);
						newNettotal = ProdNetTotal;
					} else {
//						netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						newNettotal = ProdNetTotal;
					}

				} else {
					newTax = "0.0";
					newNettotal = newTotal;
				}

			} else if (taxValue.matches("")) {
				newTax = "0.0";
				newNettotal = newTotal;
			} else {
				newTax = "0.0";
				newNettotal = newTotal;
			}

			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("productcode", newProductCode);
			hm.put("carton_price", newCprice);
			hm.put("price", newPrice);
			hm.put("total", newTotal);
			hm.put("subtotal", newSubtotal);
			hm.put("tax", newTax);
			hm.put("nettotal", newNettotal);

			Log.d("newProductCode", newProductCode);
			Log.d("newCprice", newCprice);
			Log.d("newPrice", newPrice);
			Log.d("newTotal", newTotal);
			Log.d("priceflag", priceflag);
			Log.d("newSubtotal", newSubtotal);
			Log.d("newTax", newTax);
			Log.d("nettotal", newNettotal);

			dbcatalog.updateNewPrice(hm);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		onResume();
		newProductCode = "";
		newPrice = "";
		newCprice = "";
		itemDiscount = "";
		newTotal = "";
		newSubtotal = "";
		newTax = "";
		newNettotal = "";
	}

	private class SaveCartOrder extends AsyncTask<Void, Void, Void> {		
		@Override
		protected void onPreExecute() {
			mDialogStatus="";
			 mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				
				if (CarouselFragment.onlineMode.matches("True")) {
					if (CarouselFragment.checkOffline == true) {
						if (mDialogStatus.matches("true")) { // Temp Offline
							mSaveOrder = OfflineSalesOrderWebService.summaryPlaceOrderOffline("fncSaveSO", "1");
						} else {
							Log.d("CheckOffline Alert -->", "False");
							if(mobileHaveOfflineMode.matches("1")) {
								getActivity().finish();
							}
						}
					} else { // Online

						String cattype = FWMSSettingsDatabase.getCatalogTypeStr();
						Log.d("catalogtype", cattype);
						if(cattype.matches("Invoice")){

							mSaveOrder = SOTSummaryWebService.placeOrderInvoice("fncSaveInvoice");
						}else{
							mSaveOrder = SOTSummaryWebService.placeOrder("fncSaveSO");
						}

						if (!mSaveOrder.matches("failed")) {
							//Simultanously save in offline
							OfflineSalesOrderWebService.summaryPlaceOrderOffline("fncSaveSO", "0");
						}
					}
				} else if (CarouselFragment.onlineMode.matches("False")) { // Permnt Offline
					mSaveOrder = OfflineSalesOrderWebService.summaryPlaceOrderOffline("fncSaveSO", "1");
				}
				
			} catch (JSONException e) {				
				e.printStackTrace();
			} catch (IOException e) {				
				e.printStackTrace();
			} catch (XmlPullParserException e) {				
				e.printStackTrace();
			}
			return null;
			}
		@Override
		protected void onPostExecute(Void result) {
			if (mSaveOrder.matches("failed")) {
				Toast.makeText(getActivity(), "Failed",Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(),"Saved Successfully",Toast.LENGTH_SHORT).show();
				DBCatalog.deleteAllProduct();
				SOTDatabase.deleteAttribute();
				dbcatalog.truncateTables();				
				Catalog.setCustomerCode("");
				Catalog.setCustomerName("");
				onResume();	
				mPager.setCurrentItem(1);
			}
		}
	}
	public class CartAdapter extends BaseAdapter implements Constants {

		Cursor cursor;
		Activity context;
		int resource;
		DBCatalog dbcatalog;

		AlertDialog.Builder builder;
		String slPrice = "", slUomCode = "", slCartonPerQty = "", taxType = "",
				taxValue = "", ss_Cqty, beforeLooseQty, priceflag = "",
				calCarton = "";
		EditText sl_codefield, sl_cartonQty, sl_namefield, sl_looseQty, sl_qty,
				sl_foc, sl_price, sl_itemDiscount, sl_uom, sl_total, sl_tax,
				sl_netTotal, sl_cartonPerQty, sl_cprice, sl_exchange;
		LinearLayout uomcperqty_ll, foc_layout;
		ImageView expand;
		TextView price_txt;
		double tt, itmDisc = 0, netTtal = 0, taxAmount = 0;
		TextWatcher cqtyTW, lqtyTW, qtyTW;

		public CartAdapter(Activity context, int resource, Cursor cursor) {
			super();
			this.cursor = cursor;
			this.context = context;
			this.resource = resource;
			dbcatalog = new DBCatalog(getActivity());
			SOTDatabase.init(getActivity());
		}

		@Override
		public int getCount() {
			return cursor.getCount();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			final ViewHolder holder;
			Log.i("position", "" + position);
			cursor.moveToPosition(position);
			if (row == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = context.getLayoutInflater();
				row = inflater.inflate(resource, parent, false);
				holder.productCode = (TextView) row
						.findViewById(R.id.productCode);
				holder.productName = (TextView) row
						.findViewById(R.id.productName);
				holder.ProductPrice = (TextView) row
						.findViewById(R.id.productPrice);
				holder.producttotal = (TextView) row.findViewById(R.id.Total);
				holder.quantity = (TextView) row.findViewById(R.id.Quantity);
				holder.productImage = (SquareImageView) row
						.findViewById(R.id.productImage);
				holder.deleteImage = (ImageView) row
						.findViewById(R.id.delete_img);
				holder.edit_Image = (ImageView) row.findViewById(R.id.edit_img);

				holder.cqty = (TextView) row.findViewById(R.id.cQty);
				holder.cprice = (TextView) row.findViewById(R.id.CQtyPrice);

				holder.label_cqty_ll = (LinearLayout) row
						.findViewById(R.id.label_cc_ll);
				holder.txt_cqty_ll = (LinearLayout) row
						.findViewById(R.id.txt_cc_ll);
				holder.plussymbol_ll = (LinearLayout) row
						.findViewById(R.id.plussymbol_ll);
				// holder.cQty_txt = (TextView) row.findViewById(R.id.cQty_txt);
				// holder.cQtyPrice_txt = (TextView)
				// row.findViewById(R.id.cQtyPrice_txt);
				holder.lQty_txt = (TextView) row.findViewById(R.id.lQty_txt);
				holder.lQtyPrice_tx = (TextView) row
						.findViewById(R.id.lQtyPrice_txt);
				// holder.txtviewsym4 = (TextView)
				// row.findViewById(R.id.textViewsymbol4);
				holder.txtviewsym5 = (TextView) row
						.findViewById(R.id.textViewsymbol5);

				holder.label_lqty_ll = (LinearLayout) row
						.findViewById(R.id.label_lqty_ll);
				holder.Total_txt_ll = (LinearLayout) row
						.findViewById(R.id.Total_txt_ll);

			} else {
				holder = (ViewHolder) row.getTag();
			}

			row.setTag(holder);

			holder.quantity.setTag(position);
			holder.edit_Image.setTag(position);
			holder.deleteImage.setTag(position);

			try {

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

				holder.productCode.setText(cursor.getString(cursor
						.getColumnIndex(Constants.COLUMN_PRODUCT_CODE)));
				holder.productName.setText(cursor.getString(cursor
						.getColumnIndex(Constants.COLUMN_PRODUCT_NAME)));
				/*
				 * holder.ProductPrice.setText(cursor.getString(cursor
				 * .getColumnIndex(DBCatalog.COLUMN_PRICE)));
				 */
				priceflag = SalesOrderSetGet.getCartonpriceflag();
				calCarton = LogOutSetGet.getCalcCarton();

				if (priceflag.matches("null") || priceflag.matches("")) {
					priceflag = "0";
				}

				if (priceflag.matches("1")) {
					// sl_cprice.setVisibility(View.VISIBLE);
					// price_txt.setVisibility(View.VISIBLE);

					holder.cqty.setText(cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_CARTON_QTY)));


					String cprice = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_CARTONPRICE));
					double dCPrice = 0.00;
					if(cprice!=null && !cprice.isEmpty()){
						dCPrice = Double.valueOf(cprice);
					}else{
						dCPrice = 0.00;
					}

					holder.cprice.setText(twoDecimalPoint(dCPrice));

					holder.ProductPrice.setText(cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_LOOSE_QTY)));

					holder.quantity.setText(cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRICE)));

					holder.lQty_txt.setText("Loose");
					holder.lQtyPrice_tx.setText("L.Price");

					holder.label_cqty_ll.setVisibility(View.VISIBLE);
					holder.txt_cqty_ll.setVisibility(View.VISIBLE);

					// holder.txtviewsym4.setVisibility(View.VISIBLE);
					holder.plussymbol_ll.setVisibility(View.VISIBLE);
					// holder.txtviewsym5.setVisibility(View.VISIBLE);

				} else {
					String price = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRICE));
					double dPrice = 0.00;
					if(price!=null && !price.isEmpty()){
						dPrice = Double.valueOf(price);
					}else{
						dPrice = 0.00;
					}
					holder.ProductPrice.setText(twoDecimalPoint(dPrice));

					holder.quantity.setText(cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_QUANTITY)));

					holder.lQty_txt.setText("Price");
					holder.lQtyPrice_tx.setText("Qty");

					holder.cqty.setVisibility(View.GONE);
					holder.cprice.setVisibility(View.GONE);

					holder.label_cqty_ll.setVisibility(View.GONE);
					holder.txt_cqty_ll.setVisibility(View.GONE);
					// holder.sl_cprice.setVisibility(View.GONE);
					// holder.price_txt.setVisibility(View.GONE);

					holder.label_lqty_ll.setGravity(Gravity.CENTER);
					holder.Total_txt_ll.setGravity(Gravity.TOP);
					// holder.txtviewsym4.setVisibility(View.GONE);
					// holder.txtviewsym5.setVisibility(View.INVISIBLE);
					holder.plussymbol_ll.setVisibility(View.GONE);
				}
				holder.producttotal.setText(twoDecimalPoint(Double
						.valueOf(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_SUB_TOTAL)))));

				/*
				 * holder.quantity.setText(cursor.getString(cursor
				 * .getColumnIndex(DBCatalog.COLUMN_QUANTITY)));
				 */
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inSampleSize = 2;
				byte[] encodeByte = Base64.decode(cursor.getString(cursor
						.getColumnIndex(Constants.COLUMN_PRODUCT_IMAGE)),
						Base64.DEFAULT);
				Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
						encodeByte.length, o);
				if (bitmap != null) {
					holder.productImage.setImageBitmap(bitmap);
				} else {
					holder.productImage.setImageResource(R.mipmap.no_image);
				}
				holder.quantity.setId(position);

			} catch (Exception e) {
				e.printStackTrace();
			}
			/** Quantity EditText onClick **/

			holder.quantity
					.setOnEditorActionListener(new OnEditorActionListener() {

						@SuppressWarnings("deprecation")
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							// TODO Auto-generated method stub
							if (actionId == EditorInfo.IME_ACTION_DONE) {

								Log.i("Done Clicked", "********");
								double productQty;
								double productTotal = 0.0;
								int pos = holder.quantity.getId();
								Log.i("pos", "" + pos);
								cursor.moveToPosition(pos);
								String productCode = cursor.getString(cursor
										.getColumnIndex(Constants.COLUMN_PRODUCT_CODE));
								Log.i("productCode", "" + productCode);
								String qty = holder.quantity.getText()
										.toString();
								String price = holder.ProductPrice.getText()
										.toString();

								if (!qty.matches("0") && !qty.matches("")) {
									productQty = Double.parseDouble(qty);
								} else {
									holder.quantity.setText("1");
									productQty = 1;
									Toast.makeText(context, "Invalid Quantity",
											Toast.LENGTH_SHORT).show();
								}

								if (!price.matches("")) {
									productTotal = productQty
											* Double.parseDouble(price);
								}

								dbcatalog.updateTotal(productCode, price,
										productQty, productTotal);

								cursor.requery();

								mCartTotal.setText(twoDecimalPoint(Double
										.valueOf(DBCatalog.getTotal())));
								mCartTax.setText(fourDecimalPoint(Double
										.valueOf(DBCatalog.getTax())));
								mCartNetTotal.setText(twoDecimalPoint(Double
										.valueOf(DBCatalog.getNetTotal())));

								notifyDataSetChanged();

							}
							return false;
						}
					});

			/** DeleteImage ImageView onClick **/

			holder.deleteImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					deleteAlertDialog(v);

				}
			});

			holder.edit_Image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					int pos = (Integer) v.getTag();
					cursor.moveToPosition(pos);
					String id = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRODUCT_ID));
					String slNo = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRODUCT_SLNO));
					String productCode = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRODUCT_CODE));
					String productName = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRODUCT_NAME));
                    String dbProductCode = SOTDatabase.getAttributeProduct(slNo,productCode);
					if(dbProductCode!=null && !dbProductCode.isEmpty()){
						viewAttributeDialog(id,slNo,productCode,productName);
					}else{
						viewCartDialog(v);
					}


				}

			});

			return row;
		}

		class ViewHolder {
			TextView productCode;
			TextView productName;
			TextView ProductPrice;
			TextView producttotal;
			TextView quantity;
			SquareImageView productImage;
			ImageView edit_Image;
			ImageView deleteImage;

			TextView cqty;
			TextView cprice;
			TextView txtviewsym5;
			LinearLayout txt_cqty_ll;
			LinearLayout label_cqty_ll;
			LinearLayout plussymbol_ll;
			TextView lQty_txt;
			TextView lQtyPrice_tx;
			LinearLayout label_lqty_ll;
			LinearLayout Total_txt_ll;
		}
       public void viewAttributeDialog(String id,String slNo,String productCode,String productName){
		   ArrayList<Attribute> colorarrvalues  = SOTDatabase.getAttributeColorValues(productCode,slNo);
		   ColorAttributeDialog productModifierDialog = new ColorAttributeDialog();

		   ArrayList<Attribute> mDistinctColorArr = new ArrayList<>();
		   mDistinctColorArr.add(colorarrvalues.get(0));
		   for (Attribute colour : colorarrvalues) {
			   boolean flag = false;
			   for (Attribute colorUnique : mDistinctColorArr) {
				   if (colorUnique.getCode().equals(colour.getCode())) {
					   flag = true;
				   }
			   }
			   if (!flag)
				   mDistinctColorArr.add(colour);

		   }

		   ArrayList<Attribute> sizearrvalues = SOTDatabase.getAttributeSizeValues(productCode, slNo);
		   Log.d("sizearrvalues", "" + sizearrvalues.size());
		   Log.d("mDistinctColorArr", "" + mDistinctColorArr.size());
		   Log.d("colorarrvalues", "" + colorarrvalues.size());
		   productModifierDialog.setCatalogCartEdit("Catalog");
		   productModifierDialog.setAttributeArr(getActivity(), slNo, productName, productCode, mDistinctColorArr, sizearrvalues,id,0);
		   productModifierDialog.show(getActivity().getFragmentManager(), "dialog");
		   productModifierDialog.setOnProductModifierDialogListener(new ColorAttributeDialog.ProductModifierDialogListener() {
			   @Override
			   public void refreshAdapter() {
				   cursor.requery();
				   setValue();
				   Log.d("refreshAdapter", "completed");
				   notifyDataSetChanged();
			   }
		   });

		  /* productModifierDialog.setAttributeArr(
				   getActivity(),productCode,productName, colorArr);*/
		//  productModifierDialog.show(getActivity().getFragmentManager(), "dialog");

		  /* productModifierDialog.setOnCompletionListener(new ColorAttributeDialog.OnCompletionListener() {
			   @Override
			   public void onCompleted(String qty,ArrayList<Attribute> attributeArr) {
				  // attributeQtyCalc(v,qty,isAddBtn,attributeArr);
			   }
		   });*/
		}
		public void deleteAlertDialog(final View view) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Confirm Delete");
			builder.setIcon(R.mipmap.delete);
			builder.setMessage("Do you want to delete the cart item?");
			builder.setCancelable(false);
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

							Log.i("Delete Button Clicked", "********");
							int pos = (Integer) view.getTag();
							Log.i("pos", "" + pos);
							cursor.moveToPosition(pos);
							String slNo = cursor.getString(cursor
									.getColumnIndex(Constants.COLUMN_PRODUCT_SLNO));
							String productCode = cursor.getString(cursor
									.getColumnIndex(Constants.COLUMN_PRODUCT_CODE));
							Log.i("productCode", "" + productCode);
							dbcatalog.deleteProduct(productCode);
							SOTDatabase.deleteAttributeProducts(slNo,productCode);
							cursor.requery();
							notifyDataSetChanged();
							cartTotal();
							
							 

						}
					});
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}

		private void viewCartDialog(View v) {
			// TODO Auto-generated method stub

			builder = new AlertDialog.Builder(context);
			int pos = (Integer) v.getTag();
			Log.i("pos", "" + pos);
			cursor.moveToPosition(pos);
			//builder.setTitle(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PRODUCT_CODE)));
			LayoutInflater adbInflater = LayoutInflater.from(context);
			
			View cartview = adbInflater.inflate(R.layout.cart_item, null);
		    mDialogCancelImgV   = (ImageView) cartview.findViewById(R.id.close);
			mDialogUpdateImgV= (ImageView) cartview.findViewById(R.id.ok);	    
			foc_layout = (LinearLayout) cartview.findViewById(R.id.foc_layout);
			sl_codefield = (EditText) cartview.findViewById(R.id.sl_codefield);
			sl_namefield = (EditText) cartview.findViewById(R.id.sl_namefield);
			sl_cartonQty = (EditText) cartview.findViewById(R.id.sl_cartonQty);
			sl_looseQty = (EditText) cartview.findViewById(R.id.sl_looseQty);
			sl_qty = (EditText) cartview.findViewById(R.id.sl_qty);
			sl_foc = (EditText) cartview.findViewById(R.id.sl_foc);
			sl_price = (EditText) cartview.findViewById(R.id.sl_price);
			sl_itemDiscount = (EditText) cartview
					.findViewById(R.id.sl_itemDiscount);
			sl_cartonPerQty = (EditText) cartview
					.findViewById(R.id.sl_cartonPerQty);
			sl_uom = (EditText) cartview.findViewById(R.id.sl_uom);
			sl_total = (EditText) cartview.findViewById(R.id.sl_total);
			sl_tax = (EditText) cartview.findViewById(R.id.sl_tax);
			sl_netTotal = (EditText) cartview.findViewById(R.id.sl_netTotal);

			sl_cprice = (EditText) cartview.findViewById(R.id.sl_cprice);
			sl_exchange = (EditText) cartview.findViewById(R.id.sl_exchange);
			price_txt = (TextView) cartview.findViewById(R.id.price_txt);
			expand = (ImageView) cartview.findViewById(R.id.expand);

			uomcperqty_ll = (LinearLayout) cartview
					.findViewById(R.id.uomcperqty_ll);
			final Spinner prompt = (Spinner) cartview
					.findViewById(R.id.weight_status);
			mDialogUpdateImgV.setOnClickListener(mUpdateOnClickListener);
			mDialogCancelImgV.setOnClickListener(mDismissOnClickListener);
			
			builder.setView(cartview);
			uomcperqty_ll.setVisibility(View.VISIBLE);
			
			taxType = cursor.getString(cursor
					.getColumnIndex(Constants.COLUMN_TAXTYPE));
			taxValue = cursor.getString(cursor
					.getColumnIndex(Constants.COLUMN_TAXVALUE));
			slCartonPerQty = cursor.getString(cursor
					.getColumnIndex(Constants.COLUMN_PCSPERCARTON));
			slPrice = cursor.getString(cursor
					.getColumnIndex(Constants.COLUMN_PRICE));

			priceflag = SalesOrderSetGet.getCartonpriceflag();

			if (priceflag.matches("null") || priceflag.matches("")) {
				priceflag = "0";
			}

			if (priceflag.matches("1")) {
				sl_cprice.setVisibility(View.VISIBLE);
				price_txt.setVisibility(View.GONE);
			} else {
				sl_cprice.setVisibility(View.GONE);
				price_txt.setVisibility(View.VISIBLE);
			}

			expand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (foc_layout.getVisibility() == View.VISIBLE) {
						// Its visible
						foc_layout.setVisibility(View.GONE);

					} else {
						foc_layout.setVisibility(View.VISIBLE);
						// Either gone or invisible
					}

				}
			});

			sl_cartonQty
					.setOnEditorActionListener(new OnEditorActionListener() {

						// public boolean onEditorAction(TextView v, int
						// actionId,
						// KeyEvent event) {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_NEXT) {
								// TODO Auto-generated method stub
								slPrice = sl_price.getText().toString();

								if (calCarton.matches("0")) {
									if (priceflag.matches("0")) {

									} else if (priceflag.matches("1")) {
										cartonQtyNew();
									}
								} else {
									if (priceflag.matches("0")) {
										cartonQty();
									} else if (priceflag.matches("1")) {
										cartonQtyNew();
									}
								}

								sl_looseQty.requestFocus();
								return true;
							}
							return false;
						}
					});

			cqtyTW = new TextWatcher() {

				@Override
				public void afterTextChanged(Editable s) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					ss_Cqty = sl_cartonQty.getText().toString();
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					slPrice = sl_price.getText().toString();

					if (calCarton.matches("0")) {
						if (priceflag.matches("0")) {

						} else if (priceflag.matches("1")) {
							cartonQtyNew();
						}
					} else {
						if (priceflag.matches("0")) {
							cartonQty();
						} else if (priceflag.matches("1")) {
							cartonQtyNew();
						}
					}

					int length = sl_cartonQty.length();
					if (length == 0) {

						if (calCarton.matches("0")) {

						} else {

							String lqty = sl_looseQty.getText().toString();
							if (!lqty.matches("")) {
								sl_qty.removeTextChangedListener(qtyTW);
								sl_qty.setText(lqty);
								sl_qty.addTextChangedListener(qtyTW);

								if (sl_qty.length() != 0) {
									sl_qty.setSelection(sl_qty.length());
								}
								double lsQty = Double.parseDouble(lqty);

								if (priceflag.matches("0")) {
									productTotal(lsQty);
								} else if (priceflag.matches("1")) {
									// String cprice =
									// sl_price.getText().toString();
									productTotalNew();
								}
							}
						}
					}
				}
			};
			sl_looseQty.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_NEXT) {
						slPrice = sl_price.getText().toString();

						if (calCarton.matches("0")) {
							if (priceflag.matches("0")) {

							} else if (priceflag.matches("1")) {
								looseQtyCalcNew();
							}
						} else {
							if (priceflag.matches("0")) {
								looseQtyCalc();
							} else if (priceflag.matches("1")) {
								looseQtyCalcNew();
							}
						}

						sl_qty.requestFocus();
						return true;
					}
					return false;
				}

			});

			lqtyTW = new TextWatcher() {

				@Override
				public void afterTextChanged(Editable s) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					beforeLooseQty = sl_looseQty.getText().toString();
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					slPrice = sl_price.getText().toString();

					if (calCarton.matches("0")) {
						if (priceflag.matches("0")) {

						} else if (priceflag.matches("1")) {
							looseQtyCalcNew();
						}
					} else {
						if (priceflag.matches("0")) {
							looseQtyCalc();
						} else if (priceflag.matches("1")) {
							looseQtyCalcNew();
						}
					}

					int length = sl_looseQty.length();
					if (length == 0) {
						if (calCarton.matches("0")) {

						} else {

							String qty = sl_qty.getText().toString();
							if (!beforeLooseQty.matches("") && !qty.matches("")) {

								double qtyCnvrt = Double.parseDouble(qty);
								double lsCnvrt = Double.parseDouble(beforeLooseQty);

								sl_qty.removeTextChangedListener(qtyTW);
								sl_qty.setText("" + (qtyCnvrt - lsCnvrt));
								sl_qty.addTextChangedListener(qtyTW);

								if (sl_qty.length() != 0) {
									sl_qty.setSelection(sl_qty.length());
								}

								if (priceflag.matches("0")) {
									looseQtyCalc();
								} else if (priceflag.matches("1")) {
									looseQtyCalcNew();
								}
							}
						}
					}
				}
			};

			sl_qty.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_NEXT) {

						slPrice = sl_price.getText().toString();
						String qty = sl_qty.getText().toString();

						if (calCarton.matches("0")) {

						} else {
							if (!qty.matches("")) {
								clQty();
							}
						}

						if (priceflag.matches("1")) {
							sl_cprice.requestFocus();
						} else {
							sl_price.requestFocus();
						}
						return true;
					}
					return false;
				}
			});

			qtyTW = new TextWatcher() {

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

					slPrice = sl_price.getText().toString();
					String qty = sl_qty.getText().toString();

					if (qty.matches("")) {
						qty = "0";
					}

					if (calCarton.matches("0")) {

						if (priceflag.matches("0")) {

							if (!qty.matches("")) {
								double quantity = Double.parseDouble(qty);
								productTotal(quantity);

								int length = sl_qty.length();
								if (length == 0) {
									if (calCarton.matches("0")) {
										productTotal(0);
									} else {
										productTotal(0);

										sl_cartonQty
												.removeTextChangedListener(cqtyTW);
										sl_cartonQty.setText("");
										sl_cartonQty
												.addTextChangedListener(cqtyTW);

										sl_looseQty
												.removeTextChangedListener(lqtyTW);
										sl_looseQty.setText("");
										sl_looseQty
												.addTextChangedListener(lqtyTW);
									}
								}
							}
						} else if (priceflag.matches("1")) {
							String pcsPerCarton = sl_cartonPerQty.getText()
									.toString();
							if (!pcsPerCarton.matches("")) {
								double pcsPerCartonCalc = Double
										.parseDouble(pcsPerCarton);
								if (pcsPerCartonCalc == 1) {

									if (!qty.matches("")) {
										double quantity = Double.parseDouble(qty);
										productTotal(quantity);
									}

								}
							}

							int length = sl_qty.length();
							if (length == 0) {
								if (calCarton.matches("0")) {

								} else {
									productTotal(0);

									sl_cartonQty
											.removeTextChangedListener(cqtyTW);
									sl_cartonQty.setText("");
									sl_cartonQty.addTextChangedListener(cqtyTW);

									sl_looseQty
											.removeTextChangedListener(lqtyTW);
									sl_looseQty.setText("");
									sl_looseQty.addTextChangedListener(lqtyTW);
								}
							}

						}

					} else {
						if (!qty.matches("")) {
							clQty();
						}

						int length = sl_qty.length();
						if (length == 0) {
							if (calCarton.matches("0")) {

							} else {
								productTotal(0);

								sl_cartonQty.removeTextChangedListener(cqtyTW);
								sl_cartonQty.setText("");
								sl_cartonQty.addTextChangedListener(cqtyTW);

								sl_looseQty.removeTextChangedListener(lqtyTW);
								sl_looseQty.setText("");
								sl_looseQty.addTextChangedListener(lqtyTW);
							}
						}
					}

				}

			};

			sl_foc.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_NEXT) {

						if (sl_price.getText().toString().equals("0.00")
								|| sl_price.getText().toString().equals("0")
								|| sl_price.getText().toString().equals("0.0")
								|| sl_price.getText().toString().equals(".0")) {
							sl_price.setText("");
						}
						sl_price.requestFocus();
						return true;
					}
					return false;
				}
			});

			sl_foc.addTextChangedListener(new TextWatcher() {

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

				}

			});
			sl_price.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_NEXT) {

						String qty = sl_qty.getText().toString();
						if (!qty.matches("")) {
							double qtyCalc = Double.parseDouble(qty);
							slPrice = sl_price.getText().toString();
							if (priceflag.matches("0")) {
								productTotal(qtyCalc);
							} else if (priceflag.matches("1")) {
								productTotalNew();
							}
						}
						sl_itemDiscount.requestFocus();
						return true;
					}
					return false;
				}
			});
			sl_price.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (sl_price.getText().toString().equals("0.00")
							|| sl_price.getText().toString().equals("0")
							|| sl_price.getText().toString().equals("0.0")
							|| sl_price.getText().toString().equals(".0")) {
						sl_price.setText("");
					}
					return false;
				}
			});
			sl_price.addTextChangedListener(new TextWatcher() {

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

					String qty = sl_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						slPrice = sl_price.getText().toString();
						if (priceflag.matches("0")) {
							productTotal(qtyCalc);
						} else if (priceflag.matches("1")) {
							productTotalNew();
						}
					}
				}

			});
			sl_itemDiscount
					.setOnEditorActionListener(new OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_NEXT
									|| actionId == EditorInfo.IME_ACTION_DONE) {

								InputMethodManager imm = (InputMethodManager) context
										.getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										sl_itemDiscount.getWindowToken(), 0);
								return true;
							}
							return false;
						}
					});

			sl_cprice.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_NEXT) {

						String qty = sl_qty.getText().toString();
						if (!qty.matches("")) {
							double qtyCalc = Double.parseDouble(qty);
							String cPrice = sl_cprice.getText().toString();

							if (priceflag.matches("0")) {
								productTotal(qtyCalc);
							} else if (priceflag.matches("1")) {
								productTotalNew();
							}
						}
						sl_price.requestFocus();
						return true;
					}
					return false;
				}
			});
			sl_cprice.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (sl_cprice.getText().toString().equals("0.00")
							|| sl_cprice.getText().toString().equals("0")
							|| sl_cprice.getText().toString().equals("0.0")
							|| sl_cprice.getText().toString().equals(".0")) {
						sl_cprice.setText("");
					}
					return false;
				}
			});
			sl_cprice.addTextChangedListener(new TextWatcher() {

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

					String qty = sl_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						String cPrice = sl_cprice.getText().toString();

						if (priceflag.matches("0")) {
							productTotal(qtyCalc);
						} else if (priceflag.matches("1")) {
							productTotalNew();
						}
					}
				}

			});

			sl_itemDiscount.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable s) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {

					slPrice = sl_price.getText().toString();

					if (priceflag.matches("0")) {
						itemDiscountCalc();
					} else if (priceflag.matches("1")) {
						itemDiscountCalcNew();
					}

				}
			});

			sl_cartonQty.addTextChangedListener(cqtyTW);
			sl_looseQty.addTextChangedListener(lqtyTW);
			sl_qty.addTextChangedListener(qtyTW);

			prompt.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					String value = prompt.getSelectedItem().toString();
					if (value.matches("Default")) {
						sl_codefield.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_PRODUCT_CODE)));
						sl_namefield.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_PRODUCT_NAME)));
						sl_cartonQty.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_CARTON_QTY)));
						sl_looseQty.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_LOOSE_QTY)));
						sl_qty.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_QUANTITY)));
						sl_price.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_PRICE)));
						sl_foc.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_FOC)));
						sl_itemDiscount.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_ITEM_DISCOUNT)));
						sl_uom.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_UOMCODE)));
						sl_cprice.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_CARTONPRICE)));
						sl_exchange.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_EXCHANGEQTY)));

						if (slCartonPerQty.matches("1")
								|| slCartonPerQty.matches("0")
								|| slCartonPerQty.matches("")) {

							// sl_cartonQty.setEnabled(false);
							sl_cartonQty.setFocusable(false);
							sl_cartonQty
									.setBackgroundResource(R.drawable.ic_edit_disable_bg);

							// sl_looseQty.setEnabled(false);
							sl_looseQty.setFocusable(false);
							sl_looseQty
									.setBackgroundResource(R.drawable.ic_edit_disable_bg);

							sl_qty.requestFocus();

						} else {
							sl_cartonQty.setFocusableInTouchMode(true);
							sl_cartonQty
									.setBackgroundResource(R.drawable.ic_edit_enable_bg);

							sl_looseQty.setFocusableInTouchMode(true);
							sl_looseQty
									.setBackgroundResource(R.drawable.ic_edit_enable_bg);

							sl_cartonQty.requestFocus();
						}
						sl_qty.setFocusableInTouchMode(true);
						sl_qty.setBackgroundResource(R.drawable.ic_edit_enable_bg);

						if (FormSetterGetter.isEditPrice()) {
							sl_price.setEnabled(true);
							sl_price.setFocusableInTouchMode(true);
							sl_price.setBackgroundResource(R.drawable.ic_edit_enable_bg);
						} else {
							sl_price.setEnabled(false);
							sl_price.setFocusable(false);
							sl_price.setGravity(Gravity.LEFT);
							sl_price.setBackgroundResource(R.drawable.ic_edit_disable_bg);
						}
						sl_foc.setFocusableInTouchMode(true);
						sl_foc.setBackgroundResource(R.drawable.ic_edit_enable_bg);
						sl_itemDiscount.setFocusableInTouchMode(true);
						sl_itemDiscount
								.setBackgroundResource(R.drawable.ic_edit_enable_bg);
						sl_uom.setFocusableInTouchMode(false);
						sl_uom.setBackgroundResource(R.drawable.ic_edit_disable_bg);
						sl_cartonPerQty.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_PCSPERCARTON)));

					} else if ((value.matches("Exchange"))
							|| (value.matches("Foc"))) {
						sl_codefield.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_PRODUCT_CODE)));
						sl_namefield.setText(cursor.getString(cursor
								.getColumnIndex(Constants.COLUMN_PRODUCT_NAME)));

						sl_qty.setText("0");
						sl_qty.setGravity(Gravity.CENTER);
						sl_qty.setFocusable(false);
						sl_qty.setBackgroundResource(R.drawable.ic_edit_disable_bg);

						sl_price.setText("0.0");
						sl_price.setGravity(Gravity.CENTER);
						sl_price.setEnabled(false);
						sl_price.setBackgroundResource(R.drawable.ic_edit_disable_bg);

						sl_foc.setText("0");
						sl_foc.setGravity(Gravity.CENTER);
						sl_foc.setFocusable(false);
						sl_foc.setBackgroundResource(R.drawable.ic_edit_disable_bg);

						sl_itemDiscount.setText("0");
						sl_itemDiscount.setGravity(Gravity.CENTER);
						sl_itemDiscount.setFocusable(false);
						sl_itemDiscount
								.setBackgroundResource(R.drawable.ic_edit_disable_bg);

						sl_total.setText("0.0");
						sl_total.setFocusable(false);
						sl_total.setBackgroundResource(R.drawable.ic_edit_disable_bg);

						sl_tax.setText("0.0");
						sl_tax.setFocusable(false);
						sl_tax.setBackgroundResource(R.drawable.ic_edit_disable_bg);

						sl_netTotal.setText("0.0");
						sl_netTotal.setFocusable(false);
						sl_netTotal.setBackgroundResource(R.drawable.ic_edit_disable_bg);

						sl_cartonQty.setText("0");
						sl_cartonQty.setGravity(Gravity.CENTER);
						sl_cartonQty.setFocusable(false);
						sl_cartonQty.setBackgroundResource(R.drawable.ic_edit_disable_bg);

						// sl_looseQty.setEnabled(false);
						sl_looseQty.setText("0");
						sl_looseQty.setGravity(Gravity.CENTER);
						sl_looseQty.setFocusable(false);
						sl_looseQty.setBackgroundResource(R.drawable.ic_edit_disable_bg);

					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});

	/*		builder.setNegativeButton("cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.setPositiveButton("Update",
					new DialogInterface.OnClickListener() {

						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});*/
			alert = builder.create();
			alert.show();

		}

		 private OnClickListener mUpdateOnClickListener  = new OnClickListener(){
			  
			  @Override
			  public void onClick(View arg0) {
					double cartTotal, cartTax, cartNetTotal;
					String codeStr = sl_codefield.getText().toString();
					String cartonQtyStr = sl_cartonQty.getText()
							.toString();
					String looseQtyStr = sl_looseQty.getText()
							.toString();
					String qtyStr = sl_qty.getText().toString();
					String focStr = sl_foc.getText().toString();
					String priceStr = sl_price.getText().toString();
					String dicountStr = sl_itemDiscount.getText()
							.toString();
					String cartonPerQtyStr = sl_cartonPerQty.getText()
							.toString();
					String totalStr = sl_total.getText().toString();
					String taxStr = sl_tax.getText().toString();
					String netTotalStr = sl_netTotal.getText()
							.toString();

					String cpriceStr = sl_cprice.getText().toString();
					String exQtyStr = sl_exchange.getText().toString();

					if (priceStr.matches("")) {
						priceStr = "0";
					}

					if (cpriceStr.matches("")) {
						cpriceStr = "0";
					}

					try {
						if (calCarton.matches("1")
								&& qtyStr.matches("")
								&& focStr.matches("")
								&& exQtyStr.matches("")) {
							Toast.makeText(context,
									"Enter the quantity",
									Toast.LENGTH_SHORT).show();

						} else if (calCarton.matches("0")
								&& cartonQtyStr.matches("")
								&& looseQtyStr.matches("")
								&& qtyStr.matches("")) {
							Toast.makeText(context,
									"Enter the carton/quantity",
									Toast.LENGTH_SHORT).show();
						} else {

							int  foc = 0;
							double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0,
									cartonQty = 0, looseQty = 0, qty = 0;
							String sbTtl = "";
							String netT = "";
							if (!cartonQtyStr.matches("")) {
								cartonQty = Double
										.parseDouble(cartonQtyStr);
							}
							if (!looseQtyStr.matches("")) {
								looseQty = Double
										.parseDouble(looseQtyStr);
							}
							if (!qtyStr.matches("")) {
								qty = Double.parseDouble(qtyStr);
							}
							if (!focStr.matches("")) {
								foc = Integer.parseInt(focStr);
							}
							if (!cartonPerQtyStr.matches("")) {
							}
							if (!priceStr.matches("")) {
								price = Double.parseDouble(priceStr);
							}

							if (cpriceStr.matches("")) {
								cpriceStr = "0.00";
							}

							if (priceflag.matches("0")) {
								cpriceStr = "0.00";
							}

							if (exQtyStr.matches("")) {
								exQtyStr = "0";
							}

							if (!totalStr.matches("")) {
								total = Double.parseDouble(totalStr);

								String itemDisc = sl_itemDiscount
										.getText().toString();
								if (!itemDisc.matches("")) {
									itmDisc = Double
											.parseDouble(itemDisc);
									subTotal = total;
								} else {
									subTotal = total;
								}

								sbTtl = twoDecimalPoint(subTotal);

							}
							if (!taxStr.matches("")) {
								tax = Double.parseDouble(taxStr);
							}
							if (!netTotalStr.matches("")) {
								if (taxType != null
										&& !taxType.isEmpty()) {
									if (taxType.matches("I")
											|| taxType.matches("Z")) {
										ntTot = subTotal;
									} else {
										ntTot = subTotal + tax;
									}
								} else {
									ntTot = subTotal + tax;
								}

								netT = twoDecimalPoint(ntTot);
							}

							if (taxValue.matches("")
									|| taxValue == null) {
								taxValue = "0";
							}

							if (priceflag.matches("0")) {
								itemDiscountCalc();
							} else if (priceflag.matches("1")) {
								itemDiscountCalcNew();
							}

							String disctStr = sl_itemDiscount.getText()
									.toString();
							if (!disctStr.matches("")) {
								discount = Double.parseDouble(disctStr);

							}
							String totl = twoDecimalPoint(tt);
							Log.d("total" + tt, totl);

							double dis = 0.0;
							if (!dicountStr.matches("")) {
								dis = Double.parseDouble(dicountStr);
							}
							Log.d("tt+dis", "->" + tt + dis);
							DBCatalog.updateProduct(codeStr, cartonQty,
									looseQty, price, qty, tt + dis,
									netT, foc, tax, discount, sbTtl,
									cpriceStr, exQtyStr);
							cursor.requery();
							Toast.makeText(context, "Updated",
									Toast.LENGTH_SHORT).show();
							notifyDataSetChanged();

							cartTotal = DBCatalog.getSubTotal();
							cartTax = DBCatalog.getTax();
							cartNetTotal = DBCatalog.getNetTotal();
							mCartTotal.setText(twoDecimalPoint(Double
									.valueOf(cartTotal)));
							mCartTax.setText(fourDecimalPoint(Double
									.valueOf(cartTax)));
							mCartNetTotal
									.setText(twoDecimalPoint(Double
											.valueOf(cartNetTotal)));
							 alert.dismiss();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}  
			 };
			 
			 private OnClickListener mDismissOnClickListener  = new OnClickListener(){
			  
			  @Override
			  public void onClick(View arg0) {			  
			             
				  alert.dismiss();
			  } 
			 };
		public void cartonQty() {
			String crtnQty = sl_cartonQty.getText().toString();

			if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

				double cartonQtyCalc = Double.parseDouble(crtnQty);

				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

				double qty = 0;

				String lsQty = sl_looseQty.getText().toString();

				if (!lsQty.matches("")) {
					double lsQtyCnvrt = Double.parseDouble(lsQty);
					qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

				} else {
					qty = cartonQtyCalc * cartonPerQtyCalc;
				}

				String q = twoDecimalPoint(qty);
				
				sl_qty.removeTextChangedListener(qtyTW);
				sl_qty.setText("" + q);
				sl_qty.addTextChangedListener(qtyTW);

				if (sl_qty.length() != 0) {
					sl_qty.setSelection(sl_qty.length());
				}

				productTotal(qty);
			}
		}

		public void looseQtyCalc() {
			String crtnQty = sl_cartonQty.getText().toString();
			String lsQty = sl_looseQty.getText().toString();

			if (lsQty.matches("")) {
				lsQty = "0";
			}

			if (!slCartonPerQty.matches("") && !crtnQty.matches("")
					&& !lsQty.matches("")) {

				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
				double looseQtyCalc = Double.parseDouble(lsQty);

				double qty;

				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

				String q = twoDecimalPoint(qty);
				
				sl_qty.removeTextChangedListener(qtyTW);
				sl_qty.setText("" + q);
				sl_qty.addTextChangedListener(qtyTW);

				if (sl_qty.length() != 0) {
					sl_qty.setSelection(sl_qty.length());
				}

				productTotal(qty);
			}

			if (!lsQty.matches("")) {

				double looseQtyCalc = Double.parseDouble(lsQty);
				double qty;

				if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
					double cartonQtyCalc = Double.parseDouble(crtnQty);
					double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

					qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
				} else {
					qty = looseQtyCalc;
				}

				sl_qty.removeTextChangedListener(qtyTW);
				sl_qty.setText("" + qty);
				sl_qty.addTextChangedListener(qtyTW);

				if (sl_qty.length() != 0) {
					sl_qty.setSelection(sl_qty.length());
				}

				productTotal(qty);
			}
		}

		public void itemDiscountCalc() {

			try {
				String itmDscnt = sl_itemDiscount.getText().toString();
				String qty = sl_qty.getText().toString();
				String prc = sl_price.getText().toString();

				if (itmDscnt.matches("")) {
					itmDscnt = "0";
				}

				if (!itmDscnt.matches("") && !qty.matches("")
						&& !prc.matches("")) {

					double itemDiscountCalc = 0.0;

					itemDiscountCalc = Double.parseDouble(itmDscnt);

					double quantityCalc = Double.parseDouble(qty);
					double priceCalc = Double.parseDouble(prc);

					tt = (quantityCalc * priceCalc) - itemDiscountCalc;

					Log.d("ttl", "" + tt);
					String Prodtotal = twoDecimalPoint(tt);
					sl_total.setText("" + Prodtotal);

					double taxAmount = 0.0, netTotal = 0.0;
					if (!taxType.matches("") && !taxValue.matches("")) {

						double taxValueCalc = Double.parseDouble(taxValue);

						if (taxType.matches("E")) {

							taxAmount = (tt * taxValueCalc) / 100;
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							netTotal = tt + taxAmount;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);

						} else if (taxType.matches("I")) {

							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);

						} else if (taxType.matches("Z")) {

							sl_tax.setText("0.0");

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);

						} else {
							sl_tax.setText("0.0");
							sl_netTotal.setText("" + Prodtotal);
						}
					} else if (taxValue.matches("")) {
						sl_tax.setText("0.0");
						sl_netTotal.setText("" + Prodtotal);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void productTotal(double qty) {

			try {

				double taxAmount = 0.0, netTotal = 0.0;
				double taxAmount1 = 0.0, netTotal1 = 0.0;

				if (slPrice.matches("")) {
					slPrice = "0";
				}

				if (!slPrice.matches("")) {

					double slPriceCalc = Double.parseDouble(slPrice);
					String itmDscnt = sl_itemDiscount.getText().toString();
					if (!itmDscnt.matches("")) {

						tt = (qty * slPriceCalc);

					} else {

						tt = qty * slPriceCalc;

					}

					String Prodtotal = twoDecimalPoint(tt);

					double subTotal = 0.0;

					String itemDisc = sl_itemDiscount.getText().toString();
					if (!itemDisc.matches("")) {
						itmDisc = Double.parseDouble(itemDisc);
						subTotal = tt - itmDisc;
					} else {
						subTotal = tt;
					}

					String sbTtl = twoDecimalPoint(subTotal);

					sl_total.setText("" + sbTtl);

					if (!taxType.matches("") && !taxValue.matches("")) {

						double taxValueCalc = Double.parseDouble(taxValue);

						if (taxType.matches("E")) {

							if (!itemDisc.matches("")) {
								taxAmount1 = (subTotal * taxValueCalc) / 100;
								String prodTax = fourDecimalPoint(taxAmount1);
								sl_tax.setText("" + prodTax);

								netTotal1 = subTotal + taxAmount1;
								String ProdNetTotal = twoDecimalPoint(netTotal1);
								sl_netTotal.setText("" + ProdNetTotal);
							} else {

								taxAmount = (tt * taxValueCalc) / 100;
								String prodTax = fourDecimalPoint(taxAmount);
								sl_tax.setText("" + prodTax);

								netTotal = tt + taxAmount;
								String ProdNetTotal = twoDecimalPoint(netTotal);
								sl_netTotal.setText("" + ProdNetTotal);
							}

						} else if (taxType.matches("I")) {
							if (!itemDisc.matches("")) {
								taxAmount1 = (subTotal * taxValueCalc)
										/ (100 + taxValueCalc);
								String prodTax = fourDecimalPoint(taxAmount1);
								sl_tax.setText("" + prodTax);

								// netTotal1 = subTotal + taxAmount;
								netTotal1 = subTotal;
								String ProdNetTotal = twoDecimalPoint(netTotal1);
								sl_netTotal.setText("" + ProdNetTotal);
							} else {
								taxAmount = (tt * taxValueCalc)
										/ (100 + taxValueCalc);
								String prodTax = fourDecimalPoint(taxAmount);
								sl_tax.setText("" + prodTax);

								// netTotal = tt + taxAmount;
								netTotal = tt;
								String ProdNetTotal = twoDecimalPoint(netTotal);
								sl_netTotal.setText("" + ProdNetTotal);
							}

						} else if (taxType.matches("Z")) {

							sl_tax.setText("0.0");
							if (!itemDisc.matches("")) {
								// netTotal1 = subTotal + taxAmount;
								netTotal1 = subTotal;
								String ProdNetTotal = twoDecimalPoint(netTotal1);
								sl_netTotal.setText("" + ProdNetTotal);
							} else {
								// netTotal = tt + taxAmount;
								netTotal = tt;
								String ProdNetTotal = twoDecimalPoint(netTotal);
								sl_netTotal.setText("" + ProdNetTotal);
							}

						} else {
							sl_tax.setText("0.0");
							sl_netTotal.setText("" + Prodtotal);
						}

					} else if (taxValue.matches("")) {
						sl_tax.setText("0.0");
						sl_netTotal.setText("" + Prodtotal);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void clQty() {
			String qty = sl_qty.getText().toString();
			String crtnperQty = sl_cartonPerQty.getText().toString();
			double q = 0, r = 0;

			if (crtnperQty.matches("0") || crtnperQty.matches("null")
					|| crtnperQty.matches("0.00")) {
				crtnperQty = "1";
			}

			if (!crtnperQty.matches("")) {
				if (!qty.matches("")) {
					try {
						double qty_nt = Double.parseDouble(qty);
						double pcs_nt = Double.parseDouble(crtnperQty);

						Log.d("qty_nt", "" + qty_nt);
						Log.d("pcs_nt", "" + pcs_nt);

						q = (int) (qty_nt / pcs_nt);
						r = qty_nt % pcs_nt;

						String ctn = twoDecimalPoint(q);
						String loose = twoDecimalPoint(r);
						
						Log.d("cqty", "" + ctn);
						Log.d("lqty", "" + loose);
						
						sl_cartonQty.removeTextChangedListener(cqtyTW);
						sl_cartonQty.setText("" + ctn);
						sl_cartonQty.addTextChangedListener(cqtyTW);

						sl_looseQty.removeTextChangedListener(lqtyTW);
						sl_looseQty.setText("" + loose);
						sl_looseQty.addTextChangedListener(lqtyTW);
						
					
						productTotal(qty_nt);
//						sl_cartonQty.setText("" + ctn);
//						sl_looseQty.setText("" + loose);

					} catch (ArithmeticException e) {
						System.out.println("Err: Divided by Zero");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}

		public void cartonQtyNew() {
			String crtnQty = sl_cartonQty.getText().toString();

			if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
				double qty = 0;

				String lsQty = sl_looseQty.getText().toString();

				if (!lsQty.matches("")) {
					double lsQtyCnvrt = Double.parseDouble(lsQty);
					qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

				} else {
					qty = cartonQtyCalc * cartonPerQtyCalc;
				}

				String q = twoDecimalPoint(qty);
				
				sl_qty.removeTextChangedListener(qtyTW);
				sl_qty.setText("" + q);
				sl_qty.addTextChangedListener(qtyTW);

				if (sl_qty.length() != 0) {
					sl_qty.setSelection(sl_qty.length());
				}

				productTotalNew();
			}
		}

		public void looseQtyCalcNew() {
			String crtnQty = sl_cartonQty.getText().toString();
			String lsQty = sl_looseQty.getText().toString();

			if (lsQty.matches("")) {
				lsQty = "0";
			}

			if (!slCartonPerQty.matches("") && !crtnQty.matches("")
					&& !lsQty.matches("")) {

				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
				double looseQtyCalc = Double.parseDouble(lsQty);

				double qty;
				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

				String q = twoDecimalPoint(qty);
				
				sl_qty.removeTextChangedListener(qtyTW);
				sl_qty.setText("" + q);
				sl_qty.addTextChangedListener(qtyTW);

				if (sl_qty.length() != 0) {
					sl_qty.setSelection(sl_qty.length());
				}

				productTotalNew();
			}

			if (!lsQty.matches("")) {

				double looseQtyCalc = Double.parseDouble(lsQty);
				double qty;

				if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
					double cartonQtyCalc = Double.parseDouble(crtnQty);
					double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

					qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
				} else {
					qty = looseQtyCalc;
				}

				String q = twoDecimalPoint(qty);
				
				sl_qty.removeTextChangedListener(qtyTW);
				sl_qty.setText("" + q);
				sl_qty.addTextChangedListener(qtyTW);

				if (sl_qty.length() != 0) {
					sl_qty.setSelection(sl_qty.length());
				}

				productTotalNew();
			}
		}

		public void itemDiscountCalcNew() {

			try {
				String itmDscnt = sl_itemDiscount.getText().toString();

				if (itmDscnt.matches("")) {
					itmDscnt = "0";
				}

				String lPrice = sl_price.getText().toString();
				String cPrice = sl_cprice.getText().toString();
				String cqty = sl_cartonQty.getText().toString();
				String lqty = sl_looseQty.getText().toString();

				if (lPrice.matches("")) {
					lPrice = "0";
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

				if (!itmDscnt.matches("")) {
					double itemDiscountCalc = 0.0;
					itemDiscountCalc = Double.parseDouble(itmDscnt);

					double lPriceCalc = Double.parseDouble(lPrice);
					double cPriceCalc = Double.parseDouble(cPrice);

					double cqtyCalc = Double.parseDouble(cqty);
					double lqtyCalc = Double.parseDouble(lqty);

					tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc)
							- itemDiscountCalc;

					Log.d("ttl", "" + tt);
					String Prodtotal = twoDecimalPoint(tt);
					sl_total.setText("" + Prodtotal);

					double taxAmount = 0.0, netTotal = 0.0;
					if (!taxType.matches("") && !taxValue.matches("")) {

						double taxValueCalc = Double.parseDouble(taxValue);

						if (taxType.matches("E")) {

							taxAmount = (tt * taxValueCalc) / 100;
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							netTotal = tt + taxAmount;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);

						} else if (taxType.matches("I")) {

							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);

						} else if (taxType.matches("Z")) {

							sl_tax.setText("0.0");

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);

						} else {
							sl_tax.setText("0.0");
							sl_netTotal.setText("" + Prodtotal);
						}
					} else if (taxValue.matches("")) {
						sl_tax.setText("0.0");
						sl_netTotal.setText("" + Prodtotal);
					}
				}

			} catch (Exception e) {

			}
		}

		public void productTotalNew() {

			try {
				double taxAmount = 0.0, netTotal = 0.0;
				double taxAmount1 = 0.0, netTotal1 = 0.0;

				String lPrice = sl_price.getText().toString();
				String cPrice = sl_cprice.getText().toString();
				String cqty = sl_cartonQty.getText().toString();
				String lqty = sl_looseQty.getText().toString();

				if (lPrice.matches("")) {
					lPrice = "0";
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

				double lPriceCalc = Double.parseDouble(lPrice);
				double cPriceCalc = Double.parseDouble(cPrice);

				double cqtyCalc = Double.parseDouble(cqty);
				double lqtyCalc = Double.parseDouble(lqty);

				tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

				String Prodtotal = twoDecimalPoint(tt);

				double subTotal = 0.0;

				String itemDisc = sl_itemDiscount.getText().toString();
				if (!itemDisc.matches("")) {
					itmDisc = Double.parseDouble(itemDisc);
					subTotal = tt - itmDisc;
				} else {
					subTotal = tt;
				}

				String sbTtl = twoDecimalPoint(subTotal);

				sl_total.setText("" + sbTtl);

				if (!taxType.matches("") && !taxValue.matches("")) {

					double taxValueCalc = Double.parseDouble(taxValue);

					if (taxType.matches("E")) {

						if (!itemDisc.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc) / 100;
							String prodTax = fourDecimalPoint(taxAmount1);
							sl_tax.setText("" + prodTax);

							netTotal1 = subTotal + taxAmount1;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							sl_netTotal.setText("" + ProdNetTotal);
						} else {

							taxAmount = (tt * taxValueCalc) / 100;
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							netTotal = tt + taxAmount;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);
						}

					} else if (taxType.matches("I")) {
						if (!itemDisc.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount1);
							sl_tax.setText("" + prodTax);

							// netTotal1 = subTotal + taxAmount1;
							netTotal1 = subTotal;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							sl_netTotal.setText("" + ProdNetTotal);
						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);
						}

					} else if (taxType.matches("Z")) {

						sl_tax.setText("0.0");
						if (!itemDisc.matches("")) {
							// netTotal1 = subTotal + taxAmount;
							netTotal1 = subTotal;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							sl_netTotal.setText("" + ProdNetTotal);
						} else {
							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);
						}

					} else {
						sl_tax.setText("0.0");
						sl_netTotal.setText("" + Prodtotal);
					}

				} else if (taxValue.matches("")) {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}

			} catch (Exception e) {

			}
		}
		public String twoDecimalPoint(double d) {
			DecimalFormat df = new DecimalFormat("#.##");
			df.setMinimumFractionDigits(2);
			String tot = df.format(d);

			return tot;
		}
	}



	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

}
