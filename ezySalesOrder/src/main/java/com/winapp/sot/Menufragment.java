package com.winapp.sot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.NewProductWebService;

public class Menufragment extends SherlockFragment {

	MenuClickInterFace mClick;
	EditText catSearch, subCatSearch;
	Button searchProduct, clearProduct;
	ArrayList<HashMap<String, String>> categoryArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> subCategoryArr = new ArrayList<HashMap<String, String>>();

	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	private AlertDialog myalertDialog = null;
	int textlength = 0;
	ArrayList<HashMap<String, String>> searchResults;
	String keyValues, valid_url = "";
	String categoryStr, subCategoryStr;

	interface MenuClickInterFace {
		void onListitemClick(String cat, String subCat);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mClick = (MenuClickInterFace) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		catSearch = (EditText) getView().findViewById(R.id.CatSearch);
		subCatSearch = (EditText) getView().findViewById(R.id.SubCatSearch);
		searchProduct = (Button) getView().findViewById(R.id.searchProduct);
		clearProduct = (Button) getView().findViewById(R.id.clearProduct);
		
		categoryArr.clear();
		subCategoryArr.clear();

		FWMSSettingsDatabase.init(getActivity());
		valid_url = FWMSSettingsDatabase.getUrl();
		new NewProductWebService(valid_url);

		AsyncCallWSCat cat = new AsyncCallWSCat();
		cat.execute();

		AsyncCallWSSubCat subCat = new AsyncCallWSSubCat();
		subCat.execute();

		searchProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				categoryStr = catSearch.getText().toString();
				subCategoryStr = subCatSearch.getText().toString();
				if (categoryStr.matches("") && subCategoryStr.matches("")) {
					Toast.makeText(getActivity(),
							"Select category or subcategory",
							Toast.LENGTH_SHORT).show();
				} else {
					mClick.onListitemClick(categoryStr, subCategoryStr);
				}

			}
		});
		
		clearProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				catSearch.setText("");
				subCatSearch.setText("");	
			}
		});

		catSearch
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						catSearch) {

					@Override
					public boolean onDrawableClick() {
						// TODO Auto-generated method stub

						AlertDialog.Builder myDialog = new AlertDialog.Builder(
								getActivity());
						final EditText editText = new EditText(getActivity());
						final ListView listview = new ListView(getActivity());
						LinearLayout layout = new LinearLayout(getActivity());
						layout.setOrientation(LinearLayout.VERTICAL);
						myDialog.setTitle("Category");
						editText.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.search, 0, 0, 0);
						layout.addView(editText);
						layout.addView(listview);
						myDialog.setView(layout);
						arrayAdapterProd = new CustomAlertAdapterProd(
								getActivity(), categoryArr);
						listview.setAdapter(arrayAdapterProd);

						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> adapter,
									View v, int position, long arg3) {

								myalertDialog.dismiss();
								getArraylsit = arrayAdapterProd.getArrayList();
								HashMap<String, String> datavalue = getArraylsit
										.get(position);
								Set<Entry<String, String>> keys = datavalue
										.entrySet();
								Iterator<Entry<String, String>> iterator = keys
										.iterator();
								while (iterator.hasNext()) {
									@SuppressWarnings("rawtypes")
									Entry mapEntry = iterator.next();
									keyValues = (String) mapEntry.getKey();
									catSearch.setText(keyValues);

								}
							}
						});

						searchResults = new ArrayList<HashMap<String, String>>(
								categoryArr);
						editText.addTextChangedListener(new TextWatcher() {
							@Override
							public void afterTextChanged(Editable s) {

							}

							@Override
							public void beforeTextChanged(CharSequence s,
									int start, int count, int after) {

							}

							@Override
							public void onTextChanged(CharSequence s,
									int start, int before, int count) {
								textlength = editText.getText().length();
								searchResults.clear();
								for (int i = 0; i < categoryArr.size(); i++) {
									String supplierName = categoryArr.get(i)
											.toString();
									if (textlength <= supplierName.length()) {
										if (supplierName.toLowerCase()
												.contains(
														editText.getText()
																.toString()
																.toLowerCase()
																.trim()))
											searchResults.add(categoryArr
													.get(i));

									}
								}

								arrayAdapterProd = new CustomAlertAdapterProd(
										getActivity(), searchResults);
								listview.setAdapter(arrayAdapterProd);

							}
						});
						myDialog.setNegativeButton("cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

						myalertDialog = myDialog.show();
						return true;

					}
				});

		subCatSearch
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						subCatSearch) {

					@Override
					public boolean onDrawableClick() {

						AlertDialog.Builder myDialog = new AlertDialog.Builder(
								getActivity());
						final EditText editText = new EditText(getActivity());
						final ListView listview = new ListView(getActivity());
						LinearLayout layout = new LinearLayout(getActivity());
						layout.setOrientation(LinearLayout.VERTICAL);
						myDialog.setTitle("Sub Category");
						editText.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.search, 0, 0, 0);
						layout.addView(editText);
						layout.addView(listview);
						myDialog.setView(layout);
						arrayAdapterProd = new CustomAlertAdapterProd(
								getActivity(), subCategoryArr);
						listview.setAdapter(arrayAdapterProd);

						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> adapter,
									View v, int position, long arg3) {

								myalertDialog.dismiss();
								getArraylsit = arrayAdapterProd.getArrayList();

								HashMap<String, String> datavalue = getArraylsit
										.get(position);
								Set<Entry<String, String>> keys = datavalue
										.entrySet();
								Iterator<Entry<String, String>> iterator = keys
										.iterator();
								while (iterator.hasNext()) {
									@SuppressWarnings("rawtypes")
									Entry mapEntry = iterator.next();
									keyValues = (String) mapEntry.getKey();
									subCatSearch.setText(keyValues);

								}
							}
						});

						searchResults = new ArrayList<HashMap<String, String>>(
								subCategoryArr);
						editText.addTextChangedListener(new TextWatcher() {
							@Override
							public void afterTextChanged(Editable s) {

							}

							@Override
							public void beforeTextChanged(CharSequence s,
									int start, int count, int after) {

							}

							@Override
							public void onTextChanged(CharSequence s,
									int start, int before, int count) {
								textlength = editText.getText().length();
								searchResults.clear();
								for (int i = 0; i < subCategoryArr.size(); i++) {
									String supplierName = subCategoryArr.get(i)
											.toString();
									if (textlength <= supplierName.length()) {
										if (supplierName.toLowerCase()
												.contains(
														editText.getText()
																.toString()
																.toLowerCase()
																.trim()))
											searchResults.add(subCategoryArr
													.get(i));

									}
								}

								arrayAdapterProd = new CustomAlertAdapterProd(
										getActivity(), searchResults);
								listview.setAdapter(arrayAdapterProd);
							}
						});
						myDialog.setNegativeButton("cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

						myalertDialog = myDialog.show();
						return true;

					}
				});

	}

	private class AsyncCallWSCat extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menulist, container, false);
		return v;
	}

}
