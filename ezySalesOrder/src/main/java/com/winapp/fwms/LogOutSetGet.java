package com.winapp.fwms;

import android.graphics.Bitmap;

public class LogOutSetGet {
	private static boolean Active;
	private static String applicationType="";
	private static String addProduct="";
	private static Bitmap bitmap;
	private static String calcCarton="";
	private static int msgCount;
	 
	 public static int getMsgCount() {
	  return msgCount;
	 }

	 public static void setMsgCount(int msgCount) {
	  LogOutSetGet.msgCount = msgCount;
	 }
	 
	public static boolean isActive() {
		return Active;
	}

	public static void setActive(boolean active) {
		Active = active;
	}

	public static String getApplicationType() {
		return applicationType;
	}

	public static void setApplicationType(String applicationType) {
		LogOutSetGet.applicationType = applicationType;
	}

	public static String getAddProduct() {
		return addProduct;
	}

	public static void setAddProduct(String addProduct) {
		LogOutSetGet.addProduct = addProduct;
	}

	public static Bitmap getBitmap() {
		return bitmap;
	}

	public static void setBitmap(Bitmap bitmap) {
		LogOutSetGet.bitmap = bitmap;
	}

	public static String getCalcCarton() {
		return calcCarton;
	}

	public static void setCalcCarton(String calcCarton) {
		LogOutSetGet.calcCarton = calcCarton;
	}
		
	
}
