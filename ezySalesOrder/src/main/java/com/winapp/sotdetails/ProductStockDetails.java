package com.winapp.sotdetails;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sot.WebServiceClass;

public class ProductStockDetails extends Activity {

	LinearLayout productstock_details_layout;
	ImageView back;
	TextView page_Title;
	EditText pro_detail_code, pro_detail_name, pro_detail_catcode,
			pro_detail_subcode, pro_detail_suppliercode, pro_detail_umo,
			pro_detail_carton, pro_detail_retprice, pro_detail_wholeprice,
			pro_detail_taxperc, pro_detail_spec;

	ArrayList<HashMap<String, String>> CustomerListArray = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> customer_hashValue = new HashMap<String, String>();
	String customer_jsonString = null;
	JSONObject customer_jsonResponse;
	JSONArray customer_jsonMainNode;
	String prod_Code = "", LocationCode = "";
	String ProductCode = "", ProductName = "", CategoryCode = "",
			SubCategoryCode = "", SupplierCode = "", UOMCode = "",
			PcsPerCarton = "", RetailPrice = "", WholeSalePrice = "",
			TaxPerc = "", Specification = "";
	String valid_url = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.productstockdetails);

		productstock_details_layout = (LinearLayout) findViewById(R.id.productstock_details_layout);
		page_Title = (TextView) findViewById(R.id.page_Title);

		pro_detail_code = (EditText) findViewById(R.id.pro_detail_code_ed);
		pro_detail_name = (EditText) findViewById(R.id.pro_detail_name_ed);
		pro_detail_catcode = (EditText) findViewById(R.id.pro_detail_catcode_ed);
		pro_detail_subcode = (EditText) findViewById(R.id.pro_detail_subcode_ed);
		pro_detail_suppliercode = (EditText) findViewById(R.id.pro_detail_suppliercode_ed);
		pro_detail_umo = (EditText) findViewById(R.id.pro_detail_umo_ed);
		pro_detail_carton = (EditText) findViewById(R.id.pro_detail_carton_ed);
		pro_detail_retprice = (EditText) findViewById(R.id.pro_detail_retprice_ed);
		pro_detail_wholeprice = (EditText) findViewById(R.id.pro_detail_wholeprice_ed);
		pro_detail_taxperc = (EditText) findViewById(R.id.pro_detail_taxperc_ed);
		pro_detail_spec = (EditText) findViewById(R.id.pro_detail_spec_ed);

		FWMSSettingsDatabase.init(ProductStockDetails.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.e("extras", "Extra NULL");
		} else {
			prod_Code = extras.getString("valProductCode");
			LocationCode = extras.getString("LocationCode");
		}

		CustomersyncCall customerservice = new CustomersyncCall();
		customerservice.execute();

		back = (ImageView) findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ProductStockDetails.this,
						ProductStockActivity.class);
				startActivity(i);
				ProductStockDetails.this.finish();
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

	private class CustomersyncCall extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(ProductStockDetails.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(productstock_details_layout, false);
			progressBar = new ProgressBar(ProductStockDetails.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			// progressBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			customer_hashValue.put("CompanyCode", cmpnyCode);
			customer_hashValue.put("ProductCode", prod_Code);
			customer_hashValue.put("LocationCode", LocationCode);
			customer_jsonString = WebServiceClass.parameterService(
					customer_hashValue, "fncGetProductWithStock");

			try {

				customer_jsonResponse = new JSONObject(customer_jsonString);
				customer_jsonMainNode = customer_jsonResponse
						.optJSONArray("JsonArray");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*********** Process each JSON Node ************/
			int lengthJsonArr = customer_jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode;
				try {

					jsonChildNode = customer_jsonMainNode.getJSONObject(i);

					ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					ProductName = jsonChildNode.optString("ProductName")
							.toString();
					CategoryCode = jsonChildNode.optString("CategoryCode")
							.toString();
					SubCategoryCode = jsonChildNode
							.optString("SubCategoryCode").toString();
					SupplierCode = jsonChildNode.optString("SupplierCode")
							.toString();
					UOMCode = jsonChildNode.optString("UOMCode").toString();
					PcsPerCarton = jsonChildNode.optString("PcsPerCarton")
							.toString();
					RetailPrice = jsonChildNode.optString("RetailPrice")
							.toString();
					WholeSalePrice = jsonChildNode.optString("WholeSalePrice")
							.toString();
					TaxPerc = jsonChildNode.optString("TaxPerc").toString();
					Specification = jsonChildNode.optString("Specification");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/******* Fetch node values **********/
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			page_Title.setText(ProductName);
			pro_detail_code.setText(ProductCode);
			pro_detail_name.setText(ProductName);
			pro_detail_catcode.setText(CategoryCode);
			pro_detail_subcode.setText(SubCategoryCode);
			pro_detail_suppliercode.setText(SupplierCode);
			pro_detail_umo.setText(UOMCode);
			pro_detail_carton.setText(PcsPerCarton);
			pro_detail_retprice.setText(RetailPrice);
			pro_detail_wholeprice.setText(WholeSalePrice);
			pro_detail_taxperc.setText(TaxPerc);
			pro_detail_spec.setText(Specification);

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(productstock_details_layout, true);

		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(ProductStockDetails.this,
				ProductStockActivity.class);
		startActivity(i);
		ProductStockDetails.this.finish();
	}
}
