package com.winapp.sot;

import java.util.ArrayList;
import java.util.HashMap;

public class SalesOrderSetGet {
	static String locationcode;
	static String locationname;
	static String saleorderdate = "";
	static String deliverydate = "";
	static String customercode;
	static String currencycode = "";
	static String currencyname = "";
	static String currencyrate = "";
	static String remarks="";
	static String status;
	static String customername;
	static String customerCode;
	static String generalsetting;
	static String grainvoiceno;
	static String gradono;
	static String grainvoicedate;
	static String gradodate;
	static String companytax;
	static String companytaxvalue;
	static String suppliertax;
	static double totTaxAmt = 0;
	static String suppliercode;
	static String suppliergroupcode = "";
	static String srinvoiceno;
	static ArrayList<String> from_location;
	// static ArrayList<String> lst_location;
	static String transferchangefromloc = "";
	static String custname = "";
	static String taxValue = "0.00";
	static String transferfromloc = "";
	static String cartonpriceflag = "";
	static String currentdate = "";
	static String catalogtabposition = "0";
	static String balanceAmount = "0";
	static String netTotal = "0";
	static String receiptoninvoice = "";
	static String sCollectCash;
	static String header_flag;
	static ArrayList<String> landingMenuArr;
	static String mobileproductstockprint = "";
	static String soRemarks="";
	static String stockTakeNo = "";
	static String autoBatchNo="";
	static String haveBatchOnStockIn;
	static String NextBatchNo;  
	static String haveBatchOnTransfer;
	static String fromLoc;
	static String toLoc;
	static String haveBatchOnTransferToLocation;
	static String haveBatchOnStockAdjustment;
	static String stocktotransfer="";
	static String invoiceprintdetail="";	
	static String routepermission="";
	static String mobileloginpage="";
	static String enablecustomercode="";
	static String mobileoverduealert="";
	static String soAdditionalInfo="";
	static String tranblocknegativestock;
	static String tranblockinvoiceabovelimit;
	static String serverDate;
	static String haveTracking;
	static String haveMerchandising;
	static String doQtyValidateWithSo;
	static String schedulingType;
	static String haveMultipleCustomerPrice;
	static String loginPhoneNo="";
	static String HaveBatchOnStockOut="";
	static String serverDateTime="";
	static String localCurrency ="";
	static String taxCode ="";
	static String taxType ="";
	static String taxPerc ="";
	static String tranBlockTerms ="";
	static String tranBlockCreditLimit ="";
	static String malaysiaShowGST ="";
	static String appPrintGroup ="";
	static String haveemailintegration ="";
	static String customerTaxPerc ="";
	static String haveAttribute ="";
    static String showUnitCostStockTake = "";
	static String showAddToCart = "";
	static String mobileShowCodeOnSearch = "";
	static String selfOrderShowProductCode = "";
	static String mobileHaveOfflineMode = "";
	static String receiptAutoCreditAmount = "";
	static String hostingValidation="";
	static String customerHaveCashbill="";
	static String mobileDoShowRoute="";
	static String isShowCost ="";
	static String duration ="";
	static String avg_cost="";
	static String orderNo ="";
	static String orderDate ="";
	static String statusCheck="";
	static String haveBatchOnConsignmentOut ="";
	static double con_qty;
	static String name ="";
	static ArrayList<String> consignmentNo;
	static String flag="";
	static String shortCode ="";
	static String tranType="";
	static String click="";
	static String address="";
	static String total="";
	static String sub_tot ="";
	static String total_disc ="";
	static String subTot=" ";
	static String tax =" ";
	static String net_tot=" ";
	static ArrayList<String> sl_No;
	static ArrayList<HashMap<String, String>> hashMap;
	static ArrayList<HashMap<String, String>> hashMap_list;
	static String stockRefNo;
	static String SlNo;
	static String ret_qty;
	static String result_value;
	static String cust_code;
	static String cust_name;
	static String con_no;
	static int sl_no;
	static String type="";
	static String cQty="";
	static String outStanding ="";
	static String havePos ="";
	static String mobile_dateChange;
	static String payTo;
	static String doNo;
	static String expense_dateChange;
	static String transfer_stockReq;
	static String carton_quantity;
	public static String getMobileShowCodeOnSearch() {
		return mobileShowCodeOnSearch;
	}

