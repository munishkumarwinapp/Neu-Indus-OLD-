package com.winapp.printcube.model;

public interface TransparentTransmissionListener {

	public void onOpen(boolean success);
//	public void onClose(boolean success);
	public void onReceive(byte[] data, int length);
}
