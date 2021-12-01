package com.winapp.sotdetails;

public class SOTdetailsGetSet {

	static int screenWidth;
	static int screenHeight;

	static int grid_row_width;
	static int grid_row_height;

	String cust_Code;
	String cust_Name;

	public String page;

	public String stock_prod_code;
	public String stock_prod_name;
	public String stock_cqty;
	public String stock_lqty;
	public String stock_qty;
	public String stock_location;
	public String sortOrder;
	public String status;
	
	public SOTdetailsGetSet() {

	}

	public SOTdetailsGetSet(String cust_Code, String cust_Name) {
		this.cust_Code = cust_Code;
		this.cust_Name = cust_Name;
	}

	public String getCust_Code() {
		return cust_Code;
	}

	public void setCust_Code(String cust_Code) {
		this.cust_Code = cust_Code;
	}

	public String getCust_Name() {
		return cust_Name;
	}

	public void setCust_Name(String cust_Name) {
		this.cust_Name = cust_Name;
	}

	public static int getGrid_row_width() {
		return grid_row_width;
	}

	public static void setGrid_row_width(int grid_row_width) {
		SOTdetailsGetSet.grid_row_width = grid_row_width;
	}

	public static int getGrid_row_height() {
		return grid_row_height;
	}

	public static void setGrid_row_height(int grid_row_height) {
		SOTdetailsGetSet.grid_row_height = grid_row_height;
	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static void setScreenWidth(int screenWidth) {
		SOTdetailsGetSet.screenWidth = screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}

	public static void setScreenHeight(int screenHeight) {
		SOTdetailsGetSet.screenHeight = screenHeight;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getStock_prod_code() {
		return stock_prod_code;
	}

	public void setStock_prod_code(String stock_prod_code) {
		this.stock_prod_code = stock_prod_code;
	}

	public String getStock_prod_name() {
		return stock_prod_name;
	}

	public void setStock_prod_name(String stock_prod_name) {
		this.stock_prod_name = stock_prod_name;
	}

	public String getStock_cqty() {
		return stock_cqty;
	}

	public void setStock_cqty(String stock_cqty) {
		this.stock_cqty = stock_cqty;
	}

	public String getStock_lqty() {
		return stock_lqty;
	}

	public void setStock_lqty(String stock_lqty) {
		this.stock_lqty = stock_lqty;
	}

	public String getStock_qty() {
		return stock_qty;
	}

	public void setStock_qty(String stock_qty) {
		this.stock_qty = stock_qty;
	}

	public String getStock_location() {
		return stock_location;
	}

	public void setStock_location(String stock_location) {
		this.stock_location = stock_location;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
