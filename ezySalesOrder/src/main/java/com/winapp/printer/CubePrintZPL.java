package com.winapp.printer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.datecs.api.emsr.EMSR;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.ProtocolAdapter;
import com.datecs.api.rfid.ContactlessCard;
import com.datecs.api.rfid.RC663;
import com.datecs.api.universalreader.UniversalReader;
import com.winapp.SFA.R;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.Product;
import com.winapp.model.Receipt;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printcube.utils.Constants;
import com.winapp.sot.Company;
import com.winapp.sot.In_Cash;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ProdDetails;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by user on 30-Dec-16.
 */

public class CubePrintZPL {
    public interface OnCompletedListener {
        public void onCompleted();
    }
    private Context context;
    private UIHelper helper;
    private String macAddress;
    public static String configLabel ="";

   // public static final int DESCRIPTION_WIDTH1 = 53;

  /*  public static final int PAPER_WIDTH = 80;
    public static final int SLNO_WIDTH = 4;
    public static final int DESCRIPTION_WIDTH = 42;
    public static final int QTY_WIDTH = 8;
    public static final int PRICE_WIDTH = 8;
    public static final int TAX_WIDTH = 8;
    public static final int TOTAL_WIDTH = 8;
    public static final int LEFT_SPACE = 28;
    public static final int RIGHT_SPACE = 25;
    private static final String LINE_SEPARATOR = "\r\n";*/

    public static int PAPER_WIDTH = 0;
    public static  int SLNO_WIDTH = 0;
    public static  int DESCRIPTION_WIDTH = 0;
    public static  int EXPENSE_DESCRIPTION_WIDTH = 0;
    public static  int QTY_WIDTH = 0;
    public static  int PRICE_WIDTH = 0;
    public static  int TAX_WIDTH = 0;
    public static  int TAX_TYPE_WIDTH = 0;
    public static  int TOTAL_WIDTH = 0;
    public static  int TOTAL_SPACE = 0;
    public static  int LEFT_SPACE = 0;
    public static  int RIGHT_SPACE = 0;
    public static  int BETWEEN_SPACE =0;
    public static  int INVOICENO = 0;
    public static  int INVOICEDATE =0;
    public static  int CREDIT_AMOUNT = 0;
    public static  int PAID_AMOUNT = 0;
    public static  int NETTOTAL = 0;
    public static  int NUM_WIDTH = 0;

    public static  int GST_SUMMARY_LABEL = 0;
    public static  int GST_AMOUNT_LABEL = 0;
    public static  int GST_LABEL = 0;

    public static  int GST_SUMMARY_VALUE = 0;
    public static  int GST_AMOUNT_VALUE = 0;
    public static  int GST_VALUE = 0;

    public static  int PRODUCT_NAME_WIDTH = 0;
    public static  int CARTON_WIDTH = 0;
    public static  int LOOSE_WIDTH = 0;
    public static  int QUANTITY_WIDTH = 0;
    public static  int INVOICE_NO_WIDTH = 0;
    public static  int CUSTOMER_NAME_WIDTH =0;
    public static  int  TOT_WIDTH = 0;

    private static String LINE_SEPARATOR = "";

    private static final String COLON  = ":";
    private static final String PRODUCT_CODE  = "ProductCode";
    private static final String PRODUCT_NAME  = "ProductName";
    private static final String QUANTITY  = "Quantity";
    private static final String PRICE  = "Price";
    private static final String TOTAL  = "Total";

    private static final String INVOICE_NO  = "Invoice No";
    private static final String INVOICE_DATE  = "Invoice Date";
    private static final String SALESRETURN_NO  = "Sales Return No";
    private static final String SALESRETURN_DATE  = "Sales Return Date";
    private static final String TRANSFER_NO  = "Transfer No";
    private static final String TRANSFER_DATE  = "Transfer Date";
    private static final String CUSTOMER_CODE  = "Customer Code";
    private static final String CUSTOMER_NAME  = "Customer Name";
    private static final String ADDRESS  = "Address";
    private static final String ADDRESS2  = "Address2";
    private static final String ADDRESS3  = "Address3";

    private static final String HEAD_PHONE  = "Head Phone";
    private static final String EMAIL  = "Email";

    private static final String TERM  = "Term";
    private static final String PHONE_NO  = "Phone No";
    private static final String ITEM_DISCOUNT  = "Item Discount";
    private static final String BILL_DISCOUNT  = "Bill Discount";
    private static final String SUB_TOTAL  = "SubTotal";
    private static final String TAX  = "Tax";
    private static final String NET_TOTAL  = "NetTotal";
    private static final String TOTAL_OUTSTANDING  = "Total Outstanding";

    private static final String TAG = Constants.TAG + "Bluetooth";
    public static final String DEVICE_NAME = "device_name";
    private static final boolean D = true;
    public static final String TOAST = "toast";
    private static final int REQUEST_ENABLE_BT = 3;
    private OnCompletedListener listener;
    private InitCompletionListener initListener;
    private boolean isConnected = false;
    private ProtocolAdapter.Channel mPrinterChannel;
    private BluetoothAdapter btAdapter;
    private Printer mPrinter;
    private ProtocolAdapter mProtocolAdapter;
    private BluetoothSocket mBtSocket;
    private EMSR mEMSR;
    private RC663 mRC663;
    private ProtocolAdapter.Channel mUniversalChannel;
    // Request to get the bluetooth device
    private static final int DEFAULT_NETWORK_PORT = 9100;
    private PrinterServer mPrinterServer;
    private Socket mNetSocket;
    private ZebraPrinter printer;
    public static  int HEADER_SPACE = 0;
    public static  int  SALES_RETURN_WIDTH = 0;
    public static  int  SALES_DATE_WIDTH = 0;
    public static  int  AMOUNT_WIDTH = 0;
    public static  int  CONCATENATE_LENGTH  = 0;
    private static final String FILE_NAME = "TEMP.LBL";
    public interface InitCompletionListener {
        public void initCompleted();
    }
    public void setInitCompletionListener(InitCompletionListener initListener) {
        this.initListener = initListener;
    }

    // @Offline
    boolean checkOffline;
    String onlineMode,valid_url,dialogStatus,serverdateTime="",printertype="" ;

