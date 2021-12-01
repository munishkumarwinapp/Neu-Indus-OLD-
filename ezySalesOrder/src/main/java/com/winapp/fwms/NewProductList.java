package com.winapp.fwms;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.sotdetails.ProductStockActivity;

public class NewProductList extends ListActivity {

	ArrayList<String> barcodeArr = new ArrayList<String>();
	ArrayList<String> getbarcodeArr = new ArrayList<String>();
	String[] list1;
	EditText edbarcode;
	Button addBarcode;
	String adBorcode;
	String proCode;
	Button save;
	boolean barcode;
	String codeFld, descStr, strtsonStr, endsonStr, CtgryfldStr,
			SubCtgryfldStr, uomStr, piecesprcrtnStr, weightFld="0.0",cartonPriceStr,unitStr,binValue;
	boolean check_haveBatch, check_haveExpire, check_haveMFD;
	String valid_url;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout barcode_parent;
	String prodResult;
	boolean upd;
	ArrayList<String> productCodeArr = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newproduct_list);

		barcode_parent = (LinearLayout) findViewById(R.id.barcode_parent);

		edbarcode = (EditText) findViewById(R.id.edbarcode);
		addBarcode = (Button) findViewById(R.id.addBarcode);

		save = (Button) findViewById(R.id.add);

		FWMSSettingsDatabase.init(NewProductList.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new NewProductWebService(valid_url);

		barcodeArr.clear();

		barcodeArr = NewProductWebService.getBarcodeArr();
		if (!barcodeArr.isEmpty()) {
			Log.d("getBarc", barcodeArr.toString());
			list1 = barcodeArr.toArray(new String[barcodeArr.size()]);
			setListAdapter(new ListViewAct(NewProductList.this, list1));
		} else {
			Log.d("getBarc", "empty");
		}

		upd = AddProductSetterGetter.isUpdate();

		if (upd == true) {
			ClearFieldSetterGetter.setClearBarcode(false);
			ClearFieldSetterGetter.setClearProduct(false);
			save.setText("Update");
		} else {
			save.setText("Save");
			barcodeArr.clear();
			list1 = barcodeArr.toArray(new String[barcodeArr.size()]);
			setListAdapter(new ListViewAct(NewProductList.this, list1));
			ClearFieldSetterGetter.setClearBarcode(false);
		}

		addBarcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				adBorcode = edbarcode.getText().toString();
				AsyncCallWSBarcode task = new AsyncCallWSBarcode();
				task.execute();
			}
		});

		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				codeFld = AddProductSetterGetter.getCodeFld();
				descStr = AddProductSetterGetter.getDescStr();
				strtsonStr = AddProductSetterGetter.getStrtsonStr();
				endsonStr = AddProductSetterGetter.getEndsonStr();
				CtgryfldStr = AddProductSetterGetter.getCtgryfldStr();
				SubCtgryfldStr = AddProductSetterGetter.getSubCtgryfldStr();
				uomStr = AddProductSetterGetter.getUomStr();
				piecesprcrtnStr = AddProductSetterGetter.getPiecesprcrtnStr();
				check_haveBatch = AddProductSetterGetter.isCheck_haveBatch();
				check_haveExpire = AddProductSetterGetter.isCheck_haveExpire();
				check_haveMFD = AddProductSetterGetter.isCheck_haveMFD();
				
				cartonPriceStr = AddProductSetterGetter.getCartonPrice();
				unitStr = AddProductSetterGetter.getUnits();
				binValue =AddProductSetterGetter.getBinCode();
				weightFld = AddProductSetterGetter.getWeight();
				
				if(weightFld.matches("") || weightFld==null){
					weightFld="0.0";
				}
				
				proCode = NewProductWebService.getProCode();

				int srtsOn=0,edsOn=0;
				if (!strtsonStr.matches("")) {

					srtsOn = Integer.parseInt(strtsonStr);
					
				}
				if (!endsonStr.matches("")) {
				
					edsOn = Integer.parseInt(endsonStr);
					
				}
				
				if (descStr.matches("")) {
					Toast.makeText(getApplicationContext(),
							"Enter Product Description", Toast.LENGTH_SHORT)
							.show();
				}

