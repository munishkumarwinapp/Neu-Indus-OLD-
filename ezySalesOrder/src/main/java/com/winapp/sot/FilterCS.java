package com.winapp.sot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.winapp.adapter.FilterAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.model.Filter;
import com.winapp.offline.OfflineDatabase;

public class FilterCS implements Constants,OnClickListener {
	
	public interface OnFilterCompletionListener {
		public void onFilterCompleted(String category, String subcategory);
	}
	private Activity activity;   
	private TextView mFilterCategory,mFilterSubCategory,mFilterProduct;
	private EditText mFilter;
	private ImageView mClose;
	private Dialog mDialog;
	private HashMap<String, String> mparam = new HashMap<String, String>();
	private String jsonString="",companyCode,validUrl,category = null,subcategory= null,mobileHaveOfflineMode="";
	private JSONObject jsonResponse;
	private JSONArray jsonMainNode;
    private ArrayList<Filter> mArrayListFilterCategory;
    private ArrayList<Filter> mArrayListFilterSubCategory;
	private ArrayList<Filter> mArrayListFilterProduct;
    private FilterAdapter filteradapter;
    private ListView mListView;
    private boolean mFilterFlag = false;
    private ImageView mClearFilter,mFilterApply;
    private Filter filter;
    private OnFilterCompletionListener listener;
    private static OfflineDatabase offlinedb;
    private TableRow mFilterCategoryTableRow, mFilterSubCategoryTableRow,
			filter_product_TableRow;
    
	public FilterCS(Activity activity){
		this.activity = activity;	
		this.mArrayListFilterCategory = new ArrayList<Filter>();
		this.mArrayListFilterSubCategory = new ArrayList<Filter>();
		this.mArrayListFilterProduct = new ArrayList<Filter>();
		companyCode = SupplierSetterGetter.getCompanyCode();
		FWMSSettingsDatabase.init(activity);
		offlinedb = new OfflineDatabase(activity);
		validUrl = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(validUrl);
		
		new GetCategory().execute(mFilterFlag);
		new GetSubCategory().execute(mFilterFlag);
	
	}
	public void OnFilterCompletionListener(OnFilterCompletionListener listener) {
		this.listener = listener;
	}
	public void filterDialog(){
		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		mDialog= new Dialog(activity);
		//dialog.setTitle("Filter");
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.filter_layout);
		
	    mDialog.setCancelable(false);
	    mDialog.show();		
		
		mFilterCategory = (TextView) mDialog.findViewById(R.id.filter_category);
		mFilterSubCategory = (TextView) mDialog.findViewById(R.id.filter_subcategory);
		mFilterProduct = (TextView) mDialog.findViewById(R.id.filter_product);

		mFilter = (EditText) mDialog.findViewById(R.id.filter);
		mClose = (ImageView) mDialog.findViewById(R.id.close);
		mListView = (ListView) mDialog.findViewById(R.id.filter_list);
		
		mFilterApply = (ImageView) mDialog.findViewById(R.id.filter_apply);
		mClearFilter = (ImageView) mDialog.findViewById(R.id.filter_clear);	
		
		mFilterCategoryTableRow = (TableRow) mDialog
			    .findViewById(R.id.filter_category_tbl);

		filter_product_TableRow  = (TableRow) mDialog
				.findViewById(R.id.filter_product_tbl);

			  mFilterSubCategoryTableRow = (TableRow) mDialog
			    .findViewById(R.id.filter_subcategory_tbl);
		
		mClose.setOnClickListener(this);
//		mFilterCategory.setOnClickListener(this);
//		mFilterProduct.setOnClickListener(this);
//		mFilterSubCategory.setOnClickListener(this);
		mClearFilter.setOnClickListener(this);
		mFilterApply.setOnClickListener(this);

		mFilterCategoryTableRow.setOnClickListener(this);
		filter_product_TableRow.setOnClickListener(this);
		mFilterSubCategoryTableRow.setOnClickListener(this);

		mFilter.addTextChangedListener(mTextFilter);
		
		Log.d("FilterCategory","-->"+ mArrayListFilterCategory.size());
		Log.d("FilterSubCategory","-->"+ mArrayListFilterSubCategory.size());
		Log.d("FilterSubCategory","-->"+ mArrayListFilterProduct.size());