    public CubePrintZPL(){

    }
    public CubePrintZPL(Context context, String macAddress){
        if (!macAddress
                .matches("[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
            throw new IllegalArgumentException(macAddress
                    + context.getString(R.string.is_not_valid_mac_address));
        }
        this.context = context;
        this.macAddress = macAddress;
        helper = new UIHelper(context);
        FWMSSettingsDatabase.init(context);
        OfflineDatabase.init(context);
        valid_url = FWMSSettingsDatabase.getUrl();
        onlineMode = OfflineDatabase.getOnlineMode();
        checkOffline = OfflineCommon.isConnected(context);
        new DateWebservice(valid_url,context);
        serverdateTime = getServerDateTime();
//        printertype = FWMSSettingsDatabase.getPrinterTypeStr();
        initPrinterWidth();

    }
    public void printZPL(){
        initPrinterWidth();
         btAdapter = BluetoothAdapter.getDefaultAdapter();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(R.string.title_please_wait));
        dialog.setMessage(context.getString(R.string.connecting_to_printer));
        dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_spinner));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Looper.prepare();
                Connection connection = null;
                connection = new BluetoothConnection(macAddress);
                Looper.prepare();
                try {
                    connection.open();
                    printer = ZebraPrinterFactory.getInstance(connection);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            helper.updateProgressDialog(R.string.printing_in_progress);
                        }
                    });
                    File filepath = context.getFileStreamPath(FILE_NAME);
                    printer.sendFileContents(filepath.getAbsolutePath());
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    dialog.dismiss();
                    if (initListener != null) {
                        initListener.initCompleted();
                    }
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }

    public void initPrinterWidth(){

        String malaysiaShowGST  = SalesOrderSetGet.getMalaysiaShowGST();

        if(malaysiaShowGST!=null && !malaysiaShowGST.isEmpty()){

        }else{
            malaysiaShowGST="";
        }

        printertype = "4 Inch Bluetooth";
//        printertype = "3 Inch Bluetooth Generic";

        if(printertype.matches("4 Inch Bluetooth")) {
            PAPER_WIDTH = 80;
            SLNO_WIDTH = 4;
            if (malaysiaShowGST.matches("1")) {
                DESCRIPTION_WIDTH = 42;
            }else{
                DESCRIPTION_WIDTH = 52;
            }
            EXPENSE_DESCRIPTION_WIDTH = 59;
            QTY_WIDTH = 8;
            PRICE_WIDTH = 8;
            TAX_WIDTH = 8;
            TOTAL_WIDTH = 8;
            LEFT_SPACE = 28;
            RIGHT_SPACE = 25;
            TAX_TYPE_WIDTH = 2;
            LINE_SEPARATOR = "\r\n";
            TOTAL_SPACE = 54;
            BETWEEN_SPACE = 36;
            NUM_WIDTH = 5;
            INVOICENO = 15;
            INVOICEDATE = 15;
            CREDIT_AMOUNT = 15;
            PAID_AMOUNT = 15;
            NETTOTAL = 15;

            GST_SUMMARY_LABEL = 31;
            GST_AMOUNT_LABEL = 15;
            GST_LABEL = 13;

            GST_SUMMARY_VALUE = 24;
            GST_AMOUNT_VALUE = 22;
            GST_VALUE = 13;

            PRODUCT_NAME_WIDTH = 45;
            CARTON_WIDTH = 10;
            LOOSE_WIDTH = 10;
            QUANTITY_WIDTH = 10;

            INVOICE_NO_WIDTH = 25;
            CUSTOMER_NAME_WIDTH = 25;
                    TOT_WIDTH = 25;

            SALES_RETURN_WIDTH = 26;
            SALES_DATE_WIDTH = 27;
            AMOUNT_WIDTH = 27;
            CONCATENATE_LENGTH = 27;
            HEADER_SPACE  = 25;

        }else if(printertype.matches("3 Inch Bluetooth Generic")) {
            PAPER_WIDTH = 46;
            SLNO_WIDTH = 4;
            if (malaysiaShowGST.matches("1")) {
                DESCRIPTION_WIDTH = 9;
            }else{
                DESCRIPTION_WIDTH = 20;
            }
            EXPENSE_DESCRIPTION_WIDTH = 35;
            QTY_WIDTH = 8;
            PRICE_WIDTH = 8;
            TAX_WIDTH = 8;
            TOTAL_WIDTH = 7;
            LEFT_SPACE = 1;
            RIGHT_SPACE = 25;
            TOTAL_SPACE = 20;
            TAX_TYPE_WIDTH = 2;
            BETWEEN_SPACE = 3;
//            LINE_SEPARATOR = "{br}";
            LINE_SEPARATOR = "\r\n";

            NUM_WIDTH = 3;
            INVOICENO = 11;
            INVOICEDATE = 11;
            CREDIT_AMOUNT = 7;
            PAID_AMOUNT = 7;
            NETTOTAL = 7;

            GST_SUMMARY_LABEL = 14;
            GST_AMOUNT_LABEL = 15;
            GST_LABEL = 13;

            GST_SUMMARY_VALUE = 10;
            GST_AMOUNT_VALUE = 19;
            GST_VALUE = 13;

            PRODUCT_NAME_WIDTH = 22;
            CARTON_WIDTH = 7;
            LOOSE_WIDTH = 7;
            QTY_WIDTH = 7;

            INVOICE_NO_WIDTH = 15;
            CUSTOMER_NAME_WIDTH =20;
            TOT_WIDTH =7;

            SALES_RETURN_WIDTH = 15;
            SALES_DATE_WIDTH = 15;
            AMOUNT_WIDTH = 16;
            HEADER_SPACE  = 15;
            CONCATENATE_LENGTH = 17;

        }
    }
    public void initGenericPrinter() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isBluetoothEnabled()) {
           print();
          //  initConnection();
        }else {
            context.registerReceiver(bluetoothReceiver, new IntentFilter(
                    BluetoothAdapter.ACTION_STATE_CHANGED));
            enableBluetooth();
        }
    }
    public void print(){
      //  btAdapter = BluetoothAdapter.getDefaultAdapter();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(R.string.title_please_wait));
        dialog.setMessage(context.getString(R.string.connecting_to_printer));
        dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_spinner));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    BluetoothDevice btDevice = btAdapter.getRemoteDevice(macAddress);
                    BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                    mBtSocket = btSocket;
               /* InputStream in = mBtSocket.getInputStream();
                OutputStream out = mBtSocket.getOutputStream();*/
                    Printer.setDebug(true);
                    EMSR.setDebug(true);
                    mProtocolAdapter = new ProtocolAdapter(btSocket.getInputStream(), btSocket.getOutputStream());
                    if (mProtocolAdapter.isProtocolEnabled()) {
                      //  Log.d(LOG_TAG, "Protocol mode is enabled");
                        // Get printer instance
                        mPrinterChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
                        mPrinter = new Printer(mPrinterChannel.getInputStream(), mPrinterChannel.getOutputStream());

                        // Check if printer has encrypted magnetic head
                        ProtocolAdapter.Channel emsrChannel = mProtocolAdapter
                                .getChannel(ProtocolAdapter.CHANNEL_EMSR);
                        try {
                            // Close channel silently if it is already opened.
                            try {
                                emsrChannel.close();
                            } catch (IOException e) {
                            }

                            // Try to open EMSR channel. If method failed, then probably EMSR is not supported
                            // on this device.
                            emsrChannel.open();

                            mEMSR = new EMSR(emsrChannel.getInputStream(), emsrChannel.getOutputStream());
                            EMSR.EMSRKeyInformation keyInfo = mEMSR.getKeyInformation(EMSR.KEY_AES_DATA_ENCRYPTION);
                            if (!keyInfo.tampered && keyInfo.version == 0) {
                              //  Log.d(LOG_TAG, "Missing encryption key");
                                // If key version is zero we can load a new key in plain mode.
                                byte[] keyData = CryptographyHelper.createKeyExchangeBlock(0xFF,
                                        EMSR.KEY_AES_DATA_ENCRYPTION, 1, CryptographyHelper.AES_DATA_KEY_BYTES,
                                        null);
                                mEMSR.loadKey(keyData);
                            }
                            mEMSR.setEncryptionType(EMSR.ENCRYPTION_TYPE_AES256);
                            mEMSR.enable();
                          // Log.d(LOG_TAG, "Encrypted magnetic stripe reader is available");
                        } catch (IOException e) {
                            if (mEMSR != null) {
                                mEMSR.close();
                                mEMSR = null;
                            }
                        }

                        // Check if printer has encrypted magnetic head
                        ProtocolAdapter.Channel rfidChannel = mProtocolAdapter
                                .getChannel(ProtocolAdapter.CHANNEL_RFID);

                        try {
                            // Close channel silently if it is already opened.
                            try {
                                rfidChannel.close();
                            } catch (IOException e) {
                            }

                            // Try to open RFID channel. If method failed, then probably RFID is not supported
                            // on this device.
                            rfidChannel.open();

                            mRC663 = new RC663(rfidChannel.getInputStream(), rfidChannel.getOutputStream());
                            mRC663.setCardListener(new RC663.CardListener() {
                                @Override
                                public void onCardDetect(ContactlessCard card) {
                                    // processContactlessCard(card);
                                }
                            });
                            mRC663.enable();
                          //  Log.d(LOG_TAG, "RC663 reader is available");
                        } catch (IOException e) {
                            if (mRC663 != null) {
                                mRC663.close();
                                mRC663 = null;
                            }
                        }

                        // Check if printer has encrypted magnetic head
                        mUniversalChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_UNIVERSAL_READER);
                        new UniversalReader(mUniversalChannel.getInputStream(), mUniversalChannel.getOutputStream());

                    } else {
                     //   Log.d(LOG_TAG, "Protocol mode is disabled");

                        // Protocol mode it not enables, so we should use the row streams.
                        mPrinter = new Printer(mProtocolAdapter.getRawInputStream(),
                                mProtocolAdapter.getRawOutputStream());
                    }
                    //  mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
                    mPrinter.setConnectionListener(new Printer.ConnectionListener() {
                        @Override
                        public void onDisconnect() {
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    dialog.dismiss();
                    if (initListener != null) {
                        initListener.initCompleted();
                    }
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
        /*final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(R.string.title_please_wait));
        dialog.setMessage(context.getString(R.string.connecting_to_printer));
        dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_spinner));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    BluetoothDevice btDevice = btAdapter.getRemoteDevice(macAddress);
                    BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                    mBtSocket = btSocket;
                    InputStream in = mBtSocket.getInputStream();
                    OutputStream out = mBtSocket.getOutputStream();
                    mProtocolAdapter = new ProtocolAdapter(in, out);
                    mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    dialog.dismiss();
                    if (initListener != null) {
                        initListener.initCompleted();
                    }
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();*/
    }
    private  void closePrinterConnection() {
        if (mRC663 != null) {
            try {
                mRC663.disable();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mRC663.close();
        }

        if (mEMSR != null) {
            mEMSR.close();
        }

        if (mPrinter != null) {
            mPrinter.close();
        }

        if (mProtocolAdapter != null) {
            mProtocolAdapter.close();
        }
    }
    private void closeBluetoothConnection() {
        // Close Bluetooth connection
        BluetoothSocket s = mBtSocket;
        mBtSocket = null;
        if (s != null) {
          //  Log.d(LOG_TAG, "Close Bluetooth socket");
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        context.unregisterReceiver(bluetoothReceiver);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        print();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };
    private boolean isBluetoothEnabled() {
        if (btAdapter != null) {
            return btAdapter.isEnabled();
        }
        return false;
    }

    private boolean enableBluetooth() {
        if (btAdapter == null) {
            helper.showErrorDialog(R.string.no_bluetooth_support);
            return false;
        } else if (!btAdapter.isEnabled()) {
            return btAdapter.enable();
        }
        return false;
    }
    public void setOnCompletedListener(OnCompletedListener listener) {
        this.listener = listener;
    }

  public void printReceipt(String customercode, String customername,
                           String receiptno, String receiptdate,
                           List<ProductDetails> receipts, List<String> sort, String gnrlStngs,
                           int nofcopies, boolean isSingleCustomer,
                           List<ProductDetails> footerValue,int invoiceLength,ArrayList<HashMap<String, String>> salesReturnArr){
      try {
          String totalOutStanding = "",configLabel = "";
          String start ="^XA^POI^A0N,25,25^FD";
          String end = "^FS^XZ";

//            String start ="^POI^A0N,25,25^FD";
//            String end = "^FS";

          String chinesestart = "^XA^POI^LL150^CI28^A@N,25,25,E:MSUNG.TTF^FO0,0^FH^FD";

          String showCustomerCode = MobileSettingsSetterGetter
                  .getShowCustomerCode();
          String showCustomerName = MobileSettingsSetterGetter
                  .getShowCustomerName();
          String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
          String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
          String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
          String showCustomerPhone = MobileSettingsSetterGetter
                  .getShowCustomerPhone();
          String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
          String showCustomerEmail = MobileSettingsSetterGetter
                  .getShowCustomerEmail();
          String showCustomerTerms = MobileSettingsSetterGetter
                  .getShowCustomerTerms();
          String showTotalOutstanding = MobileSettingsSetterGetter
                  .getShowTotalOutstanding();
          String showProductFullName = MobileSettingsSetterGetter
                  .getShowProductFullName();
          String showFooter = MobileSettingsSetterGetter.getShowFooter();
          String address1 = CustomerSetterGetter.getCustomerAddress1();
          String address2 = CustomerSetterGetter.getCustomerAddress2();
          String address3 = CustomerSetterGetter.getCustomerAddress3();
          String taxType = SalesOrderSetGet.getCompanytax();

          String user = SupplierSetterGetter.getUsername();
//          String serverdateTime = SalesOrderSetGet.getServerDateTime();
          String localCurrency = SalesOrderSetGet.getLocalCurrency();

          String malaysiaShowGST  = SalesOrderSetGet.getMalaysiaShowGST();

          if(malaysiaShowGST!=null && !malaysiaShowGST.isEmpty()){

          }else{
              malaysiaShowGST="";
          }

          String companyTaxValue = SalesOrderSetGet.getCompanytaxvalue();
          if(companyTaxValue!=null && !companyTaxValue.isEmpty()){
              companyTaxValue = companyTaxValue.split("\\.")[0];
          }
          if(printertype.matches("4 Inch Bluetooth")) {
              byte[] send = new byte[3];
              //Title
              send[0] = 0x1b;
              send[1] = 0x61;
              send[2] = 0;
              GlobalData.mService.write(send);
          }

          for (int n = 0; n < nofcopies; n++) {


              double taxZ = 0.00, taxS = 0.00, subTotalZ = 0.00, subTotalS = 0.00,dNetTotal = 0.00,dCreditAmt=0.00,dPaidAmt=0.00;
              int no = 1;

              configLabel += start + subAlignCenter("RECEIPT") + end;
              configLabel += LINE_SEPARATOR;
              configLabel += LINE_SEPARATOR;

              configLabel += printCompanyDetails();

//              configLabel += LINE_SEPARATOR;
              configLabel += start +horizontalLine("-") + end;
              configLabel += LINE_SEPARATOR;
              configLabel += start + subAlignDataLeft(HEADER_SPACE, "Receipt No");
              configLabel += COLON + " " + receiptno + end ;
              configLabel += LINE_SEPARATOR;
              configLabel += start +subAlignDataLeft(HEADER_SPACE, "Receipt Date");
              configLabel += COLON + " " + receiptdate + end;
              configLabel += LINE_SEPARATOR;
              if (showCustomerCode.matches("True")) {

                  if (!customercode.matches("")) {
                      configLabel += start +subAlignDataLeft(HEADER_SPACE, CUSTOMER_CODE);
                      configLabel += COLON + " " + customercode + end;
                      configLabel += LINE_SEPARATOR;
                  }
              }
              if (showCustomerName.matches("True")) {

                  if (!customername.matches("")) {
                      configLabel += chinesestart + subAlignDataLeft(HEADER_SPACE, CUSTOMER_NAME);
                    //  configLabel += COLON + " " + customername;
                      if(printertype.matches("3 Inch Bluetooth Generic")){
                          if(customername.length()>29){
                              String data = concatenateStr(customername);
                              configLabel += COLON + " " + data;
                          }else{
                              configLabel += COLON + " " + customername;
                          }

                      }else{
                          configLabel += COLON + " " + customername;
                      }

                      configLabel += end;
                      configLabel += LINE_SEPARATOR;

                  }
              }
              if (showAddress1.matches("True")) {
                  if (!address1.matches("")) {
                      configLabel += chinesestart + subAlignDataLeft(HEADER_SPACE, ADDRESS);
                      //configLabel += COLON + " " + address1;
                      if(printertype.matches("3 Inch Bluetooth Generic")){
                          if(customername.length()>29){
                              String data = concatenateStr(address1);
                              configLabel += COLON + " " + data;
                          }else{
                              configLabel += COLON + " " + address1;
                          }

                      }else{
                          configLabel += COLON + " " + address1;
                      }
                      configLabel += end;
                      configLabel += LINE_SEPARATOR;
                  }
              }

              if (showAddress2.matches("True")) {
                  if (!address2.matches("")) {

                      configLabel += chinesestart + subAlignDataLeft(HEADER_SPACE, "        ");
                     // configLabel += " " + " " + address2;
                      if(printertype.matches("3 Inch Bluetooth Generic")){
                          if(customername.length()>29){
                              String data = concatenateStr(address2);
                              configLabel += " " + " " + data;
                          }else{
                              configLabel += " " + " " + address2;
                          }

                      }else{
                          configLabel += " " + " " + address2;
                      }
                      configLabel += end ;
                      configLabel += LINE_SEPARATOR;

                  }
              }

              if (showAddress3.matches("True")) {
                  if (!address3.matches("")) {
                      configLabel += chinesestart + subAlignDataLeft(HEADER_SPACE, "        ");
                     // configLabel += " " + " " + address3;
                      if(printertype.matches("3 Inch Bluetooth Generic")){
                          if(customername.length()>29){
                              String data = concatenateStr(address3);
                              configLabel += " " + " " + data;
                          }else{
                              configLabel += " " + " " + address3;
                          }

                      }else{
                          configLabel += " " + " " + address3;
                      }

                      configLabel += end;
                      configLabel += LINE_SEPARATOR;
                  }
              }
              if (showCustomerPhone.matches("True")) {
                  if (!CustomerSetterGetter.getCustomerPhone().matches("")) {

                      configLabel += start + subAlignDataLeft(HEADER_SPACE, PHONE_NO);
                      configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone() + end;
                      configLabel += LINE_SEPARATOR;
                  }
              }

              if (showCustomerHP.matches("True")) {
                  if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                      configLabel += start +subAlignDataLeft(HEADER_SPACE, HEAD_PHONE);
                      configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP() + end;
                      configLabel += LINE_SEPARATOR;

                  }
              }

              if (showCustomerEmail.matches("True")) {
                  if (!CustomerSetterGetter.getCustomerEmail().matches("")) {

                      configLabel += start + subAlignDataLeft(HEADER_SPACE, EMAIL);
                      //configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                      if(printertype.matches("3 Inch Bluetooth Generic")){
                          if(customername.length()>29){
                              String data = concatenateStr(CustomerSetterGetter.getCustomerEmail());
                              configLabel += COLON + " " + data;
                          }else{
                              configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                          }

                      }else{
                          configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                      }

                      configLabel += end;
                      configLabel += LINE_SEPARATOR;
                  }
              }

              if (showCustomerTerms.matches("True")) {
                  if (!CustomerSetterGetter.getCustomerTerms().matches("")) {

                      configLabel += start +subAlignDataLeft(HEADER_SPACE, TERM);
                      configLabel += COLON + " " + CustomerSetterGetter.getCustomerTerms() +end;
                      configLabel += LINE_SEPARATOR;

                  }
              }

              //  configLabel += LINE_SEPARATOR;
              configLabel += start + horizontalLine("-") + end;

              if(invoiceLength>1){
                  configLabel += LINE_SEPARATOR;

                  if(printertype.matches("3 Inch Bluetooth Generic")){
                      configLabel += start + subAlignData(NUM_WIDTH,"No");
                      configLabel += subAlignData(INVOICENO,"InvoiceNo");
                      configLabel += subAlignData(INVOICEDATE,"InvoiceDate");
                      configLabel += subAlignRightData(CREDIT_AMOUNT,"Credit");
                      configLabel += subAlignRightData(PAID_AMOUNT,"Paid");
                      configLabel += subAlignRightData(NETTOTAL,"NetTtl");

                  }else if(printertype.matches("4 Inch Bluetooth")){
                      configLabel += start + subAlignData(NUM_WIDTH,"No");
                      configLabel += subAlignData(INVOICENO,"Invoice No");
                      configLabel += subAlignData(INVOICEDATE,"Invoice Date");
                      configLabel += subAlignRightData(CREDIT_AMOUNT,"Credit Amount");
                      configLabel += subAlignRightData(PAID_AMOUNT,"Paid Amount");
                      configLabel += subAlignRightData(NETTOTAL,"NetTotal");
                  }

                  configLabel += end;
                  configLabel += LINE_SEPARATOR;
                  configLabel += start +horizontalLine("-") +end;
                  configLabel += LINE_SEPARATOR;
              }


              for (ProductDetails receipt : receipts) {
                  if(invoiceLength == 1){
                      configLabel += LINE_SEPARATOR;

                      configLabel += start + subAlignDataLeft(HEADER_SPACE, "Invoice No");
                      configLabel += COLON + " " + receipt.getItemno() + end;
                      configLabel += LINE_SEPARATOR;

                      configLabel += start +subAlignDataLeft(HEADER_SPACE, "Invoice Date");
                      configLabel += COLON + " " + receipt.getItemdate() + end;
                      configLabel += LINE_SEPARATOR;

                      configLabel += start +horizontalLine("-") +end;
                      configLabel += LINE_SEPARATOR;


                      configLabel += start +subAlignData(SLNO_WIDTH, "No");
                      configLabel += subAlignData(DESCRIPTION_WIDTH, "Description");
                      configLabel += subAlignRightData(QTY_WIDTH, "Qty");
                      configLabel += subAlignRightData(PRICE_WIDTH, "Price");
                      if (malaysiaShowGST.matches("1")) {
                          configLabel += subAlignRightData(PRICE_WIDTH, "GST(" + companyTaxValue + "%)");
                      }
                      configLabel += subAlignRightData(TOTAL_WIDTH, "Total");
                      configLabel += subAlignRightData(TAX_TYPE_WIDTH, "  ");

                      configLabel += end;
                      configLabel += LINE_SEPARATOR;
                      configLabel += horizontalLine("-");
                      configLabel += LINE_SEPARATOR;


                      for (ProdDetails product : receipt.getProductsDetails()) {
                          String invoicenum = product.getItemnum().toString();
                          if (invoicenum.matches(receipt.getItemno().toString())) {
                              configLabel += chinesestart +subAlignData(SLNO_WIDTH, product.getSno().toString());
                              //  configLabel += subAlignData(DESCRIPTION_WIDTH1, (product.getDescription().length() > 31) ? product.getDescription().substring(0, 30) : product.getDescription());
                              configLabel += subAlignData(DESCRIPTION_WIDTH, (product.getDescription().length() > 40) ? product.getDescription().substring(0, 39) : product.getDescription());
                              // print next line if it is 3 Inch Bluetooth Generic printer
                              if(printertype.matches("3 Inch Bluetooth Generic")){
                                  if(product.getDescription().length()>DESCRIPTION_WIDTH){
                                      configLabel += LINE_SEPARATOR;
                                      if (malaysiaShowGST.matches("1")) {
                                          configLabel += subAlignRightData(24,"");
                                      }else{
                                          configLabel += subAlignRightData(15,"");
                                      }

                                  }
                              }
                              configLabel += subAlignRightData(QTY_WIDTH, "" + product.getQty());
                              configLabel += subAlignRightData(PRICE_WIDTH, "" + product.getPrice());
                              // configLabel += subAlignRightData(TOTAL_WIDTH, "" + product.getTotal());

                              if (malaysiaShowGST.matches("1")) {
                                  String taxStr = product.getTax();
                                  if (taxStr != null && !taxStr.isEmpty()) {
                                      double dTax = Double.valueOf(taxStr);
                                      configLabel += subAlignRightData(TAX_WIDTH, twoDecimalPoint(Double.valueOf(dTax)));
                                  }
                              }
                              //  double subTotal = 0.00;
                              if (product.getTax() != null && !product.getTax().isEmpty()) {
                                  double tax = Double.valueOf(product.getTax());

                                  if (tax == 0) {
                                      taxZ += tax;
                                      if (product.getSubtotal() != null && !product.getSubtotal().isEmpty()) {
                                          subTotalZ += Double.valueOf(product.getSubtotal());
                                      }
                                      System.out.println("subTotalZ - >" + subTotalZ);

                                      configLabel += subAlignRightData(TOTAL_WIDTH, "" + product.getTotal());
                                      if (malaysiaShowGST.matches("1")) {
                                          configLabel += subAlignRightData(TAX_TYPE_WIDTH, " Z");
                                      }

                                  } else {
                                      taxS += tax;
                                      //   subTotalS += subTotal;
                                      if (product.getSubtotal() != null && !product.getSubtotal().isEmpty()) {
                                          subTotalS += Double.valueOf(product.getSubtotal());
                                      }

                                      System.out.println("subTotalS - >" + subTotalS);

                                      configLabel += subAlignRightData(TOTAL_WIDTH, "" + product.getTotal());
                                      if (malaysiaShowGST.matches("1")) {
                                          configLabel += subAlignRightData(TAX_TYPE_WIDTH, " S");
                                      }
                                  }
                              }
                              if(product.getTaxType()!=null && !product.getTaxType().isEmpty()){
                                  taxType = product.getTaxType();
                              }

                              configLabel += end;
                              configLabel += LINE_SEPARATOR;
                          }
                      }

                      configLabel += start +horizontalLine("-") + end;
                      configLabel += LINE_SEPARATOR;

                      if (Double.parseDouble(receipt.getItemdisc().toString()) > 0) {
                          configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Item Discount");
                          configLabel += subAlignDataRight(1, COLON);
                          configLabel += subAlignRightData(TOTAL_SPACE, " " + receipt.getItemdisc().toString()) + end;
                          configLabel += LINE_SEPARATOR;
                          // configLabel += subAlignDataLeftSpace(9,COLON+" "+prd.getItemdisc().toString());

                      }
                      int currencyLen = localCurrency.length() - 2;

                  //    configLabel += subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amt Excel.GST(" + localCurrency + ") ");
                      Log.d("Receipt tax type",""+taxType);
                      if(taxType.equalsIgnoreCase("E")){
                          configLabel += start +subAlignDataLeft(RIGHT_SPACE-currencyLen, "Total Amt Excel.GST("+localCurrency+") ");
                      }else if(taxType.equalsIgnoreCase("I")){
                          configLabel += start +subAlignDataLeft(RIGHT_SPACE-currencyLen, "Total Amt Incel.GST("+localCurrency+") ");                    }
                      else{
                          configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Total Amt ("+localCurrency+") ");
                      }

                      configLabel += subAlignDataRight(1, COLON);

                      configLabel += subAlignRightData(TOTAL_SPACE, " " + receipt.getSubtotal().toString());

                      configLabel += end;

                      configLabel += LINE_SEPARATOR;
                      if (Double.parseDouble(receipt.getBilldisc().toString()) > 0) {

                          configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Bill Discount");
                          configLabel += subAlignDataRight(1, COLON);
                          configLabel += subAlignRightData(TOTAL_SPACE, " " + receipt.getBilldisc().toString()) + end;
                          configLabel += LINE_SEPARATOR;

                      }

                      configLabel += start +subAlignDataLeft(RIGHT_SPACE, "(+) GST @ " + companyTaxValue + "% (" + localCurrency + ") ");
                      configLabel += subAlignDataRight(1, COLON);
                      configLabel += subAlignRightData(TOTAL_SPACE, " " + receipt.getTax().toString()) +end;


                      configLabel += LINE_SEPARATOR;
                      configLabel += start +horizontalLine("-")+end;
                      configLabel += LINE_SEPARATOR;


                      configLabel += start +subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount Payable(" + localCurrency + ")");
                      configLabel += subAlignDataRight(1, COLON);
                      configLabel += subAlignRightData(TOTAL_SPACE, " " + receipt.getNettot().toString())+end;
                      configLabel += LINE_SEPARATOR;


                      String creditamount = receipt.getCreditAmount();
                      if (creditamount != null && !creditamount.isEmpty()) {
                          double creditVal = Double.valueOf(receipt.getCreditAmount());
                          if (creditVal > 0) {
                              configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Credit Amount");
                              configLabel += subAlignDataRight(1, COLON);
                              configLabel += subAlignRightData(TOTAL_SPACE, "" + creditVal)+end;
                              configLabel += LINE_SEPARATOR;
                          }

                      }


                      if (!In_Cash.getPay_Mode().matches("")
                              || !In_Cash.getPay_Mode().matches("null")
                              || !In_Cash.getPay_Mode().matches(null)) {

                          String pay_Mode = In_Cash.getPay_Mode();
                          configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Pay Mode");
                          configLabel += subAlignDataRight(1, COLON);
                          configLabel += subAlignRightData(TOTAL_SPACE, " " + pay_Mode)+end;
                          configLabel += LINE_SEPARATOR;

                          Log.d("paymode.toLowerCase()",pay_Mode.toLowerCase());
                          if (pay_Mode.equalsIgnoreCase("cheque")) {

                              String bank_Name = In_Cash.getBank_code();
                              String cheque_No = In_Cash.getCheck_No();
                              String cheque_Date = In_Cash.getCheck_Date();

                              configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Bank Name");
                              configLabel += subAlignDataRight(1, COLON);
                              configLabel += subAlignRightData(TOTAL_SPACE, " " + bank_Name) +end;
                              configLabel += LINE_SEPARATOR;

                              configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Cheque No");
                              configLabel += subAlignDataRight(1, COLON);
                              configLabel += subAlignRightData(TOTAL_SPACE, " " + cheque_No) +end;
                              configLabel += LINE_SEPARATOR;

                              configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Cheque Date");
                              configLabel += subAlignDataRight(1, COLON);
                              configLabel += subAlignRightData(TOTAL_SPACE, " " + cheque_Date) + end;
                              configLabel += LINE_SEPARATOR;
                          }
                      }


                      configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Paid Amount (" + localCurrency + ") ");
                      configLabel += subAlignDataRight(1, COLON);
                      configLabel += subAlignRightData(TOTAL_SPACE, " " + receipt.getPaidamount().toString()) +end;

                      configLabel += LINE_SEPARATOR;
                      configLabel += start +horizontalLine("-") +end;
                      configLabel += LINE_SEPARATOR;
                      configLabel += LINE_SEPARATOR;

                          if (malaysiaShowGST.matches("1")) {

                              if (localCurrency.equals("RM")) {
                                  configLabel += start +horizontalLineSpace("-") + end;
                                  configLabel += LINE_SEPARATOR;
                                  configLabel += start +subAlignDataRight(GST_SUMMARY_LABEL, "GST Summary");
                                  configLabel += subAlignDataRight(GST_AMOUNT_LABEL, "Amount");
                                  configLabel += subAlignDataRight(GST_LABEL, "GST")+end;
                                  configLabel += LINE_SEPARATOR;
                                  configLabel +=start + horizontalLineSpace("-")+end;
                                  configLabel += LINE_SEPARATOR;
                                  configLabel += start +subAlignDataRight(GST_SUMMARY_VALUE, "Z=0%");
                                  configLabel += subAlignDataRight(GST_AMOUNT_VALUE, "" + twoDecimalPoint(subTotalZ));
                                  configLabel += subAlignDataRight(GST_VALUE, "" + twoDecimalPoint(taxZ))+end;
                                  configLabel += LINE_SEPARATOR;
                                  configLabel += start +subAlignDataRight(GST_SUMMARY_VALUE, "S=" + companyTaxValue + "%");
                                  configLabel += subAlignDataRight(GST_AMOUNT_VALUE, "" + twoDecimalPoint(subTotalS));
                                  configLabel += subAlignDataRight(GST_VALUE, "" + twoDecimalPoint(taxS))+end;
                                  configLabel += LINE_SEPARATOR;
                                  configLabel += start +horizontalLineSpace("-")+end;
                              }
                          }

                  }else{

                      configLabel += start +subAlignData(NUM_WIDTH,""+ no);
                      configLabel += subAlignData(INVOICENO,receipt.getItemno());
                      configLabel += subAlignData(INVOICEDATE,receipt.getItemdate());
                      configLabel += subAlignRightData(CREDIT_AMOUNT,receipt.getCreditAmount());
                      configLabel += subAlignRightData(PAID_AMOUNT,receipt.getPaidamount());
                      configLabel += subAlignRightData(NETTOTAL,""+receipt.getNettot())+end;
                      configLabel += LINE_SEPARATOR;

                      no++;
                      if(receipt.getNettot()!=null && !receipt.getNettot().isEmpty()){
                          dNetTotal += Double.valueOf(receipt.getNettot());
                      }
                      if(receipt.getPaidamount()!=null && !receipt.getPaidamount().isEmpty()){
                          dPaidAmt += Double.valueOf(receipt.getPaidamount());
                      }
                      if(receipt.getCreditAmount()!=null && !receipt.getCreditAmount().isEmpty()){
                          dCreditAmt += Double.valueOf(receipt.getCreditAmount());
                      }
                  }

              }
              if(invoiceLength>1) {
                  configLabel += horizontalLine("-");
                  configLabel += LINE_SEPARATOR;


                  if (!In_Cash.getPay_Mode().matches("")
                          || !In_Cash.getPay_Mode().matches("null")
                          || !In_Cash.getPay_Mode().matches(null)) {

                      String paymode = In_Cash.getPay_Mode();

                      configLabel += start + subAlignDataLeft(RIGHT_SPACE, "PayMode");
                      configLabel += subAlignDataRight(1, COLON);
                      configLabel += subAlignRightData(TOTAL_SPACE, " " + paymode) +end;
                      configLabel += LINE_SEPARATOR;

                        Log.d("paymode.toLowerCase()",paymode.toLowerCase());
                      if (paymode.equalsIgnoreCase("cheque")) {

                          String bank_Name = In_Cash.getBank_code();
                          String cheque_No = In_Cash.getCheck_No();
                          String cheque_Date = In_Cash.getCheck_Date();

                          configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Bank Name");
                          configLabel += subAlignDataRight(1, COLON);
                          configLabel += subAlignRightData(TOTAL_SPACE, " " + bank_Name)+end;
                          configLabel += LINE_SEPARATOR;

                          configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Cheque No");
                          configLabel += subAlignDataRight(1, COLON);
                          configLabel += subAlignRightData(TOTAL_SPACE, " " + cheque_No)+end;
                          configLabel += LINE_SEPARATOR;

                          configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Cheque Date");
                          configLabel += subAlignDataRight(1, COLON);
                          configLabel += subAlignRightData(TOTAL_SPACE, " " + cheque_Date)+end;
                          configLabel += LINE_SEPARATOR;
                      }
                  }

                  configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Total Credit Amount");
                  configLabel += subAlignDataRight(1, COLON);
                  configLabel += subAlignRightData(TOTAL_SPACE, " " + twoDecimalPoint(dCreditAmt))+end;
                  configLabel += LINE_SEPARATOR;

                  configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Total Paid Amount");
                  configLabel += subAlignDataRight(1, COLON);
                  configLabel += subAlignRightData(TOTAL_SPACE, " " + twoDecimalPoint(dPaidAmt))+end;
                  configLabel += LINE_SEPARATOR;

                  configLabel += start +subAlignDataLeft(RIGHT_SPACE, "NetTotal");
                  configLabel += subAlignDataRight(1, COLON);
                  configLabel += subAlignRightData(TOTAL_SPACE, " " + twoDecimalPoint(dNetTotal))+end;
                  configLabel += LINE_SEPARATOR;
                  configLabel += start +horizontalLine("-") + end;
                  configLabel += LINE_SEPARATOR;

              }

              if(salesReturnArr.size()>0)
              {

                  Log.d("salesReturnArr.size()" ,"<-->"+salesReturnArr.size());
                  double dTotal = 0.00;
                  configLabel += LINE_SEPARATOR;
                  configLabel += LINE_SEPARATOR;
                  configLabel += start +horizontalLine("-") +end;
                  configLabel += LINE_SEPARATOR;


                  if(printertype.matches("3 Inch Bluetooth Generic")){
                      configLabel += start +subAlignData(SALES_RETURN_WIDTH, "SR No");
                      configLabel += subAlignData(SALES_DATE_WIDTH, "SR Date");

                  }else if(printertype.matches("4 Inch Bluetooth")){
                      configLabel += start +subAlignData(SALES_RETURN_WIDTH, "Sales Return No");
                      configLabel += subAlignData(SALES_DATE_WIDTH, "Sales Return Date");
                  }

                  configLabel += subAlignDataRight(AMOUNT_WIDTH, "Amount");
                  configLabel +=end;
                  configLabel += LINE_SEPARATOR;
                  configLabel += start +horizontalLine("-")+end;
                  configLabel += LINE_SEPARATOR;

                  for(int i=0;i<salesReturnArr.size();i++){
                      String salesReturnNo =  salesReturnArr.get(i).get("SalesReturnNo");
                      String salesReturnDate =  salesReturnArr.get(i).get("SalesReturnDate");
                      String creditAmount =  salesReturnArr.get(i).get("CreditAmount");
                      configLabel += start +subAlignData(SALES_RETURN_WIDTH, salesReturnNo);
                      configLabel += subAlignData(SALES_DATE_WIDTH, salesReturnDate);
                      configLabel += subAlignDataRight(AMOUNT_WIDTH, creditAmount);

                      if(creditAmount!=null && !creditAmount.isEmpty()){
                          dTotal += Double.valueOf(creditAmount);
                      }
                      configLabel += end;
                      configLabel += LINE_SEPARATOR;
                  }
                  configLabel += LINE_SEPARATOR;
                  configLabel += start +horizontalLine("-") +end;
                  configLabel += LINE_SEPARATOR;
                  configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Total");
                  configLabel += subAlignDataRight(1, COLON);
                  configLabel += subAlignRightData(TOTAL_SPACE, " " + twoDecimalPoint(dTotal))+end;
                  configLabel += LINE_SEPARATOR;
                  configLabel += start +horizontalLine("-") +end;
                  configLabel += LINE_SEPARATOR;
                  configLabel += LINE_SEPARATOR;

              }

              configLabel += LINE_SEPARATOR;
              configLabel += LINE_SEPARATOR;
              configLabel += LINE_SEPARATOR;
              configLabel += start +horizontalLine("-")+end;
              configLabel += LINE_SEPARATOR;
              configLabel += start +subAlignDataBtSpace("Received By");
              configLabel += subAlignDataRight(BETWEEN_SPACE, " ");
              configLabel += subAlignDataBtSpace("Authorized By")+end;
              configLabel += LINE_SEPARATOR;
              configLabel += start +horizontalLine("-") +end;
              configLabel += LINE_SEPARATOR;
              configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
              configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime)+end;

              // print next line if it is 3 Inch Bluetooth Generic printer
              if(printertype.matches("3 Inch Bluetooth Generic")){
                  configLabel += LINE_SEPARATOR;
                  configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Issued By ");
                  configLabel += subAlignDataLeftSpace(17, COLON + " " + user)+end;

              }else if(printertype.matches("4 Inch Bluetooth")){
                  configLabel += subAlignDataRight(13, " ");
                  configLabel += start +subAlignDataRight(11, "Issued By : ");
                  configLabel += subAlignDataLeftSpace(14, " " + user)+end;
              }

             // configLabel += subAlignDataRight(13, " ");
             // configLabel += subAlignDataRight(11, "Issued By : ");
             // configLabel += subAlignDataLeftSpace(14, " " + user);
              configLabel += LINE_SEPARATOR;
              configLabel += start +horizontalLine("-") +end;
              configLabel += LINE_SEPARATOR;
              configLabel += start +subAlignCenter("Thank You")+end;
              configLabel += LINE_SEPARATOR;
              configLabel += LINE_SEPARATOR;
              configLabel += LINE_SEPARATOR;
              configLabel += LINE_SEPARATOR;
              configLabel += LINE_SEPARATOR;
              System.out.println(configLabel);
          }

          sendMessage(configLabel);

      }catch (Exception e){
          helper.showErrorDialog(e.getMessage());
          e.printStackTrace();
      }

  }


    // print invoice
    public void printInvoice(String invoiceno, String invoicedate,
                             String customercode, String customername,
                             List<ProductDetails> product, List<ProductDetails> productdet,
                             List<String> printsortHeader, String gnrlStngs, int nofcopies,
                             List<ProductDetails> product_batch, List<ProductDetails> footerValue){
        try {
            FileOutputStream os = context.openFileOutput(FILE_NAME,
                    Context.MODE_PRIVATE);

            String configLabel ="";

            String start ="^XA^POI^A0N,25,25^FD";
            String end = "^FS^XZ";

//            String start ="^POI^A0N,25,25^FD";
//            String end = "^FS";

            String chinesestart = "^XA^POI^LL150^CI28^A@N,25,25,E:MSUNG.TTF^FO0,0^FH^FD";

            String showCustomerCode = MobileSettingsSetterGetter
                    .getShowCustomerCode();
            String showCustomerName = MobileSettingsSetterGetter
                    .getShowCustomerName();
            String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
            String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
            String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
            String showCustomerPhone = MobileSettingsSetterGetter
                    .getShowCustomerPhone();
            String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
            String showCustomerEmail = MobileSettingsSetterGetter
                    .getShowCustomerEmail();
            String showCustomerTerms = MobileSettingsSetterGetter
                    .getShowCustomerTerms();
            String showTotalOutstanding = MobileSettingsSetterGetter
                    .getShowTotalOutstanding();
            String showProductFullName = MobileSettingsSetterGetter.getShowProductFullName();
            String showFooter = MobileSettingsSetterGetter.getShowFooter();
            String address1 = CustomerSetterGetter.getCustomerAddress1();
            String address2 = CustomerSetterGetter.getCustomerAddress2();
            String address3 = CustomerSetterGetter.getCustomerAddress3();

            String taxType = SalesOrderSetGet.getCompanytax();
            String user = SupplierSetterGetter.getUsername();
//            String serverdateTime = SalesOrderSetGet.getServerDateTime();
            String localCurrency = SalesOrderSetGet.getLocalCurrency();

            String malaysiaShowGST  = SalesOrderSetGet.getMalaysiaShowGST();

            if(malaysiaShowGST!=null && !malaysiaShowGST.isEmpty()){

            }else{
                malaysiaShowGST="";
            }


            String companyTaxValue = SalesOrderSetGet.getCompanytaxvalue();
            if(companyTaxValue!=null && !companyTaxValue.isEmpty()){
                companyTaxValue = companyTaxValue.split("\\.")[0];
            }
            Log.d("printer Type","-->"+printertype);
           /* if(printertype.matches("4 Inch Bluetooth")) {
                byte[] send=new byte[3];
                //Title
                send[0]=0x1b;
                send[1]=0x61;
                send[2]=0;
                GlobalData.mService.write(send);
            }*/

            //  GlobalData.mTTransmission.sendBytes(send);
            for (int n = 0; n < nofcopies; n++) {


                double taxZ = 0.00, taxS = 0.00, subTotalZ = 0.00, subTotalS = 0.00;

                configLabel += start + subAlignCenter("TAX INVOICE") + end;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;

                configLabel += printCompanyDetails();

                configLabel += start + horizontalLine("-") + end;
                configLabel += LINE_SEPARATOR;


                configLabel += start + subAlignDataLeft(HEADER_SPACE, INVOICE_NO);
                configLabel += COLON + " " + invoiceno + end;
                configLabel += LINE_SEPARATOR;
                configLabel += start + subAlignDataLeft(HEADER_SPACE, INVOICE_DATE);
                configLabel += COLON + " " + invoicedate+ end;
                configLabel += LINE_SEPARATOR;

                if (showCustomerCode.matches("True")) {

                    if (!customercode.matches("")) {
                        configLabel +=  start + subAlignDataLeft(HEADER_SPACE, CUSTOMER_CODE) ;
                        configLabel += COLON + " " + customercode + end;
                        configLabel += LINE_SEPARATOR;
                    }
                }
                if (showCustomerName.matches("True")) {

                    if (!customername.matches("")) {
                        configLabel += chinesestart + subAlignDataLeft(HEADER_SPACE, CUSTOMER_NAME);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (customername.length() > 29) {
                                String data = concatenateStr(customername);
                                configLabel += COLON + " " + data+ end;
                            } else {
                                configLabel += COLON + " " + customername+ end;
                            }

                        } else {
                            configLabel += COLON + " " + customername+ end;
                        }
                        configLabel += LINE_SEPARATOR;

                    }
                }
                if (showAddress1.matches("True")) {
                    if (!address1.matches("")) {
                        configLabel += chinesestart + subAlignDataLeft(HEADER_SPACE, ADDRESS);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address1.length() > 29) {
                                String data = concatenateStr(address1);
                                configLabel += COLON + " " + data+ end;
                            } else {
                                configLabel += COLON + " " + address1+ end;
                            }

                        } else {
                            configLabel += COLON + " " + address1+ end;
                        }
                        configLabel += LINE_SEPARATOR;
                    }
                }

                if (showAddress2.matches("True")) {
                    if (!address2.matches("")) {

                        configLabel += chinesestart + subAlignDataLeft(HEADER_SPACE, "        ");
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address2.length() > 29) {
                                String data = concatenateStr(address2);
                                configLabel += " " + " " + data+ end;
                            } else {
                                configLabel += " " + " " + address2+ end;
                            }

                        } else {
                            configLabel += " " + " " + address2+ end;
                        }
                        configLabel += LINE_SEPARATOR;

                    }
                }

                if (showAddress3.matches("True")) {
                    if (!address3.matches("")) {
                        configLabel += chinesestart + subAlignDataLeft(HEADER_SPACE, "        ");
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address3.length() > 29) {
                                String data = concatenateStr(address3);
                                configLabel += " " + " " + data+ end;
                            } else {
                                configLabel += " " + " " + address3+ end;
                            }

                        } else {
                            configLabel += " " + " " + address3+ end;
                        }
                        configLabel += LINE_SEPARATOR;
                    }
                }

                if (showCustomerPhone.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerPhone().matches("")) {

                        configLabel += start +subAlignDataLeft(HEADER_SPACE, PHONE_NO);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone()+ end;
                        configLabel += LINE_SEPARATOR;
                    }
                }

                if (showCustomerHP.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                        configLabel += start +subAlignDataLeft(HEADER_SPACE, HEAD_PHONE);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP()+ end;
                        configLabel += LINE_SEPARATOR;

                    }
                }

                if (showCustomerEmail.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
                        configLabel += start +subAlignDataLeft(HEADER_SPACE, EMAIL);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (CustomerSetterGetter.getCustomerEmail().length() > 29) {
                                String data = concatenateStr(CustomerSetterGetter.getCustomerEmail());
                                configLabel += COLON + " " + data+ end;
                            } else {
                                configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail()+ end;
                            }

                        } else {
                            configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail()+ end;
                        }
                        configLabel += LINE_SEPARATOR;
                    }
                }

                if (showCustomerTerms.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerTerms().matches("")) {

                        configLabel += start +subAlignDataLeft(HEADER_SPACE, TERM);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerTerms()+ end;
                        configLabel += LINE_SEPARATOR;

                    }
                }

                configLabel += start + horizontalLine("-")+ end;
                configLabel += LINE_SEPARATOR;
                configLabel += start + subAlignData(SLNO_WIDTH, "No");
                configLabel += subAlignData(DESCRIPTION_WIDTH, "Description");
                configLabel += subAlignRightData(QTY_WIDTH, "Qty");
                configLabel += subAlignRightData(PRICE_WIDTH, "Price");
                if (malaysiaShowGST.matches("1")) {
                    configLabel += subAlignRightData(TAX_WIDTH, "GST(" + companyTaxValue + "%)");
                }
                configLabel += subAlignRightData(TOTAL_WIDTH, "Total");
                configLabel += subAlignRightData(TAX_TYPE_WIDTH, "  ")+ end;
                configLabel += LINE_SEPARATOR;
                configLabel += start + horizontalLine("-")+ end;
                configLabel += LINE_SEPARATOR;


                int i = 1;
                for (ProductDetails prod : product) {
                    configLabel += chinesestart + subAlignData(SLNO_WIDTH, String.valueOf(i));
                    configLabel += subAlignData(DESCRIPTION_WIDTH, /*(prod.getDescription().length() > 40) ? prod.getDescription().substring(0, 39) :*/ prod.getDescription());
                    // print next line if it is 3 Inch Bluetooth Generic printer
                    if (printertype.matches("3 Inch Bluetooth Generic")) {
                        if (prod.getDescription().length() > DESCRIPTION_WIDTH) {
                            configLabel += LINE_SEPARATOR;
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(15,"");
                            }else{
                                configLabel += subAlignRightData(24,"");
                            }
                        }
                    }
                    configLabel += subAlignRightData(QTY_WIDTH, "" + prod.getQty());
                    configLabel += subAlignRightData(PRICE_WIDTH, "" + prod.getPrice());

                    if (malaysiaShowGST.matches("1")) {
                        String taxStr = prod.getTax();
                        if (taxStr != null && !taxStr.isEmpty()) {
                            double dTax = Double.valueOf(taxStr);
                            configLabel += subAlignRightData(TAX_WIDTH, twoDecimalPoint(Double.valueOf(dTax)));
                        }
                    }
                    //  double subTotal = 0.00;
                    if (prod.getTax() != null && !prod.getTax().isEmpty()) {
                        double tax = Double.valueOf(prod.getTax());

                        if (tax == 0) {
                            taxZ += tax;
                            if (prod.getSubtotal() != null && !prod.getSubtotal().isEmpty()) {
                                subTotalZ += Double.valueOf(prod.getSubtotal());
                            }
                            System.out.println("subTotalZ - >" + subTotalZ);

                            configLabel += subAlignRightData(TOTAL_WIDTH, "" + prod.getTotal());
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(TAX_TYPE_WIDTH, " Z");
                            }

                        } else {
                            taxS += tax;
                            //   subTotalS += subTotal;
                            if (prod.getSubtotal() != null && !prod.getSubtotal().isEmpty()) {
                                subTotalS += Double.valueOf(prod.getSubtotal());
                            }

                            System.out.println("subTotalS - >" + subTotalS);

                            configLabel += subAlignRightData(TOTAL_WIDTH, "" + prod.getTotal());
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(TAX_TYPE_WIDTH, " S");
                            }
                        }
                    }
                    if (prod.getTaxType() != null && !prod.getTaxType().isEmpty()) {
                        taxType = prod.getTaxType();
                    }
                    i++;
                    configLabel += end;
                    configLabel += LINE_SEPARATOR;

                }
                configLabel += start + horizontalLine("-") + end;
                for (ProductDetails prd : productdet) {
                    configLabel += LINE_SEPARATOR;
                    if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
                        configLabel += start + subAlignDataLeft(RIGHT_SPACE, "Item Discount");
                        configLabel += subAlignDataRight(1, COLON);
                        configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getItemdisc().toString()) + end;
                        configLabel += LINE_SEPARATOR;
                        // configLabel += subAlignDataLeftSpace(9,COLON+" "+prd.getItemdisc().toString());

                    }

                    int currencyLen = localCurrency.length() - 2;
                    if (currencyLen > 3) {
                        localCurrency = (localCurrency.length() > 3) ? localCurrency.substring(0, 2) : localCurrency;

                    }
                    System.out.println("currencyLen-->" + currencyLen);
                    if (taxType.equalsIgnoreCase("E")) {
                        configLabel += start +subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amt Excel.GST(" + localCurrency + ") ");
                    } else if (taxType.equalsIgnoreCase("I")) {
                        configLabel += start +subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amt Incel.GST(" + localCurrency + ") ");
                    } else {
                        configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Total Amt (" + localCurrency + ") ");
                    }

                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getSubtotal().toString()) + end;

                    configLabel += LINE_SEPARATOR;
                    if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
                        //configLabel +=  COLON+" "+"12.022";

                        //   configLabel += subAlignDataRight(1,COLON);
                        //  configLabel += subAlignRightData(54," "+prd.getItemdisc().toString());


                        //configLabel += subAlignData("Item Discount  : 12.022");
                        // configLabel += subAlignDataDetail("SubTotal   : 12.022");
                        configLabel += start + subAlignDataLeft(RIGHT_SPACE, "Bill Discount");
                        configLabel += subAlignDataRight(1, COLON);
                        configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getBilldisc().toString()) +end;
                        configLabel += LINE_SEPARATOR;
                        // configLabel += subAlignDataLeftSpace(9,COLON+" "+prd.getBilldisc().toString());
                        // configLabel +=  COLON+" "+"12.022";

                    }
                    //else{
                    configLabel += start +subAlignDataLeft(RIGHT_SPACE, "(+) GST @ " + companyTaxValue + "% (" + localCurrency + ") ");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getTax().toString()) + end;


                    //configLabel +=  COLON+" "+"12.022";
                    //  configLabel += subAlignDataDetail("Tax  : 12.0222");
                    configLabel += LINE_SEPARATOR;
                    configLabel += start + horizontalLine("-")+ end;
                    configLabel += LINE_SEPARATOR;


                    configLabel += start + subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount Payable(" + localCurrency + ")");
                    configLabel += subAlignDataRight(1, COLON);

                    configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getNettot().toString()) + end;


                    configLabel += LINE_SEPARATOR;


                    configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Paid Amount (" + localCurrency + ") ");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getPaidamount().toString())+end;

                    configLabel += LINE_SEPARATOR;
                    configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Balance (" + localCurrency + ") ");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getBalanceAmount().toString())+end;


                }
                if (malaysiaShowGST.matches("1")) {
                    configLabel += LINE_SEPARATOR;

                    if (localCurrency.equals("RM")) {
                        configLabel += start +horizontalLineSpace("-") +end;
                        configLabel += LINE_SEPARATOR;
                        configLabel += start +subAlignDataRight(GST_SUMMARY_LABEL, "GST Summary");
                        configLabel += subAlignDataRight(GST_AMOUNT_LABEL, "Amount");
                        configLabel += subAlignDataRight(GST_LABEL, "GST")+end;
                        configLabel += LINE_SEPARATOR;
                        configLabel += start +horizontalLineSpace("-")+end;
                        configLabel += LINE_SEPARATOR;
                        configLabel += start +subAlignDataRight(GST_SUMMARY_VALUE, "Z=0%");
                        configLabel += subAlignDataRight(GST_AMOUNT_VALUE, "" + twoDecimalPoint(subTotalZ));
                        configLabel += subAlignDataRight(GST_VALUE, "" + twoDecimalPoint(taxZ))+end;
                        configLabel += LINE_SEPARATOR;
                        configLabel += start +subAlignDataRight(GST_SUMMARY_VALUE, "S=" + companyTaxValue + "%");
                        configLabel += subAlignDataRight(GST_AMOUNT_VALUE, "" + twoDecimalPoint(subTotalS));
                        configLabel += subAlignDataRight(GST_VALUE, "" + twoDecimalPoint(taxS))+end;
                        configLabel += LINE_SEPARATOR;
                        configLabel += start +horizontalLineSpace("-")+end;
                    }

                configLabel += LINE_SEPARATOR;
            }
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += start + horizontalLine("-")+ end;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += start +subAlignDataBtSpace("Received By");
                configLabel += subAlignDataRight(BETWEEN_SPACE, " ");
                configLabel += subAlignDataBtSpace("Authorized By")+end;
                configLabel += LINE_SEPARATOR;
                configLabel += start + horizontalLine("-")+ end;
                configLabel += LINE_SEPARATOR;
                configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
                configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime)+end;

                // print next line if it is 3 Inch Bluetooth Generic printer
                if(printertype.matches("3 Inch Bluetooth Generic")){
                    configLabel += LINE_SEPARATOR;
                    configLabel += start +subAlignDataLeft(RIGHT_SPACE, "Issued By ");
                    configLabel += subAlignDataLeftSpace(17, COLON + " " + user)+end;

                }else if(printertype.matches("4 Inch Bluetooth")){
                    configLabel += start +subAlignDataRight(13, " ");
                    configLabel += subAlignDataRight(11, "Issued By : ");
                    configLabel += subAlignDataLeftSpace(14, " " + user)+end;
                }

                configLabel += LINE_SEPARATOR;
                configLabel += start +horizontalLine("-")+end;
                configLabel += LINE_SEPARATOR;
                configLabel += start +subAlignCenter("Thank You")+end;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;

                System.out.println(configLabel);
            }

            //Start
            /*String valueUTF8 = URLEncoder.encode(configLabel, "UTF-8");
            String res = valueUTF8.replaceAll("\\%", "\\_");
            String result = res.replaceAll("\\+", "\\ ");

            Log.d("valueUTF8",""+result);*/
            // End

           // sendMessage(configLabel);

