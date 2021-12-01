package com.winapp.fwms;

public class AddProductSetterGetter {

	static boolean check_invalid = false;
	static String codeFld = "";
	static String descStr = "";
	static String strtsonStr = "";
	static String endsonStr = "";
	static String CtgryfldStr = "";
	static String SubCtgryfldStr = "";
	static String uomStr = "";
	static String piecesprcrtnStr = "";
	static boolean check_haveBatch = false;
	static boolean check_haveExpire = false;
	static boolean check_haveMFD = false;
	static boolean update = false;
	static String weight="";
	static String cartonPrice="";
	static String units="";
	static String binCode ="";
	
	public static boolean isCheck_invalid() {
		return check_invalid;
	}

	public static void setCheck_invalid(boolean check_invalid) {
		AddProductSetterGetter.check_invalid = check_invalid;
	}

	public static String getCodeFld() {
		return codeFld;
	}

	public static void setCodeFld(String codeFld) {
		AddProductSetterGetter.codeFld = codeFld;
	}

	public static String getDescStr() {
		return descStr;
	}

	public static void setDescStr(String descStr) {
		AddProductSetterGetter.descStr = descStr;
	}

	public static String getStrtsonStr() {
		return strtsonStr;
	}

	public static void setStrtsonStr(String strtsonStr) {
		AddProductSetterGetter.strtsonStr = strtsonStr;
	}

	public static String getEndsonStr() {
		return endsonStr;
	}

	public static void setEndsonStr(String endsonStr) {
		AddProductSetterGetter.endsonStr = endsonStr;
	}

	public static String getCtgryfldStr() {
		return CtgryfldStr;
	}

	public static void setCtgryfldStr(String ctgryfldStr) {
		CtgryfldStr = ctgryfldStr;
	}

	public static String getSubCtgryfldStr() {
		return SubCtgryfldStr;
	}

	public static void setSubCtgryfldStr(String subCtgryfldStr) {
		SubCtgryfldStr = subCtgryfldStr;
	}

	public static String getUomStr() {
		return uomStr;
	}

	public static void setUomStr(String uomStr) {
		AddProductSetterGetter.uomStr = uomStr;
	}

	public static String getPiecesprcrtnStr() {
		return piecesprcrtnStr;
	}

	public static void setPiecesprcrtnStr(String piecesprcrtnStr) {
		AddProductSetterGetter.piecesprcrtnStr = piecesprcrtnStr;
	}

	public static boolean isCheck_haveBatch() {
		return check_haveBatch;
	}

	public static void setCheck_haveBatch(boolean check_haveBatch) {
		AddProductSetterGetter.check_haveBatch = check_haveBatch;
	}

	public static boolean isCheck_haveExpire() {
		return check_haveExpire;
	}

	public static void setCheck_haveExpire(boolean check_haveExpire) {
		AddProductSetterGetter.check_haveExpire = check_haveExpire;
	}

	public static boolean isCheck_haveMFD() {
		return check_haveMFD;
	}

	public static void setCheck_haveMFD(boolean check_haveMFD) {
		AddProductSetterGetter.check_haveMFD = check_haveMFD;
	}

	public static boolean isUpdate() {
		return update;
	}

	public static void setUpdate(boolean update) {
		AddProductSetterGetter.update = update;
	}

	public static String getWeight() {
		return weight;
	}

	public static void setWeight(String weight) {
		AddProductSetterGetter.weight = weight;
	}

	public static String getCartonPrice() {
		return cartonPrice;
	}

	public static void setCartonPrice(String cartonPrice) {
		AddProductSetterGetter.cartonPrice = cartonPrice;
	}

	public static String getUnits() {
		return units;
	}

	public static void setUnits(String units) {
		AddProductSetterGetter.units = units;
	}

	public static String getBinCode() {
		return binCode;
	}

	public static void setBinCode(String binCode) {
		AddProductSetterGetter.binCode = binCode;
	}
}
