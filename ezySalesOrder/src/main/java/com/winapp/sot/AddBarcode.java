package com.winapp.sot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ResourceCursorAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;

public class AddBarcode extends Activity implements OnItemClickListener {

	ImageButton add, back;
	Intent callSummary;
	Button  btn_strts, btn_ends, ok, cancel, edupButton, eddownButton,weight_decimal_minus,weight_decimal_plus;
	EditText edpalettecount, edcodefield, ednamefield, edweight, edbarcode,
			edtotal, edpieceperqty, editText, getStrtsEnds, startson, endson,
			edtotalcount, edproductId, item_remarks,
			weight_decimal;
	TextView prodid;
	ImageButton barcode_save,carton_loose,set_weight;
	String edit_id, productTxt = null, barcodeTxt = null, OutputData = "",
			keyValue, value, productendson, productstartson, valid_url,
			keyP = "FWMS", editBrcd;
	ListView lv;
	int count = 1, textlength = 0, stuprange = 50, stdownrange = 1, stwght = 1,
			eduprange = 50, eddownrange = 1, edwght = 1, palettecount, strts,
			ends,edwghtdecimal;

	static String productresult, barcoderesult;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetProduct";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCT_WEIGHT = "Weight";
	private static final String PRODUCT_STARTSON = "WeightBarcodeStartsOn";
	private static final String PRODUCT_ENDSON = "WeightBarcodeEndsOn";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";

	AlertDialog alert;
	AlertDialog.Builder builder;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> paletteids = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> albarsplit = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> hashmap = new HashMap<String, String>();
	//ArrayList<HashMap<String, String>> albarcode = new ArrayList<HashMap<String, String>>();
	ArrayList<String> alprodcode = new ArrayList<String>();
	//ArrayList<String> albar = new ArrayList<String>();
	private AlertDialog myalertDialog = null;

	HashMap<String, String> hmsplitbc = new HashMap<String, String>();
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> searchResults;
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> productweight = new ArrayList<HashMap<String, String>>();
	// MySQLiteDataBase sqldb;

	ProductListAdapter crsrAdptr;
	Cursor cursor;

	ArrayList<HashMap<String, String>> datadb;

	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout addproduct_layout, itemRemarkslayout;
	ArrayList<String> editProductArr = new ArrayList<String>();
	ArrayList<String> editBarcodeArr = new ArrayList<String>();

	String keyProdCode, ProductCode, ProductName, WeightBarcodeStartsOn,
			WeightBarcodeEndsOn, HaveBatch, HaveExpiry, HaveMfgDate, weightFld,
			CategoryCode, SubCategoryCode, UOMCode, PcsPerCarton, barcodeWeightDecimal="",header,barcodefrom;
	InputMethodManager mgr;
	ArrayList<String> productId_arr;
	ArrayList<String> products_arr;
	SOTDatabase sqldb;
	String serialno = "", priceflag, quantity, cprice, productprice, taxtype,
			taxvalue, pieceperqty, itemdiscount = "";
	int cartonqty, barcodecount, corl=0;
	AlertDialog levelDialog;