	public static void setMobileShowCodeOnSearch(String mobileShowCodeOnSearch) {
		SalesOrderSetGet.mobileShowCodeOnSearch = mobileShowCodeOnSearch;
	}

	public static String getShowAddToCart() {
		return showAddToCart;
	}

	public static void setShowAddToCart(String showAddToCart) {
		SalesOrderSetGet.showAddToCart = showAddToCart;
	}

	public static String getCustomerTaxPerc() {
		return customerTaxPerc;
	}

	public static void setCustomerTaxPerc(String customerTaxPerc) {
		SalesOrderSetGet.customerTaxPerc = customerTaxPerc;
	}

	public static String getHaveemailintegration() {
		return haveemailintegration;
	}

	public static void setHaveemailintegration(String haveemailintegration) {
		SalesOrderSetGet.haveemailintegration = haveemailintegration;
	}

	public static String getMalaysiaShowGST() {
		return malaysiaShowGST;
	}

	public static void setMalaysiaShowGST(String malaysiaShowGST) {
		SalesOrderSetGet.malaysiaShowGST = malaysiaShowGST;
	}

	public static String getTranBlockCreditLimit() {
		return tranBlockCreditLimit;
	}

	public static void setTranBlockCreditLimit(String tranBlockCreditLimit) {
		SalesOrderSetGet.tranBlockCreditLimit = tranBlockCreditLimit;
	}

	public static String getTaxType() {
		return taxType;
	}

	public static void setTaxType(String taxType) {
		SalesOrderSetGet.taxType = taxType;
	}

	public static String getTaxPerc() {
		return taxPerc;
	}

	public static void setTaxPerc(String taxPerc) {
		SalesOrderSetGet.taxPerc = taxPerc;
	}

	public static String getTaxCode() {
		return taxCode;
	}

	public static void setTaxCode(String taxCode) {
		SalesOrderSetGet.taxCode = taxCode;
	}

	public static String getLocalCurrency() {
		return localCurrency;
	}

	public static void setLocalCurrency(String localCurrency) {
		SalesOrderSetGet.localCurrency = localCurrency;
	}

	public static String getHaveBatchOnStockOut() {
		return HaveBatchOnStockOut;
	}

	public static void setHaveBatchOnStockOut(String haveBatchOnStockOut) {
		HaveBatchOnStockOut = haveBatchOnStockOut;
	}

	public static String getHaveMultipleCustomerPrice() {
		return haveMultipleCustomerPrice;
	}

	public static void setHaveMultipleCustomerPrice(String haveMultipleCustomerPrice) {
		SalesOrderSetGet.haveMultipleCustomerPrice = haveMultipleCustomerPrice;
	}

	public static String getHaveMerchandising() {
		return haveMerchandising;
	}

	public static void setHaveMerchandising(String haveMerchandising) {
		SalesOrderSetGet.haveMerchandising = haveMerchandising;
	}

	public static String getHaveTracking() {
		return haveTracking;
	}

	public static void setHaveTracking(String haveTracking) {
		SalesOrderSetGet.haveTracking = haveTracking;
	}

	public static String getServerDate() {
		return serverDate;
	}

	public static void setServerDate(String serverDate) {
		SalesOrderSetGet.serverDate = serverDate;
	}

	public static String getTranblockinvoiceabovelimit() {
		return tranblockinvoiceabovelimit;
	}

	public static void setTranblockinvoiceabovelimit(
			String tranblockinvoiceabovelimit) {
		SalesOrderSetGet.tranblockinvoiceabovelimit = tranblockinvoiceabovelimit;
	}

	public static String getTranblocknegativestock() {
		return tranblocknegativestock;
	}

	public static void setTranblocknegativestock(String tranblocknegativestock) {
		SalesOrderSetGet.tranblocknegativestock = tranblocknegativestock;
	}

	public static String getSoAdditionalInfo() {
		return soAdditionalInfo;
	}

	public static void setSoAdditionalInfo(String soAdditionalInfo) {
		SalesOrderSetGet.soAdditionalInfo = soAdditionalInfo;
	}

	public static String getMobileoverduealert() {
	  return mobileoverduealert;
	 }

	 public static void setMobileoverduealert(String mobileoverduealert) {
	  SalesOrderSetGet.mobileoverduealert = mobileoverduealert;
	 }
	
	public static String getRoutepermission() {
		return routepermission;
	}

	public static void setRoutepermission(String routepermission) {
		SalesOrderSetGet.routepermission = routepermission;
	}

	
	public static String getHaveBatchOnTransferToLocation() {
		return haveBatchOnTransferToLocation;
	}

