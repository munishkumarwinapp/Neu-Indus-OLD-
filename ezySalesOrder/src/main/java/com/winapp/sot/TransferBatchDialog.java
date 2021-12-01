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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.util.CustomCalendar;

public class TransferBatchDialog extends DialogFragment {

	PopupWindow popupWindow1;

	AlertDialog.Builder myDialog;
	BatchListAdapter batchListAdapter;
	Calendar mCalendar;

	String code = "";
	EditText expirydate, edtotalqty;
	EditText batch_codefield, batch_namefield, batchno, bat_cartonQty,
			bat_looseQty, bat_qty, batch_cartonPerQty, sl_productcode, sl_name,
			sl_cartonqty, sl_looseqty, sl_qty, sl_uom, sl_stock,
			sl_pcspercarton, slPrice, sm_cartonqty, sm_looseqty, sm_qty; // bat_foc,
																			// sl_foc,
	ImageButton batch_add, batch_finish, batch_cancel;
	ListView batch_list;

	TextWatcher cqtyTW, lqtyTW, qtyTW;
	String beforeLooseQty, beforeFoc;
	String ss_Cqty = "",calCarton="";
	Activity mActivity;
	String batCode, btName, btCartonPerQty, havebatch, haveexpiry, Id, sl_price ,NoOfCarton, sale_sl_no="";
	String[] Cmd = { "Delete" };
	Cursor cursor, sl_cursor;
	AlertDialog batDialog;
	private SOTDatabase sqldb;
	ArrayList<String> products_arr = new ArrayList<String>();
	TextView serial_id;
	LinearLayout batchno_text, batchexpiry_text;
	String newDate="";
	int sl_no = 1;
	CustomCalendar customCalendar;
	EditText sl_StockinHand, sl_Total, sl_Tax, sl_Nettotal ;
	Button sl_Addbtn, sl_Minusbtn;
	boolean isStockRequestBatchDetail = false;




	/** StockRequestBatchDetail **/
	public void initiateBatchPopupWindow(Activity context, String id, String sno,
			String haveBatch, String haveExpiry, String code, String name,
			String slCartonPerQty, Cursor crsr, String price,
			HashMap<String, EditText> hm, ArrayList<HashMap<String, String>> stockRequestBatchDetailArrHm, boolean mIsStockRequestBatchDetail) {		
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
		this.sm_cartonqty = hm.get("sm_cqty");
		this.sm_looseqty = hm.get("sm_lqty");
		this.sm_qty = hm.get("sm_qty");
		this.isStockRequestBatchDetail = mIsStockRequestBatchDetail;
		this.sale_sl_no = sno;
		Log.d("ID", "" + Id);		
		
		Log.d("stockRequestBatchDetail", "" + stockRequestBatchDetailArrHm.size());
		
		if (stockRequestBatchDetailArrHm.size() > 0) {

			for (int i = 0; i < stockRequestBatchDetailArrHm.size(); i++) {

				// String StockRequestNo =
				// stockRequestBatchDetailArrHm.get(i).get("StockRequestNo");
				String ProductCode = stockRequestBatchDetailArrHm.get(i).get(
						"ProductCode");
				String BatchNo = stockRequestBatchDetailArrHm.get(i).get(
						"BatchNo");
				 String ExpiryDate = stockRequestBatchDetailArrHm.get(i).get("ExpiryDate");
				// String MfgDate =
				// stockRequestBatchDetailArrHm.get(i).get("MfgDate");
				 if(ProductCode.matches(batCode)){
				String CQty = stockRequestBatchDetailArrHm.get(i).get("CQty");
				String LQty = stockRequestBatchDetailArrHm.get(i).get("LQty");
				String Qty = stockRequestBatchDetailArrHm.get(i).get("Qty");
				String SR_Slno = stockRequestBatchDetailArrHm.get(i).get("SR_Slno");
				String Remarks = stockRequestBatchDetailArrHm.get(i).get("Remarks");
				
				String qty = SOTDatabase.getBatchQtySR(BatchNo,ProductCode,ExpiryDate, SR_Slno);
				String cQty = SOTDatabase.getBatchcQtySR(BatchNo,ProductCode,ExpiryDate, SR_Slno);
				String lQty = SOTDatabase.getBatchlQtySR(BatchNo,ProductCode,ExpiryDate, SR_Slno); 
				
				
				 String stockSno = SOTDatabase.getStockSno(Id);
			
				Log.d("ID", "" + Id);  
			     Log.d("slNo", "" + SR_Slno);
			     Log.d("stockSno", "" + stockSno); 
			     
			     if(SR_Slno.matches(stockSno)){
				if ((qty.matches("0")) && (cQty.matches("0"))
						&& (lQty.matches("0"))) {					
					SOTDatabase.updateStockReqBatchDetailProduct(ProductCode,
							BatchNo,ExpiryDate, CQty, LQty, Qty, SR_Slno, Remarks);					
				}	
			     }
			}
			}
		}
			

		batch();
	}

	/** TransferSummary & StockAdjustmentSummary **/
	public void initiateBatchPopupWindow(Activity context, String id, String srno,
			String haveBatch, String haveExpiry, String code, String name,
			String slCartonPerQty, Cursor crsr, String price,
			HashMap<String, EditText> hm) {

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
		this.sm_cartonqty = hm.get("sm_cqty");
		this.sm_looseqty = hm.get("sm_lqty");
		this.sm_qty = hm.get("sm_qty");
		this.sale_sl_no = srno;
		Log.d("ID", "" + Id);
		batch();
	}

