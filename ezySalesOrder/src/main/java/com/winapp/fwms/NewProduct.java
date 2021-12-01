package com.winapp.fwms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.model.BinData;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.WebServiceClass;
import com.winapp.sotdetails.AddCustomer;
import com.winapp.sotdetails.CustomerListActivity;
import com.winapp.sotdetails.ProductStockActivity;

public class NewProduct extends Activity implements OnItemClickListener {
	ImageView back, logout;
	Button save;
	Intent callSummary, calllogout, callproduct, calladdproduct;
	EditText codefield, descriptionview, startson, endson, Categoryfield,
			SubCategoryfield, uom, piecespercarton, weight,binField;
	String descStr, strtsonStr, endsonStr, CtgryfldStr, SubCtgryfldStr, uomStr,
			piecesprcrtnStr,cartonPriceStr, unitStr,binValue,binFldStr;
	int catArrSize;

	ArrayList<String> productCodeArr = new ArrayList<String>();
	ProgressDialog progressDialog;
	final Context context = this;
	String[] catString;
	String[] subCatString;
	String[] uomString;
	String keyValues;
	String uomCode;
	String codeFld,weightFld="0.0";

	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> searchResults;
	ArrayList<HashMap<String, String>> categoryArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> subCategoryArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> uomArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> binArr = new ArrayList<HashMap<String, String>>();

	ArrayList<String> prodCodeArr = new ArrayList<String>();

	HashMap<String, String> loccode = new HashMap<String, String>();

	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	int textlength = 0;
	private AlertDialog myalertDialog = null;

	boolean check_haveBatch = false, check_haveExpire = false,
			check_haveMFD = false;

	CheckBox haveBatch, haveExpire, haveMFD;

	ArrayList<String> barcodeArr = new ArrayList<String>();
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	String valid_url;
	LinearLayout newproduct_layout,weightLabellayout,weightLabel2layout,cartonPriceLayout;

	EditText getStrtsEnds,cartonPrice,units;
	Button btn_strts, btn_ends;
	int strts, ends;
	String prodResult, updateResult,ProductStock;
	Spinner binData;
	ArrayList<BinData> Customer_bin_List = new ArrayList<BinData>();
	ArrayList<BinData> bin_List =new ArrayList<>();
	String bin_jsonString,binPro_jsonString;
	JSONObject bin_jsonResponse,binPro_jsonResponse;
	JSONArray bin_jsonMainNode,binPro_jsonMainNode;
	int select_pos_tax_code=0;
	String binCode ="";
	String bin_Value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newproduct_screen);

		newproduct_layout = (LinearLayout) findViewById(R.id.newproduct_layout);
		cartonPriceLayout = (LinearLayout) findViewById(R.id.cartonPriceLayout);
		getStrtsEnds = (EditText) findViewById(R.id.getStrtsEnds);

		btn_strts = (Button) findViewById(R.id.btn_strts);
		btn_ends = (Button) findViewById(R.id.btn_ends);

		save = (Button) findViewById(R.id.add);

		codefield = (EditText) findViewById(R.id.codefield);
		descriptionview = (EditText) findViewById(R.id.descriptionview);
		startson = (EditText) findViewById(R.id.startson);
		endson = (EditText) findViewById(R.id.endson);
		Categoryfield = (EditText) findViewById(R.id.Categoryfield);
		SubCategoryfield = (EditText) findViewById(R.id.SubCategoryfield);
		uom = (EditText) findViewById(R.id.uom);
		piecespercarton = (EditText) findViewById(R.id.piecespercarton);
		binField =(EditText)findViewById(R.id.binfield);
//		binData =(Spinner)findViewById(R.id.bin);