//            String finalres = "^XA" + configLabel +"^XZ";

            os.write(configLabel.getBytes());
            os.flush();
            os.close();
        } catch (Exception e) {
            helper.showErrorDialog(e.getMessage());
        }

    }

    // Sales Return Print starts
    //saravana 14.02.17 - both 3 inch and 4 inch print
    public void printSalesReturn(String srno, String srdate,
                                 String customercode, String customername,
                                 List<ProductDetails> product, List<ProductDetails> productdet,
                                 int nofcopies){
        try {
            String configLabel ="";

            String showCustomerCode = MobileSettingsSetterGetter
                    .getShowCustomerCode();
            String showCustomerName = MobileSettingsSetterGetter
                    .getShowCustomerName();
            String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
            String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
            String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
            String showCustomerPhone = MobileSettingsSetterGetter
                    .getShowCustomerPhone();
            String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
            String showCustomerEmail = MobileSettingsSetterGetter
                    .getShowCustomerEmail();
            String showCustomerTerms = MobileSettingsSetterGetter
                    .getShowCustomerTerms();
            String showTotalOutstanding = MobileSettingsSetterGetter
                    .getShowTotalOutstanding();
            String showProductFullName = MobileSettingsSetterGetter.getShowProductFullName();
            String showFooter = MobileSettingsSetterGetter.getShowFooter();
            String address1 = CustomerSetterGetter.getCustomerAddress1();
            String address2 = CustomerSetterGetter.getCustomerAddress2();
            String address3 = CustomerSetterGetter.getCustomerAddress3();
            String localCurrency = SalesOrderSetGet.getLocalCurrency();

            String user = SupplierSetterGetter.getUsername();
//            String serverdateTime = SalesOrderSetGet.getServerDateTime();
            String taxType = SalesOrderSetGet.getCompanytax();

            String companyTaxValue = SalesOrderSetGet.getCompanytaxvalue();
            if(companyTaxValue!=null && !companyTaxValue.isEmpty()){
                companyTaxValue = companyTaxValue.split("\\.")[0];
            }

            String malaysiaShowGST  = SalesOrderSetGet.getMalaysiaShowGST();

            if(malaysiaShowGST!=null && !malaysiaShowGST.isEmpty()){

            }else{
                malaysiaShowGST="";
            }
            if(printertype.matches("4 Inch Bluetooth")) {
                byte[] send = new byte[3];
                //Title
                send[0] = 0x1b;
                send[1] = 0x61;
                send[2] = 0;
                GlobalData.mService.write(send);
            }

            for (int n = 0; n < nofcopies; n++) {
                double taxZ = 0.00,taxS = 0.00,subTotalZ=0.00,subTotalS=0.00;

                configLabel += subAlignCenter("SALES RETURN");
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;

                configLabel += printCompanyDetails();

                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;


                configLabel += subAlignDataLeft(HEADER_SPACE, SALESRETURN_NO);
                configLabel += COLON + " " + srno;
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(HEADER_SPACE, SALESRETURN_DATE);
                configLabel += COLON + " " + srdate;
                configLabel += LINE_SEPARATOR;

                if (showCustomerCode.matches("True")) {

                    if (!customercode.matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, CUSTOMER_CODE);
                        configLabel += COLON + " " + customercode;
                        configLabel += LINE_SEPARATOR;
                    }
                }
                if (showCustomerName.matches("True")) {

                    if (!customername.matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, CUSTOMER_NAME);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (customername.length() > 29) {
                                String data = concatenateStr(customername);
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + customername;
                            }

                        } else {
                            configLabel += COLON + " " + customername;
                        }
                        configLabel += LINE_SEPARATOR;

                    }
                }
                if (showAddress1.matches("True")) {
                    if (!address1.matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, ADDRESS);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address1.length() > 29) {
                                String data = concatenateStr(address1);
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + address1;
                            }

                        } else {
                            configLabel += COLON + " " + address1;
                        }
                        configLabel += LINE_SEPARATOR;
                    }
                }

                if (showAddress2.matches("True")) {
                    if (!address2.matches("")) {

                        configLabel += subAlignDataLeft(HEADER_SPACE, "        ");
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address2.length() > 29) {
                                String data = concatenateStr(address2);
                                configLabel += " " + " " + data;
                            } else {
                                configLabel += " " + " " + address2;
                            }

                        } else {
                            configLabel += " " + " " + address2;
                        }
                        configLabel += LINE_SEPARATOR;

                    }
                }

                if (showAddress3.matches("True")) {
                    if (!address3.matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, "        ");
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address3.length() > 29) {
                                String data = concatenateStr(address3);
                                configLabel += " " + " " + data;
                            } else {
                                configLabel += " " + " " + address3;
                            }

                        } else {
                            configLabel += " " + " " + address3;
                        }
                        configLabel += LINE_SEPARATOR;
                    }
                }


                if (showCustomerPhone.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerPhone().matches("")) {

                        configLabel += subAlignDataLeft(HEADER_SPACE, PHONE_NO);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone();
                        configLabel += LINE_SEPARATOR;
                    }
                }

                if (showCustomerHP.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, HEAD_PHONE);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP();
                        configLabel += LINE_SEPARATOR;

                    }
                }

                if (showCustomerEmail.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerEmail().matches("")) {


                        configLabel += subAlignDataLeft(HEADER_SPACE, EMAIL);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (CustomerSetterGetter.getCustomerEmail().length() > 29) {
                                String data = concatenateStr(CustomerSetterGetter.getCustomerEmail());
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                            }

                        } else {
                            configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                        }
                        configLabel += LINE_SEPARATOR;
                    }
                }

                String invoice_no = SalesOrderSetGet.getSrinvoiceno();

                if (invoice_no != null && !invoice_no.isEmpty()) {

                    configLabel += subAlignDataLeft(HEADER_SPACE, INVOICE_NO);
                    configLabel += COLON + " " + invoice_no;
                    configLabel += LINE_SEPARATOR;

                }


                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignData(SLNO_WIDTH, "No");
                configLabel += subAlignData(DESCRIPTION_WIDTH, "Description");
                configLabel += subAlignRightData(QTY_WIDTH, "Qty");
                configLabel += subAlignRightData(PRICE_WIDTH, "Price");
                if (malaysiaShowGST.matches("1")) {
                    configLabel += subAlignRightData(PRICE_WIDTH, "GST(" + companyTaxValue + "%)");
                }
                configLabel += subAlignRightData(TOTAL_WIDTH, "Total");
                configLabel += subAlignRightData(TAX_TYPE_WIDTH, "  ");
                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;


                int i = 1;
                for (ProductDetails prod : product) {
                    configLabel += subAlignData(SLNO_WIDTH, String.valueOf(i));
                    configLabel += subAlignData(DESCRIPTION_WIDTH, (prod.getDescription().length() > 40) ? prod.getDescription().substring(0, 39) : prod.getDescription());

                    // print next line if it is 3 Inch Bluetooth Generic printer
                    if (printertype.matches("3 Inch Bluetooth Generic")) {
                        if (prod.getDescription().length() > DESCRIPTION_WIDTH) {
                            configLabel += LINE_SEPARATOR;
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(15," ");
                            }else{
                                configLabel += subAlignRightData(24," ");
                            }
                        }
                    }

                    configLabel += subAlignRightData(QTY_WIDTH, "" + prod.getQty());
                    configLabel += subAlignRightData(PRICE_WIDTH, "" + prod.getPrice());

                    if (malaysiaShowGST.matches("1")) {
                        String taxStr = prod.getTax();
                        if (taxStr != null && !taxStr.isEmpty()) {
                            double dTax = Double.valueOf(taxStr);
                            configLabel += subAlignRightData(TAX_WIDTH, twoDecimalPoint(Double.valueOf(dTax)));
                        }
                    }

                    double subTotal = 0;
                    if (prod.getTax() != null && !prod.getTax().isEmpty()) {
                        double tax = Double.valueOf(prod.getTax());
                        if (prod.getSubtotal() != null && !prod.getSubtotal().isEmpty()) {
                            subTotal += Double.valueOf(prod.getSubtotal());
                        }
                        if (tax == 0) {
                            taxZ += tax;
                            subTotalZ += subTotal;
                            configLabel += subAlignRightData(TOTAL_WIDTH, "" + prod.getTotal());
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(TAX_TYPE_WIDTH, " Z");
                            }

                        } else {
                            taxS += tax;
                            subTotalS += subTotal;
                            configLabel += subAlignRightData(TOTAL_WIDTH, "" + prod.getTotal());
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(TAX_TYPE_WIDTH, " S");
                            }
                        }
                    }
                    if(prod.getTaxType()!=null && !prod.getTaxType().isEmpty()){
                        taxType = prod.getTaxType();
                    }
                    i++;
                    configLabel += LINE_SEPARATOR;
                }
                configLabel += horizontalLine("-");
                for (ProductDetails prd : productdet) {
                    configLabel += LINE_SEPARATOR;
                    if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
                        configLabel += subAlignDataLeft(RIGHT_SPACE, "Item Discount");
                        configLabel += subAlignDataRight(1, COLON);
                        configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getItemdisc().toString());
                        configLabel += LINE_SEPARATOR;
                        // configLabel += subAlignDataLeftSpace(9,COLON+" "+prd.getItemdisc().toString());

                    }
                    int currencyLen = localCurrency.length() - 2;
                    if (currencyLen > 3) {
                        localCurrency = (localCurrency.length() > 3) ? localCurrency.substring(0, 2) : localCurrency;

                    }


                    Log.d("tax type",""+taxType);
                    if(taxType.equalsIgnoreCase("E")){
                        configLabel += subAlignDataLeft(RIGHT_SPACE-currencyLen, "Total Amt Excel.GST("+localCurrency+") ");
                    }else if(taxType.equalsIgnoreCase("I")){
                        configLabel += subAlignDataLeft(RIGHT_SPACE-currencyLen, "Total Amt Incel.GST("+localCurrency+") ");                    }
                    else{
                        configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Amt ("+localCurrency+") ");
                    }

                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getSubtotal().toString());

                    configLabel += LINE_SEPARATOR;
                    if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {

                        configLabel += subAlignDataLeft(RIGHT_SPACE, "Bill Discount");
                        configLabel += subAlignDataRight(1, COLON);
                        configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getBilldisc().toString());
                        configLabel += LINE_SEPARATOR;
                    }

                    configLabel += subAlignDataLeft(RIGHT_SPACE, "(+) GST @ " + companyTaxValue + "% (" + localCurrency + ") ");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getTax().toString());


                    configLabel += LINE_SEPARATOR;
                    configLabel += horizontalLine("-");
                    configLabel += LINE_SEPARATOR;

                    configLabel += subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount(" + localCurrency + ")");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getNettot().toString());


                }
                if (malaysiaShowGST.matches("1")) {
                    configLabel += LINE_SEPARATOR;

                    if (localCurrency.equals("RM")) {
                        configLabel += horizontalLineSpace("-");
                        configLabel += LINE_SEPARATOR;
                        configLabel += subAlignDataRight(GST_SUMMARY_LABEL, "GST Summary");
                        configLabel += subAlignDataRight(GST_AMOUNT_LABEL, "Amount");
                        configLabel += subAlignDataRight(GST_LABEL, "GST");
                        configLabel += LINE_SEPARATOR;
                        configLabel += horizontalLineSpace("-");
                        configLabel += LINE_SEPARATOR;
                        configLabel += subAlignDataRight(GST_SUMMARY_VALUE, "Z=0%");
                        configLabel += subAlignDataRight(GST_AMOUNT_VALUE, "" + twoDecimalPoint(subTotalZ));
                        configLabel += subAlignDataRight(GST_VALUE, "" + twoDecimalPoint(taxZ));
                        configLabel += LINE_SEPARATOR;
                        configLabel += subAlignDataRight(GST_SUMMARY_VALUE, "S=" + companyTaxValue + "%");
                        configLabel += subAlignDataRight(GST_AMOUNT_VALUE, "" + twoDecimalPoint(subTotalS));
                        configLabel += subAlignDataRight(GST_VALUE, "" + twoDecimalPoint(taxS));
                        configLabel += LINE_SEPARATOR;
                        configLabel += horizontalLineSpace("-");
                    }

                    configLabel += LINE_SEPARATOR;
                }
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataBtSpace("Returned By");
                configLabel += subAlignDataRight(BETWEEN_SPACE, " ");
                configLabel += subAlignDataBtSpace("Authorized By");
                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");

                // print next line if it is 3 Inch Bluetooth Generic printer
                if(printertype.matches("3 Inch Bluetooth Generic")){
                    configLabel += LINE_SEPARATOR;
                    configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
                    configLabel += subAlignDataLeftSpace(17, COLON + " " + user);

                }else if(printertype.matches("4 Inch Bluetooth")){
                    configLabel += subAlignDataRight(13, " ");
                    configLabel += subAlignDataRight(11, "Issued By : ");
                    configLabel += subAlignDataLeftSpace(14, " " + user);
                }

                //  configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);
                //  configLabel += subAlignDataRight(13, " ");
                //  configLabel += subAlignDataRight(11, "Issued By : ");
                //  configLabel += subAlignDataLeftSpace(14, " " + user);
                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignCenter("Thank You");
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;

                System.out.println(configLabel);
            }

            sendMessage(configLabel);

        } catch (Exception e) {
            helper.showErrorDialog(e.getMessage());
        }

    }

    // ~ arun ~ requested by sheik - print transfer
    public void printTransfer(String No, String Date,
                                        String fromlocation, String tolocation,
                                        List<ProductDetails> product, String title, int nofcopies)
            throws IOException {

        String localCurrency = SalesOrderSetGet.getLocalCurrency();

        String user = SupplierSetterGetter.getUsername();
//            String serverdateTime = SalesOrderSetGet.getServerDateTime();
        String taxType = SalesOrderSetGet.getCompanytax();

        String companyTaxValue = SalesOrderSetGet.getCompanytaxvalue();
        if(companyTaxValue!=null && !companyTaxValue.isEmpty()){
            companyTaxValue = companyTaxValue.split("\\.")[0];
        }

        String malaysiaShowGST  = SalesOrderSetGet.getMalaysiaShowGST();

        if(malaysiaShowGST!=null && !malaysiaShowGST.isEmpty()){

        }else{
            malaysiaShowGST="";
        }
        if(printertype.matches("4 Inch Bluetooth")) {
            byte[] send = new byte[3];
            //Title
            send[0] = 0x1b;
            send[1] = 0x61;
            send[2] = 0;
            GlobalData.mService.write(send);
        }

        for (int n = 0; n < nofcopies; n++) {
            int y = 0;

            configLabel += subAlignCenter("TRANSFER");
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;

            configLabel += printCompanyDetails();

            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;


            if (title.matches("STOCK REQUEST")) {
//                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No")
//                        + text(170, y, " : ") + text(190, y, sono);
//                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
//                        "SR Date") + text(170, y, " : ") + text(190, y, sodate);

                configLabel += subAlignDataLeft(HEADER_SPACE, "SR No");
                configLabel += COLON + " " + No;
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(HEADER_SPACE, "SR Date");
                configLabel += COLON + " " + Date;
                configLabel += LINE_SEPARATOR;
            } else if (title.matches("TRANSFER")) {
//                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
//                        "Transfer No")
//                        + text(170, y, " : ")
//                        + text(190, y, sono);
//                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
//                        "Transfer Date")
//                        + text(170, y, " : ")
//                        + text(190, y, sodate);

                configLabel += subAlignDataLeft(HEADER_SPACE, TRANSFER_NO);
                configLabel += COLON + " " + No;
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(HEADER_SPACE, TRANSFER_DATE);
                configLabel += COLON + " " + Date;
                configLabel += LINE_SEPARATOR;
            }

//            cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
//                    "FromLocation"));
//            cpclConfigLabel += (text(170, y, " : "));
//            cpclConfigLabel += (text(190, y, fromlocation));
//            cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
//                    "ToLocation"));
//            cpclConfigLabel += (text(170, y, " : "));
//            cpclConfigLabel += (text(190, y, tolocation));

            configLabel += subAlignDataLeft(HEADER_SPACE, "FromLocation");
            configLabel += COLON + " " + fromlocation;
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(HEADER_SPACE, "ToLocation");
            configLabel += COLON + " " + tolocation;
            configLabel += LINE_SEPARATOR;

//            cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

            double totalqty = 0.00;
            if (title.matches("STOCK REQUEST")) {

                /*cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
                cpclConfigLabel += text(210, y, "Description");

                cpclConfigLabel += text(500, y, "Qty");
                // cpclConfigLabel += text(395, y, "TransferQty");
                // cpclConfigLabel += text(500, y, "Total");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);*/

                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignData(SLNO_WIDTH, "Code");
                configLabel += subAlignData(DESCRIPTION_WIDTH, "Description");
                configLabel += subAlignRightData(QTY_WIDTH, "Qty");
                configLabel += subAlignRightData(PRICE_WIDTH, "TransferQty");
                configLabel += subAlignRightData(TOTAL_WIDTH, "Total");
//                configLabel += subAlignRightData(TAX_TYPE_WIDTH, "  ");
                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;

                totalqty = 0.00;
                for (ProductDetails prods : product) {

                    /*cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
                            .getItemcode().toString());
                    cpclConfigLabel += text(210, y, (prods.getDescription()
                            .length() > 10) ? prods.getDescription()
                            .substring(0, 9) : prods.getDescription());
                    cpclConfigLabel += text(500, y, prods.getQty());*/

                    configLabel += subAlignData(SLNO_WIDTH, prods
                            .getItemcode().toString());
                    configLabel += subAlignData(DESCRIPTION_WIDTH, (prods.getDescription().length() > 40) ? prods.getDescription().substring(0, 39) : prods.getDescription());

                    // print next line if it is 3 Inch Bluetooth Generic printer
                    if (printertype.matches("3 Inch Bluetooth Generic")) {
                        if (prods.getDescription().length() > DESCRIPTION_WIDTH) {
                            configLabel += LINE_SEPARATOR;
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(15," ");
                            }else{
                                configLabel += subAlignRightData(24," ");
                            }
                        }
                    }
                    configLabel += subAlignRightData(QTY_WIDTH, "" + prods.getQty());
//                    configLabel += subAlignRightData(PRICE_WIDTH, "" + prods.getPrice());

                    totalqty += prods.getTotalqty();
                    configLabel += LINE_SEPARATOR;
                }

            } else if (title.matches("TRANSFER")) {

                String showcartonorloose = SalesOrderSetGet.getCartonpriceflag();

                /*if(showcartonorloose.matches("1")){
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                    cpclConfigLabel += text(70, y, "Description");
                    cpclConfigLabel += text(280, y, "CQty");
                    cpclConfigLabel += text(375, y, "LQty");
                }else{
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
                    cpclConfigLabel += text(210, y, "Description");
                }

                cpclConfigLabel += text(500, y, "Qty");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);*/

                if(showcartonorloose.matches("1")){
                    configLabel += horizontalLine("-");
                    configLabel += LINE_SEPARATOR;
                    configLabel += subAlignData(SLNO_WIDTH, "SNo");
                    configLabel += subAlignData(DESCRIPTION_WIDTH, "Description");
                    configLabel += subAlignRightData(QTY_WIDTH, "CQty");
                    configLabel += subAlignRightData(PRICE_WIDTH, "LQty");
//                configLabel += subAlignRightData(TAX_TYPE_WIDTH, "  ");

                }else{
                    configLabel += subAlignData(SLNO_WIDTH, "Code");
                    configLabel += subAlignData(DESCRIPTION_WIDTH, "Description");
                }

                configLabel += subAlignRightData(QTY_WIDTH, "Qty");

                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;

                totalqty = 0.00;
                int i = 1;
                for (ProductDetails prods : product) {

                    String showProductFullName = MobileSettingsSetterGetter.getShowProductFullName();

                    if(showcartonorloose.matches("1")){

                        /*cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(i));

                        if (showProductFullName.matches("True")) {
                            cpclConfigLabel += text(70, y, (prods
                                    .getDescription().length() > 31) ? prods
                                    .getDescription().substring(0, 30)
                                    : prods.getDescription());
                            cpclConfigLabel += text(280, y += LINE_SPACING,
                                    prods.getCqty().toString());
                        } else {
                            cpclConfigLabel += text(70, y, (prods
                                    .getDescription().length() > 10) ? prods
                                    .getDescription().substring(0, 9)
                                    : prods.getDescription());
                            cpclConfigLabel += text(280, y, prods.getCqty()
                                    .toString());
                        }*/

                        configLabel += subAlignData(SLNO_WIDTH, ""+i);
                        configLabel += subAlignData(DESCRIPTION_WIDTH, (prods.getDescription().length() > 40) ? prods.getDescription().substring(0, 39) : prods.getDescription());


                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (prods.getDescription().length() > DESCRIPTION_WIDTH) {
                                configLabel += LINE_SEPARATOR;
                                if (malaysiaShowGST.matches("1")) {
                                    configLabel += subAlignRightData(15," ");
                                }else{
                                    configLabel += subAlignRightData(24," ");
                                }
                            }
                        }

//                        cpclConfigLabel += text(375, y, prods.getLqty());
//                        cpclConfigLabel += text(500, y, prods.getQty());
                        configLabel += subAlignRightData(QTY_WIDTH, "" + prods.getCqty());
                        configLabel += subAlignRightData(QTY_WIDTH, "" + prods.getLqty());
                        configLabel += subAlignRightData(QTY_WIDTH, "" + prods.getQty());

                    }else{
//                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
//                                .getItemcode().toString());
//                        cpclConfigLabel += text(210, y, (prods.getDescription()
//                                .length() > 10) ? prods.getDescription()
//                                .substring(0, 9) : prods.getDescription());
//                        cpclConfigLabel += text(500, y, prods.getQty());
                        configLabel += subAlignData(SLNO_WIDTH, ""+i);
                        configLabel += subAlignData(DESCRIPTION_WIDTH, (prods.getDescription().length() > 40) ? prods.getDescription().substring(0, 39) : prods.getDescription());
                        configLabel += subAlignRightData(QTY_WIDTH, "" + prods.getQty());

                    }

                    totalqty += prods.getTotalqty();
                    configLabel += LINE_SEPARATOR;
                    i++;
                }

            }
