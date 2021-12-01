package com.winapp.sot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.util.CustomCalendar;

public class BatchDialog extends DialogFragment {

	PopupWindow popupWindow1;

	AlertDialog.Builder myDialog;
	BatchListAdapter batchListAdapter;
	Calendar mCalendar;

	EditText expirydate,edtotalqty;
	String code = "";

	EditText batch_codefield, batch_namefield, batchno, bat_cartonQty,
			bat_looseQty, bat_qty, batch_cartonPerQty, bat_foc, sl_productcode, sl_name,
			sl_cartonqty, sl_looseqty, sl_qty, sl_foc, sl_uom, sl_stock, sl_pcspercarton, slPrice,
			sm_total, sm_total_new, sm_itemDisc, sm_subTotal, sm_tax, sm_netTotal, sm_cartonqty, sm_looseqty, sm_qty;
	
	ImageButton batch_add, batch_finish,batch_cancel;
	ListView batch_list;
	double tota = 0, smTax=0, sbTtl=0, netTotal;
	TextWatcher cqtyTW, lqtyTW, qtyTW;
	String beforeLooseQty, beforeFoc;
	String ss_Cqty = "";
	Activity mActivity;
	String batCode, btName, btCartonPerQty, havebatch, haveexpiry, Id, sl_price, calCarton="",addDetect="";
	String[] Cmd = { "Edit", "Delete" };
	Cursor cursor, sl_cursor;
	AlertDialog batDialog;
	private SOTDatabase sqldb;
	ArrayList<String> products_arr = new ArrayList<String>();
	TextView batchno_text, batchexpiry_text, serial_id, batchfoc_text;
	String newDate="";
	CustomCalendar customCalendar;
	EditText sl_StockinHand, sl_Total, sl_Tax, sl_Nettotal ;
	Button sl_Addbtn, sl_Minusbtn;
	
	int sl_no = 1;
	String str_ssupdate="", str_sscancel="", str_sssno="", sale_sl_no="";
	 
	/** GRA Summary **/
	
	public void initiateBatchPopupWindow(Activity context, String id, String sno, String haveBatch,
			String haveExpiry, String code, String name, String slCartonPerQty,Cursor crsr, String price,HashMap<String, EditText> hm) {

		this.mActivity = context;
		this.Id = id;
		this.sl_cursor = crsr;
		this.mCalendar = Calendar.getInstance();
		this.batCode = code;
		this.btName = name;
		this.btCartonPerQty = slCartonPerQty;
		this.havebatch = haveBatch;
		this.haveexpiry = haveExpiry;
		this.sl_price = price;
		
		this.sm_total = hm.get("sm_total");
		this.sm_total_new = hm.get("sm_total_new");
		this.sm_itemDisc = hm.get("sm_itemDisc");
		this.sm_subTotal = hm.get("sm_subTotal");
		this.sm_tax = hm.get("sm_tax");
		this.sm_netTotal = hm.get("sm_netTotal");
		this.sale_sl_no = sno;
		Log.d("ID", ""+Id);
		batch();
	}
	
/** StockTake Summary **/
	
	public void initiateBatchPopupWindow(Activity context, String id, String sno, String haveBatch,
			String haveExpiry, String code, String name, String slCartonPerQty,Cursor crsr, HashMap<String, EditText> hm, String temp) {

		this.mActivity = context;
		this.Id = id;
		this.sl_cursor = crsr;
		this.mCalendar = Calendar.getInstance();
		this.batCode = code;
		this.btName = name;
		this.btCartonPerQty = slCartonPerQty;
		this.havebatch = haveBatch;
		this.haveexpiry = haveExpiry;
		
		this.sm_cartonqty = hm.get("sm_cqty");
		this.sm_looseqty = hm.get("sm_lqty");
		this.sm_qty = hm.get("sm_qty");
		this.sale_sl_no = sno;
		Log.d("ID", ""+Id);
		batch();
	}
	
	/** StockAdjustment SummaryY **/
	public void initiateBatchPopupWindow(Activity context, String id, String sno, String haveBatch,
			String haveExpiry, String code, String name, String slCartonPerQty,Cursor crsr,HashMap<String, EditText> hm) {

		this.mActivity = context;
		this.Id = id;
		this.sl_cursor = crsr;
		this.mCalendar = Calendar.getInstance();
		this.batCode = code;
		this.btName = name;
		this.btCartonPerQty = slCartonPerQty;
		this.havebatch = haveBatch;
		this.haveexpiry = haveExpiry;
		
		this.sm_cartonqty = hm.get("sm_cqty");
		this.sm_looseqty = hm.get("sm_lqty");
		this.sm_qty = hm.get("sm_qty");
		this.sale_sl_no = sno;
		Log.d("ID", ""+Id);
		batch();
	}
	
	/** GRA Add Product **/
	public void initiateBatchPopupWindow(Activity context, String haveBatch,
			String haveExpiry, String code, String name, String slCartonPerQty, String price,HashMap<String, EditText> hm) {

		this.mActivity = context;
		
		this.mCalendar = Calendar.getInstance();
		this.batCode = code;
		this.btName = name;
		this.btCartonPerQty = slCartonPerQty;
		this.havebatch = haveBatch;
		this.haveexpiry = haveExpiry;
		
		this.sl_productcode = hm.get("Productcode");
		this.sl_name = hm.get("Productname");	
		this.sl_cartonqty = hm.get("Cartonqty");
		this.sl_looseqty = hm.get("Looseqty");
		this.sl_qty = hm.get("Qty");
		this.sl_foc = hm.get("Foc");
		this.sl_uom = hm.get("Uom");
		this.sl_stock = hm.get("Stock");
		this.sl_pcspercarton = hm.get("Cartonperqty");
		this.slPrice = hm.get("Price");
		this.sl_price = price;
		
		Cursor salcursor = SOTDatabase.getCursor();
		int cnt = salcursor.getCount();
//		if (salcursor != null && salcursor.getCount() > 0) {
			cnt = cnt+1;
			Log.d("sale count", ""+cnt);
			this.sale_sl_no = cnt+"";
//		}
		
		batch();
	}
	
