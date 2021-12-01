package com.winapp.fwms;

public class ClearFieldSetterGetter {

	static boolean clearProduct = false;
	static boolean clearBarcode = false;

	public static boolean isClearProduct() {
		return clearProduct;
	}

	public static void setClearProduct(boolean clearProduct) {
		ClearFieldSetterGetter.clearProduct = clearProduct;
	}

	public static boolean isClearBarcode() {
		return clearBarcode;
	}

	public static void setClearBarcode(boolean clearBarcode) {
		ClearFieldSetterGetter.clearBarcode = clearBarcode;
	}

}