		mFilterCategoryTableRow.setBackgroundColor(Color.parseColor("#626776"));
		mFilterCategory.setBackgroundColor(Color.parseColor("#626776"));
		mFilterCategory.setTextColor(Color.WHITE);
		mFilterCategory.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category_selected,0,0, 0);

//		filter_product_TableRow.setBackgroundColor(Color.parseColor("#626776"));
//		mFilterProduct.setBackgroundColor(Color.parseColor("#626776"));
//		mFilterProduct.setTextColor(Color.WHITE);
//		mFilterProduct.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category_selected,0,0, 0);
		
		filteradapter = new FilterAdapter(activity,mArrayListFilterCategory);
		mListView.setAdapter(filteradapter);
		
	
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {

//				CheckBox checkBox = (CheckBox)v.findViewById(R.id.checkbox);
//				Log.d("click", "list click");
//				if (checkBox.isChecked()) {
//					checkBox.setChecked(true);
//				} else {
//					checkBox.setChecked(false);
//				}
//				
//				Filter filters = originalArraylist.get(position);
//				if (holder.checkBox.isChecked()) {	
//					Log.d("selected pos", "isChecked" + selectedPosition);				
//					filters.setSelected(false);
//					selectedPosition = -1;
//				} else {
//					selectAll(false);
//					Log.d("selected pos", "unChecked " + selectedPosition);
//					filters.setSelected(true);				
//					selectedPosition = position;
//				}
				
				filteradapter.setCheckPosition(position);
//				filteradapter.notifyDataSetChanged();
				
				/*CheckBox checkBox = (CheckBox) v;
				Filter filter = originalArraylist.get(v.getId());
				if (checkBox.isChecked()) {
					selectAll(false);
					filter.setSelected(true);
					selectedPosition = v.getId();
				} else {
					int id = v.getId();
					filter.setSelected(false);
					selectedPosition = -1;
				}*/
				

			}
		});
		
	}
	TextWatcher mTextFilter = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {

			if(mFilter.getText().length()==0){
				
				mFilter.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search,0,  0, 0);
			}
			else{
				
				mFilter.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search,0,R.mipmap.ic_clear_btn, 0);
				
				
				mFilter.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(mFilter) {
					@Override
					public boolean onDrawableClick() {
						
						//Log.d("clear", "clear");
						mFilter.setText("");
						return true;

					}

				});

			}
			String text = mFilter.getText().toString().toLowerCase(Locale.getDefault());
			filteradapter.filter(text);
			
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close:
			mFilter.setText("");
			mDialog.dismiss();
			break;
		case R.id.filter_category_tbl:
			filterCategory();	
			break;
			case R.id.filter_product_tbl:
				filterProduct();
				break;
		case R.id.filter_subcategory_tbl:
			filterSubCategory();
			break;
		case R.id.filter_apply:
//			mFilterApply.setBackgroundColor(Color.parseColor("#626776"));
//			mFilterApply.setTextColor(Color.WHITE);	
//			mFilterApply.setGravity(Gravity.CENTER_VERTICAL);
			mFilter.setText("");
			mApplyFilter();
			break;
		case R.id.filter_clear:
//			 mClearFilter.setBackgroundColor(Color.parseColor("#626776"));
//			 mClearFilter.setTextColor(Color.WHITE);
//			 mClearFilter.setGravity(Gravity.CENTER_VERTICAL);
			unCheckedAll(false,mArrayListFilterCategory,mArrayListFilterSubCategory);
			break;
		default:
			break;
		}
	}
	public void filterCategory(){
		mFilterSubCategoryTableRow.setBackgroundColor(Color.WHITE);
		filter_product_TableRow.setBackgroundColor(Color.WHITE);
		mFilterSubCategory.setBackgroundColor(Color.WHITE);
		mFilterSubCategory.setTextColor(Color.parseColor("#626776"));

		mFilterProduct.setBackgroundColor(Color.WHITE);
		mFilterProduct.setTextColor(Color.parseColor("#626776"));

		mFilterCategory.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_subcategory_selected,0,0, 0);
		mFilterProduct.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category,0,0, 0);
		mFilterSubCategory.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category,0,0, 0);
