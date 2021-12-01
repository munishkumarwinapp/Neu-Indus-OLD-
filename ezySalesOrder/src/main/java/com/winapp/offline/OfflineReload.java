package com.winapp.offline;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;

import com.winapp.fwms.FormSetterGetter;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.SalesOrderSetGet;

public class OfflineReload {

	String routeMasterStr, goodsReceiveStr, deliveryOrderStr, salesOrderStr, packedStr, deliveryVerificationStr,
			salesReturnStr, productListStr, customerListStr, catalogStr,
			receiptsStr, settingsStr, invoiceStr, stockrequestStr, transferStr,
			stockTakeStr, swtichtoofflinestr, swtichtoonlinestr, uploadStr,
			stockAdjustmentStr, expenseStr, overdueStr, taskStr,merchandiseStr,merchandiseScheduleStr,offlineModeStr;
	ArrayList<String> landingMenuArr = new ArrayList<String>();

	public void OnlineloadingMenus() {

		landingMenuArr.clear();
		offlineModeStr =FormSetterGetter.getOfflineMode();
		merchandiseScheduleStr =FormSetterGetter.getMerchandiseSchedule();
		merchandiseStr =FormSetterGetter.getMerchandise();
		routeMasterStr = FormSetterGetter.getRouteMaster();
		goodsReceiveStr = FormSetterGetter.getGoodsReceive();
		salesOrderStr = FormSetterGetter.getSalesOrder();
		packedStr = "Packing";
		deliveryVerificationStr = "Delivery Verification";
		deliveryOrderStr = FormSetterGetter.getDeliveryOrder();
		invoiceStr = FormSetterGetter.getInvoice();
		salesReturnStr = FormSetterGetter.getSalesReturn();
		receiptsStr = FormSetterGetter.getReceipts();
		customerListStr = FormSetterGetter.getCustomerList();
		productListStr = FormSetterGetter.getProductList();
		catalogStr = FormSetterGetter.getCatalog();
		settingsStr = FormSetterGetter.getSettings();
		stockrequestStr = FormSetterGetter.getStockRequest();
		transferStr = FormSetterGetter.getTransfer();
		stockTakeStr = FormSetterGetter.getStockTake();
		stockAdjustmentStr = FormSetterGetter.getStockAdjustment();
		expenseStr = FormSetterGetter.getExpense();
		overdueStr = FormSetterGetter.getOverdue();
		taskStr = FormSetterGetter.getTasks();


		if (merchandiseScheduleStr.matches("Merchandise Schedule")) {   
			   String haveMerchandising = SalesOrderSetGet.getHaveMerchandising();
			   if(haveMerchandising.matches("1")){
			     landingMenuArr.add("Merchandise Schedule");
			   } 
			  }
		if (merchandiseStr.matches("Merchandise")) {			
			String haveMerchandising = SalesOrderSetGet.getHaveMerchandising();
			if(haveMerchandising.matches("1")){
				 landingMenuArr.add("Merchandise");
			} 
		}
		
		if (routeMasterStr.matches("Route")) {
			String routePermission = SalesOrderSetGet.getRoutepermission();
			if (routePermission.matches("True")) {
				landingMenuArr.add("Route");
			}
		}

		if (goodsReceiveStr.matches("Goods Receive")) {
			landingMenuArr.add("Goods Receive");
		}

		if (salesOrderStr.matches("Sales Order")) {
			landingMenuArr.add("Sales Order");
		}
		
		if (packedStr.matches("Packing")) {
			String haveDeliveryVerification = MobileSettingsSetterGetter.getHaveDeliveryVerification();
			if(haveDeliveryVerification.matches("True")){
				landingMenuArr.add("Packing");
			}
		}	
		
		if (deliveryVerificationStr.matches("Delivery Verification")) {
			String haveDeliveryVerification = MobileSettingsSetterGetter.getHaveDeliveryVerification();
			if(haveDeliveryVerification.matches("True")){
				landingMenuArr.add("Delivery Verification");
			}
		}	

		if (deliveryOrderStr.matches("Delivery Order")) {
			landingMenuArr.add("Delivery Order");
		}

		if (invoiceStr.matches("Invoice")) {
			landingMenuArr.add("Invoice");
		}

		if (salesReturnStr.matches("Sales Return")) {
			landingMenuArr.add("Sales Return");
		}

		if (receiptsStr.matches("Receipts")) {
			landingMenuArr.add("Receipts");
		}

		if (customerListStr.matches("Customer List")) {
			landingMenuArr.add("Customer List");
		}

		if (productListStr.matches("Product Stock")) {
			landingMenuArr.add("Product Stock");
		}

		if (stockrequestStr.matches("Stock Request")) {
			landingMenuArr.add("Stock Request");
		}

		if (transferStr.matches("Transfer")) {
			landingMenuArr.add("Transfer");
		}

		if (stockTakeStr.matches("Stock Take")) {
			landingMenuArr.add("Stock Take");
		}

		if (stockAdjustmentStr.matches("Stock Adjustment")) {
			landingMenuArr.add("Stock Adjustment");
		}

		if (expenseStr.matches("Expense")) {
			landingMenuArr.add("Expense");
		}

		if (overdueStr.matches("Overdue Invoices")) {
			landingMenuArr.add("Overdue Invoices");
		}
		
		if (taskStr.matches("Tasks")) {
			landingMenuArr.add("Task");
		}

		if (catalogStr.matches("Catalog")) {
			landingMenuArr.add("Catalog");
		}

		if (settingsStr.matches("Settings")) {
			landingMenuArr.add("Settings");
		}

		/*if (offlineModeStr.matches("Offline Mode")) {
			landingMenuArr.add("Offline Mode");
		}*/

		landingMenuArr.add("Exit");
		SalesOrderSetGet.setLandingMenuArr(landingMenuArr);
		Log.d("UserGroup", "" + landingMenuArr.toString());
	}

