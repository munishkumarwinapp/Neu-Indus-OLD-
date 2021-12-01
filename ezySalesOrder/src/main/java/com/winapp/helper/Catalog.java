package com.winapp.helper;

public class Catalog {
	private String productcode;
	private String productname;
	private String price;
	private String categorycode;
	private String subcategorycode;
	private String uomcode;
	private String pcspercarton;
	private String wholesalesprice;
	private String retailprice;
	private String productimage;
	private static String customerCode = "";
	private static String customerName = "";
	private static String customerGroupCode = "";
	private static boolean mShowingPrice;
	private static boolean isSearchVisible;
	private static boolean isChildFragmentVisible;

	public Catalog() {
		// TODO Auto-generated constructor stub
	}

	public Catalog(String productcode, String productname, String productimage,
			String price, String categorycode, String subcategorycode,
			String uomcode, String pcspercarton, String wholesalesprice) {
		// TODO Auto-generated constructor stub
		this.productcode = productcode;
		this.productname = productname;
		this.productimage = productimage;
		this.price = price;
		this.categorycode = categorycode;
		this.subcategorycode = subcategorycode;
		this.uomcode = uomcode;
		this.pcspercarton = pcspercarton;
		this.wholesalesprice = wholesalesprice;

	}

	public String getProductcode() {
		return productcode;
	}

	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCategorycode() {
		return categorycode;
	}

	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}

	public String getSubcategorycode() {
		return subcategorycode;
	}

	public void setSubcategorycode(String subcategorycode) {
		this.subcategorycode = subcategorycode;
	}

	public String getUomcode() {
		return uomcode;
	}

	public void setUomcode(String uomcode) {
		this.uomcode = uomcode;
	}

	public String getPcspercarton() {
		return pcspercarton;
	}

	public void setPcspercarton(String pcspercarton) {
		this.pcspercarton = pcspercarton;
	}

	public String getWholesalesprice() {
		return wholesalesprice;
	}

	public void setWholesalesprice(String wholesalesprice) {
		this.wholesalesprice = wholesalesprice;
	}

	public String getRetailprice() {
		return retailprice;
	}

	public void setRetailprice(String retailprice) {
		this.retailprice = retailprice;
	}

	public String getProductimage() {
		return productimage;
	}

	public void setProductimage(String productimage) {
		this.productimage = productimage;
	}

	public static String getCustomerCode() {
		return customerCode;
	}

	public static void setCustomerCode(String customerCode) {
		Catalog.customerCode = customerCode;
	}

	public static String getCustomerName() {
		return customerName;
	}

	public static void setCustomerName(String customerName) {
		Catalog.customerName = customerName;
	}

	public static String getCustomerGroupCode() {
		return customerGroupCode;
	}

	public static void setCustomerGroupCode(String customerGroupCode) {
		Catalog.customerGroupCode = customerGroupCode;
	}

	public static boolean isUpdatedPrice() {
		return mShowingPrice;
	}

	public static void setUpdatedPrice(boolean showingPrice) {
		Catalog.mShowingPrice = showingPrice;
	}

	public static boolean isChildFragmentVisible() {
		return isChildFragmentVisible;
	}

	public static void setChildFragmentVisible(boolean isChildFragmentVisible) {
		Catalog.isChildFragmentVisible = isChildFragmentVisible;
	}

	public static boolean isSearchVisible() {
		return isSearchVisible;
	}

	public static void setSearchVisible(boolean isSearchVisible) {
		Catalog.isSearchVisible = isSearchVisible;
	}
}
