package com.winapp.fwms;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.sot.SalesOrderSetGet;

public class Summary extends ListActivity {
	ImageView back, logout;
	Intent callSummary, calllogout, callstockin;
	static ExpandableListAdapter listAdapter;
	static ExpandableListView expListView;
	static List<String> listDataHeader;
	Button ok;
	static HashMap<String, List<String>> listDataChild;
	static MySQLiteDataBase sqldb;
	static ArrayList<String> arraList;
	static ArrayList<String> arraWeight;
	static ArrayList<String> arratotpalweight;
	static ArrayList<String> dataHeader;
	Cursor cursor;
	String palletid, productcode, productname, weightbarcode, weight;
	HashMap<String, String> productList;
	ArrayList<HashMap<String, String>> datadb;
	static List<String> prodPalette;
	String setStockInCartonDetail, setStockdetail, setProductStock;
	String setStockInStr;
	String getStockInRefId, setStockInRefId;
	String keyP = "FWMS";
	double avgcost;
	String valid_url;
	ArrayList<HashMap<String, String>> pCode = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> loccode = new HashMap<String, String>();
//	private ArrayList<String> ListProduct = new ArrayList<String>();

	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.summary_screen);

		back = (ImageView) findViewById(R.id.back);
		ok = (Button) findViewById(R.id.button1);

		FWMSSettingsDatabase.init(Summary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SetStockInDetail(valid_url);

		sqldb = new MySQLiteDataBase(this);
		prodPalette = new ArrayList<String>();
		arraList = new ArrayList<String>();
		arraWeight = new ArrayList<String>();
		datadb = sqldb.getAllProducts();
		if (datadb.isEmpty() || datadb.equals("[]")) {
			ok.setVisibility(View.INVISIBLE);
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
			expListView.setOnItemLongClickListener(new  OnItemLongClickListener() {

			    @Override
			    public boolean onItemLongClick(AdapterView<?> arg0,  View view, int position, long id) {
			     // TODO Auto-generated method stub
			    	if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {			    
			               /*int  groupPosition = ExpandableListView.getPackedPositionGroup(id);
			               alertDialog(groupPosition);*/
			    	TextView lblListpalette = (TextView) view.findViewById(R.id.lblListHeader);
			        String sno = lblListpalette.getText().toString();
			        String palettenum = sno.replaceAll("\\D+", "");
			                  //int  groupPosition = ExpandableListView.getPackedPositionGroup(id);
			                  alertDialog(palettenum);
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
				// TODO Auto-generated method stub

				AsyncCallWSSetStockIn task = new AsyncCallWSSetStockIn();
				task.execute();
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callSummary = new Intent(Summary.this, AddProduct.class);
				startActivity(callSummary);
				Summary.this.finish();
			}
		});

	}

//	private void enableViews(View v, boolean enabled) {
//		if (v instanceof ViewGroup) {
//			ViewGroup vg = (ViewGroup) v;
//			for (int i = 0; i < vg.getChildCount(); i++) {
//				enableViews(vg.getChildAt(i), enabled);
//			}
//		}
//		v.setEnabled(enabled);
//	}

	public void prepareListData() {
		sqldb = new MySQLiteDataBase(this);
		expListView = (ExpandableListView) findViewById(android.R.id.list);
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		arraList = sqldb.getCountid();
		for (int i = 0; i < arraList.size(); i++) {
			String arrastr = arraList.get(i);
			String s = "Palette-" + arrastr;
			arraWeight = sqldb.totalpaletteweight(arrastr);
			String st = arraWeight.toString();
			String strva = s + "=" + st;
			prodPalette = sqldb.getProductPalette(arrastr);
			listDataHeader.add(strva);
			listDataChild.put(listDataHeader.get(i), prodPalette);
			listAdapter = new ExpandableListAdapter(Summary.this,
					listDataHeader, listDataChild);
		}
	}

	public class ExpandableListAdapter extends BaseExpandableListAdapter {
		boolean checkFocus = false;
		private Context _context;
		private List<String> _listDataHeader;
		private HashMap<String, List<String>> _listDataChild;
//		private ArrayList<HashMap<String, List<String>>> ssl = new ArrayList<HashMap<String, List<String>>>();
		MySQLiteDataBase sqldb;

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
			sqldb = new MySQLiteDataBase(_context);
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
			txtListChildname.setText(childText);
			String totarraval = al.toString();
			String[] splitnw = totarraval.split("=");
			String pname = splitnw[0].replace("[", "");
			String pweight = splitnw[1].replace("]", "");
			txtListChildname.setText(pname);
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

									sqldb.deleteProduct(childText);
									sqldb.totalpaletteweight(childText);
									child.remove(childPosition);
									if (childText != null) {
										ArrayList<String> snoCount = new ArrayList<String>();
										String[] eq = headerTitle.split("=");
										String title = eq[0];
//										String remsta = eq[1].replace("[", "");
//										String remend = remsta.replace("]", "");
										String paletteno = title.replaceAll(
												"\\D+", "");
										snoCount = sqldb.snoCountID(paletteno);
										for (int i = 0; i < snoCount.size(); i++) {
											int sno = 1 + i;
											HashMap<String, String> queryValues = new HashMap<String, String>();
											queryValues.put("_id", ""
													+ snoCount.get(i));
											queryValues.put("snum", "" + sno);
											sqldb.updateSNO(queryValues);
										}
									}
									notifyDataSetChanged();
									Toast.makeText(_context, " Deleted",
											Toast.LENGTH_SHORT).show();

									int palettecount = sqldb.maxpalettecount();
									if (palettecount == 0) {
										ok.setVisibility(View.INVISIBLE);
									}
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
			ImageView edit = (ImageView) convertView
					.findViewById(R.id.imageView1);
			edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog.Builder alert = new AlertDialog.Builder(
							_context);
					LinearLayout lila1 = new LinearLayout(_context);
					lila1.setOrientation(1);
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
										queryValues.put("productname", editname
												.getText().toString());
										queryValues.put("weight", editweight
												.getText().toString());
										sqldb.updateProduct(queryValues);

										ArrayList<String> al = new ArrayList<String>();
										al = sqldb.getPaletteInfo(childText);
										String totarraval = al.toString();
										String[] splitnw = totarraval
												.split("=");
										String pname = splitnw[0].replace("[",
												"");
										String pweight = splitnw[1].replace(
												"]", "");
										txtListChildname.setText(pname);
										txtListChildweight.setText(pweight);
										mgr.toggleSoftInput(0, 0);
										notifyDataSetChanged();
										Toast.makeText(_context, " updated",
												Toast.LENGTH_SHORT).show();

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
			sqldb = new MySQLiteDataBase(_context);
			String headerTitle = (String) getGroup(groupPosition);
			String[] eq = headerTitle.split("=");
			String title = eq[0];
//			String remsta = eq[1].replace("[", "");
//			String remend = remsta.replace("]", "");
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_exp_header,
						null);
			}
			ImageView headerimage = (ImageView) convertView
				     .findViewById(R.id.lblListHeaderImage);
			TextView lblListpalette = (TextView) convertView
					.findViewById(R.id.paletteno);
			TextView lblListHeader = (TextView) convertView
					.findViewById(R.id.lblListHeader);
			TextView lblListHeadcount = (TextView) convertView
					.findViewById(R.id.palettecount);

			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(title);
			// lblListHeadcount.setText(remend);
			if (isExpanded) {
			    headerimage.setImageResource(R.mipmap.collapse);
			   } else {
			    headerimage.setImageResource(R.mipmap.expand);
			   }
			String umn = title.replaceAll("\\D+", "");
			double tot = sqldb.getTotal(umn);
			lblListHeadcount.setText(String.format("%.2f", tot));
			String countpale = sqldb.summarypalettecount(umn);
			lblListpalette.setText(countpale);
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

	private class AsyncCallWSSetStockIn extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

			dialog = new ProgressDialog(Summary.this,
					AlertDialog.THEME_HOLO_DARK);
			dialog.setMessage("Please wait...");
			dialog.show();
			dialog.getWindow().setLayout(300, 120);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			saveToServer();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			toastMessage();

			dialog.hide();
		}
	}

	@SuppressWarnings("unchecked")
	public void saveToServer() {
		
		NumberFormat nf = NumberFormat.getInstance();
		ArrayList<String> pc = new ArrayList<String>();
		ArrayList<String> pn = new ArrayList<String>();
		ArrayList<String> pb = new ArrayList<String>();
		ArrayList<String> pw = new ArrayList<String>();
		ArrayList<String> dpc = new ArrayList<String>();
		ArrayList<String> dpn = new ArrayList<String>();
		ArrayList<Double> dpqty = new ArrayList<Double>();
		ArrayList<String> palecode = new ArrayList<String>();
		ArrayList<String> allsno = new ArrayList<String>();
		ArrayList<String> countcarton = new ArrayList<String>();
//		ArrayList<Double> qtydetail = new ArrayList<Double>();
		String prodco = "";
		@SuppressWarnings("rawtypes")
		ArrayList pco = new ArrayList();
		ArrayList<Double> price = new ArrayList<Double>();
		ArrayList<String> averagecost = new ArrayList<String>();
		ArrayList<Double> qty = new ArrayList<Double>();
		ArrayList<Double> totalweight = new ArrayList<Double>();
		pc = sqldb.producode();
		pn = sqldb.produname();
		pb = sqldb.prodbarcode();
		pw = sqldb.weight();
		allsno = sqldb.getAllSno();
		palecode = sqldb.palettecode();
		String distinctprodname = "";
		String getdistinctprod = "";
		String prodavgcos = "";
		String subnettotal = "";
		String removcommatotal = "";
		double totalproduct = 0;
		try {
			String username = SupplierSetterGetter.getUsername();
			String supcode = SupplierSetterGetter.getSuppliercode();
			String date = SupplierSetterGetter.getDate();
			String remarks = SupplierSetterGetter.getRemarks();
			String locationcode = SalesOrderSetGet.getLocationcode();
		
			pCode = SetStockInDetail.getProductAvgCost("fncGetProduct");

			ar: for (int i = 0; i < pc.size(); i++) {

				prodco = pc.get(i);
				if (pco.isEmpty()) {
					pco.add(prodco);
				} else {
					if (pco.contains(prodco)) {
						continue ar;

					} else {
						pco.add(prodco);
					}

				}
				prodavgcos = SetStockInDetail.getAvgCost("fncGetProduct",
						prodco);
				double weights = sqldb.getprodweight(prodco);
				double prodacost=0.0;
				if(!prodavgcos.matches("")){
				prodacost = Double.parseDouble(prodavgcos);
				}
				avgcost = weights * prodacost;
				dpqty = sqldb.totalprodqty(prodco);
				String cpc = sqldb.getCartonSno(prodco);
				Double conve=0.0;
				if(!prodavgcos.matches("")){
				conve = Double.parseDouble(prodavgcos);
				}
				price.add(conve);
				nf.setMaximumFractionDigits(2);
				String myString = nf.format(avgcost);
				String removecommavg = myString.replace(",", "");
				averagecost.add(removecommavg);
				totalweight.add(avgcost);
				qty.addAll(dpqty);
				countcarton.add(cpc);

			}

			dpc = sqldb.distinctprodcode();
			for (int j = 0; j < dpc.size(); j++) {

				distinctprodname = dpc.get(j);
				getdistinctprod = sqldb.unqiuetproductnames(distinctprodname);
				dpn.add(getdistinctprod);

			}
			for (int k = 0; k < totalweight.size(); k++) {
				totalproduct = totalproduct + totalweight.get(k);
			}
			nf.setMaximumFractionDigits(2);
			subnettotal = nf.format(totalproduct);
			removcommatotal = subnettotal.replace(",", "");
			Log.d("dpc", "" + dpc);
			Log.d("dpn", "" + dpn);
			Log.d("weights*prodacost", "" + avgcost);
			Log.d("dpqty", "" + dpqty);
			Log.d("qtyweight", "" + qty);
			Log.d("averagecost", "" + averagecost);
			Log.d("price", "" + price);
			Log.d("pcsize", "" + pc.size());
			Log.d("pc", "" + pc);
			Log.d("countcarton", "" + countcarton);
			Log.d("subnettotal", "" + removcommatotal);
			String totalsuppweight = sqldb.totalsuppweight();
			setStockInStr = SetStockInDetail.setStockIn(
					"fncSaveStockInWithCartonBarcode", locationcode, supcode,
					remarks, date, username, pc, pn, pb, pw, dpc, dpn, dpqty,
					palecode, price, averagecost, qty, totalsuppweight,
					countcarton, allsno, removcommatotal);

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
	}

	public void toastMessage() {

		if (setStockInStr.matches("")) {
			Toast.makeText(getApplicationContext(), "failed",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT)
					.show();
			sqldb.removeAll();
			Intent i = new Intent(Summary.this, StockInActivity.class);
			startActivity(i);
			Summary.this.finish();
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(Summary.this, AddProduct.class);
		startActivity(i);
		Summary.this.finish();
	}
	
	public void alertDialog(final String paletteno) {
		//  final int paletteno=groupPosition+1;
		  
		  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		  alertDialog.setTitle("Edit");
		  alertDialog.setMessage("Do you want to Edit Palette-"+paletteno);
		  alertDialog.setIcon(R.mipmap.edit);
		  alertDialog.setPositiveButton("YES",
		    new DialogInterface.OnClickListener() {
		     @Override
			public void onClick(DialogInterface dialog, int which) {
		      SupplierSetterGetter.setExpheader(Integer.valueOf(paletteno));
		      Intent i = new Intent(Summary.this, AddProduct.class);
		      startActivity(i);
		      Summary.this.finish(); 
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
	
}