	public static void setHaveBatchOnTransferToLocation(
			String haveBatchOnTransferToLocation) {
		SalesOrderSetGet.haveBatchOnTransferToLocation = haveBatchOnTransferToLocation;
	}

	public static String getHaveBatchOnStockAdjustment() {
		return haveBatchOnStockAdjustment;
	}

	public static void setHaveBatchOnStockAdjustment(
			String haveBatchOnStockAdjustment) {
		SalesOrderSetGet.haveBatchOnStockAdjustment = haveBatchOnStockAdjustment;
	}

	  public static String getHaveBatchOnTransfer() {
		return haveBatchOnTransfer;
	}

	public static void setHaveBatchOnTransfer(String haveBatchOnTransfer) {
		SalesOrderSetGet.haveBatchOnTransfer = haveBatchOnTransfer;
	}

	public static String getHaveBatchOnStockIn() {
	   return haveBatchOnStockIn;
	  }
	 
	  public static void setHaveBatchOnStockIn(String haveBatchOnStockIn) {
	   SalesOrderSetGet.haveBatchOnStockIn = haveBatchOnStockIn;
	  }
	
	
	public static ArrayList<String> getLandingMenuArr() {
		return landingMenuArr;
	}

	public static void setLandingMenuArr(ArrayList<String> landingMenuArr) {
		SalesOrderSetGet.landingMenuArr = landingMenuArr;
	}

	public static String getCustname() {
		return custname;
	}

	public static void setCustname(String custname) {
		SalesOrderSetGet.custname = custname;
	}

	public static double getTotTaxAmt() {
		return totTaxAmt;
	}

	public static void setTotTaxAmt(double totTaxAmt) {
		SalesOrderSetGet.totTaxAmt = totTaxAmt;
	}

	public static String getLocationcode() {
		return locationcode;
	}

	public static void setLocationcode(String locationcode) {
		SalesOrderSetGet.locationcode = locationcode;
	}

	public static String getSaleorderdate() {
		return saleorderdate;
	}

	public static void setSaleorderdate(String saleorderdate) {
		SalesOrderSetGet.saleorderdate = saleorderdate;
	}

	public static String getDeliverydate() {
		return deliverydate;
	}

	public static void setDeliverydate(String deliverydate) {
		SalesOrderSetGet.deliverydate = deliverydate;
	}

	public static String getCustomercode() {
		return customercode;
	}

	public static void setCustomercode(String customercode) {
		SalesOrderSetGet.customercode = customercode;
	}

	public static String getCurrencycode() {
		return currencycode;
	}

	public static void setCurrencycode(String currencycode) {
		SalesOrderSetGet.currencycode = currencycode;
	}

	public static String getCurrencyrate() {
		return currencyrate;
	}

	public static void setCurrencyrate(String currencyrate) {
		SalesOrderSetGet.currencyrate = currencyrate;
	}

	public static String getRemarks() {
		return remarks;
	}

	public static void setRemarks(String remarks) {
		SalesOrderSetGet.remarks = remarks;
	}

	public static String getStatus() {
		return status;
	}

	public static void setStatus(String status) {
		SalesOrderSetGet.status = status;
	}

	public static String getCustomername() {
		return customername;
	}

	public static void setCustomername(String customername) {
		SalesOrderSetGet.customername = customername;
	}

	public static String getGeneralsetting() {
		return generalsetting;
	}

	public static void setGeneralsetting(String generalsetting) {
		SalesOrderSetGet.generalsetting = generalsetting;
	}

	public static String getGrainvoiceno() {
		return grainvoiceno
				;
	}

	public static void setGrainvoiceno(String grainvoiceno) {
		SalesOrderSetGet.grainvoiceno = grainvoiceno;
	}

	public static String getGradono() {
		return gradono;
	}

	public static void setGradono(String gradono) {
		SalesOrderSetGet.gradono = gradono;
	}

	public static String getGrainvoicedate() {
		return grainvoicedate;
	}

	public static void setGrainvoicedate(String grainvoicedate) {
		SalesOrderSetGet.grainvoicedate = grainvoicedate;
	}

	public static String getGradodate() {
		return gradodate;
	}

	public static void setGradodate(String gradodate) {
		SalesOrderSetGet.gradodate = gradodate;
	}