//		mFilterProduct.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_subcategory,0,0, 0);
		mFilterCategoryTableRow.setBackgroundColor(Color.parseColor("#626776"));
//		filter_product_TableRow.setBackgroundColor(Color.parseColor("#626776"));
		mFilterCategory.setBackgroundColor(Color.parseColor("#626776"));		
		mFilterCategory.setTextColor(Color.WHITE);
//		mFilterProduct.setBackgroundColor(Color.parseColor("#626776"));
//		mFilterProduct.setTextColor(Color.WHITE);
		
		mFilterFlag = true;
		mFilter.setText("");
		if(mArrayListFilterCategory.isEmpty()){
			new GetCategory().execute(mFilterFlag);
		}else{
			filteradapter = new FilterAdapter(activity,mArrayListFilterCategory);
			mListView.setAdapter(filteradapter);
		}	
	}
	public void filterSubCategory(){
		mFilterCategoryTableRow.setBackgroundColor(Color.WHITE);
		filter_product_TableRow.setBackgroundColor(Color.WHITE);
		mFilterCategory.setBackgroundColor(Color.WHITE);
		mFilterCategory.setTextColor(Color.parseColor("#626776"));

		mFilterProduct.setBackgroundColor(Color.WHITE);
		mFilterProduct.setTextColor(Color.parseColor("#626776"));

		mFilterSubCategory.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_subcategory_selected,0,0, 0);
		mFilterProduct.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category,0,0, 0);
		mFilterCategory.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category,0,0, 0);
//		mFilterProduct.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category,0,0, 0);
		mFilterSubCategoryTableRow.setBackgroundColor(Color.parseColor("#626776"));
//		filter_product_TableRow.setBackgroundColor(Color.parseColor("#626776"));
		mFilterSubCategory.setBackgroundColor(Color.parseColor("#626776"));		
		mFilterSubCategory.setTextColor(Color.WHITE);
//		mFilterProduct.setBackgroundColor(Color.parseColor("#626776"));
//		mFilterProduct.setTextColor(Color.WHITE);
		
		mFilterFlag = true;
		mFilter.setText("");
		if(mArrayListFilterSubCategory.isEmpty()){
			new GetSubCategory().execute(mFilterFlag);
		}else{
			filteradapter = new FilterAdapter(activity,mArrayListFilterSubCategory);
			mListView.setAdapter(filteradapter);
		}
	}

	public void filterProduct(){

		mFilterCategoryTableRow.setBackgroundColor(Color.WHITE);
		mFilterSubCategoryTableRow.setBackgroundColor(Color.WHITE);
		mFilterSubCategory.setBackgroundColor(Color.WHITE);
		mFilterSubCategory.setTextColor(Color.parseColor("#626776"));
		mFilterCategory.setBackgroundColor(Color.WHITE);
		mFilterCategory.setTextColor(Color.parseColor("#626776"));

		mFilterProduct.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_subcategory_selected,0,0, 0);
		mFilterSubCategory.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category,0,0, 0);
		mFilterCategory.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_category,0,0, 0);
		filter_product_TableRow.setBackgroundColor(Color.parseColor("#626776"));
//		mFilterCategoryTableRow.setBackgroundColor(Color.parseColor("#626776"));
		mFilterProduct.setBackgroundColor(Color.parseColor("#626776"));
		mFilterProduct.setTextColor(Color.WHITE);

		mFilterFlag = true;
		mFilter.setText("");

		mArrayListFilterProduct.clear();
		Filter filter = new Filter();
		filter.setCode("All");
		filter.setDescription("All Product");
		mArrayListFilterProduct.add(filter);

//		if(mArrayListFilterCategory.isEmpty()){
//			new GetCategory().execute(mFilterFlag);
//		}else{
			filteradapter = new FilterAdapter(activity,mArrayListFilterProduct);
			mListView.setAdapter(filteradapter);
