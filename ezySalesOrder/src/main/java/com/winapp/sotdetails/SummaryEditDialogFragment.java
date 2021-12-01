package com.winapp.sotdetails;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.winapp.adapter.Attribute;
import com.winapp.SFA.R;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.sot.InvoiceAddProduct;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesAddProduct;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesSummary;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class SummaryEditDialogFragment extends DialogFragment {

	private String id = "", slPrice = "", slUomCode = "", slCartonPerQty = "",
			taxType = "", taxValue = "", ss_Cqty, beforeLooseQty,
			priceflag = "", calCarton = "",mimimumSellingPrice="",mimimumCartonSellingPrice="",haveAttribute="",
			cprice="",haveBatch="",haveExpiry="";
	private EditText sl_codefield, sl_cartonQty, sl_namefield, sl_looseQty,
			sl_qty, sl_foc, sl_price, sl_itemDiscount, sl_uom, sl_total,sl_total_inclusive,
			sl_tax, sl_netTotal, sl_cartonPerQty, sl_cprice, sl_exchange;
	private LinearLayout uomcperqty_ll, foc_layout, price_txt_layout;
	private ImageView expand, mDialogUpdateImgV, mDialogCancelImgV;
	private TextView price_txt,txt_price;
	private double tt, itmDisc = 0, netTtal = 0, taxAmount = 0;
	private TextWatcher cqtyTW, lqtyTW, qtyTW;
	private Cursor cursor;
	private SummaryEditDialogListener mSummaryEditDialogListener;
	private ArrayList<Attribute> colorarrvalues=new ArrayList<>();

	public static SummaryEditDialogFragment newInstance(String id) {
		Log.d("idcheckDialog",id);
		SummaryEditDialogFragment frag = new SummaryEditDialogFragment();
		Bundle args = new Bundle();
		args.putString("ID", id);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View dialogView = inflater
				.inflate(R.layout.cart_item, container, false);

		id = getArguments().getString("ID");
		SOTDatabase.init(getActivity());
		cursor = SOTDatabase.getCursorForEditProduct(id);
		cursor.moveToPosition(0);
		mDialogCancelImgV = (ImageView) dialogView.findViewById(R.id.close);
		mDialogUpdateImgV = (ImageView) dialogView.findViewById(R.id.ok);
		foc_layout = (LinearLayout) dialogView.findViewById(R.id.foc_layout);
		sl_codefield = (EditText) dialogView.findViewById(R.id.sl_codefield);
		sl_namefield = (EditText) dialogView.findViewById(R.id.sl_namefield);
		sl_cartonQty = (EditText) dialogView.findViewById(R.id.sl_cartonQty);
		sl_looseQty = (EditText) dialogView.findViewById(R.id.sl_looseQty);
		sl_qty = (EditText) dialogView.findViewById(R.id.sl_qty);
		sl_foc = (EditText) dialogView.findViewById(R.id.sl_foc);
		sl_price = (EditText) dialogView.findViewById(R.id.sl_price);
		sl_itemDiscount = (EditText) dialogView
				.findViewById(R.id.sl_itemDiscount);
		sl_cartonPerQty = (EditText) dialogView
				.findViewById(R.id.sl_cartonPerQty);
		sl_uom = (EditText) dialogView.findViewById(R.id.sl_uom);
		sl_total = (EditText) dialogView.findViewById(R.id.sl_total);
		sl_total_inclusive = (EditText) dialogView.findViewById(R.id.sl_total_inclusive);
		sl_tax = (EditText) dialogView.findViewById(R.id.sl_tax);
		sl_netTotal = (EditText) dialogView.findViewById(R.id.sl_netTotal);

		sl_cprice = (EditText) dialogView.findViewById(R.id.sl_cprice);
		sl_exchange = (EditText) dialogView.findViewById(R.id.sl_exchange);
		price_txt = (TextView) dialogView.findViewById(R.id.price_txt);
		expand = (ImageView) dialogView.findViewById(R.id.expand);

		price_txt_layout = (LinearLayout) dialogView.findViewById(R.id.price_txt_layout);

		txt_price= (TextView) dialogView.findViewById(R.id.txt_price);

		uomcperqty_ll = (LinearLayout) dialogView
				.findViewById(R.id.uomcperqty_ll);
		final Spinner prompt = (Spinner) dialogView
				.findViewById(R.id.weight_status);
		uomcperqty_ll.setVisibility(View.VISIBLE);

		prompt.setVisibility(View.GONE);

		priceflag = SalesOrderSetGet.getCartonpriceflag();
		calCarton = LogOutSetGet.getCalcCarton();

//		taxType = cursor.getString(cursor
//				.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
		taxValue = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));

		taxType = SalesOrderSetGet.getCompanytax();