	/** StockTake Add Product **/
	public void initiateBatchPopupWindow(Activity context, String haveBatch,
			String haveExpiry, String code, String name, String slCartonPerQty, HashMap<String, EditText> hm, String temp) {

		this.mActivity = context;
		
		this.mCalendar = Calendar.getInstance();
		this.batCode = code;
		this.btName = name;
		this.btCartonPerQty = slCartonPerQty;
		this.havebatch = haveBatch;
		this.haveexpiry = haveExpiry;
		
		this.sl_productcode = hm.get("Productcode");
		this.sl_name = hm.get("Productname");	
		this.sl_cartonqty = hm.get("Cartonqty");
		this.sl_looseqty = hm.get("Looseqty");
		this.sl_qty = hm.get("Qty");
		this.sl_stock = hm.get("Stock");
		this.sl_pcspercarton = hm.get("Cartonperqty");
		
		
		String dbSno = SOTDatabase.getProdSlno(batCode);
		
		if(dbSno != null && !dbSno.isEmpty()){
			this.sale_sl_no = dbSno;
		}else{
			Cursor salcursor = SOTDatabase.getCursor();
			int cnt = salcursor.getCount();
//			if (salcursor != null && salcursor.getCount() > 0) {
				cnt = cnt+1;
				Log.d("sale count", ""+cnt);
				this.sale_sl_no = cnt+"";
//			}
		}
		
		
		
		batch();
	}
	
	/** StockAdjustment Add Product**/
	 public void initiateBatchPopupWindow(Activity context, String haveBatch,
	   String haveExpiry, String code, String name, String slCartonPerQty, HashMap<String, Object> hm) {

	  this.mActivity = context;
	  
	  this.mCalendar = Calendar.getInstance();
	  this.batCode = code;
	  this.btName = name;
	  this.btCartonPerQty = slCartonPerQty;
	  this.havebatch = haveBatch;
	  this.haveexpiry = haveExpiry;
	  
	  this.sl_productcode = (EditText) hm.get("Productcode");
	  this.sl_name = (EditText) hm.get("Productname"); 
	  this.sl_cartonqty = (EditText) hm.get("Cartonqty");
	  this.sl_looseqty = (EditText) hm.get("Looseqty");
	  this.sl_qty = (EditText) hm.get("Qty");
	  this.sl_StockinHand = (EditText) hm.get("StockInHand");
	  this.sl_Total = (EditText) hm.get("Total");
	  this.sl_Tax = (EditText) hm.get("Tax");
	  this.sl_Nettotal = (EditText) hm.get("Net_Total");
	  this.sl_uom = (EditText) hm.get("Uom");
//	  this.sl_stock = (EditText) hm.get("Stock");
	  this.sl_pcspercarton = (EditText) hm.get("Cartonperqty");
	  this.sl_Addbtn = (Button) hm.get("AddStock");
	  this.sl_Minusbtn = (Button) hm.get("MinusStock");
	  
	  Cursor salcursor = SOTDatabase.getCursor();
		int cnt = salcursor.getCount();
//		if (salcursor != null && salcursor.getCount() > 0) {
			cnt = cnt+1;
			Log.d("sale count", ""+cnt);
			this.sale_sl_no = cnt+"";
//		}
	  
	  Log.d("sl_StockinHand",""+sl_StockinHand.getText().toString());
	  
	  batch();
	 }
	
	public void batch(){

		myDialog = new AlertDialog.Builder(mActivity);
		
		LayoutInflater li = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.addbatch, null, false);

		myDialog.setView(v);

		myDialog.setCancelable(false);
		sqldb = new SOTDatabase(mActivity);
//		sot = new SOTDatabase(mActivity);
		
		batchno_text = (TextView) v.findViewById(R.id.txt1);
		batchexpiry_text = (TextView) v.findViewById(R.id.txt2);
		serial_id = (TextView) v.findViewById(R.id.serial_id);
		batchfoc_text = (TextView) v.findViewById(R.id.batchfoc_text);
		
		batch_codefield = (EditText) v.findViewById(R.id.batch_codefield);
		batch_namefield = (EditText) v.findViewById(R.id.batch_namefield);
		batchno = (EditText) v.findViewById(R.id.batchno);
		expirydate = (EditText) v.findViewById(R.id.expirydate);
		bat_cartonQty = (EditText) v.findViewById(R.id.batch_cartonQty);
		bat_looseQty = (EditText) v.findViewById(R.id.batch_looseQty);
		bat_qty = (EditText) v.findViewById(R.id.batch_qty);
		batch_cartonPerQty = (EditText) v.findViewById(R.id.batch_cartonPerQty);
		bat_foc = (EditText) v.findViewById(R.id.batch_focQty);
		batch_add = (ImageButton) v.findViewById(R.id.bacth_add);
		batch_list = (ListView) v.findViewById(R.id.batch_list);
		
		edtotalqty= (EditText) v.findViewById(R.id.edtotalqty);
		
		batch_finish = (ImageButton) v.findViewById(R.id.batch_finish);
		batch_cancel = (ImageButton) v.findViewById(R.id.batch_cancel);
		calCarton =  LogOutSetGet.getCalcCarton();
		
		if (havebatch.matches("False")) {
			batchno.setFocusable(false);
			batchno_text.setVisibility(View.GONE);
			batchno.setBackgroundResource(R.drawable.labelbg);
			batchno.setVisibility(View.GONE);
		}

		if (haveexpiry.matches("False")) {
			expirydate.setFocusable(false);
			batchexpiry_text.setVisibility(View.GONE);
			expirydate.setBackgroundResource(R.drawable.labelbg);
			expirydate.setVisibility(View.GONE);
		}

		if (btCartonPerQty.matches("1") || btCartonPerQty.matches("0")
				|| btCartonPerQty.matches("")) {

			if(calCarton.matches("0")){
				bat_cartonQty.requestFocus();
							
			}else{
			
			bat_cartonQty.setFocusable(false);
			bat_cartonQty.setBackgroundResource(R.drawable.labelbg);

			bat_looseQty.setFocusable(false);
			bat_looseQty.setBackgroundResource(R.drawable.labelbg);

			bat_qty.requestFocus();
					
			}

		} else {
			bat_cartonQty.setFocusableInTouchMode(true);
			bat_cartonQty.setBackgroundResource(R.drawable.edittext_bg);

			bat_looseQty.setFocusableInTouchMode(true);
			bat_looseQty.setBackgroundResource(R.drawable.edittext_bg);

			bat_cartonQty.requestFocus();
		}
		
