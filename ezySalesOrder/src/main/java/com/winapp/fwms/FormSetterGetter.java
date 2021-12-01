package com.winapp.fwms;

public class FormSetterGetter {

	static String goodsReceive = "";
	static String deliveryOrder = "";
	static String salesOrder = "";
	static String salesReturn = "";
	static String productList = "";
	static String productAnalysis = "";
	static String customerList = "";
	static String catalog = "";
	static String receipts = "";
	static String settings = "";
	static String invoice = "";
	static String stockRequest = "";
	static String transfer = "";
	static String stockTake = "";
	static String stockAdjustment = "";
	static boolean productAdd = false;
	static boolean customerAdd = false;
	static boolean EditPrice = false;
	static boolean EditInvoice = false;
	static boolean DeleteInvoice = false;
	static boolean DeleteReceipt = false;
	static boolean ReceiptAll = false;
	static String routeMaster = "";
	static String expense = "";
	static String overdue = "";
	static String addInvoice = "";
	static String tasks = "";
	static String deliveryVerification = "";
	static String merchandiseSchedule = "";
	static String merchandise = "";
	static String hidePrice = "";
	static String offlineMode = "";
	static boolean editTransactionDate = false;
	static boolean showAllLocation =false;
	static boolean showDashboard=false;
	static String consignment = "";
	static String consignmentStock ="";
	static String consignmentReturn = "";
	static String consignmentStockTake ="";
	static String ShowProductAnalysis ="";
	static String quickTransfer = "";
	static String manualStock = "";
	static String settlement = "";
	public static boolean isEditTransactionDate() {
		return editTransactionDate;
	}

	public static void setEditTransactionDate(boolean editTransactionDate) {
		FormSetterGetter.editTransactionDate = editTransactionDate;
	}

	public static String getMerchandiseSchedule() {
		return merchandiseSchedule;
	}

	public static void setMerchandiseSchedule(String merchandiseSchedule) {
		FormSetterGetter.merchandiseSchedule = merchandiseSchedule;
	}

	public static String getMerchandise() {
		return merchandise;
	}

	public static void setMerchandise(String merchandise) {
		FormSetterGetter.merchandise = merchandise;
	}

	public static String getDeliveryVerification() {
		return deliveryVerification;
	}

	public static void setDeliveryVerification(String deliveryVerification) {
		FormSetterGetter.deliveryVerification = deliveryVerification;
	}

	public static String getTasks() {
		return tasks;
	}

	public static void setTasks(String tasks) {
		FormSetterGetter.tasks = tasks;
	}

	public static String getAddInvoice() {
		return addInvoice;
	}

	public static void setAddInvoice(String addInvoice) {
		FormSetterGetter.addInvoice = addInvoice;
	}

	public static String getOverdue() {
		return overdue;
	}

	public static void setOverdue(String overdue) {
		FormSetterGetter.overdue = overdue;
	}

	public static String getRouteMaster() {
		return routeMaster;
	}

	public static void setRouteMaster(String routeMaster) {
		FormSetterGetter.routeMaster = routeMaster;
	}

	public static String getExpense() {
		return expense;
	}

	public static void setExpense(String expense) {
		FormSetterGetter.expense = expense;
	}

	public static String getGoodsReceive() {
		return goodsReceive;
	}

	public static void setGoodsReceive(String goodsReceive) {
		FormSetterGetter.goodsReceive = goodsReceive;
	}

	public static String getDeliveryOrder() {
		return deliveryOrder;
	}

	public static void setDeliveryOrder(String deliveryOrder) {
		FormSetterGetter.deliveryOrder = deliveryOrder;
	}

	public static String getSalesOrder() {
		return salesOrder;
	}

	public static void setSalesOrder(String salesOrder) {
		FormSetterGetter.salesOrder = salesOrder;
	}

	public static String getSalesReturn() {
		return salesReturn;
	}

	public static void setSalesReturn(String salesReturn) {
		FormSetterGetter.salesReturn = salesReturn;
	}

	public static String getProductList() {
		return productList;
	}

	public static void setProductList(String productList) {
		FormSetterGetter.productList = productList;
	}

	public static String getCustomerList() {
		return customerList;
	}

	public static void setCustomerList(String customerList) {
		FormSetterGetter.customerList = customerList;
	}

	public static String getCatalog() {
		return catalog;
	}

	public static void setCatalog(String catalog) {
		FormSetterGetter.catalog = catalog;
	}

	public static String getReceipts() {
		return receipts;
	}

	public static void setReceipts(String receipts) {
		FormSetterGetter.receipts = receipts;
	}

	public static String getSettings() {
		return settings;
	}

	public static void setSettings(String settings) {
		FormSetterGetter.settings = settings;
	}

