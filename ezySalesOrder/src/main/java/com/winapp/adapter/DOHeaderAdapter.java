package com.winapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.sot.ConsignmentHeader;
import com.winapp.sot.DeliveryOrderHeader;
import com.winapp.sot.InvoiceHeader;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ReceiptHeader;
import com.winapp.sot.RowItem;
import com.winapp.sot.SO;
import com.winapp.sot.SalesOrderHeader;
import com.winapp.sot.SalesReturnHeader;
import com.winapp.sot.StockAdjustmentHeader;
import com.winapp.sot.StockRequestHeader;
import com.winapp.sot.StockTakeHeader;
import com.winapp.sot.TransferHeader;
import com.winapp.sotdetails.DeliveryVerificationHeader;
import com.winapp.sotdetails.ExpenseHeader;
import com.winapp.sotdetails.PackingHeader;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DOHeaderAdapter extends BaseAdapter {
	Context ctx;
	String datavalue, status, paid = "1", notpaid = "0", all = "2",podpending = "3",
			clssnm = "SalesOrder",showView = "true", verified = "Verified", notverified = "Not Verified";
	ArrayList<SO> listarray = new ArrayList<SO>();
	private LayoutInflater mInflater = null;
	private int selectedPosition = -1;

	boolean isSaleOrder;
	int resource;
//	ArrayList<SO> sectionitem = new ArrayList<SO>();
	private ArrayList<String> sectionheader = new ArrayList<String>();
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	String trantype;

	public DOHeaderAdapter(Activity context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.ctx = context;
	}

	public DOHeaderAdapter(Activity activty, int resource, String status,
						   ArrayList<SO> list,ArrayList<String> secheader,String trantype) {
		Log.d("checkTeanype","-->"+trantype);
		this.listarray.clear();
		this.sectionheader.clear();
		this.ctx = activty;
		if(!list.isEmpty()){
			this.listarray = list;
		}
		Log.d("listarray","Empty"+listarray.size() +ctx);
		this.resource = resource;
		this.status = status;
		this.sectionheader = secheader;
		mInflater = activty.getLayoutInflater();
		this.trantype = trantype;


	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listarray.size();
	}

	@Override
	public SO getItem(int position) {
		// TODO Auto-generated method stub
		return listarray.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final SO so = listarray.get(position);
		final ViewHolder holder;
		int rowType = getItemViewType(position);

		Log.e("So Postion Data", so.toString());

		if (convertView == null) {
			holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.delivery_list_item, null);
				holder.header_item = (TextView) convertView.findViewById(R.id.tvTitle);
				holder.title_layout = (LinearLayout) convertView.findViewById(R.id.title_layout);


			holder.main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
			holder.so_sno = (TextView) convertView.findViewById(R.id.sno);
			holder.so_date = (TextView) convertView.findViewById(R.id.date);
			holder.so_custcode = (TextView) convertView
					.findViewById(R.id.custcode);
			holder.so_amount = (TextView) convertView.findViewById(R.id.amount);
			holder.so_status = (TextView) convertView
					.findViewById(R.id.status);
			holder.so_invoiceSigned = (TextView) convertView .findViewById(R.id.invoice_signed);
			holder.so_layout2 = (LinearLayout) convertView
					.findViewById(R.id.layout2);
			holder.so_delCustomerName = (TextView) convertView
					.findViewById(R.id.DelCustomerName);
			holder.so_bal_amt =(TextView) convertView.findViewById(R.id.balance_amount);

			holder.address1 = (TextView) convertView
					.findViewById(R.id.customeraddress);

//			holder.address1.setText(so.getAddress());

			if (ctx instanceof SalesOrderHeader
					|| ctx instanceof StockRequestHeader
					|| ctx instanceof TransferHeader
					||ctx instanceof ConsignmentHeader) {

				holder.so_status.setVisibility(View.VISIBLE);
				holder.so_invoiceSigned.setVisibility(View.INVISIBLE);

			}
			if(ctx instanceof StockTakeHeader){
				holder.so_status.setVisibility(View.GONE);
			}
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkbox);


			////
			convertView.setTag(holder);
			convertView.setId(position);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String dateStr = so.getAssignDate();

		if(sectionheader.size()>0){

			Log.e("dateStr",""+dateStr);
			Log.e("header",""+sectionheader.get(position));


           if (dateStr != null && !dateStr.isEmpty()){
			   if(dateStr.matches(sectionheader.get(position))){
				   holder.title_layout.setVisibility(View.GONE);
				   holder.header_item.setText(sectionheader.get(position));
			   }else{
				   holder.header_item.setText("0");
				   holder.title_layout.setVisibility(View.GONE);
			   }
		   }else{
			   holder.header_item.setText("0");
			   holder.title_layout.setVisibility(View.GONE);
		   }


//				Log.d("header",""+sectionheader.get(position));
			Log.d("Child",""+so.getCustomerName());
		}else{
			holder.title_layout.setVisibility(View.GONE);
		}


		holder.checkBox.setChecked(so.isSelected());
		holder.checkBox.setId(position);
		holder.checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				SO so = listarray.get(v.getId());
				if (checkBox.isChecked()) {
					selectAll(false);
					so.setSelected(true);
					selectedPosition = v.getId();
//					((View) checkBox.getParent())
					holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
				} else {
					int id = v.getId();
					so.setSelected(false);
					selectedPosition = -1;
					if (ctx instanceof DeliveryOrderHeader) {
						if (so.getGotSignatureOnDO().toString().matches("True")) {
//							((View) checkBox.getParent())
							holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
		  				} else {
		  					if (id % 2 == 0) {
//								((View) checkBox.getParent())
								holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
							} else {
//								((View) checkBox.getParent())
								holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
							}
		  				}

					}
					else if (ctx instanceof ConsignmentHeader) {
						if (so.getGotSignatureOnDO().toString().matches("True")) {
//							((View) checkBox.getParent())
							holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
						} else {
							if (id % 2 == 0) {
//								((View) checkBox.getParent())
								holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
							} else {
//								((View) checkBox.getParent())
								holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
							}
						}

					}

					else{
						if (id % 2 == 0) {
//							((View) checkBox.getParent())
							holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
						} else {
//							((View) checkBox.getParent())
							holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
						}
					}
			
					
				}
				if (ctx instanceof SalesOrderHeader) {
					((SalesOrderHeader) ctx).showViews(checkBox.isChecked(),
							R.id.printer);
				}
				if (ctx instanceof InvoiceHeader) {
					((InvoiceHeader) ctx).showViews(checkBox.isChecked(),
							R.id.printer);
				}
				if (ctx instanceof DeliveryOrderHeader) {
					((DeliveryOrderHeader) ctx).showViews(checkBox.isChecked(),
							R.id.printer);
				}
				if (ctx instanceof SalesReturnHeader) {
					((SalesReturnHeader) ctx).showViews(checkBox.isChecked(),
							R.id.printer);
				}
				if (ctx instanceof StockRequestHeader) {
					((StockRequestHeader) ctx).showViews(checkBox.isChecked(),
							R.id.printer);
				}
				if (ctx instanceof TransferHeader) {
					((TransferHeader) ctx).showViews(checkBox.isChecked(),
							R.id.printer);
				}
				if (ctx instanceof ReceiptHeader) {
					
					((ReceiptHeader) ctx).showViews(checkBox.isChecked(),
							R.id.printer);
				}
				if (ctx instanceof ConsignmentHeader) {
					((ConsignmentHeader) ctx).showViews(checkBox.isChecked(),R.id.printer);
				}
				if (holder.checkBox.isChecked()) {
					RowItem.setPrintoption("True");

				} else {
					RowItem.setPrintoption("False");

				}
			}
		});
		
		holder.so_sno.setText(so.getSno());
		holder.so_date.setText(so.getDate());
		holder.so_bal_amt.setText(so.getSubTotal());
		
		
		/********Based on ShowPriceDO Amount will Gone or Visible *********/
		  if(ctx instanceof DeliveryOrderHeader){
		   if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
		    holder.so_amount.setVisibility(View.VISIBLE);
		   }else{
		    holder.so_amount.setVisibility(View.GONE);
		   }



		   String delcustomer = so.getDelCustomerName();
		   
		   if(delcustomer!=null && !delcustomer.isEmpty()){
			   
			   if(delcustomer.matches("null") || delcustomer.matches("")){
				   holder.so_layout2.setVisibility(View.GONE);
			   }else{
				   holder.so_layout2.setVisibility(View.VISIBLE);
				   holder.so_delCustomerName.setText(""+delcustomer);
			   }
	  
		   }else{
			   holder.so_layout2.setVisibility(View.GONE);
		   }
	 
		   
		  }
	   else if(ctx instanceof ConsignmentHeader) {
			if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")) {
				holder.so_amount.setVisibility(View.VISIBLE);
				holder.so_amount.setText(so.getNettotal());
			} else {
				holder.so_amount.setVisibility(View.GONE);
			}

			String delcustomer = so.getDelCustomerName();

			if (delcustomer != null && !delcustomer.isEmpty()) {

				if (delcustomer.matches("null")) {
					holder.so_layout2.setVisibility(View.GONE);
				} else {
					holder.so_layout2.setVisibility(View.VISIBLE);
					holder.so_delCustomerName.setText("" + delcustomer);
				}

			} else {
				holder.so_layout2.setVisibility(View.GONE);
			}


		}
		else if(ctx instanceof SalesOrderHeader){
			    /*** Amount will Gone or Visible Based on Hide Price From GetUserPermission  ***/
			    if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){ 
			    if(FormSetterGetter.getHidePrice().matches("Hide Price")){   
			       holder.so_amount.setVisibility(View.GONE);     
			    }else{
			      holder.so_amount.setVisibility(View.VISIBLE);
			    }
			    }else{
			      holder.so_amount.setVisibility(View.VISIBLE); 
			    }
			     }

		if( ctx instanceof TransferHeader || ctx instanceof StockRequestHeader || ctx instanceof SalesReturnHeader || 
				ctx instanceof ReceiptHeader){
			holder.so_sno.setVisibility(View.GONE);
		}
		
		
		
		if (ctx instanceof StockRequestHeader || ctx instanceof TransferHeader) {
			holder.so_custcode.setText(so.getFromlocation());
			holder.so_amount.setText(so.getTolocation());
		} else {
			
			if (ctx instanceof InvoiceHeader || /*ctx instanceof SalesOrderHeader ||*/ ctx instanceof DeliveryOrderHeader ) {
				if(showView.matches("true")){
					holder.so_sno.setVisibility(View.VISIBLE);
//					holder.so_date.setVisibility(View.GONE);
					holder.so_custcode.setText(so.getCustomerName());
					holder.so_bal_amt.setText(so.getSubTotal());
//					if(so.getStatus().matches("0")){
//						holder.so_status.setText("Not Paid");
//					}else if(so.getStatus().matches("1")){
//						holder.so_status.setText("Paid");
//					}else{
//						holder.so_status.setText("Partially Paid");
//					}
//					if(so.getInvoiceSigned().matches("0")){
//						holder.so_invoiceSigned.setText("Not Delivered");
//					}else{
//						holder.so_invoiceSigned.setText("Delivered");
//					}

					Log.d("customername","cc "+so.getCustomerName());

				}
				else{
					holder.so_sno.setVisibility(View.VISIBLE);
					holder.so_custcode.setText(so.getCustomerCode());
				}
				
			}else if( ctx instanceof ConsignmentHeader){
				if(showView.matches("true")){
					holder.so_sno.setVisibility(View.VISIBLE);
					holder.so_custcode.setText(so.getCustomerName());
					holder.so_amount.setText(so.getNettotal());
					holder.so_date.setVisibility(View.GONE);

					if(trantype.matches("COI")) {
						holder.so_bal_amt.setVisibility(View.VISIBLE);
						holder.so_bal_amt.setText(so.getBalanceamount());

						if (so.getStatus().matches("0")) {
							holder.so_status.setText("Not Paid");
							holder.so_status.setTextColor(Color
									.parseColor("#F34945"));
						} else if (so.getStatus().matches("1")) {
							holder.so_status.setText("Paid");
							holder.so_status.setTextColor(Color
									.parseColor("#2BDF73"));
						} else {
							holder.so_status.setText("Partially Paid");
							holder.so_status.setTextColor(Color
									.parseColor("#654321"));
						}
						if (so.getInvoiceSigned().matches("0")) {
							holder.so_invoiceSigned.setText("Not Delivered");
						} else {
							holder.so_invoiceSigned.setText("Delivered");
						}
					}else{
						holder.so_status.setVisibility(View.GONE);
						holder.so_invoiceSigned.setVisibility(View.GONE);
						holder.so_bal_amt.setVisibility(View.GONE);
					}

					Log.d("customername","cc "+so.getCustomerName());

				}
				else{
					holder.so_sno.setVisibility(View.VISIBLE);
					holder.so_custcode.setText(so.getCustomerCode());
					holder.so_date.setVisibility(View.GONE);
					holder.so_amount.setText(so.getNettotal());
					if(so.getStatus().matches("0")){
						holder.so_status.setText("Not Paid");
						holder.so_status.setTextColor(Color
								.parseColor("#F34945"));
					}else if(so.getStatus().matches("1")){
						holder.so_status.setText("Paid");
						holder.so_status.setTextColor(Color
								.parseColor("#2BDF73"));
					}else{
						holder.so_status.setText("Partially Paid");
						holder.so_status.setTextColor(Color
								.parseColor("#654321"));
					}
					if(so.getInvoiceSigned().matches("0")){
						holder.so_invoiceSigned.setText("Not Delivered");
					}else{
						holder.so_invoiceSigned.setText("Delivered");
					}
				}
			}
			else{				
				if (ctx instanceof SalesOrderHeader) {
					if(showView.matches("true")){
						holder.so_sno.setVisibility(View.GONE);
						holder.so_custcode.setText(so.getCustomerName());
						holder.so_status.setVisibility(View.GONE);
					}
					else{
						holder.so_sno.setVisibility(View.GONE);
						holder.so_custcode.setText(so.getCustomerCode());
						holder.so_status.setVisibility(View.VISIBLE);
					}
				}else{
					holder.so_custcode.setText(so.getCustomerCode());
				}
			}
			
			holder.so_amount.setText(so.getNettotal());
		}
	
		
		if (ctx instanceof StockAdjustmentHeader) {
					
				holder.checkBox.setVisibility(View.GONE);
				holder.so_sno.setText(so.getStAdjust_no());
				holder.so_date.setText(so.getStAdjust_date());
				holder.so_amount.setText(so.getStAdjust_location());
				holder.so_custcode.setVisibility(View.GONE);
				holder.so_status.setVisibility(View.GONE);
		
		}
		
	//  DeliveryVerificationHeader
		  if(ctx instanceof DeliveryVerificationHeader){
		   holder.so_sno.setVisibility(View.GONE);
		   holder.so_date.setVisibility(View.GONE);
		   holder.so_amount.setVisibility(View.GONE);
		   holder.so_status.setVisibility(View.VISIBLE);
		   holder.so_custcode.setText(so.getCustomerName());
		     }
		  
		  if(ctx instanceof PackingHeader){
			  holder.checkBox.setVisibility(View.GONE);
			  holder.so_custcode.setVisibility(View.VISIBLE);
			   holder.so_custcode.setText(so.getCustomerName()+"("+so.getCustomerCode()+")");
			   holder.so_sno.setText(so.getSno());
			   holder.so_date.setText(so.getDate());
			   holder.so_amount.setText(so.getRemarks1());
		}
		
		if (ctx instanceof SalesOrderHeader || ctx instanceof PackingHeader || ctx instanceof DeliveryVerificationHeader
				|| ctx instanceof StockRequestHeader
				|| ctx instanceof TransferHeader || ctx instanceof ExpenseHeader) {
			holder.so_status.setText(so.getStatus());
		}
		 
		if (ctx instanceof InvoiceHeader) {
			if (status != null) {
				if (status.matches(paid)) {
					if (position % 2 == 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_even_bg);
						}
						holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_date
								.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#2BDF73"));
						holder.so_amount.setTextColor(Color
								.parseColor("#2BDF73"));
					} else {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_odd_bg);
						}
						holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_date
								.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#2BDF73"));
						holder.so_amount.setTextColor(Color
								.parseColor("#2BDF73"));
					}
				} else if (status.matches(podpending)) {
					if (position % 2 == 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_even_bg);
						}