	public static String getCurrencyname() {
		return currencyname;
	}

	public static void setCurrencyname(String currencyname) {
		SalesOrderSetGet.currencyname = currencyname;
	}

	public static String getCompanytax() {
		return companytax;
	}

	public static void setCompanytax(String companytax) {
		SalesOrderSetGet.companytax = companytax;
	}

	public static String getTaxValue() {
		return taxValue;
	}

	public static void setTaxValue(String taxValue) {
		SalesOrderSetGet.taxValue = taxValue;
	}

	public static String getSuppliertax() {
		return suppliertax;
	}

	public static void setSuppliertax(String suppliertax) {
		SalesOrderSetGet.suppliertax = suppliertax;
	}

	public static String getSuppliercode() {
		return suppliercode;
	}

	public static void setSuppliercode(String suppliercode) {
		SalesOrderSetGet.suppliercode = suppliercode;
	}

	public static String getSuppliergroupcode() {
		return suppliergroupcode;
	}

	public static void setSuppliergroupcode(String suppliergroupcode) {
		SalesOrderSetGet.suppliergroupcode = suppliergroupcode;
	}

	public static String getSrinvoiceno() {
		return srinvoiceno;
	}

	public static void setSrinvoiceno(String srinvoiceno) {
		SalesOrderSetGet.srinvoiceno = srinvoiceno;
	}

	public static String getServerDateTime() {
		return serverDateTime;
	}

	public static void setServerDateTime(String serverDateTime) {
		SalesOrderSetGet.serverDateTime = serverDateTime;
	}

	/** Get FromLocation **/
	public static ArrayList<String> getFrom_location() {
		return new ArrayList<String>(from_location);
	}

	/** Set FromLocation **/
	public static void setFrom_location(ArrayList<String> from_location) {
		SalesOrderSetGet.from_location = new ArrayList<String>(from_location);
	}

	// public static ArrayList<String> getLst_location() {
	// return lst_location;
	// }
	//
	// public static void setLst_location(ArrayList<String> lst_location) {
	// SalesOrderSetGet.lst_location = lst_location;
	// }

	public static String getTransferchangefromloc() {
		return transferchangefromloc;
	}

	public static void setTransferchangefromloc(String transferchangefromloc) {
		SalesOrderSetGet.transferchangefromloc = transferchangefromloc;
	}

	public static String getTransferfromloc() {
		return transferfromloc;
	}

	public static void setTransferfromloc(String transferfromloc) {
		SalesOrderSetGet.transferfromloc = transferfromloc;
	}

	public static String getCartonpriceflag() {
		return cartonpriceflag;
	}

	public static void setCartonpriceflag(String cartonpriceflag) {
		SalesOrderSetGet.cartonpriceflag = cartonpriceflag;
	}

	public static String getCurrentdate() {
		return currentdate;
	}

	public static void setCurrentdate(String currentdate) {
		SalesOrderSetGet.currentdate = currentdate;
	}

	public static String getCatalogtabposition() {
		return catalogtabposition;
	}

	public static void setCatalogtabposition(String catalogtabposition) {
		SalesOrderSetGet.catalogtabposition = catalogtabposition;
	}

	public static String getBalanceAmount() {
		return balanceAmount;
	}

	public static void setBalanceAmount(String balanceAmount) {
		SalesOrderSetGet.balanceAmount = balanceAmount;
	}

	public static String getNetTotal() {
		return netTotal;
	}

	public static void setNetTotal(String netTotal) {
		SalesOrderSetGet.netTotal = netTotal;
	}

	public static String getReceiptoninvoice() {
		return receiptoninvoice;
	}

	public static void setReceiptoninvoice(String receiptoninvoice) {
		SalesOrderSetGet.receiptoninvoice = receiptoninvoice;
	}

	public static String getsCollectCash() {
		return sCollectCash;
	}

	public static void setsCollectCash(String sCollectCash) {
		SalesOrderSetGet.sCollectCash = sCollectCash;
	}

	public static String getHeader_flag() {
		return header_flag;
	}

	public static void setHeader_flag(String header_flag) {
		SalesOrderSetGet.header_flag = header_flag;
	}

	public static String getMobileproductstockprint() {
		return mobileproductstockprint;
	}

	public static void setMobileproductstockprint(String mobileproductstockprint) {
		SalesOrderSetGet.mobileproductstockprint = mobileproductstockprint;
	}

	public static String getLocationname() {
		return locationname;
	}