//		taxValue = SalesOrderSetGet.getTaxValue();

		slCartonPerQty = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
		slPrice = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
		cprice = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));

		mimimumSellingPrice = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_MINIMUM_SELLING_PRICE));
		mimimumCartonSellingPrice = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_MINIMUM_CARTON_SELLING_PRICE));

		Log.d("mimimumCartonSellingPrice","-->"+mimimumSellingPrice+"-->"+mimimumCartonSellingPrice);

		mDialogUpdateImgV.setOnClickListener(mUpdateOnClickListener);
		mDialogCancelImgV.setOnClickListener(mDismissOnClickListener);
		if (priceflag.matches("null") || priceflag.matches("")) {
			priceflag = "0";
		}

		if (priceflag.matches("1")) {
			sl_cprice.setVisibility(View.VISIBLE);
			price_txt.setVisibility(View.GONE);

			price_txt_layout.setVisibility(View.VISIBLE);
		} else {
			sl_cprice.setVisibility(View.GONE);
			price_txt.setVisibility(View.VISIBLE);

			price_txt_layout.setVisibility(View.GONE);
		}

		String prodcodestr = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
		String snostr = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
		haveBatch = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_HAVE_BATCH));
		haveExpiry = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_HAVE_EXPIRY));
		sl_codefield.setText(prodcodestr);
		sl_namefield.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));
		sl_cartonQty.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));
		sl_looseQty.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));
		sl_qty.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));
		sl_price.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE)));
		sl_foc.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_FOC)));
		sl_itemDiscount.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT)));
		sl_uom.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_UOM)));
		sl_cprice.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE)));
		sl_exchange.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY)));

		String netTotal = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
		String tax = cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_TAX));

		Log.d("showValues","->"+netTotal+"tax:"+tax+","+cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL)));

		sl_total.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL)));
		sl_tax.setText(tax);
		sl_netTotal.setText(netTotal);
		sl_total.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL)));


		if(getActivity() instanceof SalesSummary){
			if(haveBatch!=null && !haveBatch.isEmpty() && haveExpiry!=null && !haveExpiry.isEmpty()){
				if(haveBatch.equalsIgnoreCase("True") && haveExpiry.equalsIgnoreCase("True")){
					sl_exchange.setEnabled(false);
					sl_exchange.setFocusable(false);
					sl_exchange.setGravity(Gravity.CENTER);
					sl_exchange.setBackgroundResource(R.drawable.labelbg);
					sl_exchange.setFocusableInTouchMode(false);
				}else{
					sl_exchange.setEnabled(true);
					sl_exchange.setBackgroundResource(R.drawable.ic_txt_enable_bg);
					sl_exchange.setFocusableInTouchMode(true);
				}
			}else{
				sl_exchange.setEnabled(true);
				sl_exchange.setBackgroundResource(R.drawable.ic_txt_enable_bg);
				sl_exchange.setFocusableInTouchMode(true);
			}
		}


