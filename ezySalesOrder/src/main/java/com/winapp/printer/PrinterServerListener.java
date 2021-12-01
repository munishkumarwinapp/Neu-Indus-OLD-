package com.winapp.printer;

import java.net.Socket;

public interface PrinterServerListener {
    public void onConnect(Socket socket);
}