	public static void setLocationname(String locationname) {
		SalesOrderSetGet.locationname = locationname;
	}

	public static String getSoRemarks() {
		return soRemarks;
	}

	public static void setSoRemarks(String soRemarks) {
		SalesOrderSetGet.soRemarks = soRemarks;
	}

	public static String getStockTakeNo() {
		return stockTakeNo;
	}

	public static void setStockTakeNo(String stockTakeNo) {
		SalesOrderSetGet.stockTakeNo = stockTakeNo;
	}

	public static String getCompanytaxvalue() {
		return companytaxvalue;
	}

	public static void setCompanytaxvalue(String companytaxvalue) {
		SalesOrderSetGet.companytaxvalue = companytaxvalue;
	}

	public static String getAutoBatchNo() {
		return autoBatchNo;
	}

	public static void setAutoBatchNo(String autoBatchNo) {
		SalesOrderSetGet.autoBatchNo = autoBatchNo;
	}

	public static String getNextBatchNo() {
		return NextBatchNo;
	}

	public static void setNextBatchNo(String nextBatchNo) {
		NextBatchNo = nextBatchNo;
	}

	public static String getFromLoc() {
		return fromLoc;
	}

	public static void setFromLoc(String fromLoc) {
		SalesOrderSetGet.fromLoc = fromLoc;
	}

	public static String getToLoc() {
		return toLoc;
	}

	public static void setToLoc(String toLoc) {
		SalesOrderSetGet.toLoc = toLoc;
	}

	public static String getStocktotransfer() {
		return stocktotransfer;
	}

	public static void setStocktotransfer(String stocktotransfer) {
		SalesOrderSetGet.stocktotransfer = stocktotransfer;
	}

	public static String getInvoiceprintdetail() {
		return invoiceprintdetail;
	}

	public static void setInvoiceprintdetail(String invoiceprintdetail) {
		SalesOrderSetGet.invoiceprintdetail = invoiceprintdetail;
	}

	public static String getMobileloginpage() {
		return mobileloginpage;
	}

	public static void setMobileloginpage(String mobileloginpage) {
		SalesOrderSetGet.mobileloginpage = mobileloginpage;
	}

	public static String getEnablecustomercode() {
		return enablecustomercode;
	}

	public static void setEnablecustomercode(String enablecustomercode) {
		SalesOrderSetGet.enablecustomercode = enablecustomercode;
	}

	public static String getDoQtyValidateWithSo() {
		return doQtyValidateWithSo;
	}

	public static void setDoQtyValidateWithSo(String doQtyValidateWithSo) {
		SalesOrderSetGet.doQtyValidateWithSo = doQtyValidateWithSo;
	}

	public static String getSchedulingType() {
		return schedulingType;
	}

	public static void setSchedulingType(String schedulingType) {
		SalesOrderSetGet.schedulingType = schedulingType;
	}

	public static String getLoginPhoneNo() {
		  return loginPhoneNo;
		 }

		 public static void setLoginPhoneNo(String loginPhoneNo) {
		  SalesOrderSetGet.loginPhoneNo = loginPhoneNo;
		 }

	public static String getTranBlockTerms() {
		return tranBlockTerms;
	}

	public static void setTranBlockTerms(String tranBlockTerms) {
		SalesOrderSetGet.tranBlockTerms = tranBlockTerms;
	}

	public static String getAppPrintGroup() {
		return appPrintGroup;
	}

	public static void setAppPrintGroup(String appPrintGroup) {
		SalesOrderSetGet.appPrintGroup = appPrintGroup;
	}

	public static String getHaveAttribute() {
		return haveAttribute;
	}

	public static void setHaveAttribute(String haveAttribute) {
		SalesOrderSetGet.haveAttribute = haveAttribute;
	}

	public static String getShowUnitCostStockTake() {
		return showUnitCostStockTake;
	}

	public static void setShowUnitCostStockTake(String showUnitCostStockTake) {
		SalesOrderSetGet.showUnitCostStockTake = showUnitCostStockTake;
	}
	public static String getSelfOrderShowProductCode() {
		return selfOrderShowProductCode;
	}

	public static void setSelfOrderShowProductCode(String selfOrderShowProductCode) {
		SalesOrderSetGet.selfOrderShowProductCode = selfOrderShowProductCode;
	}

	public static String getMobileHaveOfflineMode() {
		return mobileHaveOfflineMode;
	}

