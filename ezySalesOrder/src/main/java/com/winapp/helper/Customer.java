package com.winapp.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.FWMSSettingsDatabase;

public class Customer {
	Activity activity;
	CustomAlertAdapterSupp arrayAdapterSupp;
	AlertDialog myalertDialog;
	int textlength = 0;
	ArrayList<HashMap<String, String>> searchResults = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	EditText custcode, custname;

	static String custresult, valid_url, flag;
	String suppTxt = null;

	public Customer(Activity context, EditText custcode, EditText custname,
			ArrayList<HashMap<String, String>> al, String mflag) {
		this.al.clear();
		this.getArraylsit.clear();
		this.searchResults.clear();
		this.activity = context;
		this.custcode = custcode;
		this.custname = custname;
		this.al = al;
		Customer.flag = mflag;
		FWMSSettingsDatabase.init(activity);
		valid_url = FWMSSettingsDatabase.getUrl();
		custalertDialogSearch();
	}

	public void custalertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(activity);
		final EditText editText = new EditText(activity);
		final ListView listview = new ListView(activity);
		LinearLayout layout = new LinearLayout(activity);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		if(flag.matches("Area")){
			myDialog.setTitle("Area");
		}else if(flag.matches("Van")){
			myDialog.setTitle("Van");
		}else if(flag.matches("Customer")){
			myDialog.setTitle("Customer");
		}
		
		editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search, 0, 0, 0);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(activity, al);
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
					String values = (String) mapEntry.getValue();
					
					
					if(flag.matches("Area")){
						custcode.setText("'"+keyValues+"'");
					}else{
						custcode.setText(keyValues);
					}
					
					
					
					custname.setText(values);
					Catalog.setCustomerCode(keyValues);
					Catalog.setCustomerName(values);
					custcode.addTextChangedListener(new TextWatcher() {
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

							textlength = custcode.getText().length();
							custname.setText("");

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

				arrayAdapterSupp = new CustomAlertAdapterSupp(activity,
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
}
