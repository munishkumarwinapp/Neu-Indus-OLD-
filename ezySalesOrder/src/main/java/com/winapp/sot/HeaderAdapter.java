package com.winapp.sot;

import java.util.ArrayList;

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
import com.winapp.sotdetails.DeliveryVerificationHeader;
import com.winapp.sotdetails.ExpenseHeader;
import com.winapp.sotdetails.PackingHeader;

import org.w3c.dom.Text;

public class HeaderAdapter extends BaseAdapter {
	Context ctx;
	String datavalue, status, paid = "1", notpaid = "0", all = "2",podpending = "3",overdue = "4",
			clssnm = "SalesOrder",showView = "true", verified = "Verified", notverified = "Not Verified";
	ArrayList<SO> listarray = new ArrayList<SO>();
	private LayoutInflater mInflater = null;
	private int selectedPosition = -1;
	
	boolean isSaleOrder;
	int resource;

	public HeaderAdapter(Activity activty, int resource, String status, ArrayList<SO> list) {
		this.listarray.clear();
		this.ctx = activty;
		if(!list.isEmpty()) {
			this.listarray = list;
		}
//		this.listarray = list;
		Log.d("listarray","Empty"+listarray.size() +ctx);
		this.resource = resource;
		this.status = status;
		mInflater = activty.getLayoutInflater();

		Log.d("status","st "+status);
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
		try {
			final SO so = listarray.get(position);
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(resource, null);
				holder.so_sno = (TextView) convertView.findViewById(R.id.sno);
				holder.so_date = (TextView) convertView.findViewById(R.id.date);
				holder.so_custcode = (TextView) convertView.findViewById(R.id.custcode);
				holder.so_amount = (TextView) convertView.findViewById(R.id.amount);
				holder.so_status = (TextView) convertView.findViewById(R.id.status);

				holder.so_layout2 = (LinearLayout) convertView
						.findViewById(R.id.layout2);
				holder.so_delCustomerName = (TextView) convertView
						.findViewById(R.id.DelCustomerName);

				Log.d("ActivityCheck","-->"+ctx);

				if (ctx instanceof TransferHeader){
					holder.remarks = (TextView) convertView.findViewById(R.id.remarks);
					holder.txt_fromloc = (TextView) convertView.findViewById(R.id.txt_fromloc);
					holder.txt_toloc = (TextView) convertView.findViewById(R.id.txt_toloc);
					holder.txt_remarks = (TextView) convertView.findViewById(R.id.txt_remarks);
				}

				if (ctx instanceof SalesOrderHeader
						|| ctx instanceof StockRequestHeader
						|| ctx instanceof TransferHeader) {

					holder.so_status.setVisibility(View.VISIBLE);
				}
				if(ctx instanceof SalesOrderHeader)
				{
					holder.list_item_layout = (LinearLayout) convertView
							.findViewById(R.id.list_item_layout);
				}
				if (ctx instanceof StockTakeHeader) {
					holder.so_status.setVisibility(View.GONE);
				}

				if (ctx instanceof InvoiceHeader) {
					holder.list_item_layout = (LinearLayout) convertView
							.findViewById(R.id.list_item_layout);
					holder.balance_amount = (TextView) convertView
							.findViewById(R.id.balance_amount);
					holder.overdue_txt = (TextView) convertView
							.findViewById(R.id.overdue_txt);
					holder.invoice_signed = (TextView) convertView
							.findViewById(R.id.invoice_signed);
					holder.customeraddress = (TextView) convertView.findViewById(R.id.customeraddress);
					holder.customeraddresslayout = (LinearLayout) convertView.findViewById(R.id.customeraddresslayout);
					holder.customeraddresslayout.setVisibility(View.GONE);
				}
                if (ctx instanceof ReceiptHeader || ctx instanceof CashInvoiceHeader) {
                    holder.list_item_layout = (LinearLayout) convertView
                            .findViewById(R.id.list_item_layout);
                    holder.balance_amount = (TextView) convertView
                            .findViewById(R.id.balance_amount);
                    holder.overdue_txt = (TextView) convertView
                            .findViewById(R.id.overdue_txt);
                    holder.invoice_signed = (TextView) convertView
                            .findViewById(R.id.invoice_signed);
                    holder.customeraddresslayout = (LinearLayout) convertView.findViewById(R.id.customeraddresslayout);
                    holder.customeraddresslayout.setVisibility(View.GONE);
                }
				if (ctx instanceof ReceiptHeader) {
					//holder.list_item_layout.setBackgroundResource(R.drawable.list_grey_bg);
					holder.status_layout = (LinearLayout) convertView
							.findViewById(R.id.statusLayout);
					holder.status_layout.setVisibility(View.GONE);
					holder.balance_amount.setVisibility(View.GONE);

				}

				if(ctx instanceof SalesReturnHeader){
					Log.d("ctxGet","-->"+"executing!!");
					holder.list_item_layout = (LinearLayout) convertView.findViewById(R.id.list_item_layout);
					holder.so_status = (TextView)convertView.findViewById(R.id.status);
					holder.so_status.setVisibility(View.INVISIBLE);
				}

				if (ctx instanceof InvoiceHeader) {
					holder.IsClosed_status = (TextView) convertView
							.findViewById(R.id.IsClosed_status);
					holder.IsPosted_status = (TextView) convertView
							.findViewById(R.id.IsPosted_status);
				}

				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.checkbox);
				convertView.setTag(holder);
				convertView.setId(position);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkBox.setChecked(so.isSelected());
			holder.checkBox.setId(position);
			Log.d("ListArrayItemCheck","-->"+listarray.size());

			final View finalConvertView = convertView;
			holder.checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox checkBox = (CheckBox) v;
					SO so = listarray.get(v.getId());
					if (checkBox.isChecked()) {
						selectAll(false);
						so.setSelected(true);
						selectedPosition = v.getId();
						holder.checkBox.setVisibility(View.VISIBLE);

						if(ctx instanceof SalesReturnHeader){
							Log.d("isCheckedValue","-->"+checkBox.isChecked()+selectedPosition);
							((View) holder.list_item_layout.getParent())
									.setBackgroundResource(R.drawable.list_grey_select);
						}
						if(ctx instanceof SalesOrderHeader){
							Log.d("isCheckedValue","-->"+checkBox.isChecked()+selectedPosition);
							((View) holder.list_item_layout.getParent())
									.setBackgroundResource(R.drawable.list_grey_select);
						}
						if (ctx instanceof SalesOrderHeader || ctx instanceof SalesReturnHeader || ctx instanceof InvoiceHeader || ctx instanceof ReceiptHeader || ctx instanceof CashInvoiceHeader) {
							((View) holder.list_item_layout.getParent())
									.setBackgroundResource(R.drawable.list_grey_select);
						} else {
							((View) checkBox.getParent())
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						}

					} else {
						int id = v.getId();
						so.setSelected(false);
						selectedPosition = -1;
						if (ctx instanceof DeliveryOrderHeader) {
							if (so.getGotSignatureOnDO().toString().matches("True")) {

								if (ctx instanceof InvoiceHeader || ctx instanceof ReceiptHeader || ctx instanceof CashInvoiceHeader) {
									((View) holder.list_item_layout.getParent())
											.setBackgroundResource(R.drawable.list_grey_select);
								}
								else {
									((View) checkBox.getParent())
											.setBackgroundResource(R.drawable.list_item_selected_bg);
								}
							} else {

								if (ctx instanceof InvoiceHeader || ctx instanceof ReceiptHeader || ctx instanceof CashInvoiceHeader) {
									((View) holder.list_item_layout.getParent())
											.setBackgroundResource(R.drawable.list_grey_bg);
								}
								else if(ctx instanceof SalesReturnHeader)
								{
									((View) holder.list_item_layout.getParent()).setBackgroundResource(R.drawable.list_grey_bg);
								}
								else if(ctx instanceof SalesOrderHeader)
								{
									((View) holder.list_item_layout.getParent()).setBackgroundResource(R.drawable.list_grey_bg);
								}
								else {
									if (id % 2 == 0) {
										((View) checkBox.getParent())
												.setBackgroundResource(R.drawable.list_item_even_bg);
									} else {
										((View) checkBox.getParent())
												.setBackgroundResource(R.drawable.list_item_odd_bg);
									}
								}
							}

						} else {

							Log.d("uncheck","-->"+selectedPosition);

							if (ctx instanceof InvoiceHeader || ctx instanceof ReceiptHeader || ctx instanceof CashInvoiceHeader || ctx instanceof SalesOrderHeader) {
								((View) holder.list_item_layout.getParent())
										.setBackgroundResource(R.drawable.list_grey_bg);
							}
							else if(ctx instanceof SalesReturnHeader)
							{
								((View) holder.list_item_layout.getParent())
										.setBackgroundResource(R.drawable.list_grey_bg);
							}
							else {
								if (id % 2 == 0) {
									((View) checkBox.getParent())
											.setBackgroundResource(R.drawable.list_item_even_bg);
								} else {
									((View) checkBox.getParent())
											.setBackgroundResource(R.drawable.list_item_odd_bg);
								}
							}
						}
					}
					if (ctx instanceof SalesOrderHeader) {
						((SalesOrderHeader) ctx).showViews(checkBox.isChecked(),
								R.id.printer);
					}
					if (ctx instanceof InvoiceHeader || ctx instanceof CashInvoiceHeader) {
						if (ctx instanceof InvoiceHeader) {
							((InvoiceHeader) ctx).showViews(checkBox.isChecked(),
									R.id.printer);
						} else if (ctx instanceof CashInvoiceHeader) {
							((CashInvoiceHeader) ctx).showViews(checkBox.isChecked(),
									R.id.printer);
						}

					}
					if (ctx instanceof DeliveryOrderHeader) {
						((DeliveryOrderHeader) ctx).showViews(checkBox.isChecked(), R.id.printer);
					}
					if (ctx instanceof SalesReturnHeader) {
						((SalesReturnHeader) ctx).showViews(checkBox.isChecked(), R.id.printer);
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
					if (holder.checkBox.isChecked()) {
						RowItem.setPrintoption("True");

					} else {
						RowItem.setPrintoption("False");

					}
				}
			});


			holder.so_sno.setText(so.getSno());
			holder.so_date.setText(so.getDate());


			/********Based on ShowPriceDO Amount will Gone or Visible *********/
			if (ctx instanceof DeliveryOrderHeader) {
				if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")) {
					holder.so_amount.setVisibility(View.VISIBLE);
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


			} else if (ctx instanceof SalesOrderHeader) {
				/*** Amount will Gone or Visible Based on Hide Price From GetUserPermission  ***/
				if (FormSetterGetter.getHidePrice() != null && !FormSetterGetter.getHidePrice().isEmpty()) {
					if (FormSetterGetter.getHidePrice().matches("Hide Price")) {
						holder.so_amount.setVisibility(View.GONE);
					} else {
						holder.so_amount.setVisibility(View.VISIBLE);
					}
				} else {
					holder.so_amount.setVisibility(View.VISIBLE);
				}
			}


			if ( ctx instanceof StockRequestHeader /*||
				ctx instanceof ReceiptHeader*/) {
				holder.so_sno.setVisibility(View.GONE);
			}

			if ( ctx instanceof SalesReturnHeader /*||
				ctx instanceof ReceiptHeader*/) {
				holder.so_sno.setVisibility(View.VISIBLE);
				holder.balance_amount = (TextView) convertView.findViewById(R.id.balance_amount);
				holder.balance_amount.setText(so.getBalanceamount());
				holder.so_status = (TextView) convertView.findViewById(R.id.status);
				holder.so_status.setText(so.getSalesType());
				holder.customeraddresslayout = (LinearLayout) convertView.findViewById(R.id.customeraddresslayout);
				holder.customeraddresslayout.setVisibility(View.GONE);
			}

			if(ctx instanceof TransferHeader){
				holder.so_sno.setText(so.getSno());
				holder.remarks.setText(so.getRemarks1());
			}


			if (ctx instanceof StockRequestHeader || ctx instanceof TransferHeader) {
				holder.so_custcode.setText(so.getFromlocation());
				holder.so_amount.setText(so.getTolocation());
			} else {

				if (ctx instanceof InvoiceHeader || /*ctx instanceof SalesOrderHeader ||*/ ctx instanceof DeliveryOrderHeader
						|| ctx instanceof CashInvoiceHeader) {

					if (ctx instanceof InvoiceHeader) {
						holder.IsClosed_status.setText(so.getIsClosed());
						holder.IsPosted_status.setText(so.getIsPosted());
					}

					if (showView.matches("true")) {
						holder.so_sno.setVisibility(View.GONE);
						holder.so_custcode.setText(so.getCustomerName());
					} else {
						holder.so_sno.setVisibility(View.VISIBLE);
						holder.so_custcode.setText(so.getCustomerCode());
					}

					if (ctx instanceof InvoiceHeader) {
						holder.so_sno.setVisibility(View.VISIBLE);
						holder.so_custcode.setText(so.getCustomerName());
						holder.balance_amount.setText(so.getBalanceamount());
						holder.customeraddresslayout.setVisibility(View.GONE);
						/*if(so.getCustomeraddress1().matches("") && so.getCustomeraddress2().matches("") && so.getCustomeraddress3().matches(""))
						{
							holder.customeraddresslayout.setVisibility(View.GONE);
						}
						else
						{
							holder.customeraddress.setText(so.getCustomeraddress1()+" "+so.getCustomeraddress2()+" "+so.getCustomeraddress3());
						}*/


						String invoicesigned = so.getInvoiceSigned();
						String doNo =so.getDono();

//						if(!doNo.isEmpty()){
//							Log.d("Deliveryorder","-->"+doNo);
//							holder.invoice_signed.setTextColor(Color
//									.parseColor("#2BDF73"));
//							holder.invoice_signed.setText("Delivered");
//						}else{
//							holder.invoice_signed.setTextColor(Color
//									.parseColor("#000000"));
//							holder.invoice_signed.setText("Not Delivered");
//						}
//						if (invoicesigned != null && !invoicesigned.isEmpty()) {
//
							if (invoicesigned.matches("1")|| ! (doNo.isEmpty())) {
								Log.d("Deliveryorder","-->"+doNo);
								holder.invoice_signed.setTextColor(Color
										.parseColor("#2BDF73"));
								holder.invoice_signed.setText("Delivered");
							} else {
								holder.invoice_signed.setTextColor(Color
										.parseColor("#000000"));
								holder.invoice_signed.setText("Not Delivered");
							}

//						}

						if (status != null) {

							if (status.matches(paid)) {
								holder.so_status.setText("Paid");
								holder.so_status.setTextColor(Color
										.parseColor("#2BDF73"));
								holder.overdue_txt.setVisibility(View.GONE);
							} else if (status.matches(notpaid)) {
								holder.so_status.setText("Not Paid");
								holder.so_status.setTextColor(Color
										.parseColor("#F34945"));
								holder.overdue_txt.setVisibility(View.GONE);
							} else if (status.matches(podpending)) {
								holder.so_status.setText("POD Pending");
								holder.so_status.setTextColor(Color
										.parseColor("#F34945"));
								holder.overdue_txt.setVisibility(View.GONE);
							} else if (status.matches(overdue)) {
								holder.so_status.setText("Not Paid");
								holder.overdue_txt.setVisibility(View.VISIBLE);
								holder.overdue_txt.setText("- Overdue Days : " + so.getOverdueDays());
								holder.so_status.setTextColor(Color
										.parseColor("#F34945"));
							}
						}

					}
                    if (ctx instanceof CashInvoiceHeader) {
                        holder.so_sno.setVisibility(View.VISIBLE);
                        holder.so_custcode.setText(so.getCustomerName());
                        holder.balance_amount.setText(so.getBalanceamount());

                        String invoicesigned = so.getInvoiceSigned();
                        String doNo =so.getDono();

//						if(!doNo.isEmpty()){
//							Log.d("Deliveryorder","-->"+doNo);
//							holder.invoice_signed.setTextColor(Color
//									.parseColor("#2BDF73"));
//							holder.invoice_signed.setText("Delivered");
//						}else{
//							holder.invoice_signed.setTextColor(Color
//									.parseColor("#000000"));
//							holder.invoice_signed.setText("Not Delivered");
//						}
//						if (invoicesigned != null && !invoicesigned.isEmpty()) {
//
                        if (invoicesigned.matches("1")|| ! (doNo.isEmpty())) {
                            Log.d("Deliveryorder","-->"+doNo);
                            holder.invoice_signed.setTextColor(Color
                                    .parseColor("#2BDF73"));
                            holder.invoice_signed.setText("Delivered");
                        } else {
                            holder.invoice_signed.setTextColor(Color
                                    .parseColor("#000000"));
                            holder.invoice_signed.setText("Not Delivered");
                        }

//						}

                        if (status != null) {

                            if (status.matches(paid)) {
                                holder.so_status.setText("Paid");
                                holder.so_status.setTextColor(Color
                                        .parseColor("#2BDF73"));
                                holder.overdue_txt.setVisibility(View.GONE);
                            } else if (status.matches(notpaid)) {
                                holder.so_status.setText("Not Paid");
                                holder.so_status.setTextColor(Color
                                        .parseColor("#F34945"));
                                holder.overdue_txt.setVisibility(View.GONE);
                            } else if (status.matches(podpending)) {
                                holder.so_status.setText("POD Pending");
                                holder.so_status.setTextColor(Color
                                        .parseColor("#F34945"));
                                holder.overdue_txt.setVisibility(View.GONE);
                            } else if (status.matches(overdue)) {
                                holder.so_status.setText("Not Paid");
                                holder.overdue_txt.setVisibility(View.VISIBLE);
                                holder.overdue_txt.setText("- Overdue Days : " + so.getOverdueDays());
                                holder.so_status.setTextColor(Color
                                        .parseColor("#F34945"));
                            }
                        }

                    }

				} else {
					if (ctx instanceof SalesOrderHeader) {
						if (showView.matches("true")) {
							holder.so_sno.setVisibility(View.VISIBLE);
							holder.so_custcode.setText(so.getCustomerName());
							holder.so_status.setVisibility(View.VISIBLE);
							holder.so_sno.setText(so.getSno());
							holder.balance_amount = (TextView) convertView.findViewById(R.id.balance_amount);
							holder.balance_amount.setText(so.getSubTotal());
							holder.customeraddresslayout = (LinearLayout) convertView.findViewById(R.id.customeraddresslayout);
							holder.customeraddress = (TextView) convertView.findViewById(R.id.customeraddress);
							if(so.getCustomeraddress1().equals("") && so.getCustomeraddress2().equals("") && so.getCustomeraddress3().equals(""))
							{
								holder.customeraddresslayout.setVisibility(View.GONE);
							}
							else
							{
								holder.customeraddress.setText(so.getCustomeraddress1()+" "+so.getCustomeraddress2()+" "+so.getCustomeraddress3());
							}
						} else {
							holder.so_sno.setVisibility(View.GONE);
							holder.so_custcode.setText(so.getCustomerCode());
							holder.so_status.setVisibility(View.VISIBLE);
							holder.balance_amount = (TextView) convertView.findViewById(R.id.balance_amount);
							holder.balance_amount.setText(so.getSubTotal());
							holder.customeraddresslayout = (LinearLayout) convertView.findViewById(R.id.customeraddresslayout);
							holder.customeraddress = (TextView) convertView.findViewById(R.id.customeraddress);
							if(so.getCustomeraddress1().equals("") && so.getCustomeraddress2().equals("") && so.getCustomeraddress3().equals(""))
							{
								holder.customeraddresslayout.setVisibility(View.GONE);
							}
							else
							{
								holder.customeraddress.setText(so.getCustomeraddress1()+" "+so.getCustomeraddress2()+" "+so.getCustomeraddress3());
							}
						}
					} else if (ctx instanceof ReceiptHeader) {
						holder.so_custcode.setText(so.getCustomerName());
					} else {
						if(ctx instanceof SalesReturnHeader){
							holder.so_custcode.setText(so.getCustomerName());
						}else{
							holder.so_custcode.setText(so.getCustomerCode());
						}
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
			if (ctx instanceof DeliveryVerificationHeader) {
				holder.so_sno.setVisibility(View.GONE);
				holder.so_date.setVisibility(View.GONE);
				holder.so_amount.setVisibility(View.GONE);
				holder.so_status.setVisibility(View.VISIBLE);
				holder.so_custcode.setText(so.getCustomerName());
			}

			if (ctx instanceof PackingHeader) {
				holder.checkBox.setVisibility(View.GONE);
				holder.so_custcode.setVisibility(View.VISIBLE);
				holder.so_custcode.setText(so.getCustomerName() + "(" + so.getCustomerCode() + ")");
				holder.so_sno.setText(so.getSno());
				holder.so_date.setText(so.getDate());
				holder.so_amount.setText(so.getRemarks1());
			}

			if (ctx instanceof SalesOrderHeader || ctx instanceof PackingHeader || ctx instanceof DeliveryVerificationHeader
					|| ctx instanceof StockRequestHeader
					|| ctx instanceof TransferHeader || ctx instanceof ExpenseHeader) {
				holder.so_status.setText(so.getStatus());
			}

			if (ctx instanceof InvoiceHeader || ctx instanceof CashInvoiceHeader || ctx instanceof SalesOrderHeader) {
				if (status != null) {
					if (status.matches(paid)) {

//					if (position % 2 == 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_grey_select);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_grey_bg);
						}
						/*holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_date
								.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#2BDF73"));
						holder.so_amount.setTextColor(Color
								.parseColor("#2BDF73"));*/
//					} else {
						/*if (so.isSelected()) {
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
								.parseColor("#2BDF73"));*/
//					}
					} else if (status.matches(podpending)) {
//					if (position % 2 == 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_grey_select);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_grey_bg);
						}
//						holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
//						holder.so_date
//								.setTextColor(Color.parseColor("#2BDF73"));
//						holder.so_custcode.setTextColor(Color
//								.parseColor("#2BDF73"));
//						holder.so_amount.setTextColor(Color
//								.parseColor("#2BDF73"));
//					} else {
						/*if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_item_odd_bg);
						}*/
//						holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
//						holder.so_date
//								.setTextColor(Color.parseColor("#2BDF73"));
//						holder.so_custcode.setTextColor(Color
//								.parseColor("#2BDF73"));
//						holder.so_amount.setTextColor(Color
//								.parseColor("#2BDF73"));
//					}
					} else if (status.matches(notpaid)) {
//					if (position % 2 == 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_grey_select);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_grey_bg);
						}
						/*holder.so_sno.setTextColor(Color.parseColor("#F34945"));
						holder.so_date
								.setTextColor(Color.parseColor("#F34945"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#F34945"));
						holder.so_amount.setTextColor(Color
								.parseColor("#F34945"));*/
//					} else {
						/*if (so.isSelected()) {
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
								.parseColor("#F34945"));*/
//					}
					} else if (status.matches(overdue)) {
//					if (position % 2 == 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_grey_select);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_grey_bg);
						}
					} else if (status.matches(all)) {
						String totlbal = so.getBalanceamount();

						if (totlbal != null && !totlbal.isEmpty()) {

						} else {
							totlbal = "";
						}

						double totalbalance;
						if ((totlbal == null) || (totlbal == "")) {
							totalbalance = 0.0;
						} else {
							totalbalance = Double.valueOf(totlbal);
						}

						if (totalbalance > 0) {
							if (so.isSelected()) {
								convertView
										.setBackgroundResource(R.drawable.list_grey_select);
							} else {
								convertView
										.setBackgroundResource(R.drawable.list_grey_bg);
							}
						/*holder.so_sno.setTextColor(Color.parseColor("#F34945"));
						holder.so_date
								.setTextColor(Color.parseColor("#F34945"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#F34945"));
						holder.so_amount.setTextColor(Color
								.parseColor("#F34945"));*/

							holder.so_status.setText("Not Paid");
							holder.so_status.setTextColor(Color
									.parseColor("#F34945"));
						} else {
							if (so.isSelected()) {
								convertView
										.setBackgroundResource(R.drawable.list_grey_select);
							} else {
								convertView
										.setBackgroundResource(R.drawable.list_grey_bg);
							}
						/*holder.so_sno.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_date
								.setTextColor(Color.parseColor("#2BDF73"));
						holder.so_custcode.setTextColor(Color
								.parseColor("#2BDF73"));
						holder.so_amount.setTextColor(Color
								.parseColor("#2BDF73"));*/

							holder.so_status.setText("Paid");
							holder.so_status.setTextColor(Color
									.parseColor("#2BDF73"));
						}

					}
				} else {
					if (position % 2 == 0) {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_grey_select);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_grey_bg);
						}
					/*holder.so_sno.setTextColor(Color.parseColor("#035994"));
					holder.so_date.setTextColor(Color.parseColor("#035994"));
					holder.so_custcode
							.setTextColor(Color.parseColor("#035994"));
					holder.so_amount.setTextColor(Color.parseColor("#035994"));*/
					} else {
						if (so.isSelected()) {
							convertView
									.setBackgroundResource(R.drawable.list_grey_select);
						} else {
							convertView
									.setBackgroundResource(R.drawable.list_grey_bg);
						}

					/*holder.so_sno.setTextColor(Color.parseColor("#646464"));
					holder.so_date.setTextColor(Color.parseColor("#646464"));
					holder.so_custcode
							.setTextColor(Color.parseColor("#646464"));
					holder.so_amount.setTextColor(Color.parseColor("#646464"));*/
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
				} else {
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
						convertView
								.setBackgroundResource(R.drawable.list_item_selected_bg);
					} else {
						if (so.getGotSignatureOnDO().toString().matches("True")) {
							convertView.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView.setBackgroundResource(R.drawable.list_item_even_bg);
						}
					}
					holder.so_sno.setTextColor(Color.parseColor("#035994"));
					holder.so_date.setTextColor(Color.parseColor("#035994"));
					holder.so_custcode.setTextColor(Color.parseColor("#035994"));
					holder.so_amount.setTextColor(Color.parseColor("#035994"));

				} else {

					if (so.isSelected()) {
						convertView
								.setBackgroundResource(R.drawable.list_item_selected_bg);
					} else {
						if (so.getGotSignatureOnDO().toString().matches("True")) {
							convertView.setBackgroundResource(R.drawable.list_item_selected_bg);
						} else {
							convertView.setBackgroundResource(R.drawable.list_item_odd_bg);
						}
					}
					holder.so_sno.setTextColor(Color.parseColor("#646464"));
					holder.so_date.setTextColor(Color.parseColor("#646464"));
					holder.so_custcode.setTextColor(Color.parseColor("#646464"));
					holder.so_amount.setTextColor(Color.parseColor("#646464"));

				}


			} else if (ctx instanceof ReceiptHeader) {

				if (so.isSelected()) {
					convertView
							.setBackgroundResource(R.drawable.list_grey_select);
				} else {
					convertView
							.setBackgroundResource(R.drawable.list_grey_bg);
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
					holder.so_custcode.setTextColor(Color.parseColor("#035994"));
					holder.so_amount.setTextColor(Color.parseColor("#035994"));
					if (ctx instanceof TransferHeader){
						holder.txt_fromloc.setTextColor(Color.parseColor("#035994"));
						holder.txt_toloc.setTextColor(Color.parseColor("#035994"));
						holder.txt_remarks.setTextColor(Color.parseColor("#035994"));
					}
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

					if (ctx instanceof TransferHeader){
						holder.txt_fromloc.setTextColor(Color.parseColor("#035994"));
						holder.txt_toloc.setTextColor(Color.parseColor("#035994"));
						holder.txt_remarks.setTextColor(Color.parseColor("#035994"));
					}
					if (ctx instanceof SalesOrderHeader
							|| ctx instanceof PackingHeader
							|| ctx instanceof StockRequestHeader
							|| ctx instanceof TransferHeader
							|| ctx instanceof ExpenseHeader) {
						holder.so_status.setTextColor(Color.parseColor("#646464"));
					}
				}

				if(ctx instanceof SalesReturnHeader || ctx instanceof SalesOrderHeader)
				{
					convertView.setBackgroundResource(R.drawable.list_grey_bg);

					if (so.isSelected()) {
						convertView
								.setBackgroundResource(R.drawable.list_grey_select);
					} else {
						convertView
								.setBackgroundResource(R.drawable.list_grey_bg);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
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
		Log.d("selectAll","-->"+select);
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
		CheckBox checkBox;
		TextView so_sno,remarks,txt_fromloc,txt_toloc,txt_remarks;
		TextView so_date;
		TextView so_custcode;
		TextView so_amount;
		TextView so_status;
		LinearLayout so_layout2;
		TextView so_delCustomerName;
		TextView balance_amount;
		LinearLayout list_item_layout,status_layout;
		TextView overdue_txt;
		TextView invoice_signed;
		TextView IsPosted_status;
		TextView IsClosed_status;
		TextView customeraddress;
		LinearLayout customeraddresslayout;
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
