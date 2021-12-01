package com.winapp.sot;

import android.os.Parcel;
import android.os.Parcelable;

public class ProdDetails implements Parcelable {

	private String itemnum;
	private String itemcode;
	private String description;
	private String qty;
	private String price;
	private String total;
	private static String totalbalance;
    private String sno;
    private String sortproduct;
	private double focqty;
	private double exchangeqty;
	private String UOMCode;
	private String tax;
	private String subtotal;
	private String taxType="";
	private String taxPerc="";
	public ProdDetails(Parcel in) {

		this.itemnum = in.readString();
		this.itemcode = in.readString();
		this.description = in.readString();
		this.qty = in.readString();
		this.price = in.readString();
		this.total = in.readString();

	}

	public ProdDetails() {
		
	}

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getItemcode() {
		return itemcode;
	}

	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getItemnum() {
		return itemnum;
	}

	public void setItemnum(String itemnum) {
		this.itemnum = itemnum;
	}

	public static String getTotalbalance() {
		return totalbalance;
	}

	public static void setTotalbalance(String totalbalance) {
		ProdDetails.totalbalance = totalbalance;
	}

	public String getSortproduct() {
		return sortproduct;
	}

	public void setSortproduct(String sortproduct) {
		this.sortproduct = sortproduct;
	}

	@Override
	public int describeContents() {
		
		return 0;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
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

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public String getUOMCode() {
		return UOMCode;
	}

	public void setUOMCode(String uOMCode) {
		UOMCode = uOMCode;
	}

	public String getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}

	public String getTaxPerc() {
		return taxPerc;
	}

	public void setTaxPerc(String taxPerc) {
		this.taxPerc = taxPerc;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(itemnum);
		dest.writeString(itemcode);
		dest.writeString(description);
		dest.writeString(qty);
		dest.writeString(price);
		dest.writeString(total);

	}

	public static final Creator<ProdDetails> CREATOR = new Creator<ProdDetails>() {
		@Override
		public ProdDetails createFromParcel(Parcel in) {
			return new ProdDetails(in);
		}

		@Override
		public ProdDetails[] newArray(int size) {
			return new ProdDetails[size];
		}
	};
}
