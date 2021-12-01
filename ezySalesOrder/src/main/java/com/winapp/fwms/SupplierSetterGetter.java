package com.winapp.fwms;

import java.util.HashMap;

public class SupplierSetterGetter {

	static String Locationcode;
	static String locCode;
	static String companyCode="";
	static String companyName;
	static String suppliercode;
	static String remarks;
	static double total;
	static String date;
	static String username;
	static int expheader=0;
	static String taxCode ="";
	static HashMap<String,String>loc_code_name;
	static String locationCheck;
	
	public static String getCompanyName() {
		return companyName;
	}

	public static void setCompanyName(String companyName) {
		SupplierSetterGetter.companyName = companyName;
	}

	public static String getCompanyCode() {
		return companyCode;
	}

	public static void setCompanyCode(String companyCode) {
		SupplierSetterGetter.companyCode = companyCode;
	}

	public static String getLocationcode() {
		return Locationcode;
	}

	public static void setLocationcode(String locationcode) {
		Locationcode = locationcode;
	}

	public static String getSuppliercode() {
		return suppliercode;
	}

	public static void setSuppliercode(String suppliercode) {
		SupplierSetterGetter.suppliercode = suppliercode;
	}

	public static String getRemarks() {
		return remarks;
	}

	public static void setRemarks(String remarks) {
		SupplierSetterGetter.remarks = remarks;
	}

	public static double getTotal() {
		return total;
	}

	public static void setTotal(double total) {
		SupplierSetterGetter.total = total;
	}

	public static String getDate() {
		return date;
	}

	public static void setDate(String date) {
		SupplierSetterGetter.date = date;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		SupplierSetterGetter.username = username;
	}

	public static String getLocCode() {
		return locCode;
	}

	public static void setLocCode(String locCode) {
		SupplierSetterGetter.locCode = locCode;
	}

	public static int getExpheader() {
		return expheader;
	}

	public static void setExpheader(int expheader) {
		SupplierSetterGetter.expheader = expheader;
	}

	public static HashMap<String, String> getLoc_code_name() {
		return loc_code_name;
	}

	public static void setLoc_code_name(HashMap<String, String> loc_code_name) {
		SupplierSetterGetter.loc_code_name = loc_code_name;
	}

	public static String getTaxCode() {
		return taxCode;
	}

	public static void setTaxCode(String taxCode) {
		SupplierSetterGetter.taxCode = taxCode;
	}

	public static String getLocationCheck() {
		return locationCheck;
	}

	public static void setLocationCheck(String locationCheck) {
		SupplierSetterGetter.locationCheck = locationCheck;
	}
}