	//  TransferAddProduct
	public void initiateBatchPopupWindow(Activity context, String haveBatch,
			String haveExpiry, String code, String name, String slCartonPerQty,
			String price, HashMap<String, EditText> hm,
			ArrayList<HashMap<String, String>> alBatchStock) {

		this.mActivity = context;

		this.mCalendar = Calendar.getInstance();
		this.batCode = code;
		this.btName = name;
		this.btCartonPerQty = slCartonPerQty;
		this.havebatch = haveBatch;
		this.haveexpiry = haveExpiry;
		this.sl_price = price;
		this.sl_productcode = hm.get("Productcode");
		this.sl_name = hm.get("Productname");
		this.sl_cartonqty = hm.get("Cartonqty");
		this.sl_looseqty = hm.get("Looseqty");
		this.sl_qty = hm.get("Qty");
		this.sl_uom = hm.get("Uom");
		this.sl_stock = hm.get("Stock");
		this.sl_pcspercarton = hm.get("Cartonperqty");
		
		Cursor salcursor = SOTDatabase.getCursor();
		int cnt = salcursor.getCount();
			cnt = cnt+1;
			Log.d("sale count", ""+cnt);
			this.sale_sl_no = cnt+"";

		Log.d("alBatchStock", "batch dialog" + alBatchStock.size());
	
		//String batNo = alBatchStock.get(0).get("BatchNo");
		String dbPcode = SOTDatabase.getBatchNoSR(batCode, sale_sl_no);

		if (dbPcode.matches("")) {
	
		if (alBatchStock.size() > 0) {

			for (int i = 0; i < alBatchStock.size(); i++) {
				int q = 0, r = 0;
				String cqty = "", lqty = "";

				String qty = alBatchStock.get(i).get("Qty");
				String NoOfCarton =  alBatchStock.get(i).get("NoOfCarton");
				String cartonPerQty = alBatchStock.get(i).get("PcsPerCarton");
				double dQty = Double.parseDouble(qty);
				Log.d("qty", "" + qty);
				Log.d("cartonPerQty", "" + cartonPerQty);
				if (!cartonPerQty.matches("")) {
					if (!qty.matches("")) {
						try {
							int qty_nt = (int) dQty;
							int pcs_nt = Integer.parseInt(cartonPerQty);

							Log.d("qty_nt", "" + qty_nt);
							Log.d("pcs_nt", "" + pcs_nt);

							q = qty_nt / pcs_nt;
							r = qty_nt % pcs_nt;

							Log.d("cqty", "" + q);
							Log.d("lqty", "" + r);

							cqty = "" + q;
							lqty = "" + r;
							// bat_cartonQty.setText("" + q);
							// bat_looseQty.setText("" + r);

						} catch (ArithmeticException e) {
							System.out.println("Err: Divided by Zero");

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				calCarton = LogOutSetGet.getCalcCarton();
				if(calCarton.matches("0")){
					cqty = NoOfCarton;
					lqty = "0";
					
				}
								
				double cartonqty=0,qty_nt=0;
				
				if(!qty.matches("")){
					qty_nt =  Double.parseDouble(qty);
				}
				
				if(calCarton.matches("0")){
					
					if(!cqty.matches("")){
						cartonqty = Double.parseDouble(cqty);
						
						if(cartonqty>0 || qty_nt>0){
							int batchSlNo = i + 1;

							HashMap<String, String> hmValue = new HashMap<String, String>();
							// hmValue.put("id", bat_id);
							hmValue.put("slNo", batchSlNo + "");
							hmValue.put("ProductCode", batCode);
							hmValue.put("ProductName", btName);
							hmValue.put("BatchNo", alBatchStock.get(i).get("BatchNo"));
							hmValue.put("ExpiryDate", alBatchStock.get(i).get("ExpiryDate"));
							hmValue.put("AvailCQty", cqty);
							hmValue.put("AvailLQty", lqty);
							hmValue.put("AvailQty", qty);
							hmValue.put("CQty", "0");
							hmValue.put("LQty", "0");
							hmValue.put("Qty", "0");
							hmValue.put("PcsPerCarton", btCartonPerQty);
							hmValue.put("HaveBatch", havebatch);
							hmValue.put("HaveExpiry", haveexpiry);
							hmValue.put("SR_Slno", sale_sl_no);
							SOTDatabase.storeBatch(hmValue);
								
							Log.d("ADD Product", "->" + hmValue);
						}
					}
		
				}else{
					if(qty_nt>0){
						int batchSlNo = i + 1;

						HashMap<String, String> hmValue = new HashMap<String, String>();
						// hmValue.put("id", bat_id);
						hmValue.put("slNo", batchSlNo + "");
						hmValue.put("ProductCode", batCode);
						hmValue.put("ProductName", btName);
						hmValue.put("BatchNo", alBatchStock.get(i).get("BatchNo"));
						hmValue.put("ExpiryDate", alBatchStock.get(i).get("ExpiryDate"));
						hmValue.put("AvailCQty", cqty);
						hmValue.put("AvailLQty", lqty);
						hmValue.put("AvailQty", qty);
						hmValue.put("CQty", "0");
						hmValue.put("LQty", "0");
						hmValue.put("Qty", "0");
						hmValue.put("PcsPerCarton", btCartonPerQty);
						hmValue.put("HaveBatch", havebatch);
						hmValue.put("HaveExpiry", haveexpiry);
						hmValue.put("SR_Slno", sale_sl_no);
						SOTDatabase.storeBatch(hmValue);
							
						Log.d("ADD Product", "->" + hmValue);
					}
				}
				
			}
		}
		}

		batch();
	}
	
	// StockAdjustmentAddproduct
	
	public void initiateBatchPopupWindow(Activity context, String haveBatch,
			String haveExpiry, String code, String name, String slCartonPerQty,
			HashMap<String, Object> hm,
			ArrayList<HashMap<String, String>> alBatchStock) {

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
		this.sl_stock = (EditText) hm.get("Stock");
		this.sl_pcspercarton = (EditText) hm.get("Cartonperqty");
		this.sl_Addbtn = (Button) hm.get("AddStock");
		this.sl_Minusbtn = (Button) hm.get("MinusStock");
		
		Log.d("alBatchStock", "" + alBatchStock.size());
		
		Cursor salcursor = SOTDatabase.getCursor();
		int cnt = salcursor.getCount();
			cnt = cnt+1;
			Log.d("sale count", ""+cnt);
			this.sale_sl_no = cnt+"";
		
		//String batNo = alBatchStock.get(0).get("BatchNo");
		String dbPcode = SOTDatabase.getBatchNoSR(batCode, sale_sl_no);

		if (dbPcode.matches("")) {
	
		if (alBatchStock.size() > 0) {

			for (int i = 0; i < alBatchStock.size(); i++) {
				int q = 0, r = 0;
				String cqty = "", lqty = "";

				String qty = alBatchStock.get(i).get("Qty");
				String NoOfCarton =  alBatchStock.get(i).get("NoOfCarton");
				String cartonPerQty = alBatchStock.get(i).get("PcsPerCarton");
				double dQty = Double.parseDouble(qty);
				Log.d("qty", "" + qty);
				Log.d("cartonPerQty", "" + cartonPerQty);
				if (!cartonPerQty.matches("")) {
					if (!qty.matches("")) {
						try {
							int qty_nt = (int) dQty;
							int pcs_nt = Integer.parseInt(cartonPerQty);

							Log.d("qty_nt", "" + qty_nt);
							Log.d("pcs_nt", "" + pcs_nt);

							q = qty_nt / pcs_nt;
							r = qty_nt % pcs_nt;

							Log.d("cqty", "" + q);
							Log.d("lqty", "" + r);

							cqty = "" + q;
							lqty = "" + r;
							// bat_cartonQty.setText("" + q);
							// bat_looseQty.setText("" + r);

						} catch (ArithmeticException e) {
							System.out.println("Err: Divided by Zero");

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				calCarton = LogOutSetGet.getCalcCarton();
				if(calCarton.matches("0")){
					cqty = NoOfCarton;
					lqty = "0";
					
				}
						
				
				double cartonqty=0,qty_nt=0;
				
				if(!qty.matches("")){
					qty_nt =  Double.parseDouble(qty);
				}
				
				if(calCarton.matches("0")){
					
					if(!cqty.matches("")){
						cartonqty = Double.parseDouble(cqty);
						
						if(cartonqty>0 || qty_nt>0){
							int batchSlNo = i + 1;

							HashMap<String, String> hmValue = new HashMap<String, String>();
							// hmValue.put("id", bat_id);
							hmValue.put("slNo", batchSlNo + "");
							hmValue.put("ProductCode", batCode);
							hmValue.put("ProductName", btName);
							hmValue.put("BatchNo", alBatchStock.get(i).get("BatchNo"));
							hmValue.put("ExpiryDate", alBatchStock.get(i).get("ExpiryDate"));
							hmValue.put("AvailCQty", cqty);
							hmValue.put("AvailLQty", lqty);
							hmValue.put("AvailQty", qty);
							hmValue.put("CQty", "0");
							hmValue.put("LQty", "0");
							hmValue.put("Qty", "0");
							hmValue.put("PcsPerCarton", btCartonPerQty);
							hmValue.put("HaveBatch", havebatch);
							hmValue.put("HaveExpiry", haveexpiry);
							hmValue.put("SR_Slno", sale_sl_no);
							SOTDatabase.storeBatch(hmValue);
								
							Log.d("ADD Product", "->" + hmValue);
						}
					}
		
				}else{
					if(qty_nt>0){
						int batchSlNo = i + 1;

						HashMap<String, String> hmValue = new HashMap<String, String>();
						// hmValue.put("id", bat_id);
						hmValue.put("slNo", batchSlNo + "");
						hmValue.put("ProductCode", batCode);
						hmValue.put("ProductName", btName);
						hmValue.put("BatchNo", alBatchStock.get(i).get("BatchNo"));
						hmValue.put("ExpiryDate", alBatchStock.get(i).get("ExpiryDate"));
						hmValue.put("AvailCQty", cqty);
						hmValue.put("AvailLQty", lqty);
						hmValue.put("AvailQty", qty);
						hmValue.put("CQty", "0");
						hmValue.put("LQty", "0");
						hmValue.put("Qty", "0");
						hmValue.put("PcsPerCarton", btCartonPerQty);
						hmValue.put("HaveBatch", havebatch);
						hmValue.put("HaveExpiry", haveexpiry);
						hmValue.put("SR_Slno", sale_sl_no);

						SOTDatabase.storeBatch(hmValue);
							
						Log.d("ADD Product", "->" + hmValue);
					}
				}
				
				
			}
		}
		}

		batch();
	}
	
	//End

	public void batch() {

		myDialog = new AlertDialog.Builder(mActivity);

		LayoutInflater li = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.batch_transfer, null, false);

		myDialog.setView(v);
		myDialog.setCancelable(false);
		sqldb = new SOTDatabase(mActivity);

		batchno_text = (LinearLayout) v.findViewById(R.id.txt1);
		batchexpiry_text = (LinearLayout) v.findViewById(R.id.txt2);
		serial_id = (TextView) v.findViewById(R.id.serial_id);

		batch_codefield = (EditText) v.findViewById(R.id.batch_codefield);
		batch_namefield = (EditText) v.findViewById(R.id.batch_namefield);
		batchno = (EditText) v.findViewById(R.id.batchno);
		expirydate = (EditText) v.findViewById(R.id.expirydate);
		bat_cartonQty = (EditText) v.findViewById(R.id.batch_cartonQty);
		bat_looseQty = (EditText) v.findViewById(R.id.batch_looseQty);
		bat_qty = (EditText) v.findViewById(R.id.batch_qty);
		batch_cartonPerQty = (EditText) v.findViewById(R.id.batch_cartonPerQty);
		batch_add = (ImageButton) v.findViewById(R.id.batch_add);
		batch_list = (ListView) v.findViewById(R.id.batch_list);

		edtotalqty = (EditText) v.findViewById(R.id.edtotalqty);
		calCarton = LogOutSetGet.getCalcCarton();
		batch_finish = (ImageButton) v.findViewById(R.id.batch_finish);
		batch_cancel = (ImageButton) v.findViewById(R.id.batch_cancel);

		batchno.setBackgroundResource(R.drawable.labelbg);
		batchno.setFocusable(false);
		
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
//				bat_cartonQty.requestFocus();
			}else{
			
			bat_cartonQty.setFocusable(false);
			bat_cartonQty.setBackgroundResource(R.drawable.labelbg);

			bat_looseQty.setFocusable(false);
			bat_looseQty.setBackgroundResource(R.drawable.labelbg);

//			bat_qty.requestFocus();
//			InputMethodManager imm = (InputMethodManager) mActivity
//					.getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(bat_qty, InputMethodManager.SHOW_IMPLICIT);
			}

		} else {
			bat_cartonQty.setFocusableInTouchMode(true);
			bat_cartonQty.setBackgroundResource(R.drawable.edittext_bg);

			bat_looseQty.setFocusableInTouchMode(true);
			bat_looseQty.setBackgroundResource(R.drawable.edittext_bg);

//			bat_cartonQty.requestFocus();
		}

		serial_id.setText("");
		batch_codefield.setText(batCode);
		batch_namefield.setText(btName);
		batch_cartonPerQty.setText(btCartonPerQty);
		
		
		 cursor = SOTDatabase.getBatchCursorWithSR(batCode, sale_sl_no);
		
		//delete		

//		if(isStockRequestBatchDetail){
//			
//			String slno = SOTDatabase.getProductSno(Id);
//			   cursor = SOTDatabase.getBatchCursorWithSR(batCode, slno);
//			   Log.d("cursor", "->" + cursor.toString());
//			  }else{
//			   cursor = SOTDatabase.getBatchCursor(batCode);
//			   Log.d("cursor", "->" + cursor.toString());
//			  }
		
		
//		cursor = SOTDatabase.getBatchCursor(batCode);
//		Log.d("cursor", "->" + cursor.toString());
		batchListAdapter = new BatchListAdapter(mActivity, cursor);
		batch_list.setAdapter(batchListAdapter);
		registerForContextMenu(batch_list);

		batch_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				if(isStockRequestBatchDetail){
				    bat_cartonQty.removeTextChangedListener(cqtyTW);   
				    bat_qty.removeTextChangedListener(qtyTW); 
				    bat_looseQty.removeTextChangedListener(lqtyTW);
				    }
				serial_id.setText(((TextView) v.findViewById(R.id.batch_sl_id))
						.getText().toString());
				batchno.setText(((TextView) v.findViewById(R.id.batch_no))
						.getText().toString());
				expirydate.setText(((TextView) v
						.findViewById(R.id.batch_expiryDate)).getText()
						.toString());
				bat_cartonQty.setText(((TextView) v
						.findViewById(R.id.batch_cQty)).getText().toString());
				bat_looseQty.setText(((TextView) v
						.findViewById(R.id.batch_lQty)).getText().toString());

				String strQty = ((TextView) v.findViewById(R.id.batch_Qty))
						.getText().toString();
				String strcQty = ((TextView) v.findViewById(R.id.batch_cQty))
						.getText().toString();
				String strlQty = ((TextView) v.findViewById(R.id.batch_lQty))
						.getText().toString();
				
				if (strQty.matches("0")) {
					bat_qty.setText("");
					bat_qty.requestFocus();
					
					((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
	                .showSoftInput(bat_qty, 2);
					bat_qty.setSelection(bat_qty.length());
					
				} else {
					bat_qty.setText(((TextView) v.findViewById(R.id.batch_Qty))
							.getText().toString());
					bat_qty.setSelection(bat_qty.length());
				}
				
				if(strcQty.matches("0")){
					bat_cartonQty.setText("");
				}
				
				if(strlQty.matches("0")){
					bat_looseQty.setText("");
				}
							
				if (btCartonPerQty.matches("1") || btCartonPerQty.matches("0")
						|| btCartonPerQty.matches("")) {
					bat_qty.requestFocus();
					bat_qty.setSelection(bat_qty.length());
				}else{
					bat_cartonQty.requestFocus();	
					bat_cartonQty.setSelection(bat_cartonQty.length());
				}
				
				if(calCarton.matches("0")){
					bat_cartonQty.requestFocus();	
					bat_cartonQty.setSelection(bat_cartonQty.length());
				}

				batch_add.setVisibility(View.VISIBLE);
				if(isStockRequestBatchDetail){
				    bat_cartonQty.addTextChangedListener(cqtyTW);   
				    bat_qty.addTextChangedListener(qtyTW); 
				    bat_looseQty.addTextChangedListener(lqtyTW);
				    }
			}

		});

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
								
								((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
				                .showSoftInput(bat_qty, 2);
								bat_qty.setSelection(bat_qty.length());
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
								
								((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
				                .showSoftInput(bat_qty, 2);
								bat_qty.setSelection(bat_qty.length());
							}	
							
						}
					} else {

						if (haveexpiry.matches("True")) {
//							expirydate.requestFocus();
							Toast.makeText(mActivity, "Select Expiry Date",
									Toast.LENGTH_SHORT).show();
						} 

					}
					
				}

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
				
				String totQty = SOTDatabase.getTotaBatchQtySR(prodcode, sale_sl_no);
				int totCqty = SOTDatabase.getTotalBatchCQtySR(prodcode, sale_sl_no);
				int totLqty = SOTDatabase.getTotalBatchLQtySR(prodcode, sale_sl_no);
		
				double tqty=0;
								
				if(!totQty.matches("")){
					tqty = Double.parseDouble(totQty);
				}
				
				if (mActivity instanceof TransferAddProduct) {
					
					sl_cartonqty.setText("" + totCqty);
					sl_looseqty.setText("" + totLqty);
					
					sl_qty.setText("" + totQty);
					
					if(totQty.matches("0") || totQty.matches("0.0")){
						
						if(calCarton.matches("0")){
							
							if(totCqty>0 || tqty>0){
								 storeInDatabase();
							}
															
						}else{
							Toast.makeText(mActivity, "Total quantity is "+ totQty, Toast.LENGTH_SHORT).show();
							
							  sl_productcode.setText("");
							  sl_name.setText("");
							  sl_cartonqty.setText("");
							  sl_looseqty.setText("");
							  sl_qty.setText("");
							  sl_uom.setText("");
							  sl_uom.setText("");
							  sl_stock.setText("");
							  sl_pcspercarton.setText("");
						}
	
					}else{
						 storeInDatabase();
					}			    

				} else if (mActivity instanceof TransferSummary) {
					clQty(Id, "" + totQty, 0, btCartonPerQty, prodcode);
					
				}else if(mActivity instanceof StockAdjustmentAddproduct){
					
					calCarton = LogOutSetGet.getCalcCarton();
					if(calCarton.matches("0")){
						summaryStore();
					}else{
						if(totQty.matches("0") || totQty.matches("0.0")){
							Toast.makeText(mActivity, "Total quantity is "+ totQty, Toast.LENGTH_SHORT).show();
							
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
							sl_pcspercarton.setText("");
						}else{
						summaryStore();
						}
					}
	
					 
				    }else if(mActivity instanceof StockAdjustmentSummary){
						
						HashMap<String, String> hm = new HashMap<String, String>();
						
						hm.put("product_id", Id);
						hm.put("carton_qty", totCqty+"");
						hm.put("loose_qty", totLqty+"");
						hm.put("quantity", totQty);

						sqldb.updateStockAdjustmentSummary(hm);
												
						sm_cartonqty.setText(twoDecimalPoint(SOTDatabase.getTotalCqty()));
						sm_looseqty.setText(twoDecimalPoint(SOTDatabase.getTotalLqty()));
						sm_qty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));
						
						sl_cursor.requery();
					    cursor.requery();	
						
					}
				
				if(totCqty>0 || tqty>0){
				batDialog.dismiss();
				}else{
					Toast.makeText(mActivity, "Please add something", Toast.LENGTH_SHORT).show();
				}
			}
		});

		batch_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// sot.deletebatchbyprodcode(batch_codefield.getText().toString());
				serial_id.setText("");
				if (mActivity instanceof TransferAddProduct) {
					sl_productcode.setText("");
					sl_name.setText("");
					sl_cartonqty.setText("");
					sl_looseqty.setText("");
					sl_qty.setText("");
					// sl_foc.setText("");
					sl_uom.setText("");
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
				   }else if(mActivity instanceof TransferSummary){
					   sl_cursor.requery();
						cursor.requery();
				   }
				Toast.makeText(mActivity, "Cancelled", Toast.LENGTH_SHORT)
						.show();
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
					if(calCarton.matches("0")){
						
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
					}
						// productTotal(lsQty);
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
					if(calCarton.matches("0")){
						
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
					if(calCarton.matches("0")){
						
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

		bat_cartonQty.addTextChangedListener(cqtyTW);
		bat_looseQty.addTextChangedListener(lqtyTW);
		bat_qty.addTextChangedListener(qtyTW);


		batDialog = myDialog.show();


//		batDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
	

	public void strtDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		expirydate.setText(sdf.format(mCalendar.getTime()));
	}

	private class BatchListAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public BatchListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.transfer_batch_list_item, cursor);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			CheckBox batch_checkbox = (CheckBox) view
					.findViewById(R.id.transfer_checkbox);
			batch_checkbox.setVisibility(View.VISIBLE);

			// batch_checkbox.setTag(tag);
			int pos = cursor.getInt(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID));
			Log.d("Position", "getView: no tag on " + pos);
			batch_checkbox.setTag(pos);

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

			TextView batch_avail_cQty = (TextView) view
					.findViewById(R.id.avl_cQty);
			batch_avail_cQty.setVisibility(View.VISIBLE);
			batch_avail_cQty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_CARTON)));

			TextView batch_avail_lQty = (TextView) view
					.findViewById(R.id.avl_lQty);
			batch_avail_lQty.setVisibility(View.VISIBLE);
			batch_avail_lQty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_LOOSE)));

			TextView batch_avail_Qty = (TextView) view
					.findViewById(R.id.avl_Qty);
			batch_avail_Qty.setVisibility(View.VISIBLE);
			batch_avail_Qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_QTY)));

			TextView batch_cQty = (TextView) view.findViewById(R.id.batch_cQty);
			batch_cQty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

			TextView batch_lQty = (TextView) view.findViewById(R.id.batch_lQty);
			batch_lQty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

			TextView batch_Qty = (TextView) view.findViewById(R.id.batch_Qty);
			batch_Qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

			TextView batch_pcsPerCarton = (TextView) view
					.findViewById(R.id.batch_pcsPerCarton);
			batch_pcsPerCarton.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));
			
			TextView batch_remarks = (TextView) view
			.findViewById(R.id.remarks);
			batch_remarks.setVisibility(View.VISIBLE);
			
			String remarks = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_REMARKS));
			
			if(remarks!=null && !remarks .isEmpty()){
				
			}else{
				remarks = "-";
			}
			
			batch_remarks.setText(remarks);
			
			String prodcode, avlQty, qty;
			prodcode = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
			avlQty = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_QTY));
			qty = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

			
			calCarton = LogOutSetGet.getCalcCarton();
			if(calCarton.matches("0")){

			}else{
			
			if (avlQty.matches(qty)) {
					batch_checkbox.setChecked(true);
				} else {
					batch_checkbox.setChecked(false);
				}

			}

			if (batch_checkbox.isChecked()) {
				view.setBackgroundResource(R.drawable.list_item_selected_bg);
			} else {
				view.setBackgroundResource(R.drawable.list_item_even_bg);
			}

			String totQty = SOTDatabase.getBatchQtySR(prodcode, sale_sl_no);
			
			
			//delete
		/*	String totQty = "";		
			if(isStockRequestBatchDetail){
				String slno = SOTDatabase.getProductSno(Id);
				totQty = SOTDatabase.getBatchQtySR(prodcode,slno);
			} else{
				totQty = SOTDatabase.getBatchQty(prodcode);
			}*/
			edtotalqty.setText(totQty);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View tmpView = super.getView(position, convertView, parent);
			Log.d("getView:", "" + position);
			final CheckBox cBox = (CheckBox) tmpView
					.findViewById(R.id.transfer_checkbox);
			int id = (int) cBox.getTag();
			Log.d("cBox id value:", "" + id);
			cBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox checkBox = (CheckBox) v;
					int id = (int) checkBox.getTag();
					Log.d("checkBox id value:", "" + id);
					if (checkBox.isChecked()) {
						Log.d("Check Box", "Checked!");
						SOTDatabase.updateBatchQty(id, true);
					} else {
						Log.d("Check Box", "NOT Checked!");
						SOTDatabase.updateBatchQty(id, false);
					}
					cursor.requery();
				}
			});

			return tmpView;
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
					
					if(calCarton.matches("0")){
						q = 0;
						r = 0;
					}

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
			// menu.getItem(1)
			// .setOnMenuItemClickListener(new onContextMenuClick());
		}
	}

	private class onContextMenuClick implements
			MenuItem.OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			Log.d("onCreateContextMenu", "onContextItemSelected");
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			if (item.getTitle() == "Delete") {
				String id = ((TextView) info.targetView
						.findViewById(R.id.batch_sl_id)).getText().toString();
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
			} else {
				return false;
			}
			return true;

		}
	}

	public void clQty(String productid, String quantity, int foc,
			String pieceperqty, String prodcode) {

		int cartonQty = 0;
		double looseQty = 0;
		products_arr.clear();
		String cprice, productprice, taxtype, taxvalue, itemdiscount;

		products_arr = sqldb.getProducts(productid);

		// quantity = products_arr.get(0);
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
						

							netTotal1 = subTotal + taxAmount1;
							ProdNetTotal = twoDecimalPoint(netTotal1);
							
						} else {

							taxAmount = (tt * taxValueCalc) / 100;
							prodTax = fourDecimalPoint(taxAmount);
						

							netTotal = tt + taxAmount;
							ProdNetTotal = twoDecimalPoint(netTotal);
							
						}

					} else if (taxtype.matches("I")) {
						if (!itemdiscount.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc)
									/ (100 + taxValueCalc);
							prodTax = fourDecimalPoint(taxAmount1);
						

//							netTotal1 = subTotal + taxAmount1;
							netTotal1 = subTotal;
							ProdNetTotal = twoDecimalPoint(netTotal1);
							
						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							prodTax = fourDecimalPoint(taxAmount);
							

//							netTotal = tt + taxAmount;
							netTotal = tt;
							ProdNetTotal = twoDecimalPoint(netTotal);
							
						}

					} else if (taxtype.matches("Z")) {

						
						if (!itemdiscount.matches("")) {
//							netTotal1 = subTotal + taxAmount;
							netTotal1 = subTotal;
							ProdNetTotal = twoDecimalPoint(netTotal1);
							
						} else {
//							netTotal = tt + taxAmount;
							netTotal = tt;
							ProdNetTotal = twoDecimalPoint(netTotal);
						
						}

					} else {
						// sl_tax.setText("0.0");
						taxvalue = "0";
						netTotal = subTotal;
						ProdNetTotal = twoDecimalPoint(netTotal);
						
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

		int totCqty = SOTDatabase.getTotalBatchCQtySR(prodcode, sale_sl_no);
		int totLqty = SOTDatabase.getTotalBatchLQtySR(prodcode, sale_sl_no);
		
		
		//delete
		/*int totCqty, totLqty;
		
		if(isStockRequestBatchDetail){
			String slno = SOTDatabase.getProductSno(Id);
			totCqty = SOTDatabase.getTotalBatchCQtySR(prodcode,slno);
			totLqty = SOTDatabase.getTotalBatchLQtySR(prodcode,slno);
		} else{
			totCqty = SOTDatabase.getTotalBatchCQty(prodcode);
			totLqty = SOTDatabase.getTotalBatchLQty(prodcode);
		}*/
		
		if(calCarton.matches("0")){
			cartonQty = totCqty;
			looseQty = totLqty;
		}
		
		Log.d("Result", "c " + cartonQty + "l " + looseQty + "tt " + Prodtotal
				+ "sbTtl " + sbTtl + "prodTax " + prodTax + "ProdNetTotal "
				+ ProdNetTotal);

		sqldb.updateProductForBatch(productid, cartonQty, looseQty,
				productprice, itemdiscount, Prodtotal, sbTtl, prodTax,
				ProdNetTotal, quantity, "" + foc);

		sqldb.updateBilldiscProductValues(productid, sbTtl);

		sm_cartonqty.setText(twoDecimalPoint(SOTDatabase.getTotalCqty()));
		sm_looseqty.setText(twoDecimalPoint(SOTDatabase.getTotalLqty()));
		sm_qty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));
		
		sl_cursor.requery();
		cursor.requery();

	}

	public void storeBatch() {

		String bat_id = serial_id.getText().toString();
		String batchNo = batchno.getText().toString();
		String expiryDate = expirydate.getText().toString();
		String batchQty = bat_qty.getText().toString();
		String batchCqty = bat_cartonQty.getText().toString();
		String batchLqty = bat_looseQty.getText().toString();
		String prodcode = batch_codefield.getText().toString();

		String avl_qty = SOTDatabase.getBatchAvlQty(bat_id, prodcode);		
		String avl_cqty = SOTDatabase.getBatchAvlcQty(bat_id, prodcode);

		  if (batchCqty != null && !batchCqty.isEmpty()) {
		  } else {
		   batchCqty = "0";
		  }
		  if (batchLqty != null && !batchLqty.isEmpty()) {
		  } else {
		   batchLqty = "0";
		  }
		  
		  if (batchQty != null && !batchQty.isEmpty()) {
		  } else {
			  batchQty = "0";
		  }
		
		double davl_Qty = Double.parseDouble(avl_qty);
		double d_qty = Double.parseDouble(batchQty);
		
		if(calCarton.matches("0")){
			davl_Qty = Double.parseDouble(avl_cqty);
			d_qty = Double.parseDouble(batchCqty);
		}		
		
		if (davl_Qty >= d_qty) {

			Cursor cursor_count = SOTDatabase.getBatchCursorWithSR(prodcode, sale_sl_no);
			int count = cursor_count.getCount();
			count = count + 1;
			Log.d("cursor count", "->" + count);
			String batchSlNo = count + "";

			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("id", bat_id);
			hm.put("slNo", batchSlNo);
			hm.put("BatchNo", batchNo);
			hm.put("ExpiryDate", expiryDate);
			hm.put("CQty", batchCqty);
			hm.put("LQty", batchLqty);
			hm.put("Qty", batchQty);

			Log.d("Update Product", "->" + hm);
			SOTDatabase.updateBatch(hm);

			String totQty = SOTDatabase.getBatchQtySR(prodcode, sale_sl_no);
			
			//delete
			/*String totQty = "";		
			if(isStockRequestBatchDetail){
				String slno = SOTDatabase.getProductSno(Id);
				totQty = SOTDatabase.getBatchQtySR(prodcode,slno);
			} else{
				totQty = SOTDatabase.getBatchQty(prodcode);
			}*/
			
			
			edtotalqty.setText(totQty);

			batchno.setText("");
			expirydate.setText("");
			bat_cartonQty.setText("");
			bat_looseQty.setText("");
			bat_qty.setText("");
			serial_id.setText("");

			batch_add.setVisibility(View.GONE);
//			bat_qty.requestFocus();
//			((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
//            .showSoftInput(bat_qty, 2);
			
			InputMethodManager inputMethodManager = (InputMethodManager) mActivity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(
					bat_qty.getWindowToken(), 0);
						
			bat_qty.setSelection(bat_qty.length());

		} else {
//			bat_qty.requestFocus();
			
			
			if(calCarton.matches("0")){
				Toast.makeText(mActivity,
						"Carton Quantity must be less than or equal to available carton quantiy",
						Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(mActivity,
						"Quantity must be less than or equal to available quantiy",
						Toast.LENGTH_LONG).show();
			}
			
			
		}

	}
	
	public void storeInDatabase() {

		  double dcartonQty = 0.0, dlooseQty = 0.0, dQty = 0.0, dprice = 0.0, dpcspercarton = 0.0;
		  int cartonQty_int, looseQty_int, qty_int, pcspercarton_int;
		  	//delete
//		  cursor = SOTDatabase.getCursor();
//
//		  if (cursor != null && cursor.getCount() > 0) {
//		   sl_no = cursor.getCount();
//		   sl_no++;
//		  }
		  
		  sl_no =  Integer.parseInt(sale_sl_no);

		  String productcode = sl_productcode.getText().toString();
		  String productname = sl_name.getText().toString();
		  String uom = sl_uom.getText().toString();
		  String pcspercarton = sl_pcspercarton.getText().toString();

		  String sl_cartonQty = sl_cartonqty.getText().toString();
		  String sl_looseQty = sl_looseqty.getText().toString();
		  String sl_Qty = sl_qty.getText().toString();

		  if (sl_cartonQty != null && !sl_cartonQty.isEmpty()) {
		   dcartonQty = Double.parseDouble(sl_cartonQty);
		  }
		  if (sl_looseQty != null && !sl_looseQty.isEmpty()) {
		   dlooseQty = Double.parseDouble(sl_looseQty);
		  }
		  if (sl_Qty != null && !sl_Qty.isEmpty()) {
		   dQty = Double.parseDouble(sl_Qty);
		  }
		  if (pcspercarton != null && !pcspercarton.isEmpty()) {
		   dpcspercarton = Double.parseDouble(pcspercarton);
		  }
		  if (sl_price != null && !sl_price.isEmpty()) {
		   dprice = Double.parseDouble(sl_price);
		  }

		  cartonQty_int = (int) dcartonQty;
		  looseQty_int = (int) dlooseQty;
		  qty_int = (int) dQty;
		  pcspercarton_int = (int) dpcspercarton;

		  Log.d("sl_cartonQty", "" + sl_cartonQty);
		  Log.d("sl_looseQty", "" + sl_looseQty);
		  Log.d("sl_Qty", "" + sl_Qty);
		  Log.d("Price", "" + dprice);

		  String prodCode = SOTDatabase.getProductCodeSR(productcode, sale_sl_no);
		  
		  if(prodCode !=null && !prodCode.isEmpty()){
			  if(prodCode.matches(productcode)){
				  SOTDatabase.updateProductForTransferSR(productcode,
						    cartonQty_int, looseQty_int, qty_int, sale_sl_no);
			  }else{
			  
			  SOTDatabase.storeProduct(sl_no, productcode, productname,
			    cartonQty_int, looseQty_int, qty_int, 0, dprice, 0.0, uom,
			    pcspercarton_int, 0.0, 0.0, "", "", "", "", "", "0.00", "0",
			    "0", "", "","","","","");
			  }
		  }else{
			  SOTDatabase.storeProduct(sl_no, productcode, productname,
					    cartonQty_int, looseQty_int, qty_int, 0, dprice, 0.0, uom,
					    pcspercarton_int, 0.0, 0.0, "", "", "", "", "", "0.00", "0",
					    "0", "", "","","","","");
		  }
		  
		  SOTDatabase.updateproductbatch(productcode,havebatch,haveexpiry);

		  sl_productcode.setText("");
		  sl_name.setText("");
		  sl_cartonqty.setText("");
		  sl_looseqty.setText("");
		  sl_qty.setText("");
		  sl_uom.setText("");
		  sl_uom.setText("");
		  sl_stock.setText("");
		  sl_pcspercarton.setText("");
 
		  sl_productcode.requestFocus();

		  InputMethodManager imm = (InputMethodManager) mActivity
		    .getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.hideSoftInputFromWindow(sl_productcode.getWindowToken(), 0);

		  sl_cartonqty.setEnabled(true);
		  sl_cartonqty.setFocusable(true);
		  sl_cartonqty.setBackgroundResource(R.drawable.edittext_bg);

		  sl_looseqty.setEnabled(true);
		  sl_looseqty.setFocusable(true);
		  sl_looseqty.setBackgroundResource(R.drawable.edittext_bg);

		  sl_qty.setEnabled(true);
		  sl_qty.setFocusable(true);
		  sl_qty.setBackgroundResource(R.drawable.edittext_bg);

		 }
	
	public void summaryStore() {
		
		//delete
//		Cursor cursor = SOTDatabase.getCursor();//
//		if (cursor != null && cursor.getCount() > 0) {
//		sl_no = cursor.getCount();
//		sl_no++;
//	}
		
		String prodcode = batch_codefield.getText().toString();
		String totQty = SOTDatabase.getTotaBatchQtySR(prodcode, sale_sl_no);
		int totCqty = SOTDatabase.getTotalBatchCQtySR(prodcode, sale_sl_no);
		int totLqty = SOTDatabase.getTotalBatchLQtySR(prodcode, sale_sl_no);


		int Qtyinhand = 0, qty = 0, cartonPerQty = 0;
		double price = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0;
		String subtotal = "", nettotal = "";
		HashMap<String, String> hm = new HashMap<String, String>();

		String uomStr = sl_uom.getText().toString();
		String totalStr = sl_Total.getText().toString();
		String taxStr = sl_Tax.getText().toString();
		String netTotalStr = sl_Nettotal.getText().toString();
		String qtyinhand = sl_StockinHand.getText().toString();

		

		if (!totQty.matches("")) {
			qty = Integer.parseInt(totQty);
		}
		if (!btCartonPerQty.matches("")) {
			cartonPerQty = Integer.parseInt(btCartonPerQty);
		}

		String p_Sno = SOTDatabase.getSalesId(sale_sl_no);
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
			queryValues.put("AddDetect", "-1");
			queryValues.put("SOSlno", i_sssno+"");

			 SOTDatabase.updateStockAdjustmentProduct(queryValues);
			// SOTDatabase.updateproductbatch(batCode,havebatch,haveexpiry);

		} else {
			
			HashMap<String, String> queryValues = new HashMap<String, String>();
			queryValues.put("slNo", sale_sl_no);
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
			queryValues.put("AddDetect", "-1");
			queryValues.put("MinimumSellingPrice", "0");
			queryValues.put("ItemRemarks", "");
			queryValues.put("SOSlno", "");

			SOTDatabase.storeStockAdjustmentProduct(queryValues);
      		SOTDatabase.updateproductbatch(batCode, havebatch, haveexpiry);

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