//		binData.setBackgroundResource(drawable.customer_spinner);
//		binData.setClickable(true);

		cartonPrice  =(EditText) findViewById(R.id.cartonPrice);
		units =(EditText) findViewById(R.id.units);
		
		weight =  (EditText) findViewById(R.id.weightfield);
		
		haveBatch = (CheckBox) findViewById(R.id.checkBox1);
		haveExpire = (CheckBox) findViewById(R.id.checkBox2);
		haveMFD = (CheckBox) findViewById(R.id.checkBox3);

		String ctnPrice = SalesOrderSetGet.getCartonpriceflag();
		if(ctnPrice.matches("1")){
			cartonPriceLayout.setVisibility(View.VISIBLE);
		}else{
			cartonPriceLayout.setVisibility(View.GONE);
		}
		
		codeFld = codefield.getText().toString();
		descStr = descriptionview.getText().toString();
		strtsonStr = startson.getText().toString();
		endsonStr = endson.getText().toString();
		CtgryfldStr = Categoryfield.getText().toString();
		SubCtgryfldStr = SubCategoryfield.getText().toString();
		uomStr = uom.getText().toString();
		piecesprcrtnStr = piecespercarton.getText().toString();
		binFldStr =binField.getText().toString();
		
		cartonPriceStr = cartonPrice.getText().toString();
		unitStr = units.getText().toString();

		weightFld = weight.getText().toString();
		
		if(weightFld.matches("")){
			weightFld="0.0";
		}

		weightLabellayout = (LinearLayout) findViewById (R.id.weightLabellayout);
		weightLabel2layout = (LinearLayout) findViewById (R.id.weightLabel2layout);
		
		String appType = LogOutSetGet.getApplicationType();
		
		if(appType.matches("S")){
			weightLabellayout.setVisibility(View.GONE);
			weightLabel2layout.setVisibility(View.GONE);
		} else if(appType.matches("W")){
			weightLabellayout.setVisibility(View.VISIBLE);
			weightLabel2layout.setVisibility(View.VISIBLE);
		}


		btn_strts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (getStrtsEnds.getText().toString().matches("")) {
					Toast.makeText(NewProduct.this,
							"Please enter barcode above", Toast.LENGTH_SHORT)
							.show();
					getStrtsEnds.requestFocus();
				} 

				else {
					strts = getStrtsEnds.getSelectionStart() + 1;
					startson.setText("" + strts);
				}
			}
		});

		btn_ends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (getStrtsEnds.getText().toString().matches("")) {
					Toast.makeText(NewProduct.this,
							"Please enter barcode above", Toast.LENGTH_SHORT)
							.show();
					getStrtsEnds.requestFocus();
					
				} 
	
				else {
					ends = getStrtsEnds.getSelectionStart();
					endson.setText("" + ends);
				}
			}
		});

