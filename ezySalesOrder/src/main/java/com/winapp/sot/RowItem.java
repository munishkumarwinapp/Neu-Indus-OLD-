package com.winapp.sot;

public class RowItem {

	private String title;
	private int icon;
	private static String deviceID;
	static boolean afterPrint = false;
	private static String deviceName;
	private static String printoption = "False";

	public RowItem(String title, int icon) {
		this.title = title;
		this.icon = icon;

	}

	public static String getDeviceName() {
		return deviceName;
	}

	public static void setDeviceName(String deviceName) {
		RowItem.deviceName = deviceName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public static String getPrintoption() {
		return printoption;
	}

	public static void setPrintoption(String printoption) {
		RowItem.printoption = printoption;
	}

	public static String getDeviceID() {
		return deviceID;
	}

	public static void setDeviceID(String deviceID) {
		RowItem.deviceID = deviceID;
	}

	public static boolean isAfterPrint() {
		return afterPrint;
	}

	public static void setAfterPrint(boolean afterPrint) {
		RowItem.afterPrint = afterPrint;
	}

}