//            configLabel += LINE_SEPARATOR;
//            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            /*if(printertype.matches("3 Inch Bluetooth Generic")) {
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Quantity  ");
                configLabel += subAlignDataLeftSpace(TOTAL_SPACE, COLON + " " + String.valueOf((int) totalqty));
            }else if(printertype.matches("4 Inch Bluetooth")){
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Quantity  ");
//                configLabel += subAlignDataRight(13, " ");
                configLabel += subAlignDataRight(TOTAL_SPACE, COLON + " "+ String.valueOf((int) totalqty));
            }*/

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Quantity");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE, " " + String.valueOf((int) totalqty));


            configLabel += LINE_SEPARATOR;
//            configLabel += LINE_SEPARATOR;
           /* configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataBtSpace("Returned By");
            configLabel += subAlignDataRight(BETWEEN_SPACE, " ");
            configLabel += subAlignDataBtSpace("Authorized By");
            configLabel += LINE_SEPARATOR;*/
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
            configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);
            // print next line if it is 3 Inch Bluetooth Generic printer
            if(printertype.matches("3 Inch Bluetooth Generic")){
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
                configLabel += subAlignDataLeftSpace(17, COLON + " " + user);

            }else if(printertype.matches("4 Inch Bluetooth")){
                configLabel += subAlignDataRight(13, " ");
                configLabel += subAlignDataRight(11, "Issued By : ");
                configLabel += subAlignDataLeftSpace(14, " " + user);
            }

            //  configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);
            //  configLabel += subAlignDataRight(13, " ");
            //  configLabel += subAlignDataRight(11, "Issued By : ");
            //  configLabel += subAlignDataLeftSpace(14, " " + user);
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignCenter("Thank You");
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;

            System.out.println(configLabel);
        }
        sendMessage(configLabel);
    }


    public void printCurrentDateReceipt(String receiptdate,
                                        ArrayList<Receipt> receiptlist, List<ProductDetails> footerValue){

        String configLabel ="";
        String user = SupplierSetterGetter.getUsername();
        double cashamount = 0.00, chequeamount = 0.00, total = 0.00, otheramount = 0.00;
        String paymode_cash = "", paymode_cheque = "", paymode_other = "";
        int no = 1;

        byte[] send=new byte[3];
        //Title
        send[0]=0x1b;
        send[1]=0x61;
        send[2]=0;
        GlobalData.mService.write(send);


        configLabel += subAlignCenter("RECEIPT SUMMARY");
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;

        configLabel += printCompanyDetails();

        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignDataLeft(RIGHT_SPACE, "Receipt Date");
        configLabel += COLON + " " + receiptdate;
        configLabel += LINE_SEPARATOR;



        //  configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignData(5,"No");
        configLabel += subAlignData(25,"Receipt No");
        configLabel += subAlignData(25,"Customer Name");
        configLabel += subAlignRightData(25,"Total");
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;

        for (Receipt receipt : receiptlist) {
            configLabel += subAlignData(5,""+ no);
            configLabel += subAlignData(25,receipt.getReceiptno().toString());
            configLabel += subAlignData(25,(receipt.getCustomername().length() > 25) ? receipt
                    .getCustomername().substring(0, 24) : receipt
                    .getCustomername());
            configLabel += subAlignRightData(25, twoDecimalPoint(receipt.getPaidamount()));

            if (receipt.getPaymode().toLowerCase().matches("cash")) {
                cashamount += receipt.getPaidamount();
                paymode_cash = "Cash";
                Log.d("cashamount", "-->" + cashamount);
            } else if (receipt.getPaymode().toLowerCase().matches("cheque")) {
                chequeamount += receipt.getPaidamount();
                paymode_cheque = "Cheque";
                Log.d("chequeamount", "-->" + chequeamount);
            } else {
                otheramount += receipt.getPaidamount();
                paymode_other = "Others";
                Log.d("otheramount", "-->" + otheramount);
            }
            total += receipt.getPaidamount();
            no++;
            configLabel += LINE_SEPARATOR;
        }
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;


        if (paymode_cash.toLowerCase().matches("cash")
                && paymode_cheque.toLowerCase().matches("cheque")
                && paymode_other.toLowerCase().matches("others")) {


            configLabel += subAlignDataLeft(RIGHT_SPACE, "Cash");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(cashamount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Cheque");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(chequeamount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Others");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(otheramount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;


        } else if (paymode_cash.toLowerCase().matches("cash")
                && paymode_cheque.toLowerCase().matches("cheque")) {

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Cash");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(cashamount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Cheque");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(chequeamount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;


        } else if (paymode_cash.toLowerCase().matches("cash")
                && paymode_other.toLowerCase().matches("others")) {

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Cash");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(cashamount));
            configLabel += LINE_SEPARATOR;


            configLabel += subAlignDataLeft(RIGHT_SPACE, "Others");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(otheramount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;



        } else if (paymode_cheque.toLowerCase().matches("cheque")
                && paymode_other.toLowerCase().matches("others")) {

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Cheque");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(chequeamount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Others");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(otheramount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;

        } else if (paymode_cash.toLowerCase().matches("cash")) {

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Cash");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(cashamount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;
        } else if (paymode_cheque.toLowerCase().matches("cheque")) {

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Cheque");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(chequeamount));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;
        } else {
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;
        }
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;



        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignDataBtSpace("Received By");
        configLabel += subAlignDataRight(36, " ");
        configLabel += subAlignDataBtSpace("Authorized By");
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
        configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);
        configLabel += subAlignDataRight(13, " ");
        configLabel += subAlignDataRight(11, "Issued By : ");
        configLabel += subAlignDataLeftSpace(14, " " + user);
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignCenter("Thank You");
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        System.out.println(configLabel);

        sendMessage(configLabel);


    }

    public void printOnlineInvoiceInOffline(HashMap<String,String> hashmap,
                                            ArrayList<In_Cash> OnlineInvoiceInOfflineArr,int nofcopies,  List<ProductDetails> footerValue){
        String configLabel ="";
        String receiptNo = hashmap.get("ReceiptNo");
        String receiptDate = hashmap.get("ReceiptDate");

        String customerCode  = hashmap.get("CustomerCode");
        String customerName  = hashmap.get("CustomerName");
        String paymode  = hashmap.get("PayMode");

        String showCustomerCode = MobileSettingsSetterGetter
                .getShowCustomerCode();
        String showCustomerName = MobileSettingsSetterGetter
                .getShowCustomerName();
        String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
        String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
        String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
        String showCustomerPhone = MobileSettingsSetterGetter
                .getShowCustomerPhone();
        String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
        String showCustomerEmail = MobileSettingsSetterGetter
                .getShowCustomerEmail();
        String showCustomerTerms = MobileSettingsSetterGetter
                .getShowCustomerTerms();
        String showTotalOutstanding = MobileSettingsSetterGetter
                .getShowTotalOutstanding();
        String showProductFullName = MobileSettingsSetterGetter
                .getShowProductFullName();
        String showFooter = MobileSettingsSetterGetter.getShowFooter();
        String address1 = CustomerSetterGetter.getCustomerAddress1();
        String address2 = CustomerSetterGetter.getCustomerAddress2();
        String address3 = CustomerSetterGetter.getCustomerAddress3();
        String user = SupplierSetterGetter.getUsername();


        byte[] send=new byte[3];
        //Title
        send[0]=0x1b;
        send[1]=0x61;
        send[2]=0;
        GlobalData.mService.write(send);
        for (int n = 0; n < nofcopies; n++) {
            int no = 1;
            double total = 0.00;
            configLabel += subAlignCenter("RECEIPT");
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;

            configLabel += printCompanyDetails();

            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Receipt No");
            configLabel += COLON + " " + receiptNo;
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Receipt Date");
            configLabel += COLON + " " + receiptDate;
            configLabel += LINE_SEPARATOR;
            if (showCustomerCode.matches("True")) {

                if (!customerCode.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, CUSTOMER_CODE);
                    configLabel += COLON + " " + customerCode;
                    configLabel += LINE_SEPARATOR;
                }
            }
            if (showCustomerName.matches("True")) {

                if (!customerName.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, CUSTOMER_NAME);
                    configLabel += COLON + " " + customerName;
                    configLabel += LINE_SEPARATOR;

                }
            }
            if (showAddress1.matches("True")) {
                if (!address1.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, ADDRESS);
                    configLabel += COLON + " " + address1;
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showAddress2.matches("True")) {
                if (!address2.matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, "        ");
                    configLabel += " " + " " + address2;
                    configLabel += LINE_SEPARATOR;

                }
            }

            if (showAddress3.matches("True")) {
                if (!address3.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, "        ");
                    configLabel += " " + " " + address3;
                    configLabel += LINE_SEPARATOR;
                }
            }
            if (showCustomerPhone.matches("True")) {
                if (!CustomerSetterGetter.getCustomerPhone().matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, PHONE_NO);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone();
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showCustomerHP.matches("True")) {
                if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, HEAD_PHONE);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP();
                    configLabel += LINE_SEPARATOR;

                }
            }

            if (showCustomerEmail.matches("True")) {
                if (!CustomerSetterGetter.getCustomerEmail().matches("")) {


                    configLabel += subAlignDataLeft(RIGHT_SPACE, EMAIL);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showCustomerTerms.matches("True")) {
                if (!CustomerSetterGetter.getCustomerTerms().matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, TERM);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerTerms();
                    configLabel += LINE_SEPARATOR;

                }
            }

            //  configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignData(5,"No");
            configLabel += subAlignData(25,"Invoice No");
            configLabel += subAlignData(25,"Invoice Date");
            configLabel += subAlignRightData(25,"Total");
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            for (In_Cash mIn_Cash : OnlineInvoiceInOfflineArr) {
                configLabel += subAlignData(5,""+ no);
                configLabel += subAlignData(25,mIn_Cash.getIn_InvNo().toString());
                configLabel += subAlignData(25,mIn_Cash.getIn_Date());
                configLabel += subAlignRightData(25, mIn_Cash.getAdd_paid());
                configLabel += LINE_SEPARATOR;
                if(mIn_Cash.getAdd_paid()!=null && !mIn_Cash.getAdd_paid().isEmpty()){
                    total += Double.valueOf(mIn_Cash.getAdd_paid());
                }
                no++;
            }
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Paymode");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,paymode);
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

        }

        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignDataBtSpace("Received By");
        configLabel += subAlignDataRight(36, " ");
        configLabel += subAlignDataBtSpace("Authorized By");
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
        configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);
        configLabel += subAlignDataRight(13, " ");
        configLabel += subAlignDataRight(11, "Issued By : ");
        configLabel += subAlignDataLeftSpace(14, " " + user);
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignCenter("Thank You");
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        System.out.println(configLabel);

        sendMessage(configLabel);


    }
    public void InvoiceSummmaryPrint(String flag,String user,String fromDate,String toDate,String locationCode,
                                 String locationName,
                                 ArrayList<Product> ProductListArray){
        String configLabel ="";
        double total = 0.00,totqty=0;
        int no = 1;
        double nettot = 0,qty=0;
        if(printertype.matches("4 Inch Bluetooth")) {
            byte[] send = new byte[3];
            //Title
            send[0] = 0x1b;
            send[1] = 0x61;
            send[2] = 0;
            GlobalData.mService.write(send);
        }
        Log.d("flag",flag);

        configLabel += subAlignCenter(flag);
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;

        configLabel += printCompanyDetails();
        configLabel += LINE_SEPARATOR;

        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;

        configLabel += subAlignDataLeft(RIGHT_SPACE, "User");
        configLabel += COLON + " " + user;
        configLabel += LINE_SEPARATOR;

        configLabel += subAlignDataLeft(RIGHT_SPACE, "From Date");
        configLabel += COLON + " " + fromDate;
        configLabel += LINE_SEPARATOR;

        configLabel += subAlignDataLeft(RIGHT_SPACE, "To Date");
        configLabel += COLON + " " + toDate;
        configLabel += LINE_SEPARATOR;

        configLabel += subAlignDataLeft(RIGHT_SPACE, "Location Code");
        configLabel += COLON + " " + locationCode;
        configLabel += LINE_SEPARATOR;

        configLabel += subAlignDataLeft(RIGHT_SPACE, "Location Name");
        configLabel += COLON + " " + locationName;
        configLabel += LINE_SEPARATOR;

        //  configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;

        if(flag.matches("Invoice By Product")){
            configLabel += subAlignData(NUM_WIDTH,"No");
            configLabel += subAlignData(PRODUCT_NAME_WIDTH,"Product Name");
            configLabel += subAlignRightData(CARTON_WIDTH,"Carton");
            configLabel += subAlignRightData(LOOSE_WIDTH,"Loose");
            if(printertype.matches("4 Inch Bluetooth")) {
                configLabel += subAlignRightData(QTY_WIDTH,"Quantity");
            }else if(printertype.matches("3 Inch Bluetooth Generic")) {
                configLabel += subAlignRightData(QTY_WIDTH,"Qty");
        }
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            for (Product invoice : ProductListArray) {
                configLabel += subAlignData(NUM_WIDTH,""+ no);

                if(printertype.matches("4 Inch Bluetooth")) {
                    configLabel += subAlignData(PRODUCT_NAME_WIDTH,(invoice.getProductName().length() > 40) ? invoice
                            .getProductName().substring(0, 39) : invoice
                            .getProductName());
                }else if(printertype.matches("3 Inch Bluetooth Generic")){
                    configLabel += subAlignData(PRODUCT_NAME_WIDTH,(invoice.getProductName().length() > PRODUCT_NAME_WIDTH) ? invoice
                            .getProductName().substring(0, 21) : invoice
                            .getProductName());
                }


                configLabel += subAlignRightData(CARTON_WIDTH,invoice.getCqty().toString());
                configLabel += subAlignRightData(LOOSE_WIDTH,invoice.getLqty().toString());

                if(invoice.getQty()!=null && !invoice.getQty().isEmpty()){
                    qty = Double.parseDouble(invoice.getQty());
                }

                configLabel += subAlignRightData(QTY_WIDTH, String.valueOf(qty).split("\\.")[0]);

                totqty += qty;
                no++;
                configLabel += LINE_SEPARATOR;
            }
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Quantity");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE,String.valueOf(totqty).split("\\.")[0]);
            configLabel += LINE_SEPARATOR;
        }else if(flag.matches("Invoice Summary")){
            configLabel += subAlignData(NUM_WIDTH,"No");
            configLabel += subAlignData(INVOICE_NO_WIDTH,"Invoice No");
            configLabel += subAlignData(CUSTOMER_NAME_WIDTH,"Customer Name");
            configLabel += subAlignRightData(TOT_WIDTH,"Total");
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            for (Product invoice : ProductListArray) {
                configLabel += subAlignData(NUM_WIDTH,""+ no);

                configLabel += subAlignData(INVOICE_NO_WIDTH,invoice.getNo().toString());



                if(printertype.matches("4 Inch Bluetooth")) {
                    configLabel += subAlignData(CUSTOMER_NAME_WIDTH,(invoice.getName().length() > 25) ? invoice
                            .getName().substring(0, 24) : invoice
                            .getName());

                }else if(printertype.matches("3 Inch Bluetooth Generic")){
                    configLabel += subAlignData(CUSTOMER_NAME_WIDTH,(invoice.getName().length() > 20) ? invoice
                            .getName().substring(0, 19) : invoice
                            .getName());

                }

                if(invoice.getNetTotal()!=null && !invoice.getNetTotal().isEmpty()){
                    nettot = Double.parseDouble(invoice.getNetTotal());
                }

                configLabel += subAlignRightData(TOT_WIDTH, twoDecimalPoint(nettot));

                total += nettot;
                no++;
                configLabel += LINE_SEPARATOR;
            }
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;
        }



        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
      /*  configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignDataBtSpace("Received By");
        configLabel += subAlignDataRight(36, " ");
        configLabel += subAlignDataBtSpace("Authorized By");
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;*/
        configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
        configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);


        // print next line if it is 3 Inch Bluetooth Generic printer
        if(printertype.matches("3 Inch Bluetooth Generic")){
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
            configLabel += subAlignDataLeftSpace(17, COLON + " " + user);

        }else if(printertype.matches("4 Inch Bluetooth")){
            configLabel += subAlignDataRight(13, " ");
            configLabel += subAlignDataRight(11, "Issued By : ");
            configLabel += subAlignDataLeftSpace(14, " " + user);
        }


      //  configLabel += subAlignDataRight(13, " ");
      //  configLabel += subAlignDataRight(11, "Issued By : ");
      //  configLabel += subAlignDataLeftSpace(14, " " + user);
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignCenter("Thank You");
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        System.out.println(configLabel);

        sendMessage(configLabel);


    }

    public void ReceiptSummmaryPrint(String customercode, String customername,
                                     String receiptno, String receiptdate,
                                     List<ProductDetails> receipts, List<String> sort, String gnrlStngs,
                                     int nofcopies, boolean isSingleCustomer,
                                     List<ProductDetails> footerValue,int invoiceLength){
        String configLabel ="";
        double total = 0.00,totqty=0,dNetTotal = 0.00,dCreditAmt=0.00,dPaidAmt=0.00;
        int no = 1;
        double nettot = 0,qty=0;
        if(printertype.matches("4 Inch Bluetooth")) {
            byte[] send = new byte[3];
            //Title
            send[0] = 0x1b;
            send[1] = 0x61;
            send[2] = 0;
            GlobalData.mService.write(send);
        }
        String totalOutStanding = "";


        String showCustomerCode = MobileSettingsSetterGetter
                .getShowCustomerCode();
        String showCustomerName = MobileSettingsSetterGetter
                .getShowCustomerName();
        String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
        String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
        String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
        String showCustomerPhone = MobileSettingsSetterGetter
                .getShowCustomerPhone();
        String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
        String showCustomerEmail = MobileSettingsSetterGetter
                .getShowCustomerEmail();
        String showCustomerTerms = MobileSettingsSetterGetter
                .getShowCustomerTerms();
        String showTotalOutstanding = MobileSettingsSetterGetter
                .getShowTotalOutstanding();
        String showProductFullName = MobileSettingsSetterGetter
                .getShowProductFullName();
        String showFooter = MobileSettingsSetterGetter.getShowFooter();
        String address1 = CustomerSetterGetter.getCustomerAddress1();
        String address2 = CustomerSetterGetter.getCustomerAddress2();
        String address3 = CustomerSetterGetter.getCustomerAddress3();
        String taxType = SalesOrderSetGet.getCompanytax();

        String user = SupplierSetterGetter.getUsername();
//          String serverdateTime = SalesOrderSetGet.getServerDateTime();
        String localCurrency = SalesOrderSetGet.getLocalCurrency();

        String malaysiaShowGST  = SalesOrderSetGet.getMalaysiaShowGST();

        if(malaysiaShowGST!=null && !malaysiaShowGST.isEmpty()){

        }else{
            malaysiaShowGST="";
        }

        String companyTaxValue = SalesOrderSetGet.getCompanytaxvalue();
        if(companyTaxValue!=null && !companyTaxValue.isEmpty()){
            companyTaxValue = companyTaxValue.split("\\.")[0];
        }
        for (int n = 0; n < nofcopies; n++) {

            configLabel += subAlignCenter("RECEIPT SUMMARY");
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;

            configLabel += printCompanyDetails();

            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Receipt No");
            configLabel += COLON + " " + receiptno;
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Receipt Date");
            configLabel += COLON + " " + receiptdate;
            configLabel += LINE_SEPARATOR;

            if (showCustomerCode.matches("True")) {

                if (!customercode.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, CUSTOMER_CODE);
                    configLabel += COLON + " " + customercode;
                    configLabel += LINE_SEPARATOR;
                }
            }
            if (showCustomerName.matches("True")) {

                if (!customername.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, CUSTOMER_NAME);
                    //  configLabel += COLON + " " + customername;
                    if(printertype.matches("3 Inch Bluetooth Generic")){
                        if(customername.length()>19){
                            String data = concatenateStr(customername);
                            configLabel += COLON + " " + data;
                        }else{
                            configLabel += COLON + " " + customername;
                        }

                    }else{
                        configLabel += COLON + " " + customername;
                    }
                    configLabel += LINE_SEPARATOR;

                }
            }
            if (showAddress1.matches("True")) {
                if (!address1.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, ADDRESS);
                    //configLabel += COLON + " " + address1;
                    if(printertype.matches("3 Inch Bluetooth Generic")){
                        if(customername.length()>19){
                            String data = concatenateStr(address1);
                            configLabel += COLON + " " + data;
                        }else{
                            configLabel += COLON + " " + address1;
                        }

                    }else{
                        configLabel += COLON + " " + address1;
                    }
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showAddress2.matches("True")) {
                if (!address2.matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, "        ");
                    // configLabel += " " + " " + address2;
                    if(printertype.matches("3 Inch Bluetooth Generic")){
                        if(customername.length()>19){
                            String data = concatenateStr(address2);
                            configLabel += " " + " " + data;
                        }else{
                            configLabel += " " + " " + address2;
                        }

                    }else{
                        configLabel += " " + " " + address2;
                    }
                    configLabel += LINE_SEPARATOR;

                }
            }

            if (showAddress3.matches("True")) {
                if (!address3.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, "        ");
                    // configLabel += " " + " " + address3;
                    if(printertype.matches("3 Inch Bluetooth Generic")){
                        if(customername.length()>19){
                            String data = concatenateStr(address3);
                            configLabel += " " + " " + data;
                        }else{
                            configLabel += " " + " " + address3;
                        }

                    }else{
                        configLabel += " " + " " + address3;
                    }
                    configLabel += LINE_SEPARATOR;
                }
            }
            if (showCustomerPhone.matches("True")) {
                if (!CustomerSetterGetter.getCustomerPhone().matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, PHONE_NO);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone();
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showCustomerHP.matches("True")) {
                if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, HEAD_PHONE);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP();
                    configLabel += LINE_SEPARATOR;

                }
            }

            if (showCustomerEmail.matches("True")) {
                if (!CustomerSetterGetter.getCustomerEmail().matches("")) {


                    configLabel += subAlignDataLeft(RIGHT_SPACE, EMAIL);
                    //configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                    if(printertype.matches("3 Inch Bluetooth Generic")){
                        if(customername.length()>19){
                            String data = concatenateStr(CustomerSetterGetter.getCustomerEmail());
                            configLabel += COLON + " " + data;
                        }else{
                            configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                        }

                    }else{
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                    }
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showCustomerTerms.matches("True")) {
                if (!CustomerSetterGetter.getCustomerTerms().matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, TERM);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerTerms();
                    configLabel += LINE_SEPARATOR;

                }
            }

            //  configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");

