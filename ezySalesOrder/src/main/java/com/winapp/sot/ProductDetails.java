package com.winapp.sot;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.winapp.model.Product;

public class ProductDetails implements Parcelable {
	private String sno;
	private String itemno;
	private String itemdate;
	private String itemcode;
	private String description;
	private String qty;
	private String price;
	private String total;
	private String itemdisc;
	private String billdisc;
	private String subtotal;
	private String tax;
	private String nettot;
	private String totaloutstanding;
	private List<ProdDetails> productsDetails;
	private double totalqty;
	private String paidamount;
	private String remarks = "";
	private String sortproduct;
	private double focqty;
	private double exchangeqty;
    private String issueQty;
	private String returnQty;
	private String product_batchno;
	private String batch_productcode;
	private String receiptMessage;
	private String sortOrder;
	private String cqty;
	private String lqty;
	private String quantity;
	private String UOMCode;
	private String CreditAmount;
	private String BalanceAmount;
	private String taxType="";
	private String taxPerc="";
	private String createDate="";
	private ArrayList<Product> attributeList = new ArrayList<Product>();
	private static String focQtys;
	private String taxtype="";
	private String consignmentNumber ="";
	private String customeraddress1;
	private String customeraddress2;
	private String customeraddress3;

	public ProductDetails(ProductDetails productDetails, ArrayList<Product> attributeList) {
		super();
		this.itemcode = productDetails.getItemcode();
		this.description = productDetails.getDescription();
		this.qty = productDetails.getQty();
		this.price = productDetails.getPrice();
		this.total = productDetails.getTotal();
		this.taxType = productDetails.getTaxType();
		this.taxPerc = productDetails.getTaxPerc();
		this.attributeList = attributeList;
	}



	public String getCqty() {
	  return cqty;
	 }

	 public void setCqty(String cqty) {
	  this.cqty = cqty;
	 }

	 public String getLqty() {
	  return lqty;
	 }

	 public void setLqty(String lqty) {
	  this.lqty = lqty;
	 }
	
	
	public static final Creator<ProductDetails> CREATOR = new Creator<ProductDetails>() {
		@Override
		public ProductDetails createFromParcel(Parcel source) {
			return new ProductDetails(source);
		}

		@Override
		public ProductDetails[] newArray(int size) {
			return new ProductDetails[size];
		}
	};

	public ArrayList<Product> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(ArrayList<Product> attributeList) {
		this.attributeList = attributeList;
	}

	public ProductDetails(Parcel in) {
		itemno = in.readString();
		itemdate = in.readString();
		itemdisc = in.readString();
		billdisc = in.readString();
		subtotal = in.readString();
		tax = in.readString();
		nettot = in.readString();
		paidamount = in.readString();
		remarks = in.readString();
		totaloutstanding = in.readString();
	}

	public ProductDetails() {

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
	
	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public double getExchangeqty() {
		return exchangeqty;
	}

	public void setExchangeqty(double exchangeqty) {
		this.exchangeqty = exchangeqty;
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

	public String getItemdisc() {
		return itemdisc;
	}

	public void setItemdisc(String itemdisc) {
		this.itemdisc = itemdisc;
	}

	public String getBilldisc() {
		return billdisc;
	}

	public void setBilldisc(String billdisc) {
		this.billdisc = billdisc;
	}

	public String getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getNettot() {
		return nettot;
	}

	public void setNettot(String nettot) {
		this.nettot = nettot;
	}

	public String getTotaloutstanding() {
		return totaloutstanding;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setTotaloutstanding(String totaloutstanding) {
		this.totaloutstanding = totaloutstanding;
	}

	public String getItemno() {
		return itemno;
	}

	public void setItemno(String itemno) {
		this.itemno = itemno;
	}

	public String getItemdate() {
		return itemdate;
	}

	public void setItemdate(String itemdate) {
		this.itemdate = itemdate;
	}

	public String getPaidamount() {
		return paidamount;
	}

	public void setPaidamount(String paidamount) {
		this.paidamount = paidamount;
	}

	public double getTotalqty() {
		return totalqty;
	}

	public void setTotalqty(double totalqty) {
		this.totalqty = totalqty;
	}

	public String getSortproduct() {
		return sortproduct;
	}

	public void setSortproduct(String sortproduct) {
		this.sortproduct = sortproduct;
	}

	public double getFocqty() {
		return focqty;
	}

	public void setFocqty(double focqty) {
		this.focqty = focqty;
	}

	public String getTaxPerc() {
		return taxPerc;
	}

	public void setTaxPerc(String taxPerc) {
		this.taxPerc = taxPerc;
	}

	public String getIssueQty() {
		return issueQty;
	}

	public void setIssueQty(String issueQty) {
		this.issueQty = issueQty;
	}

	public String getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(String returnQty) {
		this.returnQty = returnQty;
	}

	public String getProduct_batchno() {
		return product_batchno;
	}

	public void setProduct_batchno(String product_batchno) {
		this.product_batchno = product_batchno;
	}

	
	
	public String getBatch_productcode() {
		return batch_productcode;
	}

	public void setBatch_productcode(String batch_productcode) {
		this.batch_productcode = batch_productcode;
	}

	
	
	public String getUOMCode() {
		return UOMCode;
	}

	public void setUOMCode(String uOMCode) {
		UOMCode = uOMCode;
	}
	
	

	public String getCreditAmount() {
		return CreditAmount;
	}

	public void setCreditAmount(String creditAmount) {
		CreditAmount = creditAmount;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(itemno);
		dest.writeString(itemdate);
		dest.writeString(price);
		dest.writeString(itemdisc);
		dest.writeString(billdisc);
		dest.writeString(subtotal);
		dest.writeString(tax);
		dest.writeString(nettot);
		dest.writeString(paidamount);
		dest.writeString(remarks);
		dest.writeString(totaloutstanding);
	}

	@Override
	public int describeContents() {

		return 0;
	}

	public List<ProdDetails> getProductsDetails() {
		return productsDetails;
	}

	public void setProductsDetails(List<ProdDetails> productsDetails) {
		this.productsDetails = productsDetails;
	}

	public String getReceiptMessage() {
		return receiptMessage;
	}

	public void setReceiptMessage(String receiptMessage) {
		this.receiptMessage = receiptMessage;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getBalanceAmount() {
		return BalanceAmount;
	}

	public void setBalanceAmount(String balanceAmount) {
		BalanceAmount = balanceAmount;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public static String getFocQtys() {
		return focQtys;
	}

	public static void setFocQtys(String focQtys) {
		ProductDetails.focQtys = focQtys;
	}


	public String getConsignmentNumber() {
		return consignmentNumber;
	}

	public void setConsignmentNumber(String consignmentNumber) {
		this.consignmentNumber = consignmentNumber;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
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
}