// Added New 13.04.2017
		if (taxType != null && !taxType.isEmpty()) {
			if (taxType.matches("I")) {
				double dTax = tax.equals("") ? 0 : Double.valueOf(tax);
				double dNetTotal = netTotal.equals("") ? 0 : Double.valueOf(netTotal);
				double dTotalIncl = dNetTotal - dTax;
				String totIncl = twoDecimalPoint(dTotalIncl);
				sl_total_inclusive.setText(totIncl);
				sl_total.setVisibility(View.GONE);
				sl_total_inclusive.setVisibility(View.VISIBLE);
			}else{
				sl_total_inclusive.setText(cursor.getString(cursor
						.getColumnIndex(SOTDatabase.COLUMN_TOTAL)));
				sl_total.setVisibility(View.VISIBLE);
				sl_total_inclusive.setVisibility(View.GONE);
			}
		}else{
			sl_total_inclusive.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TOTAL)));
			sl_total.setVisibility(View.VISIBLE);
			sl_total_inclusive.setVisibility(View.GONE);
		}

//		sl_total.setText(cursor.getString(cursor
//				.getColumnIndex(SOTDatabase.COLUMN_TOTAL)));
//		sl_tax.setText(tax);
//		sl_netTotal.setText(netTotal);
//		sl_total.setText(cursor.getString(cursor
//				.getColumnIndex(SOTDatabase.COLUMN_TOTAL)));

		if (priceflag.matches("1")) {

			if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
					|| slCartonPerQty.matches("")) {
				sl_price.setText("");
				sl_price.setVisibility(View.INVISIBLE);
				txt_price.setVisibility(View.GONE);
			}else{
				sl_price.setVisibility(View.VISIBLE);
				txt_price.setVisibility(View.VISIBLE);
			}

		} else {
			sl_price.setVisibility(View.VISIBLE);
			txt_price.setVisibility(View.VISIBLE);
		}

		if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
				|| slCartonPerQty.matches("")) {

			// sl_cartonQty.setEnabled(false);
			sl_cartonQty.setFocusable(false);
			sl_cartonQty.setBackgroundResource(R.drawable.ic_edit_disable_bg);

			// sl_looseQty.setEnabled(false);
			sl_looseQty.setFocusable(false);
			sl_looseQty.setBackgroundResource(R.drawable.ic_edit_disable_bg);

			sl_qty.requestFocus();

		} else {
			sl_cartonQty.setFocusableInTouchMode(true);
			sl_cartonQty.setBackgroundResource(R.drawable.ic_edit_enable_bg);

			sl_looseQty.setFocusableInTouchMode(true);
			sl_looseQty.setBackgroundResource(R.drawable.ic_edit_enable_bg);

			sl_cartonQty.requestFocus();
		}
		sl_qty.setFocusableInTouchMode(true);
		sl_qty.setBackgroundResource(R.drawable.ic_edit_enable_bg);

		/*if (FormSetterGetter.isEditPrice()) {
			sl_price.setEnabled(true);
			sl_price.setFocusableInTouchMode(true);
			sl_price.setBackgroundResource(R.drawable.ic_edit_enable_bg);
		} else {
			sl_price.setEnabled(false);
			sl_price.setFocusable(false);
			sl_price.setGravity(Gravity.CENTER);
			sl_price.setBackgroundResource(R.drawable.ic_edit_disable_bg);
		}*/
		if (FormSetterGetter.isEditPrice()) {

			sl_price.setEnabled(true);
			sl_price.setFocusableInTouchMode(true);
			sl_price.setBackgroundResource(R.drawable.ic_edit_enable_bg);

			sl_cprice.setEnabled(true);
			sl_cprice.setFocusableInTouchMode(true);
			sl_cprice.setBackgroundResource(R.drawable.ic_edit_enable_bg);

//			sl_price.setBackgroundResource(R.drawable.edittext_bg);
//			sl_cprice.setBackgroundResource(R.drawable.edittext_bg);
		} else {

			double priceCheckZero = 0;
			if(!slPrice.matches("")){
				priceCheckZero = Double.parseDouble(slPrice);
			}

			if(priceCheckZero>0){
				sl_price.setEnabled(false);
				sl_price.setFocusable(false);
				sl_price.setGravity(Gravity.CENTER);
				sl_price.setBackgroundResource(R.drawable.labelbg);
				sl_price.setFocusableInTouchMode(false);
			}else{
				sl_price.setEnabled(true);
				sl_price.setFocusableInTouchMode(true);
				sl_price.setBackgroundResource(R.drawable.ic_edit_enable_bg);
//				sl_price.setEnabled(true);
//				sl_price.setBackgroundResource(R.drawable.edittext_bg);
//				sl_price.setFocusableInTouchMode(true);

			}

			double cpriceCheckZero = 0;
			if(!cprice.matches("")){
				cpriceCheckZero = Double.parseDouble(cprice);
			}

			if(cpriceCheckZero>0){
				sl_cprice.setEnabled(false);
				sl_cprice.setFocusable(false);
				sl_cprice.setGravity(Gravity.CENTER);
				sl_cprice.setBackgroundResource(R.drawable.labelbg);
				sl_cprice.setFocusableInTouchMode(false);
			}else{

				sl_cprice.setEnabled(true);
				sl_cprice.setFocusableInTouchMode(true);
				sl_cprice.setBackgroundResource(R.drawable.ic_edit_enable_bg);
//				sl_cprice.setEnabled(true);
//				sl_cprice.setBackgroundResource(R.drawable.edittext_bg);
//				sl_cprice.setFocusableInTouchMode(true);
			}
		}

		sl_foc.setFocusableInTouchMode(true);
		sl_foc.setBackgroundResource(R.drawable.ic_edit_enable_bg);
		sl_itemDiscount.setFocusableInTouchMode(true);
		sl_itemDiscount.setBackgroundResource(R.drawable.ic_edit_enable_bg);
		sl_uom.setFocusableInTouchMode(false);
		sl_uom.setBackgroundResource(R.drawable.ic_edit_disable_bg);
		sl_cartonPerQty.setText(cursor.getString(cursor
				.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));

		try{
			haveAttribute = SalesOrderSetGet.getHaveAttribute();

			if(haveAttribute!=null && !haveAttribute.isEmpty()){

			}else{
				haveAttribute="";
			}

			colorarrvalues = SOTDatabase.getAttributeColorValues(prodcodestr, snostr);

			if(haveAttribute.matches("2")){

				if(colorarrvalues.size()>0){
					sl_price.setEnabled(false);
					sl_price.setFocusable(false);
					sl_price.setGravity(Gravity.CENTER);
					sl_price.setBackgroundResource(R.drawable.ic_edit_disable_bg);

					sl_qty.setEnabled(false);
					sl_qty.setFocusable(false);
					sl_qty.setGravity(Gravity.CENTER);
					sl_qty.setBackgroundResource(R.drawable.ic_edit_disable_bg);
				}
			}
		}catch (Exception e){

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

		sl_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				ss_Cqty = sl_cartonQty.getText().toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeLooseQty = sl_looseQty.getText().toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

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
									sl_cartonQty.addTextChangedListener(cqtyTW);

									sl_looseQty
											.removeTextChangedListener(lqtyTW);
									sl_looseQty.setText("");
									sl_looseQty.addTextChangedListener(lqtyTW);
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

								sl_cartonQty.removeTextChangedListener(cqtyTW);
								sl_cartonQty.setText("");
								sl_cartonQty.addTextChangedListener(cqtyTW);

								sl_looseQty.removeTextChangedListener(lqtyTW);
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

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
		sl_itemDiscount.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT
						|| actionId == EditorInfo.IME_ACTION_DONE) {

					InputMethodManager imm = (InputMethodManager) getActivity()
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

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

		return dialogView;
	}

	private OnClickListener mUpdateOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			double slprice = 0,piecePerCarton = 0, minSellingPrice = 0,miniCartonSellingPrice=0,slcprice=0;
			String price = sl_price.getText().toString();
			String cprice = sl_cprice.getText().toString();
			if (!price.matches("")) {
				slprice = Double.parseDouble(price);
			}
			if (!cprice.matches("")) {
				slcprice = Double.parseDouble(cprice);
			}



