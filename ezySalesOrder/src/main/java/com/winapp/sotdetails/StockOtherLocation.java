package com.winapp.sotdetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;

public class StockOtherLocation extends Activity {

	EditText product_name, product_code;
	ListView stockList;
	String prod_Code = "", value = "", valid_url, result,ProdCode="", ProdName="";
	ProgressBar progressBar;
	LinearLayout stock_layout;
	LinearLayout spinnerLayout;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	ArrayList<SOTdetailsGetSet> stockArr = new ArrayList<SOTdetailsGetSet>();
	public CustomerListCustomAdapter Adapter;
	ImageButton back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stock_otherlocation);

		stock_layout = (LinearLayout) findViewById(R.id.stock_layout);
		product_code = (EditText) findViewById(R.id.prod_code);
		product_name = (EditText) findViewById(R.id.prod_name);
		back = (ImageButton) findViewById(R.id.back);
		stockList = (ListView) findViewById(R.id.stock_list);

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.e("extras", "Extra NULL");
		} else {
			prod_Code = extras.getString("ProductCode");
		}

		FWMSSettingsDatabase.init(StockOtherLocation.this);
		valid_url = FWMSSettingsDatabase.getUrl();

		ProductStockAsyncCall task = new ProductStockAsyncCall();
		task.execute();
		
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(StockOtherLocation.this, ProductStockActivity.class);
				startActivity(i);
				StockOtherLocation.this.finish();
			}
		});
	}

	private class ProductStockAsyncCall extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(StockOtherLocation.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stock_layout, false);
			progressBar = new ProgressBar(StockOtherLocation.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			stockArr.clear();

			SoapObject request = new SoapObject(NAMESPACE,
					"fncGetProductWithStock");
			PropertyInfo prodCode = new PropertyInfo();
			PropertyInfo companyCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			prodCode.setName("ProductCode");
			prodCode.setValue(prod_Code);
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			try {
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;
				envelope.bodyOut = request;
				envelope.setOutputSoapObject(request);

				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						valid_url);
				androidHttpTransport.call(SOAP_ACTION
						+ "fncGetProductWithStock", envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

				value = response.toString();
				Log.d("Stock Value", value);
				result = " { Stock : " + value + "}";

				JSONObject jsonResponse;
				try {
					jsonResponse = new JSONObject(result);

					JSONArray jsonMainNode = jsonResponse.optJSONArray("Stock");

					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						String ProductCode = jsonChildNode.optString(
								"ProductCode").toString();
						String ProductName = jsonChildNode.optString(
								"ProductName").toString();

						String LocationCode = jsonChildNode.optString(
								"LocationCode").toString();
						String NoOfCarton = jsonChildNode.optString(
								"PcsPerCarton").toString();
						String Qty = jsonChildNode.optString("Qty").toString();

						int cartonqty = (int) (Double.parseDouble(Qty) / Double
								.parseDouble(NoOfCarton));
						int lqty = (int) (Double.parseDouble(Qty) % Double
								.parseDouble(NoOfCarton));
						String looseqty = twoDecimalPoint(lqty);

						SOTdetailsGetSet stock_Set_Get = new SOTdetailsGetSet();

						ProdCode = ProductCode;
						ProdName = ProductName;
						
						stock_Set_Get.setStock_prod_code(ProductCode);
						stock_Set_Get.setStock_prod_name(ProductName);
						stock_Set_Get.setStock_cqty(""+cartonqty);
						stock_Set_Get.setStock_lqty(""+looseqty);
						stock_Set_Get.setStock_qty(Qty);
						stock_Set_Get.setStock_location(LocationCode);
						
						stockArr.add(stock_Set_Get);						
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

			} catch (Exception e) {

				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			Log.d("Stock Result", stockArr.toString());

			product_code.setText(ProdCode);
			product_name.setText(ProdName);

			Adapter = new CustomerListCustomAdapter(StockOtherLocation.this,
					stockArr);
			stockList.setAdapter(Adapter);

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stock_layout, true);
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

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public class CustomerListCustomAdapter extends BaseAdapter {

		private ArrayList<SOTdetailsGetSet> listarray = new ArrayList<SOTdetailsGetSet>();

		LayoutInflater mInflater;
		CustomHolder holder = new CustomHolder();
		SOTdetailsGetSet user;

		public CustomerListCustomAdapter(Context context,
				ArrayList<SOTdetailsGetSet> productsList) {
			listarray.clear();
			this.listarray = productsList;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listarray.size();
		}

		@Override
		public SOTdetailsGetSet getItem(int position) {
			return listarray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return listarray.get(position).hashCode();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View row = convertView;
			holder = null;

			if (row == null) {
				row = mInflater.inflate(R.layout.stock_listitem, null);
				holder = new CustomHolder();

				holder.location = (TextView) row.findViewById(R.id.location);
				holder.CQty = (TextView) row.findViewById(R.id.CQty);
				holder.LQty = (TextView) row.findViewById(R.id.LQty);
				holder.Qty = (TextView) row.findViewById(R.id.Qty);

				row.setTag(holder);
			} else {
				holder = (CustomHolder) row.getTag();
			}

			user = getItem(position);
			
			Log.d("Stock Qty", user.getStock_qty());
			
			if (user.getStock_qty().matches(".00")
					|| user.getStock_qty().matches("0.00")
					|| user.getStock_qty().matches("0.0")) {
				holder.Qty.setText("0");
			} else {
				
				 StringTokenizer tokens = new StringTokenizer(user.getStock_qty(),".");
		         String qty = tokens.nextToken();

				holder.Qty.setText(qty);
			}
			
			if (user.getStock_lqty().matches(".00")
					|| user.getStock_lqty().matches("0.00")
					|| user.getStock_lqty().matches("0.0")) {
				holder.LQty.setText("0");
			} else {
				holder.LQty.setText(user.getStock_lqty());
			}
	
			holder.CQty.setText(user.getStock_cqty());		
			holder.location.setText(user.getStock_location());

			if (position % 2 == 0) {

				row.setBackgroundResource(drawable.list_item_even_bg);
				holder.location.setTextColor(Color.parseColor("#035994"));
				holder.CQty.setTextColor(Color.parseColor("#035994"));
				holder.LQty.setTextColor(Color.parseColor("#035994"));
				holder.Qty.setTextColor(Color.parseColor("#035994"));

			} else {

				row.setBackgroundResource(drawable.list_item_odd_bg);
				holder.location.setTextColor(Color.parseColor("#646464"));
				holder.CQty.setTextColor(Color.parseColor("#646464"));
				holder.LQty.setTextColor(Color.parseColor("#646464"));
				holder.Qty.setTextColor(Color.parseColor("#646464"));
			}

			return row;
		}

		final class CustomHolder {
			TextView location;
			TextView CQty;
			TextView LQty;
			TextView Qty;
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(StockOtherLocation.this, ProductStockActivity.class);
		startActivity(i);
		StockOtherLocation.this.finish();
	}
}