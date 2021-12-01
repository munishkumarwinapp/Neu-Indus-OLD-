package com.winapp.fwms;

public class SupplierObject {
	int suppliercode;
	String suppliername;

	SupplierObject(int suppliercode, String suppliername) {
		this.suppliercode = suppliercode;
		this.suppliername = suppliername;

	}

	public int getSuppliercode() {
		return suppliercode;
	}

	public void setSuppliercode(int suppliercode) {
		this.suppliercode = suppliercode;
	}

	public String getSuppliername() {
		return suppliername;
	}

	public void setSuppliername(String suppliername) {
		this.suppliername = suppliername;
	}

	@Override
	public String toString() {
		return "SupplierObject : " + "Suppliercode = " + suppliercode
				+ ", Suppliername=" + suppliername;
	}

}