//				else if (uomStr.matches("")) {
//
//					Toast.makeText(getApplicationContext(), "Enter UOM Code",
//							Toast.LENGTH_SHORT).show();
//				}

				 else if (srtsOn > edsOn) {
						Toast.makeText(NewProductList.this,
								"Ends On must be greater than starts On",
								Toast.LENGTH_SHORT).show();		
				    }
				
				else {

					if (upd == true) {
						AsyncCallWSProCode task = new AsyncCallWSProCode();
						task.execute();
					} else {

						boolean invalid = AddProductSetterGetter
								.isCheck_invalid();

						if (invalid == true) {
							Toast.makeText(getApplicationContext(),
									"Product code already exist",
									Toast.LENGTH_SHORT).show();
						} else {
							AddProductSetterGetter.setCheck_invalid(false);
							AsyncCallWSProCode task = new AsyncCallWSProCode();
							task.execute();
						}

					}
				}

			}
		});
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

	public class ListViewAct extends ArrayAdapter<String> {

		private final Context context;
		private final String[] values1;

		public ListViewAct(Context context, String[] objects1) {
			super(context, R.layout.newproduct_item, objects1);
			this.context = context;
			this.values1 = objects1;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView = inflater.inflate(R.layout.newproduct_item, parent,
					false);

			TextView textView1 = (TextView) rowView
					.findViewById(R.id.product_code);
			textView1.setText(values1[position]);

			return rowView;
		}
	}

	private class AsyncCallWSBarcode extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			getbarcodeArr.clear();
			spinnerLayout = new LinearLayout(NewProductList.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(barcode_parent, false);
			progressBar = new ProgressBar(NewProductList.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			getbarcodeArr = NewProductWebService
					.getBarcodeService("fncGetProductBarCode");
			
			productCodeArr = NewProductWebService
					.getproductCode("fncGetProductForSearch");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			boolean res = false;
			for (String alphabet : getbarcodeArr) {
				if (alphabet.toLowerCase().equals(adBorcode.toLowerCase())) {
					res = true;
					break;
				}
			}
			
			boolean code = false;
			for (String alphabet : productCodeArr) {
				if (alphabet.toLowerCase()
						.equals(edbarcode.getText().toString().toLowerCase())) {
					
					code = true;
					break;
				}
			}

			if (adBorcode.matches("")) {
				Toast.makeText(getApplicationContext(),
						"Please Enter the barcode", Toast.LENGTH_LONG).show();
			} else if(code == true){
							
					Toast.makeText(getApplicationContext(),
							"Product code already exist",
							Toast.LENGTH_SHORT).show();				
			}	
			else if (res == true) {
				Toast.makeText(getApplicationContext(),
						"Barcode already exist", Toast.LENGTH_SHORT).show();

				edbarcode.setText("");
			} else {
				barcodeArr.add(adBorcode);
				list1 = barcodeArr.toArray(new String[barcodeArr.size()]);
				setListAdapter(new ListViewAct(NewProductList.this, list1));
				edbarcode.setText("");
				NewProductWebService.setBarcodeArr(barcodeArr);
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(barcode_parent, true);

		}
	}

	private class AsyncCallWSProCode extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(NewProductList.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(barcode_parent, false);
			progressBar = new ProgressBar(NewProductList.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			try {

				prodResult = NewProductWebService.addProductService(
						"fncSaveProduct", codeFld, descStr, strtsonStr,
						endsonStr, CtgryfldStr, SubCtgryfldStr, uomStr,
						piecesprcrtnStr, check_haveBatch, check_haveExpire,
						check_haveMFD, weightFld,cartonPriceStr, unitStr,binValue);

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

			if (prodResult.matches("")) {
				Toast.makeText(getApplicationContext(), "Failed",
						Toast.LENGTH_SHORT).show();
			} else {

				save.setText("Save");
				AddProductSetterGetter.setUpdate(false);
				Toast.makeText(getApplicationContext(),
						"Product Saved Successfully", Toast.LENGTH_SHORT)
						.show();
				NewProductWebService.setProCode(null);
				barcodeArr.clear();
				list1 = barcodeArr.toArray(new String[barcodeArr.size()]);
				setListAdapter(new ListViewAct(NewProductList.this, list1));
				ClearFieldSetterGetter.setClearProduct(true);

				AddProductSetterGetter.setCodeFld("");
				AddProductSetterGetter.setDescStr("");
				AddProductSetterGetter.setUomStr("");
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(barcode_parent, true);
			// progressDialog.hide();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		boolean clearBar = ClearFieldSetterGetter.isClearBarcode();
		boolean upd = AddProductSetterGetter.isUpdate();

		if (upd == true) {
			save.setText("Update");
		} else {
			save.setText("Save");

		}
		if (clearBar == true) {
			barcodeArr.clear();
			list1 = barcodeArr.toArray(new String[barcodeArr.size()]);
			setListAdapter(new ListViewAct(NewProductList.this, list1));
			ClearFieldSetterGetter.setClearBarcode(false);
		}

	}

	@Override
	public void onBackPressed() {
		String addProd=LogOutSetGet.getAddProduct();
		
		if(addProd.matches("ProductStock")){
			Intent i = new Intent(NewProductList.this, ProductStockActivity.class);
			startActivity(i);
			NewProductList.this.finish();
		}else if (addProd.matches("StockIn")){
			Intent i = new Intent(NewProductList.this, AddProduct.class);
			startActivity(i);
			NewProductList.this.finish();
		}		
	}
}