	// Strings to Show In Dialog with Radio Buttons
	final CharSequence[] items = {" Carton "," Loose "};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addbarcode);

		FWMSSettingsDatabase.init(AddBarcode.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new NewProductWebService(valid_url);
		Bundle b = getIntent().getExtras();

		addproduct_layout = (LinearLayout) findViewById(R.id.addproduct_layout);
		itemRemarkslayout = (LinearLayout) findViewById(R.id.itemRemarkslayout);

		add = (ImageButton) findViewById(R.id.btplus);

		barcode_save = (ImageButton) findViewById(R.id.barcode_save);
		back = (ImageButton) findViewById(R.id.back);
		carton_loose = (ImageButton) findViewById(R.id.carton_loose);
		set_weight= (ImageButton) findViewById(R.id.set_weight);
		lv = (ListView) findViewById(R.id.listView1);
		edcodefield = (EditText) findViewById(R.id.codefield);
		edpalettecount = (EditText) findViewById(R.id.palettecount);
		ednamefield = (EditText) findViewById(R.id.namefield);
		edweight = (EditText) findViewById(R.id.weightresult);
		edbarcode = (EditText) findViewById(R.id.weightbarcode);
		edtotal = (EditText) findViewById(R.id.edtotalweight);
		edpieceperqty = (EditText) findViewById(R.id.weightpieceperqty);
		edtotalcount = (EditText) findViewById(R.id.edtotalcount);
		item_remarks = (EditText) findViewById(R.id.item_remarks);
		edproductId = (EditText) findViewById(R.id.productid);

		productId_arr = new ArrayList<String>();
		products_arr = new ArrayList<String>();
		productId_arr.clear();
		products_arr.clear();

		sqldb = new SOTDatabase(AddBarcode.this);
		header = SalesOrderSetGet.getHeader_flag();
		datadb = sqldb.getAllProducts();

		if (b != null) {
			
			 HashMap<String, String> queryValues = new HashMap<String, String>();		    	 
	    	 queryValues.put("status", "1");		    	 
	    	 sqldb.updateBarcodeStatus(queryValues);
			
			edproductId.setText(b.getString("SOT_ssid"));
			edcodefield.setText(b.getString("SOT_ssproductcode"));
			ednamefield.setText(b.getString("SOT_str_ssprodname"));
			edpalettecount.setText(b.getString("SOT_str_ssno"));
			serialno = b.getString("SOT_str_ssno");
			String ctnqty = b.getString("SOT_str_c_qty");
			String itemRemarks = b.getString("SOT_str_itemremarks");
			barcodefrom = b.getString("Barcodefrom");
//			Log.d("barcodefrom", barcodefrom);
				if((itemRemarks != null && !itemRemarks.isEmpty())){
				itemRemarkslayout.setVisibility(View.VISIBLE);
				item_remarks.setText(itemRemarks);
			}
 
			double dcartonqty = Double.parseDouble(ctnqty);
			cartonqty =(int)dcartonqty;
			Log.d("carton qty", "" + cartonqty);
			getListView();
		}
		
		String countsno="";
//		if(corl==1){	
			int boxes = sqldb.getBoxesSum(edproductId.getText()
					.toString());
			
			countsno = String.valueOf(boxes);
//		}else{
//			countsno = sqldb.summarypalettecount(edproductId.getText()
//					.toString());
//		}
		
		
		if (!countsno.matches("")) {
			barcodecount = Integer.valueOf(countsno);

			Log.d("barcode count", "" + barcodecount);
		}
		String productid = sqldb.getProd(edproductId.getText().toString());
		  if(productid.matches("")){
			barcode_save.setVisibility(View.INVISIBLE);
			Log.d("INVISIBLE", "INVISIBLE");
		}else{
			barcode_save.setVisibility(View.VISIBLE);
			Log.d("VISIBLE", "VISIBLE");
		}
		editProductArr.clear();
		al.clear();
		AsyncCallWSADDPRD task = new AsyncCallWSADDPRD();
		task.execute();
	
		registerForContextMenu(lv);

		carton_loose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cartonloosealert();
			
			}
		});
		
		set_weight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				setStartsEndsOn();
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String productid = sqldb.getProd(edproductId.getText().toString());
				  if(productid.matches("")){
				
					  if(barcodefrom.matches("InvoiceSummary")){
							callSummary = new Intent(AddBarcode.this,
									InvoiceSummary.class);
						}
						else if(barcodefrom.matches("DeliverySummary")){
							callSummary = new Intent(AddBarcode.this,
									DeliverySummary.class);
						}else if(barcodefrom.matches("GraSummary")){
							callSummary = new Intent(AddBarcode.this,
									GraSummary.class);
						}else if(barcodefrom.matches("TransferSummary")){
						  callSummary = new Intent(AddBarcode.this,
								  TransferSummary.class);
					  }
					     startActivity(callSummary);
					     AddBarcode.this.finish();
				    }
				    else{
				     alertDialog();
				    }
				
			}
		});
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (edpieceperqty.getText().length() != 0
						&& edpieceperqty.getText().toString() != "") {
					
					if(corl == 1){
						addListItemfield();
					}else{
						addListitemPPerQty();
					}

					//Log.d("edpieceperqty", "1");
				} else {
					addListItemfield(); // no value in boxes
					
					//Log.d("edpieceperqty", "2");

				}
			}
		});

		barcode_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(barcodefrom.matches("GraSummary")){		
					  
					  int barcodestatus = 1;
//						sqldb.updateBarcodestatus(edproductId.getText()
//								.toString(),barcodestatus);						
					
						String totalbarcodeweight = sqldb.getProductWeight(edproductId.getText()
								.toString());
						sqldb.updateQty(edproductId.getText()
								.toString(), totalbarcodeweight,barcodestatus);
						Log.d("totalbarcodeweight", ""+totalbarcodeweight);
							
						clQty();
						
//						callSummary = new Intent(AddBarcode.this,
//								GraSummary.class);
//						  startActivity(callSummary);
				}else{
					updateSummary();
				}	
			}
		});

		edbarcode.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT
						|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					if (!edbarcode.getText().toString().matches("")) {
						editBarCodeField();
					} else {

					}
					return true;
				}
				return false;
			}
		});

		edweight.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (edcodefield.getText().length() != 0
							&& edcodefield.getText().toString() != "") {
						if (ednamefield.getText().length() != 0
								&& ednamefield.getText().toString() != "") {
							if (edbarcode.getText().toString().matches("")
									&& edweight.getText().toString()
											.matches("")) {
								Toast.makeText(getApplicationContext(),
										"Please Enter Weight ",
										Toast.LENGTH_SHORT).show();
							}
							if (edweight.getText().length() != 0
									&& edweight.getText().toString() != "") {

								if (edpieceperqty.getText().length() != 0
										&& edpieceperqty.getText().toString() != "") {
									
									if(corl == 1){
										doneMethod();
									}else{
										donePPQtyMethod();
									}
									
								} else {
									doneMethod();
								}

								edweight.requestFocus();
							}

						} else {
							Toast.makeText(getApplicationContext(),
									"Please Select Product ",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								" Please Enter Code ", Toast.LENGTH_SHORT)
								.show();
					}
					return true;
				}
				return false;
			}
		});
	}
	
	public void cartonloosealert(){
		 // Creating and Building the Dialog 
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the option");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
        @Override
		public void onClick(DialogInterface dialog, int item) {
           
            Log.d("item", ""+item);
            switch(item)
            {
                case 0:
                	corl = 0;
                	carton_loose.setImageResource(R.mipmap.ic_carton);
                         break;
                case 1:
                	corl = 1;
                	carton_loose.setImageResource(R.mipmap.ic_loose);
                        break;
                
            }
            levelDialog.dismiss();    
            }
        });
        levelDialog = builder.create();
        levelDialog.show();
	}
	
	public void updateSummary(){
		
	int barcodestatus = 1;
		String totalbarcodeweight = sqldb.getProductWeight(edproductId.getText()
				.toString());
		sqldb.updateQty(edproductId.getText()
				.toString(), totalbarcodeweight,barcodestatus);
		Log.d("totalbarcodeweight", ""+totalbarcodeweight);
		priceflag = SalesOrderSetGet.getCartonpriceflag();

	if (priceflag.matches("null") || priceflag.matches("")) {
		priceflag = "0";
	}

	clQty();

 
	}
	
	public void addListItemfield() {
		int sno = 1;
		String prd;

		if (edcodefield.getText().length() != 0
				&& edcodefield.getText().toString() != "") {
			if (ednamefield.getText().length() != 0
					&& ednamefield.getText().toString() != "") {
				if (edweight.getText().toString().matches("")) {
					barcodeValue();
				}

				if (edweight.getText().length() != 0
						&& edweight.getText().toString() != "") {
					HashMap<String, String> queryValues = new HashMap<String, String>();
					if (datadb != null) {
						Log.d("edproductId", "addListItemfield"+edproductId.getText().toString());
						prd = sqldb.getProd(edproductId.getText().toString());
						Log.d("prd", "addListItemfield"+prd);
						if (prd.equals(edproductId.getText().toString())) {
							int snos = sqldb.getnum(edproductId.getText()
									.toString());
							int sns = snos + 1;
							queryValues.put("paletteId", edpalettecount
									.getText().toString());
							queryValues.put("code", edcodefield.getText()
									.toString());
							queryValues.put("name", ednamefield.getText()
									.toString());
							if (edbarcode.getText().toString().equals("")) {
								String zero = "0";
								queryValues.put("barcode", zero);
							} else {
								queryValues.put("barcode", edbarcode.getText()
										.toString());
							}
							queryValues.put("weight", edweight.getText()
									.toString());
							queryValues.put("snum", "" + sns);

							queryValues.put("productId", ""
									+ edproductId.getText().toString());
							
							

							Log.d("1", "" + sno);
						} else {
							queryValues.put("paletteId", edpalettecount
									.getText().toString());
							queryValues.put("code", edcodefield.getText()
									.toString());
							queryValues.put("name", ednamefield.getText()
									.toString());
							if (edbarcode.getText().toString().equals("")) {
								String zero = "0";
								queryValues.put("barcode", zero);
							} else {

								queryValues.put("barcode", edbarcode.getText()
										.toString());
							}
							queryValues.put("weight", edweight.getText()
									.toString());
							queryValues.put("snum", "" + sno);

							queryValues.put("productId", ""
									+ edproductId.getText().toString());
							
							
							Log.d("2", "" + sno);
						}

					} else {
						queryValues.put("paletteId", edpalettecount.getText()
								.toString());
						queryValues.put("code", edcodefield.getText()
								.toString());
						queryValues.put("name", ednamefield.getText()
								.toString());
						if (edbarcode.getText().toString().equals("")) {
							String zero = "0";
							queryValues.put("barcode", zero);
						} else {
							queryValues.put("barcode", edbarcode.getText()
									.toString());
						}
						queryValues
								.put("weight", edweight.getText().toString());
						queryValues.put("snum", "" + sno);

						queryValues.put("productId", ""
								+ edproductId.getText().toString());
						
						

						Log.d("3", "" + sno);
					}

					String countsno = sqldb.summarypalettecount(edproductId
							.getText().toString());
					if (!countsno.matches("")) {
						barcodecount = Integer.valueOf(countsno);

						Log.d("barcode count", "" + barcodecount);
					}

					String boxes = edpieceperqty.getText().toString();
					if(boxes.matches("")){
						boxes = "1";
					}
					
					queryValues.put("Boxes", boxes); 
					queryValues.put("status", "0");
					// added boxes column value
				
					
					sqldb.insertproduct(queryValues);
					getListView();
					
					edweight.setText("");
					edbarcode.setText("");
					edpieceperqty.setText("");
				} else if (edweight.getText().toString().matches("")
						&& edbarcode.getText().toString().matches("")) {
					Toast.makeText(getApplicationContext(),
							"Please Enter Barcode or Weight ",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getApplicationContext(),
						"Please Select Product ", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), " Please Enter Code ",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void addListitemPPerQty() {
		int sno = 1;
		String prd;
		int pieceperqty = Integer.parseInt(edpieceperqty.getText().toString());
		if (pieceperqty <= 100) {

			if (edcodefield.getText().length() != 0
					&& edcodefield.getText().toString() != "") {
				if (ednamefield.getText().length() != 0
						&& ednamefield.getText().toString() != "") {

					if (edweight.getText().toString().matches("")) {
						barcodeValue();
					}

					if (edweight.getText().length() != 0
							&& edweight.getText().toString() != "") {
						for (int i = 0; i < pieceperqty; i++) {

							HashMap<String, String> queryValues = new HashMap<String, String>();
							if (datadb != null) {
								prd = sqldb.getProd(edproductId.getText()
										.toString());
								Log.d("prd", "addListitemPPerQty"+prd);
								if (prd.equals(edproductId.getText().toString())) {
									int snos = sqldb.getnum(edproductId
											.getText().toString());
									int sns = snos + 1;
									queryValues.put("paletteId", edpalettecount
											.getText().toString());
									queryValues.put("code", edcodefield
											.getText().toString());
									queryValues.put("name", ednamefield
											.getText().toString());
									if (edbarcode.getText().toString()
											.equals("")) {
										String zero = "0";
										queryValues.put("barcode", zero);
									} else {
										queryValues.put("barcode", edbarcode
												.getText().toString());
									}
									queryValues.put("weight", edweight
											.getText().toString());
									queryValues.put("snum", "" + sns);

									queryValues.put("productId", ""
											+ edproductId.getText().toString());
									Log.d("1", "" + sno);
								} else {
									queryValues.put("paletteId", edpalettecount
											.getText().toString());
									queryValues.put("code", edcodefield
											.getText().toString());
									queryValues.put("name", ednamefield
											.getText().toString());
									if (edbarcode.getText().toString()
											.equals("")) {
										String zero = "0";
										queryValues.put("barcode", zero);
									} else {

										queryValues.put("barcode", edbarcode
												.getText().toString());
									}
									queryValues.put("weight", edweight
											.getText().toString());
									queryValues.put("snum", "" + sno);

									queryValues.put("productId", ""
											+ edproductId.getText().toString());
									Log.d("2", "" + sno);
								}

							} else {
								queryValues.put("paletteId", edpalettecount
										.getText().toString());
								queryValues.put("code", edcodefield.getText()
										.toString());
								queryValues.put("name", ednamefield.getText()
										.toString());
								if (edbarcode.getText().toString().equals("")) {
									String zero = "0";
									queryValues.put("barcode", zero);
								} else {
									queryValues.put("barcode", edbarcode
											.getText().toString());
								}
								queryValues.put("weight", edweight.getText()
										.toString());
								queryValues.put("snum", "" + sno);
								queryValues.put("productId", ""
										+ edproductId.getText().toString());
								Log.d("3", "" + sno);
							}

							String countsno = sqldb
									.summarypalettecount(edproductId.getText()
											.toString());
							if (!countsno.matches("")) {
								barcodecount = Integer.valueOf(countsno);

								Log.d("barcode count", "" + barcodecount);
							}

//							String boxes = edpieceperqty.getText().toString();
//							if(boxes.matches("")){
//								boxes = "1";
//							}
							
							queryValues.put("Boxes", "1");
							queryValues.put("status", "0");
							
							sqldb.insertproduct(queryValues);
							getListView();
							
						}
					} else if (edweight.getText().toString().matches("")
							&& edbarcode.getText().toString().matches("")) {
						Toast.makeText(getApplicationContext(),
								"Please Enter Weight ", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							"Please Select Product ", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(), " Please Enter Code ",
						Toast.LENGTH_SHORT).show();
			}

		}
		edweight.setText("");
		edbarcode.setText("");
		edpieceperqty.setText("");
	}

	public void getListView() {
		cursor = sqldb.getPalette(this.edproductId.getText().toString());

		crsrAdptr = new ProductListAdapter(this, cursor);
		lv.setAdapter(crsrAdptr);
		
		double tot = sqldb.getTotal(edproductId.getText().toString());
		edtotal.setText(String.format("%.3f", tot));

//		if(corl==1){
			int countsno1 = sqldb.getBoxesSum(edproductId.getText()
					.toString());
			edtotalcount.setText(""+countsno1);
//		}else{
//			String countsno = sqldb.summarypalettecount(edproductId.getText()
//					.toString());
//			edtotalcount.setText(countsno);
//		}
		
		if(barcode_save.getVisibility()==View.INVISIBLE){
			barcode_save.setVisibility(View.VISIBLE);
		}
	}

	public void donePPQtyMethod() {

		int sno = 1;
		String prd;
		int pieceperqty = Integer.parseInt(edpieceperqty.getText().toString());
		for (int i = 0; i < pieceperqty; i++) {
			HashMap<String, String> queryValues = new HashMap<String, String>();
			if (datadb != null) {
				prd = sqldb.getProd(edproductId.getText().toString());
				Log.d("prd", "donePPQtyMethod"+prd);
				if (prd.equals(edproductId.getText().toString())) {
					int snos = sqldb.getnum(edproductId.getText().toString());
					int sns = snos + 1;
					queryValues.put("paletteId", edpalettecount.getText()
							.toString());
					queryValues.put("code", edcodefield.getText().toString());
					queryValues.put("name", ednamefield.getText().toString());
					if (edbarcode.getText().toString().equals("")) {
						String zero = "0";
						queryValues.put("barcode", zero);
					} else {

						queryValues.put("barcode", edbarcode.getText()
								.toString());
					}
					queryValues.put("weight", edweight.getText().toString());
					queryValues.put("snum", "" + sns);
					queryValues.put("productId", ""
							+ edproductId.getText().toString());
					Log.d("1", "" + sno);
				} else {

					queryValues.put("paletteId", edpalettecount.getText()
							.toString());
					queryValues.put("code", edcodefield.getText().toString());
					queryValues.put("name", ednamefield.getText().toString());
					if (edbarcode.getText().toString().equals("")) {
						String zero = "0";
						queryValues.put("barcode", zero);
					} else {

						queryValues.put("barcode", edbarcode.getText()
								.toString());
					}
					queryValues.put("weight", edweight.getText().toString());
					queryValues.put("snum", "" + sno);
					queryValues.put("productId", ""
							+ edproductId.getText().toString());
					Log.d("2", "" + sno);
				}
			} else {

				queryValues.put("paletteId", edpalettecount.getText()
						.toString());
				queryValues.put("code", edcodefield.getText().toString());
				queryValues.put("name", ednamefield.getText().toString());
				if (edbarcode.getText().toString().equals("")) {
					String zero = "0";
					queryValues.put("barcode", zero);
				} else {

					queryValues.put("barcode", edbarcode.getText().toString());
				}
				queryValues.put("weight", edweight.getText().toString());
				queryValues.put("snum", "" + sno);
				queryValues.put("productId", ""
						+ edproductId.getText().toString());
				Log.d("3", "" + sno);
			}

			String countsno = sqldb.summarypalettecount(edproductId.getText()
					.toString());
			if (!countsno.matches("")) {
				barcodecount = Integer.valueOf(countsno);

				Log.d("barcode count", "" + barcodecount);
			}
			
//			String boxes = edpieceperqty.getText().toString();
//			if(boxes.matches("")){
//				boxes = "1";
//			}
			
			queryValues.put("Boxes", "1");
			queryValues.put("status", "0");
			
			sqldb.insertproduct(queryValues);
			getListView();
		}
		edpieceperqty.setText("");
		edbarcode.setText("");
		edweight.setText("");
	}

	public void doneMethod() {

		int sno = 1;
		String prd;
		HashMap<String, String> queryValues = new HashMap<String, String>();
		if (datadb != null) {
			Log.d("edproductId", "doneMethod"+edproductId.getText().toString());
			prd = sqldb.getProd(edproductId.getText().toString());
			Log.d("prd", "doneMethod"+prd);
			if (prd.equals(edproductId.getText().toString())) {
				int snos = sqldb.getnum(edproductId.getText().toString());
				int sns = snos + 1;
				queryValues.put("paletteId", edpalettecount.getText()
						.toString());
				queryValues.put("code", edcodefield.getText().toString());
				queryValues.put("name", ednamefield.getText().toString());
				if (edbarcode.getText().toString().equals("")) {
					String zero = "0";
					queryValues.put("barcode", zero);
				} else {

					queryValues.put("barcode", edbarcode.getText().toString());
				}
				queryValues.put("weight", edweight.getText().toString());
				queryValues.put("snum", "" + sns);
				Log.d("weight-->", "w" + edweight.getText().toString());
				queryValues.put("productId", ""
						+ edproductId.getText().toString());
				Log.d("1", "" + sno);
			} else {

				queryValues.put("paletteId", edpalettecount.getText()
						.toString());
				queryValues.put("code", edcodefield.getText().toString());
				queryValues.put("name", ednamefield.getText().toString());
				if (edbarcode.getText().toString().equals("")) {
					String zero = "0";
					queryValues.put("barcode", zero);
				} else {

					queryValues.put("barcode", edbarcode.getText().toString());
				}
				queryValues.put("weight", edweight.getText().toString());
				queryValues.put("snum", "" + sno);
				queryValues.put("productId", ""
						+ edproductId.getText().toString());
				Log.d("2", "" + sno);
			}
		} else {

			queryValues.put("paletteId", edpalettecount.getText().toString());
			queryValues.put("code", edcodefield.getText().toString());
			queryValues.put("name", ednamefield.getText().toString());
			if (edbarcode.getText().toString().equals("")) {
				String zero = "0";
				queryValues.put("barcode", zero);
			} else {

				queryValues.put("barcode", edbarcode.getText().toString());
			}
			queryValues.put("weight", edweight.getText().toString());
			queryValues.put("snum", "" + sno);
			queryValues.put("productId", "" + edproductId.getText().toString());
			Log.d("3", "" + sno);

		}

		String countsno = sqldb.summarypalettecount(edproductId.getText()
				.toString());
		if (!countsno.matches("")) {
			barcodecount = Integer.valueOf(countsno);

			Log.d("barcode count", "" + barcodecount);
		}

		String boxes = edpieceperqty.getText().toString();
		if(boxes.matches("")){
			boxes = "1";
		}
		
		queryValues.put("Boxes", boxes); 
		queryValues.put("status", "0");
		
		sqldb.insertproduct(queryValues);
		getListView();

		edweight.setText("");
		edbarcode.setText("");
	}

	public void editBarCodeField() {
		if (edbarcode.getText().toString() != ""
				&& edbarcode.getText().length() != 0) {
			String getbarcode = edbarcode.getText().toString();
			Set<Entry<String, String>> keys = hmsplitbc.entrySet();
			Iterator<Entry<String, String>> iterator = keys.iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Entry mapEntry = iterator.next();
				String keyValue = (String) mapEntry.getKey();
				String value = (String) mapEntry.getValue();

				String stbarc = edcodefield.getText().toString();
				try{
				if (stbarc.toLowerCase().equals(keyValue.toLowerCase())) {
					Log.d("hmKey", keyValue);
					Log.d("hmvalue", value);
					String[] parts = value.split(",");
					String strpart1 = parts[0];
					String strpart2 = parts[1];
					String strpart3 = parts[2];
					Log.d("strpart1", strpart1);
					Log.d("strpart2", strpart2);
					Log.d("strpart3", "weight decimal"+strpart3);
					
					if (!strpart1.equals(strpart2) && !strpart1.equals(null)
							&& !strpart1.equals(null) && !strpart1.equals("0")) {
						int startwe = Integer.valueOf(parts[0]);
						int endwe = Integer.valueOf(parts[1]);
						if (startwe < endwe) {
							Log.d("startwe", "" + startwe);
							Log.d("endwe", "" + endwe);
							String[] pairs = value.split(",");
							int part1 = Integer.valueOf(pairs[0]);
							int part2 = Integer.valueOf(pairs[1]);
							int part4 = Integer.valueOf(pairs[2]);
							if (part2 <= edbarcode.getText().length()) {
								int part3 = part1 - 1;
								Log.d("part2", "" + part2);
								String substr = getbarcode.substring(part3,
										part2);
								int length = substr.length();
								if (length >= 3) {
									String str = new StringBuilder(substr)
											.insert(substr.length() - part4, ".")
											.toString();
									edweight.setText(str);
									Log.d("length3", "" + length);
									Log.d("str3", str);
									if (edpieceperqty.getText().length() != 0
											&& edpieceperqty.getText()
													.toString() != "") {
										donePPQtyMethod();
									} else {
										doneMethod();
									}
									break;
								} else if (length >= 2) {
									String str = new StringBuilder(substr)
											.insert(substr.length() - part4, ".")
											.toString();
									String seg = "0" + str;
									edweight.setText(seg);
									Log.d("str2", str);
									Log.d("length2", "" + length);
									if (edpieceperqty.getText().length() != 0
											&& edpieceperqty.getText()
													.toString() != "") {
										donePPQtyMethod();
									} else {
										doneMethod();
									}
									break;
								} else if (length >= 1) {
									String str = substr + ".00";
									Log.d("str1", str);
									edweight.setText(str);
									if (edpieceperqty.getText().length() != 0
											&& edpieceperqty.getText()
													.toString() != "") {
										donePPQtyMethod();
									} else {
										doneMethod();
									}
									break;
								}
							} else {
								Log.d("tpart2", "" + part2);
								Toast.makeText(
										getApplicationContext(),
										"Use At least " + part2
												+ " digit barcode  ",
										Toast.LENGTH_SHORT).show();
								break;
							}
						} else {
							edweight.setText("0.0");
							setStartsEndsOn();
							break;
						}
					}

					else {
						edweight.setText("0.0");
						setStartsEndsOn();
						break;
					}

				}
				
			}catch(Exception e){
			e.printStackTrace();	
			}
			}
		}

		edbarcode.addTextChangedListener(new TextWatcher() {
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
				textlength = edbarcode.getText().length();
				edweight.setText("");
			}
		});
	}


	public void barcodeValue() {
		if (edbarcode.getText().toString() != ""
				&& edbarcode.getText().length() != 0) {
			String getbarcode = edbarcode.getText().toString();
			Set<Entry<String, String>> keys = hmsplitbc.entrySet();
			Iterator<Entry<String, String>> iterator = keys.iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Entry mapEntry = iterator.next();
				String keyValue = (String) mapEntry.getKey();
				String value = (String) mapEntry.getValue();

				String stbarc = edcodefield.getText().toString();

				if (stbarc.toLowerCase().equals(keyValue.toLowerCase())) {

					String[] parts = value.split(",");
					String strpart1 = parts[0];
					String strpart2 = parts[1];
					String strpart3 = parts[2];
					Log.d("strpart1", strpart1);
					Log.d("strpart2", strpart2);
					Log.d("strpart3", strpart3);

					if (!strpart1.equals(strpart2) && !strpart1.equals(null)
							&& !strpart1.equals(null) && !strpart1.equals("0")) {
						int startwe = Integer.valueOf(parts[0]);
						int endwe = Integer.valueOf(parts[1]);
						if (startwe < endwe) {
							Log.d("startwe", "" + startwe);
							Log.d("endwe", "" + endwe);
							String[] pairs = value.split(",");
							int part1 = Integer.valueOf(pairs[0]);
							int part2 = Integer.valueOf(pairs[1]);
							int part4 = Integer.valueOf(pairs[2]);
							if (part2 <= edbarcode.getText().length()) {
								int part3 = part1 - 1;
								Log.d("part2", "" + part2);
								String substr = getbarcode.substring(part3,
										part2);
								int length = substr.length();
								if (length >= 3) {
									String str = new StringBuilder(substr)
											.insert(substr.length() - part4, ".")
											.toString();
									edweight.setText(str);
									Log.d("length3", "" + length);
									Log.d("str3", str);
									break;
								} else if (length >= 2) {
									String str = new StringBuilder(substr)
											.insert(substr.length() - part4, ".")
											.toString();
									String seg = "0" + str;
									edweight.setText(seg);
									Log.d("str2", str);
									Log.d("length2", "" + length);
									break;
								} else if (length >= 1) {
									String str = substr + ".00";
									Log.d("str1", str);
									edweight.setText(str);
									break;
								}
							} else {
								Log.d("tpart2", "" + part2);
								Toast.makeText(
										getApplicationContext(),
										"Use At least " + part2
												+ " digit barcode  ",
										Toast.LENGTH_SHORT).show();
								edweight.setText("");
								break;
							}
						} else {
							setStartsEndsOn();
							break;
						}
					}

					else {
						setStartsEndsOn();
						break;
					}

				}
			}
		}
	}

	public void setStartsEndsOn() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.barcode_dialog);

		btn_strts = (Button) dialog.findViewById(R.id.btn_strts);
		btn_ends = (Button) dialog.findViewById(R.id.btn_ends);
		getStrtsEnds = (EditText) dialog.findViewById(R.id.getStrtsEnds);

		startson = (EditText) dialog.findViewById(R.id.startson);
		endson = (EditText) dialog.findViewById(R.id.endson);
		
		weight_decimal_minus = (Button) dialog.findViewById(R.id.weight_decimal_minus);
		weight_decimal = (EditText) dialog.findViewById(R.id.weight_decimal);
		weight_decimal_plus = (Button) dialog.findViewById(R.id.weight_decimal_plus);		

		getStrtsEnds.setText(edbarcode.getText().toString());

		weight_decimal_minus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String weightDecimal = weight_decimal.getText().toString();
				if (!weightDecimal.matches("")) {					
					int wghtDecimal = Integer.parseInt(weightDecimal);
					
					if(wghtDecimal!=1){
					weight_decimal.setText(""+(wghtDecimal-1));
					}

				}
			}
		});
		
		weight_decimal_plus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String weightDecimal = weight_decimal.getText().toString();
				if (!weightDecimal.matches("")) {
					int wghtDecimal = Integer.parseInt(weightDecimal);					
					if(wghtDecimal!=3){
					weight_decimal.setText(""+(wghtDecimal+1));
					}
				}
			}
		});
		
		btn_strts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (getStrtsEnds.getText().toString().matches("")) {
					Toast.makeText(AddBarcode.this,
							"Please enter barcode above", Toast.LENGTH_SHORT)
							.show();
					getStrtsEnds.requestFocus();
				}

				else {
					strts = getStrtsEnds.getSelectionStart() + 1;

					stwght = strts;
					startson.setText("" + strts);
				}
			}
		});

		btn_ends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (getStrtsEnds.getText().toString().matches("")) {
					Toast.makeText(AddBarcode.this,
							"Please enter barcode above", Toast.LENGTH_SHORT)
							.show();
					getStrtsEnds.requestFocus();

				}

				else {
					ends = getStrtsEnds.getSelectionStart();
					edwght = ends;
					endson.setText("" + ends);
				}
			}
		});
		
		ok = (Button) dialog.findViewById(R.id.okbutton);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				edbarcode.setText(getStrtsEnds.getText().toString());

				String ston = startson.getText().toString();
				String edon = endson.getText().toString();
				String wghtDecimal = weight_decimal.getText().toString();

				if (!ston.matches("") && !edon.matches("") && !wghtDecimal.matches("")) {
					stwght = Integer.parseInt(ston);
					edwght = Integer.parseInt(edon);
					edwghtdecimal = Integer.parseInt(wghtDecimal);
					
					if (stwght < edwght) {
						
						int wght=edwght-stwght;
						
						if(wght>=edwghtdecimal){
							AsyncCallWSSetWeight task = new AsyncCallWSSetWeight();
							task.execute();
							dialog.dismiss();
						}else{
							Toast.makeText(getApplicationContext(),
									"Please enter valid Barcode weight decimal point",
									Toast.LENGTH_SHORT).show();
						}
	
					} else {
						Toast.makeText(getApplicationContext(),
								"End Weight must be greater than Start Weight",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Not be empty",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		cancel = (Button) dialog.findViewById(R.id.cancelbutton);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.cancel();
			}
		});
		dialog.show();
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

	private class AsyncCallWSSetWeight extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(AddBarcode.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(addproduct_layout, false);
			progressBar = new ProgressBar(AddBarcode.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				if (!edcodefield.getText().toString().matches("")) {
					String proCode = edcodefield.getText().toString();
					NewProductWebService.setWeightService(
							"fncSaveProductWeightBarcode", proCode, stwght,
							edwght,""+edwghtdecimal);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} catch (XmlPullParserException e) {

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(addproduct_layout, true);

			AsyncCallWSADDPRD task = new AsyncCallWSADDPRD();
			task.execute();
//			AsyncCallBARCODE barcodetask = new AsyncCallBARCODE();
//			barcodetask.execute();

		}
	}

	private class AsyncCallWSADDPRD extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();
			barcodeWeightDecimal="";
			spinnerLayout = new LinearLayout(AddBarcode.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(addproduct_layout, false);
			progressBar = new ProgressBar(AddBarcode.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));

			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String productstartend;
			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo companyCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					valid_url);
			try {

				androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				productTxt = response.toString();
				productresult = " { ProductDetails : " + productTxt + "}";
				JSONObject jsonResponse;
				try {

					jsonResponse = new JSONObject(productresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("ProductDetails");
					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						String productcodes = jsonChildNode.optString(
								PRODUCT_CODE).toString();
						String productnames = jsonChildNode.optString(
								PRODUCT_NAME).toString();
						String productweights = jsonChildNode.optString(
								PRODUCT_WEIGHT).toString();
						productstartson = jsonChildNode.optString(
								PRODUCT_STARTSON).toString();
						productendson = jsonChildNode.optString(PRODUCT_ENDSON)
								.toString();
						barcodeWeightDecimal = jsonChildNode.optString("WeightBarcodeDecimalPoints")
								.toString();
		
						if(barcodeWeightDecimal.matches("")){
							barcodeWeightDecimal="2";
						}
						
						if (productstartson.equals("")
								&& productendson.equals("")) {
							productstartend = "0" + "," + "0";
						} else {
							productstartend = productstartson + ","
									+ productendson + "," + barcodeWeightDecimal;
						}

						HashMap<String, String> producthm = new HashMap<String, String>();
						producthm.put(productcodes, productnames);
						al.add(producthm);
						hashmap.putAll(producthm);
						HashMap<String, String> productsplithm = new HashMap<String, String>();
						productsplithm.put(productcodes, productstartend);
						albarsplit.add(productsplithm);
						hmsplitbc.putAll(productsplithm);
						alprodcode.add(productcodes);

						HashMap<String, String> producthmweight = new HashMap<String, String>();
						producthmweight.put(productcodes, productweights);
						productweight.add(producthmweight);
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
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(addproduct_layout, true);
		}
	}

/*	private class AsyncCallBARCODE extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			SoapObject request = new SoapObject(NAMESPACE, webMethNamebar);

			PropertyInfo companyCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					valid_url);
			try {

				androidHttpTransport.call(SOAP_ACTION + webMethNamebar,
						envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				barcodeTxt = response.toString();
				barcoderesult = " { BarCodeDetails : " + barcodeTxt + "}";

				JSONObject jsonResponse;
				try {

					jsonResponse = new JSONObject(barcoderesult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("BarCodeDetails");

					int lengthJsonArr = jsonMainNode.length();

					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						String productbarcode = jsonChildNode.optString(
								PRODUCTNAME_BARCODE).toString();
						String barcode = jsonChildNode.optString(
								PRODUCT_BARCODE).toString();
						HashMap<String, String> barcodehm = new HashMap<String, String>();
						barcodehm.put(productbarcode, barcode);
						albarcode.add(barcodehm);
						albar.add(barcode);

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

		}

	}*/

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		prodid = (TextView) v.findViewById(R.id.idprod);
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		if (item.getTitle() == "Edit") {
			
			edweight.setText("");
			edbarcode.setText("");
			edpieceperqty.setText("");
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			LinearLayout lila1 = new LinearLayout(this);
			lila1.setOrientation(LinearLayout.VERTICAL);
			final EditText editcode = new EditText(this);
			final EditText editname = new EditText(this);
			final EditText editbarcode = new EditText(this);
			final EditText editweight = new EditText(this);
			lila1.addView(editcode);
			lila1.addView(editname);
			editcode.setKeyListener(null);
			editname.setKeyListener(null);
			lila1.addView(editbarcode);
			lila1.addView(editweight);
			editweight.setRawInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			alert.setView(lila1);
			alert.setIcon(R.mipmap.edit);
			alert.setTitle("Edit");
			edit_id = this.cursor.getString(this.cursor.getColumnIndex("_id"));
			ArrayList<HashMap<String, String>> getprodinfo = new ArrayList<HashMap<String, String>>();
			getprodinfo = sqldb.getProductInfo(edit_id);
			Log.d("getprodinfo", "" + getprodinfo);
			for (int i = 0; i < getprodinfo.size(); i++) {
				String arraylistelement = getprodinfo.get(i).toString();
				String les = arraylistelement.replace("{", "");
				String lee = les.replace("}", "");
				String[] pairs = lee.split(",");
				String[] splitbarcode = pairs[0].split("=");

				String finalbarcode = splitbarcode[1];
				editbarcode.setText(finalbarcode);
				Log.d("finalbarcode", finalbarcode);// }

				String[] splitweight = pairs[1].split("=");
				String finalweight = splitweight[1];
				editweight.setText(finalweight);
				Log.d("finalweight", finalweight);

				String[] splitcode = pairs[3].split("=");
				String finalcode = splitcode[1];
				editcode.setText(finalcode);
				Log.d("finalcode", finalcode);

				String[] splitname = pairs[4].split("=");
				String finalname = splitname[1];
				editname.setText(finalname);
				Log.d("finalname", finalname);

				editweight.setFocusable(true);
				editweight.requestFocus();
				editweight.setRawInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);
				mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
			alert.setPositiveButton("Update",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (editbarcode.getText().toString().matches("")
									|| editweight.getText().toString()
											.matches("")) {
							} else {

								HashMap<String, String> queryValues = new HashMap<String, String>();
								queryValues.put("id", edit_id);
								queryValues.put("productcode", editcode
										.getText().toString());
								queryValues.put("productname", editname
										.getText().toString());
								queryValues.put("barcode", editbarcode
										.getText().toString());
								queryValues.put("weight", editweight.getText()
										.toString());
								sqldb.updateProductList(queryValues);

								getListView();

								mgr.toggleSoftInput(0, 0);
							}
							
						}
					});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
							mgr.toggleSoftInput(0, 0);
						}
					});
			alert.show();
		} else if (item.getTitle() == "Delete") {

			edweight.setText("");
			edbarcode.setText("");
			edpieceperqty.setText("");
			
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			sqldb.deleteProds(id);
			if (id != null) {
				ArrayList<String> snoCount = new ArrayList<String>();

				snoCount = sqldb.snoCountID(edproductId.getText().toString());
				for (int i = 0; i < snoCount.size(); i++) {
					int sno = 1 + i;
					HashMap<String, String> queryValues = new HashMap<String, String>();
					queryValues.put("_id", "" + snoCount.get(i));
					queryValues.put("snum", "" + sno);
					sqldb.updateSNUM(queryValues);
				}
			}
			crsrAdptr.notifyDataSetChanged();
			getListView();
			datadb = sqldb.getAllProducts();
			   if(datadb.isEmpty()){
			   sqldb.updateBarcodeStatus(edproductId.getText().toString(),0);
			   }

			Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
			String productid = sqldb.getProd(edproductId.getText().toString());
			  if(productid.matches("")){
				barcode_save.setVisibility(View.INVISIBLE);
			}else{
				barcode_save.setVisibility(View.VISIBLE);
			}
		
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		myalertDialog.dismiss();
		getArraylsit = arrayAdapterProd.getArrayList();
		HashMap<String, String> datavalue = getArraylsit.get(position);
		Set<Entry<String, String>> keys = datavalue.entrySet();
		Iterator<Entry<String, String>> iterator = keys.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Entry mapEntry = iterator.next();
			String keyValues = (String) mapEntry.getKey();
			String values = (String) mapEntry.getValue();
			edcodefield.setText(keyValues);
			ednamefield.setText(values);

			if (!productweight.isEmpty()) {
				for (int i = 0; i < productweight.size(); i++) {
					HashMap<String, String> wghtvalue = productweight.get(i);
					Set<Entry<String, String>> wghtkeys = wghtvalue.entrySet();
					Iterator<Entry<String, String>> iterators = wghtkeys
							.iterator();
					while (iterators.hasNext()) {
						@SuppressWarnings("rawtypes")
						Entry mapEntrys = iterators.next();
						String wghtkey = (String) mapEntrys.getKey();
						String wghtvalues = (String) mapEntrys.getValue();
						if (keyValues.matches(wghtkey)) {
							if (wghtvalues.matches("0.0")
									|| wghtvalues.matches("0.00")) {
								edweight.setText("");
							} else {
								edweight.setText(wghtvalues);
							}
							break;
						}
					}
				}
			}

			edbarcode.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

			imm.toggleSoftInputFromWindow(
					edbarcode.getApplicationWindowToken(),
					InputMethodManager.SHOW_FORCED, 0);

			edcodefield.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {

					textlength = edcodefield.getText().length();
					ednamefield.setText("");
					edweight.setText("");
				}
			});
		}
	}

	private class ProductListAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public ProductListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.addproduct_list_item, cursor);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView paletteid = (TextView) view
					.findViewById(R.id.idprodpalatte);
			paletteid.setVisibility(View.VISIBLE);

			paletteid.setText(serialno);

			TextView snum = (TextView) view.findViewById(R.id.productsno);
			snum.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_SNUM)));

			TextView productcode = (TextView) view
					.findViewById(R.id.productcode);
		
			productcode.setVisibility(View.GONE);

			TextView productname = (TextView) view
					.findViewById(R.id.productname);
			
			productname.setVisibility(View.GONE);

			TextView barcode = (TextView) view
					.findViewById(R.id.productbarcode);
			barcode.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_BARCODE)));

			TextView weight = (TextView) view.findViewById(R.id.productweight);
			weight.setText(""
					+ cursor.getDouble(cursor
							.getColumnIndex(SOTDatabase.COLUMN_WEIGHT)));

		}
	}

	public void clQty() {

		int cartonQty = 0;
		double looseQty = 0;

		String productid = edproductId.getText().toString();
		products_arr = sqldb.getProducts(productid);

		quantity = products_arr.get(0);
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

			String countsno="";
			
//			if(corl==1){	
				int boxes = sqldb.getBoxesSum(productid);
				
				countsno = String.valueOf(boxes);
//			}else{
//				countsno = sqldb.summarypalettecount(productid);
//			}

			if(corl == 1){
				Log.d("loose", "loose calc");
				
				if (!countsno.matches("")) {
					looseQty = Integer.valueOf(countsno);
				}
				cartonQty = 0;
				Log.d("cqty", "" + cartonQty);
				Log.d("lqty", "" + looseQty);
				
			}else{
				if (!countsno.matches("")) {
					cartonQty = Integer.valueOf(countsno);
				}
				looseQty = 0;
				Log.d("cqty", "" + cartonQty);
				Log.d("lqty", "" + looseQty);
			}
			

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

							netTotal1 = subTotal + taxAmount1;
							ProdNetTotal = twoDecimalPoint(netTotal1);
							// sl_netTotal.setText("" + ProdNetTotal);
						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							prodTax = fourDecimalPoint(taxAmount);
							// sl_tax.setText("" + prodTax);

							netTotal = tt + taxAmount;
							ProdNetTotal = twoDecimalPoint(netTotal);
							// sl_netTotal.setText("" + ProdNetTotal);
						}

					} else if (taxtype.matches("Z")) {

						// sl_tax.setText("0.0");
						if (!itemdiscount.matches("")) {
							netTotal1 = subTotal + taxAmount;
							ProdNetTotal = twoDecimalPoint(netTotal1);
							// sl_netTotal.setText("" + ProdNetTotal);
						} else {
							netTotal = tt + taxAmount;
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

		Log.d("Result", "c " + cartonQty + "l " + looseQty + "tt " + Prodtotal
				+ "sbTtl " + sbTtl + "prodTax " + prodTax + "ProdNetTotal "
				+ ProdNetTotal);

		if(barcodefrom.matches("GraSummary")){
			sqldb.updateProductValues1(productid, cartonQty, looseQty, productprice,
					itemdiscount, Prodtotal, sbTtl, prodTax, ProdNetTotal);
		}else{
		sqldb.updateProductValues(productid, cartonQty, looseQty, productprice,
				itemdiscount, Prodtotal, sbTtl, prodTax, ProdNetTotal);
		}

		sqldb.updateBilldiscProductValues(productid, sbTtl);

		if(barcodefrom.matches("InvoiceSummary")){
			callSummary = new Intent(AddBarcode.this,
					InvoiceSummary.class);
		}
		else if(barcodefrom.matches("DeliverySummary")){
			callSummary = new Intent(AddBarcode.this,
					DeliverySummary.class);
		}else if(barcodefrom.matches("GraSummary")){
			callSummary = new Intent(AddBarcode.this,
					GraSummary.class);
		}else if(barcodefrom.matches("TransferSummary")){
			callSummary = new Intent(AddBarcode.this,
					TransferSummary.class);
		}
		startActivity(callSummary);
		AddBarcode.this.finish();
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}
	public void alertDialog() {
		  
		  AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddBarcode.this);
		///  alertDialog.setTitle("Save");
		  alertDialog.setMessage("Do you want to Save");
		 // alertDialog.setIcon(R.drawable.ic_save_img);
		  alertDialog.setPositiveButton("YES",
		    new DialogInterface.OnClickListener() {
		     @Override
		     public void onClick(DialogInterface dialog, int which) {
		      
		    	 HashMap<String, String> queryValues = new HashMap<String, String>();		    	 
		    	 queryValues.put("status", "1");		    	 
		    	 sqldb.updateBarcodeStatus(queryValues);
		    	 
		    	 if(barcodefrom.matches("GraSummary")){
		    		 
		    		  int barcodestatus = 1;
						
						String totalbarcodeweight = sqldb.getProductWeight(edproductId.getText()
								.toString());
						sqldb.updateQty(edproductId.getText()
								.toString(), totalbarcodeweight,barcodestatus);
						Log.d("totalbarcodeweight", ""+totalbarcodeweight);
						
						clQty();
					}else{
						updateSummary();
					}	
		     }
		    });

		  alertDialog.setNegativeButton("NO",
		    new DialogInterface.OnClickListener() {
		     @Override
		     public void onClick(DialogInterface dialog, int which) {
		    	 		  
		    	 Cursor cursor = sqldb.getStatus();
		    	 		    	
		 		if (cursor.moveToFirst()) {
		 			do {
		 				String id = cursor.getString(cursor.getColumnIndex("_id"));
		 				
		 				sqldb.deleteProds(id);
		 				if (id != null) {
		 					ArrayList<String> snoCount = new ArrayList<String>();

		 					snoCount = sqldb.snoCountID(edproductId.getText().toString());
		 					for (int i = 0; i < snoCount.size(); i++) {
		 						int sno = 1 + i;
		 						HashMap<String, String> queryValues = new HashMap<String, String>();
		 						queryValues.put("_id", "" + snoCount.get(i));
		 						queryValues.put("snum", "" + sno);
		 						sqldb.updateSNUM(queryValues);
		 					}
		 				}
		 				crsrAdptr.notifyDataSetChanged();
		 				getListView();
		 				datadb = sqldb.getAllProducts();
		 				   if(datadb.isEmpty()){
		 				   sqldb.updateBarcodeStatus(edproductId.getText().toString(),0);
		 				   }
		 			} while (cursor.moveToNext());
		 		}

		    	 if(barcodefrom.matches("InvoiceSummary")){
						callSummary = new Intent(AddBarcode.this,
								InvoiceSummary.class);
					}
					else if(barcodefrom.matches("DeliverySummary")){
						callSummary = new Intent(AddBarcode.this,
								DeliverySummary.class);
					}else if(barcodefrom.matches("GraSummary")){
						callSummary = new Intent(AddBarcode.this,
								GraSummary.class);
					}else if(barcodefrom.matches("TransferSummary")){
					 callSummary = new Intent(AddBarcode.this,
							 TransferSummary.class);
				 }
				     startActivity(callSummary);
				     AddBarcode.this.finish();
		      dialog.dismiss();
		     }
		    });
		  alertDialog.show();
		 }
	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	@Override
	public void onBackPressed() {
		String productid = sqldb.getProd(edproductId.getText().toString());
		  if(productid.matches("")){

			  if(barcodefrom.matches("InvoiceSummary")){
					callSummary = new Intent(AddBarcode.this,
							InvoiceSummary.class);
				}
				else if(barcodefrom.matches("DeliverySummary")){
					callSummary = new Intent(AddBarcode.this,
							DeliverySummary.class);
				}else if(barcodefrom.matches("GraSummary")){
					callSummary = new Intent(AddBarcode.this,
							GraSummary.class);
				}else if(barcodefrom.matches("TransferSummary")){
				  callSummary = new Intent(AddBarcode.this,
						  TransferSummary.class);
			  }
		   startActivity(callSummary);
		   AddBarcode.this.finish();
		  }
		  else{
			  alertDialog();
		  }
	}
}
