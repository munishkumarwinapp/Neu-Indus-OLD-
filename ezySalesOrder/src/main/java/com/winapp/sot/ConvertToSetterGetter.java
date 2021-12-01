package com.winapp.sot;

import java.util.ArrayList;
import java.util.HashMap;

public class ConvertToSetterGetter {

	public static String slNo = "";
	public static String DoNo = "";
	public static String SoNo = "";
	public static String edit_inv_no = "";
	public static String edit_gra_no = "";
	public static String edit_do_no = "";
	public static String edit_stockreq_no = "";
	public static String edit_salesreturn_no = "";

	public static String getEdit_salesreturn_no() {
		return edit_salesreturn_no;
	}

	public static void setEdit_salesreturn_no(String edit_salesreturn_no) {
		ConvertToSetterGetter.edit_salesreturn_no = edit_salesreturn_no;
	}

	public static ArrayList<HashMap<String, String>> stockRequestBatchDetailArr;
	
	public static String getSlNo() {
		return slNo;
	}

	public static void setSlNo(String slNo) {
		ConvertToSetterGetter.slNo = slNo;
	}

	public static String getDoNo() {
		return DoNo;
	}

	public static void setDoNo(String doNo) {
		DoNo = doNo;
	}

	public static String getSoNo() {
		return SoNo;
	}

	public static void setSoNo(String soNo) {
		SoNo = soNo;
	}

	public static String getEdit_inv_no() {
		return edit_inv_no;
	}

	public static void setEdit_inv_no(String edit_inv_no) {
		ConvertToSetterGetter.edit_inv_no = edit_inv_no;
	}

	public static String getEdit_gra_no() {
		return edit_gra_no;
	}

	public static void setEdit_gra_no(String edit_gra_no) {
		ConvertToSetterGetter.edit_gra_no = edit_gra_no;
	}

	public static String getEdit_do_no() {
		return edit_do_no;
	}

	public static void setEdit_do_no(String edit_do_no) {
		ConvertToSetterGetter.edit_do_no = edit_do_no;
	}

	public static String getEdit_stockreq_no() {
		return edit_stockreq_no;
	}

	public static void setEdit_stockreq_no(String edit_stockreq_no) {
		ConvertToSetterGetter.edit_stockreq_no = edit_stockreq_no;
	}

	public static ArrayList<HashMap<String, String>> getStockRequestBatchDetailArr() {
		return stockRequestBatchDetailArr;
	}

	public static void setStockRequestBatchDetailArr(
			ArrayList<HashMap<String, String>> stockRequestBatchDetailArr) {
		ConvertToSetterGetter.stockRequestBatchDetailArr = stockRequestBatchDetailArr;
	}



	
}