//		}
	}

	public void unCheckedAll(boolean select,ArrayList<Filter>alCategory,ArrayList<Filter>alsubCategory){
		 
			for (Filter filter : alCategory) {
				filter.setSelected(select);
			}
			for (Filter filter : alsubCategory) {
				filter.setSelected(select);
			}
			filteradapter.notifyDataSetChanged();
//			mClearFilter.setBackgroundColor(Color.WHITE);
//			mClearFilter.setTextColor(Color.BLACK);
	}
	public void getCheckedAll(ArrayList<Filter>alCategory,ArrayList<Filter>alsubCategory) {
	
		category ="";
		subcategory ="";
		
		for (Filter filter : alCategory) {
			if (filter.isSelected()) {
				
				category = filter.getCode();			
				
			}
			
		}
		for (Filter filter : alsubCategory) {
			if (filter.isSelected()) {
				
				subcategory = filter.getCode();
				
			}
			
		}
		/*Toast.makeText(activity, "category-->"+category+""+"subcategory-->"+subcategory,
				Toast.LENGTH_SHORT).show();*/
		
	}
	public void mApplyFilter(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {									
					getCheckedAll(mArrayListFilterCategory,mArrayListFilterSubCategory);
					mDialog.dismiss();
//					mFilterApply.setBackgroundColor(Color.WHITE);
//					mFilterApply.setTextColor(Color.BLACK);	
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (listener != null) {
								listener.onFilterCompleted(category,subcategory);
							}
						}
					});
				}
				Looper.loop();
				Looper.myLooper().quit();
			}
		}).start();
	}
	/********************** FNC_GETCATEGORY ************************/
	private class GetCategory extends AsyncTask<Object, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			mArrayListFilterCategory.clear();
		}

		@Override
		protected Boolean doInBackground(Object... param) {
			
			boolean result = (boolean) param[0];
			
			boolean interntConnection = isNetworkConnected();
			if (interntConnection == true) {
				mparam.put("CompanyCode", companyCode);
		  		mparam.put("CategoryCode", "");
		  		jsonString = SalesOrderWebService.getSODetail(mparam, FNC_GETCATEGORY);
			}else{
				if(mobileHaveOfflineMode.matches("1")){
					String sResult = offlinedb.getCategory();
					jsonString = " { SODetails : " + sResult + "}";
				}

			}
			

			
			try {
				if(jsonString!=null) {
					Log.d("jsonString ", "" + jsonString);
					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("SODetails");

					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						Filter filter = new Filter();
						JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

						filter.setCode(jsonChildNode.getString("CategoryCode"));
						filter.setDescription(jsonChildNode.getString("Description"));

						mArrayListFilterCategory.add(filter);
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mparam.clear();
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		
		if(result)
		{
		//filteradapter = new FilterAdapter(activity,mArrayListFilterCategory);
		//mListView.setAdapter(filteradapter);
			Log.d("Category true", "true");
			
			filteradapter = new FilterAdapter(activity,mArrayListFilterCategory);
			mListView.setAdapter(filteradapter);

		}
		else{
			Log.d("Category false", "false");
		}
		}
	}
	/********************** FNC_GETSUBCATEGORY ************************/
	private class GetSubCategory extends AsyncTask<Object, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			mArrayListFilterSubCategory.clear();
		}

		@Override
		protected Boolean doInBackground(Object... param) {	
			
			boolean result = (boolean) param[0];
			
			boolean interntConnection = isNetworkConnected();
			if (interntConnection == true) {
				mparam.put("CompanyCode", companyCode);
		  		mparam.put("SubCategoryCode", "");
		  		jsonString = SalesOrderWebService.getSODetail(mparam,
						FNC_GETSUBCATEGORY);
			}else{
				if(mobileHaveOfflineMode.matches("1")) {
					String sResult = offlinedb.getSubCategory();
					jsonString = " { SODetails : " + sResult + "}";
				}
			}
			


			try {
				if(jsonString!=null) {
					Log.d("jsonString ", "" + jsonString);

					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("SODetails");


					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						Filter filter = new Filter();
						JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

						filter.setCode(jsonChildNode.getString("SubCategoryCode"));
						filter.setDescription(jsonChildNode.getString("Description"));

						mArrayListFilterSubCategory.add(filter);
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mparam.clear();
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		
		if(result){
			Log.d("subCategory true", "true");
			//filteradapter = new FilterAdapter(activity,mArrayListFilterSubCategory);
			//mListView.setAdapter(filteradapter);
			filteradapter = new FilterAdapter(activity,mArrayListFilterSubCategory);
			mListView.setAdapter(filteradapter);
		}
		else{
			Log.d("subCategory false", "false");

		}
		}
	}
	
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}
}