	public void OfflineloadingMenus() {

		landingMenuArr.clear();
		offlineModeStr =FormSetterGetter.getOfflineMode();
		merchandiseScheduleStr =FormSetterGetter.getMerchandiseSchedule();
		merchandiseStr =FormSetterGetter.getMerchandise();
		routeMasterStr = FormSetterGetter.getRouteMaster();
		goodsReceiveStr = FormSetterGetter.getGoodsReceive();
		salesOrderStr = FormSetterGetter.getSalesOrder();
		packedStr ="";
		deliveryVerificationStr = "";
		deliveryOrderStr = FormSetterGetter.getDeliveryOrder();
		invoiceStr = FormSetterGetter.getInvoice();
		salesReturnStr = FormSetterGetter.getSalesReturn();
		receiptsStr = FormSetterGetter.getReceipts();
		customerListStr = FormSetterGetter.getCustomerList();
		productListStr = FormSetterGetter.getProductList();
		catalogStr = FormSetterGetter.getCatalog();
		settingsStr = FormSetterGetter.getSettings();
		stockrequestStr = FormSetterGetter.getStockRequest();
		transferStr = FormSetterGetter.getTransfer();
		stockTakeStr = FormSetterGetter.getStockTake();
		stockAdjustmentStr = FormSetterGetter.getStockAdjustment();
		expenseStr = FormSetterGetter.getExpense();
		overdueStr = FormSetterGetter.getOverdue();
		taskStr = FormSetterGetter.getTasks();

		routeMasterStr = "";
		goodsReceiveStr = "";
		// salesOrderStr = "";
		deliveryOrderStr = "";
		// invoiceStr = "";
		salesReturnStr = "";
		// receiptsStr = "";
		productListStr = "";
//		catalogStr = "";
		// settingsStr = "";
		stockrequestStr = "";
		transferStr = "";
		stockTakeStr = "";
		stockAdjustmentStr = "";
		expenseStr = "";
		overdueStr = "";
		taskStr="";
		merchandiseStr ="";
		merchandiseScheduleStr ="";


		if (merchandiseScheduleStr.matches("Merchandise Schedule")) {
			   String haveMerchandising = SalesOrderSetGet.getHaveMerchandising();
			   if(haveMerchandising.matches("1")){
			     landingMenuArr.add("Merchandise Schedule");
			   } 
			  }
		if (merchandiseStr.matches("Merchandise")) {
			String haveMerchandising = SalesOrderSetGet.getHaveMerchandising();
			if(haveMerchandising.matches("1")){
				 landingMenuArr.add("Merchandise");
			} 
		}
		
		if (routeMasterStr.matches("Route")) {
			String routePermission = SalesOrderSetGet.getRoutepermission();
			if (routePermission.matches("True")) {
				landingMenuArr.add("Route");
			}
		}

		if (goodsReceiveStr.matches("Goods Receive")) {
			landingMenuArr.add("Goods Receive");
		}

		if (salesOrderStr.matches("Sales Order")) {
			landingMenuArr.add("Sales Order");
		}

		if (deliveryOrderStr.matches("Delivery Order")) {
			landingMenuArr.add("Delivery Order");
		}

		if (invoiceStr.matches("Invoice")) {
			landingMenuArr.add("Invoice");
		}

		if (salesReturnStr.matches("Sales Return")) {
			landingMenuArr.add("Sales Return");
		}

		if (receiptsStr.matches("Receipts")) {
			landingMenuArr.add("Receipts");
		}

		if (customerListStr.matches("Customer List")) {
			landingMenuArr.add("Customer List");
		}

		if (productListStr.matches("Product Stock")) {
			landingMenuArr.add("Product Stock");
		}

		if (stockrequestStr.matches("Stock Request")) {
			landingMenuArr.add("Stock Request");
		}

		if (transferStr.matches("Transfer")) {
			landingMenuArr.add("Transfer");
		}

		if (stockTakeStr.matches("Stock Take")) {
			landingMenuArr.add("Stock Take");
		}

		if (stockAdjustmentStr.matches("Stock Adjustment")) {
			landingMenuArr.add("Stock Adjustment");
		}

		if (expenseStr.matches("Expense")) {
			landingMenuArr.add("Expense");
		}

		if (overdueStr.matches("Overdue Invoices")) {
			landingMenuArr.add("Overdue Invoices");
		}
		
		if (taskStr.matches("Tasks")) {
			landingMenuArr.add("Task");
		}

		if (catalogStr.matches("Catalog")) {
			landingMenuArr.add("Catalog");
		}

		if (settingsStr.matches("Settings")) {
			landingMenuArr.add("Settings");
		}

		/*if (offlineModeStr.matches("Offline Mode")) {
			landingMenuArr.add("Offline Mode");
		}*/

		landingMenuArr.add("Exit");
		SalesOrderSetGet.setLandingMenuArr(landingMenuArr);
		Log.d("UserGroup", "" + landingMenuArr.toString());
	}

}