//            if(invoiceLength>1){
                configLabel += LINE_SEPARATOR;


                /*if(printertype.matches("3 Inch Bluetooth Generic")){
                    configLabel += subAlignData(NUM_WIDTH,"No");
                    configLabel += subAlignData(11,"InvoiceNo");
                    configLabel += subAlignData(10,"InvoiceDate");
                    configLabel += subAlignRightData(10,"NetTtl");
                    configLabel += subAlignRightData(10,"Paid");

                }else if(printertype.matches("4 Inch Bluetooth")){
                    configLabel += subAlignData(NUM_WIDTH,"No");
                    configLabel += subAlignData(15,"Invoice No");
                    configLabel += subAlignData(20,"Invoice Date");
                    configLabel += subAlignRightData(20,"NetTotal");
                    configLabel += subAlignRightData(20,"Paid Amount");
                }*/

            if(printertype.matches("3 Inch Bluetooth Generic")){
                configLabel += subAlignData(NUM_WIDTH,"No");
                configLabel += subAlignData(INVOICENO,"InvoiceNo");
                configLabel += subAlignData(INVOICEDATE,"InvoiceDate");
                configLabel += subAlignRightData(NETTOTAL,"NetTtl");
                configLabel += subAlignRightData(CREDIT_AMOUNT,"Credit");
                configLabel += subAlignRightData(PAID_AMOUNT,"Paid");


            }else if(printertype.matches("4 Inch Bluetooth")){
                configLabel += subAlignData(NUM_WIDTH,"No");
                configLabel += subAlignData(INVOICENO,"Invoice No");
                configLabel += subAlignData(INVOICEDATE,"Invoice Date");
                configLabel += subAlignRightData(NETTOTAL,"NetTotal");
                configLabel += subAlignRightData(CREDIT_AMOUNT,"Credit Amount");
                configLabel += subAlignRightData(PAID_AMOUNT,"Paid Amount");

            }

                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;