	public static String getInvoice() {
		return invoice;
	}

	public static void setInvoice(String invoice) {
		FormSetterGetter.invoice = invoice;
	}

	public static String getStockRequest() {
		return stockRequest;
	}

	public static void setStockRequest(String stockRequest) {
		FormSetterGetter.stockRequest = stockRequest;
	}

	public static String getTransfer() {
		return transfer;
	}

	public static void setTransfer(String transfer) {
		FormSetterGetter.transfer = transfer;
	}

	public static String getStockTake() {
		return stockTake;
	}

	public static void setStockTake(String stockTake) {
		FormSetterGetter.stockTake = stockTake;
	}

	public static String getStockAdjustment() {
		return stockAdjustment;
	}

	public static void setStockAdjustment(String stockAdjustment) {
		FormSetterGetter.stockAdjustment = stockAdjustment;
	}

	public static boolean isProductAdd() {
		return productAdd;
	}

	public static void setProductAdd(boolean productAdd) {
		FormSetterGetter.productAdd = productAdd;
	}

	public static boolean isCustomerAdd() {
		return customerAdd;
	}

	public static void setCustomerAdd(boolean customerAdd) {
		FormSetterGetter.customerAdd = customerAdd;
	}

	public static boolean isEditInvoice() {
		return EditInvoice;
	}

	public static void setEditInvoice(boolean editInvoice) {
		EditInvoice = editInvoice;
	}

	public static boolean isDeleteInvoice() {
		return DeleteInvoice;
	}

	public static void setDeleteInvoice(boolean deleteInvoice) {
		DeleteInvoice = deleteInvoice;
	}

	public static boolean isDeleteReceipt() {
		return DeleteReceipt;
	}

	public static void setDeleteReceipt(boolean deleteReceipt) {
		DeleteReceipt = deleteReceipt;
	}

	public static boolean isEditPrice() {
		return EditPrice;
	}

	public static void setEditPrice(boolean editPrice) {
		EditPrice = editPrice;
	}

	public static boolean isReceiptAll() {
		return ReceiptAll;
	}

	public static void setReceiptAll(boolean receiptAll) {
		ReceiptAll = receiptAll;
	}
	public static String getHidePrice() {
		return hidePrice;
	}

	public static void setHidePrice(String hidePrice) {
		FormSetterGetter.hidePrice = hidePrice;
	}

	public static String getOfflineMode() {
		return offlineMode;
	}

	public static void setOfflineMode(String offlineMode) {
		FormSetterGetter.offlineMode = offlineMode;
	}

	public static boolean isShowAllLocation() {
		return showAllLocation;
	}

	public static void setShowAllLocation(boolean showAllLocation) {
		FormSetterGetter.showAllLocation = showAllLocation;
	}

	public static boolean isShowDashboard() {
		return showDashboard;
	}

	public static void setShowDashboard(boolean showDashboard) {
		FormSetterGetter.showDashboard = showDashboard;
	}

	public static String getProductAnalysis() {
		return productAnalysis;
	}

	public static void setProductAnalysis(String productAnalysis) {
		FormSetterGetter.productAnalysis = productAnalysis;
	}

	public static String getConsignment() {
		return consignment;
	}

	public static void setConsignment(String consignment) {
		FormSetterGetter.consignment = consignment;
	}

	public static String getConsignmentStock() {
		return consignmentStock;
	}

	public static void setConsignmentStock(String consignmentStock) {
		FormSetterGetter.consignmentStock = consignmentStock;
	}

	public static String getConsignmentReturn() {
		return consignmentReturn;
	}

	public static void setConsignmentReturn(String consignmentReturn) {
		FormSetterGetter.consignmentReturn = consignmentReturn;
	}

	public static String getConsignmentStockTake() {
		return consignmentStockTake;
	}

	public static void setConsignmentStockTake(String consignmentStockTake) {
		FormSetterGetter.consignmentStockTake = consignmentStockTake;
	}

	public static String getShowProductAnalysis() {
		return ShowProductAnalysis;
	}

	public static void setShowProductAnalysis(String showProductAnalysis) {
		ShowProductAnalysis = showProductAnalysis;
	}

	public static String getQuickTransfer() {
		return quickTransfer;
	}

	public static void setQuickTransfer(String quickTransfer) {
		FormSetterGetter.quickTransfer = quickTransfer;
	}

	public static String getManualStock() {
		return manualStock;
	}

	public static void setManualStock(String manualStock) {
		FormSetterGetter.manualStock = manualStock;
	}

	public static String getSettlement() {
		return settlement;
	}

	public static void setSettlement(String settlement) {
		FormSetterGetter.settlement = settlement;
	}
}
