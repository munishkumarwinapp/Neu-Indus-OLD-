package com.winapp.sot;

public class SO {
	private String sno;
	private String date;
	private String customerCode;
	private String customerName;
	private String nettotal;
	private String status;
	private boolean isSelected;
	private String fromlocation;
	private String tolocation;
	private String balanceamount;
	static double totalamount;
	static double totalpaid;
	private String stAdjust_no;
	private String stAdjust_date;
	private String stAdjust_location;
	private String productCode;
	private String productName;
	private String terms;
	private String pending;
	private String outLetName;
	private String refNo;
	private String refDate;
	private String deliveryCode;
	private String jobStartTime;
	private String jobEndTime;
	private String address;
	private String remarks1;
	private String remarks2;
	private String assignUser;
	private String assignDate;
	private String gotSignatureOnDO;
	private String isJobClosed;
	private String statusCode;
	private String statusName;
	private String outletAddress;
	private String soNo;
	private int orderNo;
	private String vanCode;
	private String scheduleNo;
	private String scheduleUser;
	private String sortOrder;
	private String DelCustomerName;
	private String NoOfDays;
	private String DueDays;
	private String overdueDays;
	private static double overdueTotalAmount;
	private String InvoiceSigned="";
	private String IsClosed="";
	private String IsPosted="";
	private String remarks ="";
	private static String bal_amt;
	private static String netTotal;
	private static String paidAmt;
	private  String dono;
	private String salesType;
	private double balance_amt;
	private String customeraddress1;
	private String customeraddress2;
	private String customeraddress3;
	private String subTotal;
	private String paidAmts;
	private String tax;

	public String getPaidAmts() {
		return paidAmts;
	}

	public void setPaidAmts(String paidAmts) {
		this.paidAmts = paidAmts;
	}

	public String getIsClosed() {
		return IsClosed;
	}

	public void setIsClosed(String isClosed) {
		IsClosed = isClosed;
	}

	public String getIsPosted() {
		return IsPosted;
	}

	public void setIsPosted(String isPosted) {
		IsPosted = isPosted;
	}

	public String getOverdueDays() {
		return overdueDays;
	}

	public void setOverdueDays(String overdueDays) {
		this.overdueDays = overdueDays;
	}

	public static double getOverdueTotalAmount() {
		return overdueTotalAmount;
	}

	public static void setOverdueTotalAmount(double overdueTotalAmount) {
		SO.overdueTotalAmount = overdueTotalAmount;
	}

	public String getNoOfDays() {
		return NoOfDays;
	}

	public void setNoOfDays(String noOfDays) {
		NoOfDays = noOfDays;
	}

	public String getDueDays() {
		return DueDays;
	}

	public void setDueDays(String dueDays) {
		DueDays = dueDays;
	}

	public String getDelCustomerName() {
		return DelCustomerName;
	}

	public void setDelCustomerName(String delCustomerName) {
		DelCustomerName = delCustomerName;
	}

	public String getAssignUser() {
		return assignUser;
	}

	public void setAssignUser(String assignUser) {
		this.assignUser = assignUser;
	}

	public String getAssignDate() {
		return assignDate;
	}

	public void setAssignDate(String assignDate) {
		this.assignDate = assignDate;
	}

	public String getGotSignatureOnDO() {
		return gotSignatureOnDO;
	}

	public void setGotSignatureOnDO(String gotSignatureOnDO) {
		this.gotSignatureOnDO = gotSignatureOnDO;
	}

	public String getIsJobClosed() {
		return isJobClosed;
	}

