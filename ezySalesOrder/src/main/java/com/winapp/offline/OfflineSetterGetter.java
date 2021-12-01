package com.winapp.offline;

import java.util.ArrayList;
import java.util.HashMap;

public class OfflineSetterGetter {

	public static ArrayList<HashMap<String, String>> stockUpdateArr = new ArrayList<HashMap<String, String>>();

	public static ArrayList<HashMap<String, String>> getStockUpdateArr() {
		return stockUpdateArr;
	}

	public static void setStockUpdateArr(
			ArrayList<HashMap<String, String>> stockUpdateArr) {
		OfflineSetterGetter.stockUpdateArr = stockUpdateArr;
	}

}
