package com.winapp.sot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.winapp.catalog.SOCatalogActivity;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.Settings;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Catalog;
import com.winapp.offline.OfflineDataDownloader;
import com.winapp.sotdetails.CRMTaskActivity;
import com.winapp.sotdetails.CustomerListActivity;
import com.winapp.sotdetails.DBCatalog;
import com.winapp.sotdetails.DeliveryVerificationHeader;
import com.winapp.sotdetails.ExpenseHeader;
import com.winapp.sotdetails.OverdueHeader;
import com.winapp.sotdetails.PackingHeader;
import com.winapp.sotdetails.ProductAnalysisActivity;
import com.winapp.sotdetails.ProductStockActivity;
import com.winapp.trackwithmap.DeliveryOrderNewHeader;

public class SlideMenuFragment extends SherlockFragment {
	ListView list;
	MenuClickInterFace mClick;
	TextView salesorder_txt, product_txt, customer_txt, invoice_txt,
			setting_txt;
	Intent intent;
	String[] slidemenuitem = new String[] { "Merchandise Schedule","Merchandise", "Route",
			"Goods Receive", "Sales Order","Delivery Order", "Invoice", "Sales Return", "Receipts",
			"Product Stock", "Product Analysis","Customer List", "Stock Request", "Transfer",
			"Stock Take", "Stock Adjustment", "Expense", "Overdue", "Task",
			"Catalog", "Settings", "Exit", "Packing","Delivery Verification","Consignment" };

	int[] slidemenuitemimage = new int[] { R.mipmap.slidemenu_merchandise,R.mipmap.slidemenu_merchandise,
			R.mipmap.slidemenu_routemaster,
			R.mipmap.slidemenu_goodsreceive, R.mipmap.slidemenu_salesorder,
			R.mipmap.slidemenu_deliveryorder, R.mipmap.slidemenu_invoice,
			R.mipmap.slidemenu_salesreturn, R.mipmap.slidemenu_receipt,
			R.mipmap.slidemenu_productstock, R.mipmap.slidemenu_productstock,R.mipmap.slidemenu_customer,
			R.mipmap.slidemenu_stockrequest, R.mipmap.slidemenu_transfer,
			R.mipmap.slidemenu_stock_take,
			R.mipmap.slidemenu_stock_adjustment,
			R.mipmap.slidemenu_expense, R.mipmap.slidemenu_overdue,
			R.mipmap.slidemenu_task, R.mipmap.slidemenu_catalog,
			R.mipmap.slidemenu_setting, R.mipmap.slidemenu_exit,R.mipmap.company_icon,R.mipmap.slidemenu_packing,
			R.mipmap.slidemenu_deliveryverification,R.mipmap.refresh_icon,R.mipmap.slidemenu_task,};

    private OfflineDataDownloader dataDownloader;
    String valid_url;

	public interface MenuClickInterFace {
		void onListItemClick(String item);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mClick = (MenuClickInterFace) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

        valid_url = FWMSSettingsDatabase.getUrl();

		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

		ArrayList<String> landingMenuArr = SalesOrderSetGet.getLandingMenuArr();

		Log.d("getLocationname","--->"+SalesOrderSetGet.getLocationname());


		HashMap<String, String> com_name = new HashMap<String, String>();
		com_name.put("menuname", Company.getCompanyName()+ " - "+"("+SalesOrderSetGet.getLocationname()+")");
		com_name.put("menuimage",Integer.toString(slidemenuitemimage[24]));
		aList.add(com_name);

        Log.d("CompanyNameDet",""+Company.getCompanyName());

		HashMap<String, String> refresh = new HashMap<String, String>();
        refresh.put("menuname", "Refresh");
        refresh.put("menuimage",Integer.toString(slidemenuitemimage[25]));
		aList.add(refresh);

		HashMap<String, String> hmstr = new HashMap<String, String>();
		hmstr.put("menuname", "Home");
		hmstr.put("menuimage", Integer.toString(R.mipmap.slidemenu_home));
		aList.add(hmstr);

		// Log.d("aList","-->"+aList.toString());
		Log.d("homeStr", "homeStr");
		Log.d("UserGroupArrSize", "" + landingMenuArr.size());

		for (int i = 0; i < landingMenuArr.size(); i++) {
			HashMap<String, String> hm = new HashMap<String, String>();

			String str = landingMenuArr.get(i);
			if (str.matches("Merchandise Schedule")) {
				hm.put("menuname", slidemenuitem[0]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[0]));

			}
			else if (str.matches("Merchandise")) {
				hm.put("menuname", slidemenuitem[1]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[1]));

			}

