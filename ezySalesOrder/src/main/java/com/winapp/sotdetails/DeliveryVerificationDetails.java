package com.winapp.sotdetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SOTSummaryWebService;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SlideMenuFragment;

public class DeliveryVerificationDetails extends SherlockFragmentActivity
		implements SlideMenuFragment.MenuClickInterFace {

	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, main_parent;
	private TextView customername_txt, customercode_txt, sono_txt;
	private EditText scanbarcode_edt, dialogCarton, dialogWeight;
	private SlidingMenu menu;
	private ImageButton searchIcon, saveIcon, printerIcon;
	private ListView mList;

	private ArrayList<HashMap<String, String>> mDetailsArr = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mHeaderArr = new ArrayList<HashMap<String, String>>();
	// private ArrayList<HashMap<String, String>> editDetailsArr = new
	// ArrayList<HashMap<String, String>>();
	private customAdapter mCustomAdapter;
	private String valid_url = "", mSoNo = "", mLocationCode = "",
			mCustomerCode = "", mCustomerName = "", mIsQtyVerified = "",
			summaryResult = "";
	private double mBillDiscount = 0;
	private Cursor mCursor = null;

	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.delivery_verification_details);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Delivery Verification");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		printerIcon = (ImageButton) customNav.findViewById(R.id.printer);
		saveIcon = (ImageButton) customNav.findViewById(R.id.custcode_img);

		saveIcon.setImageResource(R.mipmap.ic_save_img);
		searchIcon.setVisibility(View.GONE);
		printerIcon.setVisibility(View.GONE);
		saveIcon.setVisibility(View.VISIBLE);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

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

		SOTDatabase.init(DeliveryVerificationDetails.this);
		FWMSSettingsDatabase.init(DeliveryVerificationDetails.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);

		main_parent = (LinearLayout) findViewById(R.id.main_parent);
		customername_txt = (TextView) findViewById(R.id.customername_txt);
		customercode_txt = (TextView) findViewById(R.id.customercode_txt);
		sono_txt = (TextView) findViewById(R.id.sono_txt);
		scanbarcode_edt = (EditText) findViewById(R.id.scanbarcode_edt);
		mList = (ListView) findViewById(R.id.listview);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			mHeaderArr.clear();
			mDetailsArr.clear();
			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();

			mDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SODetails");
			mHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SOHeader");
			mIsQtyVerified = (String) getIntent().getSerializableExtra(
					"isQtyVerified");

			Log.d("mIsQtyVerified", "->" + mIsQtyVerified);
			Log.d("Delivery Verification details", "" + mDetailsArr);
			Log.d("Delivery Verification Header", "" + mHeaderArr);

			if (mHeaderArr != null) {
				for (int i = 0; i < mHeaderArr.size(); i++) {

					String SoDate = "", DeliveryDate = "", Total = "", BillDIscount = "", ItemDiscount = "", CurrencyCode = "", CurrencyRate = "", Remarks = "";

					mSoNo = mHeaderArr.get(i).get("InvoiceNo");
					SoDate = mHeaderArr.get(i).get("InvoiceDate").split("\\ ")[0];
//					DeliveryDate = mHeaderArr.get(i).get("DeliveryDate")
//							.split("\\ ")[0];
					mLocationCode = mHeaderArr.get(i).get("LocationCode");
					mCustomerCode = mHeaderArr.get(i).get("CustomerCode");
					mCustomerName = mHeaderArr.get(i).get("CustomerName");
					Total = mHeaderArr.get(i).get("Total");
					ItemDiscount = mHeaderArr.get(i).get("ItemDiscount");
					BillDIscount = mHeaderArr.get(i).get("BillDIscount");
					Remarks = mHeaderArr.get(i).get("Remarks");
					CurrencyCode = mHeaderArr.get(i).get("CurrencyCode");
					CurrencyRate = mHeaderArr.get(i).get("CurrencyRate");

					customername_txt.setText(mCustomerName);
					customercode_txt.setText(mCustomerCode);
					sono_txt.setText(mSoNo);

					if (!BillDIscount.matches("")) {
						mBillDiscount = Double.parseDouble(BillDIscount);
					}

					SalesOrderSetGet.setCustomername(mCustomerName);
					SalesOrderSetGet.setSaleorderdate(SoDate);
					SalesOrderSetGet.setDeliverydate(DeliveryDate);
					SalesOrderSetGet.setLocationcode(mLocationCode);
					SalesOrderSetGet.setCustomercode(mCustomerCode);
					SalesOrderSetGet.setRemarks(Remarks);
					SalesOrderSetGet.setCurrencycode(CurrencyCode);
					SalesOrderSetGet.setCurrencyrate(CurrencyRate);
					SalesOrderSetGet.setCurrencyname("");

				}
			}

			if (mDetailsArr != null) {
				Log.d("mDetailsArr.size()", "->" + mDetailsArr.size());
				if (mDetailsArr.size() > 0) {
					SOTDatabase.storeDeliveryVerification(mDetailsArr,
							mIsQtyVerified);
				}
			}
		}

		mCursor = SOTDatabase.getCursor();

		if (mCursor != null && mCursor.getCount() > 0) {
			mCustomAdapter = new customAdapter(
					DeliveryVerificationDetails.this, mCursor);
			mList.setAdapter(mCustomAdapter);
			// registerForContextMenu(mList);
		}

		scanbarcode_edt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(
							scanbarcode_edt.getWindowToken(), 0);
					if (!scanbarcode_edt.getText().toString().matches("")) {
						editBarCodeField();
					} else {

					}
					return true;
				}
				return false;
			}
		});

		saveIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				double totalCqty = 0, totalQty = 0;
				totalCqty = SOTDatabase.getTotalQty();
				totalQty = SOTDatabase.getTotalCqty();

				if (totalCqty > 0 || totalQty > 0) {
					AsyncCallWSSummary task = new AsyncCallWSSummary();
					task.execute();

				} else {
					Toast.makeText(getApplicationContext(), " Enter Barcode ",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View targetView,
					int position, long arg3) {
				String id = ((TextView) targetView.findViewById(R.id.id))
						.getText().toString();
				String productcode = ((TextView) targetView
						.findViewById(R.id.productcode)).getText().toString();
				String productname = ((TextView) targetView
						.findViewById(R.id.productname)).getText().toString();
				String order_carton = ((TextView) targetView
						.findViewById(R.id.order_carton)).getText().toString();
				String order_weight = ((TextView) targetView
						.findViewById(R.id.order_weight)).getText().toString();
				String packed_carton = ((TextView) targetView
						.findViewById(R.id.packed_carton)).getText().toString();
				String packed_weight = ((TextView) targetView
						.findViewById(R.id.packed_weight)).getText().toString();

				double dCarton = 0, dWeight = 0;

				if (packed_carton != null && !packed_carton.isEmpty()) {
					dCarton = Double.parseDouble(packed_carton);
				}

				if (packed_weight != null && !packed_weight.isEmpty()) {
					dWeight = Double.parseDouble(packed_weight);
				}

				editAlertDialog(id, productname, dCarton, dWeight);
			}
		});

	}

	// /** Context Menu Start **/
	//
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// menu.add(0, v.getId(), 0, "Edit");
	//
	// }
	//
	// @SuppressWarnings("deprecation")
	// @Override
	// public boolean onContextItemSelected(android.view.MenuItem item) {
	// AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	// .getMenuInfo();
	// if (item.getTitle() == "Edit") {
	//
	// String id = ((TextView) info.targetView.findViewById(R.id.id))
	// .getText().toString();
	// String productcode = ((TextView) info.targetView
	// .findViewById(R.id.productcode)).getText().toString();
	// String productname = ((TextView) info.targetView
	// .findViewById(R.id.productname)).getText().toString();
	// String order_carton = ((TextView) info.targetView
	// .findViewById(R.id.order_carton)).getText().toString();
	// String order_weight = ((TextView) info.targetView
	// .findViewById(R.id.order_weight)).getText().toString();
	// String packed_carton = ((TextView) info.targetView
	// .findViewById(R.id.packed_carton)).getText().toString();
	// String packed_weight = ((TextView) info.targetView
	// .findViewById(R.id.packed_weight)).getText().toString();
	//
	// double dCarton=0, dWeight=0;
	//
	// if(packed_carton != null && !packed_carton.isEmpty()){
	// dCarton = Double.parseDouble(packed_carton);
	// }
	//
	// if(packed_weight != null && !packed_weight.isEmpty()){
	// dWeight = Double.parseDouble(packed_weight);
	// }
	//
	// editAlertDialog(id, productname, dCarton, dWeight);
	//
	// } else {
	// return false;
	// }
	// return true;
	//
	// }
	//
	// /** Context Menu End **/

	/** AsyncTask Start **/

	private class AsyncCallWSSummary extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			double discnt = 0.0;

			try {
				summaryResult = SOTSummaryWebService
						.summaryDeliveryVerificationService(
								"fncUpdateInvoiceWithScannedQty", mBillDiscount,
								mSoNo);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(main_parent, true);

			if (summaryResult.matches("failed")) {

				Toast.makeText(DeliveryVerificationDetails.this, "Failed",
						Toast.LENGTH_SHORT).show();

			} else {

				Toast.makeText(DeliveryVerificationDetails.this,
						"Saved Successfully", Toast.LENGTH_SHORT).show();
				clearView();
			}

		}
	}

	/** AsyncTask End **/

	/** Adapter Start **/

	public class customAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public customAdapter(Context context, Cursor cursor) {
			super(context, R.layout.delivery_verification_detail_listitem,
					cursor);
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {

			TextView productid = (TextView) row.findViewById(R.id.id);
			TextView productcode = (TextView) row
					.findViewById(R.id.productcode);
			TextView productname = (TextView) row
					.findViewById(R.id.productname);
			TextView order_carton = (TextView) row
					.findViewById(R.id.order_carton);
			TextView order_weight = (TextView) row
					.findViewById(R.id.order_weight);
			TextView packed_carton = (TextView) row
					.findViewById(R.id.packed_carton);
			TextView packed_weight = (TextView) row
					.findViewById(R.id.packed_weight);

			String originalQty = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_DV_ORIGINAL_QTY));
			String qty = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

			productid.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID)));
			productcode.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));
			productname.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));
			order_carton.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_DV_ORIGINAL_CQTY)));
			order_weight.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_DV_ORIGINAL_QTY)));
			packed_carton.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));
			packed_weight.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

			double dOriginalQty = 0, dQty = 0;
			if (originalQty != null && !originalQty.isEmpty()) {
				dOriginalQty = Double.parseDouble(originalQty);
			}

			if (qty != null && !qty.isEmpty()) {
				dQty = Double.parseDouble(qty);
			}

			if (dQty > 0) {
				if (dQty > dOriginalQty || dQty < dOriginalQty) {
					row.setBackgroundResource(drawable.list_item_red_bg);
				} else if (dQty == dOriginalQty) {
					row.setBackgroundResource(drawable.list_item_selected_bg);
				} else {
					row.setBackgroundResource(R.color.white);
				}
			} else {
				row.setBackgroundResource(R.color.white);
			}

			// if (position % 2 == 0) {
			//
			// row.setBackgroundResource(R.drawable.list_item_even_bg);
			// productcode.setTextColor(Color.parseColor("#035994"));
			// productname.setTextColor(Color.parseColor("#035994"));
			// order_carton.setTextColor(Color.parseColor("#035994"));
			// order_weight.setTextColor(Color.parseColor("#035994"));
			// packed_carton.setTextColor(Color.parseColor("#035994"));
			// packed_weight.setTextColor(Color.parseColor("#035994"));
			//
			// } else {
			//
			// row.setBackgroundResource(R.drawable.list_item_odd_bg);
			// productcode.setTextColor(Color.parseColor("#646464"));
			// productname.setTextColor(Color.parseColor("#646464"));
			// order_carton.setTextColor(Color.parseColor("#035994"));
			// order_weight.setTextColor(Color.parseColor("#035994"));
			// packed_carton.setTextColor(Color.parseColor("#035994"));
			// packed_weight.setTextColor(Color.parseColor("#035994"));
			// }

		}

		public void resetData() {
			notifyDataSetChanged();
		}

	}

	/** Adapter Start **/

	private void editBarCodeField() {
		if (scanbarcode_edt.getText().toString() != ""
				&& scanbarcode_edt.getText().length() != 0) {

			String mWeight1 = "0", mWeight2 = "00", mWeight = "0";

			try {

				String getbarcode = scanbarcode_edt.getText().toString(); // 2,0000001,290814,01602,7
				getbarcode="0"+getbarcode;
				getbarcode = getbarcode.replace(" ", "");
				// String strpart1 = getbarcode.substring(0, 1); //remove 1st
				// char
				String mProductcode = getbarcode.substring(0, 7); // get
																	// productcode
				// String mDay = getbarcode.substring(8, 10); // get day
				// String mMonth = getbarcode.substring(10, 12); // get month
				// String mYear = getbarcode.substring(12, 14); // get year
				mWeight1 = getbarcode.substring(7, 9); // get before dot
				mWeight2 = getbarcode.substring(9, 12); // get after dot
				String strpart2 = getbarcode.substring(12, 13); // remove last
																// char

				// Log.d("strpart1", "-> "+strpart1);
				Log.d("mProductcode", "-> " + mProductcode);
				// Log.d("mDay", "-> "+mDay);
				// Log.d("mMonth", "-> "+mMonth);
				// Log.d("mYear", "-> "+mYear);
				Log.d("mWeight1", "-> " + mWeight1);
				Log.d("mWeight2", "-> " + mWeight2);
				Log.d("strpart2", "-> " + strpart2);

				// String mDate = mDay+"/"+mMonth+"/"+mYear;
				mWeight = mWeight1 + "." + mWeight2;

				Log.d("mProductcode", "-> " + mProductcode);
				// Log.d("mDate", "-> "+mDate);
				Log.d("mWeight", "-> " + mWeight);

				// mProductcode = "0000001";

				Log.d("getbarcode.length()", "" + getbarcode.length());

				if (getbarcode.length() == 13) {

					if (mProductcode != null && !mProductcode.isEmpty()) {

						Cursor cursor = SOTDatabase
								.getCursorForProductCode(mProductcode);
						if (cursor != null && cursor.getCount() > 0) {

							if (cursor.moveToFirst()) {
								do {
									double dCarton = 0, dWeight = 0.000;
									String pId = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID));
									String pCode = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
									String cQty = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
									String lQty = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
									String qty = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
									String foc = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_FOC));
									String price = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
									String itemDiscount = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
									String taxType = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
									String taxValue = cursor
											.getString(cursor
													.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));

									if (cQty != null && !cQty.isEmpty()) {
										dCarton = Double.parseDouble(cQty);
										dCarton = dCarton + 1;
									} else {
										dCarton = 1.00;
									}

									if (qty != null && !qty.isEmpty()) {
										dWeight = Double.parseDouble(qty)
												+ Double.parseDouble(mWeight);
									} else {
										dWeight = Double.parseDouble(mWeight);
									}
									
									Log.d("dWeight", "-> " + dWeight);

									try {
										/** total Calculation Start **/
										double total = 0.0, itmDisc = 0.0;
										double taxAmount = 0.0, netTotal = 0.0;
										double taxAmount1 = 0.0, netTotal1 = 0.0;
										String prodTax = "0", ProdNetTotal = "0", proSubtotal = "0";

										if (price.matches("")) {
											price = "0";
										}

										double itemDiscountCalc = 0.0;
										if (!itemDiscount.matches("")) {
											itemDiscountCalc = Double
													.parseDouble(itemDiscount);
										}

										if (!price.matches("")) {

											double slPriceCalc = Double
													.parseDouble(price);

											if (!itemDiscount.matches("")) {
												total = (dWeight * slPriceCalc)
														- itemDiscountCalc;
												// tt = (qty * slPriceCalc);
											} else {
												total = dWeight * slPriceCalc;
											}

											String Prodtotal = twoDecimalPoint(total);

											double subTotal = 0.0;

											if (!itemDiscount.matches("")) {
												itmDisc = Double
														.parseDouble(itemDiscount);
												subTotal = total - itmDisc;
											} else {
												subTotal = total;
											}

											proSubtotal = twoDecimalPoint(subTotal);

											if (!taxType.matches("")
													&& !taxValue.matches("")) {

												double taxValueCalc = Double
														.parseDouble(taxValue);

												if (taxType.matches("E")) {

													if (!itemDiscount
															.matches("")) {
														taxAmount1 = (subTotal * taxValueCalc) / 100;
														prodTax = fourDecimalPoint(taxAmount1);
														netTotal1 = subTotal
																+ taxAmount1;
														ProdNetTotal = twoDecimalPoint(netTotal1);
													} else {
														taxAmount = (total * taxValueCalc) / 100;
														prodTax = fourDecimalPoint(taxAmount);
														netTotal = total
																+ taxAmount;
														ProdNetTotal = twoDecimalPoint(netTotal);
													}

												} else if (taxType.matches("I")) {
													if (!itemDiscount
															.matches("")) {
														taxAmount1 = (subTotal * taxValueCalc)
																/ (100 + taxValueCalc);
														prodTax = fourDecimalPoint(taxAmount1);
														netTotal1 = subTotal;
														ProdNetTotal = twoDecimalPoint(netTotal1);
													} else {
														taxAmount = (total * taxValueCalc)
																/ (100 + taxValueCalc);
														prodTax = fourDecimalPoint(taxAmount);
														netTotal = total;
														ProdNetTotal = twoDecimalPoint(netTotal);
													}

												} else if (taxType.matches("Z")) {

													prodTax = "0.0";
													if (!itemDiscount
															.matches("")) {
														// netTotal1 = subTotal
														// + taxAmount;

														netTotal1 = subTotal;
														ProdNetTotal = twoDecimalPoint(netTotal1);
													} else {
														// netTotal = tt +
														// taxAmount;
														netTotal = total;
														ProdNetTotal = Prodtotal;
													}

												} else {
													prodTax = "0.0";
													ProdNetTotal = twoDecimalPoint(netTotal);
												}

											} else if (taxValue.matches("")) {
												prodTax = "0.0";
												ProdNetTotal = Prodtotal;
											} else {
												prodTax = "0.0";
												ProdNetTotal = Prodtotal;
											}
										}

										/** total Calculation End **/

										Log.d("update", "-> update");
										SOTDatabase.updateDeliveryVerification(
												pId, dCarton, dWeight, total,
												prodTax, proSubtotal,
												ProdNetTotal);

									} catch (Exception e) {

									}

									mCursor.requery();
									mCustomAdapter.notifyDataSetChanged();
									scanbarcode_edt.setText("");
									scanbarcode_edt.requestFocus();

								} while (cursor.moveToNext());
							}
						} else {
							scanbarcode_edt.setText("");
							scanbarcode_edt.requestFocus();
							Toast.makeText(
									getApplicationContext(),
									" This Product code is not available in this order ",
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					scanbarcode_edt.setText("");
					scanbarcode_edt.requestFocus();
					Toast.makeText(getApplicationContext(),
							" Invalid Barcode ", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				scanbarcode_edt.setText("");
				scanbarcode_edt.requestFocus();
				Toast.makeText(getApplicationContext(), " Invalid Barcode ",
						Toast.LENGTH_SHORT).show();
			}

		}

		scanbarcode_edt.addTextChangedListener(new TextWatcher() {
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
				// textlength = scanbarcode_edt.getText().length();
			}
		});
	}

	public void clearView() {
		saveIcon.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteAllProduct();
		SOTDatabase.deleteBillDisc();
		mList.setAdapter(null);
		mCursor.requery();
		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");

		/*
		 * Intent i = new Intent(SalesSummary.this, SalesOrderHeader.class);
		 * startActivity(i);
		 */
		Intent intent = new Intent(DeliveryVerificationDetails.this,
				DeliveryVerificationHeader.class);
		startActivity(intent);
		DeliveryVerificationDetails.this.finish();

	}

	public void editAlertDialog(final String id, String pName, double carton,
			double weight) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(pName);
		final LayoutParams lparams = new LayoutParams(150, 80); // Width ,
																// height
		lparams.setMargins(5, 5, 5, 5);

		TextView txtCarton = new TextView(this);
		 txtCarton.setLayoutParams(lparams);
		txtCarton.setGravity(Gravity.CENTER);
		txtCarton.setText("Carton");

		dialogCarton = new EditText(this);
		 dialogCarton.setLayoutParams(lparams);
		dialogCarton.setGravity(Gravity.CENTER);
		dialogCarton.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
		dialogCarton.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
