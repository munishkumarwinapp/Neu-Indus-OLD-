package com.winapp.printer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.winapp.model.CurrencyDeno;
import com.winapp.model.Product;
import com.winapp.sot.ProdDetails;
import com.winapp.sot.ProductDetails;

public class PreviewPojo {
	private static String nofcopies = "";
	private static List<ProductDetails> products;
	private static List<ProductDetails> productsDetails;
	private static List<ProdDetails> receiptproducts;
	private static List<Product> mAttributeDetail;
	private static ArrayList<HashMap<String, String>> searchProductArr;
	private static ArrayList<CurrencyDeno> settlementDetail;
	private static ArrayList<CurrencyDeno> settlementHeader;
	private static ArrayList<Product> mAttributeDetails;


	public static String getNofcopies() {
		return nofcopies;
	}

	public static void setNofcopies(String nofcopies) {
		PreviewPojo.nofcopies = nofcopies;
	}

	public static List<ProductDetails> getProducts() {
		return products;
	}

	public static void setProducts(List<ProductDetails> products) {
		PreviewPojo.products = products;
	}

	public static List<ProductDetails> getProductsDetails() {
		return productsDetails;
	}

	public static void setProductsDetails(List<ProductDetails> productsDetails) {
		PreviewPojo.productsDetails = productsDetails;
	}

	public static List<ProdDetails> getReceiptproducts() {
		return receiptproducts;
	}

	public static void setReceiptproducts(List<ProdDetails> receiptproducts) {
		PreviewPojo.receiptproducts = receiptproducts;
	}

	public static List<Product> getAttributeDetail() {
		return mAttributeDetail;
	}

	public static void setAttributeDetail(List<Product> mAttributeDetail) {
		PreviewPojo.mAttributeDetail = mAttributeDetail;
	}

	public static ArrayList<HashMap<String, String>> getSearchProductArr() {
		return searchProductArr;
	}

	public static void setSearchProductArr(ArrayList<HashMap<String, String>> searchProductArr) {
		PreviewPojo.searchProductArr = searchProductArr;
	}

	public static ArrayList<CurrencyDeno> getSettlementHeader() {
		return settlementHeader;
	}

	public static void setSettlementHeader(ArrayList<CurrencyDeno> settlementHeader) {
		PreviewPojo.settlementHeader = settlementHeader;
	}

	public static ArrayList<CurrencyDeno> getSettlementDetail() {
		return settlementDetail;
	}

	public static void setSettlementDetail(ArrayList<CurrencyDeno> settlementDetail) {
		PreviewPojo.settlementDetail = settlementDetail;
	}

	public static ArrayList<Product> getmAttributeDetails() {
		return mAttributeDetails;
	}

	public static void setmAttributeDetails(ArrayList<Product> mAttributeDetails) {
		PreviewPojo.mAttributeDetails = mAttributeDetails;
	}
}