//            }


            for (ProductDetails receipt : receipts) {

                /*if (invoice.getPaidamount() != null && !invoice.getPaidamount().isEmpty()) {
                    nettot = Double.parseDouble(invoice.getPaidamount());
                }

                if (printertype.matches("4 Inch Bluetooth")) {
                    configLabel += subAlignData(NUM_WIDTH,""+no);
                    configLabel += subAlignData(11, "" + invoice.getItemno().toString());
                    configLabel += subAlignData(20, invoice.getItemdate().toString());
                    configLabel += subAlignRightData(20, invoice.getNettot());
                    configLabel += subAlignRightData(20, twoDecimalPoint(nettot));
                }else if (printertype.matches("3 Inch Bluetooth Generic")) {
                    configLabel += subAlignData(NUM_WIDTH,""+no);
                    configLabel += subAlignData(15, "" + invoice.getItemno().toString());
                    configLabel += subAlignData(10, invoice.getItemdate().toString());
                    configLabel += subAlignRightData(10, invoice.getNettot());
                    configLabel += subAlignRightData(10, twoDecimalPoint(nettot));
                }

                total += nettot;
                no++;
                configLabel += LINE_SEPARATOR;*/

                configLabel += subAlignData(NUM_WIDTH,""+ no);
                configLabel += subAlignData(INVOICENO,receipt.getItemno());
                configLabel += subAlignData(INVOICEDATE,receipt.getItemdate());
                configLabel += subAlignRightData(NETTOTAL,""+receipt.getNettot());
                configLabel += subAlignRightData(CREDIT_AMOUNT,receipt.getCreditAmount());
                configLabel += subAlignRightData(PAID_AMOUNT,receipt.getPaidamount());

                configLabel += LINE_SEPARATOR;

                no++;
                if(receipt.getNettot()!=null && !receipt.getNettot().isEmpty()){
                    dNetTotal += Double.valueOf(receipt.getNettot());
                }
                if(receipt.getPaidamount()!=null && !receipt.getPaidamount().isEmpty()){
                    dPaidAmt += Double.valueOf(receipt.getPaidamount());
                }
                if(receipt.getCreditAmount()!=null && !receipt.getCreditAmount().isEmpty()){
                    dCreditAmt += Double.valueOf(receipt.getCreditAmount());
                }

            }

            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            if (!In_Cash.getPay_Mode().matches("")
                    || !In_Cash.getPay_Mode().matches("null")
                    || !In_Cash.getPay_Mode().matches(null)) {

                String paymode = In_Cash.getPay_Mode();

                configLabel += subAlignDataLeft(RIGHT_SPACE, "PayMode");
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, " " + paymode);
                configLabel += LINE_SEPARATOR;

                Log.d("paymode.toLowerCase()",paymode.toLowerCase());
                if (paymode.equalsIgnoreCase("cheque")) {

                    String bank_Name = In_Cash.getBank_code();
                    String cheque_No = In_Cash.getCheck_No();
                    String cheque_Date = In_Cash.getCheck_Date();

                    configLabel += subAlignDataLeft(RIGHT_SPACE, "Bank Name");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + bank_Name);
                    configLabel += LINE_SEPARATOR;

                    configLabel += subAlignDataLeft(RIGHT_SPACE, "Cheque No");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + cheque_No);
                    configLabel += LINE_SEPARATOR;

                    configLabel += subAlignDataLeft(RIGHT_SPACE, "Cheque Date");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + cheque_Date);
                    configLabel += LINE_SEPARATOR;
                }
            }

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Paid Amount");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE, " " + twoDecimalPoint(dPaidAmt));
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Credit Amount");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE, " " + twoDecimalPoint(dCreditAmt));
            configLabel += LINE_SEPARATOR;

//                configLabel += subAlignDataLeft(RIGHT_SPACE, "NetTotal");
//                configLabel += subAlignDataRight(1, COLON);
//                configLabel += subAlignRightData(TOTAL_SPACE, " " + twoDecimalPoint(dNetTotal));
//                configLabel += LINE_SEPARATOR;

            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            /*configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Paid Amount");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE,twoDecimalPoint(total));
            configLabel += LINE_SEPARATOR;*/

        }


