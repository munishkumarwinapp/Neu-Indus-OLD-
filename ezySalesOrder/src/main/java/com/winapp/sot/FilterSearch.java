package com.winapp.sot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.NewProductWebService;

public class FilterSearch {
	TextView cat, subcat;
	EditText catSearch, subCatSearch;
	private AlertDialog myalertDialog = null;
	private Activity mActivity;
	AlertDialog.Builder filterDialog;
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> categoryArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> subCategoryArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	ArrayList<HashMap<String, String>> alertResults = new ArrayList<HashMap<String, String>>();
	String keyValues,valid_url="";
	int textlength = 0;
	String categoryStr, subCategoryStr;
	boolean status = false;
	boolean dialogResult;
	Handler mHandler;
	
	public String alert_Category="";
	public String alert_SubCategory="";
	

	public FilterSearch(Activity activity) {
		mActivity = activity;

		valid_url = FWMSSettingsDatabase.getUrl();
		new NewProductWebService(valid_url);
		
		AsyncCallWSCat cat = new AsyncCallWSCat();
		cat.execute();

		AsyncCallWSSubCat subCat = new AsyncCallWSSubCat();
		subCat.execute();
	}
	
	
	
	public String getAlert_Category() {
		return alert_Category;
	}



	public void setAlert_Category(String alert_Category) {
		this.alert_Category = alert_Category;
	}



	public String getAlert_SubCategory() {
		return alert_SubCategory;
	}



	public void setAlert_SubCategory(String alert_SubCategory) {
		this.alert_SubCategory = alert_SubCategory;
	}



	public boolean getDialogResult() {
		return dialogResult;
	}

	public boolean setDialogResult(boolean dialogResult) {
		return this.dialogResult = dialogResult;
	}

	public boolean filterAlertDialog() {
		alertResults.clear();
		try {
			cat = new TextView(mActivity);
			subcat = new TextView(mActivity);
			catSearch = new EditText(mActivity);
			subCatSearch = new EditText(mActivity);

			filterDialog = new AlertDialog.Builder(mActivity);
			filterDialog.setTitle("Search");

			catSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.search, 0);
			subCatSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.search, 0);

//			cat.setText("Category");
//			cat.setTextSize(19);
//			cat.setPadding(15, 3, 3, 3);
//			subcat.setText("SubCategory");
//			subcat.setTextSize(19);
//			subcat.setPadding(15, 3, 3, 3);
			catSearch.setHint("Category");
			subCatSearch.setHint("SubCategory");

			LinearLayout layout = new LinearLayout(mActivity);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(cat);
			layout.addView(catSearch);
			layout.addView(subcat);
			layout.addView(subCatSearch);

			filterDialog.setView(layout);

			catSearch
					.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
							catSearch) {
						@Override
						public boolean onDrawableClick() {

							getCategory();
							return true;
						}
					});

			subCatSearch
					.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
							subCatSearch) {
						@Override
						public boolean onDrawableClick() {

							getSubCategory();
							return true;
						}
					});
			
			filterDialog.setPositiveButton("Search",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

						}
					});
			filterDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

						}
					});
			
			final AlertDialog dialog = filterDialog.create();
			dialog.show();
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							categoryStr = catSearch.getText().toString();
							subCategoryStr = subCatSearch.getText().toString();

							if (categoryStr.length() == 0) {
								categoryStr = "";
							}

							if (subCategoryStr.length() == 0) {
								subCategoryStr = "";
							}
//							if (categoryStr.matches("")
//									&& subCategoryStr.matches("")) {
//								Toast.makeText(mActivity,
//										"Select category or subcategory",
//										Toast.LENGTH_SHORT).show();
//							}else {
								setAlert_Category(categoryStr);
								setAlert_SubCategory(subCategoryStr);
								status=true;
								endDialog(true);
								dialog.dismiss();	
//							}
						}
					});

			dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							catSearch.setText("");
							subCatSearch.setText("");
							dialog.dismiss();
							endDialog(false);
						}
					});

		
		} catch (Exception e) {

		}
		return status;

	}

	public boolean getCategory() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(mActivity);
		final EditText editText = new EditText(mActivity);
		final ListView listview = new ListView(mActivity);
		LinearLayout layout = new LinearLayout(mActivity);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Category");
		editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);
		arrayAdapterProd = new CustomAlertAdapterProd(mActivity, categoryArr);
		listview.setAdapter(arrayAdapterProd);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {

				myalertDialog.dismiss();
				getArraylsit = arrayAdapterProd.getArrayList();
				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					keyValues = (String) mapEntry.getKey();
					catSearch.setText(keyValues);

				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(categoryArr);
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
				for (int i = 0; i < categoryArr.size(); i++) {
					String supplierName = categoryArr.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(categoryArr.get(i));

					}
				}

				arrayAdapterProd = new CustomAlertAdapterProd(mActivity,
						searchResults);
				listview.setAdapter(arrayAdapterProd);

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
		return true;

	}

	public Boolean getSubCategory() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(mActivity);
		final EditText editText = new EditText(mActivity);
		final ListView listview = new ListView(mActivity);
		LinearLayout layout = new LinearLayout(mActivity);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Sub Category");
		editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);
		arrayAdapterProd = new CustomAlertAdapterProd(mActivity, subCategoryArr);
		listview.setAdapter(arrayAdapterProd);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {

				myalertDialog.dismiss();
				getArraylsit = arrayAdapterProd.getArrayList();

				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					keyValues = (String) mapEntry.getKey();
					subCatSearch.setText(keyValues);

				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(subCategoryArr);
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
				for (int i = 0; i < subCategoryArr.size(); i++) {
					String supplierName = subCategoryArr.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(subCategoryArr.get(i));

					}
				}

				arrayAdapterProd = new CustomAlertAdapterProd(mActivity,
						searchResults);
				listview.setAdapter(arrayAdapterProd);
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
		return true;

	}

	private class AsyncCallWSCat extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			categoryArr.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			categoryArr = NewProductWebService
					.categoryService("fncGetCategory");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!categoryArr.isEmpty()) {
				Log.d("Cate", categoryArr.toString());
			}

		}
	}

	private class AsyncCallWSSubCat extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			subCategoryArr.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			subCategoryArr = NewProductWebService
					.subCategoryService("fncGetSubCategory");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}

	public void endDialog(boolean result) {

		setDialogResult(result);
		Message m = mHandler.obtainMessage();
		mHandler.sendMessage(m);
	}

	@SuppressWarnings("static-access")
	public boolean showDialog() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				// process incoming messages here
				// super.handleMessage(msg);
				throw new RuntimeException();
			}
		};
		// super.show();
		try {
			Looper.getMainLooper();
			Looper.loop();
		} catch (RuntimeException e2) {
		}
		return dialogResult;
	}

}