//						holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
//						holder.so_date
//								.setTextColor(Color.parseColor("#2BDF73"));
//						holder.so_custcode.setTextColor(Color
//								.parseColor("#2BDF73"));
//						holder.so_amount.setTextColor(Color
//								.parseColor("#2BDF73"));
					} else {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_odd_bg);
						}
//						holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
//						holder.so_date
//								.setTextColor(Color.parseColor("#2BDF73"));
//						holder.so_custcode.setTextColor(Color
//								.parseColor("#2BDF73"));
//						holder.so_amount.setTextColor(Color
//								.parseColor("#2BDF73"));
					}
				}else if (status.matches(notpaid)) {
					if (position % 2 == 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_even_bg);
						}
						holder.so_sno.setTextColor(Color.parseColor("#F34945"));
						holder.so_date
								.setTextColor(Color.parseColor("#F34945"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#F34945"));
						holder.so_amount.setTextColor(Color
								.parseColor("#F34945"));
					} else {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_odd_bg);
						}
						holder.so_sno.setTextColor(Color.parseColor("#F34945"));
						holder.so_date
								.setTextColor(Color.parseColor("#F34945"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#F34945"));
						holder.so_amount.setTextColor(Color
								.parseColor("#F34945"));
					}
				} else if (status.matches(all)) {
					String totlbal = so.getBalanceamount();
					double totalbalance;
					if ((totlbal == null) || (totlbal == "")) {
						totalbalance = 0.0;
					} else {
						totalbalance = Double.valueOf(totlbal);
					}

					if (totalbalance > 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_even_bg);
						}
						holder.so_sno.setTextColor(Color.parseColor("#F34945"));
						holder.so_date
								.setTextColor(Color.parseColor("#F34945"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#F34945"));
						holder.so_amount.setTextColor(Color
								.parseColor("#F34945"));
					} else {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_odd_bg);
						}
						holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_date
								.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#2BDF73"));
						holder.so_amount.setTextColor(Color
								.parseColor("#2BDF73"));
					}

				}
			} else {
				if (position % 2 == 0) {
					if (so.isSelected()) {
						convertView
								.setBackgroundResource(R.drawable.list_item_selected_bg);
					} else {
						convertView
								.setBackgroundResource(R.drawable.list_item_even_bg);
					}
					holder.so_sno.setTextColor(Color.parseColor("#035994"));
					holder.so_date.setTextColor(Color.parseColor("#035994"));
					holder.so_custcode
							.setTextColor(Color.parseColor("#035994"));
					holder.so_amount.setTextColor(Color.parseColor("#035994"));
				} else {
					if (so.isSelected()) {
						convertView
								.setBackgroundResource(R.drawable.list_item_selected_bg);
					} else {
						convertView
								.setBackgroundResource(R.drawable.list_item_odd_bg);
					}
					holder.so_sno.setTextColor(Color.parseColor("#646464"));
					holder.so_date.setTextColor(Color.parseColor("#646464"));
					holder.so_custcode
							.setTextColor(Color.parseColor("#646464"));
					holder.so_amount.setTextColor(Color.parseColor("#646464"));
				}
			}
		} else if (ctx instanceof DeliveryVerificationHeader) {
			
//			convertView.setBackgroundResource(R.drawable.list_item_odd_bg);
		      if (so.getStatus().toString().matches(verified)) {
		 	          
		           holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
		           holder.so_date
		             .setTextColor(Color.parseColor("#2BDF73"));
		           holder.so_custcode.setTextColor(Color
		             .parseColor("#2BDF73"));
		           holder.so_amount.setTextColor(Color
		             .parseColor("#2BDF73"));
		           holder.so_status.setTextColor(Color
		               .parseColor("#2BDF73"));
		          }else{
		        	  holder.so_sno.setTextColor(Color.parseColor("#035994"));
		              holder.so_date.setTextColor(Color.parseColor("#035994"));
		              holder.so_custcode
		                .setTextColor(Color.parseColor("#035994"));
		              holder.so_amount.setTextColor(Color.parseColor("#035994"));
		              holder.so_status.setTextColor(Color
		                   .parseColor("#035994"));
		         }  

		} else if (ctx instanceof DeliveryOrderHeader) {
			
		        	  if (position % 2 == 0) {
		        		  
		        		 if (so.isSelected()) {
							 holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
		  				} else {
		  					if (so.getGotSignatureOnDO().toString().matches("True")) {
								holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
			  				} else {
								holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
			  				}
		  				}
//		  				holder.so_sno.setTextColor(Color.parseColor("#035994"));
//		  				holder.so_date.setTextColor(Color.parseColor("#035994"));
//		  				holder.so_custcode.setTextColor(Color.parseColor("#035994"));
//		  				holder.so_amount.setTextColor(Color.parseColor("#035994"));
		  				
		  			} else {
		  				
		  				if (so.isSelected()) {
							holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
		  				} else {
		  					if (so.getGotSignatureOnDO().toString().matches("True")) {
								holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
			  				} else {
								holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
			  				}
		  				}
//		  				holder.so_sno.setTextColor(Color.parseColor("#646464"));
//		  				holder.so_date.setTextColor(Color.parseColor("#646464"));
//		  				holder.so_custcode.setTextColor(Color.parseColor("#646464"));
//		  				holder.so_amount.setTextColor(Color.parseColor("#646464"));
		  				
		  			}
		           
		    
		}
		else if (ctx instanceof ConsignmentHeader) {

			if (position % 2 == 0) {

				if (so.isSelected()) {
					holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
				} else {
					if (so.getGotSignatureOnDO().toString().matches("True")) {
						holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
					} else {
						holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
					}
				}
//		  				holder.so_sno.setTextColor(Color.parseColor("#035994"));
//		  				holder.so_date.setTextColor(Color.parseColor("#035994"));
//		  				holder.so_custcode.setTextColor(Color.parseColor("#035994"));
//		  				holder.so_amount.setTextColor(Color.parseColor("#035994"));

			} else {

				if (so.isSelected()) {
					holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
				} else {
					if (so.getGotSignatureOnDO().toString().matches("True")) {
						holder.main_layout.setBackgroundResource(R.mipmap.box_bg_green);
					} else {
						holder.main_layout.setBackgroundResource(R.mipmap.box_bg);
					}
				}
//		  				holder.so_sno.setTextColor(Color.parseColor("#646464"));
//		  				holder.so_date.setTextColor(Color.parseColor("#646464"));
//		  				holder.so_custcode.setTextColor(Color.parseColor("#646464"));
//		  				holder.so_amount.setTextColor(Color.parseColor("#646464"));

			}


		}

		else {
			if (position % 2 == 0) {
				if (so.isSelected()) {
					convertView
							.setBackgroundResource(R.drawable.list_item_selected_bg);
				} else {
					convertView
							.setBackgroundResource(R.drawable.list_item_even_bg);
				}

				holder.so_sno.setTextColor(Color.parseColor("#035994"));
				holder.so_date.setTextColor(Color.parseColor("#035994"));
				holder.so_custcode.setTextColor(Color.parseColor("#035994"));
				holder.so_amount.setTextColor(Color.parseColor("#035994"));
				if (ctx instanceof SalesOrderHeader
						|| ctx instanceof PackingHeader
						|| ctx instanceof StockRequestHeader
						|| ctx instanceof TransferHeader
						|| ctx instanceof ExpenseHeader) {
					holder.so_status.setTextColor(Color.parseColor("#035994"));
				}
			} else {
				if (so.isSelected()) {
					convertView
							.setBackgroundResource(R.drawable.list_item_selected_bg);
				} else {
					convertView
							.setBackgroundResource(R.drawable.list_item_odd_bg);
				}
				holder.so_sno.setTextColor(Color.parseColor("#646464"));
				holder.so_date.setTextColor(Color.parseColor("#646464"));
				holder.so_custcode.setTextColor(Color.parseColor("#646464"));
				holder.so_amount.setTextColor(Color.parseColor("#646464"));
				if (ctx instanceof SalesOrderHeader
						|| ctx instanceof PackingHeader
						|| ctx instanceof StockRequestHeader
						|| ctx instanceof TransferHeader
						|| ctx instanceof ExpenseHeader) {
					holder.so_status.setTextColor(Color.parseColor("#646464"));
				}
			}
		}
		return convertView;
	}

	public void remove(SO item) {
		listarray.remove(item);
	}

	public boolean isAllSelected() {
		for (SO so : listarray) {
			if (!so.isSelected()) {
				return false;
			}
		}
		return true;
	}

	public void selectAll(boolean select) {
		for (SO so : listarray) {
			so.setSelected(select);
		}
		notifyDataSetChanged();
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
		notifyDataSetChanged();
	}

	class ViewHolder {
		TextView header_item;

		CheckBox checkBox;
		TextView so_sno;
		TextView so_date;
		TextView so_custcode;
		TextView so_amount;
		TextView so_status,address1;
		TextView so_invoiceSigned,so_bal_amt;
		LinearLayout so_layout2;
		TextView so_delCustomerName;
		LinearLayout main_layout,title_layout;
	}

	public void showAll(boolean show) {
		// TODO Auto-generated method stub
		if(show == true){
			showView="false";
		}
		else if(show == false){
			showView="true";
		}
		notifyDataSetChanged();
	}
}