		serial_id.setText("");
		batch_codefield.setText(batCode);
		batch_namefield.setText(btName);
		batch_cartonPerQty.setText(btCartonPerQty);
		
		cursor = SOTDatabase.getBatchCursorWithSR(batCode, sale_sl_no+"");
		
		String autoBatch=SalesOrderSetGet.getAutoBatchNo();
		
		if (!havebatch.matches("False")) {
		
		if(autoBatch.matches("1")){
			batchno.setFocusable(false);
			batchno.setBackgroundResource(R.drawable.labelbg);
			
			Cursor batchcount = SOTDatabase.getBatCursor();
			String NextBatchNo = SalesOrderSetGet.getNextBatchNo();
			int count = batchcount.getCount();
			if(count>0){
				NextBatchNo = NextBatchNo+"-"+count;
			}
			batchno.setText(NextBatchNo);
	
		}
		}
		
		Log.d("cursor", "->" + cursor.toString());
		batchListAdapter = new BatchListAdapter(mActivity, cursor);
		batch_list.setAdapter(batchListAdapter);
		registerForContextMenu(batch_list);

		if(cursor.getCount()>0){
			batch_finish.setVisibility(View.VISIBLE);
		}else{
			batch_finish.setVisibility(View.INVISIBLE);
		}
		
		//added
		if(mActivity instanceof StockTakeAddProduct || mActivity instanceof StockTakeSummary){
			
			batchfoc_text.setVisibility(View.INVISIBLE);
			bat_foc.setVisibility(View.INVISIBLE);
		}
		
		batch_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String batchNo = "", expiryDate = "", batchQty = "";

				batchNo = batchno.getText().toString();
				expiryDate = expirydate.getText().toString();
				batchQty = bat_qty.getText().toString();
				String batchCarton=	bat_cartonQty.getText().toString();
				
				
				if (batchNo != null && !batchNo.isEmpty()) {
					if (expiryDate != null && !expiryDate.isEmpty()) {
						if (batchQty != null && !batchQty.isEmpty()) {
							storeBatch();
						} else {							

							if (calCarton.matches("0")) {
								
								if(batchCarton.matches("")){
									Toast.makeText(mActivity, "Enter the carton/quantity",
											Toast.LENGTH_SHORT).show();
									bat_cartonQty.requestFocus();
								}else{
									storeBatch();
								}
							}else{
								Toast.makeText(mActivity, "Enter Qty",
										Toast.LENGTH_SHORT).show();
								bat_qty.requestFocus();
							}	
							
						}
					} else {

						if (haveexpiry.matches("True")) {
//							expirydate.requestFocus();
							Toast.makeText(mActivity, "Select Expiry Date",
									Toast.LENGTH_SHORT).show();
						} else {
							storeBatch();
						}

					}
				} else {

					if (havebatch.matches("True")) {
						batchno.requestFocus();
						Toast.makeText(mActivity, "Enter Batch No",
								Toast.LENGTH_SHORT).show();
					} else {						
						if (expiryDate != null && !expiryDate.isEmpty()) {
							if (batchQty != null && !batchQty.isEmpty()) {

								storeBatch();

							} else {
								
								if (calCarton.matches("0")) {
									
									if(batchCarton.matches("")){
										Toast.makeText(mActivity, "Enter the carton/quantity",
												Toast.LENGTH_SHORT).show();
										bat_cartonQty.requestFocus();
									}else{
										storeBatch();
									}
									
								}else{
									Toast.makeText(mActivity, "Enter Qty",
											Toast.LENGTH_SHORT).show();
									
									bat_qty.requestFocus();
								}	
								
							}
						} else {

							if (haveexpiry.matches("True")) {
//								expirydate.requestFocus();
								Toast.makeText(mActivity, "Select Expiry Date",
										Toast.LENGTH_SHORT).show();
							} else {
//								storeBatch();
							}

						}
					}
				}