			else if (str.matches("Route")) {
				hm.put("menuname", slidemenuitem[2]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[2]));

				Log.d("routeMasterStr", "routeMasterStr");
			}

			else if (str.matches("Goods Receive")) {
				hm.put("menuname", slidemenuitem[3]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[3]));

				Log.d("goodsReceiveStr", "goodsReceiveStr");
			}

			else if (str.matches("Sales Order")) {
				hm.put("menuname", slidemenuitem[4]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[4]));

				Log.d("salesOrderStr", "salesOrderStr");
			}


			else if (str.matches("Delivery Order")) {
				hm.put("menuname", slidemenuitem[5]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[5]));

				Log.d("deliveryOrderStr", "deliveryOrderStr");
			}
			else if (str.matches("Invoice")) {
				hm.put("menuname", slidemenuitem[6]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[6]));
			}

			else if (str.matches("Sales Return")) {
				hm.put("menuname", slidemenuitem[7]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[7]));

				Log.d("salesReturnStr", "salesReturnStr");
			}

			else if (str.matches("Receipts")) {
				hm.put("menuname", slidemenuitem[8]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[8]));
			}

			else if (str.matches("Product Stock")) {
				hm.put("menuname", slidemenuitem[9]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[9]));
			}

			else if (str.matches("Product Analysis")) {
				hm.put("menuname", slidemenuitem[10]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[10]));
			}

			else if (str.matches("Customer List")) {
				hm.put("menuname", slidemenuitem[11]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[11]));
			}
			else if (str.matches("Stock Request")) {
				hm.put("menuname", slidemenuitem[12]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[12]));

				Log.d("stockrequestStr", "stockrequestStr");
			} else if (str.matches("Transfer")) {
				hm.put("menuname", slidemenuitem[13]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[13]));

				Log.d("transferStr", "transferStr");
			} else if (str.matches("Stock Take")) {
				hm.put("menuname", slidemenuitem[14]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[14]));
				Log.d("Stock TakeStr", "Stock Take");
			} else if (str.matches("Stock Adjustment")) {
				hm.put("menuname", slidemenuitem[15]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[15]));

			}

			else if (str.matches("Expense")) {
				hm.put("menuname", slidemenuitem[16]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[16]));

			} else if (str.matches("Overdue Invoices")) {
				hm.put("menuname", slidemenuitem[17]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[17]));

			} else if (str.matches("Task")) {
				hm.put("menuname", slidemenuitem[18]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[18]));

			} else if (str.matches("Catalog")) {
				hm.put("menuname", slidemenuitem[19]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[19]));

				Log.d("CatalogStr", "Catalog");
			} else if (str.matches("Settings")) {
				hm.put("menuname", slidemenuitem[20]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[20]));
			} else if (str.matches("Exit")) {
				hm.put("menuname", slidemenuitem[21]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[21]));
			}
			else if (str.matches("Packing")) {
				hm.put("menuname", slidemenuitem[22]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[22]));

				Log.d("deliveryVerificationStr", "deliveryVerificationStr");
			}

			else if (str.matches("Delivery Verification")) {
				hm.put("menuname", slidemenuitem[23]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[23]));

				Log.d("deliveryVerificationStr", "deliveryVerificationStr");
			}
			else if(str.matches("Consignment")){
				hm.put("menuname", slidemenuitem[24]);
				hm.put("menuimage", Integer.toString(slidemenuitemimage[26]));
				Log.d("Consignment", "Consignment");
			}
			aList.add(hm);
		}
		Log.d("aList", "-->" + aList.toString());
		String[] from = { "menuimage", "menuname" };
		int[] to = { R.id.slidemenu_img, R.id.slidemenu_txt };
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), aList,
				R.layout.slidemenulistitem, from, to);
		ListView listView = (ListView) getView().findViewById(
				R.id.slidemenu_listview);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {

				ListView lv = (ListView) arg0;

				TextView fishtextview = (TextView) arg0.getChildAt(
						position - lv.getFirstVisiblePosition()).findViewById(
						R.id.slidemenu_txt);

				String fieldname = fishtextview.getText().toString();
				mClick.onListItemClick(fieldname);

				mClick.onListItemClick(fieldname);

                if(fieldname.matches("Refresh")){
                    dataDownloader = new OfflineDataDownloader(getActivity(), valid_url);
                    dataDownloader.startDownload(true, "Downloading from server");
                }
                else if (fieldname.matches("Home")) {
					intent = new Intent(getActivity(), LandingActivity.class);
					startActivity(intent);
					getActivity().finish();

				}
				else if (fieldname.matches("Merchandise Schedule")) {
				     intent = new Intent(getActivity(), MerchandiseSchedule.class);
				     startActivity(intent);
				     getActivity().finish();

				    }
				else if (fieldname.matches("Merchandise")) {
					intent = new Intent(getActivity(), MerchandiseHeader.class);
					startActivity(intent);
					getActivity().finish();

				} else if (fieldname.matches("Route")) {
					intent = new Intent(getActivity(), RouteHeader.class);
					startActivity(intent);
					getActivity().finish();

				} else if (fieldname.matches("Goods Receive")) {
					intent = new Intent(getActivity(), GraHeader.class);
					startActivity(intent);
					getActivity().finish();

				} else if (fieldname.matches("Sales Order")) {
					intent = new Intent(getActivity(), SalesOrderHeader.class);
					startActivity(intent);
					getActivity().finish();
				
				}  else if (fieldname.matches("Packing")) {
			        intent = new Intent(getActivity(), PackingHeader.class);
			         startActivity(intent);
			     getActivity().finish();

				}  else if (fieldname.matches("Delivery Verification")) {
			        intent = new Intent(getActivity(), DeliveryVerificationHeader.class);
			         startActivity(intent);
			     getActivity().finish();
			      
			    }else if (fieldname.matches("Delivery Order")) {

					String mobileDoShowRoute = SalesOrderSetGet.getMobileDoShowRoute();

					if(mobileDoShowRoute!=null && !mobileDoShowRoute.isEmpty()){

					}else{
						mobileDoShowRoute="";
					}
					if(mobileDoShowRoute.matches("1")) {
						intent = new Intent(getActivity(),
								DeliveryOrderNewHeader.class);
						startActivity(intent);

						getActivity().finish();
					}
					else{
						intent = new Intent(getActivity(),
								DeliveryOrderHeader.class);
						startActivity(intent);
						getActivity().finish();
					}

				} else if (fieldname.matches("Invoice")) {
					intent = new Intent(getActivity(), InvoiceHeader.class);
					startActivity(intent);
					getActivity().finish();

				}

				else if (fieldname.matches("Sales Return")) {
					intent = new Intent(getActivity(), SalesReturnHeader.class);
					startActivity(intent);
					getActivity().finish();

				}

				else if (fieldname.matches("Receipts")) {
					intent = new Intent(getActivity(), ReceiptHeader.class);
					startActivity(intent);
					getActivity().finish();

				} else if (fieldname.matches("Product Stock")) {
					intent = new Intent(getActivity(),
							ProductStockActivity.class);
					startActivity(intent);
					getActivity().finish();

				}else if (fieldname.matches("Product Analysis")) {
					intent = new Intent(getActivity(),
							ProductAnalysisActivity.class);
					startActivity(intent);
					getActivity().finish();

				} else if (fieldname.matches("Customer List")) {
					intent = new Intent(getActivity(),
							CustomerListActivity.class);
					startActivity(intent);
					getActivity().finish();

				} else if (fieldname.matches("Stock Request")) {
					intent = new Intent(getActivity(), StockRequestHeader.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Transfer")) {
					intent = new Intent(getActivity(), TransferHeader.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Stock Take")) {
					intent = new Intent(getActivity(), StockTakeHeader.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Stock Adjustment")) {
					intent = new Intent(getActivity(),
							StockAdjustmentHeader.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Expense")) {
					intent = new Intent(getActivity(), ExpenseHeader.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Overdue")) {
					intent = new Intent(getActivity(), OverdueHeader.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Task")) {
					intent = new Intent(getActivity(), CRMTaskActivity.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Consignment")) {
					intent = new Intent(getActivity(), ConsignmentHeader.class);
					startActivity(intent);
					getActivity().finish();
				}
				else if (fieldname.matches("Catalog")) {

					Catalog.setCustomerCode("");
					Catalog.setCustomerName("");
					Catalog.setCustomerGroupCode("");
					DBCatalog.deleteAllProduct();

					intent = new Intent(getActivity(), SOCatalogActivity.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Settings")) {
					intent = new Intent(getActivity(), Settings.class);
					startActivity(intent);
					getActivity().finish();
				} else if (fieldname.matches("Exit")) {
					alertDialogExit();
				}

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.slidemenulist, container, false);

		return v;
	}

	public void alertDialogExit() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle("Exit");
		alertDialog.setMessage("Do you want to Exit");
		alertDialog.setIcon(R.mipmap.slidemenu_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getActivity().finish();
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
