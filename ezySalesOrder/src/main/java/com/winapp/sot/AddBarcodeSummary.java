package com.winapp.sot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SetStockInDetail;

public class AddBarcodeSummary extends ListActivity {
	private ImageView back, logout;
	private Intent callSummary;
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader, prodPalette;
	private Button ok;
	private HashMap<String, List<String>> listDataChild;
	private SOTDatabase sqldb;
	private ArrayList<String> arraList, products_arr;
	private String header, priceflag/* ,prodcode,prodname,sno,productid */,
			barcodefrom, valid_url, quantity, cprice, productprice, taxtype,
			taxvalue, pieceperqty, itemdiscount = "";
	private LinearLayout headerlayout;
	private Cursor cursor;
	private TextView mEmpty;
	int corl=0;
	AlertDialog levelDialog;
	ImageButton carton_loose;
	// Strings to Show In Dialog with Radio Buttons
	final CharSequence[] items = {" Carton "," Loose "};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addbarcode_summary);

		back = (ImageView) findViewById(R.id.back);
		ok = (Button) findViewById(R.id.ok);
		headerlayout = (LinearLayout) findViewById(R.id.headerlayout);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		carton_loose = (ImageButton) findViewById(R.id.carton_loose);
		sqldb = new SOTDatabase(AddBarcodeSummary.this);
		FWMSSettingsDatabase.init(AddBarcodeSummary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SetStockInDetail(valid_url);
		products_arr = new ArrayList<String>();

		prodPalette = new ArrayList<String>();
		arraList = new ArrayList<String>();
		prodPalette.clear();
		products_arr.clear();
		arraList.clear();
		header = SalesOrderSetGet.getHeader_flag();
		Bundle b = getIntent().getExtras();

		if (b != null) {
			barcodefrom = b.getString("Barcodefrom");
			/*
			 * prodcode = b.getString("SOT_ssproductcode"); prodname =
			 * b.getString("SOT_str_ssprodname"); sno =
			 * b.getString("SOT_str_ssno"); productid = b.getString("SOT_ssid");
			 */

		}
		cursor = sqldb.getBarcodeCursor();
		if (cursor.getCount() == 0) {
			ok.setVisibility(View.GONE);
			headerlayout.setVisibility(View.GONE);
		} else {
			prepareListData();
			expListView.setAdapter(listAdapter);
			listAdapter.notifyDataSetChanged();

			expListView.setOnGroupClickListener(new OnGroupClickListener() {

				@Override
				public boolean onGroupClick(ExpandableListView parent, View v,
						int groupPosition, long id) {
					return false;
				}
			});

			expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

				@Override
				public void onGroupExpand(int groupPosition) {
					prepareListData();
					listAdapter.notifyDataSetChanged();
				}
			});

			expListView
					.setOnGroupCollapseListener(new OnGroupCollapseListener() {

						@Override
						public void onGroupCollapse(int groupPosition) {

							prepareListData();
							listAdapter.notifyDataSetChanged();
						}
					});
			expListView
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

								TextView lblListId = (TextView) view
										.findViewById(R.id.lblListId);

								TextView lblListSno = (TextView) view
										.findViewById(R.id.lblListSno);
								TextView lblListproductcode = (TextView) view
										.findViewById(R.id.lblListproductcode);
								TextView lblListproductname = (TextView) view
										.findViewById(R.id.lblListProductName);
								String ids = lblListId.getText().toString();
								String sno = lblListSno.getText().toString();
								String cqty = sqldb.barcodeCQty(ids);
								String itemremarks = sqldb.getItemRemarks(ids);

								Log.d("itemremarks", "--" + itemremarks);
							
								alertDialog(
										ids,
										sno,
										lblListproductcode.getText().toString(),
										lblListproductname.getText().toString(),
										cqty, itemremarks);
							}
							return false;
						}
					});
			// Listview on child click listener
			expListView.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					// TODO Auto-generated method stub

					return false;
				}
			});

		}
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (barcodefrom.matches("InvoiceSummary")) {
					callSummary = new Intent(AddBarcodeSummary.this,
							InvoiceSummary.class);
				} else if (barcodefrom.matches("DeliverySummary")) {
					callSummary = new Intent(AddBarcodeSummary.this,
							DeliverySummary.class);
				} else if (barcodefrom.matches("GraSummary")) {
					callSummary = new Intent(AddBarcodeSummary.this,
							GraSummary.class);
				}else if(barcodefrom.matches("TransferSummary")){
					callSummary = new Intent(AddBarcodeSummary.this,
							TransferSummary.class);
				}
				startActivity(callSummary);
				AddBarcodeSummary.this.finish();
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (barcodefrom.matches("InvoiceSummary")) {
					callSummary = new Intent(AddBarcodeSummary.this,
							InvoiceSummary.class);
				} else if (barcodefrom.matches("DeliverySummary")) {
					callSummary = new Intent(AddBarcodeSummary.this,
							DeliverySummary.class);
				}else if (barcodefrom.matches("GraSummary")) {
					callSummary = new Intent(AddBarcodeSummary.this,
							GraSummary.class);
				}else if(barcodefrom.matches("TransferSummary")){
					callSummary = new Intent(AddBarcodeSummary.this,
							TransferSummary.class);
				}
				startActivity(callSummary);
				AddBarcodeSummary.this.finish();
			}
		});
		
		carton_loose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cartonloosealert();
			
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
	
	public void testing() {
		String barcode_json = "";
		ArrayList<String> productid = new ArrayList<String>();
		ArrayList<ProductBarcode> allproducts = new ArrayList<ProductBarcode>();
		allproducts.clear();
		productid.clear();
	
		productid = sqldb.getBarcodeStatus();

		if (!productid.isEmpty()) {

			for (int i = 0; i < productid.size(); i++) {
				String prodtid = productid.get(i);
				allproducts = sqldb.getAllprodValues(prodtid);
				String productSno = SOTDatabase.getProductSno(prodtid);
				for (ProductBarcode prodbarcode : allproducts) {

					String prodcode = prodbarcode.getProductcode();
					String prodseqno = prodbarcode.getSeqno();
					String prodweightbarcode = prodbarcode.getBarcode();
					double prodweight = prodbarcode.getWeight();

					barcode_json += productSno + "^" + prodcode + "^"
							+ prodseqno + "^" + prodweightbarcode + "^"
							+ prodweight + "^" + "!";
				}
			}

			Log.d("barcode_json", barcode_json);

			barcode_json = barcode_json.substring(0, barcode_json.length() - 1);

		}
		

	}

	public void updateSummary(String productid) {

		int barcodestatus = 1;
		cursor = sqldb.getPalette(productid);
		if (cursor.getCount() == 0) {
			barcodestatus = 0;
			sqldb.updateBarcodestatus(productid, barcodestatus);
			prepareListData();
			expListView.setAdapter(listAdapter);
			listAdapter.notifyDataSetChanged();
			Log.d("productid 0", "Prod" + productid);
		} else {
			String totalbarcodeweight = sqldb.getProductWeight(productid);
			Log.d("productid 1", "Prod" + productid);
			if (barcodefrom.matches("GraSummary")) {
				sqldb.updateQty(productid, totalbarcodeweight, barcodestatus);
			}else{
				sqldb.updateQty(productid, totalbarcodeweight, barcodestatus);
			}
			
		}
		cursor = sqldb.getBarcodeCursor();
		if (cursor.getCount() == 0) {
			ok.setVisibility(View.GONE);
			headerlayout.setVisibility(View.GONE);
			expListView.setVisibility(View.GONE);
			mEmpty.setVisibility(View.VISIBLE);
		}
		
		priceflag = SalesOrderSetGet.getCartonpriceflag();

		if (priceflag.matches("null") || priceflag.matches("")) {
			priceflag = "0";
		}

		clQty(productid);
	}

	public void prepareListData() {
		expListView = (ExpandableListView) findViewById(android.R.id.list);
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		arraList = sqldb.getCountid();
		for (int i = 0; i < arraList.size(); i++) {
			String productid = arraList.get(i);
			String id = arraList.get(i);
			prodPalette = sqldb.getProductPalette(productid);
			listDataHeader.add(id);
			listDataChild.put(listDataHeader.get(i), prodPalette);
			listAdapter = new ExpandableListAdapter(AddBarcodeSummary.this,
					listDataHeader, listDataChild);
		}
	}

	public class ExpandableListAdapter extends BaseExpandableListAdapter {
		boolean checkFocus = false;
		private Context _context;
		private List<String> _listDataHeader;
		private HashMap<String, List<String>> _listDataChild;
		SOTDatabase sqldb;

		public ExpandableListAdapter(Context context,
				List<String> listDataHeader,
				HashMap<String, List<String>> listDataChild) {
			this._context = context;
			this._listDataHeader = listDataHeader;
			this._listDataChild = listDataChild;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			return this._listDataChild.get(_listDataHeader.get(groupPosition))
					.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			sqldb = new SOTDatabase(_context);
			final String childText = (String) this.getChild(groupPosition,
					childPosition);
			final String headerTitle = (String) getGroup(groupPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_item, null);
			}
			final TextView txtListChildsno = (TextView) convertView
					.findViewById(R.id.lblsno);
			final TextView txtListChildid = (TextView) convertView
					.findViewById(R.id.lbprid);
			final TextView txtListChildname = (TextView) convertView
					.findViewById(R.id.lblListItem);
			final TextView txtListChildweight = (TextView) convertView
					.findViewById(R.id.lbcartoncount);
			ArrayList<String> al = new ArrayList<String>();

			al = sqldb.getPaletteInfo(childText);
			txtListChildid.setText(childText);
			// txtListChildname.setText(childText);
			String totarraval = al.toString();
			String[] splitnw = totarraval.split("=");
			String pbarcode = splitnw[0].replace("[", "");
			String pweight = splitnw[1].replace("]", "");
			txtListChildname.setText(pbarcode);
			txtListChildweight.setText(pweight);

			ArrayList<String> prodnamesno = new ArrayList<String>();
			prodnamesno = sqldb.summaryprodsno(childText);
			String prsno = prodnamesno.toString();
			String psno = prsno.replace("[", "");
			String psnum = psno.replace("]", "");
			txtListChildsno.setText(psnum);

			ImageView delete = (ImageView) convertView
					.findViewById(R.id.imageView2);
			delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {


					 // Creating and Building the Dialog 
			       AlertDialog.Builder radiobuilder = new AlertDialog.Builder(_context);
			       radiobuilder.setTitle("Select the option");
			       radiobuilder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
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
			           
			           AlertDialog.Builder builder = new AlertDialog.Builder(
								_context);
						builder.setMessage("Do you want to remove?");
						builder.setCancelable(false);
						builder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										List<String> child = _listDataChild
												.get(_listDataHeader
														.get(groupPosition));
										String productid = sqldb
												.prodBarCodeId(childText);
										sqldb.deleteProds(childText);
										sqldb.totalpaletteweight(childText);
										child.remove(childPosition);
										if (childText != null) {
											ArrayList<String> snoCount = new ArrayList<String>();
											String[] eq = headerTitle.split("=");
											String title = eq[0];
											
											String paletteno = title.replaceAll(
													"\\D+", "");
											snoCount = sqldb.snoCountID(paletteno);
											for (int i = 0; i < snoCount.size(); i++) {
												int sno = 1 + i;
												HashMap<String, String> queryValues = new HashMap<String, String>();
												queryValues.put("_id", ""
														+ snoCount.get(i));
												queryValues.put("snum", "" + sno);
												sqldb.updateSNUM(queryValues);
											}
										}

										notifyDataSetChanged();
										Toast.makeText(_context, " Deleted",
												Toast.LENGTH_SHORT).show();

										updateSummary(productid);
										
									}
								});
						builder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
						AlertDialog alertDialog = builder.create();
						alertDialog.show();
						
			           }
			       });
			       levelDialog = radiobuilder.create();
			       levelDialog.show();
				
					
					
					
				}
			});
			ImageView edit = (ImageView) convertView
					.findViewById(R.id.imageView1);
			edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog.Builder alert = new AlertDialog.Builder(
							_context);
					LinearLayout lila1 = new LinearLayout(_context);
					lila1.setOrientation(LinearLayout.VERTICAL);
					final EditText editname = new EditText(_context);
					final EditText editweight = new EditText(_context);
					lila1.addView(editname);
					lila1.addView(editweight);
					alert.setView(lila1);
					alert.setIcon(R.mipmap.edit);
					alert.setTitle("Edit");
					final String valuename = txtListChildname.getText()
							.toString();
					final String valueweight = txtListChildweight.getText()
							.toString();
					editname.setText(valuename);
					editweight.setText(valueweight);

					editweight.setFocusable(true);
					editweight.requestFocus();
					editweight
							.setRawInputType(Configuration.HARDKEYBOARDHIDDEN_YES);
					editweight.setRawInputType(InputType.TYPE_CLASS_NUMBER
							| InputType.TYPE_NUMBER_FLAG_DECIMAL);
					mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

					alert.setPositiveButton("Update",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (editname.getText().toString()
											.matches("")
											|| editweight.getText().toString()
													.matches("")) {

									} else {

										HashMap<String, String> queryValues = new HashMap<String, String>();
										queryValues.put("id", txtListChildid
												.getText().toString());
										queryValues.put("barcode", editname
												.getText().toString());
										queryValues.put("weight", editweight
												.getText().toString());
										sqldb.updateProducts(queryValues);

										ArrayList<String> al = new ArrayList<String>();
										al = sqldb.getPaletteInfo(childText);
										String totarraval = al.toString();
										String[] splitnw = totarraval
												.split("=");
										String pbarcode = splitnw[0].replace(
												"[", "");
										String pweight = splitnw[1].replace(
												"]", "");
										txtListChildname.setText(pbarcode);
										txtListChildweight.setText(pweight);
										mgr.toggleSoftInput(0, 0);
										String productid = sqldb
												.prodBarCodeId(childText);
										Toast.makeText(_context, " updated",
												Toast.LENGTH_SHORT).show();
										notifyDataSetChanged();

										updateSummary(productid);
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
				}
			});
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return this._listDataChild.get(
					this._listDataHeader.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return this._listDataHeader.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return this._listDataHeader.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			sqldb = new SOTDatabase(_context);
			String headerTitle = (String) getGroup(groupPosition);
			
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.exp_header_group,
						null);
			}
			ImageView headerimage = (ImageView) convertView
					.findViewById(R.id.lblListImage);
			TextView lblListId = (TextView) convertView
					.findViewById(R.id.lblListId);
			TextView lblListSno = (TextView) convertView
					.findViewById(R.id.lblListSno);
			TextView lblListProductName = (TextView) convertView
					.findViewById(R.id.lblListProductName);

			TextView lblListCount = (TextView) convertView
					.findViewById(R.id.lblListCount);
			TextView lblListTotal = (TextView) convertView
					.findViewById(R.id.lblListTotal);
			TextView listproductcode = (TextView) convertView
					.findViewById(R.id.lblListproductcode);
		
			lblListSno.setTypeface(null, Typeface.BOLD);
			// lblListSno.setText(headerTitle);
			int sno = 1 + groupPosition;
			Log.d("sno---------->", "" + sno);

			lblListSno.setText("" + sno);
			lblListId.setText(headerTitle);

			// lblListHeadcount.setText(remend);
			if (isExpanded) {

				headerimage.setImageResource(R.mipmap.collapse);
			} else {
				headerimage.setImageResource(R.mipmap.expand);
			}
			// String umn = title.replaceAll("\\D+", "");
			double tot = sqldb.getTotal(headerTitle);
			lblListTotal.setText(String.format("%.3f", tot));
			String countpale = sqldb.summarypalettecount(headerTitle);
			lblListCount.setText(countpale);
			String productcode = sqldb.distinctProdCode(headerTitle);
			listproductcode.setText(productcode);
			String productname = sqldb.distinctProdName(headerTitle);
			lblListProductName.setText(productname);
			// listHeader_productname.setText(productname);

			notifyDataSetChanged();

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	public void onBackPressed() {
	
		if (barcodefrom.matches("InvoiceSummary")) {
			callSummary = new Intent(AddBarcodeSummary.this,
					InvoiceSummary.class);
		} else if (barcodefrom.matches("DeliverySummary")) {
			callSummary = new Intent(AddBarcodeSummary.this,
					DeliverySummary.class);
		} else if (barcodefrom.matches("GraSummary")) {
			callSummary = new Intent(AddBarcodeSummary.this,
					GraSummary.class);
		}else if(barcodefrom.matches("TransferSummary")){
			callSummary = new Intent(AddBarcodeSummary.this,
					TransferSummary.class);
		}
		
		startActivity(callSummary);
		AddBarcodeSummary.this.finish();
	}

	public void alertDialog(final String id, final String sno,
			final String productcode, final String productname,
			final String cqty, final String itemremarks) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Edit");
		alertDialog.setMessage("Do you want to Edit Sno-" + sno);
		alertDialog.setIcon(R.mipmap.edit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent i = new Intent(AddBarcodeSummary.this,
								AddBarcode.class);
						i.putExtra("SOT_ssproductcode", productcode);
						i.putExtra("SOT_str_ssprodname", productname);
						i.putExtra("SOT_str_ssno", sno);
						i.putExtra("SOT_ssid", id);
						i.putExtra("SOT_str_c_qty", cqty);
						i.putExtra("SOT_str_itemremarks", itemremarks);
						i.putExtra("Barcodefrom", barcodefrom);

						startActivity(i);
						finish();
					}
				});
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

	public void clQty(String productid) {

		int cartonQty = 0;
		double looseQty = 0;
		
		products_arr = sqldb.getProducts(productid);

		quantity = products_arr.get(0);
		cprice = products_arr.get(1);
		productprice = products_arr.get(2);
		taxtype = products_arr.get(3);
		taxvalue = products_arr.get(4);
		pieceperqty = products_arr.get(5);
		itemdiscount = products_arr.get(6);

		Log.d("productid", productid);
		Log.d("quantity", quantity);
		Log.d("cprice", cprice);
		Log.d("productprice", productprice);
		Log.d("taxtype", taxtype);
		Log.d("taxvalue", taxvalue);
		Log.d("pieceperqty", pieceperqty);
		Log.d("itemdiscount", itemdiscount);

		try {
			double qty_nt = Double.parseDouble(quantity);
			int pcs_nt = Integer.parseInt(pieceperqty);

			Log.d("qty_nt", "" + qty_nt);
			Log.d("pcs_nt", "" + pcs_nt);

			// cartonQty = (int) (qty_nt / pcs_nt);
			// looseQty = qty_nt % pcs_nt;
			
			String countsno = sqldb.summarypalettecount(productid);
			
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

				// String itmDscnt = sl_itemDiscount.getText().toString();
				if (!itemdiscount.matches("")) {
					tt = (qty * slPriceCalc);
				} else {
					tt = qty * slPriceCalc;
				}

				Prodtotal = twoDecimalPoint(tt);

				// String itemDisc = sl_itemDiscount.getText().toString();
				if (!itemdiscount.matches("")) {
					itmDisc = Double.parseDouble(itemdiscount);
					subTotal = tt - itmDisc;
				} else {
					subTotal = tt;
				}

				sbTtl = twoDecimalPoint(subTotal);

				// sl_total.setText("" + sbTtl);

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