				batchno.requestFocus();
				// cursor = SOTDatabase.getBatchCursor(prodcode);
				cursor.requery();

			}
		});

		expirydate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				customCalendar = new CustomCalendar(mActivity);
				 customCalendar.showCalendarView();
				 boolean mstatus = customCalendar.showDialog();
				 Log.d("mstatus", mstatus+"");
				 if (mstatus == true) {
					 String sDate = customCalendar.getSelectDate();
					 expirydate.setText(sDate);
					} else {
						Log.d("False", "-->" + "False");
					}
			}
			});
				
		batch_finish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				String prodcode = batch_codefield.getText().toString();
				String totQty = SOTDatabase.getTotaBatchQtySR(prodcode, sale_sl_no+"");
				int totFocQty = SOTDatabase.getTotaBatchFocQtySR(prodcode, sale_sl_no+"");
				
				int totCqty = SOTDatabase.getTotalBatchCQtySR(prodcode, sale_sl_no+"");
				int totLqty = SOTDatabase.getTotalBatchLQtySR(prodcode, sale_sl_no+"");
				
				if(mActivity instanceof GraAddProduct){
								
					sl_cartonqty.setText(""+totCqty);
					sl_looseqty.setText(""+totLqty);
					
					sl_qty.setText(""+totQty);
					sl_foc.setText(""+totFocQty);
					
					sl_cartonqty.setFocusable(false);
					sl_cartonqty.setBackgroundResource(R.drawable.labelbg);
					
					sl_looseqty.setFocusable(false);
					sl_looseqty.setBackgroundResource(R.drawable.labelbg);
					
					sl_qty.setFocusable(false);
					sl_qty.setBackgroundResource(R.drawable.labelbg);

					sl_foc.setFocusable(false);
					sl_foc.setBackgroundResource(R.drawable.labelbg);
					
					slPrice.requestFocus();
					
					((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
			        .showSoftInput(slPrice, 2);
					
				} else if(mActivity instanceof GraSummary){					
					clQty(Id,""+totQty,totFocQty,btCartonPerQty,prodcode);
					
					cursor = SOTDatabase.getCursor();

					if (cursor != null && cursor.getCount() > 0) {

						tota = SOTDatabase.getTotal();
								Log.d("Tota", ""+tota);
								String smtotal= twoDecimalPoint(tota);

						smTax = SOTDatabase.getTax();
								String ProdTax = fourDecimalPoint(smTax);
								sm_tax.setText("" + ProdTax);

								sbTtl = SOTDatabase.getSubTotal();
								String sub = twoDecimalPoint(sbTtl);
								sm_subTotal.setText("" + sub);

						double  tot_item_disc = SOTDatabase.getTotalItemDisc();
								String tot_itemDisc = twoDecimalPoint(tot_item_disc);
								
								sm_itemDisc.setText(tot_itemDisc);
								
								sm_total.setText("" + sub);
								
								sm_total_new.setText("" + smtotal);								
								
								String taxType = SalesOrderSetGet.getCompanytax();
								if(taxType!=null && !taxType.isEmpty()){
									if (taxType.matches("I") || taxType.matches("Z")) {
										netTotal = sbTtl;
									}else{
										netTotal = sbTtl + smTax;
									}
								}else{
									netTotal = sbTtl + smTax;
								}
								
								String ProdNettotal = twoDecimalPoint(netTotal);
								sm_netTotal.setText("" + ProdNettotal);
					}
					
					
				}else if(mActivity instanceof StockTakeAddProduct){
					
					sl_cartonqty.setText(""+totCqty);
					sl_looseqty.setText(""+totLqty);
					sl_qty.setText(""+totQty);
					
					sl_cartonqty.setFocusable(false);
					sl_cartonqty.setBackgroundResource(R.drawable.labelbg);
					
					sl_looseqty.setFocusable(false);
					sl_looseqty.setBackgroundResource(R.drawable.labelbg);
					
					sl_qty.setFocusable(false);
					sl_qty.setBackgroundResource(R.drawable.labelbg);
					
				}else if(mActivity instanceof StockTakeSummary){
					
					HashMap<String, String> hm = new HashMap<String, String>();
					
					hm.put("product_id", Id);
					hm.put("carton_qty", totCqty+"");
					hm.put("loose_qty", totLqty+"");
					hm.put("quantity", totQty);

					sqldb.updateStockAdjustmentSummary(hm);
										
					sm_cartonqty.setText(twoDecimalPoint(SOTDatabase
							.getTotalCqty()));
					sm_looseqty.setText(twoDecimalPoint(SOTDatabase
							.getTotalLqty()));
					sm_qty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));
					
					sl_cursor.requery();
				    cursor.requery();	
				    
				}else if(mActivity instanceof StockAdjustmentAddproduct){
				     
					summaryStore();
				}else if(mActivity instanceof StockAdjustmentSummary){
					
					HashMap<String, String> hm = new HashMap<String, String>();
					
					hm.put("product_id", Id);
					hm.put("carton_qty", totCqty+"");
					hm.put("loose_qty", totLqty+"");
					hm.put("quantity", totQty);

					sqldb.updateStockAdjustmentSummary(hm);
										
					sm_cartonqty.setText(twoDecimalPoint(SOTDatabase
							.getTotalCqty()));
					sm_looseqty.setText(twoDecimalPoint(SOTDatabase
							.getTotalLqty()));
					sm_qty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));
					
					sl_cursor.requery();
				    cursor.requery();						
				}
								
				batDialog.dismiss();
			}
		});
		
		batch_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
