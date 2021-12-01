package com.winapp.printcube.net;

public interface INetworkTransmission {

	public void setParameters(String ip, int port);
	public boolean open();
	public void close();
	public boolean send(String text);
	public boolean sendBytes(byte[] text);
	public void onReceive(byte[] buffer, int length);
}
