package com.winapp.helper;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsManager implements Constants {
	private SharedPreferences preferences;
	private Context context;

	public SettingsManager(Context context) {
		this.context = context;
		preferences = this.context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
	}

	public void setApplicationType(String applicationtype) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_APPLICATIONTYPE, applicationtype);
		editor.commit();
	}

	public String getApplicationType() {
		return preferences.getString(PREF_ALLOW_APPLICATIONTYPE, null);
	}

	public void setGeneralSettings(String generalsettings) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_GENERALSETTINGS, generalsettings);
		editor.commit();
	}

	public String getGeneralSettings() {
		return preferences.getString(PREF_ALLOW_GENERALSETTINGS, null);
	}

	public void setUserName(String username) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_USERNAME, username);
		editor.commit();
	}

	public String getUserName() {
		return preferences.getString(PREF_ALLOW_USERNAME, null);
	}

	public void setLocationCode(String locationname) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_LOCATIONCODE, locationname);
		editor.commit();
	}

	public String getLocationCode() {
		return preferences.getString(PREF_ALLOW_LOCATIONCODE, null);
	}

	public void setLocationName(String locationname) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_LOCATIONNAME, locationname);
		editor.commit();
	}

	public String getLocationName() {
		return preferences.getString(PREF_ALLOW_LOCATIONNAME, null);
	}

	public void setLocations(HashMap<String, String> location) {
		SharedPreferences prefer = this.context.getSharedPreferences(
				PREF_ALLOW_LOCATION_HM, Context.MODE_PRIVATE);
		Editor editor = prefer.edit();
		for (Entry<String, String> entry : location.entrySet()) {
			editor.putString(entry.getKey(), entry.getValue());
		}
		editor.commit();
	}

	public HashMap<String, String> getLocations() {
		SharedPreferences prefer = this.context.getSharedPreferences(
				PREF_ALLOW_LOCATION_HM, Context.MODE_PRIVATE);
		HashMap<String, String> location = new HashMap<String, String>();
		for (Entry<String, ?> entry : prefer.getAll().entrySet()) {
			location.put(entry.getKey(), entry.getValue().toString());
		}
		return location;
	}

	public void setLocation(Set<String> location) {

		Editor editor = preferences.edit();
		editor.putStringSet(PREF_ALLOW_LOCATION, location);
		editor.commit();
	}

	public Set<String> getLocation() {
		return preferences.getStringSet(PREF_ALLOW_LOCATION, null);
	}

	public void setCompanyName(String companyname) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_COMPANYNAME, companyname);
		editor.commit();
	}

	public String getCompanyName() {
		return preferences.getString(PREF_ALLOW_COMPANYNAME, null);
	}

	public void setCompanyAddress1(String address1) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_COMPANYADDRESS1, address1);
		editor.commit();
	}

	public String getCompanyAddress1() {
		return preferences.getString(PREF_ALLOW_COMPANYADDRESS1, null);
	}

	public void setCompanyAddress2(String address2) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_COMPANYADDRESS2, address2);
		editor.commit();
	}

	public String getCompanyAddress2() {
		return preferences.getString(PREF_ALLOW_COMPANYADDRESS2, null);
	}

	public void setCompanyLocation(String area) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_COMPANYLOCATION, area);
		editor.commit();
	}

	public String getCompanyLocation() {
		return preferences.getString(PREF_ALLOW_COMPANYLOCATION, null);
	}

	public void setCompanyPhone(String companyphone) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_COMPANYPHONE, companyphone);
		editor.commit();
	}

	public String getCompanyPhone() {
		return preferences.getString(PREF_ALLOW_COMPANYPHONE, null);
	}

	public void setCompanyTax(String companytax) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_COMPANYTAX, companytax);
		editor.commit();
	}

	public String getCompanyTax() {
		return preferences.getString(PREF_ALLOW_COMPANYTAX, null);
	}

	public void setGoodsReceive(String goodreceive) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_GOODRECEIVE, goodreceive);
		editor.commit();
	}

	public String getGoodsReceive() {
		return preferences.getString(PREF_ALLOW_GOODRECEIVE, null);
	}

	public void setSalesOrder(String salesorder) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_SALESORDER, salesorder);
		editor.commit();
	}

	public String getSalesOrder() {
		return preferences.getString(PREF_ALLOW_SALESORDER, null);
	}

	public void setDeliveryOrder(String deliveryorder) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_DELIVERYORDER, deliveryorder);
		editor.commit();
	}

	public String getDeliveryOrder() {
		return preferences.getString(PREF_ALLOW_DELIVERYORDER, null);
	}

	public void setInvoice(String invoice) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_INVOICE, invoice);
		editor.commit();
	}

	public String getInvoice() {
		return preferences.getString(PREF_ALLOW_INVOICE, null);
	}

	public void setSalesReturn(String salesreturn) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_SALESRETURN, salesreturn);
		editor.commit();
	}

	public String getSalesReturn() {
		return preferences.getString(PREF_ALLOW_SALESRETURN, null);
	}

	public void setReceipts(String receipts) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_RECEIPTS, receipts);
		editor.commit();
	}

	public String getReceipts() {
		return preferences.getString(PREF_ALLOW_RECEIPTS, null);
	}

	public void setProductList(String receipts) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_PRODUCTLIST, receipts);
		editor.commit();
	}

	public String getProductList() {
		return preferences.getString(PREF_ALLOW_PRODUCTLIST, null);
	}

	public void setStockRequest(String stockrequest) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_STOCKREQUEST, stockrequest);
		editor.commit();
	}

	public String getStockRequest() {
		return preferences.getString(PREF_ALLOW_STOCKREQUEST, null);
	}

	public void setTransfer(String transfer) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_TRANSFER, transfer);
		editor.commit();
	}

	public String getTransfer() {
		return preferences.getString(PREF_ALLOW_TRANSFER, null);
	}

	public void setCustomerList(String customerlist) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_CUSTOMERLIST, customerlist);
		editor.commit();
	}

	public String getCustomerList() {
		return preferences.getString(PREF_ALLOW_CUSTOMERLIST, null);
	}

	public void setCatalog(String catalog) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_CATALOG, catalog);
		editor.commit();
	}

	public String getCatalog() {
		return preferences.getString(PREF_ALLOW_CATALOG, null);
	}

	public void setSetting(String setting) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_SETTINGS, setting);
		editor.commit();
	}

	public String getSetting() {
		return preferences.getString(PREF_ALLOW_SETTINGS, null);
	}

	public void setTransferchangefromloc(String transferchangefromloc) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_TRANSFERCHANGEFROMLOC,
				transferchangefromloc);
		editor.commit();
	}

	public String getTransferchangefromloc() {
		return preferences.getString(PREF_ALLOW_TRANSFERCHANGEFROMLOC, null);
	}

	public void setProductAddAllowed(boolean isAllowed) {
		Editor editor = preferences.edit();
		editor.putBoolean(PREF_ALLOW_PRODUCTADD, isAllowed);
		editor.commit();
	}

	public boolean isProductAddAllowed() {
		return preferences.getBoolean(PREF_ALLOW_PRODUCTADD, false);
	}

	public void setCustomerAddAllowed(boolean isAllowed) {
		Editor editor = preferences.edit();
		editor.putBoolean(PREF_ALLOW_CUSTOMERADD, isAllowed);
		editor.commit();
	}

	public boolean isCustomerAddAllowed() {
		return preferences.getBoolean(PREF_ALLOW_CUSTOMERADD, false);
	}

	public void setEditInvoiceAllowed(boolean isAllowed) {
		Editor editor = preferences.edit();
		editor.putBoolean(PREF_ALLOW_EDITINVOICE, isAllowed);
		editor.commit();
	}

	public boolean isEditInvoiceAllowed() {
		return preferences.getBoolean(PREF_ALLOW_EDITINVOICE, false);
	}

	public void setDeleteInvoiceAllowed(boolean isAllowed) {
		Editor editor = preferences.edit();
		editor.putBoolean(PREF_ALLOW_DELETEINVOICE, isAllowed);
		editor.commit();
	}

	public boolean isDeleteInvoiceAllowed() {
		return preferences.getBoolean(PREF_ALLOW_DELETEINVOICE, false);
	}

	public void setDeleteReceiptAllowed(boolean isAllowed) {
		Editor editor = preferences.edit();
		editor.putBoolean(PREF_ALLOW_DELETERECEIPT, isAllowed);
		editor.commit();
	}

	public boolean isDeleteReceiptAllowed() {
		return preferences.getBoolean(PREF_ALLOW_DELETERECEIPT, false);
	}
}