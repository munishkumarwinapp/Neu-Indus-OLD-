package com.winapp.sot;

public class In_Cash {

	private int in_Position;
	private String in_Date;
	private String in_Netvalue;
	private String in_Credit;
	private String in_Paid;
	private String in_Bal;
	private String in_InvNo;
	private String in_Status;
	private boolean isSelected;
	private double in_Total;
	private int in_Listsize;
	private String add_bal;
	private String add_paid;
	private String add_credit;

	private int split_Position;
	private String extraAmount;
	private static String check_No = "";
	private static String check_Date = "";
	private static String pay_Mode;
	private static String bank_code = "";
	private static String bank_Name = "";
	private static String Cust_Code;
	private static String InvoiceNo;

	private String oldCreditAmount="";
	private String oldPaidAmount="";

	public String getOldCreditAmount() {
		return oldCreditAmount;
	}

	public void setOldCreditAmount(String oldCreditAmount) {
		this.oldCreditAmount = oldCreditAmount;
	}

	public String getOldPaidAmount() {
		return oldPaidAmount;
	}

	public void setOldPaidAmount(String oldPaidAmount) {
		this.oldPaidAmount = oldPaidAmount;
	}

	public static String getInvoiceNo() {
		return InvoiceNo;
	}

	public static void setInvoiceNo(String invoiceNo) {
		InvoiceNo = invoiceNo;
	}

	public String getIn_InvNo() {
		return in_InvNo;
	}

	public void setIn_InvNo(String in_InvNo) {
		this.in_InvNo = in_InvNo;
	}

	public static String getPay_Mode() {
		return pay_Mode;
	}

	public static void setPay_Mode(String pay_Mode) {
		In_Cash.pay_Mode = pay_Mode;
	}

	public int getSplit_Position() {
		return split_Position;
	}

	public void setSplit_Position(int split_Position) {
		this.split_Position = split_Position;
	}

	public int getIn_Position() {
		return in_Position;
	}

	public void setIn_Position(int in_Position) {
		this.in_Position = in_Position;
	}

	public String getIn_Date() {
		return in_Date;
	}

	public void setIn_Date(String in_Date) {
		this.in_Date = in_Date;
	}

	public String getIn_Netvalue() {
		return in_Netvalue;
	}

	public void setIn_Netvalue(String in_Netvalue) {
		this.in_Netvalue = in_Netvalue;
	}

	public String getIn_Credit() {
		return in_Credit;
	}

	public void setIn_Credit(String in_Credit) {
		this.in_Credit = in_Credit;
	}

	public String getIn_Paid() {
		return in_Paid;
	}

	public void setIn_Paid(String in_Paid) {
		this.in_Paid = in_Paid;
	}

	public String getIn_Bal() {
		return in_Bal;
	}

	public void setIn_Bal(String in_Bal) {
		this.in_Bal = in_Bal;
	}

	public String getIn_Status() {
		return in_Status;
	}

	public void setIn_Status(String in_Status) {
		this.in_Status = in_Status;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public double getIn_Total() {
		return in_Total;
	}

	public void setIn_Total(double in_Total) {
		this.in_Total = in_Total;
	}

	public int getIn_Listsize() {
		return in_Listsize;
	}

	public void setIn_Listsize(int in_Listsize) {
		this.in_Listsize = in_Listsize;
	}

	public String getAdd_bal() {
		return add_bal;
	}

	public void setAdd_bal(String add_bal) {
		this.add_bal = add_bal;
	}

	public String getAdd_paid() {
		return add_paid;
	}

	public void setAdd_paid(String add_paid) {
		this.add_paid = add_paid;
	}

	public static String getCheck_No() {
		return check_No;
	}

	public static void setCheck_No(String check_No) {
		In_Cash.check_No = check_No;
	}

	public static String getCheck_Date() {
		return check_Date;
	}

	public static void setCheck_Date(String check_Date) {
		In_Cash.check_Date = check_Date;
	}

	public static String getBank_code() {
		return bank_code;
	}

	public static void setBank_code(String bank_code) {
		In_Cash.bank_code = bank_code;
	}

	public static String getBank_Name() {
		return bank_Name;
	}

	public static void setBank_Name(String bank_Name) {
		In_Cash.bank_Name = bank_Name;
	}

	public static String getCust_Code() {
		return Cust_Code;
	}

	public static void setCust_Code(String cust_Code) {
		Cust_Code = cust_Code;
	}

	public String getAdd_credit() {
		return add_credit;
	}

	public void setAdd_credit(String add_credit) {
		this.add_credit = add_credit;
	}

	public String getExtraAmount() {
		return extraAmount;
	}

	public void setExtraAmount(String extraAmount) {
		this.extraAmount = extraAmount;
	}
}
