package com.winapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Receipt {
private String receiptno;
private String receiptdate;
private String customercode;
private String customername;
private double paidamount;
private double creditamount;
private String paymode;
private String bankcode;
private String chequeno;
private String chequedate;
private double focqty;
private double exchangeqty;
	private String invoiceno;
	private String inv_paidamount;
	private String inv_creditamount;
	private String inv_date;
	private String inv_receiptno;
//private double cashamount;
//private double chequeamount;
public Receipt(){
	
}
public static final Parcelable.Creator<Receipt> CREATOR = new Parcelable.Creator<Receipt>() {
	@Override
	public Receipt createFromParcel(Parcel source) {
		return new Receipt(source);
	}

	@Override
	public Receipt[] newArray(int size) {
		return new Receipt[size];
	}
};

public Receipt(Parcel in) {
	receiptno = in.readString();
	receiptdate = in.readString();
	customercode = in.readString();
	customername = in.readString();
	paidamount = in.readDouble();
	creditamount = in.readDouble();
	paymode = in.readString();
	bankcode = in.readString();
	chequeno = in.readString();
	chequedate = in.readString();
	
}
public String getReceiptno() {
	return receiptno;
}
public void setReceiptno(String receiptno) {
	this.receiptno = receiptno;
}
public String getReceiptdate() {
	return receiptdate;
}
public void setReceiptdate(String receiptdate) {
	this.receiptdate = receiptdate;
}
public String getCustomercode() {
	return customercode;
}
public void setCustomercode(String customercode) {
	this.customercode = customercode;
}
public String getCustomername() {
	return customername;
}
public void setCustomername(String customername) {
	this.customername = customername;
}
public double getPaidamount() {
	return paidamount;
}
public void setPaidamount(double paidamount) {
	this.paidamount = paidamount;
}
public double getCreditamount() {
	return creditamount;
}
public void setCreditamount(double creditamount) {
	this.creditamount = creditamount;
}
public String getPaymode() {
	return paymode;
}
public void setPaymode(String paymode) {
	this.paymode = paymode;
}
public String getBankcode() {
	return bankcode;
}
public void setBankcode(String bankcode) {
	this.bankcode = bankcode;
}
public String getChequeno() {
	return chequeno;
}
public void setChequeno(String chequeno) {
	this.chequeno = chequeno;
}
public String getChequedate() {
	return chequedate;
}
public void setChequedate(String chequedate) {
	this.chequedate = chequedate;
}
/*public double getCashamount() {
	return cashamount;
}
public void setCashamount(double cashamount) {
	this.cashamount = cashamount;
}
public double getChequeamount() {
	return chequeamount;
}
public void setChequeamount(double chequeamount) {
	this.chequeamount = chequeamount;
}
*/
public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(receiptno);
	dest.writeString(receiptdate);
	dest.writeString(customercode);
	dest.writeString(customername);
	dest.writeDouble(paidamount);
	dest.writeDouble(creditamount);
	dest.writeString(paymode);
	dest.writeString(bankcode);
	dest.writeString(chequeno);
	dest.writeString(chequedate);
}
public double getFocqty() {
	return focqty;
}
public void setFocqty(double focqty) {
	this.focqty = focqty;
}
public double getExchangeqty() {
	return exchangeqty;
}
public void setExchangeqty(double exchangeqty) {
	this.exchangeqty = exchangeqty;
}
public int describeContents() {

	return 0;
}

	public String getInvoiceno() {
		return invoiceno;
	}

	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno;
	}

	public String getInv_paidamount() {
		return inv_paidamount;
	}

	public void setInv_paidamount(String inv_paidamount) {
		this.inv_paidamount = inv_paidamount;
	}

	public String getInv_creditamount() {
		return inv_creditamount;
	}

	public void setInv_creditamount(String inv_creditamount) {
		this.inv_creditamount = inv_creditamount;
	}

	public String getInv_date() {
		return inv_date;
	}

	public void setInv_date(String inv_date) {
		this.inv_date = inv_date;
	}

	public String getInv_receiptno() {
		return inv_receiptno;
	}

	public void setInv_receiptno(String inv_receiptno) {
		this.inv_receiptno = inv_receiptno;
	}
}