//		dialogCarton.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		dialogCarton.setText(carton + "");
		// dialogCarton.requestFocus(dialogCarton.getText().length());
		// InputMethodManager inputMethodManager = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// inputMethodManager.showSoftInput(dialogCarton,InputMethodManager.SHOW_IMPLICIT);

		TextView txtWeight = new TextView(this);
		 txtWeight.setLayoutParams(lparams);
		txtWeight.setGravity(Gravity.CENTER);
		txtWeight.setText("Weight");

		dialogWeight = new EditText(this);
		dialogWeight.setLayoutParams(lparams);
		dialogWeight.setGravity(Gravity.CENTER);
		dialogWeight.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
		dialogWeight.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		dialogWeight.setText(weight + "");
		dialogWeight.requestFocus(dialogWeight.getText().length());
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(dialogCarton,
				InputMethodManager.SHOW_IMPLICIT);

		LinearLayout cartonlayout = new LinearLayout(this);
		cartonlayout.setOrientation(LinearLayout.HORIZONTAL);
//		cartonlayout.setLayoutParams(lparams);
		cartonlayout.addView(txtCarton);
		cartonlayout.addView(dialogCarton);

		LinearLayout weightlayout = new LinearLayout(this);
//		weightlayout.setLayoutParams(lparams);
		weightlayout.setOrientation(LinearLayout.HORIZONTAL);
		weightlayout.addView(txtWeight);
		weightlayout.addView(dialogWeight);

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setGravity(Gravity.CENTER);
		ll.addView(cartonlayout);
		ll.addView(weightlayout);

		alertDialog.setView(ll);
		alertDialog.setCancelable(false);

		dialogCarton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (dialogCarton.getText().toString().equals("0.00")
						|| dialogCarton.getText().toString().equals("0")
						|| dialogCarton.getText().toString().equals("0.0")
						|| dialogCarton.getText().toString().equals(".0")
						|| dialogCarton.getText().toString().equals("0.000")
						|| dialogCarton.getText().toString().equals("0.0000")
						|| dialogCarton.getText().toString().equals("0.00000")) {
					dialogCarton.setText("");
				}
				return false;
			}
		});
		
		dialogWeight.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (dialogWeight.getText().toString().equals("0.00")
						|| dialogWeight.getText().toString().equals("0")
						|| dialogWeight.getText().toString().equals("0.0")
						|| dialogWeight.getText().toString().equals(".0")
						|| dialogWeight.getText().toString().equals("0.000")
						|| dialogWeight.getText().toString().equals("0.0000")
						|| dialogWeight.getText().toString().equals("0.00000")) {
					dialogWeight.setText("");
				}
				return false;
			}
		});
		
		
		alertDialog.setPositiveButton("Update",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String carton = dialogCarton.getText().toString();
						String weight = dialogWeight.getText().toString();

						Cursor selectCursor = SOTDatabase.getCursorForId(id);
						if (selectCursor != null && selectCursor.getCount() > 0) {

							if (selectCursor.moveToFirst()) {
								do {

									String pId = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID));
									String pCode = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
									String cQty = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
									String lQty = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
									String qty = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
									String foc = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_FOC));
									String price = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
									String itemDiscount = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
									String taxType = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
									String taxValue = selectCursor.getString(selectCursor
											.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));

									double dCarton = 0, dWeight = 0;

									if (carton != null && !carton.isEmpty()) {
										dCarton = Double.parseDouble(carton);
									}

									if (weight != null && !weight.isEmpty()) {
										dWeight = Double.parseDouble(weight);
									}

									//
									// if(carton != null && !carton.isEmpty()){
									// dCarton = Double.parseDouble(cQty);
									// dCarton = dCarton +1;
									// }else{
									// dCarton = 1.00;
									// }
									//
									// if(qty != null && !qty.isEmpty()){
									// dWeight = Double.parseDouble(qty) +
									// Double.parseDouble(mWeight);
									// }else{
									// dWeight = Double.parseDouble(mWeight);
									// }

									try {
										/** total Calculation Start **/
										double total = 0.0, itmDisc = 0.0;
										double taxAmount = 0.0, netTotal = 0.0;
										double taxAmount1 = 0.0, netTotal1 = 0.0;
										String prodTax = "0", ProdNetTotal = "0", proSubtotal = "0";

										if (price.matches("")) {
											price = "0";
										}

										double itemDiscountCalc = 0.0;
										if (!itemDiscount.matches("")) {
											itemDiscountCalc = Double
													.parseDouble(itemDiscount);
										}

										if (!price.matches("")) {

											double slPriceCalc = Double
													.parseDouble(price);

											if (!itemDiscount.matches("")) {
												total = (dWeight * slPriceCalc)
														- itemDiscountCalc;
												// tt = (qty * slPriceCalc);
											} else {
												total = dWeight * slPriceCalc;
											}

											String Prodtotal = twoDecimalPoint(total);

											double subTotal = 0.0;

											if (!itemDiscount.matches("")) {
												itmDisc = Double
														.parseDouble(itemDiscount);
												subTotal = total - itmDisc;
											} else {
												subTotal = total;
											}

											proSubtotal = twoDecimalPoint(subTotal);

											if (!taxType.matches("")
													&& !taxValue.matches("")) {

												double taxValueCalc = Double
														.parseDouble(taxValue);

												if (taxType.matches("E")) {

													if (!itemDiscount
															.matches("")) {
														taxAmount1 = (subTotal * taxValueCalc) / 100;
														prodTax = fourDecimalPoint(taxAmount1);
														netTotal1 = subTotal
																+ taxAmount1;
														ProdNetTotal = twoDecimalPoint(netTotal1);
													} else {
														taxAmount = (total * taxValueCalc) / 100;
														prodTax = fourDecimalPoint(taxAmount);
														netTotal = total
																+ taxAmount;
														ProdNetTotal = twoDecimalPoint(netTotal);
													}

												} else if (taxType.matches("I")) {
													if (!itemDiscount
															.matches("")) {
														taxAmount1 = (subTotal * taxValueCalc)
																/ (100 + taxValueCalc);
														prodTax = fourDecimalPoint(taxAmount1);
														netTotal1 = subTotal;
														ProdNetTotal = twoDecimalPoint(netTotal1);
													} else {
														taxAmount = (total * taxValueCalc)
																/ (100 + taxValueCalc);
														prodTax = fourDecimalPoint(taxAmount);
														netTotal = total;
														ProdNetTotal = twoDecimalPoint(netTotal);
													}

												} else if (taxType.matches("Z")) {

													prodTax = "0.0";
													if (!itemDiscount
															.matches("")) {
														// netTotal1 = subTotal
														// + taxAmount;

														netTotal1 = subTotal;
														ProdNetTotal = twoDecimalPoint(netTotal1);
													} else {
														// netTotal = tt +
														// taxAmount;
														netTotal = total;
														ProdNetTotal = Prodtotal;
													}

												} else {
													prodTax = "0.0";
													ProdNetTotal = twoDecimalPoint(netTotal);
												}

											} else if (taxValue.matches("")) {
												prodTax = "0.0";
												ProdNetTotal = Prodtotal;
											} else {
												prodTax = "0.0";
												ProdNetTotal = Prodtotal;
											}
										}

										/** total Calculation End **/

										Log.d("update", "-> update");
										SOTDatabase.updateDeliveryVerification(
												pId, dCarton, dWeight, total,
												prodTax, proSubtotal,
												ProdNetTotal);

									} catch (Exception e) {

									}

									mCursor.requery();
									mCustomAdapter.notifyDataSetChanged();
									scanbarcode_edt.setText("");
									scanbarcode_edt.requestFocus();

									dialog.dismiss();

								} while (selectCursor.moveToNext());
							}
						}

						// if (weight != null && !weight.isEmpty()) {
						// SOTDatabase.updateExpenseAmount(id, mAmt);
						// InputMethodManager imm = (InputMethodManager)
						// getSystemService(Context.INPUT_METHOD_SERVICE);
						// imm.hideSoftInputFromWindow(dialogWeight.getWindowToken(),
						// 0);
						//
						// mCursor.requery();
						//
						// dialog.dismiss();
						// }

					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								dialogWeight.getWindowToken(), 0);
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

	public void loadprogress() {
		spinnerLayout = new LinearLayout(DeliveryVerificationDetails.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		DeliveryVerificationDetails.this.addContentView(spinnerLayout,
				new LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(main_parent, false);
		progressBar = new ProgressBar(DeliveryVerificationDetails.this);
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

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(DeliveryVerificationDetails.this,
				DeliveryVerificationHeader.class);
		startActivity(i);
		DeliveryVerificationDetails.this.finish();
	}

}
