package com.winapp.helper;

public interface Constants {
    public static final String PREF_NAME = "sot";
    public static final String PREF_LANGUAGE = "language";
    public static final String PREF_ALLOW_APPLICATIONTYPE = "allow_pref_allow_applicationtype";
    public static final String PREF_ALLOW_GENERALSETTINGS = "allow_pref_allow_generalsettings";

    public static final String PREF_ALLOW_LOCATIONNAME = "allow_pref_allow_locationname";
    public static final String PREF_ALLOW_LOCATIONCODE = "allow_pref_allow_locationcode";
    public static final String PREF_ALLOW_LOCATION = "allow_pref_allow_location";

    public static final String PREF_ALLOW_LOCATION_HM = "allow_pref_allow_location_hm";

    public static final String PREF_ALLOW_USERNAME = "allow_pref_allow_username";

    public static final String PREF_ALLOW_COMPANYNAME = "allow_pref_allow_company_name";
    public static final String PREF_ALLOW_COMPANYADDRESS1 = "allow_pref_allow_company_address1";
    public static final String PREF_ALLOW_COMPANYADDRESS2 = "allow_pref_allow_company_address2";
    public static final String PREF_ALLOW_COMPANYLOCATION = "allow_pref_allow_company_location";
    public static final String PREF_ALLOW_COMPANYPHONE = "allow_pref_allow_company_phone";

    public static final String PREF_ALLOW_COMPANYTAX = "allow_pref_allow_company_tax";
    public static final String PREF_ALLOW_GOODRECEIVE = "allow_good_receive";
    public static final String PREF_ALLOW_SALESORDER = "allow_sales_order";
    public static final String PREF_ALLOW_DELIVERYORDER = "allow_delivery order";
    public static final String PREF_ALLOW_INVOICE = "allow_invoice";
    public static final String PREF_ALLOW_SALESRETURN = "allow_sales_return";
    public static final String PREF_ALLOW_RECEIPTS = "allow_Receipts";
    public static final String PREF_ALLOW_PRODUCTLIST = "allow_product_list";
    public static final String PREF_ALLOW_STOCKREQUEST = "allow_stock_request";
    public static final String PREF_ALLOW_TRANSFER = "allow_transfer";
    public static final String PREF_ALLOW_CUSTOMERLIST = "allow_customer_list";
    public static final String PREF_ALLOW_CATALOG = "allow_catalog";
    public static final String PREF_ALLOW_SETTINGS = "allow_settings";

    public static final String PREF_ALLOW_TRANSFERCHANGEFROMLOC = "allow_transferchangefromloc";

    public static final String PREF_ALLOW_PRODUCTADD = "allow_productadd";
    public static final String PREF_ALLOW_CUSTOMERADD = "allow_customeradd";
    public static final String PREF_ALLOW_EDITINVOICE = "allow_editinvoice";
    public static final String PREF_ALLOW_DELETEINVOICE = "allow_deleteinvoice";
    public static final String PREF_ALLOW_DELETERECEIPT = "allow_deletereceipt";

    // Webservice Function Names
    public static String FNC_GETSTOCKCOUNTDETAILBYNO = "fncGetStockCountDetailByNo";
    public static String FNC_SAVESTOCKCOUNT = "fncSaveStockCount";
    public static String FNC_GETSTOCKCOUNTHEADER = "fncGetStockCountHeader";

//    public static String FNC_GETPRODUCT = "fncGetProduct";
    public static String FNC_GETPRODUCTBARCODE = "fncGetProductBarCode";

    public static String FNC_GETCATEGORY = "fncGetCategory";
    public static String FNC_GETSUBCATEGORY = "fncGetSubCategory";

    public static String FNC_GETINVOICEPRODUCTSUMMARY = "fncGetInvoiceProductSummary";
    public static String FNC_GETINVOICEHEADERBYUSER = "fncGetInvoiceHeaderByUser";
    public static final String GETSERVERDATETIME = "fncGetServerDateTime";
    public static final String GETATTENDANCE = "fncGetAttendence";
    public static final String SAVEATTENDANCE = "fncSaveAttendenceInOut";

    public static final String GETUSER = "testGetUser";
    // Constants for data passing
    public static final String PRODUCT_CODE = "ProductCode";
    public static final String PRODUCT_NAME = "ProductName";
    public static final String CATEGORYCODE = "CategoryCode";
    public static final String SUBCATEGORYCODE = "SubCategoryCode";
    public static final String PCSPERCARTON = "PcsPerCarton";
    public static final String NOOFCARTON = "NoOfCarton";
    public static final String QTY = "Qty";
    public static final String COUNTCQTY = "CountCQty";
    public static final String COUNTLQTY = "CountLQty";
    public static final String COUNTQTY = "CountQty";
    public static final String PRODUCT_BARCODE = "Barcode";
    public static final String STOCK_TAKE_NO = "StockTakeNo";
    public static final String HAVEBATCH = "HaveBatch";
    public static final String HAVEEXPIRY = "HaveExpiry";


    // ************DBCatalog*****************************//
    public static final String CATALOG_TABLE = "catalog";
    public static final String COLUMN_PRODUCT_ID = "_id";
    public static final String COLUMN_PRODUCT_SLNO = "sno";
    public static final String COLUMN_PRODUCT_CODE = "productcode";
    public static final String COLUMN_PRODUCT_NAME = "productname";
    public static final String COLUMN_CARTON_QTY = "cartonqty";
    public static final String COLUMN_LOOSE_QTY = "looseqty";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "qty";
    public static final String COLUMN_TOTAL = "total";
    public static final String COLUMN_UOMCODE = "uomcode";
    public static final String COLUMN_PCSPERCARTON = "pcspercarton";
    public static final String COLUMN_WHOLESALEPRICE = "wholesaleprice";
    public static final String COLUMN_PRODUCT_IMAGE = "productimage";
    public static final String COLUMN_FOC = "foc";
    public static final String COLUMN_ITEM_DISCOUNT = "itemdiscount";
    public static final String COLUMN_TAX = "tax";
    public static final String COLUMN_SUB_TOTAL = "subtotal";
    public static final String COLUMN_TAXVALUE = "taxvalue";
    public static final String COLUMN_TAXTYPE = "taxtype";
    public static final String COLUMN_NETTOTAL = "nettotal";

    public static final String COLUMN_CARTONPRICE = "carton_price";
    public static final String COLUMN_EXCHANGEQTY = "exchange_qty";

    // Constants for identifying Activity
    public static final String ACTIVITY_STOCK_TAKE_HEADER_EDIT = "stock_take_header_edit";
    public static final String ACTIVITY_STOCK_TAKE_PRODUCT = "stock_take_product";

    // ************OFFLINE*****************************//
    public static final String PREF_OFFLINE = "offline";
    public static final String PREF_ALLOW_COMPANYTYPE = "companycode";
    public static final String PREF_ALLOW_OVERDUE = "overdue";

    public static final String FNCA4INVOICEGENERATE = "fncA4InvoiceGenerate";
    public static final String FNCA4RECEIPTGENERATE = "fncA4ReceiptGenerate";

}