//			if (mimimumSellingPrice.matches("")||(mimimumSellingPrice==null)) {
//
//				minSellingPrice = 0.00;
//			 }
//			 else{
//				minSellingPrice = Double
//						.parseDouble(mimimumSellingPrice);
//			}
//
//		if (mimimumCartonSellingPrice.matches("")||(mimimumCartonSellingPrice==null)) {
//
//			miniCartonSellingPrice = 0.00;
//			}
//			else{
//			miniCartonSellingPrice = Double
//					.parseDouble(mimimumCartonSellingPrice);
//			}




			if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
				piecePerCarton = Double.parseDouble(slCartonPerQty);
			}
			if (priceflag.matches("1")) {
			/*	if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
						|| slCartonPerQty.matches("")) {
					if (minSellingPrice > slcprice) {
						Toast.makeText(
								getActivity(),
								"Price must be greater than minimum selling price $ "
										+ mimimumSellingPrice,
								Toast.LENGTH_LONG).show();
					} else {
						storeDatabase();
					}
				}else{
					storeDatabase();
				}*/
				if (miniCartonSellingPrice > slcprice) {
					sl_cprice.requestFocus();
					Toast.makeText(
							getActivity(),
							"Carton Price must be greater than Minimum carton selling price $ "
									+ miniCartonSellingPrice,
							Toast.LENGTH_LONG).show();
				} else if (piecePerCarton>1){
					if(minSellingPrice > slprice) {
						sl_price.requestFocus();
						Toast.makeText(
								getActivity(),
								"Price must be greater than minimum selling price $ "
										+ mimimumSellingPrice,
								Toast.LENGTH_LONG).show();
					}else{
						storeDatabase();
					}
				}

				else {
					storeDatabase();
				}
			} else {
				if (minSellingPrice > slprice) {
					Toast.makeText(
							getActivity(),
							"Price must be greater than minimum selling price $ "
									+ mimimumSellingPrice,
							Toast.LENGTH_LONG).show();
				} else {
					storeDatabase();

				}
			}

		}
	};

	private OnClickListener mDismissOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			dismiss();
		}
	};

	public void storeDatabase(){

		double cartTotal, cartTax, cartNetTotal;
		String codeStr = sl_codefield.getText().toString();
		String nameStr = sl_namefield.getText().toString();
		String uomStr = sl_uom.getText().toString();
		String cartonQtyStr = sl_cartonQty.getText().toString();
		String looseQtyStr = sl_looseQty.getText().toString();
		String qtyStr = sl_qty.getText().toString();
		String focStr = sl_foc.getText().toString();
		String priceStr = sl_price.getText().toString();
		String dicountStr = sl_itemDiscount.getText().toString();
		String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
		String totalStr = sl_total.getText().toString();
		String taxStr = sl_tax.getText().toString();
		String netTotalStr = sl_netTotal.getText().toString();
		String cpriceStr = sl_cprice.getText().toString();
		String exQtyStr = sl_exchange.getText().toString();

		int Id = 0;

		if (id != null && !id.isEmpty()) {
			Id = Integer.valueOf(id);
		}

		if (priceStr.matches("")) {
			priceStr = "0";
		}

		if (cpriceStr.matches("")) {
			cpriceStr = "0";
		}

		try {
			if (calCarton.matches("1") && qtyStr.matches("")
					&& focStr.matches("") && exQtyStr.matches("")) {
				Toast.makeText(getActivity(), "Enter the quantity",
						Toast.LENGTH_SHORT).show();

			} else if (calCarton.matches("0") && cartonQtyStr.matches("")
					&& looseQtyStr.matches("") && qtyStr.matches("")) {
				Toast.makeText(getActivity(), "Enter the carton/quantity",
						Toast.LENGTH_SHORT).show();
			} else {

				int foc = 0;
				double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0, cartonQty = 0, looseQty = 0, qty = 0, cartonPerQty = 0;
				String sbTtl = "";
				String netT = "";
				if (!cartonQtyStr.matches("")) {
					cartonQty = Double.parseDouble(cartonQtyStr);
				}
				if (!looseQtyStr.matches("")) {
					looseQty = Double.parseDouble(looseQtyStr);
				}
				if (!qtyStr.matches("")) {
					qty = Double.parseDouble(qtyStr);
				}
				if (!focStr.matches("")) {
					foc = Integer.parseInt(focStr);
				}
				if (!cartonPerQtyStr.matches("")) {
					cartonPerQty = Double.parseDouble(cartonPerQtyStr);
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

					String itemDisc = sl_itemDiscount.getText().toString();
					if (!itemDisc.matches("")) {
						itmDisc = Double.parseDouble(itemDisc);
						subTotal = total;
					} else {
						subTotal = total;
					}

					sbTtl = twoDecimalPoint(subTotal);

				}
					int i_sssno = Integer.parseInt(id);
					SOTDatabase.updateBillDisc(i_sssno, nameStr, sbTtl);


				if (!taxStr.matches("")) {
					tax = Double.parseDouble(taxStr);
				}
				if (!netTotalStr.matches("")) {
					if (taxType != null && !taxType.isEmpty()) {
						if (taxType.matches("I") || taxType.matches("Z")) {
							ntTot = subTotal;
						} else {
							ntTot = subTotal + tax;
						}
					} else {
						ntTot = subTotal + tax;
					}

					netT = twoDecimalPoint(ntTot);
				}

				if (taxValue.matches("") || taxValue == null) {
					taxValue = "0";
				}

				if (priceflag.matches("0")) {
					itemDiscountCalc();
				} else if (priceflag.matches("1")) {
					itemDiscountCalcNew();
				}

				String disctStr = sl_itemDiscount.getText().toString();
				if (!disctStr.matches("")) {
					discount = Double.parseDouble(disctStr);

				}
				String totl = twoDecimalPoint(tt);
				Log.d("total" + tt, totl);

				double dis = 0.0;
				if (!dicountStr.matches("")) {
					dis = Double.parseDouble(dicountStr);
				}
				Log.d("tt+dis", "->" + tt + dis+"slno:"+Id);

				SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
						looseQty, qty, foc, price, discount, uomStr,
						cartonPerQty, tt + dis, tax, sbTtl, netT, Id,
						cpriceStr, exQtyStr);

				Cursor cursor = SOTDatabase.getCursor();
				cursor.requery();
				Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT)
						.show();
				mSummaryEditDialogListener = (SummaryEditDialogListener) getActivity();
				mSummaryEditDialogListener.refreshAdapter();

				dismiss();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

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

			if (!itmDscnt.matches("") && !qty.matches("") && !prc.matches("")) {

				double itemDiscountCalc = 0.0;

				itemDiscountCalc = Double.parseDouble(itmDscnt);

				double quantityCalc = Double.parseDouble(qty);
				double priceCalc = Double.parseDouble(prc);

				tt = (quantityCalc * priceCalc) - itemDiscountCalc;

				Log.d("ttl", "" + tt);
				String Prodtotal = twoDecimalPoint(tt);
				sl_total.setText("" + Prodtotal);
				sl_total_inclusive.setText("" + Prodtotal);
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

						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);


						double dTotalIncl = netTotal - taxAmount;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);

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
				sl_total_inclusive.setText("" + Prodtotal);
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


							double dTotalIncl = netTotal1 - taxAmount1;
							String totalIncl = twoDecimalPoint(dTotalIncl);
							Log.d("totalIncl", "" + totalIncl);
							sl_total_inclusive.setText(totalIncl);
						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);


							double dTotalIncl = netTotal - taxAmount;
							String totalIncl = twoDecimalPoint(dTotalIncl);
							Log.d("totalIncl", "" + totalIncl);
							sl_total_inclusive.setText(totalIncl);
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

					if (priceflag.matches("0")) {
						productTotal(qty_nt);
					} else if (priceflag.matches("1")) {
						productTotalNew();
					}

					// sl_cartonQty.setText("" + ctn);
					// sl_looseQty.setText("" + loose);

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
				sl_total_inclusive.setText("" + Prodtotal);
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

						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);


						double dTotalIncl = netTotal - taxAmount;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);

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
			sl_total_inclusive.setText("" + Prodtotal);
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


						double dTotalIncl = netTotal1 - taxAmount1;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);

					} else {
						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);


						double dTotalIncl = netTotal - taxAmount;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);
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

	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	public interface SummaryEditDialogListener {
		void refreshAdapter();
	}

}