package com.winapp.fwms;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.sot.GraAddProduct;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.WebServiceClass;
import com.winapp.sotdetails.ProductStockActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class AndroidTabLayoutActivity extends TabActivity implements
		OnTabChangeListener {

	TabHost tabHost;
	TextView tv1;
	TextView tv2;
	TextView pageTitle;
	ImageView back;

	String ProductCode, ProductName, WeightBarcodeStartsOn,
			WeightBarcodeEndsOn, HaveBatch, HaveExpiry, HaveMfgDate,
			CategoryCode, SubCategoryCode, UOMCode, PcsPerCarton, editBrcd,weight,ProductStock,cartonPrice,units,binDetail;
	String bin_Value,binPro_jsonString="";
	JSONObject binPro_jsonResponse;
	JSONArray binPro_jsonMainNode;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newproduct_tabs);

		pageTitle = (TextView) findViewById(R.id.pageTitle);
		back = (ImageView) findViewById(R.id.back);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			ProductCode = extras.getString("ProductCode");
			ProductName = extras.getString("ProductName");
			HaveBatch = extras.getString("HaveBatch");
			HaveExpiry = extras.getString("HaveExpiry");
			HaveMfgDate = extras.getString("HaveMfgDate");
			WeightBarcodeStartsOn = extras.getString("WeightBarcodeStartsOn");
			WeightBarcodeEndsOn = extras.getString("WeightBarcodeEndsOn");
			CategoryCode = extras.getString("CategoryCode");
			SubCategoryCode = extras.getString("SubCategoryCode");
			UOMCode = extras.getString("UOMCode");
			PcsPerCarton = extras.getString("PcsPerCarton");
			editBrcd = extras.getString("Barcode");
			weight = extras.getString("weight");
			cartonPrice = extras.getString("cartonPrice");
			units= extras.getString("units");
			binDetail =extras.getString("bindetail");
			Log.d("binDetailvalue","-->"+binDetail);

		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String addProd=LogOutSetGet.getAddProduct();
				
				if(addProd.matches("ProductStock")){
					Intent i = new Intent(AndroidTabLayoutActivity.this, ProductStockActivity.class);
					startActivity(i);
					AndroidTabLayoutActivity.this.finish();
				} else if (addProd.matches("StockIn")){
					Intent i = new Intent(AndroidTabLayoutActivity.this, AddProduct.class);
					startActivity(i);
					AndroidTabLayoutActivity.this.finish();
				} else if (addProd.matches("GraAddProduct")){
					Intent i = new Intent(AndroidTabLayoutActivity.this, GraAddProduct.class);
					startActivity(i);
					AndroidTabLayoutActivity.this.finish();
				}			
			}
		});

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		TabSpec newProduct = tabHost.newTabSpec("New Product");
		newProduct.setIndicator("New Product");
		Intent newProductIntent = new Intent(this, NewProduct.class);
		pageTitle.setText("New Product");

		if (extras != null) {
			newProductIntent.putExtra("ProductCode", ProductCode);
			newProductIntent.putExtra("ProductName", ProductName);
			newProductIntent.putExtra("WeightBarcodeStartsOn",
					WeightBarcodeStartsOn);
			newProductIntent.putExtra("WeightBarcodeEndsOn",
					WeightBarcodeEndsOn);
			newProductIntent.putExtra("HaveBatch", HaveBatch);
			newProductIntent.putExtra("HaveExpiry", HaveExpiry);
			newProductIntent.putExtra("HaveMfgDate", HaveMfgDate);
			newProductIntent.putExtra("CategoryCode", CategoryCode);
			newProductIntent.putExtra("SubCategoryCode", SubCategoryCode);
			newProductIntent.putExtra("UOMCode", UOMCode);
			newProductIntent.putExtra("PcsPerCarton", PcsPerCarton);
			newProductIntent.putExtra("weight", weight);
			
			newProductIntent.putExtra("cartonPrice", cartonPrice);
			newProductIntent.putExtra("units", units);
			newProductIntent.putExtra("binvalue", binDetail);
						
			newProduct.setContent(newProductIntent);
		} else {
			newProduct.setContent(newProductIntent);
		}
		TabSpec barcode = tabHost.newTabSpec("Barcode");
		barcode.setIndicator("Barcode");
		Intent barcode_Intent = new Intent(this, NewProductList.class);
		
		barcode.setContent(barcode_Intent);
		
		tabHost.addTab(newProduct);
		tabHost.addTab(barcode);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();

		tabHost.getTabWidget()
				.getChildAt(0)
				.setLayoutParams(
						new LinearLayout.LayoutParams((width / 2) - 2, 60));
		tabHost.getTabWidget()
				.getChildAt(1)
				.setLayoutParams(
						new LinearLayout.LayoutParams((width / 2) - 2, 60));

		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i)
					.setBackgroundColor(Color.parseColor("#626776"));
			tv1 = (TextView) tabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			tv1.setTextColor(Color.WHITE);
			tv1.setTypeface(null, Typeface.BOLD);
			tv2.setTextColor(Color.WHITE);

			tv1.setTextSize(13);
			tv2.setTextSize(13);

		}
		tv1 = (TextView) tabHost.getCurrentTabView().findViewById(
				android.R.id.title); 
		tv1.setTextColor(Color.parseColor("#FFFFFF"));

		tabHost.getTabWidget().setCurrentTab(0);

		tabHost.getTabWidget().getChildAt(0)
				.setBackgroundColor(Color.parseColor("#626776"));

	}

	@Override
	public void onTabChanged(String tabId) {
		
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i)
					.setBackgroundColor(Color.parseColor("#626776"));
			tv2 = (TextView) tabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			
			tv2.setTextColor(Color.WHITE);
			tv2.setTypeface(null, Typeface.BOLD);
		}
		tv2 = (TextView) tabHost.getCurrentTabView().findViewById(
				android.R.id.title); // for Selected Tab
		tv2.setTextColor(Color.parseColor("#FFFFFF"));
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
				.setBackgroundColor(Color.parseColor("#626776")); 

		if (tabId.matches("Barcode")) {
			pageTitle.setText("Barcode");

		} else {
			pageTitle.setText("New Product");
		}

	}

	private class AsyncCallWSProductBin extends AsyncTask<Void, Void, Void> {
		String proCode;
		public AsyncCallWSProductBin(String pCode) {
			Log.d("productCodeValue","-->"+pCode);
			this.proCode =pCode;
		}

		@Override
		protected void onPreExecute() {
			binPro_jsonString ="";
		}

		@Override
		protected Void doInBackground(Void... voids) {
			binPro_jsonString = WebServiceClass
					.URLServices("fncGetProductBinDetails",proCode);

			try{
				binPro_jsonResponse = new JSONObject(binPro_jsonString);
				binPro_jsonMainNode = binPro_jsonResponse
						.optJSONArray("JsonArray");


				int lengthJsonArr2 = binPro_jsonMainNode.length();
				Log.d("binPro_jsonMainNode","-->"+lengthJsonArr2);
				for (int i = 0; i < lengthJsonArr2; i++) {
					JSONObject jsonChildNode;
					try {

						jsonChildNode = binPro_jsonMainNode
								.getJSONObject(i);

						bin_Value = jsonChildNode.optString("BinCode").toString();
//						String binName =termcode_jsonChildNode.optString("BinName");
						Log.d("BinCodeProduct","-->"+bin_Value);
//						AddProductSetterGetter.setBinCode(bin_Value);

// 	binData.setBinCode(binCode);
//						binData.setBinName("");
//						bin_List.add(binData);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			bin_Value = AddProductSetterGetter.getBinCode();
			Log.d("Customer_bin_value","-->"+bin_Value);



		}
	}
}