//        configLabel += horizontalLine("-");
//        configLabel += LINE_SEPARATOR;
        configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
        configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);


        // print next line if it is 3 Inch Bluetooth Generic printer
        if(printertype.matches("3 Inch Bluetooth Generic")){
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
            configLabel += subAlignDataLeftSpace(17, COLON + " " + user);

        }else if(printertype.matches("4 Inch Bluetooth")){
            configLabel += subAlignDataRight(13, " ");
            configLabel += subAlignDataRight(11, "Issued By : ");
            configLabel += subAlignDataLeftSpace(14, " " + user);
        }


        //  configLabel += subAlignDataRight(13, " ");
        //  configLabel += subAlignDataRight(11, "Issued By : ");
        //  configLabel += subAlignDataLeftSpace(14, " " + user);
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignCenter("Thank You");
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        System.out.println(configLabel);

        sendMessage(configLabel);

    }

    public void printExpense(String expenseno, String expensedate,
                                   List<ProductDetails> product, String title, int nofcopies)
            throws IOException, ZebraPrinterConnectionException {
        // Used the calculate the y axis printing position dynamically

        double totalqty = 0.00;
        String user = SupplierSetterGetter.getUsername();

        if(printertype.matches("4 Inch Bluetooth")) {
            byte[] send = new byte[3];
            //Title
            send[0] = 0x1b;
            send[1] = 0x61;
            send[2] = 0;
            GlobalData.mService.write(send);
        }

        String showCustomerCode = MobileSettingsSetterGetter
                .getShowCustomerCode();
        String showCustomerName = MobileSettingsSetterGetter
                .getShowCustomerName();
        String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
        String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
        String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
        String showCustomerPhone = MobileSettingsSetterGetter
                .getShowCustomerPhone();
        String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
        String showCustomerEmail = MobileSettingsSetterGetter
                .getShowCustomerEmail();
        String showCustomerTerms = MobileSettingsSetterGetter
                .getShowCustomerTerms();
        String showTotalOutstanding = MobileSettingsSetterGetter
                .getShowTotalOutstanding();
        String showProductFullName = MobileSettingsSetterGetter
                .getShowProductFullName();
        String showFooter = MobileSettingsSetterGetter.getShowFooter();
        String address1 = CustomerSetterGetter.getCustomerAddress1();
        String address2 = CustomerSetterGetter.getCustomerAddress2();
        String address3 = CustomerSetterGetter.getCustomerAddress3();
        String taxType = SalesOrderSetGet.getCompanytax();

//          String serverdateTime = SalesOrderSetGet.getServerDateTime();
        String localCurrency = SalesOrderSetGet.getLocalCurrency();

        String malaysiaShowGST  = SalesOrderSetGet.getMalaysiaShowGST();

        if(malaysiaShowGST!=null && !malaysiaShowGST.isEmpty()){

        }else{
            malaysiaShowGST="";
        }

        String companyTaxValue = SalesOrderSetGet.getCompanytaxvalue();
        if(companyTaxValue!=null && !companyTaxValue.isEmpty()){
            companyTaxValue = companyTaxValue.split("\\.")[0];
        }

        for (int n = 0; n < nofcopies; n++) {
            int y = 0;
            int s = 1;

            /*StringBuilder temp = new StringBuilder();
            y = printTitle(228, y, "EXPENSE", temp);
            // y = printCompanyDetails(y, temp);

            if (!Company.getCompanyName().equals("")) {
                temp.append(text(LEFT_MARGIN, y += LINE_SPACING,
                        Company.getCompanyName()));
            }

            String cpclConfigLabel = temp.toString();

            cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);*/


            configLabel += subAlignCenter("EXPENSE");
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;

            configLabel += printCompanyDetails();

//            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;


            if (!expenseno.matches("")) {
//                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
//                        "Expense No")
//                        + text(150, y, " : ")
//                        + text(180, y, expenseno);
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Expense No");
                configLabel += COLON + " " + expenseno;
                configLabel += LINE_SEPARATOR;
            }

            if (!expensedate.matches("")) {
//                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
//                        "Expense Date")
//                        + text(150, y, " : ")
//                        + text(180, y, expensedate);

                configLabel += subAlignDataLeft(RIGHT_SPACE, "Expense Date");
                configLabel += COLON + " " + expensedate;
                configLabel += LINE_SEPARATOR;
            }

            if (!user.matches("")) {
//                configLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "User"));
//                configLabel += (text(150, y, " : "));
//                configLabel += (text(180, y, user));
                configLabel += subAlignDataLeft(RIGHT_SPACE, "User");
                configLabel += COLON + " " + user;
                configLabel += LINE_SEPARATOR;
            }

//            cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
//            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
//            cpclConfigLabel += text(70, y, "Description");
//            cpclConfigLabel += text(476, y, "Amount");

//            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;


                configLabel += subAlignData(NUM_WIDTH,"No");
                configLabel += subAlignData(EXPENSE_DESCRIPTION_WIDTH,"Description");
                configLabel += subAlignRightData(NETTOTAL,"Amount");


            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            int i = 1;
            for (ProductDetails products : product) {

//                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
//                        String.valueOf(i));
//
//                cpclConfigLabel += text(
//                        70,
//                        y,
//                        (products.getDescription().length() > 31) ? products
//                                .getDescription().substring(0, 30) : products
//                                .getDescription());
//
//                double dAmt = Double.valueOf(products.getNettot());
//
//                cpclConfigLabel += text(486, y, twoDecimalPoint(dAmt));

                configLabel += subAlignData(NUM_WIDTH,""+ String.valueOf(i));

                configLabel += subAlignData(EXPENSE_DESCRIPTION_WIDTH,(products.getDescription().length() > EXPENSE_DESCRIPTION_WIDTH) ? products
                            .getDescription().substring(0, EXPENSE_DESCRIPTION_WIDTH -1) : products
                            .getDescription());

                double dAmt = Double.valueOf(products.getNettot());
                configLabel += subAlignRightData(NETTOTAL,""+twoDecimalPoint(dAmt));

                configLabel += LINE_SEPARATOR;

                if (products.getNettot() != null
                        && !products.getNettot().isEmpty()) {
                    totalqty += Double.valueOf(products.getNettot());
                }

                s += i;
                i++;

            }
            String tot_amt = twoDecimalPoint(totalqty);

//            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

//            cpclConfigLabel += text(300, y += LINE_SPACING, "Total Amount")
//                    + text(450, y, " : ")
//                    + text(486, y, String.valueOf(tot_amt));
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Amount");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE, " " + String.valueOf(tot_amt));
//            configLabel += LINE_SEPARATOR;

            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

//            // Just append everything and create a single string
//            cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1"
//                    + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
//
//            os.write(cpclConfigLabel.getBytes());
//            os.flush();
        }

//        os.close();

        configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
        configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);


        // print next line if it is 3 Inch Bluetooth Generic printer
        if(printertype.matches("3 Inch Bluetooth Generic")){
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
            configLabel += subAlignDataLeftSpace(17, COLON + " " + user);

        }else if(printertype.matches("4 Inch Bluetooth")){
            configLabel += subAlignDataRight(13, " ");
            configLabel += subAlignDataRight(11, "Issued By : ");
            configLabel += subAlignDataLeftSpace(14, " " + user);
        }


        //  configLabel += subAlignDataRight(13, " ");
        //  configLabel += subAlignDataRight(11, "Issued By : ");
        //  configLabel += subAlignDataLeftSpace(14, " " + user);
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignCenter("Thank You");
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        System.out.println(configLabel);

        sendMessage(configLabel);

    }



   /* // Delivery order on invoice
    public void DeliveryOnInvoicePrint(String invoiceno, String invoicedate,
                                       String customercode, String customername,
                                       List<ProductDetails> product, List<ProductDetails> productdet,
                                       List<String> printsortHeader, String gnrlStngs, int nofcopies,
                                       List<ProductDetails> product_batch, List<ProductDetails> footerArr){

        byte[] send=new byte[3];
        //Title
        send[0]=0x1b;
        send[1]=0x61;
        send[2]=0;
        GlobalData.mService.write(send);

        String showCustomerCode = MobileSettingsSetterGetter
                .getShowCustomerCode();
        String showCustomerName = MobileSettingsSetterGetter
                .getShowCustomerName();
        String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
        String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
        String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
        String showCustomerPhone = MobileSettingsSetterGetter
                .getShowCustomerPhone();
        String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
        String showCustomerEmail = MobileSettingsSetterGetter
                .getShowCustomerEmail();
        String showCustomerTerms = MobileSettingsSetterGetter
                .getShowCustomerTerms();
        String showFooter = MobileSettingsSetterGetter.getShowFooter();

        String address1 = CustomerSetterGetter.getCustomerAddress1();
        String address2 = CustomerSetterGetter.getCustomerAddress2();
        String address3 = CustomerSetterGetter.getCustomerAddress3();
        String user = SupplierSetterGetter.getUsername();
        for (int n = 0; n < nofcopies; n++) {

            String configLabel ="";
            double total = 0.00,totalqty=0;
            int no = 1;
            double nettot = 0,qty=0;
            int s = 1;
            configLabel += subAlignCenter("DELIVERY ORDER");
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;

            configLabel += printCompanyDetails();
            configLabel += LINE_SEPARATOR;

            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, INVOICE_NO);
            configLabel += COLON + " " + invoiceno;
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, INVOICE_DATE);
            configLabel += COLON + " " + invoicedate;
            configLabel += LINE_SEPARATOR;

            if (showCustomerCode.matches("True")) {

                if (!customercode.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, CUSTOMER_CODE);
                    configLabel += COLON + " " + customercode;
                    configLabel += LINE_SEPARATOR;
                }
            }
            if (showCustomerName.matches("True")) {

                if (!customername.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, CUSTOMER_NAME);
                    configLabel += COLON + " " + customername;
                    configLabel += LINE_SEPARATOR;

                }
            }
            if (showAddress1.matches("True")) {
                if (!address1.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, ADDRESS);
                    configLabel += COLON + " " + address1;
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showAddress2.matches("True")) {
                if (!address2.matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, "        ");
                    configLabel += " " + " " + address2;
                    configLabel += LINE_SEPARATOR;

                }
            }

            if (showAddress3.matches("True")) {
                if (!address3.matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, "        ");
                    configLabel += " " + " " + address3;
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showCustomerPhone.matches("True")) {
                if (!CustomerSetterGetter.getCustomerPhone().matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, PHONE_NO);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone();
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showCustomerHP.matches("True")) {
                if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, HEAD_PHONE);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP();
                    configLabel += LINE_SEPARATOR;

                }
            }

            if (showCustomerEmail.matches("True")) {
                if (!CustomerSetterGetter.getCustomerEmail().matches("")) {


                    configLabel += subAlignDataLeft(RIGHT_SPACE, EMAIL);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (showCustomerTerms.matches("True")) {
                if (!CustomerSetterGetter.getCustomerTerms().matches("")) {

                    configLabel += subAlignDataLeft(RIGHT_SPACE, TERM);
                    configLabel += COLON + " " + CustomerSetterGetter.getCustomerTerms();
                    configLabel += LINE_SEPARATOR;

                }
            }

            //  configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignData(5, "No");
            configLabel += subAlignData(65, "Description");
            configLabel += subAlignData(20, "Quantity");
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {
               for (ProductDetails products : product) {
              *//*   configLabel += subAlignData(5, "" + no);
                configLabel += subAlignData(25, invoice.getNo().toString());
                configLabel += subAlignData(25, (invoice.getName().length() > 25) ? invoice
                        .getName().substring(0, 24) : invoice
                        .getName());


                if (invoice.getNetTotal() != null && !invoice.getNetTotal().isEmpty()) {
                    nettot = Double.parseDouble(invoice.getNetTotal());
                }

                configLabel += subAlignRightData(25, twoDecimalPoint(nettot));

                total += nettot;
                no++;
                configLabel += LINE_SEPARATOR;
            }*//*

                if ((products.getSortproduct().matches(""))
                        || (products.getSortproduct().matches("0"))) {
                    int i = 1;

                    configLabel += subAlignData(5, String.valueOf(i));
                    configLabel += subAlignData(65, (products.getDescription().length() > 59) ? products
                            .getDescription().substring(0, 58) : products
                            .getDescription());

                    //showing qty
                    configLabel += subAlignRightData(7, products.getQty()
                            .toString());
                    configLabel += LINE_SEPARATOR;
                    if (products.getQty() != null
                            && !products.getQty().isEmpty()) {
                        totalqty += Double.valueOf(products.getQty());
                    }

                    if ((products.getIssueQty() != null && !products
                            .getIssueQty().isEmpty())
                            && (products.getReturnQty() != null && !products
                            .getReturnQty().isEmpty())) {

                        if ((Double.valueOf(products.getIssueQty()) > 0)
                                && (Double.valueOf(products.getReturnQty()) > 0)) {
                            configLabel += LINE_SEPARATOR;
                            configLabel += subAlignDataLeft(RIGHT_SPACE,"Issue")
                                    +subAlignDataLeftSpace(17, COLON +  " " + products.getIssueQty())
                                    +subAlignDataRight(13, " ");

                            configLabel += subAlignDataRight(11, "Return : ")
                                    + subAlignDataRight(5, " " +products.getReturnQty());
                            configLabel += LINE_SEPARATOR;
                        }
                    }

                    s += i;
                    i++;
                }
               }

                for (int i = 0; i < printsortHeader.size(); i++) {
                    String catorsub = printsortHeader.get(i).toString();
                    configLabel += subAlignData(10,
                            catorsub + " :");
                    for (ProductDetails prods : product) {
                        if (catorsub.matches(prods.getSortproduct().toString())) {

                            configLabel += subAlignData(5,
                                     String.valueOf(s));

                            configLabel += subAlignData(
                                    65,
                                    (prods.getDescription().length() > 51) ? prods
                                            .getDescription().substring(0, 50)
                                            : prods.getDescription());

                            //showing qty
                            configLabel += subAlignRightData(7, prods.getQty()
                                    .toString());
                            configLabel += LINE_SEPARATOR;
                            if (prods.getQty() != null
                                    && !prods.getQty().isEmpty()) {
                                totalqty += Double.valueOf(prods.getQty());
                            }
                            // }
                            if ((prods.getIssueQty() != null && !prods
                                    .getIssueQty().isEmpty())
                                    && (prods.getReturnQty() != null && !prods
                                    .getReturnQty().isEmpty())) {

                                if ((Double.valueOf(prods.getIssueQty()) > 0)
                                        && (Double
                                        .valueOf(prods.getReturnQty()) > 0)) {
                                    configLabel += LINE_SEPARATOR;
                                    configLabel += subAlignDataLeft(RIGHT_SPACE,"Issue")
                                            +subAlignDataLeftSpace(17, COLON +  " " + prods.getIssueQty())
                                            +subAlignDataRight(13, " ");

                                    configLabel += subAlignDataRight(11, "Return : ")
                                            + subAlignDataRight(5, " " +prods.getReturnQty());
                                    configLabel += LINE_SEPARATOR;
                                }
                            }

                            s++;

                        }

                    }
                }
            } else {
                for (ProductDetails products : product) {
                    configLabel += subAlignData(5,
                            products.getSno().toString());

                    configLabel += subAlignData(65, (products.getDescription()
                            .length() > 51) ? products.getDescription()
                            .substring(0, 50) : products.getDescription());

                    //showing qty
                    configLabel += subAlignRightData(7, products.getQty()
                            .toString());
                    configLabel += LINE_SEPARATOR;
                    if (products.getQty() != null
                            && !products.getQty().isEmpty()) {
                        totalqty += Double.valueOf(products.getQty());
                    }
                    // }
                    if ((products.getIssueQty() != null && !products
                            .getIssueQty().isEmpty())
                            && (products.getReturnQty() != null && !products
                            .getReturnQty().isEmpty())) {

                        if ((Double.valueOf(products.getIssueQty()) > 0)
                                && (Double.valueOf(products.getReturnQty()) > 0)) {
                            configLabel += LINE_SEPARATOR;
                            configLabel += subAlignDataLeft(RIGHT_SPACE,"Issue")
                                    +subAlignDataLeftSpace(17, COLON +  " " + products.getIssueQty())
                                    +subAlignDataRight(13, " ");

                            configLabel += subAlignDataRight(11, "Return : ")
                                    + subAlignDataRight(5, " " +products.getReturnQty());
                            configLabel += LINE_SEPARATOR;
                        }
                    }

                }
            }

            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Quantity");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(54, twoDecimalPoint(totalqty).split("\\.")[0]);
            configLabel += LINE_SEPARATOR;

            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
            configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);
            configLabel += subAlignDataRight(13, " ");
            configLabel += subAlignDataRight(11, "Issued By : ");
            configLabel += subAlignDataLeftSpace(14, " " + user);
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignCenter("Thank You");
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            System.out.println(configLabel);
        }
        sendMessage(configLabel);


    }*/
   // Delivery order on invoice
   public void DeliveryOnInvoicePrint(String invoiceno, String invoicedate,
                                       String customercode, String customername,
                                       List<ProductDetails> product, List<ProductDetails> productdet,
                                       List<String> printsortHeader, String gnrlStngs, int nofcopies,
                                       List<ProductDetails> product_batch, List<ProductDetails> footerArr) {


       try {
           String configLabel = "";

           String showCustomerCode = MobileSettingsSetterGetter
                   .getShowCustomerCode();
           String showCustomerName = MobileSettingsSetterGetter
                   .getShowCustomerName();
           String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
           String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
           String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
           String showCustomerPhone = MobileSettingsSetterGetter
                   .getShowCustomerPhone();
           String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
           String showCustomerEmail = MobileSettingsSetterGetter
                   .getShowCustomerEmail();
           String showCustomerTerms = MobileSettingsSetterGetter
                   .getShowCustomerTerms();
           String showTotalOutstanding = MobileSettingsSetterGetter
                   .getShowTotalOutstanding();
           String showProductFullName = MobileSettingsSetterGetter.getShowProductFullName();
           String showFooter = MobileSettingsSetterGetter.getShowFooter();
           String address1 = CustomerSetterGetter.getCustomerAddress1();
           String address2 = CustomerSetterGetter.getCustomerAddress2();
           String address3 = CustomerSetterGetter.getCustomerAddress3();


           String user = SupplierSetterGetter.getUsername();
//            String serverdateTime = SalesOrderSetGet.getServerDateTime();
           String localCurrency = SalesOrderSetGet.getLocalCurrency();

           String companyTaxValue = SalesOrderSetGet.getCompanytaxvalue();
           if (companyTaxValue != null && !companyTaxValue.isEmpty()) {
               companyTaxValue = companyTaxValue.split("\\.")[0];
           }


           byte[] send = new byte[3];
           //Title
           send[0] = 0x1b;
           send[1] = 0x61;
           send[2] = 0;
           GlobalData.mService.write(send);

           for (int n = 0; n < nofcopies; n++) {


               double totalqty = 0.00;

               configLabel += subAlignCenter("DELIVERY ORDER");
               configLabel += LINE_SEPARATOR;
               configLabel += LINE_SEPARATOR;

               configLabel += printCompanyDetails();

               configLabel += horizontalLine("-");
               configLabel += LINE_SEPARATOR;


               configLabel += subAlignDataLeft(RIGHT_SPACE, INVOICE_NO);
               configLabel += COLON + " " + invoiceno;
               configLabel += LINE_SEPARATOR;
               configLabel += subAlignDataLeft(RIGHT_SPACE, INVOICE_DATE);
               configLabel += COLON + " " + invoicedate;
               configLabel += LINE_SEPARATOR;

               if (showCustomerCode.matches("True")) {

                   if (!customercode.matches("")) {
                       configLabel += subAlignDataLeft(RIGHT_SPACE, CUSTOMER_CODE);
                       configLabel += COLON + " " + customercode;
                       configLabel += LINE_SEPARATOR;
                   }
               }
               if (showCustomerName.matches("True")) {

                   if (!customername.matches("")) {
                       configLabel += subAlignDataLeft(RIGHT_SPACE, CUSTOMER_NAME);
                       configLabel += COLON + " " + customername;
                       configLabel += LINE_SEPARATOR;

                   }
               }
               if (showAddress1.matches("True")) {
                   if (!address1.matches("")) {
                       configLabel += subAlignDataLeft(RIGHT_SPACE, ADDRESS);
                       configLabel += COLON + " " + address1;
                       configLabel += LINE_SEPARATOR;
                   }
               }

               if (showAddress2.matches("True")) {
                   if (!address2.matches("")) {

                       configLabel += subAlignDataLeft(RIGHT_SPACE, "        ");
                       configLabel += " " + " " + address2;
                       configLabel += LINE_SEPARATOR;

                   }
               }

               if (showAddress3.matches("True")) {
                   if (!address3.matches("")) {
                       configLabel += subAlignDataLeft(RIGHT_SPACE, "        ");
                       configLabel += " " + " " + address3;
                       configLabel += LINE_SEPARATOR;
                   }
               }

               if (showCustomerPhone.matches("True")) {
                   if (!CustomerSetterGetter.getCustomerPhone().matches("")) {

                       configLabel += subAlignDataLeft(RIGHT_SPACE, PHONE_NO);
                       configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone();
                       configLabel += LINE_SEPARATOR;
                   }
               }

               if (showCustomerHP.matches("True")) {
                   if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                       configLabel += subAlignDataLeft(RIGHT_SPACE, HEAD_PHONE);
                       configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP();
                       configLabel += LINE_SEPARATOR;

                   }
               }

               if (showCustomerEmail.matches("True")) {
                   if (!CustomerSetterGetter.getCustomerEmail().matches("")) {


                       configLabel += subAlignDataLeft(RIGHT_SPACE, EMAIL);
                       configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                       configLabel += LINE_SEPARATOR;
                   }
               }

               if (showCustomerTerms.matches("True")) {
                   if (!CustomerSetterGetter.getCustomerTerms().matches("")) {

                       configLabel += subAlignDataLeft(RIGHT_SPACE, TERM);
                       configLabel += COLON + " " + CustomerSetterGetter.getCustomerTerms();
                       configLabel += LINE_SEPARATOR;

                   }
               }

               configLabel += horizontalLine("-");
               configLabel += LINE_SEPARATOR;
               configLabel += subAlignData(5, "No");
               configLabel += subAlignData(65, "Description");
               configLabel += subAlignData(20, "Quantity");
               configLabel += LINE_SEPARATOR;
               configLabel += horizontalLine("-");
               configLabel += LINE_SEPARATOR;

               for (ProductDetails products : product) {
                   configLabel += subAlignData(5, products.getSno().toString());

                   configLabel += subAlignData(65, (products.getDescription()
                           .length() > 51) ? products.getDescription()
                           .substring(0, 50) : products.getDescription());

                   configLabel += subAlignRightData(7, products.getQty().toString());

                   if ((products.getIssueQty() != null && !products
                           .getIssueQty().isEmpty())
                           && (products.getReturnQty() != null && !products
                           .getReturnQty().isEmpty())) {

                       if ((Double.valueOf(products.getIssueQty()) > 0)
                               && (Double.valueOf(products.getReturnQty()) > 0)) {
                           configLabel += LINE_SEPARATOR;
                           configLabel += subAlignDataLeft(RIGHT_SPACE, "Issue")
                                   + subAlignDataLeftSpace(17, COLON + " " + products.getIssueQty())
                                   + subAlignDataRight(13, " ");

                           configLabel += subAlignDataRight(11, "Return : ")
                                   + subAlignDataRight(5, " " + products.getReturnQty());
                       }
                   }
                   if (products.getQty() != null
                           && !products.getQty().isEmpty()) {
                       totalqty += Double.valueOf(products.getQty());
                   }
                   configLabel += LINE_SEPARATOR;
               }
               configLabel += horizontalLine("-");
               configLabel += LINE_SEPARATOR;

               configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Quantity");
               configLabel += subAlignDataRight(1, COLON);
               configLabel += subAlignRightData(54, twoDecimalPoint(totalqty).split("\\.")[0]);
               configLabel += LINE_SEPARATOR;

               configLabel += horizontalLine("-");
               configLabel += LINE_SEPARATOR;
               configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
               configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);
               configLabel += subAlignDataRight(13, " ");
               configLabel += subAlignDataRight(11, "Issued By : ");
               configLabel += subAlignDataLeftSpace(14, " " + user);
               configLabel += LINE_SEPARATOR;
               configLabel += horizontalLine("-");
               configLabel += LINE_SEPARATOR;
               configLabel += subAlignCenter("Thank You");
               configLabel += LINE_SEPARATOR;
               configLabel += LINE_SEPARATOR;
               configLabel += LINE_SEPARATOR;
               configLabel += LINE_SEPARATOR;
               configLabel += LINE_SEPARATOR;
               System.out.println(configLabel);
           }
           sendMessage(configLabel);

       } catch (Exception e) {
           helper.showErrorDialog(e.getMessage());
       }
   }
    private  String subAlignCenter(String data){
        int length = (PAPER_WIDTH-data.length())/2;
        //	System.out.println(length);
        // Create a new StringBuilder.
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }

        // Convert to string.
        String result = builder.toString()+data;
        // Print result.
        //	System.out.println(result);
        // Print result.
        return result;
    }
    private  String horizontalLine(String data){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < PAPER_WIDTH; i++)
        {
            builder.append(data);
        }
        String result = builder.toString();
        return result;
    }
    private static String horizontalLineSpace(String data){
        StringBuilder builderLine = new StringBuilder();
        for(int i=0; i < 41; i++)
        {
            builderLine.append(data);
        }
        String result1 = builderLine.toString();
        int length = (PAPER_WIDTH-result1.length())/2;
        //System.out.println(length);
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = builder.toString()+result1;
        return result;
    }
    private  String subAlignData(int size,String data){
        int length = (size-data.length());
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = data+builder.toString();
        return result;
    }
    private  String subAlignRightData(int size,String data){
        int length = (size-data.length());
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = builder.toString()+data;
        return result;
    }

    private  String subAlignDataBtSpace(String data){

        StringBuilder builder = new StringBuilder();
        for(int i=0; i < 5; i++)
        {
            builder.append(" ");
        }
        String result = builder.toString()+data+builder.toString();
        return result;
    }


    private  String subAlignDataLeftSpace(int size,String data){
        int length = size - data.length();
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }

        String result = data+builder.toString();
        return result;
    }

    private  String subAlignDataLeft(int size,String data){
        int length = (size-data.length());
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = data+builder.toString();
        return result;
    }

    private  String subAlignDataRight(int size,String data){
        int length = (size-data.length());
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = builder.toString()+data;
        return result;
    }
    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }
    public String concatenateStr(String data){
        String firstStr ="",lastStr="";
        StringBuilder builder = new StringBuilder();
        if (data.length()>29){
            firstStr = data.substring(0, 29);
            lastStr = data.substring(29, data.length());
            for(int i=0; i < CONCATENATE_LENGTH; i++)
            {
                builder.append(" ");
            }
        }
        String result = firstStr+LINE_SEPARATOR+builder.toString()+lastStr;
        System.out.format(result);
        return result;
    }
    private void sendMessage(final String message)
    {
        if(printertype.matches("4 Inch Bluetooth")) {
            try {
                // helper.updateProgressDialog(R.string.connecting_to_printer);
                // Check that we're actually connected before trying anything

                if (GlobalData.mService.getState() != GlobalData.STATE_CONNECTED)
                {
                    Toast.makeText(context, "Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (message.length() > 0) {
                    // Get the message bytes and tell the BluetoothService to write
                    byte [] send ;
                    try {
                        send = message.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        send = message.getBytes();

                    }

                    GlobalData.mService.write(send);

                }
            } catch (Exception e) {
                helper.showErrorDialog(e.getMessage());
            }
            finally {
                // if(flag){
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onCompleted();
                                    if(GlobalData.mService!=null){
                                        GlobalData.mService.stop();
                                    }
                                }
                            }
                        });
                    }
                },1500);
            }
        }else if(printertype.matches("3 Inch Bluetooth Generic")) {
            try{
                mPrinter.reset();
                mPrinter.printTaggedText(message.toString());
                mPrinter.feedPaper(110);
                mPrinter.flush();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (listener != null) {
                                        listener.onCompleted();
                                    }
                                    closeBluetoothConnection();
                                    closePrinterConnection();
                                  /*  if (mPrinter != null) {
                                        mPrinter.close();
                                    }
                                    if (mProtocolAdapter != null) {
                                        mProtocolAdapter.close();
                                    }
                                    if (mBtSocket != null) {
                                        mBtSocket.close();
                                    }*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                },1500);
            }
        }
    }

    private String printCompanyDetails() {
         String configLabel ="";
        String start ="^XA^POI^A0N,25,25^FD";
        String end = "^FS^XZ";
        String showaddress1 = MobileSettingsSetterGetter.getShowAddress1();
        String showaddress2 = MobileSettingsSetterGetter.getShowAddress2();
        String showaddress3 = MobileSettingsSetterGetter.getShowAddress3();
        String showcountrypostal = MobileSettingsSetterGetter
                .getShowCountryPostal();
        String showphone = MobileSettingsSetterGetter.getShowPhone();

        String showfax = MobileSettingsSetterGetter.getShowFax();
        String showemail = MobileSettingsSetterGetter.getShowEmail();
        String showtaxregno = MobileSettingsSetterGetter.getShowTaxRegNo();
        String showbusregno = MobileSettingsSetterGetter.getShowBizRegNo();

        String country = Company.getCountry();
        String phoneno = Company.getPhoneNo();
        String zipcode = Company.getZipCode();
        String countryZipcode = country + " " + zipcode;

        String fax = Company.getFax();
        String email = Company.getEmail();
        String taxregno = Company.getTaxRegNo();
        String busregno = Company.getBusinessRegNo();

        String showuserphoneno = MobileSettingsSetterGetter.getShowUserPhoneNo();

        String loginphoneno  = SalesOrderSetGet.getLoginPhoneNo();

        String username = SupplierSetterGetter.getUsername();



        if (!Company.getCompanyName().equals("")) {

            if (showbusregno != null && !showbusregno.isEmpty()) {
                if (showbusregno.matches("True")) {
                    if (!busregno.equals("")) {
                       // configLabel += subAlignCenter(busregno);
                      //  configLabel += LINE_SEPARATOR;
                        configLabel += start + subAlignCenter(Company.getCompanyName()+", "+"("+busregno+")");
                    }else{
                        configLabel += start +subAlignCenter(Company.getCompanyName());
                    }
                }else{
                    configLabel +=start + subAlignCenter(Company.getCompanyName());
            }
            }

            configLabel += end;
            configLabel += LINE_SEPARATOR;
        }
        if (showaddress1.matches("True")&&(showaddress2.matches("True"))&&(showaddress3.matches("True"))) {
            if (!Company.getAddress1().equals("")&&(!Company.getAddress2().equals(""))&&!Company.getAddress3().equals("")) {
                configLabel += start + subAlignCenter(Company.getAddress1() + ", " +Company.getAddress2() + ", " + Company.getAddress3()) ;
            }else if (!Company.getAddress1().equals("") && (!Company.getAddress2().equals("")) && Company.getAddress3().equals("")) {
                configLabel += start +subAlignCenter(Company.getAddress1() + ", " +Company.getAddress2());
            }else  if (!Company.getAddress1().equals("") && (Company.getAddress2().equals("")) && Company.getAddress3().equals("")) {
                configLabel += start +subAlignCenter(Company.getAddress1());
            }
            configLabel += end;
            configLabel += LINE_SEPARATOR;

        }
         else if (showaddress1.matches("True")&&(showaddress2.matches("True"))&&(showaddress3.matches("False"))) {
            if (!Company.getAddress1().equals("")&&(!Company.getAddress2().equals(""))) {
                configLabel += start +subAlignCenter(Company.getAddress1() + ", " + Company.getAddress2());
            }else if (!Company.getAddress1().equals("") && (Company.getAddress2().equals(""))) {
                configLabel += start +subAlignCenter(Company.getAddress1());
            }
            configLabel += end;
            configLabel += LINE_SEPARATOR;
            }
        else if (showaddress1.matches("True")&&(showaddress2.matches("False"))&&(showaddress3.matches("False"))) {
            if (!Company.getAddress1().equals("")) {
                configLabel += start +subAlignCenter(Company.getAddress1());
            }
            configLabel += end;
            configLabel += LINE_SEPARATOR;
        }
       /* if (showaddress1.matches("True")) {
            if (!Company.getAddress1().equals("")) {
                configLabel += subAlignCenter(Company.getAddress1());
                configLabel += LINE_SEPARATOR;
            }
        }

        if (showaddress2.matches("True")) {
            if (!Company.getAddress2().equals("")) {
                configLabel += subAlignCenter(Company.getAddress2());
                configLabel += LINE_SEPARATOR;
            }
        }

        if (showaddress3.matches("True")) {
            if (!Company.getAddress3().equals("")) {
                configLabel += subAlignCenter( Company.getAddress3());
                configLabel += LINE_SEPARATOR;
            }
        }
*/

        if (showcountrypostal.matches("True")) {
            if (!country.equals("")) {

                if (!zipcode.equals("")) {
                    configLabel += start +subAlignCenter(countryZipcode)+end;
                    configLabel += LINE_SEPARATOR;
                } else {
                    configLabel += start +subAlignCenter(country)+end;
                    configLabel += LINE_SEPARATOR;

                }
            }
        }

        if ((showphone.matches("True")) && (showfax.matches("True"))) {
            if ((!phoneno.equals("")) && (!fax.equals(""))) {
                configLabel += start +subAlignCenter("Tel "+": "+phoneno+" "+"Fax "+": "+fax);
            }else  if ((!phoneno.equals("")) && (fax.equals(""))) {
                configLabel += start +subAlignCenter("Tel "+": "+phoneno);
            }else  if ((phoneno.equals("")) && (!fax.equals(""))) {
                configLabel += start +subAlignCenter("Fax "+": "+fax);
            }
            configLabel += end;
            configLabel += LINE_SEPARATOR;
        }else if ((showphone.matches("True")) && (showfax.matches("False"))) {
            if ((!phoneno.equals(""))) {
                configLabel += start +subAlignCenter("Tel "+": "+phoneno);
            }
            configLabel += end;
            configLabel += LINE_SEPARATOR;
        }
        else if ((showphone.matches("False")) && (showfax.matches("True"))) {
            if (!fax.equals("")) {
                configLabel += start +subAlignCenter("Fax "+": "+fax);
            }
            configLabel += end;
            configLabel += LINE_SEPARATOR;
        }

      /*  if (showphone.matches("True")) {
            if (!phoneno.equals("")) {
                configLabel += subAlignCenter(phoneno);
                configLabel += LINE_SEPARATOR;
            }
        }

        if (showfax != null && !showfax.isEmpty()) {
            if (showfax.matches("True")) {
                if (!fax.equals("")) {
                    configLabel +=  subAlignCenter(fax);
                    configLabel += LINE_SEPARATOR;
                }
            }

        }*/

        if (showemail != null && !showemail.isEmpty()) {
            if (showemail.matches("True")) {
                if (!email.equals("")) {
                    configLabel += start +subAlignCenter(email)+end;
                    configLabel += LINE_SEPARATOR;
                }
            }
        }

        if (showtaxregno != null && !showtaxregno.isEmpty()) {
            if (showtaxregno.matches("True")) {
                if (!taxregno.equals("")) {
                    configLabel += start +subAlignCenter("GST ID No "+": "+taxregno)+end;
                    configLabel += LINE_SEPARATOR;
                }
            }
        }

       /* if (showbusregno != null && !showbusregno.isEmpty()) {
            if (showbusregno.matches("True")) {
                if (!busregno.equals("")) {
                    configLabel += subAlignCenter(busregno);
                    configLabel += LINE_SEPARATOR;
                }
            }
        }*/

        return configLabel;
    }

    public String getServerDateTime() {
        String serverdateTime = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            if (onlineMode.matches("True")) {
                if (checkOffline == true) {
                    Log.d("Offline Temporary", "Offline");
//                    serverdateTime = DateFormat.getDateTimeInstance().format(new Date());

                    serverdateTime = sdf.format(cal.getTime());
                    System.out.println("Current date in String Format: " + serverdateTime);

                    return serverdateTime;
                } else {
                    Log.d("Online", "Online");
                    serverdateTime = DateWebservice.getDateService("fncGetServerDateTime");
                    return serverdateTime;
                }

            } else if (onlineMode.matches("False")) {
                Log.d("Offline Permanent", "Offline");
//                serverdateTime = DateFormat.getDateTimeInstance().format(new Date());
                serverdateTime = sdf.format(cal.getTime());
                System.out.println("Current date in String Format: " + serverdateTime);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return serverdateTime;
    }

    /// extra methods

    private void error(final String text) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }
    protected void initPrinter(InputStream inputStream, OutputStream outputStream)
            throws IOException {
//        Log.d(LOG_TAG, "Initialize printer...");

        // Here you can enable various debug information
        //ProtocolAdapter.setDebug(true);
        Printer.setDebug(true);
        EMSR.setDebug(true);

        // Check if printer is into protocol mode. Ones the object is created it can not be released
        // without closing base streams.
        mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (mProtocolAdapter.isProtocolEnabled()) {
//            Log.d(LOG_TAG, "Protocol mode is enabled");

            // Into protocol mode we can callbacks to receive printer notifications
            mProtocolAdapter.setPrinterListener(new ProtocolAdapter.PrinterListener() {
                @Override
                public void onThermalHeadStateChanged(boolean overheated) {
                    if (overheated) {
//                        Log.d(LOG_TAG, "Thermal head is overheated");
                        status("OVERHEATED");
                    } else {
                        status(null);
                    }
                }

                @Override
                public void onPaperStateChanged(boolean hasPaper) {
                    if (hasPaper) {
//                        Log.d(LOG_TAG, "Event: Paper out");
                        status("PAPER OUT");
                    } else {
                        status(null);
                    }
                }

                @Override
                public void onBatteryStateChanged(boolean lowBattery) {
                    if (lowBattery) {
//                        Log.d(LOG_TAG, "Low battery");
                        status("LOW BATTERY");
                    } else {
                        status(null);
                    }
                }
            });

/*        mProtocolAdapter.setBarcodeListener(new ProtocolAdapter.BarcodeListener() {
            @Override
            public void onReadBarcode() {
                Log.d(LOG_TAG, "On read barcode");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        readBarcode(0);
                    }
                });
            }
        });

        mProtocolAdapter.setCardListener(new ProtocolAdapter.CardListener() {
            @Override
            public void onReadCard(boolean encrypted) {
                Log.d(LOG_TAG, "On read card(entrypted=" + encrypted + ")");

                if (encrypted) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            readCardEncrypted();
                        }
                    });
                } else {
                	context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            readCard();
                        }
                    });
                }
            }
        });*/

            // Get printer instance
            mPrinterChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
            mPrinter = new Printer(mPrinterChannel.getInputStream(), mPrinterChannel.getOutputStream());

            // Check if printer has encrypted magnetic head
            ProtocolAdapter.Channel emsrChannel = mProtocolAdapter
                    .getChannel(ProtocolAdapter.CHANNEL_EMSR);
            try {
                // Close channel silently if it is already opened.
                try {
                    emsrChannel.close();
                } catch (IOException e) {
                }

                // Try to open EMSR channel. If method failed, then probably EMSR is not supported
                // on this device.
                emsrChannel.open();

                mEMSR = new EMSR(emsrChannel.getInputStream(), emsrChannel.getOutputStream());
                EMSR.EMSRKeyInformation keyInfo = mEMSR.getKeyInformation(EMSR.KEY_AES_DATA_ENCRYPTION);
                if (!keyInfo.tampered && keyInfo.version == 0) {
//                    Log.d(LOG_TAG, "Missing encryption key");
                    // If key version is zero we can load a new key in plain mode.
                    byte[] keyData = CryptographyHelper.createKeyExchangeBlock(0xFF,
                            EMSR.KEY_AES_DATA_ENCRYPTION, 1, CryptographyHelper.AES_DATA_KEY_BYTES,
                            null);
                    mEMSR.loadKey(keyData);
                }
                mEMSR.setEncryptionType(EMSR.ENCRYPTION_TYPE_AES256);
                mEMSR.enable();
//                Log.d(LOG_TAG, "Encrypted magnetic stripe reader is available");
            } catch (IOException e) {
                if (mEMSR != null) {
                    mEMSR.close();
                    mEMSR = null;
                }
            }

            // Check if printer has encrypted magnetic head
            ProtocolAdapter.Channel rfidChannel = mProtocolAdapter
                    .getChannel(ProtocolAdapter.CHANNEL_RFID);

            try {
                // Close channel silently if it is already opened.
                try {
                    rfidChannel.close();
                } catch (IOException e) {
                }

                // Try to open RFID channel. If method failed, then probably RFID is not supported
                // on this device.
                rfidChannel.open();

                mRC663 = new RC663(rfidChannel.getInputStream(), rfidChannel.getOutputStream());
                mRC663.setCardListener(new RC663.CardListener() {
                    @Override
                    public void onCardDetect(ContactlessCard card) {
                        // processContactlessCard(card);
                    }
                });
                mRC663.enable();
//                Log.d(LOG_TAG, "RC663 reader is available");
            } catch (IOException e) {
                if (mRC663 != null) {
                    mRC663.close();
                    mRC663 = null;
                }
            }

            // Check if printer has encrypted magnetic head
            mUniversalChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_UNIVERSAL_READER);
            new UniversalReader(mUniversalChannel.getInputStream(), mUniversalChannel.getOutputStream());

        } else {
//            Log.d(LOG_TAG, "Protocol mode is disabled");

            // Protocol mode it not enables, so we should use the row streams.
            mPrinter = new Printer(mProtocolAdapter.getRawInputStream(),
                    mProtocolAdapter.getRawOutputStream());
        }

        if (initListener != null) {
            initListener.initCompleted();
        }
        mPrinter.setConnectionListener(new Printer.ConnectionListener() {
            @Override
            public void onDisconnect() {
                // toast("Printer is disconnected");

            /*((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        waitForConnection();
                    }
                }
            });*/
            }
        });
    }

    private void toast(final String text) {
//        Log.d(LOG_TAG, text);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void initConnection() {
        closeActiveConnection();
        if (BluetoothAdapter.checkBluetoothAddress(macAddress)) {
            establishBluetoothConnection(macAddress);
        } else {
            establishNetworkConnection(macAddress);
        }
    }
    private void establishBluetoothConnection(final String address) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(R.string.title_please_wait));
        dialog.setMessage(context.getString(R.string.connecting_to_printer));
        dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_spinner));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        closePrinterServer();

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.d(LOG_TAG, "Connecting to " + address + "...");
                Looper.prepare();
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);

                    InputStream in = null;
                    OutputStream out = null;

                    try {
                        BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                        btSocket.connect();

                        mBtSocket = btSocket;
                        in = mBtSocket.getInputStream();
                        out = mBtSocket.getOutputStream();
                    } catch (IOException e) {
                        error("FAILED to connect: " + e.getMessage());

                        waitForConnection();
                        return;
                    }

                    try {
                        initPrinter(in, out);
                    } catch (IOException e) {
                        error("FAILED to initiallize: " + e.getMessage());
                        return;
                    }
                } finally {
                    dialog.dismiss();
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        });
        t.start();
    }
    private void waitForConnection() {
        // status(null);

        closeActiveConnection();

        // Start server to listen for network connection.
        try {
      /*  mPrinterServer = new PrinterServer(new PrinterServerListener() {
            @Override
            public void onConnect(Socket socket) {
                Log.d(LOG_TAG, "Accept connection from "
                        + socket.getRemoteSocketAddress().toString());



                mNetSocket = socket;
                try {
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();
                    initPrinter(in, out);
                } catch (IOException e) {
                    e.printStackTrace();
                    error("FAILED to initialize: " + e.getMessage());
                    waitForConnection();
                }
            }
        });*/
            if (BluetoothAdapter.checkBluetoothAddress(macAddress)) {
                establishBluetoothConnection(macAddress);
            } else {
                establishNetworkConnection(macAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void status(final String text) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (text != null) {
                } else {
                }
            }
        });
    }
    private void establishNetworkConnection(final String address) {
        //  closePrinterServer();

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(R.string.title_please_wait));
        dialog.setMessage(context.getString(R.string.connecting_to_printer));
        dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_spinner));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // closePrinterServer();

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.d(LOG_TAG, "Connectiong to " + address + "...");
                Looper.prepare();
                try {
                    Socket s = null;
                    try {
                        String[] url = address.split(":");
                        int port = DEFAULT_NETWORK_PORT;

                        try {
                            if (url.length > 1) {
                                port = Integer.parseInt(url[1]);
                            }
                        } catch (NumberFormatException e) {
                        }

                        s = new Socket(url[0], port);
                        s.setKeepAlive(true);
                        s.setTcpNoDelay(true);
                    } catch (UnknownHostException e) {
                        error("FAILED to connect: " + e.getMessage());
                        waitForConnection();
                        return;
                    } catch (IOException e) {
                        error("FAILED to connect: " + e.getMessage());
                        waitForConnection();
                        return;
                    }

                    InputStream in = null;
                    OutputStream out = null;

                    try {
                        mNetSocket = s;
                        in = mNetSocket.getInputStream();
                        out = mNetSocket.getOutputStream();
                    } catch (IOException e) {
                        error("FAILED to connect: " + e.getMessage());
                        waitForConnection();
                        return;
                    }

                    try {
                        initPrinter(in, out);
                    } catch (IOException e) {
                        error("FAILED to initiallize: " + e.getMessage());
                        return;
                    }
                } finally {
                    dialog.dismiss();
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        });
        t.start();
    }

    private void closeNetworkConnection() {
        // Close network connection
        Socket s = mNetSocket;
        mNetSocket = null;
        if (s != null) {
//            Log.d(LOG_TAG, "Close Network socket");
            try {
                s.shutdownInput();
                s.shutdownOutput();
                s.close();
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
    }

    private void closePrinterServer() {
        closeNetworkConnection();

        // Close network server
        PrinterServer ps = mPrinterServer;
        mPrinterServer = null;
        if (ps != null) {
//            Log.d(LOG_TAG, "Close Network server");
            try {
                ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeActiveConnection() {
        try {
            closePrinterConnection();
            closeBluetoothConnection();
            closeNetworkConnection();
            closePrinterServer();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
