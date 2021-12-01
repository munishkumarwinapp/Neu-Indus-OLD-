package com.winapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StockCount implements Parcelable {
	private String productCode;
	private String productName;
	private String categoryCode;
	private String subcategoryCode;
	private int qty;
	private int countCQty;
	private int countLQty;
	private int countQty;
	private int pcsPerCarton;
	private int noOfCarton;
	private boolean qtyFocus;
	private String havebatch;
	private String havexpiry;
	
	
	public StockCount(){
		
	}
	public StockCount(Parcel in) {
		this.productCode = in.readString();
		this.productName = in.readString();
		this.categoryCode = in.readString();
		this.subcategoryCode = in.readString();
		this.qty = in.readInt();
		this.countCQty = in.readInt();
		this.countLQty = in.readInt();
		this.countQty = in.readInt();
		this.pcsPerCarton = in.readInt();
		this.noOfCarton = in.readInt();
		this.havebatch = in.readString();
		this.havexpiry = in.readString();
		
	}



	public String getHavebatch() {
		return havebatch;
	}
	public void setHavebatch(String havebatch) {
		this.havebatch = havebatch;
	}
	public String getHavexpiry() {
		return havexpiry;
	}
	public void setHavexpiry(String havexpiry) {
		this.havexpiry = havexpiry;
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
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getSubcategoryCode() {
		return subcategoryCode;
	}
	public void setSubcategoryCode(String subcategoryCode) {
		this.subcategoryCode = subcategoryCode;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public int getCountCQty() {
		return countCQty;
	}
	public void setCountCQty(int countCQty) {
		this.countCQty = countCQty;
	}
	public int getCountLQty() {
		return countLQty;
	}
	public void setCountLQty(int countLQty) {
		this.countLQty = countLQty;
	}
	public int getCountQty() {
		return countQty;
	}
	public void setCountQty(int countQty) {
		this.countQty = countQty;
	}
	public int getPcsPerCarton() {
		return pcsPerCarton;
	}
	public void setPcsPerCarton(int pcsPerCarton) {
		this.pcsPerCarton = pcsPerCarton;
	}
	public int getNoOfCarton() {
		return noOfCarton;
	}
	public void setNoOfCarton(int noOfCarton) {
		this.noOfCarton = noOfCarton;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(productCode);
		dest.writeString(productName);
		dest.writeString(categoryCode);
		dest.writeString(subcategoryCode);
		dest.writeInt(qty);
		dest.writeInt(countCQty);
		dest.writeInt(countLQty);
		dest.writeInt(countQty);
		dest.writeInt(pcsPerCarton);
		dest.writeInt(noOfCarton);
		dest.writeString(havebatch);
		dest.writeString(havexpiry);
	
	}
	public static final Creator<StockCount> CREATOR = new Creator<StockCount>() {
		@Override
		public StockCount createFromParcel(Parcel in) {
			return new StockCount(in);
		}

		@Override
		public StockCount[] newArray(int size) {
			return new StockCount[size];
		}
	};
}
