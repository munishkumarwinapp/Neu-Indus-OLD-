package com.winapp.printer;

/**
 * Created by user on 10-Mar-17.
 *
 * Zebra 4 Inch Print
 *
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.winapp.SFA.R;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.Product;
import com.winapp.model.Receipt;
import com.winapp.sot.Company;
import com.winapp.sot.In_Cash;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ProdDetails;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.sotdetails.ProductStockGetSet;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.winapp.SFA.R;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.Product;
import com.winapp.model.Receipt;
import com.winapp.sot.Company;
import com.winapp.sot.In_Cash;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ProdDetails;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.sotdetails.ProductStockGetSet;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

public class PrinterZPL {
    public interface OnCompletionListener {
        public void onCompleted();
    }

    private Context context;
    private UIHelper helper;
    private String macAddress;
    private ZebraPrinter printer;
    private BluetoothAdapter bluetoothAdapter;
    // private SettingsManager settings;
    private static final String FILE_NAME = "TEMP.LBL";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String LINEBREAK = "\r\b";


    private static final int LABEL_WIDTH = 574;
    //private static final int FONT = 5;
  //  private static final int FONT_SIZE = 0;
    private static final int LEFT_MARGIN = 10;
    private static final int RIGHT_MARGIN = 564;
    private static final int LINE_THICKNESS = 3;
    private static final int LINE_SPACING = 40;
    private static final String CMD_TEXT = "^FO";
    private static final String CMD_TEXT_END = "^FS";
    private static final String ZPL_CMD_START = "^XA^CFD^CI28^POI^LL";
    private static final String ZPL_CMD_END = "^XZ";
    private static final String FONT = "^A@N,25,25,E:MSUNG.TTF";
    private static final String CMD_TEXT_START = "^FD";
    private static final String CMD_LINE = "L";
    private static final String CMD_PRINT = "PRINT" + LINE_SEPARATOR;
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final int POSTFEED = 50;
    private static final String HORIZONTAL_LINE =  "^GB700";
    private Connection connection = null;
    String logoStr = "",mUser="";
    List<ProductDetails> footerArr = new ArrayList<ProductDetails>();
    private static final String HEADER = "PW " + LABEL_WIDTH + LINE_SEPARATOR
            + "TONE 0" + LINE_SEPARATOR + "SPEED 3" + LINE_SEPARATOR + "ZPL"
            + POSTFEED + LINE_SEPARATOR + "NO-PACE" + "BAR-SENSE"
            + LINE_SEPARATOR;
    byte FONT_TYPE;
    private OnCompletionListener listener;
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

    public PrinterZPL(Context context, String macAddress) {
        if (!macAddress
                .matches("[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
            throw new IllegalArgumentException(macAddress
                    + context.getString(R.string.is_not_valid_mac_address));
        }
        this.context = context;
        this.macAddress = macAddress;
        helper = new UIHelper(context);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }

    private void print() {
        Log.d("logoStr", "......" + logoStr);

        helper.updateProgressDialog(R.string.connecting_to_printer);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
               // Connection connection = null;
                connection = new BluetoothConnection(macAddress);
                try {
                    connection.open();
                    printer = ZebraPrinterFactory.getInstance(connection);
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            helper.updateProgressDialog(R.string.printing_in_progress);
                        }
                    });
                    PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();
                    Matrix matrix = new Matrix();
                    matrix.postRotate(0);

                    // //////////////////

                    String showLogo = MobileSettingsSetterGetter.getShowLogo();
                    Log.d("showLogo", "......" + showLogo);

                    if (logoStr.matches("logoprint")) {

                        if (showLogo.matches("True")) {

                            Bitmap logo = LogOutSetGet.getBitmap();

                            // Bitmap logo =
                            // BitmapFactory.decodeResource(context.getResources(),
                            // R.drawable.test2); // don't remove

                            int width = 770, imgWidth, imgHeight;
                            int diff;

                            Bitmap image = Bitmap.createBitmap(logo, 0, 0,
                                    logo.getWidth(), logo.getHeight(), matrix,
                                    true);

                            if (width > logo.getWidth()) {
                                diff = (770 - logo.getWidth()) / 2;
                                imgWidth = image.getWidth();
                                imgHeight = image.getHeight();
                            } else {
                                diff = 0;
                                imgWidth = width;
                                imgHeight = 250;
                            }

                            if (printerLanguage == PrinterLanguage.ZPL) {

                                connection
                                        .write(("! U1 setvar \"zpl.label_length\" \""
                                                + String.valueOf(imgHeight) + "\"")
                                                .getBytes());
                                connection
                                        .write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
                                                .getBytes());

                                connection
                                        .write("! U1 setvar \"ezpl.media_type\" \"continuous\""
                                                .getBytes());

                                printer.printImage(
                                        new ZebraImageAndroid(image), diff, 0,
                                        imgWidth, imgHeight, false);

                            } else if (printerLanguage == PrinterLanguage.CPCL) {

                                logo = Bitmap.createScaledBitmap(logo,
                                        logo.getWidth(), logo.getHeight(), true);

                                connection
                                        .write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n"
                                                .getBytes());
                                printer.printImage(new ZebraImageAndroid(logo),
                                        diff, 0, imgWidth, imgHeight, false);
                            }
                        }

                    } else {
                        Log.d("Logo", "No Logo");
                    }
                    // /////////////////

                    File filepath = context.getFileStreamPath(FILE_NAME);
                    printer.sendFileContents(filepath.getAbsolutePath());

                    SOTDatabase.init(context);
                    String img = SOTDatabase.getSignatureImage();
                    Log.d("Print img", "" + img);

                    if (!img.matches("")) {

                        // byte[] encodeByte = Base64.decode(img,
                        // Base64.DEFAULT);
                        // Bitmap photo = BitmapFactory.decodeByteArray(
                        // encodeByte, 0, encodeByte.length);

                        // //
                        byte[] encodeByte;

                        byte[] encodeByte1 = Base64.decode(img, Base64.DEFAULT);

                        String s;
                        try {
                            s = new String(encodeByte1, "UTF-8");
                            encodeByte = Base64.decode(s, Base64.DEFAULT);
                        } catch (Exception e) {
                            encodeByte = encodeByte1;
                        }

                        Bitmap photo = BitmapFactory.decodeByteArray(
                                encodeByte, 0, encodeByte.length);

                        // //

                        if (printerLanguage == PrinterLanguage.ZPL) {

                            Log.d("Print in ZPL Printer", "ZPL");
                            Bitmap image = Bitmap.createBitmap(photo, 0, 0,
                                    photo.getWidth(), photo.getHeight(),
                                    matrix, true);

                            connection
                                    .write("! U1 setvar \"zpl.label_length\" \"90\""
                                            .getBytes());

                            connection
                                    .write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
                                            .getBytes());

                            connection
                                    .write("! U1 setvar \"ezpl.media_type\" \"continuous\""
                                            .getBytes());

                            printer.printImage(new ZebraImageAndroid(image),
                                    -300, 0, 300, 80, false);

                        } else if (printerLanguage == PrinterLanguage.CPCL) {

                            Log.d("Print in CPCL Printer", "CPCL");
                            photo = Bitmap.createScaledBitmap(photo, 300, 80,
                                    true);

                            connection
                                    .write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n"
                                            .getBytes());
                            printer.printImage(new ZebraImageAndroid(photo), 0,
                                    0, -1, -1, false);
                        }

                        try {
                            bottomPrint();

                            File filepath1 = context
                                    .getFileStreamPath(FILE_NAME);
                            printer.sendFileContents(filepath1
                                    .getAbsolutePath());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                  if (printerLanguage == PrinterLanguage.ZPL) {
                    connection
                                .write("! U1 setvar \"zpl.label_length\" \"100\""
                                        .getBytes());


                        connection
                                .write("! U1 setvar \"ezpl.media_type\" \"continuous\""
                                        .getBytes());
                    }


                    connection.close();
                    helper.showLongToast(R.string.printed_successfully);

                } catch (ZebraPrinterLanguageUnknownException e) {
                    helper.showErrorDialog(e.getMessage());
                } catch (ConnectionException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } finally {
                    helper.dismissProgressDialog();
                    disableBluetooth();
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onCompleted();
                            }
                        }
                    });
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }

    private boolean disableBluetooth() {
        if ((bluetoothAdapter != null) && bluetoothAdapter.isEnabled()) {
            return bluetoothAdapter.disable();
        }
        return false;
    }

    /**
     * Return true if Bluetooth is currently enabled and ready for use
     *
     * @return true if bluetooth is enabled
     */
    private boolean isBluetoothEnabled() {
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

    private boolean enableBluetooth() {
        if (bluetoothAdapter == null) {
            helper.showErrorDialog(R.string.no_bluetooth_support);
            return false;
        } else if (!bluetoothAdapter.isEnabled()) {
            return bluetoothAdapter.enable();
        }
        return false;
    }

    private int printTitle(int x, int y, String title,
                           StringBuilder cpclConfigLabel) {
        cpclConfigLabel.append(text(x, y += LINE_SPACING, title));
        return y;
    }

    private String text(int x, int y, double number) {
        return text(x, y, String.format("%.2f", number));
    }

    private String text(int x, int y, String text) {
        return new StringBuilder(CMD_TEXT).append(x).append(COMMA).append(y)
                .append(CMD_TEXT_START).append(text).append(CMD_TEXT_END).append(LINE_SEPARATOR).toString();
    }

  private String textChinese(int x, int y, String text) {
      String valueUTF8 = null;
      try {
          valueUTF8 = URLEncoder.encode(text, "UTF-8");
          Log.d("Chines-->",""+valueUTF8);
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }
      String res = valueUTF8.replaceAll("\\%", "\\_");
      String result = res.replaceAll("\\+", "\\ ");
      Log.d("result",""+result);

     // String result1 = "_E6_96_91_E9_A9_AC_E7_A7_91_E6_8A_80 _E6_9C_89_E9_99_90_E5_85_AC_E5_8F_B8";
      //   "^FO10,550^FH^FD"+"Chinese print 斑马科技 有限公司^FS" +
      String result2 = "^A@N,20,20,E:MSUNG.TTF^FO"+x+","+y+"^FH^FD"+""+result+"^FS";

    //  String data = "^A@N,25,25,E:MSUNG.TTF"+result;

      return new StringBuilder(CMD_TEXT).append(x).append(COMMA).append(y)
              .append(CMD_TEXT_START).append(result2).append(CMD_TEXT_END).append(LINE_SEPARATOR).toString();

    }

    private String horizontalLine(int x,int y, int thickness) {

        return new  StringBuilder(CMD_TEXT).append(x).append(COMMA).append(y)
                .append(HORIZONTAL_LINE).append("1").append(COMMA).append(thickness).append(CMD_TEXT_END).append(LINE_SEPARATOR).toString();
    }


    public void printConsignmentOrder(String dono, String dodate,
                                      String customercode, String customername,
                                      List<ProductDetails> product, List<ProductDetails> productdet,
                                      int nofcopies) {
        helper.showProgressDialog(context.getString(R.string.print),
                context.getString(R.string.creating_file_for_printing));
        try {
            createConsignmnentFile(dono, dodate, customercode, customername,
                    product, productdet, nofcopies);
            if (isBluetoothEnabled()) {
                logoStr = "logoprint";
                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (IOException e) {
            e.printStackTrace();
            helper.dismissProgressDialog();
            helper.showErrorDialog(R.string.error_creating_file_for_printing);
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }


    private void createConsignmnentFile(String dono, String dodate,
                                        String customercode, String customername,
                                        List<ProductDetails> product, List<ProductDetails> productdet,
                                        int nofcopies) throws IOException{
        // Used the calculate the y axis printing position dynamically
        logoStr = "logoprint";
        int totalQuantity = 0;
        Log.d("delivery logoStr", "deliord" + logoStr);
        String taxType ="",taxPerc ="",taxName="";
        taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
        if(taxPerc==null || taxPerc.trim().equals("")){
            taxPerc = "0.00";
        }
        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        for (int n = 0; n < nofcopies; n++) {
            int y = 0;
            StringBuilder temp = new StringBuilder();
            y = printTitle(200, y, "CONSIGNMENT", temp);
            y = printCompanyDetails(y, temp);

            String cpclConfigLabel = temp.toString();

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CG No")
                    + text(150, y, " : ") + text(180, y, dono);
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CG Date")
                    + text(150, y, " : ") + text(180, y, dodate);

            cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                    "Cust Code"));
            cpclConfigLabel += (text(150, y, " : "));
            cpclConfigLabel += (text(180, y, customercode));
            cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                    "Cust Name"));
            cpclConfigLabel += (text(150, y, " : "));

            cpclConfigLabel += (text(
                    180,
                    y,
                    (customername.length() > 25) ? customername
                            .substring(0, 24) : customername));

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
                cpclConfigLabel += text(160, y, "Description");
                cpclConfigLabel += text(480, y, "Qty");
                cpclConfigLabel += text(565, y, "Price");
                cpclConfigLabel += text(700, y, "Total");
            }else{
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
                cpclConfigLabel += text(160, y, "Description");
                cpclConfigLabel += text(700, y, "Qty");
            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            for (ProductDetails prods : product) {
                if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
                            .getItemcode().toString());
                    // cpclConfigLabel += text(140, y,
                    // prods.getDescription().toString());
                    cpclConfigLabel += text(160, y, (prods.getDescription()
                            .length() > 25) ? prods.getDescription()
                            .substring(0, 24) : prods.getDescription());
                    cpclConfigLabel += text(480, y, prods.getQty().toString());
                    cpclConfigLabel += text(565, y, prods.getPrice().toString());
                    cpclConfigLabel += text(700, y, prods.getTotal().toString());
                    taxType = prods.getTaxType();
                    //  taxPerc = prods.getTaxPerc();
                }else{
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
                            .getItemcode().toString());
                    // cpclConfigLabel += text(140, y,
                    // prods.getDescription().toString());
                    cpclConfigLabel += text(160, y, (prods.getDescription()
                            .length() > 10) ? prods.getDescription()
                            .substring(0, 9) : prods.getDescription());
                    cpclConfigLabel += text(700, y, prods.getQty().toString());

                    totalQuantity += prods.getQty().toString().equals("") ? 0 : Integer
                            .valueOf(prods.getQty().toString());
                }


            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){

                for (ProductDetails prd : productdet) {

                  /*  if(taxType.equalsIgnoreCase("I")){
                        taxName = "Incl "+taxPerc.split("\\.")[0]+"%";
                    }else if(taxType.equalsIgnoreCase("E")){
                        taxName = "Excl "+taxPerc.split("\\.")[0]+"%";
                    }else{
                        taxName = "Tax";
                    }*/
                    if(taxType.equalsIgnoreCase("I")){
                        taxName = "GST Incl "+taxPerc.split("\\.")[0]+"%";
                    }else if(taxType.equalsIgnoreCase("E")){
                        taxName = "GST Excl "+taxPerc.split("\\.")[0]+"%";
                    }else{
                        taxName = "Tax";
                    }

                    Log.d("taxType","-->"+taxType);
                    /********************/
                    //If taxtype is I then subtotal = netotal - tax
                    String netTotal = prd.getNettot().toString();
                    if(netTotal!=null && !netTotal.isEmpty()){
                        //Do Nothing
                    }else{
                        netTotal = "0.00";
                    }
                    String tax = prd.getTax().toString();
                    if(tax!=null && !tax.isEmpty()){

                    }else{
                        tax = "0.00";
                    }
                    double dNetTotal = Double.valueOf(netTotal);
                    double dTax = Double.valueOf(tax);
                    double dSubtotal =  dNetTotal - dTax;
                    Log.d("dNetTotal","-->"+dNetTotal);
                    Log.d("dTax","-->"+dTax);
                    Log.d("subtotal","-->"+dSubtotal);


                    if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Item Disc")
                                + text(140, y, " : ")
                                + text(180, y, prd.getItemdisc().toString());
                    }

                    if (Double.parseDouble(prd.getTax().toString()) > 0) {

                        // cpclConfigLabel += text(490, y, "Sub Total")
                        // + text(650, y, " : ")
                        // + text(700, y, prd.getSubtotal().toString());
                        if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

                            if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING, "Bill Disc")
                                        + text(140, y, " : ")
                                        + text(180, y, prd.getBilldisc().toString());
                            }
                            if (Double.parseDouble(prd.getBilldisc().toString()) == 0
                                    && Double.parseDouble(prd.getItemdisc()
                                    .toString()) == 0) {
                                if(taxType!=null && !taxType.isEmpty()){
                                    if (taxType.matches("I")) {
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                "Sub Total")
                                                + text(650, y, " : ")
                                                + text(700, y, twoDecimalPoint(dSubtotal));

                                    }else{
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                "Sub Total")
                                                + text(650, y, " : ")
                                                + text(700, y, prd.getSubtotal().toString());
                                    }
                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(650, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                                /*cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(650, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());*/
                            } else {
                                if(taxType!=null && !taxType.isEmpty()){
                                    if (taxType.matches("I")) {
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                "Sub Total")
                                                + text(650, y, " : ")
                                                + text(700, y, twoDecimalPoint(dSubtotal));

                                    }else{
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                "Sub Total")
                                                + text(650, y, " : ")
                                                + text(700, y, prd.getSubtotal().toString());
                                    }
                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(650, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                              /*  cpclConfigLabel += text(490, y, "Sub Total")
                                        + text(650, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());*/
                            }
                        } else {
                            if(taxType!=null && !taxType.isEmpty()){
                                if (taxType.matches("I")) {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(650, y, " : ")
                                            + text(700, y, twoDecimalPoint(dSubtotal));

                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(650, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                            }else{
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(650, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());
                            }
                            /*cpclConfigLabel += text(490, y, "Sub Total")
                                    + text(650, y, " : ")
                                    + text(700, y, prd.getSubtotal().toString());*/
                        }
                    }

                    if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
                        // cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        // "Bill Disc")
                        // + text(140, y, " : ")
                        // + text(180, y, prd.getBilldisc().toString());
                        if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "Bill Disc")
                                    + text(140, y, " : ")
                                    + text(180, y, prd.getBilldisc().toString());
                        } else {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "") + text(140, y, "") + text(180, y, "");
                        }
                    }

                    if (Double.parseDouble(prd.getTax().toString()) > 0) {

                        if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "") + text(140, y, "") + text(180, y, "");
                        }

                        cpclConfigLabel += text(490, y, taxName)
                                + text(650, y, " : ")
                                + text(700, y, prd.getTax().toString());
                    }
                    if (Double.parseDouble(prd.getTax().toString()) == 0) {

                        cpclConfigLabel += text(490, y, "") + text(650, y, "")
                                + text(700, y, "");

                        if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
                            cpclConfigLabel += text(490, y += LINE_SPACING,
                                    "Net Total")
                                    + text(650, y, " : ")
                                    + text(700, y, prd.getNettot().toString());

                        } else {
                            cpclConfigLabel += text(490, y, "Net Total")
                                    + text(650, y, " : ")
                                    + text(700, y, prd.getNettot().toString());
                        }
                    } else {
                        cpclConfigLabel += text(490, y += LINE_SPACING, "Net Total")
                                + text(650, y, " : ")
                                + text(700, y, prd.getNettot().toString());
                    }
                }
            }else{

                cpclConfigLabel += text(490, y += LINE_SPACING, "Total Qty")
                        + text(650, y, " : ")
                        + text(700, y, totalQuantity);

            }
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
            // LINE_THICKNESS);
                   /* cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                    LINE_THICKNESS);
            cpclConfigLabel += text(75, y += LINE_SPACING,
                    "Received By");

            cpclConfigLabel += text(600, y, "Authorized By");

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            y+=70;
            // Just append everything and create a single string
            cpclConfigLabel = ZPL_CMD_START + y + FONT + cpclConfigLabel + ZPL_CMD_END;
            // cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
            Log.d("cpclConfigLabel","-->"+cpclConfigLabel);

            /*
             * FileOutputStream os = context.openFileOutput(FILE_NAME,
             * Context.MODE_PRIVATE);
             */
            os.write(cpclConfigLabel.getBytes());
            os.flush();
        }
        os.close();
    }



    public void printInvoice(String invoiceno, String invoicedate,
                             String customercode, String customername,
                             List<ProductDetails> product, List<ProductDetails> productdet,
                             List<String> printsortHeader, String gnrlStngs, int nofcopies,
                             List<ProductDetails> product_batch, List<ProductDetails> footerArr)
            throws IOException {

        helper.showProgressDialog(context.getString(R.string.print),
                context.getString(R.string.creating_file_for_printing));
        try {
            createInvoiceFile(invoiceno, invoicedate, customercode,
                    customername, product, productdet, printsortHeader,
                    gnrlStngs, nofcopies, product_batch, footerArr);
            if (isBluetoothEnabled()) {
                print();

            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }

        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void createInvoiceFile(String invoiceno, String invoicedate,
                                   String customercode, String customername,
                                   List<ProductDetails> product, List<ProductDetails> productdet,
                                   List<String> printsortHeader, String gnrlStngs, int nofcopies,
                                   List<ProductDetails> product_batch, List<ProductDetails> footerValue)
            throws IOException, ZebraPrinterConnectionException {
        // Used the calculate the y axis printing position dynamically

        // String invoiceprintdetail = SalesOrderSetGet.getInvoiceprintdetail();
        String taxType ="",taxPerc ="",taxName="";
        footerArr.clear();
        footerArr = footerValue;

        logoStr = "logoprint";

        Log.d("inv logoStr", "invoice" + logoStr);

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

        String InvoiceDetailPrintUOM =MobileSettingsSetterGetter.getInvoiceDetailPrintUOM();
        String InvoiceHeaderCaption = MobileSettingsSetterGetter.getInvoiceHeaderCaption();
        String InvoiceSubTotalCaption = MobileSettingsSetterGetter.getInvoiceSubTotalCaption();
        String InvoiceTaxCaption = MobileSettingsSetterGetter.getInvoiceTaxCaption();
        String InvoiceNetTotalCaption = MobileSettingsSetterGetter.getInvoiceNetTotalCaption();

        String subtotalCaption = "",nettotalCaption="";
        if(!InvoiceSubTotalCaption.equals("")){
            subtotalCaption = InvoiceSubTotalCaption;
        }else{
            subtotalCaption = "Sub Total";
        }
        taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
        if(taxPerc==null || taxPerc.trim().equals("")){
            taxPerc = "0.00";
        }

        if(!InvoiceNetTotalCaption.equals("")){
            nettotalCaption = InvoiceNetTotalCaption;
        }else{
            nettotalCaption = "Net Total";
        }

        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        for (int n = 0; n < nofcopies; n++) {
            int y = 0;
            int s = 1;

            StringBuilder temp = new StringBuilder();

            if(!InvoiceHeaderCaption.equals("")){
                y = printTitle(310, y, InvoiceHeaderCaption , temp);
            }else{
                y = printTitle(310, y, "INVOICE", temp);
            }

            y = printCompanyDetails(y, temp);

            String cpclConfigLabel = temp.toString();

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                    "Invoice No")
                    + text(150, y, " : ")
                    + text(180, y, invoiceno);
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                    "Invoice Date")
                    + text(150, y, " : ")
                    + text(180, y, invoicedate);

            if (showCustomerCode.matches("True")) {

                if (!customercode.matches("")) {
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                            "Cust Code"));
                    cpclConfigLabel += (text(150, y, " : "));
                    cpclConfigLabel += (text(180, y, customercode));
                }
            }
            if (showCustomerName.matches("True")) {

                if (!customername.matches("")) {
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Cust Name"));
                    cpclConfigLabel += (text(150, y, " : "));

                   cpclConfigLabel += (textChinese(
                            180,
                            y,
                            (customername.length() > 45) ? customername
                                    .substring(0, 44) : customername));

                    if (customername.length() > 45) {

                        String custlastname = customername.substring(44);

                        Log.d("Custnameeee", custlastname);

                        cpclConfigLabel += (textChinese(
                                180,
                                y += LINE_SPACING,
                                (custlastname.length() > 45) ? custlastname
                                        .substring(0, 44) : custlastname));
                    }
                }
            }

            String address1 = CustomerSetterGetter.getCustomerAddress1();
            String address2 = CustomerSetterGetter.getCustomerAddress2();
            String address3 = CustomerSetterGetter.getCustomerAddress3();
            if (showAddress1.matches("True")) {
                if (!address1.matches("")) {
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                            "Address 1"));
                    cpclConfigLabel += (text(150, y, " : "));

                    cpclConfigLabel += (textChinese(
                            180,
                            y,
                            (address1.length() > 43) ? address1
                                    .substring(0, 42) : address1));

                    if (address1.length() > 43) {

                        String addr1 = address1.substring(42);

                        Log.d("addr1", addr1);

                        cpclConfigLabel += (textChinese(
                                180,
                                y += LINE_SPACING,
                                (addr1.length() > 43) ? addr1
                                        .substring(0, 42) : addr1));
                    }
                }
            }

            if (showAddress2.matches("True")) {
                if (!address2.matches("")) {
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                            "Address 2"));
                    cpclConfigLabel += (text(150, y, " : "));

                    cpclConfigLabel += (textChinese(
                            180,
                            y,
                            (address2.length() > 43) ? address2
                                    .substring(0, 42) : address2));

                    if (address2.length() > 43) {

                        String addr2 = address2.substring(42);

                        Log.d("addr2", addr2);

                        cpclConfigLabel += (textChinese(
                                180,
                                y += LINE_SPACING,
                                (addr2.length() > 43) ? addr2
                                        .substring(0, 42) : addr2));
                    }
                }
            }

            if (showAddress3.matches("True")) {
                if (!address3.matches("")) {
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                            "Address 3"));
                    cpclConfigLabel += (text(150, y, " : "));

                    cpclConfigLabel += (textChinese(
                            180,
                            y,
                            (address3.length() > 43) ? address3
                                    .substring(0, 42) : address3));

                    if (address3.length() > 43) {

                        String addr3 = address3.substring(42);

                        Log.d("addr3", addr3);

                        cpclConfigLabel += (textChinese(
                                180,
                                y += LINE_SPACING,
                                (addr3.length() > 43) ? addr3
                                        .substring(0, 42) : addr3));
                    }
                }
            }

            if (showCustomerPhone.matches("True")) {
                if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Phone No")
                            + text(150, y, " : ")
                            + text(180, y,
                            CustomerSetterGetter.getCustomerPhone());
                }
            }

            if (showCustomerHP.matches("True")) {
                if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Head Phone")
                            + text(150, y, " : ")
                            + text(180, y, CustomerSetterGetter.getCustomerHP());
                }
            }

            if (showCustomerEmail.matches("True")) {
                if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Email")
                            + text(150, y, " : ")
                            + text(180, y,
                            CustomerSetterGetter.getCustomerEmail());
                }
            }

            if (showCustomerTerms.matches("True")) {
                if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Terms")
                            + text(150, y, " : ")
                            + text(180, y,
                            CustomerSetterGetter.getCustomerTerms());

                }
            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            if(product.size()>0){
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                cpclConfigLabel += text(70, y, "Description");
                cpclConfigLabel += text(480, y, "Qty");
                cpclConfigLabel += text(565, y, "Price");
                cpclConfigLabel += text(700, y, "Total");

                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
                if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {
                    for (ProductDetails products : product) {

                        if ((products.getSortproduct().matches(""))
                                || (products.getSortproduct().matches("0"))) {
                            int i = 1;
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    String.valueOf(i));

                            if (showProductFullName.matches("True")) {
                                cpclConfigLabel += textChinese(70, y, (products
                                        .getDescription().length() > 31) ? products
                                        .getDescription().substring(0, 30)
                                        : products.getDescription());
                                if(InvoiceDetailPrintUOM.equalsIgnoreCase("True")) {
                                    cpclConfigLabel += text(480, y += LINE_SPACING,
                                            products.getQty().toString()+" "+products.getUOMCode());
                                }else{
                                    cpclConfigLabel += text(480, y += LINE_SPACING,
                                            products.getQty().toString());
                                }

                            } else {
                                cpclConfigLabel += textChinese(70, y, (products
                                        .getDescription().length() > 31) ? products
                                        .getDescription().substring(0, 30)
                                        : products.getDescription());
                                if(InvoiceDetailPrintUOM.equalsIgnoreCase("True")) {
                                    cpclConfigLabel += text(480, y, products.getQty()
                                            .toString()+" "+products.getUOMCode());
                                }else{
                                    cpclConfigLabel += text(480, y, products.getQty()
                                            .toString());
                                }
                            }

                            cpclConfigLabel += text(565, y, products.getPrice()
                                    .toString());
                            cpclConfigLabel += text(700, y, products.getTotal()
                                    .toString());

                            // Log.d("products.getFocqty() 1", ""+(int)
                            // products.getFocqty());

                            if (products.getFocqty() > 0) {
                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Foc")
                                        + text(210, y, " : ")
                                        + text(280, y, (int) products.getFocqty());
                            }

                            // Log.d("products.getExchangeqty() 1", ""+(int)
                            // products.getExchangeqty());

                            if (products.getExchangeqty() > 0) {
                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Exchange")
                                        + text(210, y, " : ")
                                        + text(280, y,
                                        (int) products.getExchangeqty());
                            }
						/*
						 * if (!products.getIssueQty().matches("0")) {
						 * cpclConfigLabel += text(70, y += LINE_SPACING,
						 * "Issue") + text(210, y, " : ") + text(280, y,
						 * products.getIssueQty()); } if
						 * (!products.getReturnQty().matches("0")) {
						 * cpclConfigLabel += text(375, y, "Return") + text(450,
						 * y, " : ") + text(486, y, products.getReturnQty()); }
						 */
                            if ((products.getIssueQty() != null && !products
                                    .getIssueQty().isEmpty())
                                    && (products.getReturnQty() != null && !products
                                    .getReturnQty().isEmpty())) {

                                if ((Double.valueOf(products.getIssueQty()) > 0)
                                        && (Double.valueOf(products.getReturnQty()) > 0)) {

                                    cpclConfigLabel += text(70, y += LINE_SPACING,
                                            "Issue")
                                            + text(210, y, " : ")
                                            + text(280, y, products.getIssueQty());

                                    cpclConfigLabel += text(375, y, "Return")
                                            + text(450, y, " : ")
                                            + text(486, y, products.getReturnQty());

                                }
                            }

                            if (product_batch.size() > 0) {
                                for (ProductDetails prodBatch : product_batch) {

                                    if (prodBatch.getBatch_productcode().matches(
                                            products.getItemcode())) {

                                        cpclConfigLabel += text(70,
                                                y += LINE_SPACING, prodBatch
                                                        .getProduct_batchno()
                                                        .toString());
                                    }

                                }
                            }

                            s += i;
                            i++;
                        }

                        taxType = products.getTaxType();
                       // taxPerc = products.getTaxPerc();
                    }
                    for (int i = 0; i < printsortHeader.size(); i++) {
                        String catorsub = printsortHeader.get(i).toString();
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                catorsub + " :");
                        for (ProductDetails prods : product) {
                            if (catorsub.equalsIgnoreCase(prods.getSortproduct().toString())) {

                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING, String.valueOf(s));

							/*
							 * cpclConfigLabel += text(280, y += LINE_SPACING,
							 * prods.getQty() .toString());
							 */
                                if (showProductFullName.matches("True")) {
                                    cpclConfigLabel += textChinese(
                                            70,
                                            y,
                                            (prods.getDescription().length() > 31) ? prods
                                                    .getDescription().substring(0,
                                                            30) : prods
                                                    .getDescription());
                                    if(InvoiceDetailPrintUOM.equalsIgnoreCase("True")) {
                                        cpclConfigLabel += textChinese(480, y += LINE_SPACING,
                                                prods.getQty().toString() +" "+ prods.getUOMCode());
                                    }else{
                                        cpclConfigLabel += textChinese(480, y += LINE_SPACING,
                                                prods.getQty().toString());
                                    }
                                } else {
                                    cpclConfigLabel += text(
                                            70,
                                            y,
                                            (prods.getDescription().length() > 31) ? prods
                                                    .getDescription().substring(0,
                                                            30) : prods
                                                    .getDescription());

                                    if(InvoiceDetailPrintUOM.equalsIgnoreCase("True")){
                                        cpclConfigLabel += text(480, y, prods.getQty()
                                                .toString() +" "+ prods.getUOMCode());
                                    }else{
                                        cpclConfigLabel += text(480, y, prods.getQty()
                                                .toString());
                                    }



                                }
                                cpclConfigLabel += text(565, y, prods.getPrice()
                                        .toString());
                                cpclConfigLabel += text(700, y, prods.getTotal()
                                        .toString());

                                // Log.d("products.getFocqty() 2", ""+(int)
                                // prods.getFocqty());

                                if (prods.getFocqty() > 0) {
                                    cpclConfigLabel += text(70, y += LINE_SPACING,
                                            "Foc")
                                            + text(210, y, " : ")
                                            + text(280, y, (int) prods.getFocqty());
                                }

                                // Log.d("products.getExchangeqty() 2", ""+(int)
                                // prods.getExchangeqty());

                                if (prods.getExchangeqty() > 0) {
                                    cpclConfigLabel += text(70, y += LINE_SPACING,
                                            "Exchange")
                                            + text(210, y, " : ")
                                            + text(280, y,
                                            (int) prods.getExchangeqty());
                                }
							/*
							 * if (!prods.getIssueQty().matches("0")) {
							 * cpclConfigLabel += text(70, y += LINE_SPACING,
							 * "Issue") + text(210, y, " : ") + text(280, y,
							 * prods.getIssueQty()); } if
							 * (!prods.getReturnQty().matches("0")) {
							 * cpclConfigLabel += text(375, y, "Return") +
							 * text(450, y, " : ") + text(486, y,
							 * prods.getReturnQty()); }
							 */

                                if ((prods.getIssueQty() != null && !prods
                                        .getIssueQty().isEmpty())
                                        && (prods.getReturnQty() != null && !prods
                                        .getReturnQty().isEmpty())) {

                                    if ((Double.valueOf(prods.getIssueQty()) > 0)
                                            && (Double
                                            .valueOf(prods.getReturnQty()) > 0)) {

                                        cpclConfigLabel += text(70,
                                                y += LINE_SPACING, "Issue")
                                                + text(210, y, " : ")
                                                + text(280, y, prods.getIssueQty());

                                        cpclConfigLabel += text(375, y, "Return")
                                                + text(450, y, " : ")
                                                + text(486, y, prods.getReturnQty());

                                    }
                                }
                                if (product_batch.size() > 0) {
                                    for (ProductDetails prodBatch : product_batch) {

                                        if (prodBatch.getBatch_productcode()
                                                .matches(prods.getItemcode())) {

                                            cpclConfigLabel += text(70,
                                                    y += LINE_SPACING, prodBatch
                                                            .getProduct_batchno()
                                                            .toString());
                                        }

                                    }
                                }
                                s++;

                            }

                        }
                    }
                } else {
                    for (ProductDetails products : product) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                products.getSno().toString());

					/*
					 * cpclConfigLabel += text(280, y += LINE_SPACING,
					 * products.getQty() .toString());
					 */
                        if (showProductFullName.matches("True")) {
                            cpclConfigLabel += textChinese(
                                    70,
                                    y,
                                    (products.getDescription().length() > 31) ? products
                                            .getDescription().substring(0, 30)
                                            : products.getDescription());

                            if(InvoiceDetailPrintUOM.equalsIgnoreCase("True")){
                                cpclConfigLabel += text(480, y += LINE_SPACING,
                                        products.getQty().toString() +" "+ products.getUOMCode());
                            }else{
                                cpclConfigLabel += text(480, y += LINE_SPACING,
                                        products.getQty().toString());
                            }


                        } else {
                            cpclConfigLabel += textChinese(
                                    70,
                                    y,
                                    (products.getDescription().length() > 31) ? products
                                            .getDescription().substring(0, 30)
                                            : products.getDescription());
                            if(InvoiceDetailPrintUOM.equalsIgnoreCase("True")){
                                cpclConfigLabel += text(480, y, products.getQty()
                                        .toString()+" "+ products.getUOMCode());
                            }else{
                                cpclConfigLabel += text(480, y, products.getQty()
                                        .toString());
                            }

                        }
                        cpclConfigLabel += text(565, y, products.getPrice()
                                .toString());
                        cpclConfigLabel += text(700, y, products.getTotal()
                                .toString());

                        Log.d("products.getFocqty() 3",
                                "" + (int) products.getFocqty());

                        if (products.getFocqty() > 0) {
                            cpclConfigLabel += text(70, y += LINE_SPACING, "Foc")
                                    + text(210, y, " : ")
                                    + text(280, y, "" + (int) products.getFocqty());
                        }

                        Log.d("getExchangeqty() 3",
                                "" + (int) products.getExchangeqty());

                        if (products.getExchangeqty() > 0) {
                            cpclConfigLabel += text(70, y += LINE_SPACING,
                                    "Exchange")
                                    + text(210, y, " : ")
                                    + text(280, y,
                                    "" + (int) products.getExchangeqty());
                        }
					/*
					 * if (!products.getIssueQty().matches("0")) {
					 * cpclConfigLabel += text(70, y += LINE_SPACING, "Issue") +
					 * text(210, y, " : ") + text(280, y,
					 * products.getIssueQty()); } if
					 * (!products.getReturnQty().matches("0")) { cpclConfigLabel
					 * += text(375, y, "Return") + text(450, y, " : ") +
					 * text(486, y, products.getReturnQty()); }
					 */
                        if ((products.getIssueQty() != null && !products
                                .getIssueQty().isEmpty())
                                && (products.getReturnQty() != null && !products
                                .getReturnQty().isEmpty())) {

                            if ((Double.valueOf(products.getIssueQty()) > 0)
                                    && (Double.valueOf(products.getReturnQty()) > 0)) {

                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Issue")
                                        + text(210, y, " : ")
                                        + text(280, y, products.getIssueQty());

                                cpclConfigLabel += text(375, y, "Return")
                                        + text(450, y, " : ")
                                        + text(486, y, products.getReturnQty());

                            }
                        }

                        if (product_batch.size() > 0) {
                            for (ProductDetails prodBatch : product_batch) {

                                if (prodBatch.getBatch_productcode().matches(
                                        products.getItemcode())) {

                                    cpclConfigLabel += text(70, y += LINE_SPACING,
                                            prodBatch.getProduct_batchno()
                                                    .toString());
                                }

                            }
                        }
                        taxType = products.getTaxType();
                     //   taxPerc = products.getTaxPerc();
                    }
                }
                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
                for (ProductDetails prd : productdet) {
                   // String taxType ="",taxValue ="",taxName="";

                    if(taxType.equalsIgnoreCase("I")){
                        taxName = "Incl "+taxPerc.split("\\.")[0]+"%";
                    }else if(taxType.equalsIgnoreCase("E")){
                        taxName = "Excl "+taxPerc.split("\\.")[0]+"%";
                    }else{
                        taxName = "Tax";
                    }

                    Log.d("taxType","-->"+taxType);
                    /********************/
                    //If taxtype is I then subtotal = netotal - tax
                    String netTotal = prd.getNettot().toString();
                    if(netTotal!=null && !netTotal.isEmpty()){

                    }else{
                        netTotal = "0.00";
                    }
                    String tax = prd.getTax().toString();
                    if(tax!=null && !tax.isEmpty()){

                    }else{
                        tax = "0.00";
                    }
                    double dNetTotal = Double.valueOf(netTotal);
                    double dTax = Double.valueOf(tax);
                    double dSubtotal =  dNetTotal - dTax;
                    Log.d("dNetTotal","-->"+dNetTotal);
                    Log.d("dTax","-->"+dTax);
                    Log.d("subtotal","-->"+dSubtotal);
                         /********************/

                    if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Item Disc")
                                + text(140, y, " : ")
                                + text(180, y, prd.getItemdisc().toString());
                    }

                    if (Double.parseDouble(prd.getTax().toString()) > 0) {

                        if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

                            if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING, "Bill Disc")
                                        + text(140, y, " : ")
                                        + text(180, y, prd.getBilldisc().toString());
                            }
                            if (Double.parseDouble(prd.getBilldisc().toString()) == 0
                                    && Double.parseDouble(prd.getItemdisc()
                                    .toString()) == 0) {
                             if(taxType!=null && !taxType.isEmpty()){
                                 if (taxType.matches("I")) {

                                     cpclConfigLabel += text(490, y += LINE_SPACING,
                                             subtotalCaption)
                                             + text(630, y, " : ")
                                             + text(700, y, twoDecimalPoint(dSubtotal));

                                 }else{
                                     cpclConfigLabel += text(490, y += LINE_SPACING,
                                             subtotalCaption)
                                             + text(630, y, " : ")
                                             + text(700, y, prd.getSubtotal().toString());
                                 }
                             }else{
                                 cpclConfigLabel += text(490, y += LINE_SPACING,
                                         subtotalCaption)
                                         + text(630, y, " : ")
                                         + text(700, y, prd.getSubtotal().toString());
                             }


                            } else {

                                if(taxType!=null && !taxType.isEmpty()){
                                    if (taxType.matches("I")) {
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                subtotalCaption)
                                                + text(630, y, " : ")
                                                + text(700, y, twoDecimalPoint(dSubtotal));

                                    }else{
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                subtotalCaption)
                                                + text(630, y, " : ")
                                                + text(700, y, prd.getSubtotal().toString());
                                    }
                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            subtotalCaption)
                                            + text(630, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                            }
                        } else {

                            if(taxType!=null && !taxType.isEmpty()){
                                if (taxType.matches("I")) {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            subtotalCaption)
                                            + text(630, y, " : ")
                                            + text(700, y, twoDecimalPoint(dSubtotal));

                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            subtotalCaption)
                                            + text(630, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                            }else{
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        subtotalCaption)
                                        + text(630, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());
                            }
                        }
                    }

                    if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
                        if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "Bill Disc")
                                    + text(140, y, " : ")
                                    + text(180, y, prd.getBilldisc().toString());
                        } else {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "") + text(140, y, "") + text(180, y, "");
                        }
                    }

                    if (Double.parseDouble(prd.getTax().toString()) > 0) {

                        if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "") + text(140, y, "") + text(180, y, "");
                        }


                        if(!InvoiceTaxCaption.equals("")){
//                            taxCaption = InvoiceTaxCaption + taxName;
                            cpclConfigLabel += text(490, y, InvoiceTaxCaption + taxName)
                                    + text(630, y, " : ")
                                    + text(700, y, prd.getTax().toString());
                        }else{
//                            taxCaption = taxName;

                            if(taxType.equalsIgnoreCase("I")){
                                taxName = "GST Incl "+taxPerc.split("\\.")[0]+"%";
                            }else if(taxType.equalsIgnoreCase("E")){
                                taxName = "GST Excl "+taxPerc.split("\\.")[0]+"%";
                            }else{
                                taxName = "Tax";
                            }

                            cpclConfigLabel += text(490, y, taxName)
                                    + text(630, y, " : ")
                                    + text(700, y, prd.getTax().toString());
                        }


                    }
                    if (Double.parseDouble(prd.getTax().toString()) == 0) {

                        cpclConfigLabel += text(310, y, "") + text(450, y, "")
                                + text(486, y, "");

                        if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
//						cpclConfigLabel += horizontalLine(y += LINE_SPACING,LINE_THICKNESS);
                            cpclConfigLabel += text(490, y += LINE_SPACING,
                                    nettotalCaption)
                                    + text(630, y, " : ")
                                    + text(700, y, prd.getNettot().toString());

                        } else {

                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Bill Disc")
                                    + text(140, y, " : ")
                                    + text(180, y, prd.getBilldisc().toString());

                            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,LINE_THICKNESS);
                            cpclConfigLabel += text(490, y += LINE_SPACING, nettotalCaption)
                                    + text(630, y, " : ")
                                    + text(700, y, prd.getNettot().toString());
                        }
                    } else {
                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,LINE_THICKNESS);
                        cpclConfigLabel += text(490, y += LINE_SPACING, nettotalCaption)
                                + text(630, y, " : ")
                                + text(700, y, prd.getNettot().toString());
                    }

                    cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                            LINE_THICKNESS);

                    if (!prd.getRemarks().matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Remarks")
                                + text(140, y, " : ")
                                + text(180, y,
                                (prd.getRemarks().length() > 25) ? prd
                                        .getRemarks().substring(0, 24)
                                        : prd.getRemarks());

                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);
                    }
                    if (showTotalOutstanding.matches("True")) {
                        if (prd.getTotaloutstanding() != null
                                && !prd.getTotaloutstanding().isEmpty()
                                && !prd.getTotaloutstanding().matches("null")) {

                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "Total Outstanding")
                                    + text(630, y, " : ")
                                    + text(700, y, prd.getTotaloutstanding());

//						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
//								LINE_THICKNESS);

                        }

                    }

                }

            }
            // if(showTotalOutstanding.matches("True")){
            // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
            // LINE_THICKNESS);
            // }

            SOTDatabase.init(context);
            String img = SOTDatabase.getSignatureImage();

            if (showFooter.matches("True")) {

                if (img!=null && !img.isEmpty()) {

                    Log.d("Do Nothing", "Do Nothing");

                }else{ //without signature

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
                    // LINE_THICKNESS);
                   /* cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/
                    cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                            LINE_THICKNESS);
                    cpclConfigLabel += text(75, y += LINE_SPACING,
                            "Received By");

                    cpclConfigLabel += text(600, y, "Authorized By");

                    Log.d("footerArr", "" + footerArr.size());

                    if (footerArr.size() > 0) {

                        // cpclConfigLabel += text(LEFT_MARGIN, y +=
                        // LINE_SPACING,
                        // "--------------*********---------------");
                        // cpclConfigLabel += text(230, y += LINE_SPACING, "");
                        // cpclConfigLabel += text(230, y += LINE_SPACING,
                        // "*********");
                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);

                        for (ProductDetails footer : footerArr) {

                            Log.d("footer value",
                                    "val " + footer.getReceiptMessage());

                            if (footer.getReceiptMessage() != null
                                    && !footer.getReceiptMessage().isEmpty()) {
                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING,
                                        footer.getReceiptMessage());
                            }
                        }
                    }

                    y+=70;

                }

            }

            // Just append everything and create a single string


        //A@N,25,25,E:MSUNG.TTF
            cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
            Log.d("cpclConfigLabel",""+cpclConfigLabel);

            os.write(cpclConfigLabel.getBytes());

            os.flush();
        }
        // ///////////////////////////////////////////////////////////////////////////////////

        os.close();

    }
    public void printReceipt(String customercode, String customername,
                             String receiptno, String receiptdate, List<ProductDetails> product,
                             List<String> sort, String gnrlStngs, int nofcopies,
                             boolean isSingleCustomer, List<ProductDetails> footerValue,ArrayList<HashMap<String, String>> salesReturnArr)
            throws IOException {
        helper.showProgressDialog(R.string.print,
                R.string.creating_file_for_printing);
        try {

            createReceiptFile(customercode, customername, receiptno,
                    receiptdate, product, sort, gnrlStngs, nofcopies,
                    isSingleCustomer, footerValue,salesReturnArr);

            if (isBluetoothEnabled()) {
                logoStr = "logoprint";
                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void createReceiptFile(String customercode, String customername,
                                   String receiptno, String receiptdate,
                                   List<ProductDetails> receipts, List<String> sort, String gnrlStngs,
                                   int nofcopies, boolean isSingleCustomer,
                                   List<ProductDetails> footerValue,ArrayList<HashMap<String, String>> salesReturnArr) throws IOException {
        String invoiceprintdetail = SalesOrderSetGet.getInvoiceprintdetail();
        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        String payment = "", totalOutStanding = "", bank_Code = "", bank_Name = "", check_No = "", check_Date = "",taxType ="",taxPerc ="",taxName="";
        logoStr = "logoprint";

        footerArr.clear();
        footerArr = footerValue;

        Log.d("rec logoStr", "receipt" + logoStr);

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

        taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
        if(taxPerc==null || taxPerc.trim().equals("")){
            taxPerc = "0.00";
        }

		/*String showuserphoneno = MobileSettingsSetterGetter.getShowUserPhoneNo();

		  String loginphoneno  = SalesOrderSetGet.getLoginPhoneNo();

		  String username = SupplierSetterGetter.getUsername();*/
        Log.d("showProductFullName", "mm"+showProductFullName);

        for (int n = 0; n < nofcopies; n++) {
            int y = 0;
            int s = 1;
            String cpclConfigLabel = "";
            if (isSingleCustomer) {

                StringBuilder temp = new StringBuilder();
                y = printTitle(310, y, "RECEIPT", temp);
                y = printCompanyDetails(y, temp);
                cpclConfigLabel = temp.toString();
			/*	if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {
				     if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {
				      cpclConfigLabel+= text(LEFT_MARGIN, y += LINE_SPACING,
				        "Contact ")
				        + text(150, y, " : ")
				        + text(180, y, username +" "+loginphoneno);

				     }
				    }*/
                if (showCustomerCode.matches("True")) {

                    if (!customercode.matches("")) {
                        cpclConfigLabel += (text(LEFT_MARGIN,
                                y += LINE_SPACING, "Cust Code"));
                        cpclConfigLabel += (text(150, y, " : "));
                        cpclConfigLabel += (text(180, y, customercode));
                    }
                }

                if (showCustomerName.matches("True")) {

//					cpclConfigLabel += (text(LEFT_MARGIN,
//							y += LINE_SPACING, "^XA^CI28^A@N,50,50,E:SIMSUN.FNT^FO50,110^FDChinese 中文字符^FS^XZ\n"));

                    if (!customername.matches("")) {
                        cpclConfigLabel += (text(LEFT_MARGIN,
                                y += LINE_SPACING, "Cust Name"));
                        cpclConfigLabel += (text(150, y, " : "));

                        cpclConfigLabel += (textChinese(
                                180,
                                y,
                                (customername.length() > 45) ? customername
                                        .substring(0, 44) : customername));

                        if (customername.length() > 45) {

                            String custlastname = customername.substring(44);

                            Log.d("Custnameeee", custlastname);

                            cpclConfigLabel += (textChinese(
                                    180,
                                    y += LINE_SPACING,
                                    (custlastname.length() > 45) ? custlastname
                                            .substring(0, 44) : custlastname));
                        }
                    }

                }

                if (showAddress1.matches("True")) {

                    if (!CustomerSetterGetter.getCustomerAddress1().matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Address 1")
                                + text(150, y, " : ")
                                + textChinese(180, y,
                                CustomerSetterGetter
                                        .getCustomerAddress1());
                    }
                }

                if (showAddress2.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerAddress2().matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Address 2")
                                + text(150, y, " : ")
                                + textChinese(180, y,
                                CustomerSetterGetter
                                        .getCustomerAddress2());
                    }
                }

                if (showAddress3.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerAddress3().matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Address 3")
                                + text(150, y, " : ")
                                + textChinese(180, y,
                                CustomerSetterGetter
                                        .getCustomerAddress3());
                    }
                }

                if (showCustomerPhone.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Phone No")
                                + text(150, y, " : ")
                                + text(180, y,
                                CustomerSetterGetter.getCustomerPhone());
                    }
                }

                if (showCustomerHP.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Head Phone")
                                + text(150, y, " : ")
                                + text(180, y,
                                CustomerSetterGetter.getCustomerHP());
                    }
                }

                if (showCustomerEmail.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Email")
                                + text(150, y, " : ")
                                + text(180, y,
                                CustomerSetterGetter.getCustomerEmail());
                    }
                }

                if (showCustomerTerms.matches("True")) {
                    if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Terms")
                                + text(150, y, " : ")
                                + text(180, y,
                                CustomerSetterGetter.getCustomerTerms());
                    }
                }

            }

            for (ProductDetails receipt : receipts) {
                if (!isSingleCustomer) {
                    StringBuilder temp = new StringBuilder();
                    y = printTitle(310, y, "RECEIPT", temp);
                    y = printCompanyDetails(y, temp);
                    cpclConfigLabel = temp.toString();
					/*if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {
					      if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {
					       cpclConfigLabel+= text(LEFT_MARGIN, y += LINE_SPACING,
					         "Contact ")
					         + text(150, y, " : ")
					         + text(180, y, username +" "+loginphoneno);

					      }
					     }*/
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                            "Cust Code"));
                    cpclConfigLabel += (text(150, y, " : "));
                    cpclConfigLabel += (text(180, y, customercode));
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                            "Cust Name"));
                    cpclConfigLabel += (text(150, y, " : "));

                    cpclConfigLabel += (textChinese(
                            180,
                            y,
                            (customername.length() > 45) ? customername
                                    .substring(0, 44) : customername));
                }

                if (receiptno.matches(receipt.getItemno())) {
                    Log.d("receipt.getItemno()",receipt.getItemno());
                    if (!receiptno.matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Receipt No")
                                + text(150, y, " : ")
                                + text(180, y, receiptno);

                    }
                    if (receiptdate.matches(receipt.getItemdate())) {
                        if (!receiptdate.matches("")) {
                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Receipt Date")
                                    + text(150, y, " : ")
                                    + text(180, y, receiptdate);

                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "Invoice No")
                                    + text(150, y, " : ")
                                    + text(180, y, receipt.getItemno());
                        }
                    } else {
                        if (!receiptdate.matches("")) {
                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Receipt Date")
                                    + text(150, y, " : ")
                                    + text(180, y, receiptdate);
                        }
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Invoice Date")
                                + text(150, y, " : ")
                                + text(180, y, receipt.getItemdate());
                    }
                } else {
                    Log.d("receiptno",receiptno);
                    if (!receiptno.matches("")) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Receipt No")
                                + text(150, y, " : ")
                                + text(180, y, receiptno);

                    }
                    if (receiptdate.matches(receipt.getItemdate())) {
                        if (!receiptdate.matches("")) {
                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Receipt Date")
                                    + text(150, y, " : ")
                                    + text(180, y, receiptdate);
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "Invoice No")
                                    + text(150, y, " : ")
                                    + text(180, y, receipt.getItemno());
                        }
                    } else {
                        if (!receiptdate.matches("")) {
                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Receipt Date")
                                    + text(150, y, " : ")
                                    + text(180, y, receiptdate);
                        }
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Invoice No")
                                + text(150, y, " : ")
                                + text(180, y, receipt.getItemno());
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Invoice Date")
                                + text(150, y, " : ")
                                + text(180, y, receipt.getItemdate());
                    }

                }

                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                        LINE_THICKNESS);
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                cpclConfigLabel += text(70, y, "Description");
                cpclConfigLabel += text(480, y, "Qty");
                cpclConfigLabel += text(565, y, "Price");
                cpclConfigLabel += text(700, y, "Total");

                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                        LINE_THICKNESS);

                if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {

                    for (ProdDetails products : receipt.getProductsDetails()) {

                        if ((products.getSortproduct().matches(""))
                                || (products.getSortproduct().matches("0"))) {
                            int i = 1;

                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, String.valueOf(i));
                            if (showProductFullName.matches("True")) {
                                cpclConfigLabel += textChinese(
                                        70,
                                        y,
                                        (products.getDescription().length() > 31) ? products
                                                .getDescription().substring(0,
                                                        30) : products
                                                .getDescription());

                                cpclConfigLabel += text(480, y += LINE_SPACING,
                                        products.getQty().toString());
                            } else {
                                cpclConfigLabel += textChinese(
                                        70,
                                        y,
                                        (products.getDescription().length() > 31) ? products
                                                .getDescription().substring(0,
                                                        30) : products
                                                .getDescription());
                                cpclConfigLabel += text(480, y, products
                                        .getQty().toString());
                            }
                            cpclConfigLabel += text(565, y, products.getPrice()
                                    .toString());
                            cpclConfigLabel += text(700, y, products.getTotal()
                                    .toString());

                            // Log.d("products.getFocqty() 1", ""+(int)
                            // products.getFocqty());

                            if (products.getFocqty() > 0) {
                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Foc")
                                        + text(210, y, " : ")
                                        + text(280, y,
                                        (int) products.getFocqty());
                            }

                            // Log.d("products.getExchangeqty() 1", ""+(int)
                            // products.getExchangeqty());

                            if (products.getExchangeqty() > 0) {
                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Exchange")
                                        + text(210, y, " : ")
                                        + text(280, y,
                                        (int) products.getExchangeqty());
                            }

                            s += i;
                            i++;
                        }

                        taxType = products.getTaxType();
                    //    taxPerc = products.getTaxPerc();

                        Log.d("taxPerc","<>"+taxPerc);
                        Log.d("taxType","<>"+taxType);
                    }
                    for (int i = 0; i < sort.size(); i++) {
                        String catorsub = sort.get(i).toString();
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                catorsub);
                        for (ProdDetails prods : receipt.getProductsDetails()) {
                            if (catorsub.equalsIgnoreCase(prods.getSortproduct()
                                    .toString())) {
                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING, String.valueOf(s));

                                if (showProductFullName.matches("True")) {
                                    cpclConfigLabel += textChinese(
                                            70,
                                            y,
                                            (prods.getDescription().length() > 31) ? prods
                                                    .getDescription()
                                                    .substring(0, 30) : prods
                                                    .getDescription());
                                    cpclConfigLabel += text(480,
                                            y += LINE_SPACING, prods.getQty()
                                                    .toString());
                                } else {

                                    cpclConfigLabel += textChinese(
                                            70,
                                            y,
                                            (prods.getDescription().length() > 31) ? prods
                                                    .getDescription()
                                                    .substring(0, 30) : prods
                                                    .getDescription());
                                    cpclConfigLabel += text(480, y, prods
                                            .getQty().toString());
                                }
                                cpclConfigLabel += text(565, y, prods
                                        .getPrice().toString());
                                cpclConfigLabel += text(700, y, prods
                                        .getTotal().toString());

                                // Log.d("products.getFocqty() 2", ""+(int)
                                // prods.getFocqty());

                                if (prods.getFocqty() > 0) {
                                    cpclConfigLabel += text(70,
                                            y += LINE_SPACING, "Foc")
                                            + text(210, y, " : ")
                                            + text(280, y,
                                            (int) prods.getFocqty());
                                }

                                // Log.d("products.getExchangeqty() 2", ""+(int)
                                // prods.getExchangeqty());

                                if (prods.getExchangeqty() > 0) {
                                    cpclConfigLabel += text(70,
                                            y += LINE_SPACING, "Exchange")
                                            + text(210, y, " : ")
                                            + text(280, y,
                                            (int) prods
                                                    .getExchangeqty());
                                }

                                s++;
                            }
                        }
                    }

                } else {
                    for (ProdDetails product : receipt.getProductsDetails()) {
                        String invoicenum = product.getItemnum().toString();

                        if (invoicenum.matches(receipt.getItemno().toString())) {

                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, product.getSno()
                                            .toString());
                            if (showProductFullName.matches("True")) {
                                cpclConfigLabel += textChinese(
                                        70,
                                        y,
                                        (product.getDescription().length() > 31) ? product
                                                .getDescription().substring(0,
                                                        30) : product
                                                .getDescription());
                                cpclConfigLabel += text(480, y += LINE_SPACING,
                                        product.getQty().toString());
                            } else {
                                cpclConfigLabel += textChinese(
                                        70,
                                        y,
                                        (product.getDescription().length() > 31) ? product
                                                .getDescription().substring(0,
                                                        30) : product
                                                .getDescription());
                                cpclConfigLabel += text(480, y, product
                                        .getQty().toString());

                            }
                            cpclConfigLabel += text(565, y, product.getPrice()
                                    .toString());
                            cpclConfigLabel += text(700, y, product.getTotal()
                                    .toString());

                            if (product.getFocqty() > 0) {
                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Foc")
                                        + text(210, y, " : ")
                                        + text(280, y,
                                        "" + (int) product.getFocqty());
                            }



                            if (product.getExchangeqty() > 0) {
                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Exchange")
                                        + text(210, y, " : ")
                                        + text(280,
                                        y,
                                        ""
                                                + (int) product
                                                .getExchangeqty());
                            }
                        }

                        taxType = product.getTaxType();
                //        taxPerc = product.getTaxPerc();
                        Log.d("taxPerc",">"+taxPerc);
                        Log.d("taxType",">"+taxType);
                    }
                }
                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                        LINE_THICKNESS);

                if(taxType.equalsIgnoreCase("I")){
                    taxName = "GST Incl "+taxPerc.split("\\.")[0]+"%";
                }else if(taxType.equalsIgnoreCase("E")){
                    taxName = "GST Excl "+taxPerc.split("\\.")[0]+"%";
                }else{
                    taxName = "Tax";
                }

                Log.d("taxType","-->"+taxType);
                /********************/
                //If taxtype is I then subtotal = netotal - tax
                String netTotalStr = receipt.getNettot().toString();
                if(netTotalStr!=null && !netTotalStr.isEmpty()){

                }else{
                    netTotalStr = "0.00";
                }
                String tax = receipt.getTax().toString();
                if(tax!=null && !tax.isEmpty()){

                }else{
                    tax = "0.00";
                }
                double dNetTotal = Double.valueOf(netTotalStr);
                double dTax = Double.valueOf(tax);
                double dSubtotal =  dNetTotal - dTax;
                Log.d("dNetTotal","-->"+dNetTotal);
                Log.d("dTax","-->"+dTax);
                Log.d("subtotal","-->"+dSubtotal);
                /********************/


                if (Double.parseDouble(receipt.getItemdisc().toString()) > 0) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Item Disc")
                            + text(140, y, " : ")
                            + text(180, y, receipt.getItemdisc().toString());
                }
                if (Double.parseDouble(receipt.getTax().toString()) > 0) {

                    if (Double.parseDouble(receipt.getItemdisc().toString()) == 0) {

                        if (Double
                                .parseDouble(receipt.getBilldisc().toString()) > 0) {
                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Bill Disc")
                                    + text(140, y, " : ")
                                    + text(180, y, receipt.getBilldisc()
                                    .toString());
                        }
                        if (Double
                                .parseDouble(receipt.getBilldisc().toString()) == 0
                                && Double.parseDouble(receipt.getItemdisc()
                                .toString()) == 0) {

                            if(taxType!=null && !taxType.isEmpty()) {
                                if (taxType.matches("I")) {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(630, y, " : ")
                                            + text(700, y, twoDecimalPoint(dSubtotal));
                                } else {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(630, y, " : ")
                                            + text(700, y, receipt.getSubtotal()
                                            .toString());
                                }

                            }else{
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(630, y, " : ")
                                        + text(700, y, receipt.getSubtotal()
                                        .toString());
                            }



                        } else {
                            if(taxType!=null && !taxType.isEmpty()) {
                                if (taxType.matches("I")) {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(630, y, " : ")
                                            + text(700, y, twoDecimalPoint(dSubtotal));
                                } else {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(630, y, " : ")
                                            + text(700, y, receipt.getSubtotal()
                                            .toString());
                                }

                            }else{
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(630, y, " : ")
                                        + text(700, y, receipt.getSubtotal()
                                        .toString());
                            }
                        }
                    } else {

                        if(taxType!=null && !taxType.isEmpty()) {
                            if (taxType.matches("I")) {
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(630, y, " : ")
                                        + text(700, y, twoDecimalPoint(dSubtotal));
                            } else {
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(630, y, " : ")
                                        + text(700, y, receipt.getSubtotal()
                                        .toString());
                            }

                        }else{
                            cpclConfigLabel += text(490, y += LINE_SPACING,
                                    "Sub Total")
                                    + text(630, y, " : ")
                                    + text(700, y, receipt.getSubtotal()
                                    .toString());
                        }
                    }
                }

                if (Double.parseDouble(receipt.getBilldisc().toString()) > 0) {

                    if (Double.parseDouble(receipt.getItemdisc().toString()) != 0) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Bill Disc")
                                + text(140, y, " : ")
                                + text(180, y, receipt.getBilldisc().toString());
                    } else {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "") + text(140, y, "") + text(180, y, "");
                    }
                }

                if (Double.parseDouble(receipt.getTax().toString()) > 0) {

                    if (Double.parseDouble(receipt.getBilldisc().toString()) == 0) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "") + text(140, y, "") + text(180, y, "");
                    }

                    cpclConfigLabel += text(490, y, taxName)
                            + text(630, y, " : ")
                            + text(700, y, receipt.getTax().toString());
                }
                if (Double.parseDouble(receipt.getTax().toString()) == 0) {

                    cpclConfigLabel += text(490, y, "") + text(630, y, "")
                            + text(700, y, "");

                    if (Double.parseDouble(receipt.getBilldisc().toString()) == 0) {
                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                "Net Total")
                                + text(630, y, " : ")
                                + text(700, y, receipt.getNettot().toString());

                        //added new 31 aug 2016
                        String creditamount =receipt.getCreditAmount();
                        double creditVal=0;

                        if(creditamount!=null && !creditamount.isEmpty()){
                            creditVal = Double.valueOf(receipt.getCreditAmount());
                        }

                        if(creditVal>0){
                            cpclConfigLabel += text(490, y += LINE_SPACING,
                                    "Credit Amt")
                                    + text(630, y, " : ")
                                    + text(700, y, creditVal);
                        }
                        //end

                        if (!In_Cash.getPay_Mode().matches("")
                                || !In_Cash.getPay_Mode().matches("null")
                                || !In_Cash.getPay_Mode().matches(null)) {
                            String pay_Mode = In_Cash.getPay_Mode();
                            cpclConfigLabel += (text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Pay Mode"));
                            cpclConfigLabel += (text(150, y, " : "));
                            cpclConfigLabel += (text(180, y, pay_Mode));
                        }
                        if (Double.parseDouble(receipt.getPaidamount()) > 0) {
                            cpclConfigLabel += text(490, y, "Paid Amount")
                                    + text(630, y, " : ")
                                    + text(700, y, receipt.getPaidamount()
                                    .toString());
                        }

                    } else {

                        if (Double
                                .parseDouble(receipt.getBilldisc().toString()) > 0) {
                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y , "Bill Disc")
                                    + text(140, y, " : ")
                                    + text(180, y, receipt.getBilldisc()
                                    .toString());
                        }

                        cpclConfigLabel += text(490, y, "Net Total")
                                + text(630, y, " : ")
                                + text(700, y, receipt.getNettot().toString());

                        //added new 31 aug 2016
                        String creditamount =receipt.getCreditAmount();
                        double creditVal=0;

                        if(creditamount!=null && !creditamount.isEmpty()){
                            creditVal = Double.valueOf(receipt.getCreditAmount());
                        }

                        if(creditVal>0){
                            cpclConfigLabel += text(490, y += LINE_SPACING,
                                    "Credit Amt")
                                    + text(630, y, " : ")
                                    + text(700, y, creditVal);
                        }
                        // end

                        if (!In_Cash.getPay_Mode().matches("")
                                || !In_Cash.getPay_Mode().matches("null")
                                || !In_Cash.getPay_Mode().matches(null)) {
                            String pay_Mode = In_Cash.getPay_Mode();
                            cpclConfigLabel += (text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Pay Mode"));
                            cpclConfigLabel += (text(150, y, " : "));
                            cpclConfigLabel += (text(180, y, pay_Mode));
                        }
                        if (Double.parseDouble(receipt.getPaidamount()) > 0) {
                            cpclConfigLabel += text(490, y, "Paid Amount")
                                    + text(630, y, " : ")
                                    + text(700, y, receipt.getPaidamount()
                                    .toString());
                        }
                    }
                } else {
                    cpclConfigLabel += text(490, y += LINE_SPACING, "Net Total")
                            + text(630, y, " : ")
                            + text(700, y, receipt.getNettot().toString());

                    //added new 31 aug 2016
                    String creditamount =receipt.getCreditAmount();
                    double creditVal=0;

                    if(creditamount!=null && !creditamount.isEmpty()){
                        creditVal = Double.valueOf(receipt.getCreditAmount());
                    }

                    if(creditVal>0){
                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                "Credit Amt")
                                + text(630, y, " : ")
                                + text(700, y, creditVal);
                    }
                    // end
                    if (!In_Cash.getPay_Mode().matches("")
                            || !In_Cash.getPay_Mode().matches("null")
                            || !In_Cash.getPay_Mode().matches(null)) {
                        String pay_Mode = In_Cash.getPay_Mode();
                        cpclConfigLabel += (text(LEFT_MARGIN,
                                y += LINE_SPACING, "Pay Mode"));
                        cpclConfigLabel += (text(150, y, " : "));
                        cpclConfigLabel += (text(180, y, pay_Mode));
                    }
                    if (Double.parseDouble(receipt.getPaidamount()) > 0) {
                        cpclConfigLabel += text(490, y, "Paid Amount")
                                + text(630, y, " : ")
                                + text(700, y, receipt.getPaidamount()
                                .toString());
                    }
                }


                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                        LINE_THICKNESS);
                if (!In_Cash.getPay_Mode().matches("")
                        || !In_Cash.getPay_Mode().matches("null")
                        || !In_Cash.getPay_Mode().matches(null)) {
                    String pay_Mode = In_Cash.getPay_Mode();

                    if (pay_Mode.matches("Cheque")
                            || pay_Mode.matches("cheque")) {

                        bank_Code = In_Cash.getBank_code();
                        // bank_Name = In_Cash.getBank_Name();
                        check_No = In_Cash.getCheck_No();
                        check_Date = In_Cash.getCheck_Date();

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Bank Code");
                        cpclConfigLabel += text(200, y, "Cheque No");
                        cpclConfigLabel += text(420, y, "Cheque Date");
                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                bank_Code);
                        cpclConfigLabel += text(200, y, check_No);
                        cpclConfigLabel += text(420, y, check_Date);

                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);

                    }

                }

                if (!receipt.getRemarks().equals("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Remarks")
                            + text(140, y, " : ")
                            + text(180,
                            y,
                            (receipt.getRemarks().length() > 25) ? receipt
                                    .getRemarks().substring(0, 24)
                                    : receipt.getRemarks());

                    cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                            LINE_THICKNESS);
                }

                if (receipt.getPaidamount() != null
                        && !receipt.getPaidamount().isEmpty()
                        && receipt.getNettot() != null
                        && !receipt.getNettot().isEmpty()) {

                    double netTotal = Double.valueOf(receipt.getNettot());
                    double amountPaid = Double.valueOf(receipt.getPaidamount());

                    String creditamt =receipt.getCreditAmount();
                    double creditValue=0;

                    if(creditamt!=null && !creditamt.isEmpty()){
                        creditValue = Double.valueOf(receipt.getCreditAmount());
                    }


                    if (netTotal > 0 && amountPaid > 0
                            && netTotal != amountPaid) {

                        Log.d("netTotal", ""+netTotal);
                        Log.d("amountPaid", ""+amountPaid);
                        Log.d("creditValue", ""+creditValue);


//                        double invoiceOutstanding = netTotal - (amountPaid + creditValue);

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Invoice Outstanding")
                                + text(630, y, " : ")
                                + text(700, y, receipt.getBalanceAmount());

                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);

                    }
                }

                totalOutStanding = receipt.getTotaloutstanding();

            }
            if (showTotalOutstanding.matches("True")) {

                if (totalOutStanding != null && !totalOutStanding.isEmpty()
                        && !totalOutStanding.matches("null")) {

                    if (Double.valueOf(totalOutStanding) > 0) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Total Outstanding")
                                + text(630, y, " : ")
                                + text(700, y, totalOutStanding);

                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);

                    }
                }
            }

            // if (showTotalOutstanding.matches("True")) {
            // cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
            // }
            if(salesReturnArr.size()>0)
            {
                double dTotal = 0.00;
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,LINE_THICKNESS);
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No");
                cpclConfigLabel += text(350, y, "SR Date");
                cpclConfigLabel += text(675, y, "Amount");
                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,LINE_THICKNESS);

                for(int i=0;i<salesReturnArr.size();i++){
                    String salesReturnNo =  salesReturnArr.get(i).get("SalesReturnNo");
                    String salesReturnDate =  salesReturnArr.get(i).get("SalesReturnDate");
                    String creditAmount =  salesReturnArr.get(i).get("CreditAmount");
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, salesReturnNo);
                    cpclConfigLabel += text(350, y, salesReturnDate);
                    cpclConfigLabel += text(675, y, creditAmount);

                    if(creditAmount!=null && !creditAmount.isEmpty()){
                        dTotal += Double.valueOf(creditAmount);
                    }

                }

                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,LINE_THICKNESS);
                cpclConfigLabel += text(490, y+= LINE_SPACING, "Total")
                        + text(630, y, " : ")
                        + text(700, y, twoDecimalPoint(dTotal));
                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,LINE_THICKNESS);
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            }
            SOTDatabase.init(context);
            String img = SOTDatabase.getSignatureImage();
		/*	if (!img.matches("")) {
				Log.d("Do Nothing", "Do Nothing");
				Log.d("Signature image", img);
			} else {
				Log.d("footerArr", "" + footerArr.size());

				if (footerArr.size() > 0) {
					for (ProductDetails footer : footerArr) {

						Log.d("footer value",
								"val " + footer.getReceiptMessage());

						if (footer.getReceiptMessage() != null
								&& !footer.getReceiptMessage().isEmpty()) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING,
									footer.getReceiptMessage());
						}
					}
				}
			}*/

            if (showFooter.matches("True")) {

                if (img!=null && !img.isEmpty()) {

                    Log.d("Do Nothing", "Do Nothing");

                }else{ //without signature

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
                    // LINE_THICKNESS);


                 /*   cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/

                    cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                            LINE_THICKNESS);

                    cpclConfigLabel += text(75, y += LINE_SPACING,
                            "Received By");

                    cpclConfigLabel += text(600, y, "Authorized By");

                    Log.d("footerArr", "" + footerArr.size());

                    if (footerArr.size() > 0) {


                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);

                        for (ProductDetails footer : footerArr) {

                            Log.d("footer value",
                                    "val " + footer.getReceiptMessage());

                            if (footer.getReceiptMessage() != null
                                    && !footer.getReceiptMessage().isEmpty()) {
                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING,
                                        footer.getReceiptMessage());
                            }
                        }
                    }
                    y+=70;
                }
            }

            cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
            Log.d("cpclConfigLabel",""+cpclConfigLabel);


			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
            os.write(cpclConfigLabel.getBytes());
            os.flush();
        }
        os.close();

    }
    public void printDeliveryOnInvoice(String invoiceno, String invoicedate,
                                       String customercode, String customername,
                                       List<ProductDetails> product, List<ProductDetails> productdet,
                                       List<String> printsortHeader, String gnrlStngs, int nofcopies,
                                       List<ProductDetails> product_batch, List<ProductDetails> footerArr)
            throws IOException {

        helper.showProgressDialog(context.getString(R.string.print),
                context.getString(R.string.creating_file_for_printing));
        try {
            deliveryOrderOnInvoice(invoiceno, invoicedate, customercode,
                    customername, product, productdet, printsortHeader,
                    gnrlStngs, nofcopies, product_batch, footerArr);
            if (isBluetoothEnabled()) {
                print();

            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }

        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }

    public void deliveryOrderOnInvoice(String invoiceno, String invoicedate,
                                       String customercode, String customername,
                                       List<ProductDetails> product, List<ProductDetails> productdet,
                                       List<String> printsortHeader, String gnrlStngs, int nofcopies,
                                       List<ProductDetails> product_batch, List<ProductDetails> footerValue)
            throws IOException, ZebraPrinterConnectionException {
        // Used the calculate the y axis printing position dynamically

        // String invoiceprintdetail = SalesOrderSetGet.getInvoiceprintdetail();

        logoStr = "logoprint";
        footerArr.clear();
        footerArr = footerValue;

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
        String showcartonloose = SalesOrderSetGet
                .getCartonpriceflag();
        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        for (int n = 0; n < nofcopies; n++) {
            double totalqty = 0;
            int y = 0;
            int s = 1;

            StringBuilder temp = new StringBuilder();
            y = printTitle(205, y, "DELIVERY ORDER", temp);
            y = printCompanyDetails(y, temp);

            String cpclConfigLabel = temp.toString();

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                    "Invoice No")
                    + text(150, y, " : ")
                    + text(180, y, invoiceno);
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                    "Invoice Date")
                    + text(150, y, " : ")
                    + text(180, y, invoicedate);

            if (showCustomerCode.matches("True")) {

                if (!customercode.matches("")) {
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                            "Cust Code"));
                    cpclConfigLabel += (text(150, y, " : "));
                    cpclConfigLabel += (text(180, y, customercode));
                }
            }

            if (showCustomerName.matches("True")) {

                if (!customername.matches("")) {
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                            "Cust Name"));
                    cpclConfigLabel += (text(150, y, " : "));

                    cpclConfigLabel += (textChinese(
                            180,
                            y,
                            (customername.length() > 25) ? customername
                                    .substring(0, 24) : customername));

                    if (customername.length() > 25) {

                        String custlastname = customername.substring(24);

                        Log.d("Custnameeee", custlastname);

                        cpclConfigLabel += (textChinese(
                                180,
                                y += LINE_SPACING,
                                (custlastname.length() > 25) ? custlastname
                                        .substring(0, 24) : custlastname));
                    }
                }
            }

            if (showAddress1.matches("True")) {
                if (!CustomerSetterGetter.getCustomerAddress1().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Address 1")
                            + text(150, y, " : ")
                            + textChinese(180, y,
                            CustomerSetterGetter.getCustomerAddress1());
                }
            }

            if (showAddress2.matches("True")) {
                if (!CustomerSetterGetter.getCustomerAddress2().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Address 2")
                            + text(150, y, " : ")
                            + textChinese(180, y,
                            CustomerSetterGetter.getCustomerAddress2());
                }
            }

            if (showAddress3.matches("True")) {
                if (!CustomerSetterGetter.getCustomerAddress3().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Address 3")
                            + text(150, y, " : ")
                            + textChinese(180, y,
                            CustomerSetterGetter.getCustomerAddress3());
                }
            }

            if (showCustomerPhone.matches("True")) {
                if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Phone No")
                            + text(150, y, " : ")
                            + text(180, y,
                            CustomerSetterGetter.getCustomerPhone());
                }
            }

            if (showCustomerHP.matches("True")) {
                if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Head Phone")
                            + text(150, y, " : ")
                            + text(180, y, CustomerSetterGetter.getCustomerHP());
                }
            }

            if (showCustomerEmail.matches("True")) {
                if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Email")
                            + text(150, y, " : ")
                            + text(180, y,
                            CustomerSetterGetter.getCustomerEmail());
                }
            }

            if (showCustomerTerms.matches("True")) {
                if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Terms")
                            + text(150, y, " : ")
                            + text(180, y,
                            CustomerSetterGetter.getCustomerTerms());

                }
            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            if(product.size()>0){
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                cpclConfigLabel += text(70, y, "Description");
                cpclConfigLabel += text(720, y, "Qty");

                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
                if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {
                    for (ProductDetails products : product) {

                        if ((products.getSortproduct().matches(""))
                                || (products.getSortproduct().matches("0"))) {
                            int i = 1;
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    String.valueOf(i));

                            cpclConfigLabel += textChinese(
                                    70,
                                    y,
                                    (products.getDescription().length() > 31) ? products
                                            .getDescription().substring(0, 30)
                                            : products.getDescription());

                            //showing qty
                            cpclConfigLabel += text(720, y, products.getQty()
                                    .toString());
                            if (products.getQty() != null
                                    && !products.getQty().isEmpty()) {
                                totalqty += Double.valueOf(products.getQty());
                            }
                            //  }
                            if ((products.getIssueQty() != null && !products
                                    .getIssueQty().isEmpty())
                                    && (products.getReturnQty() != null && !products
                                    .getReturnQty().isEmpty())) {

                                if ((Double.valueOf(products.getIssueQty()) > 0)
                                        && (Double.valueOf(products.getReturnQty()) > 0)) {

                                    cpclConfigLabel += text(70, y += LINE_SPACING,
                                            "Issue")
                                            + text(210, y, " : ")
                                            + text(280, y, products.getIssueQty());

                                    cpclConfigLabel += text(550, y, "Return")
                                            + text(670, y, " : ")
                                            + text(720, y, products.getReturnQty());

                                }
                            }

                            s += i;
                            i++;
                        }
                    }
                    for (int i = 0; i < printsortHeader.size(); i++) {
                        String catorsub = printsortHeader.get(i).toString();
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                catorsub + " :");
                        for (ProductDetails prods : product) {
                            if (catorsub.equalsIgnoreCase(prods.getSortproduct().toString())) {

                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING, String.valueOf(s));

                                cpclConfigLabel += textChinese(
                                        70,
                                        y,
                                        (prods.getDescription().length() > 31) ? prods
                                                .getDescription().substring(0, 30)
                                                : prods.getDescription());

                                //showing qty
                                cpclConfigLabel += text(720, y, prods.getQty()
                                        .toString());

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

                                        cpclConfigLabel += text(70,
                                                y += LINE_SPACING, "Issue")
                                                + text(210, y, " : ")
                                                + text(280, y, prods.getIssueQty());

                                        cpclConfigLabel += text(550, y, "Return")
                                                + text(670, y, " : ")
                                                + text(720, y, prods.getReturnQty());

                                    }
                                }

                                s++;

                            }

                        }
                    }
                } else {
                    for (ProductDetails products : product) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                products.getSno().toString());

                        cpclConfigLabel += textChinese(70, y, (products.getDescription()
                                .length() > 31) ? products.getDescription()
                                .substring(0, 30) : products.getDescription());

                        //showing qty
                        cpclConfigLabel += text(720, y, products.getQty()
                                .toString());
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

                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Issue")
                                        + text(210, y, " : ")
                                        + text(280, y, products.getIssueQty());

                                cpclConfigLabel += text(550, y, "Return")
                                        + text(670, y, " : ")
                                        + text(720, y, products.getReturnQty());

                            }
                        }

                    }
                }
                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

                cpclConfigLabel += text(550, y += LINE_SPACING, "Total Qty")
                        + text(670, y, " : ")
                        + text(720, y, String.valueOf(totalqty).split("\\.")[0]);

                cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            }

            SOTDatabase.init(context);
            String img = SOTDatabase.getSignatureImage();

            if (showFooter.matches("True")) {

                if (!img.matches("")) {
                    Log.d("Do Nothing", "Do Nothing");
                    Log.d("Signature image", img);
                } else {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
                    // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
                    // LINE_THICKNESS);
                    /*cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/
                    cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

                    cpclConfigLabel += text(75, y += LINE_SPACING,
                            "Received By");

                    cpclConfigLabel += text(600, y, "Authorized By");

                    Log.d("footerArr", "" + footerArr.size());

                    if (footerArr.size() > 0) {

                        // cpclConfigLabel += text(LEFT_MARGIN, y +=
                        // LINE_SPACING,
                        // "--------------*********---------------");
                        // cpclConfigLabel += text(230, y += LINE_SPACING, "");
                        // cpclConfigLabel += text(230, y += LINE_SPACING,
                        // "*********");
                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);

                        for (ProductDetails footer : footerArr) {

                            Log.d("footer value",
                                    "val " + footer.getReceiptMessage());

                            if (footer.getReceiptMessage() != null
                                    && !footer.getReceiptMessage().isEmpty()) {
                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING,
                                        footer.getReceiptMessage());
                            }
                        }
                    }

                }
            }

            // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
            // LINE_THICKNESS);
            // Just append everything and create a single string
            cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
            Log.d("cpclConfigLabel",""+cpclConfigLabel);

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
            os.write(cpclConfigLabel.getBytes());
            os.flush();
        }
        // ///////////////////////////////////////////////////////////////////////////////////

        os.close();

    }

    public void printReceiptSummary(String custcode,String customername,String receiptno,String receiptdate,
                                    List<ProductDetails> receiptlist, List<ProductDetails> footerArr) {
        helper.showProgressDialog(context.getString(R.string.print),
                context.getString(R.string.creating_file_for_printing));
        try {
            createReceiptSummary(custcode, customername,receiptno, receiptdate, receiptlist, footerArr);
            if (isBluetoothEnabled()) {
                logoStr = "logoprint";
                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (IOException e) {
            e.printStackTrace();
            helper.dismissProgressDialog();
            helper.showErrorDialog(R.string.error_creating_file_for_printing);
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void createReceiptSummary(String custcode,String customername,String receiptno,String receiptdate,
                                      List<ProductDetails> receiptlist, List<ProductDetails> footerValue)
            throws IOException {
        // TODO Auto-generated method stub

        logoStr = "logoprint";
        footerArr.clear();
        footerArr = footerValue;

        Log.d("receipt logoStr", "rece" + logoStr);

        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);

        int y = 0;
        String paymode_cash = "", paymode_cheque = "", paymode_other = "";
        double cashamount = 0.00, chequeamount = 0.00, total = 0.00, otheramount = 0.00;
        StringBuilder temp = new StringBuilder();
        y = printTitle(210, y, "RECEIPT SUMMARY", temp);
        y = printCompanyDetails(y, temp);

        String cpclConfigLabel = temp.toString();

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Customer Code")
                + text(170, y, " : ") + text(250, y, custcode);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Customer Name")
                + text(170, y, " : ") + text(250, y, customername);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt No")
                + text(170, y, " : ") + text(250, y, receiptno);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt Date")
                + text(170, y, " : ") + text(250, y, receiptdate);

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No");
        cpclConfigLabel += text(200, y, "Invoice Date");
        cpclConfigLabel += text(440, y, "Net Total");
        cpclConfigLabel += text(650, y, "Paid Amount");

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
        for (ProductDetails receipt : receiptlist) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt
                    .getItemno().toString());
            cpclConfigLabel += text(200, y, receipt.getItemdate());
            cpclConfigLabel += text(470, y, receipt.getNettot());
            cpclConfigLabel += text(720, y, receipt.getPaidamount());

            if (!In_Cash.getPay_Mode().matches("")
                    || !In_Cash.getPay_Mode().matches("null")
                    || !In_Cash.getPay_Mode().matches(null)) {
                String pay_Mode = In_Cash.getPay_Mode();
                if (pay_Mode.matches("cash")) {
                    cashamount += Double.parseDouble(receipt.getPaidamount());
                    paymode_cash = "Cash";
                    Log.d("cashamount", "-->" + cashamount);
                } else if (pay_Mode.toLowerCase().matches("cheque")) {
                    chequeamount += Double.parseDouble(receipt.getPaidamount());
                    paymode_cheque = "Cheque";
                    Log.d("chequeamount", "-->" + chequeamount);
                } else {
                    otheramount += Double.parseDouble(receipt.getPaidamount());
                    paymode_other = "Others";
                    Log.d("otheramount", "-->" + otheramount);
                }
                total += Double.parseDouble(receipt.getPaidamount());
            }
        }

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

        if (paymode_cash.toLowerCase().matches("cash")
                && paymode_cheque.toLowerCase().matches("cheque")
                && paymode_other.toLowerCase().matches("others")) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
                    + text(140, y, " : ") + text(180, y, chequeamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
                    + text(140, y, " : ") + text(180, y, otheramount);

            cpclConfigLabel += text(530, y, "Total Paid") + text(630, y, " : ")
                    + text(720, y, total);

        } else if (paymode_cash.toLowerCase().matches("cash")
                && paymode_cheque.toLowerCase().matches("cheque")) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
                    + text(140, y, " : ") + text(180, y, chequeamount);
            cpclConfigLabel += text(530, y, "Total Paid") + text(630, y, " : ")
                    + text(720, y, total);
        } else if (paymode_cash.toLowerCase().matches("cash")
                && paymode_other.toLowerCase().matches("others")) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
                    + text(140, y, " : ") + text(180, y, otheramount);
            cpclConfigLabel += text(530, y, "Total Paid") + text(630, y, " : ")
                    + text(720, y, total);
        } else if (paymode_cheque.toLowerCase().matches("cheque")
                && paymode_other.toLowerCase().matches("others")) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
                    + text(140, y, " : ") + text(180, y, chequeamount);
            cpclConfigLabel += text(530, y, "Total Paid") + text(630, y, " : ")
                    + text(720, y, total);
        } else if (paymode_cash.toLowerCase().matches("cash")) {
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(530, y, "Total Paid") + text(630, y, " : ")
                    + text(720, y, total);
        } else if (paymode_cheque.toLowerCase().matches("cheque")) {
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
                    + text(140, y, " : ") + text(180, y, chequeamount);

            cpclConfigLabel += text(530, y, "Total Paid") + text(630, y, " : ")
                    + text(720, y, total);
        } else {
            cpclConfigLabel += text(530,  y += LINE_SPACING, "Total Paid") + text(630, y, " : ")
                    + text(720, y, total);
        }

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

        cpclConfigLabel += text(75, y += LINE_SPACING, "Received By");

        cpclConfigLabel += text(600, y, "Authorized By");


        Log.d("footerArr", "" + footerArr.size());

        if (footerArr.size() > 0) {

            // cpclConfigLabel += text(230, y += LINE_SPACING, "*********");
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            for (ProductDetails footer : footerArr) {

                Log.d("footer value", "val " + footer.getReceiptMessage());

                if (footer.getReceiptMessage() != null
                        && !footer.getReceiptMessage().isEmpty()) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            footer.getReceiptMessage());
                }
            }
        }

        cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
        Log.d("cpclConfigLabel",""+cpclConfigLabel);
        os.write(cpclConfigLabel.getBytes());
        os.flush();
        os.close();

    }

    public void printSalesReturn(String srno, String srdate,
                                 String customercode, String customername,
                                 List<ProductDetails> product, List<ProductDetails> productdet,List<String> printSortHeader, String appPrintGroup,
                                 int nofcopies) throws IOException {
        helper.showProgressDialog(context.getString(R.string.print),
                context.getString(R.string.creating_file_for_printing));
        try {
            createSalesReturn(srno, srdate, customercode, customername,
                    product, productdet,printSortHeader,appPrintGroup, nofcopies);
            // test();
            if (isBluetoothEnabled()) {
                logoStr = "logoprint";
                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }


    private void createSalesReturn(String srno, String srdate,
                                   String customercode, String customername,
                                   List<ProductDetails> product, List<ProductDetails> productdet,List<String> printSortHeader, String appPrintGroup,
                                   int nofcopies) throws IOException {

        logoStr = "logoprint";

        Log.d("salesreturn logoStr", "salret" + logoStr);
        String taxType="",taxPerc="", taxName="";
        String showFooter = MobileSettingsSetterGetter.getShowFooter();
        String showProductFullName = MobileSettingsSetterGetter.getShowProductFullName();
        taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
        if(taxPerc==null || taxPerc.trim().equals("")){
            taxPerc = "0.00";
        }
        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        for (int n = 0; n < nofcopies; n++) {
            int y = 0,s=0;

            StringBuilder temp = new StringBuilder();
            y = printTitle(260, y, "SALES RETURN", temp);
            y = printCompanyDetails(y, temp);

            String cpclConfigLabel = temp.toString();

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No")
                    + text(150, y, " : ") + text(180, y, srno);
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR Date")
                    + text(150, y, " : ") + text(180, y, srdate);

            cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                    "Cust Code"));
            cpclConfigLabel += (text(150, y, " : "));
            cpclConfigLabel += (text(180, y, customercode));
            cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                    "Cust Name"));
            cpclConfigLabel += (text(150, y, " : "));
            cpclConfigLabel += (textChinese(
                    180,
                    y,
                    (customername.length() > 25) ? customername
                            .substring(0, 24) : customername));

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
            cpclConfigLabel += text(70, y, "Description");
            cpclConfigLabel += text(480, y, "Qty");
            cpclConfigLabel += text(565, y, "Price");
            cpclConfigLabel += text(700, y, "Total");

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            int no = 1;
            if (appPrintGroup.matches("C") || appPrintGroup.matches("S")) {
                for (ProductDetails products : product) {

                    if ((products.getSortproduct().matches(""))
                            || (products.getSortproduct().matches("0"))) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                String.valueOf(no));

                        if (showProductFullName.matches("True")) {
                            cpclConfigLabel += textChinese(70, y, (products
                                    .getDescription().length() > 31) ? products
                                    .getDescription().substring(0, 30)
                                    : products.getDescription());
                            cpclConfigLabel += text(480, y += LINE_SPACING,
                                    products.getQty().toString());
                        } else {
                            cpclConfigLabel += textChinese(70, y, (products
                                    .getDescription().length() > 10) ? products
                                    .getDescription().substring(0, 9)
                                    : products.getDescription());
                            cpclConfigLabel += text(480, y, products.getQty()
                                    .toString());
                        }

                        cpclConfigLabel += text(565, y, products.getPrice()
                                .toString());
                        cpclConfigLabel += text(700, y, products.getTotal()
                                .toString());



                        if (products.getFocqty() > 0) {
                            cpclConfigLabel += text(70, y += LINE_SPACING,
                                    "Foc")
                                    + text(210, y, " : ")
                                    + text(280, y, (int) products.getFocqty());
                        }


                       /* if (products.getExchangeqty() > 0) {
                            cpclConfigLabel += text(70, y += LINE_SPACING,
                                    "Exchange")
                                    + text(210, y, " : ")
                                    + text(280, y,
                                    (int) products.getExchangeqty());
                        }*/

                      /*  if ((products.getIssueQty() != null && !products
                                .getIssueQty().isEmpty())
                                && (products.getReturnQty() != null && !products
                                .getReturnQty().isEmpty())) {

                            if ((Double.valueOf(products.getIssueQty()) > 0)
                                    && (Double.valueOf(products.getReturnQty()) > 0)) {

                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Issue")
                                        + text(210, y, " : ")
                                        + text(280, y, products.getIssueQty());

                                cpclConfigLabel += text(375, y, "Return")
                                        + text(450, y, " : ")
                                        + text(486, y, products.getReturnQty());

                            }
                        }*/


                        s += no;
                        no++;
                    }
                    taxType = products.getTaxType();
                  //  taxPerc = products.getTaxPerc();
                }
                for (int i = 0; i < printSortHeader.size(); i++) {
                    String catorsub = printSortHeader.get(i).toString();
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            catorsub + " :");
                    for (ProductDetails prods : product) {
                        if (catorsub.equalsIgnoreCase(prods.getSortproduct().toString())) {

                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, String.valueOf(s));


                            if (showProductFullName.matches("True")) {
                                cpclConfigLabel += textChinese(
                                        70,
                                        y,
                                        (prods.getDescription().length() > 31) ? prods
                                                .getDescription().substring(0,
                                                        30) : prods
                                                .getDescription());
                                cpclConfigLabel += text(280, y += LINE_SPACING,
                                        prods.getQty().toString());
                            } else {
                                cpclConfigLabel += text(
                                        70,
                                        y,
                                        (prods.getDescription().length() > 10) ? prods
                                                .getDescription().substring(0,
                                                        9) : prods
                                                .getDescription());
                                cpclConfigLabel += text(280, y, prods.getQty()
                                        .toString());
                            }
                            cpclConfigLabel += text(565, y, prods.getPrice()
                                    .toString());
                            cpclConfigLabel += text(486, y, prods.getTotal()
                                    .toString());


                            if (prods.getFocqty() > 0) {
                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Foc")
                                        + text(210, y, " : ")
                                        + text(280, y, (int) prods.getFocqty());
                            }

                           /* if (prods.getExchangeqty() > 0) {
                                cpclConfigLabel += text(70, y += LINE_SPACING,
                                        "Exchange")
                                        + text(210, y, " : ")
                                        + text(280, y,
                                        (int) prods.getExchangeqty());
                            }*/

                          /*  if ((prods.getIssueQty() != null && !prods
                                    .getIssueQty().isEmpty())
                                    && (prods.getReturnQty() != null && !prods
                                    .getReturnQty().isEmpty())) {

                                if ((Double.valueOf(prods.getIssueQty()) > 0)
                                        && (Double
                                        .valueOf(prods.getReturnQty()) > 0)) {

                                    cpclConfigLabel += text(70,
                                            y += LINE_SPACING, "Issue")
                                            + text(210, y, " : ")
                                            + text(280, y, prods.getIssueQty());

                                    cpclConfigLabel += text(375, y, "Return")
                                            + text(450, y, " : ")
                                            + text(486, y, prods.getReturnQty());

                                }
                            }*/

                            s++;

                        }
                        taxType = prods.getTaxType();
                     //   taxPerc = prods.getTaxPerc();
                    }
                }


            }
            else{
                for (ProductDetails products : product) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            products.getSno().toString());


                    if (showProductFullName.matches("True")) {
                        cpclConfigLabel += textChinese(
                                70,
                                y,
                                (products.getDescription().length() > 31) ? products
                                        .getDescription().substring(0, 30)
                                        : products.getDescription());

                        cpclConfigLabel += text(480, y += LINE_SPACING,
                                products.getQty().toString());
                    } else {
                        cpclConfigLabel += textChinese(
                                70,
                                y,
                                (products.getDescription().length() > 10) ? products
                                        .getDescription().substring(0, 9)
                                        : products.getDescription());
                        cpclConfigLabel += text(480, y, products.getQty()
                                .toString());
                    }
                    cpclConfigLabel += text(565, y, products.getPrice()
                            .toString());
                    cpclConfigLabel += text(700, y, products.getTotal()
                            .toString());

                    Log.d("products.getFocqty() 3",
                            "" + (int) products.getFocqty());

                    if (products.getFocqty() > 0) {
                        cpclConfigLabel += text(70, y += LINE_SPACING, "Foc")
                                + text(210, y, " : ")
                                + text(280, y, "" + (int) products.getFocqty());
                    }

                    Log.d("getExchangeqty() 3",
                            "" + (int) products.getExchangeqty());

                 /*   if (products.getExchangeqty() > 0) {
                        cpclConfigLabel += text(70, y += LINE_SPACING,
                                "Exchange")
                                + text(210, y, " : ")
                                + text(280, y,
                                "" + (int) products.getExchangeqty());
                    }*/

                 /*   if ((products.getIssueQty() != null && !products
                            .getIssueQty().isEmpty())
                            && (products.getReturnQty() != null && !products
                            .getReturnQty().isEmpty())) {

                        if ((Double.valueOf(products.getIssueQty()) > 0)
                                && (Double.valueOf(products.getReturnQty()) > 0)) {

                            cpclConfigLabel += text(70, y += LINE_SPACING,
                                    "Issue")
                                    + text(210, y, " : ")
                                    + text(280, y, products.getIssueQty());

                            cpclConfigLabel += text(375, y, "Return")
                                    + text(450, y, " : ")
                                    + text(486, y, products.getReturnQty());

                        }
                    }*/
                    taxType = products.getTaxType();
                 //   taxPerc = products.getTaxPerc();
                }

            }
          /*  int i =0;
            for (ProductDetails prods : product) {

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(i));
                // cpclConfigLabel += text(140, y,
                // prods.getDescription().toString());

                cpclConfigLabel += textChinese(70, y, (prods.getDescription()
                        .length() > 10) ? prods.getDescription()
                        .substring(0, 9) : prods.getDescription());
                cpclConfigLabel += text(480, y, prods.getQty().toString());
                cpclConfigLabel += text(565, y, prods.getPrice().toString());
                cpclConfigLabel += text(700, y, prods.getTotal().toString());
                i++;

                taxType = prods.getTaxType();
                taxPerc = prods.getTaxPerc();
            }
*/
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            for (ProductDetails prd : productdet) {

                if(taxType.equalsIgnoreCase("I")){
                    taxName = "GST Incl "+taxPerc.split("\\.")[0]+"%";
                }else if(taxType.equalsIgnoreCase("E")){
                    taxName = "GST Excl "+taxPerc.split("\\.")[0]+"%";
                }else{
                    taxName = "Tax";
                }

                Log.d("taxType","-->"+taxType);
                /********************/
                //If taxtype is I then subtotal = netotal - tax
                String netTotal = prd.getNettot().toString();
                if(netTotal!=null && !netTotal.isEmpty()){

                }else{
                    netTotal = "0.00";
                }
                String tax = prd.getTax().toString();
                if(tax!=null && !tax.isEmpty()){

                }else{
                    tax = "0.00";
                }
                double dNetTotal = Double.valueOf(netTotal);
                double dTax = Double.valueOf(tax);
                double dSubtotal =  dNetTotal - dTax;
                Log.d("dNetTotal","-->"+dNetTotal);
                Log.d("dTax","-->"+dTax);
                Log.d("subtotal","-->"+dSubtotal);
                /********************/

                if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Item Disc")
                            + text(140, y, " : ")
                            + text(180, y, prd.getItemdisc().toString());
                }

                if (Double.parseDouble(prd.getTax().toString()) > 0) {

                    if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

                        if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
                            cpclConfigLabel += text(LEFT_MARGIN,
                                    y += LINE_SPACING, "Bill Disc")
                                    + text(140, y, " : ")
                                    + text(180, y, prd.getBilldisc().toString());
                        }
                        if (Double.parseDouble(prd.getBilldisc().toString()) == 0
                                && Double.parseDouble(prd.getItemdisc()
                                .toString()) == 0) {

                            if(taxType!=null && !taxType.isEmpty()){
                                if (taxType.matches("I")) {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(630, y, " : ")
                                            + text(700, y, twoDecimalPoint(dSubtotal));

                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(630, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                            }else{
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(630, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());
                            }

//                            cpclConfigLabel += text(490, y += LINE_SPACING,
//                                    "Sub Total")
//                                    + text(630, y, " : ")
//                                    + text(700, y, prd.getSubtotal().toString());
                        } else {

                            if(taxType!=null && !taxType.isEmpty()){
                                if (taxType.matches("I")) {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(630, y, " : ")
                                            + text(700, y, twoDecimalPoint(dSubtotal));

                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(630, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                            }else{
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(630, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());
                            }

//                            cpclConfigLabel += text(490, y, "Sub Total")
//                                    + text(630, y, " : ")
//                                    + text(700, y, prd.getSubtotal().toString());
                        }
                    } else {
                        if(taxType!=null && !taxType.isEmpty()){
                            if (taxType.matches("I")) {
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(630, y, " : ")
                                        + text(700, y, twoDecimalPoint(dSubtotal));

                            }else{
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(630, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());
                            }
                        }else{
                            cpclConfigLabel += text(490, y += LINE_SPACING,
                                    "Sub Total")
                                    + text(630, y, " : ")
                                    + text(700, y, prd.getSubtotal().toString());
                        }
//                        cpclConfigLabel += text(490, y, "Sub Total")
//                                + text(630, y, " : ")
//                                + text(700, y, prd.getSubtotal().toString());
                    }
                }

                if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {

                    if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Bill Disc")
                                + text(140, y, " : ")
                                + text(180, y, prd.getBilldisc().toString());
                    } else {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "") + text(140, y, "") + text(180, y, "");
                    }
                }

                if (Double.parseDouble(prd.getTax().toString()) > 0) {

                    if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "") + text(140, y, "") + text(180, y, "");
                    }

                    cpclConfigLabel += text(490, y, taxName)
                            + text(630, y, " : ")
                            + text(700, y, prd.getTax().toString());
                }
                if (Double.parseDouble(prd.getTax().toString()) == 0) {

                    cpclConfigLabel += text(490, y, "") + text(630, y, "")
                            + text(700, y, "");

                    if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                "Net Total")
                                + text(630, y, " : ")
                                + text(700, y, prd.getNettot().toString());

                    } else {
                        cpclConfigLabel += text(490, y, "Net Total")
                                + text(630, y, " : ")
                                + text(700, y, prd.getNettot().toString());
                    }
                } else {
                    cpclConfigLabel += text(490, y += LINE_SPACING, "Net Total")
                            + text(630, y, " : ")
                            + text(700, y, prd.getNettot().toString());
                }

            }
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                    LINE_THICKNESS);

            SOTDatabase.init(context);
            String img = SOTDatabase.getSignatureImage();

            if (showFooter.matches("True")) {

                if (img!=null && !img.isEmpty()) {

                    Log.d("Do Nothing", "Do Nothing");

                }else{ //without signature

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

                    // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
                    // LINE_THICKNESS);
                   /* cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/
                    cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                            LINE_THICKNESS);
                    cpclConfigLabel += text(75, y += LINE_SPACING,
                            "Received By");

                    cpclConfigLabel += text(600, y, "Authorized By");

                    Log.d("footerArr", "" + footerArr.size());

                    if (footerArr.size() > 0) {

                        // cpclConfigLabel += text(LEFT_MARGIN, y +=
                        // LINE_SPACING,
                        // "--------------*********---------------");
                        // cpclConfigLabel += text(230, y += LINE_SPACING, "");
                        // cpclConfigLabel += text(230, y += LINE_SPACING,
                        // "*********");
                        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                                LINE_THICKNESS);

                        for (ProductDetails footer : footerArr) {

                            Log.d("footer value",
                                    "val " + footer.getReceiptMessage());

                            if (footer.getReceiptMessage() != null
                                    && !footer.getReceiptMessage().isEmpty()) {
                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING,
                                        footer.getReceiptMessage());
                            }
                        }
                    }

                    y+=70;

                }

            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
            Log.d("cpclConfigLabel",""+cpclConfigLabel);

            os.write(cpclConfigLabel.getBytes());
            os.flush();
        }
        os.close();

    }
    public void printInvoiceDate(String flag,String user,String fromDate,String toDate,String locationCode,
                                 String locationName,
                                 ArrayList<Product> ProductListArray) throws IOException {
        helper.showProgressDialog(R.string.print,
                R.string.creating_file_for_printing);
        try {

            createInvoiceDate(flag,user,fromDate,toDate,locationCode, locationName,
                    ProductListArray);

            if (isBluetoothEnabled()) {
                  logoStr = "logoprint";
                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void createInvoiceDate(String flag,String user,String fromDate,String toDate,String locationCode,
                                   String locationName,
                                   ArrayList<Product> InvoiceArray) throws IOException {
        int totalqty = 0;
        double nettotal = 0.00;

        mUser = user;
         logoStr = "logoprint";

        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        int y = 0;
        StringBuilder temp = new StringBuilder();
        y = printTitle(200, y, flag, temp);
        y = printCompanyDetails(y, temp);

        String cpclConfigLabel = temp.toString();
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "From Date")
                + text(170, y, " : ") + text(190, y, fromDate);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "To Date")
                + text(170, y, " : ") + text(190, y, toDate);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Code")
                + text(170, y, " : ") + text(190, y, locationCode);
        System.out.println("locationName==>" + locationName);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Name")
                + text(170, y, " : ") + text(190, y, locationName);
        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

        if(flag.matches("Invoice By Product")){
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Product Name");
            cpclConfigLabel += text(550, y, "Carton");
            cpclConfigLabel += text(650, y, "Loose");
            cpclConfigLabel += text(700, y, "Qty");

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            for (Product product : InvoiceArray) {
                cpclConfigLabel += textChinese(
                        LEFT_MARGIN,
                        y += LINE_SPACING,
                        (product.getProductName().length() > 20) ? product
                                .getProductName().substring(0, 19) : product
                                .getProductName());
                cpclConfigLabel += text(550, y,String.valueOf(product.getCqty()));
                cpclConfigLabel += text(650, y, product.getLqty().toString());
                cpclConfigLabel += text(700, y, product.getQty());
                if (product.getQty() != null&& !product.getQty().isEmpty()) {
                    totalqty += Integer.valueOf(product.getQty());
                }
            }
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            String totalQty = String.valueOf(totalqty);
            cpclConfigLabel += text(590, y += LINE_SPACING, "Total Quantity ")
                    + text(650, y, " : ")
                    + text(700, y, String.valueOf(totalQty));

        }else if(flag.matches("Invoice Summary")){
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No");
            cpclConfigLabel += text(250, y, "Customer Name");
            cpclConfigLabel += text(700, y, "Total");

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            for (Product product : InvoiceArray) {

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, product
                        .getNo().toString());

                cpclConfigLabel += textChinese(
                        250,
                        y,
                        (product.getName().length() > 10) ? product
                                .getName().substring(0, 9) : product
                                .getName());
                cpclConfigLabel += text(700, y, product.getNetTotal());

                if (product.getNetTotal() != null&& !product.getNetTotal().isEmpty()) {
                    nettotal += Double.valueOf(product.getNetTotal());
                }
            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            cpclConfigLabel += text(590, y += LINE_SPACING, "Total")
                    + text(650, y, " : ")
                    + text(700, y, twoDecimalPoint(nettotal));
        }




        //  y = y+40;
        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);


        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
        // LINE_THICKNESS);
                   /* cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/
        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                LINE_THICKNESS);
        cpclConfigLabel += text(75, y += LINE_SPACING,
                "Received By");

        cpclConfigLabel += text(600, y, "Authorized By");

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

        y+=70;

        // cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";

        cpclConfigLabel = ZPL_CMD_START + y + FONT + cpclConfigLabel + ZPL_CMD_END;
        // cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
        Log.d("cpclConfigLabel","-->"+cpclConfigLabel);



        os.write(cpclConfigLabel.getBytes());
        os.flush();
        os.close();
    }
    public void printCurrentDateReceipt(String receiptdate,
                                        ArrayList<Receipt> receiptlist, List<ProductDetails> footerArr) {
        helper.showProgressDialog(context.getString(R.string.print),
                context.getString(R.string.creating_file_for_printing));
        try {
            createCurrentReceiptFile(receiptdate, receiptlist, footerArr);
            if (isBluetoothEnabled()) {
                logoStr = "logoprint";
                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (IOException e) {
            e.printStackTrace();
            helper.dismissProgressDialog();
            helper.showErrorDialog(R.string.error_creating_file_for_printing);
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }


    private void createCurrentReceiptFile(String receiptdate,
                                          ArrayList<Receipt> receiptlist, List<ProductDetails> footerValue)
            throws IOException {
        // TODO Auto-generated method stub

        logoStr = "logoprint";
        footerArr.clear();
        footerArr = footerValue;

        Log.d("receipt logoStr", "rece" + logoStr);

        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);

        int y = 0;
        String paymode_cash = "", paymode_cheque = "", paymode_other = "";
        double cashamount = 0.00, chequeamount = 0.00, total = 0.00, otheramount = 0.00;
        StringBuilder temp = new StringBuilder();
        y = printTitle(200, y, "RECEIPT SUMMARY", temp);
        y = printCompanyDetails(y, temp);

        String cpclConfigLabel = temp.toString();

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt Date")
                + text(170, y, " : ") + text(190, y, receiptdate);

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt No");
        cpclConfigLabel += text(300, y, "Customer Name");
        cpclConfigLabel += text(700, y, "Total");



        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
        for (Receipt receipt : receiptlist) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt
                    .getReceiptno().toString());
            // cpclConfigLabel += text(140, y,
            // prods.getDescription().toString());
            cpclConfigLabel += textChinese(
                    300,
                    y,
                    (receipt.getCustomername().length() > 10) ? receipt
                            .getCustomername().substring(0, 9) : receipt
                            .getCustomername());
            cpclConfigLabel += text(700, y, receipt.getPaidamount());

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
        }

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

        if (paymode_cash.toLowerCase().matches("cash")
                && paymode_cheque.toLowerCase().matches("cheque")
                && paymode_other.toLowerCase().matches("others")) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
                    + text(140, y, " : ") + text(180, y, chequeamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
                    + text(140, y, " : ") + text(180, y, otheramount);

            cpclConfigLabel += text(590, y, "Total") + text(650, y, " : ")
                    + text(700, y, total);

        } else if (paymode_cash.toLowerCase().matches("cash")
                && paymode_cheque.toLowerCase().matches("cheque")) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
                    + text(140, y, " : ") + text(180, y, chequeamount);
            cpclConfigLabel += text(590, y, "Total") + text(650, y, " : ")
                    + text(700, y, total);
        } else if (paymode_cash.toLowerCase().matches("cash")
                && paymode_other.toLowerCase().matches("others")) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
                    + text(140, y, " : ") + text(180, y, otheramount);
            cpclConfigLabel += text(590, y, "Total") + text(650, y, " : ")
                    + text(700, y, total);
        } else if (paymode_cheque.toLowerCase().matches("cheque")
                && paymode_other.toLowerCase().matches("others")) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
                    + text(140, y, " : ") + text(180, y, chequeamount);
            cpclConfigLabel += text(590, y, "Total") + text(650, y, " : ")
                    + text(700, y, total);
        } else if (paymode_cash.toLowerCase().matches("cash")) {
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
                    + text(140, y, " : ") + text(180, y, cashamount);

            cpclConfigLabel += text(590, y, "Total") + text(650, y, " : ")
                    + text(700, y, total);
        } else if (paymode_cheque.toLowerCase().matches("cheque")) {
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
                    + text(700, y, " : ") + text(180, y, chequeamount);

            cpclConfigLabel += text(590, y, "Total") + text(650, y, " : ")
                    + text(700, y, total);
        } else {
            cpclConfigLabel += text(590, y, "Total") + text(650, y, " : ")
                    + text(700, y, total);
        }
        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);


        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

        // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
        // LINE_THICKNESS);
                   /* cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/
        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                LINE_THICKNESS);
        cpclConfigLabel += text(75, y += LINE_SPACING,
                "Received By");

        cpclConfigLabel += text(600, y, "Authorized By");

        cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
        Log.d("footerArr", "" + footerArr.size());

        if (footerArr.size() > 0) {

            // cpclConfigLabel += text(230, y += LINE_SPACING, "*********");

            for (ProductDetails footer : footerArr) {

                Log.d("footer value", "val " + footer.getReceiptMessage());

                if (footer.getReceiptMessage() != null
                        && !footer.getReceiptMessage().isEmpty()) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            footer.getReceiptMessage());
                }
            }
        }
        y+=70;
        cpclConfigLabel = ZPL_CMD_START + y + FONT + cpclConfigLabel + ZPL_CMD_END;
        // cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
        Log.d("cpclConfigLabel","-->"+cpclConfigLabel);
        os.write(cpclConfigLabel.getBytes());
        os.flush();
        os.close();

    }

    /**Expense Print Start **/

    public void printExpense(String expenseno, String expensedate,
                             List<ProductDetails> product, String title, int nofcopies) {
        helper.showProgressDialog(context.getString(R.string.print),
                context.getString(R.string.creating_file_for_printing));
        try {
            createExpenseFile(expenseno, expensedate, product, title, nofcopies);
            if (isBluetoothEnabled()) {
                  logoStr = "logoprint";
                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (IOException e) {
            e.printStackTrace();
            helper.dismissProgressDialog();
            helper.showErrorDialog(R.string.error_creating_file_for_printing);
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void createExpenseFile(String expenseno, String expensedate,
                                   List<ProductDetails> product, String title, int nofcopies)
            throws IOException {
        // Used the calculate the y axis printing position dynamically

          logoStr = "logoprint";

         Log.d("expnse logoStr", "exp" + logoStr);

        double totalqty = 0.00;
        String user = SupplierSetterGetter.getUsername();

        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        for (int n = 0; n < nofcopies; n++) {
            int y = 0;
            int s = 1;

            StringBuilder temp = new StringBuilder();
            y = printTitle(310, y, "EXPENSE", temp);
            // y = printCompanyDetails(y, temp);

            if (!Company.getCompanyName().equals("")) {
                temp.append(text(LEFT_MARGIN, y += LINE_SPACING,
                        Company.getCompanyName()));
            }

            String cpclConfigLabel = temp.toString();

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            if (!expenseno.matches("")) {
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Expense No")
                        + text(150, y, " : ")
                        + text(180, y, expenseno);
            }

            if (!expensedate.matches("")) {
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Expense Date")
                        + text(150, y, " : ")
                        + text(180, y, expensedate);
            }

            if (!user.matches("")) {
                cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "User"));
                cpclConfigLabel += (text(150, y, " : "));
                cpclConfigLabel += (text(180, y, user));
            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
            cpclConfigLabel += text(80, y, "Description");
            cpclConfigLabel += text(700, y, "Amount");

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            int i = 1;
            for (ProductDetails products : product) {

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        String.valueOf(i));

                cpclConfigLabel += text(
                        80,
                        y,
                        (products.getDescription().length() > 31) ? products
                                .getDescription().substring(0, 30) : products
                                .getDescription());

                double dAmt = Double.valueOf(products.getNettot());

                cpclConfigLabel += text(700, y, twoDecimalPoint(dAmt));
                if (products.getNettot() != null
                        && !products.getNettot().isEmpty()) {
                    totalqty += Double.valueOf(products.getNettot());
                }

                s += i;
                i++;

            }
            String tot_amt = twoDecimalPoint(totalqty);

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            cpclConfigLabel += text(490, y += LINE_SPACING, "Total Amount")
                    + text(650, y, " : ")
                    + text(700, y, String.valueOf(tot_amt));

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
            // LINE_THICKNESS);
                   /* cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                    LINE_THICKNESS);
            cpclConfigLabel += text(75, y += LINE_SPACING,
                    "Received By");

            cpclConfigLabel += text(600, y, "Authorized By");

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            y+=70;
            // Just append everything and create a single string
            cpclConfigLabel = ZPL_CMD_START + y + FONT + cpclConfigLabel + ZPL_CMD_END;
            // cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
            Log.d("cpclConfigLabel","-->"+cpclConfigLabel);

            os.write(cpclConfigLabel.getBytes());
            os.flush();
        }

        os.close();

    }
    public void printDeliveryOrder(String dono, String dodate,
                                   String customercode, String customername,
                                   List<ProductDetails> product, List<ProductDetails> productdet,
                                   int nofcopies) {
        helper.showProgressDialog(context.getString(R.string.print),
                context.getString(R.string.creating_file_for_printing));
        try {
            createDeliveryOrderFile(dono, dodate, customercode, customername,
                    product, productdet, nofcopies);
            if (isBluetoothEnabled()) {
                 logoStr = "logoprint";
                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (IOException e) {
            e.printStackTrace();
            helper.dismissProgressDialog();
            helper.showErrorDialog(R.string.error_creating_file_for_printing);
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void createDeliveryOrderFile(String dono, String dodate,
                                         String customercode, String customername,
                                         List<ProductDetails> product, List<ProductDetails> productdet,
                                         int nofcopies) throws IOException {
        // Used the calculate the y axis printing position dynamically
          logoStr = "logoprint";
        int totalQuantity = 0;
        Log.d("delivery logoStr", "deliord" + logoStr);
        String taxType ="",taxPerc ="",taxName="";
        taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
        if(taxPerc==null || taxPerc.trim().equals("")){
            taxPerc = "0.00";
        }
        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        for (int n = 0; n < nofcopies; n++) {
            int y = 0;
            StringBuilder temp = new StringBuilder();
            y = printTitle(200, y, "DELIVERY ORDER", temp);
            y = printCompanyDetails(y, temp);

            String cpclConfigLabel = temp.toString();

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "DO No")
                    + text(150, y, " : ") + text(180, y, dono);
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "DO Date")
                    + text(150, y, " : ") + text(180, y, dodate);

            cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                    "Cust Code"));
            cpclConfigLabel += (text(150, y, " : "));
            cpclConfigLabel += (text(180, y, customercode));
            cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
                    "Cust Name"));
            cpclConfigLabel += (text(150, y, " : "));

            cpclConfigLabel += (text(
                    180,
                    y,
                    (customername.length() > 25) ? customername
                            .substring(0, 24) : customername));

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
                cpclConfigLabel += text(160, y, "Description");
                cpclConfigLabel += text(480, y, "Qty");
                cpclConfigLabel += text(565, y, "Price");
                cpclConfigLabel += text(700, y, "Total");
            }else{
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
                cpclConfigLabel += text(160, y, "Description");
                cpclConfigLabel += text(700, y, "Qty");
            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            for (ProductDetails prods : product) {
                if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
                            .getItemcode().toString());
                    // cpclConfigLabel += text(140, y,
                    // prods.getDescription().toString());
                    cpclConfigLabel += text(160, y, (prods.getDescription()
                            .length() > 25) ? prods.getDescription()
                            .substring(0, 24) : prods.getDescription());
                    cpclConfigLabel += text(480, y, prods.getQty().toString());
                    cpclConfigLabel += text(565, y, prods.getPrice().toString());
                    cpclConfigLabel += text(700, y, prods.getTotal().toString());
                    taxType = prods.getTaxType();
                  //  taxPerc = prods.getTaxPerc();
                }else{
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
                            .getItemcode().toString());
                    // cpclConfigLabel += text(140, y,
                    // prods.getDescription().toString());
                    cpclConfigLabel += text(160, y, (prods.getDescription()
                            .length() > 10) ? prods.getDescription()
                            .substring(0, 9) : prods.getDescription());
                    cpclConfigLabel += text(700, y, prods.getQty().toString());

                    totalQuantity += prods.getQty().toString().equals("") ? 0 : Integer
                            .valueOf(prods.getQty().toString());
                }


            }

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){

                for (ProductDetails prd : productdet) {

                  /*  if(taxType.equalsIgnoreCase("I")){
                        taxName = "Incl "+taxPerc.split("\\.")[0]+"%";
                    }else if(taxType.equalsIgnoreCase("E")){
                        taxName = "Excl "+taxPerc.split("\\.")[0]+"%";
                    }else{
                        taxName = "Tax";
                    }*/
                    if(taxType.equalsIgnoreCase("I")){
                        taxName = "GST Incl "+taxPerc.split("\\.")[0]+"%";
                    }else if(taxType.equalsIgnoreCase("E")){
                        taxName = "GST Excl "+taxPerc.split("\\.")[0]+"%";
                    }else{
                        taxName = "Tax";
                    }

                    Log.d("taxType","-->"+taxType);
                    /********************/
                    //If taxtype is I then subtotal = netotal - tax
                    String netTotal = prd.getNettot().toString();
                    if(netTotal!=null && !netTotal.isEmpty()){
                        //Do Nothing
                    }else{
                        netTotal = "0.00";
                    }
                    String tax = prd.getTax().toString();
                    if(tax!=null && !tax.isEmpty()){

                    }else{
                        tax = "0.00";
                    }
                    double dNetTotal = Double.valueOf(netTotal);
                    double dTax = Double.valueOf(tax);
                    double dSubtotal =  dNetTotal - dTax;
                    Log.d("dNetTotal","-->"+dNetTotal);
                    Log.d("dTax","-->"+dTax);
                    Log.d("subtotal","-->"+dSubtotal);


                    if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                "Item Disc")
                                + text(140, y, " : ")
                                + text(180, y, prd.getItemdisc().toString());
                    }

                    if (Double.parseDouble(prd.getTax().toString()) > 0) {

                        // cpclConfigLabel += text(490, y, "Sub Total")
                        // + text(650, y, " : ")
                        // + text(700, y, prd.getSubtotal().toString());
                        if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

                            if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
                                cpclConfigLabel += text(LEFT_MARGIN,
                                        y += LINE_SPACING, "Bill Disc")
                                        + text(140, y, " : ")
                                        + text(180, y, prd.getBilldisc().toString());
                            }
                            if (Double.parseDouble(prd.getBilldisc().toString()) == 0
                                    && Double.parseDouble(prd.getItemdisc()
                                    .toString()) == 0) {
                                if(taxType!=null && !taxType.isEmpty()){
                                    if (taxType.matches("I")) {
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                "Sub Total")
                                                + text(650, y, " : ")
                                                + text(700, y, twoDecimalPoint(dSubtotal));

                                    }else{
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                "Sub Total")
                                                + text(650, y, " : ")
                                                + text(700, y, prd.getSubtotal().toString());
                                    }
                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(650, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                                /*cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(650, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());*/
                            } else {
                                if(taxType!=null && !taxType.isEmpty()){
                                    if (taxType.matches("I")) {
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                "Sub Total")
                                                + text(650, y, " : ")
                                                + text(700, y, twoDecimalPoint(dSubtotal));

                                    }else{
                                        cpclConfigLabel += text(490, y += LINE_SPACING,
                                                "Sub Total")
                                                + text(650, y, " : ")
                                                + text(700, y, prd.getSubtotal().toString());
                                    }
                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(650, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                              /*  cpclConfigLabel += text(490, y, "Sub Total")
                                        + text(650, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());*/
                            }
                        } else {
                            if(taxType!=null && !taxType.isEmpty()){
                                if (taxType.matches("I")) {
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(650, y, " : ")
                                            + text(700, y, twoDecimalPoint(dSubtotal));

                                }else{
                                    cpclConfigLabel += text(490, y += LINE_SPACING,
                                            "Sub Total")
                                            + text(650, y, " : ")
                                            + text(700, y, prd.getSubtotal().toString());
                                }
                            }else{
                                cpclConfigLabel += text(490, y += LINE_SPACING,
                                        "Sub Total")
                                        + text(650, y, " : ")
                                        + text(700, y, prd.getSubtotal().toString());
                            }
                            /*cpclConfigLabel += text(490, y, "Sub Total")
                                    + text(650, y, " : ")
                                    + text(700, y, prd.getSubtotal().toString());*/
                        }
                    }

                    if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
                        // cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        // "Bill Disc")
                        // + text(140, y, " : ")
                        // + text(180, y, prd.getBilldisc().toString());
                        if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "Bill Disc")
                                    + text(140, y, " : ")
                                    + text(180, y, prd.getBilldisc().toString());
                        } else {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "") + text(140, y, "") + text(180, y, "");
                        }
                    }

                    if (Double.parseDouble(prd.getTax().toString()) > 0) {

                        if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                                    "") + text(140, y, "") + text(180, y, "");
                        }

                        cpclConfigLabel += text(490, y, taxName)
                                + text(650, y, " : ")
                                + text(700, y, prd.getTax().toString());
                    }
                    if (Double.parseDouble(prd.getTax().toString()) == 0) {

                        cpclConfigLabel += text(490, y, "") + text(650, y, "")
                                + text(700, y, "");

                        if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
                            cpclConfigLabel += text(490, y += LINE_SPACING,
                                    "Net Total")
                                    + text(650, y, " : ")
                                    + text(700, y, prd.getNettot().toString());

                        } else {
                            cpclConfigLabel += text(490, y, "Net Total")
                                    + text(650, y, " : ")
                                    + text(700, y, prd.getNettot().toString());
                        }
                    } else {
                        cpclConfigLabel += text(490, y += LINE_SPACING, "Net Total")
                                + text(650, y, " : ")
                                + text(700, y, prd.getNettot().toString());
                    }
                }
            }else{

                cpclConfigLabel += text(490, y += LINE_SPACING, "Total Qty")
                        + text(650, y, " : ")
                        + text(700, y, totalQuantity);

            }
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

            // cpclConfigLabel += horizontalLine(y += LINE_SPACING,
            // LINE_THICKNESS);
                   /* cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "-----------------   ------------------");*/
            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING,
                    LINE_THICKNESS);
            cpclConfigLabel += text(75, y += LINE_SPACING,
                    "Received By");

            cpclConfigLabel += text(600, y, "Authorized By");

            cpclConfigLabel += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);
            y+=70;
            // Just append everything and create a single string
            cpclConfigLabel = ZPL_CMD_START + y + FONT + cpclConfigLabel + ZPL_CMD_END;
            // cpclConfigLabel = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfigLabel+"^XZ";
            Log.d("cpclConfigLabel","-->"+cpclConfigLabel);

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
            os.write(cpclConfigLabel.getBytes());
            os.flush();
        }
        os.close();
    }
    private int printCompanyDetails(int y, StringBuilder cpclConfigLabel) {

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

        String InvoiceTelCaption = MobileSettingsSetterGetter.getInvoiceTelCaption();
        String InvoiceFaxCaption = MobileSettingsSetterGetter.getInvoiceFaxCaption();
        String InvoiceEmailCaption = MobileSettingsSetterGetter.getInvoiceEmailCaption();
        String InvoiceBizRegNoCaption = MobileSettingsSetterGetter.getInvoiceBizRegNoCaption();
        String InvoiceTaxRegNoCaption = MobileSettingsSetterGetter.getInvoiceTaxRegNoCaption();

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
            cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                    Company.getCompanyName()));
        }

        if (showaddress1.matches("True")) {
            if (!Company.getAddress1().equals("")) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                        Company.getAddress1()));
            }
        }

        if (showaddress2.matches("True")) {
            if (!Company.getAddress2().equals("")) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                        Company.getAddress2()));
            }
        }

        if (showaddress3.matches("True")) {
            if (!Company.getAddress3().equals("")) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                        Company.getAddress3()));
            }
        }

        if (showcountrypostal.matches("True")) {
            if (!country.equals("")) {

                if (!zipcode.equals("")) {
                    cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                            countryZipcode));
                } else {
                    cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                            country));
                }
            }
        }

        if (showphone.matches("True")) {
            if (!phoneno.equals("")) {

                if(!InvoiceTelCaption.equals("")){

                    cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                            InvoiceTelCaption+phoneno));
                }else{
                    cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                            phoneno));
                }

            }
        }

        if (showfax != null && !showfax.isEmpty()) {
            if (showfax.matches("True")) {
                if (!fax.equals("")) {

                    if(!InvoiceFaxCaption.equals("")){
                        cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                                InvoiceFaxCaption+fax));
                    }else {
                        cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                                fax));
                    }
                }
            }
        }

        if (showemail != null && !showemail.isEmpty()) {
            if (showemail.matches("True")) {
                if (!email.equals("")) {

                    if (!InvoiceEmailCaption.equals("")) {
                        cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                                InvoiceEmailCaption+email));
                    }else{
                        cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                                email));
                    }


                }
            }
        }

        if (showtaxregno != null && !showtaxregno.isEmpty()) {
            if (showtaxregno.matches("True")) {
                if (!taxregno.equals("")) {
                    if (!InvoiceTaxRegNoCaption.equals("")) {
                        cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                                InvoiceTaxRegNoCaption+taxregno));
                    }else{
                        cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                                taxregno));
                    }

                }
            }
        }

        if (showbusregno != null && !showbusregno.isEmpty()) {
            if (showbusregno.matches("True")) {
                if (!busregno.equals("")) {
                    if (!InvoiceBizRegNoCaption.equals("")) {
                        cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                                InvoiceBizRegNoCaption+busregno));
                    }else{
                        cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                                busregno));
                    }

                }
            }
        }

        if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {
            if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {

                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
                        "Contact " + " : " + username +" "+loginphoneno));

			 /*    cpclConfigLabel+= text(LEFT_MARGIN, y += LINE_SPACING,
			       "Contact ")
			       + text(150, y, " : ")
			       + text(180, y, username +" "+loginphoneno);*/

            }
        }

        if(mUser!=null && !mUser.isEmpty()){
            cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, "User")
                    + text(170, y, " : ") + text(190, y, mUser));
        }

        cpclConfigLabel
                .append(horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS));
        return y;
    }


    public void bottomPrint() throws IOException {

        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);

        int y = 0;

        StringBuilder temp = new StringBuilder();

        String cpclConfig = temp.toString();

       /* cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
                "-----------------   ------------------");*/
        cpclConfig += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

        cpclConfig += text(75, y += LINE_SPACING, "Received By");

        cpclConfig += text(600, y, "Authorized By");

        Log.d("footerArr bottom", "" + footerArr.size());

        if (footerArr.size() > 0) {

            // cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
            // "--------------*********---------------");
            // cpclConfig += text(230, y += LINE_SPACING, "");
            // cpclConfig += text(230, y += LINE_SPACING, "*********");
            cpclConfig += horizontalLine(LEFT_MARGIN,y += LINE_SPACING, LINE_THICKNESS);

            for (ProductDetails footer : footerArr) {

                Log.d("footer value", "val " + footer.getReceiptMessage());

                if (footer.getReceiptMessage() != null
                        && !footer.getReceiptMessage().isEmpty()) {
                    cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
                            footer.getReceiptMessage());
                }
            }
        }
        cpclConfig = "^XA^CFD^CI28^POI^LL"+y+"^A@N,25,25,E:MSUNG.TTF"+cpclConfig+"^XZ";
        Log.d("cpclConfigLabel",""+cpclConfig);
      /*  cpclConfig = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
                + LINE_SEPARATOR + HEADER + cpclConfig + CMD_PRINT;*/

        os.write(cpclConfig.getBytes());
        os.flush();
        os.close();

    }

   /* public void printTest() throws IOException {
        helper.showProgressDialog(R.string.print,
                R.string.creating_file_for_printing);
        try {


            createTest();
            if (isBluetoothEnabled()) {

                print();
            } else {
                context.registerReceiver(bluetoothReceiver, new IntentFilter(
                        BluetoothAdapter.ACTION_STATE_CHANGED));
                enableBluetooth();
            }
        } catch (Exception e) {
            helper.dismissProgressDialog();
            e.printStackTrace();
        }
    }*/
   /* private void createTest() throws IOException {



        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);
        StringBuilder temp = new StringBuilder();
        String cpclConfigLabel = temp.toString();
        String value = "Chinese print 斑马科技 有限公司";
        String valueUTF8 = URLEncoder.encode(value, "UTF-8");
        String res = valueUTF8.replaceAll("\\%", "\\_");
        String result = res.replaceAll("\\+", "\\ ");


        cpclConfigLabel += "^XA" +
                "^CFD" +
                "^CI28" +
                "^POI" +
                "^LL200" +
                "^A@N,25,25,E:MSUNG.TTF"+
                "^FO10,05^FDZEBRA ^FS" +
                "^FO10,30^FDPROGRAMMING^FS" +
                "^FO50,40^GB700,1,3^FS"+
                "^FO10,56^FDZEBRA PROGRAMMING^FS" +
                "^FO10,75^FDLANGUAGE II()ZPL II)^FS" +
                "^FO10,96^FDPROGRAMMING^FS" +
                "^FO10,150^FDLANGUAGE II()ZPL II)^FS" +
                "^FO10,200^FDZEBRA PROGRAMMING^FS" +
                "^FO10,250^FDhis is a ZPL test^FS" +
                "^FO10,300^FDLANGUAGE II()ZPL II)^FS" +
                "^FO10,350^FDhis is a ZPL^FS" +
                "^FO10,400^FDhis is a ZPL^FS" +
                "^FO10,450^FDhis is a ZPL^FS" +
                "^A@N,25,25,E:MSUNG.TTF"+
                "^FO10,550^FH^FD"+"Chinese print 斑马科技 有限公司^FS" +
                "^XZ";

      *//*  try {
            connection.write("! U1 setvar \"zpl.label_length\" \"2233\""
                    .getBytes());
        } catch (ConnectionException e) {
            e.printStackTrace();
        }*//*

        os.write(cpclConfigLabel.getBytes());
        os.flush();
        os.close();
    }*/
    public static String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }
    public static String decimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("###.#");
        String tot = df.format(d);

        return tot;
    }
    /** Expense Print End **/

}