	public static void setMobileHaveOfflineMode(String mobileHaveOfflineMode) {
		SalesOrderSetGet.mobileHaveOfflineMode = mobileHaveOfflineMode;
	}

	public static String getReceiptAutoCreditAmount() {
		return receiptAutoCreditAmount;
	}

	public static void setReceiptAutoCreditAmount(String receiptAutoCreditAmount) {
		SalesOrderSetGet.receiptAutoCreditAmount = receiptAutoCreditAmount;
	}

	public static String getHostingValidation() {
		return hostingValidation;
	}

	public static void setHostingValidation(String hostingValidation) {
		SalesOrderSetGet.hostingValidation = hostingValidation;
	}

	public static String getCustomerHaveCashbill() {
		return customerHaveCashbill;
	}

	public static void setCustomerHaveCashbill(String customerHaveCashbill) {
		SalesOrderSetGet.customerHaveCashbill = customerHaveCashbill;
	}

	public static String getMobileDoShowRoute() {
		return mobileDoShowRoute;
	}

	public static void setMobileDoShowRoute(String mobileDoShowRoute) {
		SalesOrderSetGet.mobileDoShowRoute = mobileDoShowRoute;
	}

	public static String getIsShowCost() {
		return isShowCost;
	}

	public static void setIsShowCost(String isShowCost) {
		SalesOrderSetGet.isShowCost = isShowCost;
	}

	public static String getDuration() {
		return duration;
	}

	public static void setDuration(String duration) {
		SalesOrderSetGet.duration = duration;
	}

	public static String getAvg_cost() {
		return avg_cost;
	}

	public static void setAvg_cost(String avg_cost) {
		SalesOrderSetGet.avg_cost = avg_cost;
	}

	public static String getOrderNo() {
		return orderNo;
	}

	public static void setOrderNo(String orderNo) {
		SalesOrderSetGet.orderNo = orderNo;
	}

	public static String getOrderDate() {
		return orderDate;
	}

	public static void setOrderDate(String orderDate) {
		SalesOrderSetGet.orderDate = orderDate;
	}

	public static String getStatusCheck() {
		return statusCheck;
	}

	public static void setStatusCheck(String statusCheck) {
		SalesOrderSetGet.statusCheck = statusCheck;
	}

	public static String getHaveBatchOnConsignmentOut() {
		return haveBatchOnConsignmentOut;
	}

	public static void setHaveBatchOnConsignmentOut(String haveBatchOnConsignmentOut) {
		SalesOrderSetGet.haveBatchOnConsignmentOut = haveBatchOnConsignmentOut;
	}

	public static String getCustomerCode() {
		return customerCode;
	}

	public static void setCustomerCode(String customerCode) {
		SalesOrderSetGet.customerCode = customerCode;
	}

	public static double getCon_qty() {
		return con_qty;
	}

	public static void setCon_qty(double con_qty) {
		SalesOrderSetGet.con_qty = con_qty;
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		SalesOrderSetGet.name = name;
	}

	public static ArrayList<String> getConsignmentNo() {
		return consignmentNo;
	}

	public static void setConsignmentNo(ArrayList<String> consignmentNo) {
		SalesOrderSetGet.consignmentNo = consignmentNo;
	}

	public static String getFlag() {
		return flag;
	}

	public static void setFlag(String flag) {
		SalesOrderSetGet.flag = flag;
	}

	public static String getShortCode() {
		return shortCode;
	}

	public static void setShortCode(String shortCode) {
		SalesOrderSetGet.shortCode = shortCode;
	}

	public static String getTranType() {
		return tranType;
	}

	public static void setTranType(String tranType) {
		SalesOrderSetGet.tranType = tranType;
	}

	public static String getClick() {
		return click;
	}

	public static void setClick(String click) {
		SalesOrderSetGet.click = click;
	}

	public static String getAddress() {
		return address;
	}

	public static void setAddress(String address) {
		SalesOrderSetGet.address = address;
	}

	public static String getTotal() {
		return total;
	}

	public static void setTotal(String total) {
		SalesOrderSetGet.total = total;
	}

	public static String getSub_tot() {
		return sub_tot;
	}

	public static void setSub_tot(String sub_tot) {
		SalesOrderSetGet.sub_tot = sub_tot;
	}

	public static String getTotal_disc() {
		return total_disc;
	}

	public static void setTotal_disc(String total_disc) {
		SalesOrderSetGet.total_disc = total_disc;
	}