	public void setIsJobClosed(String isJobClosed) {
		this.isJobClosed = isJobClosed;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getPending() {
		return pending;
	}

	public void setPending(String pending) {
		this.pending = pending;
	}

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getNettotal() {
		return nettotal;
	}

	public void setNettotal(String nettotal) {
		this.nettotal = nettotal;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBalanceamount() {
		return balanceamount;
	}

	public void setBalanceamount(String balanceamount) {
		this.balanceamount = balanceamount;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getFromlocation() {
		return fromlocation;
	}

	public void setFromlocation(String fromlocation) {
		this.fromlocation = fromlocation;
	}

	public String getTolocation() {
		return tolocation;
	}

	public void setTolocation(String tolocation) {
		this.tolocation = tolocation;
	}

	public static double getTotalamount() {
		return totalamount;
	}

	public static void setTotalamount(double totalamount) {
		SO.totalamount = totalamount;
	}

	public String getStAdjust_no() {
		return stAdjust_no;
	}

	public void setStAdjust_no(String stAdjust_no) {
		this.stAdjust_no = stAdjust_no;
	}

	public String getStAdjust_date() {
		return stAdjust_date;
	}

	public void setStAdjust_date(String stAdjust_date) {
		this.stAdjust_date = stAdjust_date;
	}

	public String getStAdjust_location() {
		return stAdjust_location;
	}

	public void setStAdjust_location(String stAdjust_location) {
		this.stAdjust_location = stAdjust_location;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOutLetName() {
		return outLetName;
	}

	public void setOutLetName(String outLetName) {
		this.outLetName = outLetName;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public String getJobStartTime() {
		return jobStartTime;
	}

	public void setJobStartTime(String jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	public String getJobEndTime() {
		return jobEndTime;
	}

	public void setJobEndTime(String jobEndTime) {
		this.jobEndTime = jobEndTime;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getRefDate() {
		return refDate;
	}

	public void setRefDate(String refDate) {
		this.refDate = refDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemarks1() {
		return remarks1;
	}

	public void setRemarks1(String remarks1) {
		this.remarks1 = remarks1;
	}

	public String getRemarks2() {
		return remarks2;
	}

	public void setRemarks2(String remarks2) {
		this.remarks2 = remarks2;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getOutletAddress() {
		return outletAddress;
	}

	public void setOutletAddress(String outletAddress) {
		this.outletAddress = outletAddress;
	}

	public String getSoNo() {
		return soNo;
	}

	public void setSoNo(String soNo) {
		this.soNo = soNo;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getVanCode() {
		return vanCode;
	}

	public void setVanCode(String vanCode) {
		this.vanCode = vanCode;
	}

	public String getScheduleNo() {
		return scheduleNo;
	}

	public void setScheduleNo(String scheduleNo) {
		this.scheduleNo = scheduleNo;
	}

	public String getScheduleUser() {
		return scheduleUser;
	}

	public void setScheduleUser(String scheduleUser) {
		this.scheduleUser = scheduleUser;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getInvoiceSigned() {
		return InvoiceSigned;
	}

	public void setInvoiceSigned(String invoiceSigned) {
		InvoiceSigned = invoiceSigned;
	}

	public static double getTotalpaid() {
		return totalpaid;
	}

	public static void setTotalpaid(double totalpaid) {
		SO.totalpaid = totalpaid;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDono() {
		return dono;
	}

	public  void setDono(String dono) {
		this.dono = dono;
	}

	public static String getBal_amt() {
		return bal_amt;
	}

	public static void setBal_amt(String bal_amt) {
		SO.bal_amt = bal_amt;
	}

	public static String getNetTotal() {
		return netTotal;
	}

	public static void setNetTotal(String netTotal) {
		SO.netTotal = netTotal;
	}

	public static String getPaidAmt() {
		return paidAmt;
	}

	public static void setPaidAmt(String paidAmt) {
		SO.paidAmt = paidAmt;
	}

	public String getSalesType() {
		return salesType;
	}

	public  void setSalesType(String salesType) {
		this.salesType = salesType;
	}

	public double getBalance_amt() {
		return balance_amt;
	}

	public void setBalance_amt(double balance_amt) {
		this.balance_amt = balance_amt;
	}

	public String getCustomeraddress1() {
		return customeraddress1;
	}

	public void setCustomeraddress1(String customeraddress1) {
		this.customeraddress1 = customeraddress1;
	}

	public String getCustomeraddress2() {
		return customeraddress2;
	}

	public void setCustomeraddress2(String customeraddress2) {
		this.customeraddress2 = customeraddress2;
	}

	public String getCustomeraddress3() {
		return customeraddress3;
	}

	public void setCustomeraddress3(String customeraddress3) {
		this.customeraddress3 = customeraddress3;
	}

	public String getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}
}