//		binData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//					@Override
//					public void onItemSelected(AdapterView<?> parentView,
//											   View selectedItemView, int position, long id) {
//						select_pos_tax_code	=position;
//						binCode=Customer_bin_List.get(position).getBinCode();
//					}
//
//					@Override
//					public void onNothingSelected(AdapterView<?> parent) {
//					}
//				});

		FWMSSettingsDatabase.init(NewProduct.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new NewProductWebService(valid_url);

		prodCodeArr.clear();
		categoryArr.clear();
		subCategoryArr.clear();
		uomArr.clear();
		binArr.clear();


		Bundle extras = getIntent().getExtras();

		if (extras != null) {

			ClearFieldSetterGetter.setClearProduct(false);
			ClearFieldSetterGetter.setClearBarcode(false);


			save.setText("Update");
			AddProductSetterGetter.setUpdate(true);
			String pCode = extras.getString("ProductCode");

			codefield.setText(extras.getString("ProductCode"));
			descriptionview.setText(extras.getString("ProductName"));
			Log.d("descriptionview","-->"+extras.getString("ProductName"));
			String haveBtch = extras.getString("HaveBatch");
			String haveExpry = extras.getString("HaveExpiry");
			String haveMfgDt = extras.getString("HaveMfgDate");
//				String bin_Value = extras.getString("binvalue");

			if (haveBtch.matches("true")) {
				haveBatch.setChecked(true);
				check_haveBatch = true;

			} else {
				haveBatch.setChecked(false);
				check_haveBatch = false;
			}

			if (haveExpry.matches("true")) {
				haveExpire.setChecked(true);
				check_haveExpire = true;
			} else {
				haveExpire.setChecked(false);
				check_haveExpire = false;
			}

			if (haveMfgDt.matches("true")) {
				haveMFD.setChecked(true);
				check_haveMFD = true;
			} else {
				haveMFD.setChecked(false);
				check_haveMFD = false;
			}

			Log.d("taxcodecheck","--->"+bin_Value+ "-->"+extras.getString("ProductName"));
//			for (int i = 0; i < Customer_bin_List.size(); i++) {
//
//				if (Customer_bin_List.get(i).getBinCode().matches(bin_Value)) {
//					select_pos_tax_code = i;
//				}
//			}
//				binData.setSelection(select_pos_tax_code);

			startson.setText(extras.getString("WeightBarcodeStartsOn"));
			endson.setText(extras.getString("WeightBarcodeEndsOn"));
			Categoryfield.setText(extras.getString("CategoryCode"));
			SubCategoryfield.setText(extras.getString("SubCategoryCode"));
			uom.setText(extras.getString("UOMCode"));
			piecespercarton.setText(extras.getString("PcsPerCarton"));
			weight.setText(extras.getString("weight"));

			cartonPrice.setText(extras.getString("cartonPrice"));
			units.setText(extras.getString("units"));
			binField.setText(extras.getString("binvalue"));

			AddProductSetterGetter.setCodeFld(codefield.getText().toString());
			AddProductSetterGetter.setDescStr(descriptionview.getText()
					.toString());
			AddProductSetterGetter.setUomStr(uom.getText().toString());
			AddProductSetterGetter.setStrtsonStr(startson.getText().toString());
			AddProductSetterGetter.setEndsonStr(endson.getText().toString());
			AddProductSetterGetter.setCtgryfldStr(Categoryfield.getText()
					.toString());
			AddProductSetterGetter.setSubCtgryfldStr(SubCategoryfield.getText()
					.toString());
			AddProductSetterGetter.setPiecesprcrtnStr(piecespercarton.getText()
					.toString());
			AddProductSetterGetter.setCheck_haveBatch(check_haveBatch);
			AddProductSetterGetter.setCheck_haveExpire(check_haveExpire);
			AddProductSetterGetter.setCheck_haveMFD(check_haveMFD);

			AddProductSetterGetter.setCartonPrice(cartonPrice.getText().toString());
			AddProductSetterGetter.setUnits(units.getText().toString());
//				AddProductSetterGetter.setBinCode(bin_Value);
			AddProductSetterGetter.setWeight(weightFld);
			AddProductSetterGetter.setBinCode(binField.getText().toString());
		}

		AsyncCallWSGetProCode task = new AsyncCallWSGetProCode();
		task.execute();

		AsyncCallWSCat cat = new AsyncCallWSCat();
		cat.execute();

		AsyncCallWSSubCat subCat = new AsyncCallWSSubCat();
		subCat.execute();

		AsyncBinDetails bin=new AsyncBinDetails();
		bin.execute();

//		CustomersyncCall customerservice = new CustomersyncCall();
//		customerservice.execute();

		AsyncCallWSUom um = new AsyncCallWSUom();
		um.execute();
		
		codefield.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setCodeFld(codefield.getText()
						.toString());
				boolean res = false;
				for (String alphabet : prodCodeArr) {
					if (alphabet.toLowerCase().equals(
							codefield.getText().toString().toLowerCase())) {
						res = true;
						break;
					}
				}
				if (res == true) {
					AddProductSetterGetter.setCheck_invalid(true);
					Log.d("invalid", "yes");
				} else {
					AddProductSetterGetter.setCheck_invalid(false);
					Log.d("invalid", "no");
				}
				
			}
		});

		descriptionview.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setDescStr(descriptionview.getText()
						.toString());
			}

		});
		
		weight.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setWeight(weight.getText().toString());
			}

		});
		

		uom.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setUomStr(uom.getText().toString());
			}

		});

		startson.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setStrtsonStr(startson.getText()
						.toString());
			}

		});

		endson.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter
						.setEndsonStr(endson.getText().toString());
			}

		});

		Categoryfield.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setCtgryfldStr(Categoryfield.getText()
						.toString());
			}

		});

		SubCategoryfield.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setSubCtgryfldStr(SubCategoryfield
						.getText().toString());
			}

		});

		piecespercarton.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setPiecesprcrtnStr(piecespercarton
						.getText().toString());
			}

		});

		haveBatch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					check_haveBatch = true;
					AddProductSetterGetter.setCheck_haveBatch(check_haveBatch);

				} else {
					check_haveBatch = false;
				}

			}
		});

		haveExpire.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					check_haveExpire = true;
					AddProductSetterGetter
							.setCheck_haveExpire(check_haveExpire);

				} else {
					check_haveExpire = false;
				}

			}
		});

		haveMFD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (((CheckBox) v).isChecked()) {
					check_haveMFD = true;
					AddProductSetterGetter.setCheck_haveMFD(check_haveMFD);
				} else {
					check_haveMFD = false;
				}

			}
		});
		
		cartonPrice.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setCartonPrice(cartonPrice
						.getText().toString());
			}

		});
		
		units.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setUnits(units
						.getText().toString());
			}

		});


		binField.addTextChangedListener(new TextWatcher() {

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
				AddProductSetterGetter.setBinCode(binField
						.getText().toString());
			}

		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
		
				codeFld = codefield.getText().toString();
				descStr = descriptionview.getText().toString();
				strtsonStr = startson.getText().toString();
				endsonStr = endson.getText().toString();
				CtgryfldStr = Categoryfield.getText().toString();
				SubCtgryfldStr = SubCategoryfield.getText().toString();
				uomStr = uom.getText().toString();
				piecesprcrtnStr = piecespercarton.getText().toString();
//				binValue = binCode;
				cartonPriceStr = cartonPrice.getText().toString();
				unitStr = units.getText().toString();
				binFldStr =binField.getText().toString();

				weightFld = weight.getText().toString();
				
				if(weightFld.matches("")){
					weightFld="0.0";
				}
				
				AsyncCallWSProductCode task = new AsyncCallWSProductCode();
				task.execute();

			}
		});

		Categoryfield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						Categoryfield) {

					@Override
					public boolean onDrawableClick() {
					
						AlertDialog.Builder myDialog = new AlertDialog.Builder(
								NewProduct.this);
						final EditText editText = new EditText(NewProduct.this);
						final ListView listview = new ListView(NewProduct.this);
						LinearLayout layout = new LinearLayout(NewProduct.this);
						layout.setOrientation(LinearLayout.VERTICAL);
						myDialog.setTitle("Category");
						editText.setCompoundDrawablesWithIntrinsicBounds(
								R.mipmap.search, 0, 0, 0);
						layout.addView(editText);
						layout.addView(listview);
						myDialog.setView(layout);
						
						 editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					         @Override
					         public void onFocusChange(View v, boolean hasFocus) {
					             if (hasFocus) {
					              myalertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					             }
					         }
					     });
						
						arrayAdapterProd = new CustomAlertAdapterProd(
								NewProduct.this, categoryArr);
						listview.setAdapter(arrayAdapterProd);
						listview.setOnItemClickListener(NewProduct.this);

						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> adapter,
									View v, int position, long arg3) {
							
								myalertDialog.dismiss();
								getArraylsit = arrayAdapterProd.getArrayList();
							
								HashMap<String, String> datavalue = getArraylsit
										.get(position);
								Set<Entry<String, String>> keys = datavalue
										.entrySet();
								Iterator<Entry<String, String>> iterator = keys
										.iterator();
								while (iterator.hasNext()) {
									@SuppressWarnings("rawtypes")
									Entry mapEntry = iterator
											.next();
									keyValues = (String) mapEntry.getKey();									
									Categoryfield.setText(keyValues);									

								}
							}
						});

						searchResults = new ArrayList<HashMap<String, String>>(
								categoryArr);
						editText.addTextChangedListener(new TextWatcher() {
							@Override
							public void afterTextChanged(Editable s) {

							}

							@Override
							public void beforeTextChanged(CharSequence s,
									int start, int count, int after) {

							}

							@Override
							public void onTextChanged(CharSequence s,
									int start, int before, int count) {
								textlength = editText.getText().length();
								searchResults.clear();
								for (int i = 0; i < categoryArr.size(); i++) {
									String supplierName = categoryArr.get(i)
											.toString();
									if (textlength <= supplierName.length()) {
										if (supplierName.toLowerCase()
												.contains(
														editText.getText()
																.toString()
																.toLowerCase()
																.trim()))
											searchResults.add(categoryArr
													.get(i));
									}
								}
							
								arrayAdapterProd = new CustomAlertAdapterProd(
										NewProduct.this, searchResults);
								listview.setAdapter(arrayAdapterProd);

							}
						});
						myDialog.setNegativeButton("cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

						myalertDialog = myDialog.show();
						return true;

					}
				});

		SubCategoryfield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						SubCategoryfield) {

					@Override
					public boolean onDrawableClick() {
						// TODO Auto-generated method stub

						AlertDialog.Builder myDialog = new AlertDialog.Builder(
								NewProduct.this);
						final EditText editText = new EditText(NewProduct.this);
						final ListView listview = new ListView(NewProduct.this);
						LinearLayout layout = new LinearLayout(NewProduct.this);
						layout.setOrientation(LinearLayout.VERTICAL);
						myDialog.setTitle("Sub Category");
						editText.setCompoundDrawablesWithIntrinsicBounds(
								R.mipmap.search, 0, 0, 0);
						layout.addView(editText);
						layout.addView(listview);
						myDialog.setView(layout);

						 editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					         @Override
					         public void onFocusChange(View v, boolean hasFocus) {
					             if (hasFocus) {
					              myalertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					             }
					         }
					     });

						arrayAdapterProd = new CustomAlertAdapterProd(
								NewProduct.this, subCategoryArr);
						listview.setAdapter(arrayAdapterProd);
						listview.setOnItemClickListener(NewProduct.this);

						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> adapter,
									View v, int position, long arg3) {

								myalertDialog.dismiss();
								getArraylsit = arrayAdapterProd.getArrayList();

								HashMap<String, String> datavalue = getArraylsit
										.get(position);
								Set<Entry<String, String>> keys = datavalue
										.entrySet();
								Iterator<Entry<String, String>> iterator = keys
										.iterator();
								while (iterator.hasNext()) {
									@SuppressWarnings("rawtypes")
									Entry mapEntry = iterator
											.next();
									keyValues = (String) mapEntry.getKey();
									SubCategoryfield.setText(keyValues);

								}
							}
						});

						searchResults = new ArrayList<HashMap<String, String>>(
								subCategoryArr);
						editText.addTextChangedListener(new TextWatcher() {
							@Override
							public void afterTextChanged(Editable s) {

							}

							@Override
							public void beforeTextChanged(CharSequence s,
									int start, int count, int after) {

							}

							@Override
							public void onTextChanged(CharSequence s,
									int start, int before, int count) {
								textlength = editText.getText().length();
								searchResults.clear();
								for (int i = 0; i < subCategoryArr.size(); i++) {
									String supplierName = subCategoryArr.get(i)
											.toString();
									if (textlength <= supplierName.length()) {
										if (supplierName.toLowerCase()
												.contains(
														editText.getText()
																.toString()
																.toLowerCase()
																.trim()))
											searchResults.add(subCategoryArr
													.get(i));
									}
								}

								arrayAdapterProd = new CustomAlertAdapterProd(
										NewProduct.this, searchResults);
								listview.setAdapter(arrayAdapterProd);
							}
						});
						myDialog.setNegativeButton("cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

						myalertDialog = myDialog.show();
						return true;

					}
				});


		binField
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						binField) {

					@Override
					public boolean onDrawableClick() {
						// TODO Auto-generated method stub

						AlertDialog.Builder myDialog = new AlertDialog.Builder(
								NewProduct.this);
						final EditText editText = new EditText(NewProduct.this);
						final ListView listview = new ListView(NewProduct.this);
						LinearLayout layout = new LinearLayout(NewProduct.this);
						layout.setOrientation(LinearLayout.VERTICAL);
						myDialog.setTitle("Bin Details");
						editText.setCompoundDrawablesWithIntrinsicBounds(
								R.mipmap.search, 0, 0, 0);
						layout.addView(editText);
						layout.addView(listview);
						myDialog.setView(layout);

						editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (hasFocus) {
									myalertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
								}
							}
						});

						arrayAdapterProd = new CustomAlertAdapterProd(
								NewProduct.this, binArr);
						listview.setAdapter(arrayAdapterProd);
						listview.setOnItemClickListener(NewProduct.this);

						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> adapter,
													View v, int position, long arg3) {

								myalertDialog.dismiss();
								getArraylsit = arrayAdapterProd.getArrayList();

								HashMap<String, String> datavalue = getArraylsit
										.get(position);
								Set<Entry<String, String>> keys = datavalue
										.entrySet();
								Iterator<Entry<String, String>> iterator = keys
										.iterator();
								while (iterator.hasNext()) {
									@SuppressWarnings("rawtypes")
									Entry mapEntry = iterator
											.next();
									keyValues = (String) mapEntry.getKey();
									binField.setText(keyValues);

								}
							}
						});

						searchResults = new ArrayList<HashMap<String, String>>(
								binArr);
						editText.addTextChangedListener(new TextWatcher() {
							@Override
							public void afterTextChanged(Editable s) {

							}

							@Override
							public void beforeTextChanged(CharSequence s,
														  int start, int count, int after) {

							}

							@Override
							public void onTextChanged(CharSequence s,
													  int start, int before, int count) {
								textlength = editText.getText().length();
								searchResults.clear();
								for (int i = 0; i < binArr.size(); i++) {
									String supplierName = binArr.get(i)
											.toString();
									if (textlength <= supplierName.length()) {
										if (supplierName.toLowerCase()
												.contains(
														editText.getText()
																.toString()
																.toLowerCase()
																.trim()))
											searchResults.add(binArr
													.get(i));
									}
								}

								arrayAdapterProd = new CustomAlertAdapterProd(
										NewProduct.this, searchResults);
								listview.setAdapter(arrayAdapterProd);
							}
						});
						myDialog.setNegativeButton("cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										dialog.dismiss();
									}
								});

						myalertDialog = myDialog.show();
						return true;

					}
				});

		uom.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				uom) {

			@Override
			public boolean onDrawableClick() {
				// TODO Auto-generated method stub

				AlertDialog.Builder myDialog = new AlertDialog.Builder(
						NewProduct.this);
				final EditText editText = new EditText(NewProduct.this);
				final ListView listview = new ListView(NewProduct.this);
				LinearLayout layout = new LinearLayout(NewProduct.this);
				layout.setOrientation(LinearLayout.VERTICAL);
				myDialog.setTitle("UOM Code");
				editText.setCompoundDrawablesWithIntrinsicBounds(
						R.mipmap.search, 0, 0, 0);
				layout.addView(editText);
				layout.addView(listview);
				myDialog.setView(layout);
				
				 editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			         @Override
			         public void onFocusChange(View v, boolean hasFocus) {
			             if (hasFocus) {
			              myalertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			             }
			         }
			     });
				
				arrayAdapterProd = new CustomAlertAdapterProd(NewProduct.this,
						uomArr);
				listview.setAdapter(arrayAdapterProd);
				listview.setOnItemClickListener(NewProduct.this);

				listview.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View v,
							int position, long arg3) {
						
						myalertDialog.dismiss();
						getArraylsit = arrayAdapterProd.getArrayList();
						
						HashMap<String, String> datavalue = getArraylsit
								.get(position);
						Set<Entry<String, String>> keys = datavalue.entrySet();
						Iterator<Entry<String, String>> iterator = keys
								.iterator();
						while (iterator.hasNext()) {
							@SuppressWarnings("rawtypes")
							Entry mapEntry = iterator.next();
							keyValues = (String) mapEntry.getKey();
							uom.setText(keyValues);
						
						}
					}
				});

				searchResults = new ArrayList<HashMap<String, String>>(uomArr);
				editText.addTextChangedListener(new TextWatcher() {
					@Override
					public void afterTextChanged(Editable s) {

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						textlength = editText.getText().length();
						searchResults.clear();
						for (int i = 0; i < uomArr.size(); i++) {
							String supplierName = uomArr.get(i).toString();
							if (textlength <= supplierName.length()) {
								if (supplierName.toLowerCase().contains(
										editText.getText().toString()
												.toLowerCase().trim()))
									searchResults.add(uomArr.get(i));
							}
						}

						arrayAdapterProd = new CustomAlertAdapterProd(
								NewProduct.this, searchResults);
						listview.setAdapter(arrayAdapterProd);
					}
				});
				myDialog.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				myalertDialog = myDialog.show();
				return true;

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

	private class AsyncCallWSCat extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			categoryArr = NewProductWebService
					.categoryService("fncGetCategory");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}

	private class AsyncCallWSSubCat extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			subCategoryArr = NewProductWebService
					.subCategoryService("fncGetSubCategory");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}

	private class AsyncBinDetails extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... voids) {

			binArr = NewProductWebService.binDetailService("fncGetBinDetails");

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
		}
	}

	private class AsyncCallWSProCode extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(NewProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(newproduct_layout, false);
			progressBar = new ProgressBar(NewProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			try {

				if (save.getText().toString().matches("Update")) {
					codeFld = codefield.getText().toString();
					descStr = descriptionview.getText().toString();
					Log.d("descStr","-->"+descStr);
					strtsonStr = startson.getText().toString();
					endsonStr = endson.getText().toString();
					CtgryfldStr = Categoryfield.getText().toString();
					SubCtgryfldStr = SubCategoryfield.getText().toString();
					uomStr = uom.getText().toString();
					piecesprcrtnStr = piecespercarton.getText().toString();
//					binValue =binCode;
					binFldStr =binField.getText().toString();


					cartonPriceStr = cartonPrice.getText().toString();
					unitStr = units.getText().toString();


					weightFld = weight.getText().toString();
					
					if(weightFld.matches("")){
						weightFld="0.0";
					}
										
					prodResult = NewProductWebService.addProductService(
							"fncSaveProduct", codeFld, descStr, strtsonStr,
							endsonStr, CtgryfldStr, SubCtgryfldStr, uomStr,
							piecesprcrtnStr, check_haveBatch, check_haveExpire,
							check_haveMFD,weightFld,cartonPriceStr,unitStr,binFldStr);
				} else {
					prodResult = NewProductWebService.addProductService(
							"fncSaveProduct", codeFld, descStr, strtsonStr,
							endsonStr, CtgryfldStr, SubCtgryfldStr, uomStr,
							piecesprcrtnStr, check_haveBatch, check_haveExpire,
							check_haveMFD,weightFld,cartonPriceStr,unitStr,binFldStr);
				}

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
				Toast.makeText(getApplicationContext(),
						"Product Saved Successfully", Toast.LENGTH_SHORT)
						.show();
				ClearFieldSetterGetter.setClearBarcode(true);
				AddProductSetterGetter.setUpdate(false);
				codefield.setText("");
				descriptionview.setText("");
				startson.setText("");
				endson.setText("");
				Categoryfield.setText("");
				SubCategoryfield.setText("");
				uom.setText("");
				piecespercarton.setText("");
				getStrtsEnds.setText("");
				weight.setText("");
				haveBatch.setChecked(false);
				haveExpire.setChecked(false);
				haveMFD.setChecked(false);
//				binData.setBackgroundResource(drawable.customer_spinner_disabled);
//				binData.setClickable(false);
				cartonPrice.setText("");
				units.setText("");
				binField.setText("");
				
				save.setText("Save");
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(newproduct_layout, true);

		}
	}

	private class AsyncCallWSProductCode extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(NewProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(newproduct_layout, false);
			progressBar = new ProgressBar(NewProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			productCodeArr = NewProductWebService
					.getproductCode("fncGetProductForSearch");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			int srtsOn=0,edsOn=0;
			if (!strtsonStr.matches("")) {

				srtsOn = Integer.parseInt(startson.getText().toString());
				
			}
			if (!endsonStr.matches("")) {
			
				edsOn = Integer.parseInt(endson.getText().toString());
				
			}
			
			if (descStr.matches("")) {

				Toast.makeText(getApplicationContext(), "Enter Description",
						Toast.LENGTH_SHORT).show();
			} 
			else if (!codeFld.matches("") && codeFld.length()>50) {

				Toast.makeText(getApplicationContext(), "Product code is not greater than 50 character",
						Toast.LENGTH_LONG).show();
			} 			
		    else if (srtsOn > edsOn) {
				Toast.makeText(NewProduct.this,
						"Ends On must be greater than starts On",
						Toast.LENGTH_SHORT).show();		
		    }

			else if (!codeFld.matches("")) {

				if (save.getText().toString().matches("Update")) {
					progressBar.setVisibility(View.GONE);
					spinnerLayout.setVisibility(View.GONE);
					enableViews(newproduct_layout, true);
					AsyncCallWSProCode task1 = new AsyncCallWSProCode();
					task1.execute();
				} else {
					boolean res = false;
					for (String alphabet : productCodeArr) {
						if (alphabet.toLowerCase()
								.equals(codeFld.toLowerCase())) {
							
							res = true;
							break;
						}
					}

					if (res == true) {
						Toast.makeText(getApplicationContext(),
								"Product code already exist",
								Toast.LENGTH_SHORT).show();
					} else {
						progressBar.setVisibility(View.GONE);
						spinnerLayout.setVisibility(View.GONE);
						enableViews(newproduct_layout, true);
						AsyncCallWSProCode task1 = new AsyncCallWSProCode();
						task1.execute();
					}
				}
			} else {
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(newproduct_layout, true);
				AsyncCallWSProCode task1 = new AsyncCallWSProCode();
				task1.execute();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(newproduct_layout, true);
		}
	}

	private class AsyncCallWSUom extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			uomArr = NewProductWebService.uomService("fncGetUom");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			for (HashMap<String, String> map : uomArr)
			     for (Entry<String, String> mapEntry : map.entrySet())
			        {
			        String key = mapEntry.getKey();
			        String value = mapEntry.getValue();
			        
			        if(value.matches("kilo") || value.matches("Kilo")){
			        uom.setText(key);
			        }
			        }
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	private class AsyncCallWSGetProCode extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			prodCodeArr = NewProductWebService.getproductCode("fncGetProductForSearch");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}

	@Override
	public void onResume() {
		super.onResume();

		boolean upd = AddProductSetterGetter.isUpdate();
		if (upd == true) {
			save.setText("Update");
		} else {
			save.setText("Save");
		}

		boolean clearProd = ClearFieldSetterGetter.isClearProduct();
		Log.d("clearProd", "" + clearProd);
		if (clearProd == true) {
			codefield.setText("");
			descriptionview.setText("");
			startson.setText("");
			endson.setText("");
			Categoryfield.setText("");
			SubCategoryfield.setText("");
			uom.setText("");
			piecespercarton.setText("1");
			getStrtsEnds.setText("");
			weight.setText("");
			haveBatch.setChecked(false);
			haveExpire.setChecked(false);
			haveMFD.setChecked(false);
			cartonPrice.setText("");
			units.setText("");
			binField.setText("");
			ClearFieldSetterGetter.setClearProduct(false);
		}
	}

	@Override
	public void onBackPressed() {
		
		String addProd=LogOutSetGet.getAddProduct();
		
		if(addProd.matches("ProductStock")){
			Intent i = new Intent(NewProduct.this, ProductStockActivity.class);
			startActivity(i);
			NewProduct.this.finish();
		}else if (addProd.matches("StockIn")){
			Intent i = new Intent(NewProduct.this, AddProduct.class);
			startActivity(i);
			NewProduct.this.finish();
		}		
	}




//	private class CustomersyncCall extends AsyncTask<Void, Void, Void> {
//		String proCode;
//
//
//		@Override
//		protected void onPreExecute() {
//			Customer_bin_List.clear();
//			bin_jsonString ="";
//		}
//
//		@Override
//		protected Void doInBackground(Void... voids) {
//			bin_jsonString = WebServiceClass
//					.URLService("fncGetBinDetails");
//
//			try{
//				bin_jsonResponse = new JSONObject(bin_jsonString);
//				bin_jsonMainNode = bin_jsonResponse
//						.optJSONArray("JsonArray");
//				BinData taxData=new BinData();
//				taxData.setBinCode("0");
//				taxData.setBinName("Select");
//				Customer_bin_List.add(taxData);
//
//
//				int lengthJsonArr1 = bin_jsonMainNode.length();
//				for (int i = 0; i < lengthJsonArr1; i++) {
//					/****** Get Object for each JSON node. ***********/
//					JSONObject termcode_jsonChildNode;
//					try {
//
//						termcode_jsonChildNode = bin_jsonMainNode
//								.getJSONObject(i);
//
//						BinData binData =new BinData();
//
//						String binCode = termcode_jsonChildNode
//								.optString("BinCode").toString();
//						String binName =termcode_jsonChildNode.optString("BinName");
//						Log.d("BinCode","-->"+binName +" ----"+binCode);
//						binData.setBinCode(binCode);
//						binData.setBinName(binName);
//						Customer_bin_List.add(binData);
//
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//
//			}catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void aVoid) {
//			Log.d("Customer_bin_List","-->"+Customer_bin_List.size());
//			if (Customer_bin_List.size() != 0) {
//				binData.setAdapter(new CustomAdapter(NewProduct.this,
//						R.layout.row, Customer_bin_List));
//			}
//
//
//
//
//		}
//	}
//
//
//	public class CustomAdapter extends ArrayAdapter<BinData> {
//
//		ArrayList<BinData> adapterList = new ArrayList<BinData>();
//
//		public CustomAdapter(Context context, int textViewResourceId,
//							 ArrayList<BinData> objects) {
//			super(context,textViewResourceId,objects);
//			this.adapterList.clear();
//			this.adapterList = objects;
//
//		}
//
//		@Override
//		public View getDropDownView(int position, View convertView,
//									ViewGroup parent) {
//			return getCustomView(position, convertView, parent);
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			return getCustomView(position, convertView, parent);
//		}
//
//		public View getCustomView(int position, View convertView,
//								  ViewGroup parent) {
//			LayoutInflater inflater = getLayoutInflater();
//			View row = inflater.inflate(R.layout.row, parent, false);
//			BinData taxData = adapterList.get(position);
//			TextView label = (TextView) row.findViewById(R.id.locationspinner);
//			ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
//			Log.d("getTaxnameDta",""+taxData.getBinName());
//			label.setText(taxData.getBinName());
//			icon.setVisibility(View.GONE);
//			return row;
//		}
//	}



}
