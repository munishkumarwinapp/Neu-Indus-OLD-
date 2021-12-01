package com.winapp.sot;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Catalog;
import com.winapp.helper.Constants;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSetterGetter;
import com.winapp.sotdetails.DBCatalog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class OfflineSalesOrderWebService {

    static ArrayList<String> crrncyVal = new ArrayList<String>();
    static ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
    private Activity activity;
    private static OfflineDatabase offlinedb;
    private static SOTDatabase sqldb;
    static ArrayList<String> productid = new ArrayList<String>();
    static ArrayList<ProductBarcode> allproducts = new ArrayList<ProductBarcode>();
    static ArrayList<String> SOResultArr = new ArrayList<String>();

    static String deviceId;
    static Cursor cursor;
    static String header_json = "", detail_json = "", barcode_json = "";
    static String result, sum_result;
    static double dbCreditAmount = 0.0;
    static double dbPaidAmount = 0.0;

    public OfflineSalesOrderWebService(Activity activity) {
        this.activity = activity;
        offlinedb = new OfflineDatabase(activity);
        SOTDatabase.init(activity);
        sqldb = new SOTDatabase(activity);
        DBCatalog.init(activity);
        deviceId = RowItem.getDeviceID();
    }

    public static ArrayList<HashMap<String, String>> getAllCurrencyOffline() {
        ArrayList<HashMap<String, String>> currencyList = new ArrayList<HashMap<String, String>>();
        String resTxt = offlinedb.getAllCurrencyJson("");
        String result = " { LocalCurrency : " + resTxt + "}";
        Log.d("AllCurrency", result);
        JSONObject jsonResponse;
        try {

            jsonResponse = new JSONObject(result);
            JSONArray jsonMainNode = jsonResponse
                    .optJSONArray("LocalCurrency");
            int lengthJsonArr = jsonMainNode.length();
            for (int i = 0; i < lengthJsonArr; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String currencyCode = jsonChildNode.optString(
                        "CurrencyCode").toString();
                String currencyName = jsonChildNode.optString(
                        "CurrencyName").toString();

                HashMap<String, String> currencyhm = new HashMap<String, String>();

                currencyhm.put(currencyCode, currencyName);
                currencyList.add(currencyhm);

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return currencyList;
    }

    public static String generalSettingsServiceOffline() {
        String SettingValue = "";
        String resTxt = offlinedb.getGeneralSettingsValue();
        String result = " { GeneralSettings : " + resTxt + "}";
        Log.d("GeneralSettings", result);
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(result);
            JSONArray jsonMainNode = jsonResponse
                    .optJSONArray("GeneralSettings");
            int lengthJsonArr = jsonMainNode.length();
            for (int i = 0; i < lengthJsonArr; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String SettingID = jsonChildNode.optString("SettingID")
                        .toString();
                if (SettingID.matches("LOCALCURRENCY")) {
                    SettingValue = jsonChildNode.optString("SettingValue")
                            .toString();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return SettingValue;
    }

    public static ArrayList<String> getCurrencyValuesOffline(String currencycode) throws JSONException {

        String resTxt = offlinedb.getAllCurrencyJson(currencycode);
        String result = " { LocalCurrency : " + resTxt + "}";
        Log.d("Currency", result);
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(result);
            JSONArray jsonMainNode = jsonResponse
                    .optJSONArray("LocalCurrency");
            int lengthJsonArr = jsonMainNode.length();
            for (int i = 0; i < lengthJsonArr; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String currencyName = jsonChildNode.optString(
                        "CurrencyName").toString();
                String currencyRate = jsonChildNode.optString(
                        "CurrencyRate").toString();
                crrncyVal.add(currencyName);
                crrncyVal.add(currencyRate);

            }


        } catch (Exception e) {
            e.printStackTrace();
            resTxt = "Error occured";
        }

        return crrncyVal;
    }

    public static ArrayList<String> getCustGroupCodeOffline(String cuscode) {
        cstmrgrpcdal.clear();
        HashMap<String, String> customerhm = new HashMap<String, String>();
        customerhm.put("CustomerCode", cuscode);
        customerhm.put("NeedOutstandingAmount", "");
        customerhm.put("AreaCode", "");
        customerhm.put("VanCode", "");

        String result = OfflineDatabase.getCustomersList(customerhm);
        Log.d("CustomerGroups--> ", result);
        JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(result);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("JsonArray");
            int lengthJsonArr = jsonMainNode.length();

            for (int i = 0; i < lengthJsonArr; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String custcode = jsonChildNode.optString("CustomerCode").toString();
                String custgroup = jsonChildNode.optString("CustomerGroupCode").toString();
                cstmrgrpcdal.add(custcode);
                cstmrgrpcdal.add(custgroup);
            }
            Log.d("cstmrgrpcdal", "" + cstmrgrpcdal);

        } catch (Exception e) {
            e.printStackTrace();
            result = "CustomerGroups Error occured";
        }

        return cstmrgrpcdal;
    }

    public static void getCustomerTaxOffline(String cuscode) {

        HashMap<String, String> customerhm = new HashMap<String, String>();
        customerhm.put("CustomerCode", cuscode);
        customerhm.put("NeedOutstandingAmount", "");
        customerhm.put("AreaCode", "");
        customerhm.put("VanCode", "");


        String result = OfflineDatabase.getCustomersList(customerhm);
        Log.d("CustomerTax--> ", result);
        JSONObject jsonResponse;
        try {

            jsonResponse = new JSONObject(result);
            JSONArray jsonMainNode = jsonResponse
                    .optJSONArray("JsonArray");
            int lengthJsonArr = jsonMainNode.length();

            for (int i = 0; i < lengthJsonArr; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                String HaveTax = jsonChildNode.optString("HaveTax")
                        .toString();

                String TaxType = jsonChildNode.optString("TaxType")
                        .toString();

                String TaxValue = jsonChildNode.optString("TaxValue")
                        .toString();

                if (HaveTax.matches("True") || HaveTax.matches("true")) {
//                    SalesOrderSetGet.setTaxValue(TaxValue);
                    SalesOrderSetGet.setCompanytax(TaxType);
                    SalesOrderSetGet.setCustomerTaxPerc(TaxValue);
                } else {
//                    SalesOrderSetGet.setTaxValue("");
                    SalesOrderSetGet.setCompanytax("Z");
                    SalesOrderSetGet.setCustomerTaxPerc("0.00");
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            result = "CustomerTax Error occured";
        }

    }

    public static String getCrrncyRateOffline(String currencycode) {
        String currencyRate = "";

        String resTxt = offlinedb.getAllCurrencyJson(currencycode);
        String result = " { LocalCurrency : " + resTxt + "}";
        Log.d("currencyRateresult", result);
        JSONObject jsonResponse;
        try {

            jsonResponse = new JSONObject(result);
            JSONArray jsonMainNode = jsonResponse
                    .optJSONArray("LocalCurrency");
            int lengthJsonArr = jsonMainNode.length();
            for (int i = 0; i < lengthJsonArr; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                currencyRate = jsonChildNode.optString("CurrencyRate")
                        .toString();

            }
            Log.d("currencyRate", currencyRate);

        } catch (Exception e) {
            e.printStackTrace();
            resTxt = "Error occured";
        }

        return currencyRate;
    }

    public static ArrayList<String> summaryInvoiceServiceOffline(String webMethName,
                                                                 double billDisct, String subTotal, String totTax, String netTotal,
                                                                 String ttl, String SoSlNo, String SoNo, String DoNo,
                                                                 String invoiceNumber, String downloadstatus) throws JSONException, IOException,
            XmlPullParserException {

        productid.clear();
        allproducts.clear();
        SOResultArr.clear();

        cursor = SOTDatabase.getCursor();
        double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
        int slNo = 1;
        double sbtot = SOTDatabase.getsumsubTot();
        double billDiscount = billDisct / sbtot;
        double billDiscs;
        detail_json = "";
        header_json = "";
        barcode_json = "";
        String batch_json = "";
        String ReceiptNo = "";
        String invNo = "", retnInvoiceNo = "";
        String receiptNo = "", retnReceiptNo = "";
        String appType = LogOutSetGet.getApplicationType();
        String collectCash = SalesOrderSetGet.getsCollectCash();
        String user = SupplierSetterGetter.getUsername();

        String locationCode = SalesOrderSetGet.getLocationcode();
        String customerCode = SalesOrderSetGet.getCustomercode();
        String currencyCode = SalesOrderSetGet.getCurrencycode();
        String remarks = SalesOrderSetGet.getRemarks();
        String currencyRate = SalesOrderSetGet.getCurrencyrate();
        String soDate = SalesOrderSetGet.getSaleorderdate();
        String invDate = "";

        ////////
        try {
            SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (soDate != null && !soDate.isEmpty()) {
                Date mDate = oldFormat.parse(soDate);
                invDate = newFormat.format(mDate) + " 00:00:00";
            } else {
                invDate = soDate;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        String customerName = offlinedb.getCustomerName(customerCode);

        Cursor cursor = SOTDatabase.getCursor();

        ArrayList<String> getprod = new ArrayList<String>();

        if (invoiceNumber != null && !invoiceNumber.isEmpty()) {

            getprod = OfflineDatabase.getproductcodefrominvoiceno(invoiceNumber);

            for (int i = 0; i < getprod.size(); i++) {

                Log.d("getprod", getprod.get(i));

                int count = 1;

                if (cursor != null && cursor.getCount() > 0) {

                    if (cursor.moveToFirst()) {
                        do {

                            String pCode = cursor.getString(cursor
                                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));

                            if (getprod.get(i).matches(pCode)) {
                                Log.d("Matched", "Matched");
                                break;
                            } else {

                                if (count == cursor.getCount()) {
                                    Log.d("Delete this item", "code " + getprod.get(i));

                                    offlinedb.deleteInvoiceItem(invoiceNumber, getprod.get(i));
                                }
                            }

                            count++;

                        } while (cursor.moveToNext());
                    }
                }
            }
        }

        if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
            Log.d(" Not empty Invoice No", "-->" + invoiceNumber);
            invNo = invoiceNumber;
        } else {
            Log.d(" empty Invoice No", "-->" + invoiceNumber);
            invNo = OfflineDatabase.getNextRunningNumber(
                    "InvoicePrefix", "NextInvoiceNo");
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datetime = timeFormat.format(new Date());
        System.out.println(datetime);

        ArrayList<HashMap<String, String>> offlineInvoiceDetails = new ArrayList<HashMap<String, String>>();

        if (cursor != null && cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {
                do {
                    String pCode = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
                    String pName = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
                    String cQty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
                    String lQty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
                    String qty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
                    String foc = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_FOC));
                    String price = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
                    String pcsPerCarton = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
                    String total = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TOTAL));
                    String itemDiscount = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
                    String taxType = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
                    String taxValue = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
                    String retail_Price = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));
                    String sub_ToTal = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
                    String net_ToTal = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
                    String tax_amount = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TAX));

                    String cprice = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));

                    String exchangeQty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));

                    String ItemRemarks = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_ITEM_REMARKS));

                    String SOSlno = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_SO_SLNO));
                    String UOMCode = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_UOM));

                    if (SOSlno != null && !SOSlno.isEmpty()) {

                    } else {
                        SOSlno = "";
                    }
                    if (exchangeQty != null && !exchangeQty.isEmpty()) {

                    } else {
                        exchangeQty = "";
                    }

                    double sbTotal = SOTDatabase.getsubTotal(slNo);
                    billDiscs = billDiscount * sbTotal;

                    totalDiscount = billDiscs
                            + Double.parseDouble(itemDiscount);

                    subTot = Double.parseDouble(sub_ToTal);
                    ntTtl = Double.parseDouble(net_ToTal);
                    tx = Double.parseDouble(tax_amount);

                    String subTtl = twoDecimalPoint(subTot);
                    String netTtl = twoDecimalPoint(ntTtl);
                    String txAmt = fourDecimalPoint(tx);
                    String billDisc = twoDecimalPoint(billDiscs);
                    String totalDisc = twoDecimalPoint(totalDiscount);

                    HashMap<String, String> productvalues = new HashMap<String, String>();
                    productvalues.put("DateTime", datetime);
                    productvalues.put("InvoiceNo", invoiceNumber);
                    productvalues.put("slNo", slNo + "");
                    productvalues.put("ProductCode", pCode);
                    productvalues.put("ProductName", pName);
                    productvalues.put("CQty", cQty);
                    productvalues.put("Lqty", lQty);
                    productvalues.put("Qty", qty);
                    productvalues.put("FOCQty", foc);
                    productvalues.put("PcsPerCarton", pcsPerCarton);
                    productvalues.put("RetailPrice", retail_Price);
                    productvalues.put("Price", price);
                    productvalues.put("Total", total);
                    productvalues.put("ItemDiscount", itemDiscount);
                    productvalues.put("BillDiscount", billDisc);
                    productvalues.put("TotalDiscount", totalDisc);
                    productvalues.put("SubTotal", subTtl);
                    productvalues.put("Tax", txAmt);
                    productvalues.put("NetTotal", netTtl);
                    productvalues.put("TaxType", taxType);
                    productvalues.put("TaxPerc", taxValue);
                    productvalues.put("AverageCost", "");
                    productvalues.put("CategoryCode", "");
                    productvalues.put("SubCategoryCode", "");
                    productvalues.put("ExchangeQty", exchangeQty);
                    productvalues.put("CartonPrice", cprice);
                    productvalues.put("CreateUser", user);
                    productvalues.put("CreateDate", soDate);
                    productvalues.put("ModifyUser", "");
                    productvalues.put("ModifyDate", soDate);
                    productvalues.put("DownloadStatus", downloadstatus);
                    productvalues.put("OfflineInvoiceNo", invNo);
                    productvalues.put("UOMCode", UOMCode);

                    offlineInvoiceDetails.add(productvalues);

                    slNo++;
                } while (cursor.moveToNext());
            }
            OfflineDatabase.saveInvoiceDetail(offlineInvoiceDetails);
            Log.d("Detail", offlineInvoiceDetails.toString());

        }

        double blDsc = billDisct;
        double itemDisc = SOTDatabase.getitemDisc();
        double totDisc = blDsc + itemDisc;

        SoNo = ConvertToSetterGetter.getSoNo();
        DoNo = ConvertToSetterGetter.getDoNo();

        HashMap<String, String> productvalues = new HashMap<String, String>();
        productvalues.put("DateTime", datetime);
        productvalues.put("InvoiceNo", invoiceNumber);
        productvalues.put("InvoiceDate", invDate);
        productvalues.put("LocationCode", locationCode);
        productvalues.put("CustomerCode", customerCode);
        productvalues.put("CustomerName", customerName);
        productvalues.put("Total", ttl);
        productvalues.put("ItemDiscount", itemDisc + "");
        productvalues.put("BillDiscount", blDsc + "");
        productvalues.put("TotalDiscount", totDisc + "");
        productvalues.put("SubTotal", subTotal);
        productvalues.put("Tax", totTax);
        productvalues.put("NetTotal", netTotal);

        productvalues.put("CreditAmount", "0.0");
        if (collectCash.matches("1")) {
            productvalues.put("BalanceAmount", "0.0");
            productvalues.put("PaidAmount", netTotal);
        } else {
            productvalues.put("BalanceAmount", netTotal);
            productvalues.put("PaidAmount", "0.0");
        }
        productvalues.put("Remarks", remarks);
        productvalues.put("CurrencyCode", currencyCode);
        productvalues.put("CurrencyRate", currencyRate);
        productvalues.put("SoNo", SoNo);
        productvalues.put("DoNo", DoNo);
        //  productvalues.put("TotalBalance",object.getString("TotalBalance"));
        productvalues.put("CreateUser", user);
        productvalues.put("CreateDate", soDate);
        productvalues.put("ModifyUser", "");
        productvalues.put("ModifyDate", soDate);
        productvalues.put("DownloadStatus", downloadstatus);
        productvalues.put("OfflineInvoiceNo", invNo);
        retnInvoiceNo = OfflineDatabase.saveInvoiceheader(productvalues);

        if (retnInvoiceNo != null && !retnInvoiceNo.isEmpty()) {


            if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
                Log.d("Product Stock Update", "-->" + "Invoice No not empty");
                ArrayList<HashMap<String, String>> stockupdateArr = new ArrayList<HashMap<String, String>>();
                stockupdateArr = OfflineSetterGetter.getStockUpdateArr();
                OfflineDatabase.updateProductStockforeditinvoice(stockupdateArr);
            } else {
                Log.d("Product Stock Update", "-->" + "Invoice No empty");
                OfflineDatabase.updateNextRunningNumber("NextInvoiceNo");
                OfflineDatabase.updateProductStock(offlineInvoiceDetails);
            }

            if (collectCash.matches("1")) {
                Log.d(" empty Invoice No", "-->" + retnInvoiceNo);
                receiptNo = OfflineDatabase.getNextRunningNumber("ReceiptPrefix", "NextReceiptNo");

                HashMap<String, String> value = new HashMap<String, String>();
                value.put("DateTime", datetime);
                value.put("ReceiptNo", receiptNo);
                //  value.put("ReceiptDate", soDate);
                value.put("InvoiceNo", retnInvoiceNo);
                value.put("NetTotal", netTotal);
                value.put("PaidAmount", netTotal);
                value.put("CreditAmount", "0.0");
                value.put("CreateUser", user);
                value.put("CreateDate", datetime);
                value.put("ModifyUser", "");
                value.put("ModifyDate", datetime);
                value.put("DownloadStatus", downloadstatus);
                Log.d("Receipt Detail offline save ", "-->" + value.toString());
                OfflineDatabase.saveReceiptdetail(value);

                HashMap<String, String> headervalue = new HashMap<String, String>();
                headervalue.put("DateTime", datetime);
                headervalue.put("ReceiptNo", receiptNo);
                headervalue.put("ReceiptDate", invDate);
                headervalue.put("CustomerCode", customerCode);
                headervalue.put("CustomerName", customerName);
                headervalue.put("PaidAmount", netTotal);
                headervalue.put("CreditAmount", "0.0");
                headervalue.put("Paymode", "Cash");
                headervalue.put("BankCode", "");
                headervalue.put("ChequeNo", "");
                headervalue.put("ChequeDate", "1/1/1900 12:00:00 AM");
                headervalue.put("CreateUser", user);
                headervalue.put("CreateDate", datetime);
                headervalue.put("ModifyUser", "");
                headervalue.put("ModifyDate", datetime);
                headervalue.put("DownloadStatus", downloadstatus);
                receiptNo = OfflineDatabase.saveReceiptheader(headervalue);
                Log.d("Receipt header offline save ", "-->" + headervalue.toString());

                if (receiptNo != null && !receiptNo.isEmpty()) {
                    OfflineDatabase.updateNextRunningNumber("NextReceiptNo");
                }

            } else {
                receiptNo = "";
            }

        }

        String resTxt = "";

        resTxt = "[{\"Result\":\"" + retnInvoiceNo + "\",\"ReceiptNo\":\"" + receiptNo + "\"}]";
        Log.d("Invoice Result ", resTxt);

        result = " { SOResult : " + resTxt + "}";

        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(result);

            JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");


            int lengthJsonArr = jsonMainNode.length();
            for (int i = 0; i < lengthJsonArr; i++) {

                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);


                sum_result = jsonChildNode.optString("Result").toString();
                ReceiptNo = jsonChildNode.optString("ReceiptNo").toString();

                if (sum_result.matches("")) {
                    sum_result = "failed";
                    Log.d("Summary Result", sum_result);
                }

                SOResultArr.add(sum_result);
                SOResultArr.add(ReceiptNo);
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return SOResultArr;
    }


    public static String saveReceiptOffline(ArrayList<HashMap<String, String>> SaveArray, String companycode,
                                            String serverdate, String downloadstatus) {

        ArrayList<HashMap<String, String>> offlinereceiptDetail = new ArrayList<HashMap<String, String>>();
        Log.d("Offline Recepit", "Save Result.......");
        String receiptNo = "";
        dbCreditAmount = 0.0;
        dbPaidAmount = 0.0;

        String user = SupplierSetterGetter.getUsername();
        String payment = "", bank_Code = "", check_No = "", cust_Date = "";
        String cust_Code = In_Cash.getCust_Code();
        String pay_Mode = In_Cash.getPay_Mode();

        try {

            String customerName = offlinedb.getCustomerName(cust_Code);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date sDate = dateFormat.parse(serverdate);

            SimpleDateFormat regateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String receiptDate = regateFormat.format(sDate) + " 12:00:00";

            receiptNo = OfflineDatabase.getNextRunningNumber("ReceiptPrefix",
                    "NextReceiptNo");

            for (int i = 0; i < SaveArray.size(); i++) {
                String s1 = SaveArray.get(i).get("Invoice");
                String s2 = SaveArray.get(i).get("NetValue");
                String s3 = SaveArray.get(i).get("PaidValue");
                String s4 = SaveArray.get(i).get("CreditValue");

                OfflineDatabase.updateInvoiceBalance(s1, s2, s3, s4);

                dbCreditAmount = dbCreditAmount + Double.parseDouble(s4);
                dbPaidAmount = dbPaidAmount + Double.parseDouble(s3);

                HashMap<String, String> values = new HashMap<String, String>();
                values.put("CompanyCode", companycode);
                values.put("DateTime", serverdate);
                values.put("ReceiptNo", receiptNo);
                values.put("InvoiceNo", s1);
                values.put("NetTotal", s2);
                values.put("PaidAmount", s3);
                values.put("CreditAmount", s4);
                values.put("CreateUser", user);
                values.put("CreateDate", serverdate);
                values.put("ModifyUser", "");
                values.put("ModifyDate", serverdate);
                values.put("DownloadStatus", downloadstatus);

                offlinereceiptDetail.add(values);
            }
            Log.d("Receipt Detail", offlinereceiptDetail.toString());
            OfflineDatabase.store_receiptdetail(offlinereceiptDetail);

            dbCreditAmount = Math.round(dbCreditAmount * 100.0) / 100.0;
            dbPaidAmount = Math.round(dbPaidAmount * 100.0) / 100.0;

            if (pay_Mode.toLowerCase().matches("cheque")) {
                payment = pay_Mode;
                bank_Code = In_Cash.getBank_code();
                check_No = In_Cash.getCheck_No();
                cust_Date = In_Cash.getCheck_Date();

            } else //if (pay_Mode.matches("Cash")) {
            {
                payment = pay_Mode;
                bank_Code = "";
                check_No = "";
                cust_Date = "01/01/1900";

            }
            Log.d("Payment Mode", "" + pay_Mode);

            if (cust_Date.matches("")) {
                cust_Date = serverdate;
            }

            HashMap<String, String> headervalues = new HashMap<String, String>();

            headervalues.put("CompanyCode", companycode);
            headervalues.put("DateTime", serverdate);
            headervalues.put("ReceiptNo", receiptNo);
            headervalues.put("ReceiptDate", receiptDate);
            headervalues.put("CustomerCode", cust_Code);
            headervalues.put("CustomerName", customerName);
            headervalues.put("PaidAmount", dbPaidAmount + "");
            headervalues.put("CreditAmount", dbCreditAmount + "");
            headervalues.put("Paymode", payment);
            headervalues.put("BankCode", bank_Code);
            headervalues.put("ChequeNo", check_No);
            headervalues.put("ChequeDate", cust_Date);
            headervalues.put("CreateUser", user);
            headervalues.put("CreateDate", serverdate);
            headervalues.put("ModifyUser", "");
            headervalues.put("ModifyDate", serverdate);
            headervalues.put("DownloadStatus", downloadstatus);
            Log.d("Receipt Header", headervalues.toString());
            OfflineDatabase.store_receiptheader(headervalues);

            if (receiptNo != null && !receiptNo.isEmpty()) {
                OfflineDatabase.updateNextRunningNumber("NextReceiptNo");
            }

            String resTxt = "";
            resTxt = "[{\"Result\":\"" + receiptNo + "\"}]";
            result = " { JsonArray: " + resTxt + "}";
            Log.d("Result", result);

        } catch (Exception e) {
            e.printStackTrace();
            result = "Error";
            Log.d("result 2:", result);
        }
        return result;
    }

    public static String summarySOServiceOffline(String webMethName,
                                                 double billDisct, String subTotal, String totTax, String netTotal,
                                                 String ttl, String soNo, String downloadstatus) throws JSONException, IOException,
            XmlPullParserException {

        cursor = SOTDatabase.getCursor();
        double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
        int slNo = 1;
        detail_json = "";
        header_json = "";
        double sbtot = SOTDatabase.getsumsubTot();
        double billDiscount = billDisct / sbtot;
        double billDiscs;
        String sono = "", retnSONo = "";

        String locationCode = SalesOrderSetGet.getLocationcode();
        String deliveryDate = SalesOrderSetGet.getDeliverydate();
        String customerCode = SalesOrderSetGet.getCustomercode();
        String currencyCode = SalesOrderSetGet.getCurrencycode();
        String remarks = SalesOrderSetGet.getRemarks();
        String currencyRate = SalesOrderSetGet.getCurrencyrate();
        String soDate = SalesOrderSetGet.getSaleorderdate();
        String user = SupplierSetterGetter.getUsername();
        String customerName = offlinedb.getCustomerName(customerCode);
        String so_finalDate = "", do_finalDate = "";

        Log.d("SOSave ", "so_date" + soDate);
        Log.d("SOSave ", "do_date" + deliveryDate);

        try {
            SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (soDate != null && !soDate.isEmpty()) {
                Date mDate = oldFormat.parse(soDate);
                so_finalDate = newFormat.format(mDate) + " 00:00:00";
            } else {
                so_finalDate = soDate;
            }

            if (deliveryDate != null && !deliveryDate.isEmpty()) {
                Date nDate = oldFormat.parse(deliveryDate);
                do_finalDate = newFormat.format(nDate) + " 00:00:00";
            } else {
                do_finalDate = deliveryDate;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//		Cursor mcursor = SOTDatabase.getCursor();		
        ArrayList<String> getprod = new ArrayList<String>();

        if (soNo != null && !soNo.isEmpty()) {

            getprod = OfflineDatabase.getproductcodefromsono(soNo);

            for (int i = 0; i < getprod.size(); i++) {
                Log.d("getprod", getprod.get(i));
                int count = 1;
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {

                            String pCode = cursor.getString(cursor.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));

                            if (getprod.get(i).matches(pCode)) {
                                Log.d("Matched", "Matched");
                                break;
                            } else {
                                if (count == cursor.getCount()) {
                                    Log.d("Delete this item", "code " + getprod.get(i));
                                    offlinedb.deleteSOItem(soNo, getprod.get(i));
                                }
                            }
                            count++;
                        } while (cursor.moveToNext());
                    }
                }
            }
        }

        if (soNo != null && !soNo.isEmpty()) {
            Log.d(" Not empty So No", "-->" + soNo);
            sono = soNo;
        } else {
            Log.d(" empty SO No", "-->" + soNo);
            sono = OfflineDatabase.getNextRunningNumber("SOPrefix", "NextSoNo");
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datetime = timeFormat.format(new Date());
        System.out.println(datetime);

        ArrayList<HashMap<String, String>> offlineSODetails = new ArrayList<HashMap<String, String>>();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String pCode = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
                    String pName = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
                    String cQty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
                    String lQty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
                    String qty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
                    String foc = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_FOC));
                    String price = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
                    String pcsPerCarton = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
                    String total = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TOTAL));
                    String itemDiscount = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
                    String taxType = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
                    String taxValue = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
                    String retail_Price = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));

                    String sub_ToTal = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
                    String net_ToTal = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
                    String tax_amount = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TAX));

                    String cprice = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));

                    String exchangeQty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));

                    if (exchangeQty != null && !exchangeQty.isEmpty()) {

                    } else {
                        exchangeQty = "";
                    }

                    double sbTotal = SOTDatabase.getsubTotal(slNo);
                    billDiscs = billDiscount * sbTotal;

                    totalDiscount = billDiscs + Double.parseDouble(itemDiscount);
                    subTot = Double.parseDouble(sub_ToTal);
                    ntTtl = Double.parseDouble(net_ToTal);

                    tx = Double.parseDouble(tax_amount);
                    String billDisc = twoDecimalPoint(billDiscs);
                    String subTtl = twoDecimalPoint(subTot);
                    String netTtl = twoDecimalPoint(ntTtl);
                    String txAmt = fourDecimalPoint(tx);
                    String totalDisc = twoDecimalPoint(totalDiscount);

                    HashMap<String, String> productvalues = new HashMap<String, String>();
                    productvalues.put("DateTime", datetime);
                    productvalues.put("SoNo", soNo);
                    productvalues.put("slNo", slNo + "");
                    productvalues.put("ProductCode", pCode);
                    productvalues.put("ProductName", pName);
                    productvalues.put("CQty", cQty);
                    productvalues.put("Lqty", lQty);
                    productvalues.put("Qty", qty);
                    productvalues.put("FOCQty", foc);
                    productvalues.put("PcsPerCarton", pcsPerCarton);
                    productvalues.put("RetailPrice", retail_Price);
                    productvalues.put("Price", price);
                    productvalues.put("Total", total);
                    productvalues.put("ItemDiscount", itemDiscount);
                    productvalues.put("BillDiscount", billDisc);
                    productvalues.put("TotalDiscount", totalDisc);
                    productvalues.put("SubTotal", subTtl);
                    productvalues.put("Tax", txAmt);
                    productvalues.put("NetTotal", netTtl);
                    productvalues.put("TaxType", taxType);
                    productvalues.put("TaxPerc", taxValue);

                    productvalues.put("DOQty", "");
                    productvalues.put("DOFocQty", "");
                    productvalues.put("InvoiceQty", "");
                    productvalues.put("InvoiceFocQty", "");

                    productvalues.put("ExchangeQty", exchangeQty);
                    productvalues.put("CartonPrice", cprice);
                    productvalues.put("ItemRemarks", "");

                    productvalues.put("CreateUser", user);
                    productvalues.put("CreateDate", datetime);
                    productvalues.put("ModifyUser", "");
                    productvalues.put("ModifyDate", datetime);
                    productvalues.put("DownloadStatus", downloadstatus);
                    productvalues.put("OfflineSoNo", sono);

                    offlineSODetails.add(productvalues);


                    Log.d("Detail", detail_json);

                    slNo++;
                } while (cursor.moveToNext());
            }
            OfflineDatabase.storeSODetail(offlineSODetails);
            Log.d("Detail", offlineSODetails.toString());
        }

        double blDsc = billDisct;
        double itemDisc = SOTDatabase.getitemDisc();
        double totDisc = blDsc + itemDisc;

        HashMap<String, String> header = new HashMap<String, String>();

        header.put("DateTime", datetime);
        header.put("SoNo", soNo);
        header.put("SoDate", so_finalDate);
        header.put("DeliveryDate", do_finalDate);
        header.put("LocationCode", locationCode);
        header.put("CustomerCode", customerCode);
        header.put("CustomerName", customerName);
        header.put("Total", ttl);
        header.put("ItemDiscount", itemDisc + "");
        header.put("BillDiscount", blDsc + "");
        header.put("TotalDiscount", totDisc + "");
        header.put("SubTotal", subTotal);
        header.put("Tax", totTax);
        header.put("NetTotal", netTotal);
        header.put("Remarks", remarks);
        header.put("Status", "0");
        header.put("CurrencyCode", currencyCode);
        header.put("CurrencyRate", currencyRate);

        header.put("CreateUser", user);
        header.put("CreateDate", datetime);
        header.put("ModifyUser", "");
        header.put("ModifyDate", datetime);
        header.put("DownloadStatus", downloadstatus);
        header.put("OfflineSoNo", sono);
        retnSONo = OfflineDatabase.saveSOheader(header);

        Log.d("header", header.toString());
        Log.d(" retnSONo", "-->" + retnSONo);

        if (retnSONo != null && !retnSONo.isEmpty()) {
            OfflineDatabase.updateNextRunningNumber("NextSoNo");
        } else {
            retnSONo = "failed";
        }


        return retnSONo;
    }

    public static String summaryPlaceOrderOffline(String webMethName, String downloadstatus) throws JSONException, IOException,
            XmlPullParserException {

        cursor = DBCatalog.getCursor();
        int slNo = 1;
        detail_json = "";
        header_json = "";
        double ntTtl, totl, smTax, sbTtl, tx, subTot, totlitemDisc, nettot;
        String totltax, subtotl;

        totl = DBCatalog.getTotal();

        nettot = DBCatalog.getNetTotal();

        totlitemDisc = DBCatalog.getitemDisc();

        smTax = DBCatalog.getTax();
        totltax = fourDecimalPoint(smTax);

        sbTtl = DBCatalog.getSubTotal();
        subtotl = twoDecimalPoint(sbTtl);
        String sono = "", retnSONo = "";

        String locationCode = SalesOrderSetGet.getLocationcode();
        String customerCode = Catalog.getCustomerCode();
        String soDate = SalesOrderSetGet.getSaleorderdate();
        String user = SupplierSetterGetter.getUsername();
        String currencyCode = SalesOrderSetGet.getCurrencycode();
        String currencyRate = SalesOrderSetGet.getCurrencyrate();
        String deliveryDate = SalesOrderSetGet.getDeliverydate();
        String remarks = SalesOrderSetGet.getRemarks();
        String customerName = offlinedb.getCustomerName(customerCode);
        String so_finalDate = "", do_finalDate = "";

        Log.d("SOSave ", "so_date" + soDate);
        Log.d("SOSave ", "do_date" + deliveryDate);

        try {
            SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (soDate != null && !soDate.isEmpty()) {
                Date mDate = oldFormat.parse(soDate);
                so_finalDate = newFormat.format(mDate) + " 00:00:00";
            } else {
                so_finalDate = soDate;
            }

            if (deliveryDate != null && !deliveryDate.isEmpty()) {
                Date nDate = oldFormat.parse(deliveryDate);
                do_finalDate = newFormat.format(nDate) + " 00:00:00";
            } else {
                do_finalDate = deliveryDate;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sono = OfflineDatabase.getNextRunningNumber("SOPrefix", "NextSoNo");


        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datetime = timeFormat.format(new Date());
        System.out.println(datetime);

        ArrayList<HashMap<String, String>> offlineSODetails = new ArrayList<HashMap<String, String>>();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String pCode = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_PRODUCT_CODE));
                    String pName = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_PRODUCT_NAME));
                    String cQty = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_CARTON_QTY));
                    String lQty = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_LOOSE_QTY));
                    String price = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_PRICE));
                    String qty = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_QUANTITY));
                    String pcsPerCarton = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_PCSPERCARTON));
                    String wholesaleprice = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_WHOLESALEPRICE));
                    String total = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_TOTAL));
                    String foc = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_FOC));
                    String tax = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_TAX));
                    String taxtype = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_TAXTYPE));
                    String taxtvalue = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_TAXVALUE));
                    String itemdiscount = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_ITEM_DISCOUNT));
                    String nettotal = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_NETTOTAL));
                    String sub_ToTal = cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_SUB_TOTAL));

                    String cprice = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));

                    String exchangeQty = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));


                    tx = Double.parseDouble(tax);
                    ntTtl = Double.parseDouble(nettotal);

                    subTot = Double.parseDouble(sub_ToTal);
                    String subTtl = twoDecimalPoint(subTot);
                    String txAmt = fourDecimalPoint(tx);

                    HashMap<String, String> productvalues = new HashMap<String, String>();
                    productvalues.put("DateTime", datetime);
                    productvalues.put("SoNo", "");
                    productvalues.put("slNo", slNo + "");
                    productvalues.put("ProductCode", pCode);
                    productvalues.put("ProductName", pName);
                    productvalues.put("CQty", cQty);
                    productvalues.put("Lqty", lQty);
                    productvalues.put("Qty", qty);
                    productvalues.put("FOCQty", foc);
                    productvalues.put("PcsPerCarton", pcsPerCarton);
                    productvalues.put("RetailPrice", wholesaleprice);
                    productvalues.put("Price", price);
                    productvalues.put("Total", total);
                    productvalues.put("ItemDiscount", itemdiscount);
                    productvalues.put("BillDiscount", "");
                    productvalues.put("TotalDiscount", "");
                    productvalues.put("SubTotal", subTtl);
                    productvalues.put("Tax", txAmt);
                    productvalues.put("NetTotal", ntTtl + "");
                    productvalues.put("TaxType", taxtype);
                    productvalues.put("TaxPerc", taxtvalue);

                    productvalues.put("DOQty", "");
                    productvalues.put("DOFocQty", "");
                    productvalues.put("InvoiceQty", "");
                    productvalues.put("InvoiceFocQty", "");

                    productvalues.put("ExchangeQty", exchangeQty);
                    productvalues.put("CartonPrice", cprice);
                    productvalues.put("ItemRemarks", "");

                    productvalues.put("CreateUser", user);
                    productvalues.put("CreateDate", datetime);
                    productvalues.put("ModifyUser", "");
                    productvalues.put("ModifyDate", datetime);
                    productvalues.put("DownloadStatus", downloadstatus);
                    productvalues.put("OfflineSoNo", sono);

                    offlineSODetails.add(productvalues);


                    Log.d("Detail", detail_json);

                    slNo++;
                } while (cursor.moveToNext());
            }
            OfflineDatabase.storeSODetail(offlineSODetails);
            Log.d("Detail", offlineSODetails.toString());
        }

        HashMap<String, String> header = new HashMap<String, String>();

        header.put("DateTime", datetime);
        header.put("SoNo", "");
        header.put("SoDate", so_finalDate);
        header.put("DeliveryDate", so_finalDate);
        header.put("LocationCode", locationCode);
        header.put("CustomerCode", customerCode);
        header.put("CustomerName", customerName);
        header.put("Total", totl + "");
        header.put("ItemDiscount", totlitemDisc + "");
        header.put("BillDiscount", "");
        header.put("TotalDiscount", "");
        header.put("SubTotal", subtotl);
        header.put("Tax", totltax);
        header.put("NetTotal", nettot + "");
        header.put("Remarks", remarks);
        header.put("Status", "0");
        header.put("CurrencyCode", currencyCode);
        header.put("CurrencyRate", currencyRate);

        header.put("CreateUser", user);
        header.put("CreateDate", datetime);
        header.put("ModifyUser", "");
        header.put("ModifyDate", datetime);
        header.put("DownloadStatus", downloadstatus);
        header.put("OfflineSoNo", sono);
        retnSONo = OfflineDatabase.saveSOheader(header);

        Log.d("header", header.toString());
        Log.d(" retnSONo", "-->" + retnSONo);

        if (retnSONo != null && !retnSONo.isEmpty()) {
            OfflineDatabase.updateNextRunningNumber("NextSoNo");
        } else {
            retnSONo = "failed";
        }


        return retnSONo;
    }

    public static String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }

    public static String fourDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setMinimumFractionDigits(4);
        String tot = df.format(d);

        return tot;
    }

}