//				sot.deletebatchbyprodcode(batch_codefield.getText().toString());
				serial_id.setText("");
				if(mActivity instanceof GraAddProduct){
					sl_productcode.setText("");
					sl_name.setText("");
					sl_cartonqty.setText("");
					sl_looseqty.setText("");			
					sl_qty.setText("");
					sl_foc.setText("");
					sl_uom.setText("");
					sl_stock.setText("");
					sl_pcspercarton.setText("");
				}else if(mActivity instanceof StockTakeAddProduct){
					sl_productcode.setText("");
					sl_name.setText("");
					sl_cartonqty.setText("");
					sl_looseqty.setText("");			
					sl_qty.setText("");
					sl_stock.setText("");
					sl_pcspercarton.setText("");
				}else if(mActivity instanceof StockAdjustmentAddproduct){
				     sl_productcode.setText("");
				     sl_name.setText("");
				     sl_cartonqty.setText("");
				     sl_looseqty.setText("");
				     sl_qty.setText("");
				     sl_StockinHand.setText("");
				     sl_Total.setText("0");
				     sl_Tax.setText("0");
				     sl_Nettotal.setText("0");
				     sl_uom.setText("");
//				     sl_stock.setText("");
				     sl_pcspercarton.setText("");
				   
				     sl_Addbtn.setBackgroundResource(R.drawable.button_focus);
				     sl_Addbtn.setId(1);
				     sl_Minusbtn.setBackgroundResource(R.drawable.button_normal);
				    }
				Toast.makeText(mActivity, "Cancelled", Toast.LENGTH_SHORT).show();
				batDialog.dismiss();
			
			}
		});
		
		bat_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (calCarton.matches("0")) {
						
					}else{
					cartonQtyBatch();
					}
					
					bat_looseQty.requestFocus();
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
				ss_Cqty = bat_cartonQty.getText().toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (calCarton.matches("0")) {
					
				}else{
				cartonQtyBatch();
				}
				int length = bat_cartonQty.length();
				if (length == 0) {

					if (calCarton.matches("0")) {
						
					}else{
					
					String lqty = bat_looseQty.getText().toString();
					
					if(lqty.matches("")){
						lqty="0";
				       }
					
					if (!lqty.matches("")) {
						bat_qty.removeTextChangedListener(qtyTW);
						bat_qty.setText(lqty);
						bat_qty.addTextChangedListener(qtyTW);

						if (bat_qty.length() != 0) {
							bat_qty.setSelection(bat_qty.length());
						}
						double lsQty = Double.parseDouble(lqty);

						// productTotal(lsQty);
					}
					}

				}
			}

		};

		bat_looseQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (calCarton.matches("0")) {
						
					}else{
					looseQtyCalcBatch();
					}
					bat_qty.requestFocus();
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
				beforeLooseQty = bat_looseQty.getText().toString();

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (calCarton.matches("0")) {
					
				}else{
				looseQtyCalcBatch();
				}

				int length = bat_looseQty.length();
				if (length == 0) {
					
					if (calCarton.matches("0")) {
						
					}else{
					
					String qty = bat_qty.getText().toString();
					if (!beforeLooseQty.matches("") && !qty.matches("")) {

						int qtyCnvrt = Integer.parseInt(qty);
						int lsCnvrt = Integer.parseInt(beforeLooseQty);

						bat_qty.removeTextChangedListener(qtyTW);
						bat_qty.setText("" + (qtyCnvrt - lsCnvrt));
						bat_qty.addTextChangedListener(qtyTW);

						if (bat_qty.length() != 0) {
							bat_qty.setSelection(bat_qty.length());
						}
						if (calCarton.matches("0")) {
							
						}else{
						looseQtyCalcBatch();
						}
					}
				}
				}
			}

		};

		bat_qty.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					String qty = bat_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						if (calCarton.matches("0")) {
							
						}else{
						clQtyBatch();
						}
						// productTotal(qtyCalc);
					}

					bat_foc.requestFocus();
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

				String qty = bat_qty.getText().toString();
				if (!qty.matches("")) {
					double qtyCalc = Double.parseDouble(qty);
					Log.d("qty", "qq" + qtyCalc);
					if (calCarton.matches("0")) {
						
					}else{
					clQtyBatch();
					}
					// productTotal(qtyCalc);
				}

				int length = bat_qty.length();
				if (length == 0) {
					// productTotal(0);
					if (calCarton.matches("0")) {
						
					}else{
					bat_cartonQty.removeTextChangedListener(cqtyTW);
					bat_cartonQty.setText("");
					bat_cartonQty.addTextChangedListener(cqtyTW);

					bat_looseQty.removeTextChangedListener(lqtyTW);
					bat_looseQty.setText("");
					bat_looseQty.addTextChangedListener(lqtyTW);
					
					}
				}
			}

		};

		bat_foc.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					return true;
				}
				return false;
			}
		});

		bat_foc.addTextChangedListener(new TextWatcher() {

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

		bat_cartonQty.addTextChangedListener(cqtyTW);
		bat_looseQty.addTextChangedListener(lqtyTW);
		bat_qty.addTextChangedListener(qtyTW);

		//myDialog.show();
		batDialog = myDialog.show();

		batDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	final DatePickerDialog.OnDateSetListener mDate = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, monthOfYear);
			mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			strtDate();
		}
	};


	public void storeBatch() {
		
		String bat_id = serial_id.getText().toString();
		String batchNo = batchno.getText().toString();
		String expiryDate = expirydate.getText().toString();
		String batchQty = bat_qty.getText().toString();
		String prodcode = batch_codefield.getText().toString();
		String prodName = batch_namefield.getText().toString();
		String batchCqty = bat_cartonQty.getText().toString();
		String batchLqty = bat_looseQty.getText().toString();
		String batchcartonPerQty = batch_cartonPerQty.getText().toString();
		String batchFocQty = bat_foc.getText().toString();

		if(batchCqty.matches("")){
			batchCqty="0";
		}
		
		if(batchLqty.matches("")){
			batchLqty="0";
		}
		
		if(batchFocQty.matches("")){
			batchFocQty="0";
		}
		
		if(batchQty.matches("")){
			batchQty="0";
		}
		
		Cursor cursor_count = SOTDatabase.getBatchCursorWithSR(prodcode, sale_sl_no+"");
		int count = cursor_count.getCount();
		count = count + 1;
		Log.d("cursor count", "->" + count);
		String batchSlNo = count + "";

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("id", bat_id);
		hm.put("slNo", batchSlNo);
		hm.put("ProductCode", prodcode);
		hm.put("ProductName", prodName);
		hm.put("BatchNo", batchNo);
		hm.put("ExpiryDate", expiryDate);
		hm.put("CQty", batchCqty);
		hm.put("LQty", batchLqty);
		hm.put("Qty", batchQty);
		hm.put("FOCQty", batchFocQty);
		hm.put("PcsPerCarton", batchcartonPerQty);		
		hm.put("HaveBatch", havebatch);
		hm.put("HaveExpiry", haveexpiry);	
		hm.put("Price", sl_price);
		hm.put("SR_Slno", sale_sl_no+"");
		
		Log.d("ADD Product", "->" + hm);
		
		//String bno = SOTDatabase.getBatchNo(batchNo,prodcode);
		
		if(!bat_id.matches("")){
			SOTDatabase.updateBatch(hm);		
		}else{
			SOTDatabase.storeBatch(hm);	
		}
			
		String totQty=SOTDatabase.getBatchQtySR(prodcode, sale_sl_no+"");
		
		edtotalqty.setText(totQty);
		
		batchno.setText("");
		expirydate.setText("");
		bat_cartonQty.setText("");
		bat_looseQty.setText("");
		bat_qty.setText("");
		bat_foc.setText("");
		serial_id.setText("");
		
		cursor.requery();
		if(cursor.getCount()>0){
			batch_finish.setVisibility(View.VISIBLE);
		}else{
			batch_finish.setVisibility(View.INVISIBLE);
		}
		
		String autoBatch=SalesOrderSetGet.getAutoBatchNo();
		if (!havebatch.matches("False")) {
		if(autoBatch.matches("1")){
			batchno.setFocusable(false);
			batchno.setBackgroundResource(R.drawable.labelbg);
			
			Cursor batchcount = SOTDatabase.getBatCursor();
			String NextBatchNo = SalesOrderSetGet.getNextBatchNo();
			int cnt = batchcount.getCount();
			if(cnt>0){
				NextBatchNo = NextBatchNo+"-"+cnt;
			}
			batchno.setText(NextBatchNo);
		}
		}
		bat_qty.requestFocus();
		((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
        .showSoftInput(bat_qty, 2);
		bat_qty.setSelection(bat_qty.length());
		
	}

	public void strtDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		expirydate.setText(sdf.format(mCalendar.getTime()));
	}

	private class BatchListAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public BatchListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.batch_list_item, cursor);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView batch_sl_id = (TextView) view
					.findViewById(R.id.batch_sl_id);
			batch_sl_id.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID)));

			TextView batch_sl_no = (TextView) view
					.findViewById(R.id.batch_sl_no);
			batch_sl_no.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO)));

			TextView batch_prodcode = (TextView) view
					.findViewById(R.id.batch_prodcode);
			batch_prodcode.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));

			TextView batch_prodname = (TextView) view
					.findViewById(R.id.batch_prodname);
			batch_prodname.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));
			
			TextView batch_no = (TextView) view.findViewById(R.id.batch_no);
			batch_no.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO)));
			
			if (havebatch.matches("False")) {
			   // batchno_text.setVisibility(View.GONE);
			    batch_no.setVisibility(View.GONE);
			   }
			
			TextView batch_expiryDate = (TextView) view
					.findViewById(R.id.batch_expiryDate);
			batch_expiryDate.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE)));
			
			if (haveexpiry.matches("False")) {
			   // batchexpiry_text.setVisibility(View.GONE);
			    batch_expiryDate.setVisibility(View.GONE);
			   }

			TextView batch_cQty = (TextView) view.findViewById(R.id.batch_cQty);
			batch_cQty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

			TextView batch_lQty = (TextView) view.findViewById(R.id.batch_lQty);
			batch_lQty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

			TextView batch_Qty = (TextView) view.findViewById(R.id.batch_Qty);
			batch_Qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

			TextView batch_fQty = (TextView) view.findViewById(R.id.batch_fQty);
			batch_fQty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_FOC)));

			TextView batch_pcsPerCarton = (TextView) view
					.findViewById(R.id.batch_pcsPerCarton);
			batch_pcsPerCarton.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));
			
			String prodcode;
			prodcode = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
			
			String totQty = SOTDatabase.getBatchQtySR(prodcode, sale_sl_no+"");
			edtotalqty.setText(totQty);

		}
	}

	public void clQtyBatch() {
		String qty = bat_qty.getText().toString();
		String crtnperQty = btCartonPerQty;
		int q = 0, r = 0;

		if (crtnperQty.matches("0") || crtnperQty.matches("null")
				|| crtnperQty.matches("0.00")) {
			crtnperQty = "1";
		}

		if (!crtnperQty.matches("")) {
			if (!qty.matches("")) {
				try {
					int qty_nt = Integer.parseInt(qty);
					int pcs_nt = Integer.parseInt(crtnperQty);

					Log.d("qty_nt", "" + qty_nt);
					Log.d("pcs_nt", "" + pcs_nt);

					q = qty_nt / pcs_nt;
					r = qty_nt % pcs_nt;

					Log.d("cqty", "" + q);
					Log.d("lqty", "" + r);

					bat_cartonQty.setText("" + q);
					bat_looseQty.setText("" + r);

					Log.d("bat_cartonQty", "qq" + q);

				} catch (ArithmeticException e) {
					System.out.println("Err: Divided by Zero");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void cartonQtyBatch() {
		String crtnQty = bat_cartonQty.getText().toString();

		if (!btCartonPerQty.matches("") && !crtnQty.matches("")) {
			int cartonQtyCalc = Integer.parseInt(crtnQty);
			int cartonPerQtyCalc = Integer.parseInt(btCartonPerQty);
			int qty = 0;
			String lsQty = bat_looseQty.getText().toString();
			if (!lsQty.matches("")) {
				int lsQtyCnvrt = Integer.parseInt(lsQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

			} else {
				qty = cartonQtyCalc * cartonPerQtyCalc;
			}
			bat_qty.removeTextChangedListener(qtyTW);
			bat_qty.setText("" + qty);
			bat_qty.addTextChangedListener(qtyTW);

			if (bat_qty.length() != 0) {
				bat_qty.setSelection(bat_qty.length());
			}

		}
	}

	public void looseQtyCalcBatch() {
		String crtnQty = bat_cartonQty.getText().toString();
		String lsQty = bat_looseQty.getText().toString();

		if (lsQty.matches("")) {
			lsQty = "0";
		}

		if (!btCartonPerQty.matches("") && !crtnQty.matches("")
				&& !lsQty.matches("")) {
			int cartonQtyCalc = Integer.parseInt(crtnQty);
			int cartonPerQtyCalc = Integer.parseInt(btCartonPerQty);
			int looseQtyCalc = Integer.parseInt(lsQty);
			int qty;
			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

			bat_qty.removeTextChangedListener(qtyTW);
			bat_qty.setText("" + qty);
			bat_qty.addTextChangedListener(qtyTW);

			if (bat_qty.length() != 0) {
				bat_qty.setSelection(bat_qty.length());
			}

		}

		if (!lsQty.matches("")) {
			int looseQtyCalc = Integer.parseInt(lsQty);
			int qty;
			if (!crtnQty.matches("") && !btCartonPerQty.matches("")) {
				int cartonQtyCalc = Integer.parseInt(crtnQty);
				int cartonPerQtyCalc = Integer.parseInt(btCartonPerQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			} else {
				qty = looseQtyCalc;
			}
			bat_qty.removeTextChangedListener(qtyTW);
			bat_qty.setText("" + qty);
			bat_qty.addTextChangedListener(qtyTW);

			if (bat_qty.length() != 0) {
				bat_qty.setSelection(bat_qty.length());
			}

		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.d("onCreateContextMenu", "onCreateContextMenu");
		if (v.getId() == R.id.batch_list) {

			menu.setHeaderTitle("Batch");
			String[] menuItems = Cmd;

			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
			menu.getItem(0)
					.setOnMenuItemClickListener(new onContextMenuClick());
			menu.getItem(1)
			.setOnMenuItemClickListener(new onContextMenuClick());
		}
	}

	private class onContextMenuClick implements
			MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			Log.d("onCreateContextMenu", "onContextItemSelected");
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			if (item.getTitle() == "Edit") {
				
				serial_id.setText(((TextView) info.targetView
						.findViewById(R.id.batch_sl_id)).getText().toString());
				batchno.setText(((TextView) info.targetView
						.findViewById(R.id.batch_no)).getText().toString());
				expirydate.setText(((TextView) info.targetView
						.findViewById(R.id.batch_expiryDate)).getText()
						.toString());
				bat_cartonQty.setText(((TextView) info.targetView
						.findViewById(R.id.batch_cQty)).getText().toString());
				bat_looseQty.setText(((TextView) info.targetView
						.findViewById(R.id.batch_lQty)).getText().toString());
				bat_qty.setText(((TextView) info.targetView
						.findViewById(R.id.batch_Qty)).getText().toString());
				bat_foc.setText(((TextView) info.targetView
						.findViewById(R.id.batch_fQty)).getText().toString());
				
				bat_qty.requestFocus();
				bat_qty.setSelection(bat_qty.length());
				((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(bat_qty, 2);
				
				

			} else if (item.getTitle() == "Delete") {
				String id = ((TextView) info.targetView.findViewById(R.id.batch_sl_id)).getText().toString();
				Log.d("id", id);
				SOTDatabase.deleteBatch(id);
				cursor.requery();
				ArrayList<String> snoCount = new ArrayList<String>();
				snoCount = SOTDatabase.batchSnoCountID();
				Log.d("snocount", "" + snoCount);
				for (int i = 0; i < snoCount.size(); i++) {
					int sno = 1 + i;
					HashMap<String, String> queryValues = new HashMap<String, String>();
					queryValues.put("_id", "" + snoCount.get(i));
					queryValues.put("batch_sno", "" + sno);
					SOTDatabase.updateBatchSNO(queryValues);
				}
				cursor.requery();
				if(cursor.getCount()>0){
					batch_finish.setVisibility(View.VISIBLE);
				}else{
					batch_finish.setVisibility(View.INVISIBLE);
				}
			} else {
				return false;
			}
			return true;

		}
	}

	public void clQty(String productid,String quantity, int foc, String pieceperqty,String prodcode) {

			int cartonQty = 0;
			double looseQty = 0;
			products_arr.clear();
			String cprice,productprice,taxtype,taxvalue,itemdiscount;
			
			products_arr = sqldb.getProducts(productid);

//			quantity = products_arr.get(0);
			Log.d("quantity", "" + quantity);
			
			cprice = products_arr.get(1);
			productprice = products_arr.get(2);
			taxtype = products_arr.get(3);
			taxvalue = products_arr.get(4);
			pieceperqty = products_arr.get(5);
			itemdiscount = products_arr.get(6);

			Log.d("cprice", "" + cprice);
			Log.d("productprice", "" + productprice);
			Log.d("taxtype", "" + taxtype);
			Log.d("taxvalue", "" + taxvalue);
			Log.d("itemdiscount", "" + itemdiscount);

			try {
				double qty_nt = Double.parseDouble(quantity);
				int pcs_nt = Integer.parseInt(pieceperqty);

				Log.d("qty_nt", "" + qty_nt);
				Log.d("pcs_nt", "" + pcs_nt);

				 cartonQty = (int) (qty_nt / pcs_nt);
				 looseQty = qty_nt % pcs_nt;
				 
				Log.d("cqty", "" + cartonQty);
				Log.d("lqty", "" + looseQty);

			} catch (ArithmeticException e) {
				System.out.println("Err: Divided by Zero");
			} catch (Exception e) {
				e.printStackTrace();
			}

			double taxAmount = 0.0, netTotal = 0.0;
			double taxAmount1 = 0.0, netTotal1 = 0.0;
			double itmDisc = 0, tt = 0, qty = Double.valueOf(quantity);
			String sbTtl = "", Prodtotal = "", prodTax = "0", ProdNetTotal = "";
			double subTotal = 0.0;
			try {
				if (!productprice.matches("")) {

					double slPriceCalc = Double.parseDouble(productprice);

					if (!itemdiscount.matches("")) {
						tt = (qty * slPriceCalc);
					} else {
						tt = qty * slPriceCalc;
					}

					Prodtotal = twoDecimalPoint(tt);

					if (!itemdiscount.matches("")) {
						itmDisc = Double.parseDouble(itemdiscount);
						subTotal = tt - itmDisc;
					} else {
						subTotal = tt;
					}

					sbTtl = twoDecimalPoint(subTotal);

					if (!taxtype.matches("") && !taxvalue.matches("")) {

						double taxValueCalc = Double.parseDouble(taxvalue);

						if (taxtype.matches("E")) {

							if (!itemdiscount.matches("")) {
								taxAmount1 = (subTotal * taxValueCalc) / 100;
								prodTax = fourDecimalPoint(taxAmount1);
								// sl_tax.setText("" + prodTax);

								netTotal1 = subTotal + taxAmount1;
								ProdNetTotal = twoDecimalPoint(netTotal1);
								// sl_netTotal.setText("" + ProdNetTotal);
							} else {

								taxAmount = (tt * taxValueCalc) / 100;
								prodTax = fourDecimalPoint(taxAmount);
								// sl_tax.setText("" + prodTax);

								netTotal = tt + taxAmount;
								ProdNetTotal = twoDecimalPoint(netTotal);
								// sl_netTotal.setText("" + ProdNetTotal);
							}

						} else if (taxtype.matches("I")) {
							if (!itemdiscount.matches("")) {
								taxAmount1 = (subTotal * taxValueCalc)
										/ (100 + taxValueCalc);
								prodTax = fourDecimalPoint(taxAmount1);
								// sl_tax.setText("" + prodTax);

//								netTotal1 = subTotal + taxAmount1;
								netTotal1 = subTotal;
								ProdNetTotal = twoDecimalPoint(netTotal1);
								// sl_netTotal.setText("" + ProdNetTotal);
							} else {
								taxAmount = (tt * taxValueCalc)
										/ (100 + taxValueCalc);
								prodTax = fourDecimalPoint(taxAmount);
								// sl_tax.setText("" + prodTax);

//								netTotal = tt + taxAmount;
								netTotal = tt;
								ProdNetTotal = twoDecimalPoint(netTotal);
								// sl_netTotal.setText("" + ProdNetTotal);
							}

						} else if (taxtype.matches("Z")) {

							// sl_tax.setText("0.0");
							if (!itemdiscount.matches("")) {
//								netTotal1 = subTotal + taxAmount;
								netTotal1 = subTotal;
								ProdNetTotal = twoDecimalPoint(netTotal1);
								// sl_netTotal.setText("" + ProdNetTotal);
							} else {
//								netTotal = tt + taxAmount;
								netTotal = tt;
								ProdNetTotal = twoDecimalPoint(netTotal);
								// sl_netTotal.setText("" + ProdNetTotal);
							}

						} else {
							// sl_tax.setText("0.0");
							taxvalue = "0";
							netTotal = subTotal;
							ProdNetTotal = twoDecimalPoint(netTotal);
							// sl_netTotal.setText("" + ProdNetTotal);
						}

					} else if (taxvalue.matches("")) {
						taxvalue = "0";
						netTotal = subTotal;
						ProdNetTotal = twoDecimalPoint(netTotal);
					} else {
						taxvalue = "0";
						netTotal = subTotal;
						ProdNetTotal = twoDecimalPoint(netTotal);
					}

				}
			} catch (Exception e) {

			}

			
		int totCqty = SOTDatabase.getTotalBatchCQtySR(prodcode, sale_sl_no+"");
			int totLqty = SOTDatabase.getTotalBatchLQtySR(prodcode, sale_sl_no+"");
			
			if(calCarton.matches("0")){
				cartonQty = totCqty;
				looseQty = totLqty;
			}
			
			int iLqty =0;
			
			iLqty = (int) looseQty;
			
			
			Log.d("Result", "c " + cartonQty + "l " + looseQty + "tt " + Prodtotal
					+ "sbTtl " + sbTtl + "prodTax " + prodTax + "ProdNetTotal "
					+ ProdNetTotal);
			
			sqldb.updateProductForBatch(productid, cartonQty, looseQty, productprice,
					itemdiscount, Prodtotal, sbTtl, prodTax, ProdNetTotal,quantity, ""+foc);
			
			
			
			sqldb.updateBilldiscProductValues(productid, sbTtl);
			
			sl_cursor.requery();
			cursor.requery();
		}
	
	
	public void summaryStore(){
		
		String prodcode = batch_codefield.getText().toString();
		String totQty = SOTDatabase.getTotaBatchQtySR(prodcode, sale_sl_no+"");
		
		int totCqty = SOTDatabase.getTotalBatchCQtySR(prodcode, sale_sl_no+"");
		int totLqty = SOTDatabase.getTotalBatchLQtySR(prodcode, sale_sl_no+"");
		
//		  Cursor cursor = SOTDatabase.getCursor();

//		     if (cursor != null && cursor.getCount() > 0) {
//		      sl_no = cursor.getCount();
//		      sl_no++;
//		     }
		     
		     int cartonPerQty=0 ;
		     double price = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0;
		     String subtotal = "", nettotal = "";
		     HashMap<String, String> hm = new HashMap<String, String>();
		     
		     
		     String uomStr = sl_uom.getText().toString();
		     String totalStr = sl_Total.getText().toString();
		     String taxStr = sl_Tax.getText().toString();
		     String netTotalStr = sl_Nettotal.getText().toString();
		     String qtyinhand = sl_StockinHand.getText().toString();
		     
		     
		     if (!btCartonPerQty.matches("")) {
		      cartonPerQty = Integer.parseInt(btCartonPerQty);
		     }
		     
		     String p_Sno = SOTDatabase.getSalesId(sale_sl_no+"");
		     if (p_Sno != null && !p_Sno.isEmpty()) {
		      int i_sssno = Integer.parseInt(p_Sno);
		      
				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("ProductCode", batCode);
				queryValues.put("ProductName", btName);
				queryValues.put("CQty", totCqty+"");
				queryValues.put("LQty", totLqty+"");
				queryValues.put("Qty", totQty+"");
				queryValues.put("StockinHand", qtyinhand);
				queryValues.put("Price", price+"");
				queryValues.put("Dicount", "0.0" );
				queryValues.put("Uom", uomStr);
				queryValues.put("PcsPerCarton", cartonPerQty+"");
				queryValues.put("Total", total+"" );
				queryValues.put("Tax", tax+"");
				queryValues.put("NetTotal", nettotal);
				queryValues.put("SubTotal", subtotal);
				queryValues.put("CartonPrice", "0.00");
				queryValues.put("AddDetect", "1");
				queryValues.put("SOSlno", i_sssno+"");

		      SOTDatabase.updateStockAdjustmentProduct(queryValues);
		      
		      //SOTDatabase.updateproductbatch(batCode,havebatch,haveexpiry);

		     } else {
		    	 
		    	 HashMap<String, String> queryValues = new HashMap<String, String>();
					queryValues.put("slNo", sale_sl_no+"");
					queryValues.put("ProductCode", batCode);
					queryValues.put("ProductName", btName);
					queryValues.put("CQty", totCqty+"");
					queryValues.put("LQty", totLqty+"");
					queryValues.put("Qty", totQty+"");
					queryValues.put("StockinHand", qtyinhand);
					queryValues.put("Price", price+"");
					queryValues.put("Dicount", "0.0" );
					queryValues.put("Uom", uomStr);
					queryValues.put("PcsPerCarton", cartonPerQty+"");
					queryValues.put("Total", total+"" );
					queryValues.put("Tax", tax+"");
					queryValues.put("NetTotal", nettotal);
					queryValues.put("TaxType", "z");
					queryValues.put("TaxPerc", "0");
					queryValues.put("RetailPrice", "");
					queryValues.put("SubTotal", subtotal);
					queryValues.put("CartonPrice", "0.00");
					queryValues.put("AddDetect", "1");
					queryValues.put("MinimumSellingPrice", "0");
					queryValues.put("ItemRemarks", "");
					queryValues.put("SOSlno", "");

		      SOTDatabase.storeStockAdjustmentProduct(queryValues);
		      SOTDatabase.updateproductbatch(batCode,havebatch,haveexpiry);
		     }
		     
		     sl_productcode.setText("");
		     sl_name.setText("");
		     sl_cartonqty.setText("");
		     sl_looseqty.setText("");
		     sl_qty.setText("");
		     sl_StockinHand.setText("");
		     sl_Total.setText("0");
		     sl_Tax.setText("0");
		     sl_Nettotal.setText("0");
		     sl_uom.setText("");
//		     sl_stock.setText("");
		     sl_pcspercarton.setText("");
		     
		     sl_Addbtn.setBackgroundResource(R.drawable.button_focus);
		     sl_Addbtn.setId(1);
		     sl_Minusbtn.setBackgroundResource(R.drawable.button_normal);
		   
		     
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
	
}