	public static String getSubTot() {
		return subTot;
	}

	public static void setSubTot(String subTot) {
		SalesOrderSetGet.subTot = subTot;
	}

	public static String getTax() {
		return tax;
	}

	public static void setTax(String tax) {
		SalesOrderSetGet.tax = tax;
	}

	public static String getNet_tot() {
		return net_tot;
	}

	public static void setNet_tot(String net_tot) {
		SalesOrderSetGet.net_tot = net_tot;
	}

	public static ArrayList<String> getSl_No() {
		return sl_No;
	}

	public static void setSl_No(ArrayList<String> sl_No) {
		SalesOrderSetGet.sl_No = sl_No;
	}

	public static ArrayList<HashMap<String, String>> getHashMap() {
		return hashMap;
	}

	public static void setHashMap(ArrayList<HashMap<String, String>> hashMap) {
		SalesOrderSetGet.hashMap = hashMap;
	}

	public static ArrayList<HashMap<String, String>> getHashMap_list() {
		return hashMap_list;
	}

	public static void setHashMap_list(ArrayList<HashMap<String, String>> hashMap_list) {
		SalesOrderSetGet.hashMap_list = hashMap_list;
	}

	public static String getStockRefNo() {
		return stockRefNo;
	}

	public static void setStockRefNo(String stockRefNo) {
		SalesOrderSetGet.stockRefNo = stockRefNo;
	}

	public static String getSlNo() {
		return SlNo;
	}

	public static void setSlNo(String slNo) {
		SlNo = slNo;
	}

	public static String getRet_qty() {
		return ret_qty;
	}

	public static void setRet_qty(String ret_qty) {
		SalesOrderSetGet.ret_qty = ret_qty;
	}

	public static String getResult_value() {
		return result_value;
	}

	public static void setResult_value(String result_value) {
		SalesOrderSetGet.result_value = result_value;
	}

	public static String getCust_code() {
		return cust_code;
	}

	public static void setCust_code(String cust_code) {
		SalesOrderSetGet.cust_code = cust_code;
	}

	public static String getCust_name() {
		return cust_name;
	}

	public static void setCust_name(String cust_name) {
		SalesOrderSetGet.cust_name = cust_name;
	}

	public static String getCon_no() {
		return con_no;
	}

	public static void setCon_no(String con_no) {
		SalesOrderSetGet.con_no = con_no;
	}

	public static int getSl_no() {
		return sl_no;
	}

	public static void setSl_no(int sl_no) {
		SalesOrderSetGet.sl_no = sl_no;
	}

	public static String getType() {
		return type;
	}

	public static void setType(String type) {
		SalesOrderSetGet.type = type;
	}

	public static String getcQty() {
		return cQty;
	}

	public static void setcQty(String cQty) {
		SalesOrderSetGet.cQty = cQty;
	}

	public static String getOutStanding() {
		return outStanding;
	}

	public static void setOutStanding(String outStanding) {
		SalesOrderSetGet.outStanding = outStanding;
	}

	public static String getHavePos() {
		return havePos;
	}

	public static void setHavePos(String havePos) {
		SalesOrderSetGet.havePos = havePos;
	}

	public static String getMobile_dateChange() {
		return mobile_dateChange;
	}

	public static void setMobile_dateChange(String mobile_dateChange) {
		SalesOrderSetGet.mobile_dateChange = mobile_dateChange;
	}

	public static String getPayTo() {
		return payTo;
	}

	public static void setPayTo(String payTo) {
		SalesOrderSetGet.payTo = payTo;
	}

	public static String getDoNo() {
		return doNo;
	}

	public static void setDoNo(String doNo) {
		SalesOrderSetGet.doNo = doNo;
	}

	public static String getExpense_dateChange() {
		return expense_dateChange;
	}

	public static void setExpense_dateChange(String expense_dateChange) {
		SalesOrderSetGet.expense_dateChange = expense_dateChange;
	}

	public static String getTransfer_stockReq() {
		return transfer_stockReq;
	}

	public static void setTransfer_stockReq(String transfer_stockReq) {
		SalesOrderSetGet.transfer_stockReq = transfer_stockReq;
	}

	public static String getCarton_quantity() {
		return carton_quantity;
	}

	public static void setCarton_quantity(String carton_quantity) {
		SalesOrderSetGet.carton_quantity = carton_quantity;
	}
}
