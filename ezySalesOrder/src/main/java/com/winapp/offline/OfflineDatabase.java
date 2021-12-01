package com.winapp.offline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.XMLParser;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.SO;
import com.winapp.sot.SalesOrderSetGet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

public class OfflineDatabase extends SQLiteOpenHelper {
    static String comapanyCode;
    static OfflineSettingsManager offlinemanager;
    static OfflineDatabase instance = null;
    static SQLiteDatabase database = null;
    static final String DATABASE_NAME = "Offline.db";
    static final int DATABASE_VERSION = 23;

    /* offline tables */
    public static final String ONLINEMODE_TABLE = "tblOnlineMode";
    public static final String INTERNET_MODE_TABLE = "tblInternetMode";
    public static final String USERMASTER_TABLE = "tblUserMaster";
    public static final String DEVICEID_TABLE = "tblDevice";
    public static final String USERPERMISSION_TABLE = "tblGetUserPermission";
    public static final String GENERALSETTING_TABLE = "tblGetGeneralSettings";
    public static final String GETCOMPANY_TABLE = "tblGetCompany";
    public static final String GETCUSTOMER_TABLE = "tblGetCustomer";
    public static final String GETCURRENCY_TABLE = "tblGetCurrency";
    public static final String GETLOCATION_TABLE = "tblGetLocation";
    public static final String GETNEXTRUNNINGNUMBER_TABLE = "tblNextRunningNumber";
    public static final String GETCATEGORY_TABLE = "tblGetCategory";
    public static final String GETSUBCATEGORY_TABLE = "tblGetSubCategory";
    public static final String GETPRODUCT_TABLE = "tblGetProduct";
    public static final String GETPRODUCTBARCODE_TABLE = "tblGetProductBarCode";
    public static final String GETINVOICEHEADER_TABLE = "tblGetInvoiceHeader";
    public static final String GETINVOICEDETAIL_TABLE = "tblGetInvoiceDetail";
    public static final String GETHEADERBYINVOICENO_TABLE = "tblGetHeaderByInvoiceNo";
    public static final String GETINVOICECARTONDETAIL_TABLE = "tblGetInvoiceCartonDetail";
    public static final String GETPRODUCTPRICEFORSALES_TABLE = "tblGetProductPriceForSales";
    public static final String GETCOMPANYLOGO_TABLE = "tblGetCompanyLogo";
    public static final String GETPRODUCTSTOCK_TABLE = "tblGetProductStock";
    public static final String GETDATETIME_TABLE = "tblDatetime";
    public static final String DOWNLOADSTATUS_TABLE = "tblDownloadStatus";
    public static final String GETPAYMODE_TABLE = "tblPayMode";
    public static final String GETBANK_TABLE = "tblBank";
    public static final String GETINVOICEHEADERONLYHAVEBALANCE_TABLE = "tblInvoiceHeaderOnlyHaveBalance";
    public static final String GETRECEIPTEHEADER_TABLE = "tblGetReceiptHeader";
    public static final String GETRECEIPTDETAIL_TABLE = "tblGetReceiptDetail";
    public static final String GETAREA_TABLE = "tblGetArea";
    public static final String GETTERMS_TABLE = "tblGetTerms";
    public static final String GETCUSTOMERGROUP_TABLE = "tblGetCustomerGroup";
    public static final String GETCUSTOMERPRICE_TABLE = "tblGetCustomerPrice";
    public static final String GETMOBILESETTINGS_TABLE = "tblGetMobileSettings";
    public static final String GETVAN_TABLE = "tblGetVan";
    public static final String GETMOBILEPRINTFOOTER_TABLE = "tblGetMobilePrintFooter";
    public static final String GETSOHEADER_TABLE = "tblGetSOHeader";
    public static final String GETSODETAIL_TABLE = "tblGetSODetail";
    public static final String GETTAX_TABLE= "tblGetTax";

    public static final String IMAGE_TABLE = "tblGetImage";
    public static final String GETINVOICEHEADERWITHOUTDOSIGN_TABLE = "tblGetInvoiceHeaderWithoutDOSign";

    // added
    public static final String GETPRODUCTMAINIMAGE_TABLE = "tblGetProductMainImage";
    public static final String GETPRODUCTSUBIMAGES_TABLE = "tblGetProductSubImages";

    // Index

    public static final String GETPRODUCTSTOCK_TABLE_INDEX = "tblGetProductStockIndex";
    public static final String GETPRODUCT_TABLE_INDEX = "tblGetProductIndex";
    public static final String GETPRODUCTMAINIMAGE_TABLE_INDEX = "tblGetProductMainImageIndex";
    public static final String GETPRODUCTSUBIMAGES_TABLE_INDEX = "tblGetProductSubImagesIndex";
    // added new 07/04/2017
    public static final String GETCUSTOMER_TABLE_INDEX = "tblGetCustomerIndex";
    public static final String GETCUSTOMERPRICE_TABLE_INDEX = "tblGetCustomerPriceIndex";

    public static final String CREATE_USER = "CreateUser";
    public static final String CREATE_DATE = "CreateDate";
    public static final String MODIFY_USER = "ModifyUser";
    public static final String MODIFY_DATE = "ModifyDate";
    public static final String COLUMN_ONLINEMODE = "OnlineMode";
    public static final String COLUMN_OFFLINE_DIALOG = "OfflineDialog";
    public static final String COLUMN_ONLINE_DIALOG = "OnlineDialog";
    public static final String COLUMN_SERVERDATEANDTIME = "ServerDate";
    public static final String COLUMN_LOCATIONLOGO = "logo";
    /* column table id */
    public static final String COLUMN_TABLE_ID = "_id";
    /* CheckUserNameAndPassword */
    public static final String COLUMN_USERID = "UserName";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_USERGROUPCODE = "UserGroupCode";
    /* DeviceActivation */
    public static final String COLUMN_ACTIVATIONCODE = "ActivationCode";
    public static final String COLUMN_DEVICEID = "DeviceId";
    public static final String COLUMN_DEVICENAME = "DeviceName";
    public static final String COLUMN_DOWNLOADSTATUS = "download_status";
    public static final String COLUMN_DEVICE_REMARKS = "Remarks";
    public static final String COLUMN_DEVICE_ISACTIVE = "IsActive";
    public static final String COLUMN_ACTIVEDATE = "ActiveDate";
    public static final String COLUMN_INACTIVEDATE = "InActiveDate";
    public static final String COLUMN_SOPREFIX = "SOPrefix";
    public static final String COLUMN_NEXTSONO = "NextSoNo";
    public static final String COLUMN_DOPREFIX = "DOPrefix";
    public static final String COLUMN_NEXTDO = "NextDO";
    public static final String COLUMN_INVOICEPREFIX = "InvoicePrefix";
    public static final String COLUMN_NEXTINVOICENO = "NextInvoiceNo";
    public static final String COLUMN_RECEIPTPREFIX = "ReceiptPrefix";
    public static final String COLUMN_NEXTRECEIPTNO = "NextReceiptNo";
    public static final String COLUMN_GRAPREFIX = "GRAPrefix";
    public static final String COLUMN_NEXTGRANO = "NextGRANo";
    public static final String COLUMN_LASTLOGINDATE = "LastLoginDate";
    public static final String COLUMN_STOCKREQPREFIX = "StockReqPrefix";
    public static final String COLUMN_NEXTSTOCKREQNO = "NextStockReqNo";
    public static final String COLUMN_TRANSFERPREFIX = "TransferPrefix";
    public static final String COLUMN_NEXTTRANSFERNO = "NextTransferNo";
    public static final String COLUMN_CUSTOMERPREFIX = "CustomerPrefix";
    public static final String COLUMN_NEXTCUSTOMERNO = "CustomerNextNo";
    public static final String COLUMN_COMPANYNAMEALIAS = "CompanyNameAlias";
    /* GetUserPermission */
    public static final String COLUMN_FORMCODE = "FormCode";
    public static final String COLUMN_FORMNAME = "FormName";
    public static final String COLUMN_RESULT = "Result";

    /* GetGeneralSettings */
    public static final String COLUMN_SETTINGID = "SettingID";
    public static final String COLUMN_SETTINGVALUE = "SettingValue";

    /* GetCompany */
    public static final String COLUMN_COMPANYCODE = "CompanyCode";
    public static final String COLUMN_COMPANYNAME = "CompanyName";
    public static final String COLUMN_ADDRESS1 = "Address1";
    public static final String COLUMN_ADDRESS2 = "Address2";
    public static final String COLUMN_ADDRESS3 = "Address3";
    public static final String COLUMN_COUNTRY = "Country";
    public static final String COLUMN_ZIPCODE = "ZipCode";
    public static final String COLUMN_PHONENO = "PhoneNo";
    public static final String COLUMN_WEBSITE = "Website";
    public static final String COLUMN_TAXTYPE = "TaxType";
    public static final String COLUMN_TAXVALUE = "TaxValue";
    public static final String COLUMN_TAXREGNO = "TaxRegNo";
    public static final String COLUMN_BUSINESSREGNO = "BusinessRegNo";
    public static final String COLUMN_SHORTCODE = "ShortCode";
    public static final String COLUMN_LOGO = "Logo";

    /* GetCustomer */
    public static final String COLUMN_CUSTOMERCODE = "CustomerCode";
    public static final String COLUMN_CUSTOMERNAME = "CustomerName";
    public static final String COLUMN_CONTACTPERSON = "ContactPerson";
    public static final String COLUMN_CUSTOMER_ADDRESS1 = "Address1";
    public static final String COLUMN_CUSTOMER_ADDRESS2 = "Address2";
    public static final String COLUMN_CUSTOMER_ADDRESS3 = "Address3";
    public static final String COLUMN_PhoneNo = "PhoneNo";
    public static final String COLUMN_HANDPHONENO = "HandphoneNo";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_FAXNO = "FaxNo";
    public static final String COLUMN_HAVETAX = "HaveTax";
    public static final String COLUMN_CUSTOMERGROUPCODE = "CustomerGroupCode";
    public static final String COLUMN_TERMCODE = "TermCode";
    public static final String COLUMN_CREDITLIMIT = "CreditLimit";
    public static final String COLUMN_CUSTOMER_TAXTYPE = "TaxType";
    public static final String COLUMN_CUSTOMER_TAXVALUE = "TaxValue";
    public static final String COLUMN_AREACODE = "AreaCode";
    public static final String COLUMN_OUTSTANDINGAMOUNT = "OutStandingAmount";
    public static final String COLUMN_REFERENCELOCATION = "ReferenceLocation";

    public static final String COLUMN_ANDROIDVERSION_SFA = "AndroidVersion_SFA";
    public static final String COLUMN_ANDROIDVERSION_SFA_CHECKPLAYSTORE = "AndroidVersionSFAcheckplaystore";
    /* GetCurrency */
    public static final String COLUMN_CURRENCYCODE = "CurrencyCode";
    public static final String COLUMN_CURRENCYNAME = "CurrencyName";
    public static final String COLUMN_CURRENCYRATE = "CurrencyRate";
    /* GetLocation */
    public static final String COLUMN_LOCATIONCODE = "LocationCode";
    public static final String COLUMN_LOCATIONNAME = "LocationName";
    public static final String COLUMN_ISMAINLOCATION = "isMainLocation";
    public static final String COLUMN_HAVEBATCHONSTOCKIN = "HaveBatchOnStockIn";
    public static final String COLUMN_HAVEBATCHONSTOCKOUT = "HaveBatchOnStockOut";
    public static final String COLUMN_HAVEBATCHONTRANSFER = "HaveBatchOnTransfer";
    public static final String COLUMN_HAVEBATCHONSTOCKADJUSTMENT = "HaveBatchOnStockAdjustment";
    public static final String COLUMN_NEXTBATCHNO = "NextBatchNo";
    public static final String COLUMN_LASTLOGINLOCATION = "LastLoginLocation";
    /* GetCategory */
    public static final String COLUMN_CATEGORYCODE = "CategoryCode";
    public static final String COLUMN_DESCRIPTION = "Description";
    /* GetSubCategory */
    public static final String COLUMN_SUBCATEGORYCODE = "SubCategoryCode";
    /* GetProduct */
    public static final String COLUMN_PRODUCTCODE = "ProductCode";
    public static final String COLUMN_PRODUCTNAME = "ProductName";
    public static final String COLUMN_SUPPLIERCODE = "SupplierCode";
    public static final String COLUMN_UOMCODE = "UOMCode";
    public static final String COLUMN_PCSPERCARTON = "PcsPerCarton";
    public static final String COLUMN_WEIGHT = "Weight";
    public static final String COLUMN_UNITCOST = "UnitCost";
    public static final String COLUMN_AVERAGECOST = "AverageCost";
    public static final String COLUMN_RETAILPRICE = "RetailPrice";
    public static final String COLUMN_WHOLESALEPRICE = "WholeSalePrice";
    public static final String COLUMN_HAVEBATCH = "HaveBatch";
    public static final String COLUMN_HAVEEXPIRY = "HaveExpiry";
    public static final String COLUMN_HAVEMFGDATE = "HaveMfgDate";
    public static final String COLUMN_WEIGHTBARCODEASSIGNED = "WeightBarcodeAssigned";
    public static final String COLUMN_WEIGHTBARCODESTARTSON = "WeightBarcodeStartsOn";
    public static final String COLUMN_WEIGHTBARCODEENDSON = "WeightBarcodeEndsOn";
    public static final String COLUMN_ISACTIVE = "IsActive";
    public static final String COLUMN_NONSTOCKITEM = "NonStockItem";
    public static final String COLUMN_TAXPERC = "TaxPerc";
    public static final String COLUMN_SPECIFICATION = "Specification";
    public static final String COLUMN_SEQNO = "SeqNo";
    /* GetProductBarcode */
    public static final String COLUMN_BARCODE = "Barcode";
    /* GetInvoiceHeader */
    public static final String COLUMN_INVOICEDATE = "InvoiceDate";
    public static final String COLUMN_PAIDAMOUNT = "PaidAmount";
    public static final String COLUMN_CREDITAMOUNT = "CreditAmount";
    public static final String COLUMN_BALANCEAMOUNT = "BalanceAmount";
    public static final String COLUMN_REMARKS = "Remarks";
    public static final String COLUMN_SONO = "SoNo";
    public static final String COLUMN_DONO = "DoNo";
    public static final String COLUMN_TOTALBALANCE = "TotalBalance";
    /* GetInvoiceDetail */
    public static final String COLUMN_INVOICENO = "InvoiceNo";
    public static final String COLUMN_SLNO = "slNo";
    public static final String COLUMN_CQTY = "CQty";
    public static final String COLUMN_LQTY = "Lqty";
    public static final String COLUMN_QTY = "Qty";
    public static final String COLUMN_FOCQTY = "FOCQty";
    public static final String COLUMN_PRICE = "Price";
    public static final String COLUMN_TOTAL = "Total";
    public static final String COLUMN_ITEMDISCOUNT = "ItemDiscount";
    public static final String COLUMN_BILLDISCOUNT = "BillDiscount";
    public static final String COLUMN_TOTALDISCOUNT = "TotalDiscount";
    public static final String COLUMN_SUBTOTAL = "SubTotal";
    public static final String COLUMN_TAX = "Tax";
    public static final String COLUMN_NETTOTAL = "NetTotal";
    /* fncGetProductPriceForSales */
    public static final String COLUMN_CARTON = "CartonPrice";
    /* fncGetCompanyLogo */
    public static final String COLUMN_COMPANYLOGO = "Logo";
    /* fncGetCustomerGroup */
    public static final String COLUMN_COMPANYGROUPCODE = "Code";
    /* fncGetProductStock */
    public static final String COLUMN_NOOFCARTON = "NoOfCarton";
    public static final String COLUMN_MINIMUMSELLINGPRICE = "MinimumSellingPrice";
    public static final String COLUMN_CARTONPRICE = "CartonPrice";
    public static final String COLUMN_EXCHANGEQTY = "ExchangeQty";
    /* fncGetBank */
    public static final String COLUMN_BANKCODE = "BankCode";
    /* fncGetPaymode */
    public static final String COLUMN_PAYMODECODE = "PayModeCode";
    /* fncGetReceiptHeader */
    public static final String COLUMN_RECEIPTNO = "ReceiptNo";
    public static final String COLUMN_RECEIPTDATE = "ReceiptDate";
    public static final String COLUMN_CHEQUEDATE = "ChequeDate";
    public static final String COLUMN_CHEQUENO = "ChequeNo";
    public static final String COLUMN_PAYMODE = "Paymode";
    /* fncGetReceiptDetail */
    public static final String COLUMN_CODE = "Code";

    /* fncGetGetMobileSettings */
    public static final String COLUMN_SHOWLOGO = "ShowLogo";
    public static final String COLUMN_SHOWADDRESS1 = "ShowAddress1";
    public static final String COLUMN_SHOWADDRESS2 = "ShowAddress2";
    public static final String COLUMN_SHOWADDRESS3 = "ShowAddress3";
    public static final String COLUMN_SHOWCOUNTRYPOSTAL = "ShowCountryPostal";
    public static final String COLUMN_SHOWPHONE = "ShowPhone";
    public static final String COLUMN_SHOWFAX = "ShowFax";
    public static final String COLUMN_SHOWEMAIL = "ShowEmail";
    public static final String COLUMN_SHOWTAXREGNO = "ShowTaxRegNo";
    public static final String COLUMN_SHOWBIZREGNO = "ShowBizRegNo";
    public static final String COLUMN_SHOWCUSTOMERCODE = "ShowCustomerCode";
    public static final String COLUMN_SHOWCUSTOMERNAME = "ShowCustomerName";
    public static final String COLUMN_CUSTOMERADDRESS1 = "ShowCustomerAddress1";
    public static final String COLUMN_CUSTOMERADDRESS2 = "ShowCustomerAddress2";
    public static final String COLUMN_CUSTOMERADDRESS3 = "ShowCustomerAddress3";
    public static final String COLUMN_SHOWCUSTOMERPHONE = "ShowCustomerPhone";
    public static final String COLUMN_SHOWCUSTOMERHP = "ShowCustomerHP";
    public static final String COLUMN_SHOWCUSTOMEREMAIL = "ShowCustomerEmail";
    public static final String COLUMN_SHOWCUSTOMERTERMS = "ShowCustomerTerms";
    public static final String COLUMN_SHOWTOTALOUTSTANDING = "ShowTotalOutstanding";
    public static final String COLUMN_SHOWFOOTER = "ShowFooter";
    public static final String COLUMN_SHOWBATCHDETAILS = "ShowBatchDetails";
    public static final String COLUMN_SHOWPRODUCTFULLNAME = "ShowProductFullName";

    //Added
    public static final String COLUMN_RECEIPTMESSAGE = "ReceiptMessage";
    public static final String COLUMN_SORTORDER = "SortOrder";
    public static final String COLUMN_SODATE = "SoDate";
    public static final String COLUMN_DELIVERYDATE = "DeliveryDate";
    public static final String COLUMN_STATUS = "Status";
    public static final String COLUMN_ITEMREMARKS = "ItemRemarks";
    public static final String COLUMN_DOQTY = "DOQty";
    public static final String COLUMN_DOFOCQTY = "DOFocQty";
    public static final String COLUMN_DOCQTY = "DOCQty";
    public static final String COLUMN_INVOICEQTY = "InvoiceQty";
    public static final String COLUMN_INVOICEFOCQTY = "InvoiceFocQty";
    public static final String COLUMN_INVOICECQTY = "InvoiceCQty";

//Image table

    public static final String COLUMN_IMAGE_NO = "ImageNo";
    public static final String COLUMN_IMAGE_TYPE = "ImageType";
    public static final String COLUMN_SIGNATURE_IMAGE = "SignatureImage";
    public static final String COLUMN_PRODUCT_IMAGE = "ProductImage";
    public static final String COLUMN_VANCODE = "VanCode";

    public static final String COLUMN_POD_SIGN = "PODSign";
    public static final String COLUMN_TAXCODE = "TaxCode";
    public static final String COLUMN_TAXNAME ="TaxName";

    public static final String COLUMN_MINIMUMCARTONSELLINGPRICE = "MinimumCartonSellingPrice";

    // Sales Return
    public static final String GETSRHEADER_TABLE = "tblGetSalesReturnHeader";
    public static final String GETSRDETAIL_TABLE = "tblGetSalesReturnDetail";
    public static final String COLUMN_SRPREFIX = "SRPrefix";
    public static final String COLUMN_NEXTSRNO = "NextSRNo";

    public static final String COLUMN_SALESRETURNNO = "SalesReturnNo";
    public static final String COLUMN_SALESRETURNDATE = "SalesReturnDate";
    public static final String COLUMN_STOCKADJREFCODE = "StockAdjRefCode";


    public OfflineDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

		/* Create OnlineMode Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ONLINEMODE_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_SLNO + " INTEGER, " + COLUMN_ONLINEMODE + " TEXT)");

		/* Create InternetMode Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + INTERNET_MODE_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_SLNO + " INTEGER, " + COLUMN_OFFLINE_DIALOG
                + " TEXT, " + COLUMN_ONLINE_DIALOG + " TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + DOWNLOADSTATUS_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

		/* Create CheckUserNameAndPassword Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + USERMASTER_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_USERID + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_USERGROUPCODE + " TEXT,"
                + COLUMN_ISACTIVE + " TEXT,"
                + COLUMN_COMPANYCODE + " TEXT,"
                + COLUMN_COMPANYNAME + " TEXT,"
                + COLUMN_LASTLOGINLOCATION + " TEXT,"
                + COLUMN_LOCATIONNAME + " TEXT)");

		/* Create DeviceActivation Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DEVICEID_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_DEVICENAME + " TEXT,"
                + COLUMN_DEVICEID + " TEXT)");

		/* Create GetUserPermission Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + USERPERMISSION_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT,"
                + COLUMN_FORMCODE + " TEXT," + COLUMN_FORMNAME + " TEXT,"
                + COLUMN_USERGROUPCODE + " TEXT," + COLUMN_RESULT + " TEXT)");

		/* Create GetGeneralSettings Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GENERALSETTING_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT,"
                + COLUMN_SETTINGID + " TEXT," + COLUMN_SETTINGVALUE + " TEXT)");

		/* Create GetCompany Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETCOMPANY_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_COMPANYNAME + " TEXT,"
                + COLUMN_ADDRESS1 + " TEXT," + COLUMN_ADDRESS2 + " TEXT,"
                + COLUMN_ADDRESS3 + " TEXT," + COLUMN_COUNTRY + " TEXT,"
                + COLUMN_ZIPCODE + " TEXT," + COLUMN_PHONENO + " TEXT,"
                + COLUMN_WEBSITE + " TEXT," + COLUMN_TAXTYPE + " TEXT,"
                + COLUMN_TAXVALUE + " TEXT," + COLUMN_TAXREGNO + " TEXT,"
                + COLUMN_BUSINESSREGNO + " TEXT," + COLUMN_SHORTCODE + " TEXT,"
                + COLUMN_LOGO + " TEXT,"
                + COLUMN_FAXNO + " TEXT,"
                + COLUMN_ANDROIDVERSION_SFA + " TEXT,"
                + COLUMN_ANDROIDVERSION_SFA_CHECKPLAYSTORE + " TEXT,"
                + COLUMN_EMAIL + " TEXT)");

		/* Create GetCompanyLogo Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETCOMPANYLOGO_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_COMPANYLOGO + " TEXT)");

		/* Create GetLocation Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETLOCATION_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_LOCATIONCODE + " TEXT," + COLUMN_LOCATIONNAME
                + " TEXT," + COLUMN_ISMAINLOCATION + " TEXT,"
                + COLUMN_HAVEBATCHONSTOCKIN + " TEXT,"
                + COLUMN_HAVEBATCHONSTOCKOUT + " TEXT,"
                + COLUMN_HAVEBATCHONTRANSFER + " TEXT,"
                + COLUMN_HAVEBATCHONSTOCKADJUSTMENT + " TEXT,"
                + COLUMN_NEXTBATCHNO + " TEXT," + COLUMN_COMPANYCODE + " TEXT)");

		/* Create fncGetNextRunningNumber Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETNEXTRUNNINGNUMBER_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_SLNO
                + " TEXT," + COLUMN_SOPREFIX + " TEXT," + COLUMN_NEXTSONO
                + " TEXT," + COLUMN_DOPREFIX + " TEXT," + COLUMN_NEXTDO
                + " TEXT," + COLUMN_INVOICEPREFIX + " TEXT,"
                + COLUMN_NEXTINVOICENO + " TEXT," + COLUMN_SRPREFIX + " TEXT,"
                + COLUMN_NEXTSRNO + " TEXT," + COLUMN_RECEIPTPREFIX + " TEXT,"
                + COLUMN_NEXTRECEIPTNO + " TEXT," + COLUMN_GRAPREFIX + " TEXT,"
                + COLUMN_NEXTGRANO + " TEXT," + COLUMN_STOCKREQPREFIX
                + " TEXT," + COLUMN_NEXTSTOCKREQNO + " TEXT,"
                + COLUMN_TRANSFERPREFIX + " TEXT," + COLUMN_NEXTTRANSFERNO
                + " TEXT," + COLUMN_CUSTOMERPREFIX + " TEXT,"
                + COLUMN_NEXTCUSTOMERNO + " TEXT)");

		/* Create fncGetServerDateTime Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETDATETIME_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT,"
                + COLUMN_SERVERDATEANDTIME + " TEXT)");

		/* Create GetCustomer Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETCUSTOMER_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME
                + " TEXT," + COLUMN_CUSTOMERCODE + " TEXT,"
                + COLUMN_CUSTOMERNAME + " TEXT," + COLUMN_CONTACTPERSON
                + " TEXT," + COLUMN_ADDRESS1 + " TEXT," + COLUMN_ADDRESS2
                + " TEXT," + COLUMN_ADDRESS3 + " TEXT," + COLUMN_PHONENO
                + " TEXT," + COLUMN_HANDPHONENO + " TEXT," + COLUMN_EMAIL
                + " TEXT," + COLUMN_FAXNO + " TEXT," + COLUMN_HAVETAX
                + " TEXT," + COLUMN_CUSTOMERGROUPCODE + " TEXT,"
                + COLUMN_TERMCODE + " TEXT," + COLUMN_ISACTIVE + " TEXT,"
                + COLUMN_CREDITLIMIT + " TEXT," + COLUMN_TAXTYPE + " TEXT,"
                + COLUMN_TAXVALUE + " TEXT," + COLUMN_AREACODE + " TEXT,"
                + COLUMN_VANCODE + " TEXT," + COLUMN_OUTSTANDINGAMOUNT + " TEXT,"
                + COLUMN_REFERENCELOCATION + " TEXT,"
                + CREATE_USER + " TEXT,"
                + CREATE_DATE + " TEXT," + MODIFY_USER + " TEXT," + MODIFY_DATE
                + " TEXT,"
                + COLUMN_TAXCODE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

		/* Create GetCurrency Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETCURRENCY_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_CURRENCYCODE
                + " TEXT," + COLUMN_CURRENCYNAME + " TEXT,"
                + COLUMN_CURRENCYRATE + " TEXT)");

        // @jayasri
        /* Create GetArea Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETAREA_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_CODE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT)");

		/* Create GetTerms Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETTERMS_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_CODE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT)");

		/* Create GetCustomerGroup Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETCUSTOMERGROUP_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_CODE + " TEXT," + COLUMN_DESCRIPTION
                + " TEXT)");

		/* Create GetInvoiceHeader Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETINVOICEHEADER_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_INVOICENO + " TEXT," + COLUMN_INVOICEDATE + " DATE,"
                + COLUMN_LOCATIONCODE + " TEXT," + COLUMN_CUSTOMERCODE
                + " TEXT," + COLUMN_CUSTOMERNAME + " TEXT," + COLUMN_TOTAL
                + " TEXT," + COLUMN_ITEMDISCOUNT + " TEXT,"
                + COLUMN_BILLDISCOUNT + " TEXT," + COLUMN_TOTALDISCOUNT
                + " TEXT," + COLUMN_SUBTOTAL + " TEXT," + COLUMN_TAX + " TEXT,"
                + COLUMN_NETTOTAL + " TEXT," + COLUMN_PAIDAMOUNT + " TEXT,"
                + COLUMN_CREDITAMOUNT + " TEXT," + COLUMN_BALANCEAMOUNT
                + " TEXT," + COLUMN_REMARKS + " TEXT," + COLUMN_CURRENCYCODE
                + " TEXT," + COLUMN_CURRENCYRATE + " TEXT," + COLUMN_SONO
                + " TEXT," + COLUMN_DONO + " TEXT," + COLUMN_TOTALBALANCE
                + " TEXT," + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

		/* Create GetInvoiceDetail Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETINVOICEDETAIL_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_INVOICENO + " TEXT," + COLUMN_SLNO + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_PRODUCTNAME + " TEXT,"
                + COLUMN_CQTY + " TEXT," + COLUMN_LQTY + " TEXT," + COLUMN_QTY
                + " TEXT," + COLUMN_FOCQTY + " TEXT," + COLUMN_PCSPERCARTON
                + " TEXT," + COLUMN_RETAILPRICE + " TEXT," + COLUMN_PRICE
                + " TEXT," + COLUMN_TOTAL + " TEXT," + COLUMN_ITEMDISCOUNT
                + " TEXT," + COLUMN_BILLDISCOUNT + " TEXT,"
                + COLUMN_TOTALDISCOUNT + " TEXT," + COLUMN_SUBTOTAL + " TEXT,"
                + COLUMN_TAX + " TEXT," + COLUMN_NETTOTAL + " TEXT,"
                + COLUMN_TAXTYPE + " TEXT," + COLUMN_TAXPERC + " TEXT,"
                + COLUMN_AVERAGECOST + " TEXT," + COLUMN_CATEGORYCODE
                + " TEXT," + COLUMN_SUBCATEGORYCODE + " TEXT,"
                + COLUMN_EXCHANGEQTY + " TEXT," + COLUMN_CARTONPRICE + " TEXT,"
                + COLUMN_UOMCODE + " TEXT,"
                + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT," + MODIFY_USER
                + " TEXT," + MODIFY_DATE + " TEXT," + COLUMN_DOWNLOADSTATUS
                + " TEXT)");

		/* Create GetHeaderByInvoiceNo Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETHEADERBYINVOICENO_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_INVOICENO + " TEXT," + COLUMN_INVOICEDATE + " TEXT,"
                + COLUMN_LOCATIONCODE + " TEXT," + COLUMN_CUSTOMERCODE
                + " TEXT," + COLUMN_TOTAL + " TEXT," + COLUMN_ITEMDISCOUNT
                + " TEXT," + COLUMN_BILLDISCOUNT + " TEXT,"
                + COLUMN_TOTALDISCOUNT + " TEXT," + COLUMN_SUBTOTAL + " TEXT,"
                + COLUMN_TAX + " TEXT," + COLUMN_NETTOTAL + " TEXT,"
                + COLUMN_PAIDAMOUNT + " TEXT," + COLUMN_CREDITAMOUNT + " TEXT,"
                + COLUMN_BALANCEAMOUNT + " TEXT," + COLUMN_REMARKS + " TEXT,"
                + COLUMN_CURRENCYCODE + " TEXT," + COLUMN_CURRENCYRATE
                + " TEXT," + COLUMN_SONO + " TEXT," + COLUMN_DONO + " TEXT,"
                + COLUMN_TOTALBALANCE + " TEXT," + COLUMN_USERID + " TEXT,"
                + COLUMN_DEVICEID + " TEXT)");

		/* Create GetInvoiceCartonDetail Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETINVOICECARTONDETAIL_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_INVOICENO + " TEXT," + COLUMN_SLNO + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_PRODUCTNAME + " TEXT,"
                + COLUMN_SEQNO + " TEXT," + COLUMN_BARCODE + " TEXT,"
                + COLUMN_WEIGHT + " TEXT)");

		/* Create GetCategory Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETCATEGORY_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME
                + " TEXT," + COLUMN_CATEGORYCODE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT)");

		/* Create GetSubCategory Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETSUBCATEGORY_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME
                + " TEXT," + COLUMN_SUBCATEGORYCODE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT)");

		/* Create GetProduct Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETPRODUCT_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME
                + " TEXT," + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_PRODUCTNAME
                + " TEXT," + COLUMN_CATEGORYCODE + " TEXT,"
                + COLUMN_SUBCATEGORYCODE + " TEXT," + COLUMN_SUPPLIERCODE
                + " TEXT," + COLUMN_UOMCODE + " TEXT," + COLUMN_PCSPERCARTON
                + " TEXT," + COLUMN_WEIGHT + " TEXT," + COLUMN_UNITCOST
                + " TEXT," + COLUMN_AVERAGECOST + " TEXT," + COLUMN_RETAILPRICE
                + " TEXT," + COLUMN_WHOLESALEPRICE + " TEXT,"
                + COLUMN_HAVEBATCH + " TEXT," + COLUMN_HAVEEXPIRY + " TEXT,"
                + COLUMN_HAVEMFGDATE + " TEXT," + COLUMN_WEIGHTBARCODEASSIGNED
                + " TEXT," + COLUMN_WEIGHTBARCODESTARTSON + " TEXT,"
                + COLUMN_WEIGHTBARCODEENDSON + " TEXT," + COLUMN_ISACTIVE
                + " TEXT," + COLUMN_NONSTOCKITEM + " TEXT," + COLUMN_TAXPERC
                + " TEXT," + COLUMN_SPECIFICATION + " TEXT,"
                + COLUMN_MINIMUMSELLINGPRICE + " TEXT,"
                + COLUMN_MINIMUMCARTONSELLINGPRICE + " TEXT,"
                + CREATE_USER + " TEXT,"
                + CREATE_DATE + " TEXT," + MODIFY_USER + " TEXT," + MODIFY_DATE
                + " TEXT," + COLUMN_DOWNLOADSTATUS + " TEXT)");

		/* Create GetProductBarcode Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETPRODUCTBARCODE_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_BARCODE + " TEXT)");

		/* Create GetBank Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETBANK_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_BANKCODE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT)");

		/* Create GetPayMode Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETPAYMODE_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_PAYMODECODE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT)");

		/* Create GetInvoiceHeaderOnlyHaveBalance Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + GETINVOICEHEADERONLYHAVEBALANCE_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME
                + " TEXT," + COLUMN_INVOICENO + " TEXT," + COLUMN_INVOICEDATE
                + " DATE," + COLUMN_LOCATIONCODE + " TEXT,"
                + COLUMN_CUSTOMERCODE + " TEXT," + COLUMN_CUSTOMERNAME
                + " TEXT," + COLUMN_TOTAL + " TEXT," + COLUMN_ITEMDISCOUNT
                + " TEXT," + COLUMN_BILLDISCOUNT + " TEXT,"
                + COLUMN_TOTALDISCOUNT + " TEXT," + COLUMN_SUBTOTAL + " TEXT,"
                + COLUMN_TAX + " TEXT," + COLUMN_NETTOTAL + " TEXT,"
                + COLUMN_PAIDAMOUNT + " TEXT," + COLUMN_CREDITAMOUNT + " TEXT,"
                + COLUMN_BALANCEAMOUNT + " TEXT," + COLUMN_REMARKS + " TEXT,"
                + COLUMN_CURRENCYCODE + " TEXT," + COLUMN_CURRENCYRATE
                + " TEXT," + COLUMN_SONO + " TEXT," + COLUMN_DONO + " TEXT,"
                + COLUMN_TOTALBALANCE + " TEXT)");

		/* Create GetProductStock Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETPRODUCTSTOCK_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_LOCATIONCODE + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_NOOFCARTON + " TEXT,"
                + COLUMN_QTY + " TEXT)");

		/* Create GetReceiptHeader Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETRECEIPTEHEADER_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_RECEIPTNO + " TEXT," + COLUMN_RECEIPTDATE + " DATE,"
                + COLUMN_CUSTOMERCODE + " TEXT," + COLUMN_CUSTOMERNAME
                + " TEXT," + COLUMN_PAIDAMOUNT + " TEXT," + COLUMN_CREDITAMOUNT
                + " TEXT," + COLUMN_PAYMODE + " TEXT," + COLUMN_BANKCODE
                + " TEXT," + COLUMN_CHEQUENO + " TEXT," + COLUMN_CHEQUEDATE
                + " DATE," + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

		/* Create GetReceiptDetail Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETRECEIPTDETAIL_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_RECEIPTNO + " TEXT," + COLUMN_INVOICENO + " TEXT,"
                + COLUMN_NETTOTAL + " TEXT," + COLUMN_PAIDAMOUNT + " TEXT,"
                + COLUMN_CREDITAMOUNT + " TEXT," + CREATE_USER + " TEXT,"
                + CREATE_DATE + " TEXT," + MODIFY_USER + " TEXT," + MODIFY_DATE
                + " TEXT," + COLUMN_DOWNLOADSTATUS + " TEXT)");

		/* Create GetCustomerPrice Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETCUSTOMERPRICE_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_CUSTOMERGROUPCODE + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_PCSPERCARTON
                + " TEXT," + COLUMN_RETAILPRICE + " TEXT," + COLUMN_CARTONPRICE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT)");

		/* Create GetMobileSettings Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETMOBILESETTINGS_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SHOWLOGO + " TEXT,"
                + COLUMN_SHOWADDRESS1 + " TEXT," + COLUMN_SHOWADDRESS2 + " TEXT,"
                + COLUMN_SHOWADDRESS3 + " TEXT," + COLUMN_SHOWCOUNTRYPOSTAL + " TEXT,"
                + COLUMN_SHOWPHONE + " TEXT," + COLUMN_SHOWFAX + " TEXT,"
                + COLUMN_SHOWEMAIL + " TEXT," + COLUMN_SHOWTAXREGNO + " TEXT,"
                + COLUMN_SHOWBIZREGNO + " TEXT," + COLUMN_SHOWCUSTOMERCODE + " TEXT,"
                + COLUMN_SHOWCUSTOMERNAME + " TEXT," + COLUMN_CUSTOMERADDRESS1 + " TEXT,"
                + COLUMN_CUSTOMERADDRESS2 + " TEXT," + COLUMN_CUSTOMERADDRESS3 + " TEXT,"
                + COLUMN_SHOWCUSTOMERPHONE + " TEXT," + COLUMN_SHOWCUSTOMERHP + " TEXT,"
                + COLUMN_SHOWCUSTOMEREMAIL + " TEXT," + COLUMN_SHOWCUSTOMERTERMS + " TEXT,"
                + COLUMN_SHOWTOTALOUTSTANDING + " TEXT," + COLUMN_SHOWFOOTER + " TEXT,"
                + COLUMN_SHOWBATCHDETAILS + " TEXT," + COLUMN_SHOWPRODUCTFULLNAME + " TEXT,"
                + COLUMN_COMPANYNAMEALIAS + " TEXT)");

        //Added
		    /* Create GetVan Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETVAN_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_CODE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT)");

		   /* Create GetMobilePrintFooter Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETMOBILEPRINTFOOTER_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_RECEIPTMESSAGE + " TEXT,"
                + COLUMN_SORTORDER + " TEXT)");

		   /* Create GetSOHeader Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETSOHEADER_TABLE
                + " ( " + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_SONO + " TEXT," + COLUMN_SODATE + " DATE,"
                + COLUMN_DELIVERYDATE + " DATE," + COLUMN_LOCATIONCODE + " TEXT,"
                + COLUMN_CUSTOMERCODE + " TEXT," + COLUMN_CUSTOMERNAME + " TEXT,"
                + COLUMN_TOTAL + " TEXT," + COLUMN_ITEMDISCOUNT + " TEXT,"
                + COLUMN_BILLDISCOUNT + " TEXT," + COLUMN_TOTALDISCOUNT + " TEXT,"
                + COLUMN_SUBTOTAL + " TEXT," + COLUMN_TAX + " TEXT,"
                + COLUMN_NETTOTAL + " TEXT," + COLUMN_STATUS + " TEXT,"
                + COLUMN_REMARKS + " TEXT," + COLUMN_CURRENCYCODE + " TEXT,"
                + COLUMN_CURRENCYRATE + " TEXT," + COLUMN_VANCODE + " TEXT,"
                + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

		   /* Create GetProductMainImage Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETPRODUCTMAINIMAGE_TABLE
                + " ( " + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_PRODUCTNAME + " TEXT,"
                + COLUMN_CATEGORYCODE + " TEXT," + COLUMN_SUBCATEGORYCODE + " TEXT,"
                + COLUMN_UOMCODE + " TEXT," + COLUMN_PCSPERCARTON + " TEXT,"
                + COLUMN_WHOLESALEPRICE + " TEXT," + COLUMN_PRODUCT_IMAGE + " TEXT,"
                + COLUMN_SPECIFICATION + " TEXT," + COLUMN_RETAILPRICE + " TEXT,"
                + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

		   /* Create GetProductSubImages Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETPRODUCTSUBIMAGES_TABLE
                + " ( " + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_PRODUCTNAME + " DATE,"
                + COLUMN_LOCATIONCODE + " TEXT,"
                + COLUMN_CATEGORYCODE + " TEXT," + COLUMN_SUBCATEGORYCODE + " TEXT,"
                + COLUMN_UOMCODE + " TEXT," + COLUMN_PCSPERCARTON + " TEXT,"
                + COLUMN_WHOLESALEPRICE + " TEXT," + COLUMN_PRODUCT_IMAGE + " TEXT,"
                + COLUMN_QTY + " TEXT,"
                + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

		   /* Create GetSODetail Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETSODETAIL_TABLE
                + " ( " + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_SONO + " TEXT," + COLUMN_SLNO + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_PRODUCTNAME + " TEXT,"
                + COLUMN_CQTY + " TEXT," + COLUMN_LQTY + " TEXT,"
                + COLUMN_QTY + " TEXT," + COLUMN_FOCQTY + " TEXT,"
                + COLUMN_PCSPERCARTON + " TEXT," + COLUMN_RETAILPRICE + " TEXT,"
                + COLUMN_PRICE + " TEXT," + COLUMN_TOTAL + " TEXT,"
                + COLUMN_ITEMDISCOUNT + " TEXT," + COLUMN_BILLDISCOUNT + " TEXT,"
                + COLUMN_TOTALDISCOUNT + " TEXT," + COLUMN_SUBTOTAL + " TEXT,"
                + COLUMN_TAX + " TEXT," + COLUMN_NETTOTAL + " TEXT,"
                + COLUMN_TAXTYPE + " TEXT," + COLUMN_TAXPERC + " TEXT,"
                + COLUMN_DOQTY + " TEXT," + COLUMN_DOFOCQTY + " TEXT," + COLUMN_DOCQTY + " TEXT,"
                + COLUMN_INVOICEQTY + " TEXT," + COLUMN_INVOICEFOCQTY + " TEXT," + COLUMN_INVOICECQTY + " TEXT,"
                + COLUMN_EXCHANGEQTY + " TEXT,"
                + COLUMN_CARTONPRICE + " TEXT," + COLUMN_ITEMREMARKS + " TEXT,"
                + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

		   /* Create GetInvoiceHeaderWithoutDOSign Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETINVOICEHEADERWITHOUTDOSIGN_TABLE
                + " ( " + COLUMN_TABLE_ID
                + " INTEGER primary key autoincrement, " + COLUMN_COMPANYCODE
                + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_INVOICENO + " TEXT," + COLUMN_INVOICEDATE + " DATE,"
                + COLUMN_LOCATIONCODE + " TEXT," + COLUMN_CUSTOMERCODE
                + " TEXT," + COLUMN_CUSTOMERNAME + " TEXT," + COLUMN_TOTAL
                + " TEXT," + COLUMN_ITEMDISCOUNT + " TEXT,"
                + COLUMN_BILLDISCOUNT + " TEXT," + COLUMN_TOTALDISCOUNT
                + " TEXT," + COLUMN_SUBTOTAL + " TEXT," + COLUMN_TAX + " TEXT,"
                + COLUMN_NETTOTAL + " TEXT," + COLUMN_PAIDAMOUNT + " TEXT,"
                + COLUMN_CREDITAMOUNT + " TEXT," + COLUMN_BALANCEAMOUNT
                + " TEXT," + COLUMN_REMARKS + " TEXT," + COLUMN_CURRENCYCODE
                + " TEXT," + COLUMN_CURRENCYRATE + " TEXT," + COLUMN_SONO
                + " TEXT," + COLUMN_DONO + " TEXT," + COLUMN_TOTALBALANCE
                + " TEXT," + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_POD_SIGN + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

        // Image table added

        db.execSQL("CREATE TABLE IF NOT EXISTS " + IMAGE_TABLE
                + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT,"
                + COLUMN_IMAGE_NO + " TEXT,"
                + COLUMN_IMAGE_TYPE + " TEXT,"
                + COLUMN_SIGNATURE_IMAGE + " TEXT,"
                + COLUMN_PRODUCT_IMAGE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

        //* Create GetProductStockIndex Table *//*
        db.execSQL("CREATE INDEX IF NOT EXISTS " + GETPRODUCTSTOCK_TABLE_INDEX + " ON " + GETPRODUCTSTOCK_TABLE
                + " ( " + COLUMN_COMPANYCODE
                + "," + COLUMN_LOCATIONCODE + ","
                + COLUMN_PRODUCTCODE + " ) ");

        //* Create tblGetProduct_index Table *//*
        db.execSQL("CREATE INDEX IF NOT EXISTS " + GETPRODUCT_TABLE_INDEX + " ON " + GETPRODUCT_TABLE
                + " ( " + COLUMN_COMPANYCODE
                + "," + COLUMN_PRODUCTCODE + " ) ");

        //* Create GETPRODUCTMAINIMAGE_TABLE_INDEX Table *//*
        db.execSQL("CREATE INDEX IF NOT EXISTS " + GETPRODUCTMAINIMAGE_TABLE_INDEX + " ON " + GETPRODUCTMAINIMAGE_TABLE
                + " ( " + COLUMN_COMPANYCODE
                + "," + COLUMN_PRODUCTCODE + "," + COLUMN_CATEGORYCODE + ","
                + COLUMN_SUBCATEGORYCODE + " ) ");

        //* Create GETPRODUCTSUBIMAGES_TABLE_INDEX Table *//*
        db.execSQL("CREATE INDEX IF NOT EXISTS " + GETPRODUCTSUBIMAGES_TABLE_INDEX + " ON " + GETPRODUCTSUBIMAGES_TABLE
                + " ( " + COLUMN_COMPANYCODE
                + "," + COLUMN_PRODUCTCODE + "," + COLUMN_CATEGORYCODE + ","
                + COLUMN_SUBCATEGORYCODE + " ) ");

        /* Create GetCustomer Table INDEX*/
        db.execSQL("CREATE INDEX IF NOT EXISTS " + GETCUSTOMER_TABLE_INDEX  + " ON " + GETCUSTOMER_TABLE
                + " ( " + COLUMN_COMPANYCODE
                + "," +COLUMN_CUSTOMERCODE + "," +COLUMN_CUSTOMERGROUPCODE + " ) ");

        /* Create GetCustomerPrice Table INDEX*/
        db.execSQL("CREATE INDEX IF NOT EXISTS " + GETCUSTOMERPRICE_TABLE_INDEX + " ON " +GETCUSTOMERPRICE_TABLE
                + " ( " + COLUMN_COMPANYCODE
                + "," + COLUMN_CUSTOMERGROUPCODE +  ","+ COLUMN_PRODUCTCODE+","
                 + COLUMN_RETAILPRICE   +","+ COLUMN_CARTONPRICE+ " ) ");

         /* Create GetSRHeader Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETSRHEADER_TABLE
                + " ( " + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_SALESRETURNNO + " TEXT," + COLUMN_SALESRETURNDATE + " DATE,"
                + COLUMN_INVOICENO + " TEXT," + COLUMN_LOCATIONCODE + " TEXT,"
                + COLUMN_CUSTOMERCODE + " TEXT," + COLUMN_CUSTOMERNAME + " TEXT,"
                + COLUMN_TOTAL + " TEXT," + COLUMN_ITEMDISCOUNT + " TEXT,"
                + COLUMN_BILLDISCOUNT + " TEXT," + COLUMN_TOTALDISCOUNT + " TEXT,"
                + COLUMN_SUBTOTAL + " TEXT," + COLUMN_TAX + " TEXT,"
                + COLUMN_NETTOTAL + " TEXT," + COLUMN_PAIDAMOUNT + " TEXT,"
                + COLUMN_CREDITAMOUNT + " TEXT," + COLUMN_BALANCEAMOUNT + " TEXT,"
                + COLUMN_REMARKS + " TEXT," + COLUMN_TOTALBALANCE + " TEXT,"+ COLUMN_VANCODE + " TEXT,"
                + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

         /* Create GetSRDetail Table */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETSRDETAIL_TABLE
                + " ( " + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT," + COLUMN_SERVERDATEANDTIME + " TEXT,"
                + COLUMN_SALESRETURNNO + " TEXT," + COLUMN_SLNO + " TEXT,"
                + COLUMN_PRODUCTCODE + " TEXT," + COLUMN_PRODUCTNAME + " TEXT,"
                + COLUMN_CQTY + " TEXT," + COLUMN_LQTY + " TEXT,"
                + COLUMN_QTY + " TEXT," + COLUMN_FOCQTY + " TEXT,"
                + COLUMN_PCSPERCARTON + " TEXT," + COLUMN_RETAILPRICE + " TEXT,"
                + COLUMN_PRICE + " TEXT," + COLUMN_TOTAL + " TEXT,"
                + COLUMN_ITEMDISCOUNT + " TEXT," + COLUMN_BILLDISCOUNT + " TEXT,"
                + COLUMN_TOTALDISCOUNT + " TEXT," + COLUMN_SUBTOTAL + " TEXT,"
                + COLUMN_TAX + " TEXT," + COLUMN_NETTOTAL + " TEXT,"
                + COLUMN_TAXTYPE + " TEXT," + COLUMN_TAXPERC + " TEXT,"
                + COLUMN_CARTONPRICE + " TEXT," + COLUMN_STOCKADJREFCODE+ " TEXT," + COLUMN_TAXCODE+ " TEXT,"
                + CREATE_USER + " TEXT," + CREATE_DATE + " TEXT,"
                + MODIFY_USER + " TEXT," + MODIFY_DATE + " TEXT,"
                + COLUMN_DOWNLOADSTATUS + " TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + GETTAX_TABLE + " ( "
                + COLUMN_TABLE_ID + " INTEGER primary key autoincrement, "
                + COLUMN_COMPANYCODE + " TEXT,"
                + COLUMN_TAXNAME + " TEXT,"
                + COLUMN_TAXCODE + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DATABASE", "---> onUpgrade");
//		db.execSQL("DROP TABLE IF EXISTS " + GETINVOICEHEADERWITHOUTDOSIGN_TABLE + ";");
//		db.execSQL("DROP TABLE IF EXISTS " + GETCUSTOMER_TABLE + ";");
//		db.execSQL("DROP TABLE IF EXISTS " + GETCUSTOMERGROUP_TABLE + ";");
//		db.execSQL("DROP TABLE IF EXISTS " + USERMASTER_TABLE + ";");
//		db.execSQL("DROP TABLE IF EXISTS " + GETCOMPANY_TABLE + ";");
//		db.execSQL("DROP TABLE IF EXISTS " + GETDATETIME_TABLE + ";");
//		db.execSQL("DROP TABLE IF EXISTS " + GENERALSETTING_TABLE + ";");
//		db.execSQL("DROP TABLE IF EXISTS " + DOWNLOADSTATUS_TABLE + ";");
        //Added
//		   db.execSQL("DROP TABLE IF EXISTS " + GETVAN_TABLE + ";");
//		   db.execSQL("DROP TABLE IF EXISTS " + GETMOBILEPRINTFOOTER_TABLE + ";");
//		   db.execSQL("DROP TABLE IF EXISTS " + GETSOHEADER_TABLE + ";");
//		   db.execSQL("DROP TABLE IF EXISTS " + GETSODETAIL_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + GETTAX_TABLE + ";");
        if (DATABASE_VERSION == 18) {
            db.execSQL("ALTER TABLE " + GETPRODUCT_TABLE + " ADD COLUMN " + COLUMN_MINIMUMCARTONSELLINGPRICE + " TEXT;");
            db.execSQL("ALTER TABLE " + GETCUSTOMER_TABLE + " ADD COLUMN " + COLUMN_TAXCODE + " TEXT;");
        }

        onCreate(db);

    }


    public void dropUserMaster() {
        Log.d("DATABASE", "---> dropUserMaster");
        SQLiteDatabase db = getDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + USERMASTER_TABLE + ";");
        onCreate(db);
    }

    public void dropProductBarCode() {
        Log.d("DATABASE", "---> dropProductBarCode");
        SQLiteDatabase db = getDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + GETPRODUCTBARCODE_TABLE + ";");
        onCreate(db);
    }

    public void dropProductSubImages() {
        Log.d("DATABASE", "---> dropProductSubImages");
        SQLiteDatabase db = getDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + GETPRODUCTSUBIMAGES_TABLE + ";");
        onCreate(db);
    }

    public void dropProductMainImages() {
        Log.d("DATABASE", "---> dropProductMainImages");
        SQLiteDatabase db = getDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + GETPRODUCTMAINIMAGE_TABLE + ";");
        onCreate(db);
    }

    public static void init(Context context) {
        if (null == instance) {
            instance = new OfflineDatabase(context);
        }
        offlinemanager = new OfflineSettingsManager(context);
        comapanyCode = offlinemanager.getCompanyType();

    }

    public static void setcompany(Context context) {
        offlinemanager = new OfflineSettingsManager(context);
        comapanyCode = offlinemanager.getCompanyType();
    }

    public static SQLiteDatabase getDatabase() {
        if (null == database) {
            database = instance.getWritableDatabase();
        }
        return database;
    }

    public static void deactivate() {
        if (null != database && database.isOpen()) {
            database.close();
        }
        database = null;
        instance = null;
    }

    public static int deleteDeviceId() {
        return getDatabase().delete("tblDevice", null, null);
    }

    private void dropTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + USERMASTER_TABLE + ";");
            // db.execSQL("DROP TABLE IF EXISTS " + DEVICEID_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + USERPERMISSION_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GENERALSETTING_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETCOMPANY_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETLOCATION_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETNEXTRUNNINGNUMBER_TABLE
                    + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETCOMPANYLOGO_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETDATETIME_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETCUSTOMER_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETCURRENCY_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETINVOICEHEADER_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETCATEGORY_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETSUBCATEGORY_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETINVOICEDETAIL_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETHEADERBYINVOICENO_TABLE
                    + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETBANK_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETPAYMODE_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS "
                    + GETINVOICEHEADERONLYHAVEBALANCE_TABLE + ";");

            db.execSQL("DROP TABLE IF EXISTS " + GETRECEIPTEHEADER_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETRECEIPTDETAIL_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETPRODUCTSTOCK_TABLE + ";");

            db.execSQL("DROP TABLE IF EXISTS " + GETAREA_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETTERMS_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETCUSTOMERGROUP_TABLE + ";");

            db.execSQL("DROP TABLE IF EXISTS " + GETCUSTOMERPRICE_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETMOBILESETTINGS_TABLE + ";");

            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETINVOICEHEADERWITHOUTDOSIGN_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + GETTAX_TABLE + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void truncateTables() {
        dropTable(getWritableDatabase());
        onCreate(getWritableDatabase());
    }


    /* Store Online Mode Table */
    public static void store_onlinemode(String mode) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SLNO, "1");
        cv.put(COLUMN_ONLINEMODE, mode);

        getDatabase().insert(ONLINEMODE_TABLE, null, cv);
    }

    /* Store Internet Mode Table */
    public static void store_internetmode() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SLNO, "1");
        cv.put(COLUMN_OFFLINE_DIALOG, "false");
        cv.put(COLUMN_ONLINE_DIALOG, "true");

        getDatabase().insert(INTERNET_MODE_TABLE, null, cv);
    }

    /* Store CheckUserNameAndPassword Table */
    public static void store_downloadstatus() {

        String db_id = "";
        String selectQuery = "SELECT _id FROM tblDownloadStatus WHERE _id = '1'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = null;
        cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                db_id = cursor.getString(cursor.getColumnIndex("_id"));
            } while (cursor.moveToNext());
        }

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TABLE_ID, "1");
        cv.put(COLUMN_DOWNLOADSTATUS, "True");

        if (db_id != null && !db_id.isEmpty()) {
            String where = "_id = '1'";
            getDatabase().update(DOWNLOADSTATUS_TABLE, cv, where, null);
        } else {
            ;
            getDatabase().insert(DOWNLOADSTATUS_TABLE, null, cv);
        }

    }

//	public static void update_downloadstatus() {
//		ContentValues cv = new ContentValues();
//		cv.put(COLUMN_DOWNLOADSTATUS, "False");
//		String where = "_id = '1'";
//		getDatabase().update(DOWNLOADSTATUS_TABLE, cv, where, null);
//
//	}

    /* Store DeviceActivation Table */
    public static void store_deviceid(String deviceid) {
        ContentValues cv = new ContentValues();
        // cv.put(COLUMN_ACTIVATIONCODE, "");
        cv.put(COLUMN_DEVICEID, deviceid);

        getDatabase().insert(DEVICEID_TABLE, null, cv);
    }


    /* Store Company Logo Table */
    public static void store_companylogo(String companyCode, String logo) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_COMPANYCODE, companyCode);
        cv.put(COLUMN_COMPANYLOGO, logo);

        getDatabase().insert(GETCOMPANYLOGO_TABLE, null, cv);
    }


    /////////////////////

    /**
     * Start Store/Update Data
     **/

	/* Store UserMaster Table */
    public static void store_usermaster(JSONArray jsonArray) {
        database.beginTransaction();
        try {

            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_USERID, object.getString("UserName"));
                cv.put(COLUMN_PASSWORD, object.getString("Password"));
                cv.put(COLUMN_ISACTIVE, object.getString("IsActive"));
                cv.put(COLUMN_USERGROUPCODE, object.getString("UserGroupCode"));

                cv.put(COLUMN_COMPANYCODE, object.getString("CompanyCode"));
                cv.put(COLUMN_COMPANYNAME, object.getString("CompanyName"));
                cv.put(COLUMN_LASTLOGINLOCATION, object.getString("LastLoginLocation"));
                cv.put(COLUMN_LOCATIONNAME, object.getString("LocationName"));
                getDatabase().insert(USERMASTER_TABLE, null, cv);

            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store GetUserPermission Table */
    public static void store_userpermission(String companycode, String gCode, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String fcode = "", db_code = "";
                fcode = object.getString("FormCode");
                String selectQuery = "SELECT FormCode FROM tblGetUserPermission WHERE FormCode = '"
                        + fcode + "' AND UserGroupCode = '" + gCode + "'" + " AND CompanyCode = '" + companycode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("FormCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companycode);
                cv.put(COLUMN_FORMCODE, object.getString("FormCode"));
                cv.put(COLUMN_FORMNAME, object.getString("FormName"));
                cv.put(COLUMN_USERGROUPCODE, gCode);
                cv.put(COLUMN_RESULT, object.getString("Result"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "FormCode = '" + fcode
                            + "' AND UserGroupCode = '" + gCode + "'" + " AND CompanyCode = '" + companycode + "'";
                    getDatabase().update(USERPERMISSION_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(USERPERMISSION_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store DATETIME Table */
    public static void store_datetime(String datetime, String companycode) {

        String db_id = "";
        String selectQuery = "SELECT CompanyCode FROM tblDatetime WHERE CompanyCode = '" + companycode + "'";
//		Log.d("selectQuery", selectQuery);
        Cursor cursor = null;
        cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                db_id = cursor.getString(cursor.getColumnIndex("CompanyCode"));
            } while (cursor.moveToNext());
        }
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_COMPANYCODE, companycode);
        cv.put(COLUMN_SERVERDATEANDTIME, datetime);

        if (db_id != null && !db_id.isEmpty()) {
            String where = "CompanyCode = '" + companycode + "'";
            getDatabase().update(GETDATETIME_TABLE, cv, where, null);
        } else {
            getDatabase().insert(GETDATETIME_TABLE, null, cv);
        }
        if (cursor != null)
            cursor.close();
    }

    /* Store GetGeneralSettings Table */
    public static void store_generalsetting(String companycode, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String sid = "", db_no = "";
                sid = object.getString("SettingID");
                String selectQuery = "SELECT SettingID FROM tblGetGeneralSettings WHERE CompanyCode = '" + companycode + "' AND SettingID = '" + sid + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_no = cursor.getString(cursor.getColumnIndex("SettingID"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companycode);
                cv.put(COLUMN_SETTINGID, object.getString("SettingID"));
                cv.put(COLUMN_SETTINGVALUE, object.getString("SettingValue"));

                if (db_no != null && !db_no.isEmpty()) {
                    String where = "CompanyCode = '" + companycode + "' AND SettingID = '" + sid + "'";
                    getDatabase().update(GENERALSETTING_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GENERALSETTING_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            getGeneralSettings();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
    }

    /* Store fncGetNextRunningNumber Table */
    public static void store_NextRunningNumber(JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String db_no = "";
                String selectQuery = "SELECT slNo FROM tblNextRunningNumber WHERE slNo = '1'";
                Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_no = cursor.getString(cursor.getColumnIndex("slNo"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_SLNO, "1");
                cv.put(COLUMN_SOPREFIX, object.getString("SOPrefix"));
                cv.put(COLUMN_NEXTSONO, object.getString("NextSoNo"));
                cv.put(COLUMN_DOPREFIX, object.getString("DOPrefix"));
                cv.put(COLUMN_NEXTDO, object.getString("NextDoNo"));
                cv.put(COLUMN_INVOICEPREFIX, object.getString("InvoicePrefix"));
                cv.put(COLUMN_NEXTINVOICENO, object.getString("NextInvoiceNo"));
                cv.put(COLUMN_SRPREFIX, object.getString("SRPrefix"));
                cv.put(COLUMN_NEXTSRNO, object.getString("NextSRNo"));
                cv.put(COLUMN_RECEIPTPREFIX, object.getString("ReceiptPrefix"));
                cv.put(COLUMN_NEXTRECEIPTNO, object.getString("NextReceiptNo"));
                cv.put(COLUMN_GRAPREFIX, object.getString("GRAPrefix"));
                cv.put(COLUMN_NEXTGRANO, object.getString("NextGRANo"));
                cv.put(COLUMN_STOCKREQPREFIX, object.getString("StockReqPrefix"));
                cv.put(COLUMN_NEXTSTOCKREQNO, object.getString("NextStockReqNo"));
                cv.put(COLUMN_TRANSFERPREFIX, object.getString("TransferPrefix"));
                cv.put(COLUMN_NEXTTRANSFERNO, object.getString("NextTransferNo"));
                cv.put(COLUMN_CUSTOMERPREFIX, object.getString("CustomerPrefix"));
                cv.put(COLUMN_NEXTCUSTOMERNO, object.getString("CustomerNextNo"));

                if (db_no != null && !db_no.isEmpty()) {
                    Log.d("GETNEXTRUNNINGNUMBER_TABLE", "Updating");
                    String where = "slNo = '1'";
                    getDatabase().update(GETNEXTRUNNINGNUMBER_TABLE, cv, where, null);
                } else {
                    Log.d("GETNEXTRUNNINGNUMBER_TABLE", "Insert");
                    getDatabase().insert(GETNEXTRUNNINGNUMBER_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store CustomerPrice Table */
    public static void store_customerprice(String datetime, JSONArray jsonArray) {
        //SQLiteDatabase db = null;
        // Begin the transaction


        database.beginTransaction();
        try {
            int len = jsonArray.length();

            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String ccode = "", pcode = "", cgcode = "", db_code = "";
                ccode = object.getString("CompanyCode");
                pcode = object.getString("ProductCode");
                cgcode = object.getString("CustomerGroupCode");
                String selectQuery = "SELECT ProductCode FROM tblGetCustomerPrice WHERE CompanyCode = '" + ccode +
                        "' AND CustomerGroupCode = '" + cgcode + "' AND ProductCode = '" + pcode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("ProductCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, object.getString("CompanyCode"));
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_CUSTOMERGROUPCODE, object.getString("CustomerGroupCode"));
                cv.put(COLUMN_PRODUCTCODE, object.getString("ProductCode"));
                cv.put(COLUMN_PCSPERCARTON, object.getString("PcsPerCarton"));
                cv.put(COLUMN_RETAILPRICE, object.getString("RetailPrice"));
                cv.put(COLUMN_CARTONPRICE, object.getString("CartonPrice"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + ccode + "' AND CustomerGroupCode = '" + cgcode + "' AND ProductCode = '" + pcode + "'";
                    getDatabase().update(GETCUSTOMERPRICE_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETCUSTOMERPRICE_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }


            // Transaction is successful and all the records have been inserted
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store Company Table */
    public static ArrayList<String> store_company(JSONArray jsonArray) {
        ArrayList<String> CompanyCodeArr = new ArrayList<String>();
        CompanyCodeArr.clear();
        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String ccode = "", db_code = "";
                ccode = object.getString("CompanyCode");
                CompanyCodeArr.add(ccode);
                String selectQuery = "SELECT CompanyCode FROM tblGetCompany WHERE CompanyCode = '" + ccode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("CompanyCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, object.getString("CompanyCode"));
                cv.put(COLUMN_COMPANYNAME, object.getString("CompanyName"));
                cv.put(COLUMN_ADDRESS1, object.getString("Address1"));
                cv.put(COLUMN_ADDRESS2, object.getString("Address2"));
                cv.put(COLUMN_ADDRESS3, object.getString("Address3"));
                cv.put(COLUMN_COUNTRY, object.getString("Country"));
                cv.put(COLUMN_ZIPCODE, object.getString("ZipCode"));
                cv.put(COLUMN_PHONENO, object.getString("PhoneNo"));
                cv.put(COLUMN_WEBSITE, object.getString("Website"));
                cv.put(COLUMN_TAXTYPE, object.getString("TaxType"));
                cv.put(COLUMN_TAXVALUE, object.getString("TaxValue"));
                cv.put(COLUMN_TAXREGNO, object.getString("TaxRegNo"));
                cv.put(COLUMN_BUSINESSREGNO, object.getString("BusinessRegNo"));
                cv.put(COLUMN_SHORTCODE, object.getString("ShortCode"));
                cv.put(COLUMN_LOGO, object.getString("Logo"));

                cv.put(COLUMN_FAXNO, object.getString("FaxNo"));//Added
                cv.put(COLUMN_EMAIL, object.getString("Email"));

                cv.put(COLUMN_ANDROIDVERSION_SFA, object.getString("AndroidVersion_SFA"));
                cv.put(COLUMN_ANDROIDVERSION_SFA_CHECKPLAYSTORE, object.getString("AndroidVersion_SFA_CheckPlayStore"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + ccode + "'";
                    getDatabase().update(GETCOMPANY_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETCOMPANY_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
        return CompanyCodeArr;
    }

    /* Store Location Table */
    public static void store_location(String companyCode, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String ccode = "", db_code = "";
                ccode = object.getString("LocationCode");
                String selectQuery = "SELECT LocationCode FROM tblGetLocation WHERE CompanyCode = '" + companyCode + "' AND LocationCode = '" + ccode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("LocationCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_LOCATIONCODE, object.getString("LocationCode"));
                cv.put(COLUMN_LOCATIONNAME, object.getString("LocationName"));
                cv.put(COLUMN_ISMAINLOCATION, object.getString("isMainLocation"));
                cv.put(COLUMN_HAVEBATCHONSTOCKIN, object.getString("HaveBatchOnStockIn"));
                cv.put(COLUMN_HAVEBATCHONSTOCKOUT, object.getString("HaveBatchOnStockOut"));
                cv.put(COLUMN_HAVEBATCHONTRANSFER, object.getString("HaveBatchOnTransfer"));
                cv.put(COLUMN_HAVEBATCHONSTOCKADJUSTMENT, object.getString("HaveBatchOnStockAdjustment"));
                cv.put(COLUMN_NEXTBATCHNO, object.getString("NextBatchNo"));
                cv.put(COLUMN_COMPANYCODE, companyCode);

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND LocationCode = '" + ccode + "'";
                    getDatabase().update(GETLOCATION_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETLOCATION_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store Customer Table */
    public static void store_customer(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String ccode = "", db_code = "";
                ccode = object.getString("CustomerCode");
                String selectQuery = "SELECT CustomerCode FROM tblGetCustomer WHERE CompanyCode = '" + companyCode + "' AND CustomerCode = '" + ccode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("CustomerCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_CUSTOMERCODE, object.getString("CustomerCode"));
                cv.put(COLUMN_CUSTOMERNAME, object.getString("CustomerName"));
                cv.put(COLUMN_CONTACTPERSON, object.getString("ContactPerson"));
                cv.put(COLUMN_ADDRESS1, object.getString("Address1"));
                cv.put(COLUMN_ADDRESS2, object.getString("Address2"));
                cv.put(COLUMN_ADDRESS3, object.getString("Address3"));
                cv.put(COLUMN_PHONENO, object.getString("PhoneNo"));
                cv.put(COLUMN_HANDPHONENO, object.getString("HandphoneNo"));
                cv.put(COLUMN_EMAIL, object.getString("Email"));
                cv.put(COLUMN_FAXNO, object.getString("FaxNo"));
                cv.put(COLUMN_HAVETAX, object.getString("HaveTax"));
                cv.put(COLUMN_CUSTOMERGROUPCODE, object.getString("CustomerGroupCode"));
                cv.put(COLUMN_TERMCODE, object.getString("TermCode"));
                // cv.put(COLUMN_CREDITLIMIT,
                // Double.valueOf(object.getString("creditlimit")));
//				cv.put(COLUMN_ISACTIVE, object.getString("IsActive"));
                cv.put(COLUMN_CREDITLIMIT, object.getString("CreditLimit"));
                cv.put(COLUMN_TAXTYPE, object.getString("TaxType"));
                cv.put(COLUMN_TAXVALUE, object.getString("TaxValue"));
                cv.put(COLUMN_AREACODE, object.getString("AreaCode"));
                cv.put(COLUMN_VANCODE, object.getString("VanCode"));
                cv.put(COLUMN_OUTSTANDINGAMOUNT, object.getString("OutstandingAmount"));
                cv.put(COLUMN_REFERENCELOCATION, object.getString("ReferenceLocation"));

                cv.put(COLUMN_TAXCODE, object.getString("TaxCode")); // newly added jan 10 2017

                cv.put(CREATE_USER, "");
                cv.put(CREATE_DATE, "");
                cv.put(MODIFY_USER, "");
                cv.put(MODIFY_DATE, "");
                cv.put(COLUMN_DOWNLOADSTATUS, "0");

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND CustomerCode = '" + ccode + "'";
                    getDatabase().update(GETCUSTOMER_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETCUSTOMER_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store Currency Table */
    public static void store_currency(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String cgcode = "", db_code = "";
                cgcode = object.getString("CurrencyCode");
                String selectQuery = "SELECT CurrencyCode FROM tblGetCurrency WHERE CompanyCode = '" + companyCode + "' AND CurrencyCode = '" + cgcode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("CurrencyCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_CURRENCYCODE, object.getString("CurrencyCode"));
                cv.put(COLUMN_CURRENCYNAME, object.getString("CurrencyName"));
                cv.put(COLUMN_CURRENCYRATE, object.getString("CurrencyRate"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND CurrencyCode = '" + cgcode + "'";
                    getDatabase().update(GETCURRENCY_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETCURRENCY_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store Area Table */
    public static void store_area(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String cgcode = "", db_code = "";
                cgcode = object.getString("Code");
                String selectQuery = "SELECT Code FROM tblGetArea WHERE CompanyCode = '" + companyCode + "' AND Code = '" + cgcode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("Code"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_CODE, object.getString("Code"));
                cv.put(COLUMN_DESCRIPTION, object.getString("Description"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND Code = '" + cgcode + "'";
                    getDatabase().update(GETAREA_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETAREA_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
    }

    public static void store_tax(String companycode, String datetime, JSONArray jsonArray) {
        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String cgcode = "", db_code = "";
                cgcode = object.getString("TaxCode");
                String selectQuery = "SELECT TaxCode FROM tblGetTax WHERE CompanyCode = '" + companycode + "' AND TaxCode = '" + cgcode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("TaxCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companycode);
                cv.put(COLUMN_TAXCODE, object.getString("TaxCode"));
                cv.put(COLUMN_TAXNAME,object.getString("TaxName"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companycode + "' AND TaxCode = '" + cgcode + "'";
                    getDatabase().update(GETTAX_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETTAX_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store Area Table */
    public static void store_terms(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String cgcode = "", db_code = "";
                cgcode = object.getString("Code");
                String selectQuery = "SELECT Code FROM tblGetTerms WHERE CompanyCode = '" + companyCode + "' AND Code = '" + cgcode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("Code"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_CODE, object.getString("Code"));
                cv.put(COLUMN_DESCRIPTION, object.getString("Description"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND Code = '" + cgcode + "'";
                    getDatabase().update(GETTERMS_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETTERMS_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
    }

    /* Store CustomerGroup Table */
    public static void store_customergroup(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String cgcode = "", db_code = "";
                cgcode = object.getString("Code");
                String selectQuery = "SELECT Code FROM tblGetCustomerGroup WHERE CompanyCode = '" + companyCode + "' AND Code = '" + cgcode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("Code"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_CODE, object.getString("Code"));
                cv.put(COLUMN_DESCRIPTION, object.getString("Description"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND Code = '" + cgcode + "'";
                    getDatabase().update(GETCUSTOMERGROUP_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETCUSTOMERGROUP_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store InvoiceHeader Table */
    public static void store_invoiceheader(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String invno = "", db_code = "";
                invno = object.getString("InvoiceNo");
                String selectQuery = "SELECT InvoiceNo FROM tblGetInvoiceHeader WHERE CompanyCode = '" + companyCode + "' AND InvoiceNo = '" + invno + "'";
                Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("InvoiceNo"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();

                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_INVOICENO, object.getString(("InvoiceNo")));
                String dateStr = object.getString("InvoiceDate");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date myDate = dateFormat.parse(dateStr);
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String finalDate = timeFormat.format(myDate);

                cv.put(COLUMN_INVOICEDATE, finalDate);
                cv.put(COLUMN_LOCATIONCODE, object.getString(("LocationCode")));
                cv.put(COLUMN_CUSTOMERCODE, object.getString(("CustomerCode")));
                cv.put(COLUMN_CUSTOMERNAME, object.getString(("CustomerName")));
                cv.put(COLUMN_TOTAL, object.getString(("Total")));
                cv.put(COLUMN_ITEMDISCOUNT, object.getString(("ItemDiscount")));
                cv.put(COLUMN_BILLDISCOUNT, object.getString(("BillDIscount")));
                cv.put(COLUMN_TOTALDISCOUNT, object.getString(("TotalDiscount")));
                cv.put(COLUMN_SUBTOTAL, object.getString(("SubTotal")));
                cv.put(COLUMN_TAX, object.getString(("Tax")));
                cv.put(COLUMN_NETTOTAL, object.getString(("NetTotal")));
                cv.put(COLUMN_PAIDAMOUNT, object.getString(("PaidAmount")));
                cv.put(COLUMN_CREDITAMOUNT, object.getString(("CreditAmount")));
                cv.put(COLUMN_BALANCEAMOUNT, object.getString(("BalanceAmount")));
                cv.put(COLUMN_REMARKS, object.getString(("Remarks")));
                cv.put(COLUMN_CURRENCYCODE, object.getString(("CurrencyCode")));
                cv.put(COLUMN_CURRENCYRATE, object.getString(("CurrencyRate")));
                cv.put(COLUMN_SONO, object.getString(("SoNo")));
                cv.put(COLUMN_DONO, object.getString(("DoNo")));
                cv.put(COLUMN_TOTALBALANCE, object.getString(("TotalBalance")));
                // cv.put(COLUMN_USERID, object.getString(("UserName")));
                // cv.put(COLUMN_DEVICEID, object.getString(("DeviceId")));

                cv.put(CREATE_USER, "");
                cv.put(CREATE_DATE, "");
                cv.put(MODIFY_USER, "");
                cv.put(MODIFY_DATE, "");
                cv.put(COLUMN_DOWNLOADSTATUS, "0");

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND InvoiceNo = '" + invno + "'";
                    getDatabase().update(GETINVOICEHEADER_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETINVOICEHEADER_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();

            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }


    }

    /* Store Category Table */
    public static void store_category(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String ccode = "", db_code = "";
                ccode = object.getString("CategoryCode");
                String selectQuery = "SELECT CategoryCode FROM tblGetCategory WHERE CompanyCode = '" + companyCode + "' AND CategoryCode = '" + ccode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("CategoryCode"));
                    } while (cursor.moveToNext());
                }
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_CATEGORYCODE, object.getString("CategoryCode"));
                cv.put(COLUMN_DESCRIPTION, object.getString("Description"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND CategoryCode = '" + ccode + "'";
                    getDatabase().update(GETCATEGORY_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETCATEGORY_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store SubCategory Table */
    public static void store_subcategory(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String sccode = "", db_code = "";
                sccode = object.getString("SubCategoryCode");
                String selectQuery = "SELECT SubCategoryCode FROM tblGetSubCategory WHERE CompanyCode = '" + companyCode + "' AND SubCategoryCode = '" + sccode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("SubCategoryCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_SUBCATEGORYCODE, object.getString("SubCategoryCode"));
                cv.put(COLUMN_DESCRIPTION, object.getString("Description"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND SubCategoryCode = '" + sccode + "'";
                    getDatabase().update(GETSUBCATEGORY_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETSUBCATEGORY_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store Product Table */
    public static void store_product(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                String pcode = "", db_code = "";
                pcode = object.getString("ProductCode");
                String selectQuery = "SELECT ProductCode FROM tblGetProduct WHERE CompanyCode = '"
                        + companyCode + "' AND ProductCode = '" + pcode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("ProductCode"));
                    } while (cursor.moveToNext());
                }
                ContentValues cv = new ContentValues();

                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_PRODUCTCODE, object.getString("ProductCode").trim());
                cv.put(COLUMN_PRODUCTNAME, object.getString("ProductName"));
                cv.put(COLUMN_CATEGORYCODE, object.getString("CategoryCode"));
                cv.put(COLUMN_SUBCATEGORYCODE, object.getString("SubCategoryCode"));
                cv.put(COLUMN_SUPPLIERCODE, object.getString("SupplierCode"));
                cv.put(COLUMN_UOMCODE, object.getString("UOMCode"));
                cv.put(COLUMN_PCSPERCARTON, object.getString("PcsPerCarton"));
                cv.put(COLUMN_WEIGHT, object.getString("Weight"));
                cv.put(COLUMN_UNITCOST, object.getString("UnitCost"));
                cv.put(COLUMN_AVERAGECOST, object.getString("AverageCost"));
                cv.put(COLUMN_RETAILPRICE, object.getString("RetailPrice"));
                cv.put(COLUMN_WHOLESALEPRICE, object.getString("WholeSalePrice"));
                cv.put(COLUMN_HAVEBATCH, object.getString("HaveBatch"));
                cv.put(COLUMN_HAVEEXPIRY, object.getString("HaveExpiry"));
                cv.put(COLUMN_HAVEMFGDATE, object.getString("HaveMfgDate"));
                cv.put(COLUMN_WEIGHTBARCODEASSIGNED, object.getString("WeightBarcodeAssigned"));
                cv.put(COLUMN_WEIGHTBARCODESTARTSON, object.getString("WeightBarcodeStartsOn"));
                cv.put(COLUMN_WEIGHTBARCODEENDSON, object.getString("WeightBarcodeEndsOn"));
                cv.put(COLUMN_ISACTIVE, object.getString("IsActive"));
                cv.put(COLUMN_NONSTOCKITEM, object.getString("NonStockItem"));
                cv.put(COLUMN_TAXPERC, object.getString("TaxPerc"));
                cv.put(COLUMN_SPECIFICATION, object.getString("Specification"));
                cv.put(COLUMN_MINIMUMSELLINGPRICE, object.getString("MinimumSellingPrice"));
                cv.put(COLUMN_MINIMUMCARTONSELLINGPRICE, object.getString("MinimumCartonSellingPrice"));
                cv.put(CREATE_USER, "");
                cv.put(CREATE_DATE, "");
                cv.put(MODIFY_USER, "");
                cv.put(MODIFY_DATE, "");
                cv.put(COLUMN_DOWNLOADSTATUS, "0");

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND ProductCode = '" + pcode + "'";
                    getDatabase().update(GETPRODUCT_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETPRODUCT_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store ProductBarCode Table */
    public static void store_productbarcode(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_PRODUCTCODE, object.getString("ProductCode"));
                cv.put(COLUMN_BARCODE, object.getString("Barcode").trim());
                getDatabase().insert(GETPRODUCTBARCODE_TABLE, null, cv);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {
            //End the transaction
            database.endTransaction();
        }

    }

    /* Store Bank Table */
    public static void store_bank(String companyCode, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String code = "", db_code = "";
                code = object.getString("Code");
                String selectQuery = "SELECT BankCode FROM tblBank WHERE CompanyCode = '" + companyCode + "' AND BankCode = '" + code + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("BankCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_BANKCODE, object.getString("Code"));
                cv.put(COLUMN_DESCRIPTION, object.getString("Description"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND BankCode = '" + code + "'";
                    getDatabase().update(GETBANK_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETBANK_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
    }

    /* Store PayMode Table */
    public static void store_paymode(String companyCode, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String code = "", db_code = "";
                code = object.getString("Code");
                String selectQuery = "SELECT PayModeCode FROM tblPayMode WHERE CompanyCode = '" + companyCode + "' AND PayModeCode = '" + code + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("PayModeCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_PAYMODECODE, object.getString("Code"));
                cv.put(COLUMN_DESCRIPTION, object.getString("Description"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND PayModeCode = '" + code + "'";
                    getDatabase().update(GETPAYMODE_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETPAYMODE_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
    }

    /* Store ProductStock Table */
    public static void store_productstock(String companyCode, JSONArray jsonArray) {
        //SQLiteDatabase db = null;
        // Begin the transaction
        database.beginTransaction();

        try {
            int len = jsonArray.length();

            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String lcode = "", pcode = "", db_code = "";
                lcode = object.getString("LocationCode");
                pcode = object.getString("ProductCode");

                String selectQuery = "SELECT ProductCode FROM tblGetProductStock WHERE CompanyCode = '" + companyCode +
                        "' AND LocationCode = '" + lcode + "' AND ProductCode = '" + pcode + "'";
//				Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("ProductCode"));
                    } while (cursor.moveToNext());
                }
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_LOCATIONCODE, object.getString("LocationCode"));
                cv.put(COLUMN_PRODUCTCODE, object.getString("ProductCode"));
                cv.put(COLUMN_NOOFCARTON, object.getString("NoOfCarton"));
                cv.put(COLUMN_QTY, object.getString("Qty"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND LocationCode = '" + lcode + "' AND ProductCode = '" + pcode + "'";
                    getDatabase().update(GETPRODUCTSTOCK_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETPRODUCTSTOCK_TABLE, null, cv);
                }

                if (cursor != null)
                    cursor.close();
            }

            // Transaction is successful and all the records have been inserted
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }

    /* Store mobile settings Table */
    public static void store_mobilesettings(String companyCode, JSONArray jsonArray) {
        //SQLiteDatabase db = null;
        // Begin the transaction
        database.beginTransaction();

        try {
            int len = jsonArray.length();
            Log.d("mobile settings length", "" + len);

            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String db_code = "";
                String selectQuery = "SELECT CompanyCode FROM tblGetMobileSettings WHERE CompanyCode = '" + companyCode + "'";
//	     Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("CompanyCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SHOWLOGO, object.getString("ShowLogo"));
                cv.put(COLUMN_SHOWADDRESS1, object.getString("ShowAddress1"));
                cv.put(COLUMN_SHOWADDRESS2, object.getString("ShowAddress2"));
                cv.put(COLUMN_SHOWADDRESS3, object.getString("ShowAddress3"));
                cv.put(COLUMN_SHOWCOUNTRYPOSTAL, object.getString("ShowCountryPostal"));
                cv.put(COLUMN_SHOWPHONE, object.getString("ShowPhone"));
                cv.put(COLUMN_SHOWFAX, object.getString("ShowFax"));
                cv.put(COLUMN_SHOWEMAIL, object.getString("ShowEmail"));
                cv.put(COLUMN_SHOWTAXREGNO, object.getString("ShowTaxRegNo"));
                cv.put(COLUMN_SHOWBIZREGNO, object.getString("ShowBizRegNo"));
                cv.put(COLUMN_SHOWCUSTOMERCODE, object.getString("ShowCustomerCode"));
                cv.put(COLUMN_SHOWCUSTOMERNAME, object.getString("ShowCustomerName"));
                cv.put(COLUMN_CUSTOMERADDRESS1, object.getString("ShowCustomerAddress1"));
                cv.put(COLUMN_CUSTOMERADDRESS2, object.getString("ShowCustomerAddress2"));
                cv.put(COLUMN_CUSTOMERADDRESS3, object.getString("ShowCustomerAddress3"));
                cv.put(COLUMN_SHOWCUSTOMERPHONE, object.getString("ShowCustomerPhone"));
                cv.put(COLUMN_SHOWCUSTOMERHP, object.getString("ShowCustomerHP"));
                cv.put(COLUMN_SHOWCUSTOMEREMAIL, object.getString("ShowCustomerEmail"));
                cv.put(COLUMN_SHOWCUSTOMERTERMS, object.getString("ShowCustomerTerms"));
                cv.put(COLUMN_SHOWTOTALOUTSTANDING, object.getString("ShowTotalOutstanding"));
                cv.put(COLUMN_SHOWFOOTER, object.getString("ShowFooter"));
                cv.put(COLUMN_SHOWBATCHDETAILS, object.getString("ShowBatchDetails"));
                cv.put(COLUMN_SHOWPRODUCTFULLNAME, object.getString("ShowProductFullName"));
                cv.put(COLUMN_COMPANYNAMEALIAS,object.getString("CompanyNameAlias"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "'";
                    getDatabase().update(GETMOBILESETTINGS_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETMOBILESETTINGS_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();

            }
//	    setMobileSettings(companyCode);


            // Transaction is successful and all the records have been inserted
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();


        }

    }

    /* Store InvoiceDetail Table */
    public static void store_invoicedetail(
            ArrayList<HashMap<String, String>> invoiceDetails) {
        Log.d("InvoiceDetails Insert", "Inserting...");
        // Log.d("InvoiceDetails ", "..."+invoiceDetails.toString());
        for (int i = 0; i < invoiceDetails.size(); i++) {
            HashMap<String, String> detailValue = invoiceDetails.get(i);

            // updateProductStock(detailValue.get("ProductCode"),
            // detailValue.get(("Qty")), detailValue.get("FOCQty"),
            // detailValue.get("ExchangeQty"));

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_COMPANYCODE, detailValue.get(("CompanyCode")));
            cv.put(COLUMN_SERVERDATEANDTIME, detailValue.get(("DateTime")));
            cv.put(COLUMN_INVOICENO, detailValue.get(("InvoiceNo")));
            cv.put(COLUMN_SLNO, detailValue.get(("slNo")));
            cv.put(COLUMN_PRODUCTCODE, detailValue.get(("ProductCode")));
            cv.put(COLUMN_PRODUCTNAME, detailValue.get(("ProductName")));
            cv.put(COLUMN_CQTY, detailValue.get(("CQty")));
            cv.put(COLUMN_LQTY, detailValue.get(("Lqty")));
            cv.put(COLUMN_QTY, detailValue.get(("Qty")));
            cv.put(COLUMN_FOCQTY, detailValue.get(("FOCQty")));
            cv.put(COLUMN_PCSPERCARTON, detailValue.get(("PcsPerCarton")));
            cv.put(COLUMN_RETAILPRICE, detailValue.get(("RetailPrice")));
            cv.put(COLUMN_PRICE, detailValue.get(("Price")));
            cv.put(COLUMN_TOTAL, detailValue.get(("Total")));
            cv.put(COLUMN_ITEMDISCOUNT, detailValue.get(("ItemDiscount")));
            cv.put(COLUMN_BILLDISCOUNT, detailValue.get(("BillDiscount")));
            cv.put(COLUMN_TOTALDISCOUNT, detailValue.get(("TotalDiscount")));
            cv.put(COLUMN_SUBTOTAL, detailValue.get(("SubTotal")));
            cv.put(COLUMN_TAX, detailValue.get(("Tax")));
            cv.put(COLUMN_NETTOTAL, detailValue.get(("NetTotal")));
            cv.put(COLUMN_TAXTYPE, detailValue.get(("TaxType")));
            cv.put(COLUMN_TAXPERC, detailValue.get(("TaxPerc")));
            cv.put(COLUMN_AVERAGECOST, detailValue.get(("AverageCost")));
            cv.put(COLUMN_CATEGORYCODE, detailValue.get(("CategoryCode")));
            cv.put(COLUMN_SUBCATEGORYCODE, detailValue.get(("SubCategoryCode")));
            cv.put(COLUMN_EXCHANGEQTY, detailValue.get(("ExchangeQty")));
            cv.put(COLUMN_CARTONPRICE, detailValue.get(("CartonPrice")));

            cv.put(CREATE_USER, detailValue.get("CreateUser"));
            cv.put(CREATE_DATE, detailValue.get("CreateDate"));
            cv.put(MODIFY_USER, detailValue.get("ModifyUser"));
            cv.put(MODIFY_DATE, detailValue.get("ModifyDate"));
            cv.put(COLUMN_DOWNLOADSTATUS, detailValue.get("DownloadStatus"));

            getDatabase().insert(GETINVOICEDETAIL_TABLE, null, cv);
        }
    }

    /* Store ReceiptHeader Table */
    public static void store_receiptheader(HashMap<String, String> HeaderValue) {
        // Log.d("ReceiptHearder Insert", "Inserting...");
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_COMPANYCODE, HeaderValue.get("CompanyCode"));
        cv.put(COLUMN_SERVERDATEANDTIME, HeaderValue.get(("DateTime")));
        cv.put(COLUMN_RECEIPTNO, HeaderValue.get(("ReceiptNo")));
        cv.put(COLUMN_RECEIPTDATE, HeaderValue.get(("ReceiptDate")));
        cv.put(COLUMN_CUSTOMERCODE, HeaderValue.get(("CustomerCode")));
        cv.put(COLUMN_CUSTOMERNAME, HeaderValue.get(("CustomerName")));
        cv.put(COLUMN_PAIDAMOUNT, HeaderValue.get(("PaidAmount")));
        cv.put(COLUMN_CREDITAMOUNT, HeaderValue.get(("CreditAmount")));
        cv.put(COLUMN_PAYMODE, HeaderValue.get(("Paymode")));
        cv.put(COLUMN_BANKCODE, HeaderValue.get(("BankCode")));
        cv.put(COLUMN_CHEQUENO, HeaderValue.get(("ChequeNo")));
        cv.put(COLUMN_CHEQUEDATE, HeaderValue.get(("ChequeDate")));

        cv.put(CREATE_USER, HeaderValue.get("CreateUser"));
        cv.put(CREATE_DATE, HeaderValue.get("CreateDate"));
        cv.put(MODIFY_USER, HeaderValue.get("ModifyUser"));
        cv.put(MODIFY_DATE, HeaderValue.get("ModifyDate"));
        cv.put(COLUMN_DOWNLOADSTATUS, HeaderValue.get("DownloadStatus"));

        getDatabase().insert(GETRECEIPTEHEADER_TABLE, null, cv);
    }

    /* Store ReceiptDetail Table */
    public static void store_receiptdetail(
            ArrayList<HashMap<String, String>> invoiceDetails) {
        Log.d("ReceiptDetails Insert", "Inserting...");
        for (int i = 0; i < invoiceDetails.size(); i++) {
            HashMap<String, String> detailValue = invoiceDetails.get(i);

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_COMPANYCODE, detailValue.get(("CompanyCode")));
            cv.put(COLUMN_SERVERDATEANDTIME, detailValue.get(("DateTime")));
            cv.put(COLUMN_RECEIPTNO, detailValue.get(("ReceiptNo")));
            cv.put(COLUMN_INVOICENO, detailValue.get(("InvoiceNo")));
            cv.put(COLUMN_NETTOTAL, detailValue.get(("NetTotal")));
            cv.put(COLUMN_PAIDAMOUNT, detailValue.get(("PaidAmount")));
            cv.put(COLUMN_CREDITAMOUNT, detailValue.get(("CreditAmount")));

            cv.put(CREATE_USER, detailValue.get("CreateUser"));
            cv.put(CREATE_DATE, detailValue.get("CreateDate"));
            cv.put(MODIFY_USER, detailValue.get("ModifyUser"));
            cv.put(MODIFY_DATE, detailValue.get("ModifyDate"));
            cv.put(COLUMN_DOWNLOADSTATUS, detailValue.get("DownloadStatus"));

            getDatabase().insert(GETRECEIPTDETAIL_TABLE, null, cv);
        }
    }

    //Added
	 /* Store Van Table */
    public static void store_van(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String code = "", db_code = "";
                code = object.getString("Code");
                String selectQuery = "SELECT Code FROM tblGetVan WHERE CompanyCode = '" + companyCode + "' AND Code = '" + code + "'";
                Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("Code"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_CODE, object.getString("Code"));
                cv.put(COLUMN_DESCRIPTION, object.getString("Description"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND Code = '" + code + "'";
                    getDatabase().update(GETVAN_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETVAN_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
    }

    /* Store MobilePrintFooter Table */
    public static void store_mobileprinterfooter(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String code = "", db_code = "";
                code = object.getString("SortOrder");
                String selectQuery = "SELECT SortOrder FROM tblGetMobilePrintFooter WHERE CompanyCode = '" + companyCode + "' AND SortOrder = '" + code + "'";
                Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("SortOrder"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_RECEIPTMESSAGE, object.getString("ReceiptMessage"));
                cv.put(COLUMN_SORTORDER, object.getString("SortOrder"));

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND SortOrder = '" + code + "'";
                    getDatabase().update(GETMOBILEPRINTFOOTER_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETMOBILEPRINTFOOTER_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
    }

    /* Store SOHeader Table */
    public static void store_soheader(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String sono = "", db_code = "";
                sono = object.getString("SoNo");
                String selectQuery = "SELECT SoNo FROM tblGetSOHeader WHERE CompanyCode = '" + companyCode + "' AND SoNo = '" + sono + "'";
                Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("SoNo"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();

                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_SONO, object.getString(("SoNo")));
                String soDateStr = object.getString("SoDate");
                String doDateStr = object.getString("DeliveryDate");

                Log.d("SoDate", "--" + soDateStr);
//	    Log.d("DeliveryDate", "--"+doDateStr);

                SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String so_finalDate = "", do_finalDate = "";

                if (soDateStr != null && !soDateStr.isEmpty()) {
                    Date soDate = oldFormat.parse(soDateStr);
                    so_finalDate = newFormat.format(soDate);
                }

                if (doDateStr != null && !doDateStr.isEmpty()) {
                    Date doDate = oldFormat.parse(doDateStr);
                    do_finalDate = newFormat.format(doDate);
                }

                cv.put(COLUMN_SODATE, so_finalDate);
                cv.put(COLUMN_DELIVERYDATE, do_finalDate);
                cv.put(COLUMN_LOCATIONCODE, object.getString(("LocationCode")));
                cv.put(COLUMN_CUSTOMERCODE, object.getString(("CustomerCode")));
                cv.put(COLUMN_CUSTOMERNAME, object.getString(("CustomerName")));
                cv.put(COLUMN_TOTAL, object.getString(("Total")));
                cv.put(COLUMN_ITEMDISCOUNT, object.getString(("ItemDiscount")));
                cv.put(COLUMN_BILLDISCOUNT, object.getString(("BillDIscount")));
                cv.put(COLUMN_TOTALDISCOUNT, object.getString(("TotalDiscount")));
                cv.put(COLUMN_SUBTOTAL, object.getString(("SubTotal")));
                cv.put(COLUMN_TAX, object.getString(("Tax")));
                cv.put(COLUMN_NETTOTAL, object.getString(("NetTotal")));
                cv.put(COLUMN_REMARKS, object.getString(("Remarks")));
                cv.put(COLUMN_STATUS, object.getString(("Status")));
                cv.put(COLUMN_CURRENCYCODE, object.getString(("CurrencyCode")));
                cv.put(COLUMN_CURRENCYRATE, object.getString(("CurrencyRate")));
                cv.put(COLUMN_VANCODE, object.getString(("VanCode")));
                // cv.put(COLUMN_USERID, object.getString(("UserName")));
                // cv.put(COLUMN_DEVICEID, object.getString(("DeviceId")));

                cv.put(CREATE_USER, "");
                cv.put(CREATE_DATE, "");
                cv.put(MODIFY_USER, "");
                cv.put(MODIFY_DATE, "");
                cv.put(COLUMN_DOWNLOADSTATUS, "0");

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND SoNo = '" + sono + "'";
                    getDatabase().update(GETSOHEADER_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETSOHEADER_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();

            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }


    }

    /* Store InvoiceHeaderWithoutDOSig Table */
    public static void store_invoiceheaderwithoutDOsign(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String invno = "", db_code = "";
                invno = object.getString("InvoiceNo");
                String selectQuery = "SELECT InvoiceNo FROM tblGetInvoiceHeaderWithoutDOSign WHERE CompanyCode = '" + companyCode + "' AND InvoiceNo = '" + invno + "'";
//					Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("InvoiceNo"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();

                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_INVOICENO, object.getString(("InvoiceNo")));
                String dateStr = object.getString("InvoiceDate");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date myDate = dateFormat.parse(dateStr);
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String finalDate = timeFormat.format(myDate);

                cv.put(COLUMN_INVOICEDATE, finalDate);
                cv.put(COLUMN_LOCATIONCODE, object.getString(("LocationCode")));
                cv.put(COLUMN_CUSTOMERCODE, object.getString(("CustomerCode")));
                cv.put(COLUMN_CUSTOMERNAME, object.getString(("CustomerName")));
                cv.put(COLUMN_TOTAL, object.getString(("Total")));
                cv.put(COLUMN_ITEMDISCOUNT, object.getString(("ItemDiscount")));
                cv.put(COLUMN_BILLDISCOUNT, object.getString(("BillDIscount")));
                cv.put(COLUMN_TOTALDISCOUNT, object.getString(("TotalDiscount")));
                cv.put(COLUMN_SUBTOTAL, object.getString(("SubTotal")));
                cv.put(COLUMN_TAX, object.getString(("Tax")));
                cv.put(COLUMN_NETTOTAL, object.getString(("NetTotal")));
                cv.put(COLUMN_PAIDAMOUNT, object.getString(("PaidAmount")));
                cv.put(COLUMN_CREDITAMOUNT, object.getString(("CreditAmount")));
                cv.put(COLUMN_BALANCEAMOUNT, object.getString(("BalanceAmount")));
                cv.put(COLUMN_REMARKS, object.getString(("Remarks")));
                cv.put(COLUMN_CURRENCYCODE, object.getString(("CurrencyCode")));
                cv.put(COLUMN_CURRENCYRATE, object.getString(("CurrencyRate")));
                cv.put(COLUMN_SONO, object.getString(("SoNo")));
                cv.put(COLUMN_DONO, object.getString(("DoNo")));
                cv.put(COLUMN_TOTALBALANCE, object.getString(("TotalBalance")));
                // cv.put(COLUMN_USERID, object.getString(("UserName")));
                // cv.put(COLUMN_DEVICEID, object.getString(("DeviceId")));
                cv.put(COLUMN_POD_SIGN, "0");
                cv.put(CREATE_USER, "");
                cv.put(CREATE_DATE, "");
                cv.put(MODIFY_USER, "");
                cv.put(MODIFY_DATE, "");
                cv.put(COLUMN_DOWNLOADSTATUS, "0");

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND InvoiceNo = '" + invno + "'";
                    getDatabase().update(GETINVOICEHEADERWITHOUTDOSIGN_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETINVOICEHEADERWITHOUTDOSIGN_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();

            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }


    }
    /// Added  ///

    /* Store SOHeader Table */
    public static void store_productmainimage(String companyCode, String datetime, NodeList nl) {

        database.beginTransaction();
        try {
            int len = nl.getLength();
            for (int i = 0; i < len; i++) {
                org.w3c.dom.Element e = (org.w3c.dom.Element) nl.item(i);
                String productcode = "", db_code = "";
                productcode = XMLParser.getValue(e, COLUMN_PRODUCTCODE);
                String selectQuery = "SELECT ProductCode FROM tblGetProductMainImage WHERE CompanyCode = '" + companyCode + "' AND ProductCode = '" + productcode + "'";
                Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("ProductCode"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();

                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_PRODUCTCODE, XMLParser.getValue(e, COLUMN_PRODUCTCODE));
                cv.put(COLUMN_PRODUCT_IMAGE, XMLParser.getValue(e, COLUMN_PRODUCT_IMAGE));
//		    cv.put(COLUMN_PRODUCTNAME, XMLParser.getValue(e, COLUMN_PRODUCTNAME));
//		    cv.put(COLUMN_CATEGORYCODE, XMLParser.getValue(e, COLUMN_CATEGORYCODE));
//		    cv.put(COLUMN_SUBCATEGORYCODE, XMLParser.getValue(e, COLUMN_SUBCATEGORYCODE));
//		    cv.put(COLUMN_UOMCODE, XMLParser.getValue(e, COLUMN_UOMCODE));
//		    cv.put(COLUMN_PCSPERCARTON, XMLParser.getValue(e, COLUMN_PCSPERCARTON));
//		    cv.put(COLUMN_WHOLESALEPRICE, XMLParser.getValue(e, COLUMN_WHOLESALEPRICE));
//		    cv.put(COLUMN_SPECIFICATION, XMLParser.getValue(e, COLUMN_SPECIFICATION));
//		    cv.put(COLUMN_RETAILPRICE, XMLParser.getValue(e, COLUMN_RETAILPRICE));

                cv.put(CREATE_USER, "");
                cv.put(CREATE_DATE, "");
                cv.put(MODIFY_USER, "");
                cv.put(MODIFY_DATE, "");
                cv.put(COLUMN_DOWNLOADSTATUS, "0");

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND ProductCode = '" + productcode + "'";
                    Log.d("GETPRODUCTMAINIMAGE", productcode + " - UPDATEd");
                    getDatabase().update(GETPRODUCTMAINIMAGE_TABLE, cv, where, null);
                } else {
                    Log.d("GETPRODUCTMAINIMAGE", productcode + " - INSERT");
                    getDatabase().insert(GETPRODUCTMAINIMAGE_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();

            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }


    }

    /* Store ProductSubImages Table */
    public static void store_productsubimages(String companyCode, String datetime, NodeList nl) {
        //SQLiteDatabase db = null;
        // Begin the transaction
        database.beginTransaction();

        try {
            int len = nl.getLength();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    org.w3c.dom.Element e = (org.w3c.dom.Element) nl.item(i);
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_COMPANYCODE, companyCode);
                    cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                    cv.put(COLUMN_PRODUCTCODE, XMLParser.getValue(e, COLUMN_PRODUCTCODE));
//		    cv.put(COLUMN_PRODUCTNAME, XMLParser.getValue(e, COLUMN_PRODUCTNAME));
//		    cv.put(COLUMN_LOCATIONCODE, XMLParser.getValue(e, COLUMN_LOCATIONCODE));
//		    cv.put(COLUMN_CATEGORYCODE, XMLParser.getValue(e, COLUMN_CATEGORYCODE));
//		    cv.put(COLUMN_SUBCATEGORYCODE, XMLParser.getValue(e, COLUMN_SUBCATEGORYCODE));
//		    cv.put(COLUMN_UOMCODE, XMLParser.getValue(e, COLUMN_UOMCODE));
//		    cv.put(COLUMN_PCSPERCARTON, XMLParser.getValue(e, COLUMN_PCSPERCARTON));
//		    cv.put(COLUMN_WHOLESALEPRICE, XMLParser.getValue(e, COLUMN_WHOLESALEPRICE));
                    cv.put(COLUMN_PRODUCT_IMAGE, XMLParser.getValue(e, COLUMN_PRODUCT_IMAGE));
//		    cv.put(COLUMN_QTY, XMLParser.getValue(e, COLUMN_QTY));

                    cv.put(CREATE_USER, "");
                    cv.put(CREATE_DATE, "");
                    cv.put(MODIFY_USER, "");
                    cv.put(MODIFY_DATE, "");
                    cv.put(COLUMN_DOWNLOADSTATUS, "0");

                    getDatabase().insert(GETPRODUCTSUBIMAGES_TABLE, null, cv);

                }
                // Transaction is successful and all the records have been inserted
                database.setTransactionSuccessful();
            }


        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }

    }


//	/* Store InvoiceCartonDetail Table */
//	public static void store_invoicecartondetail(
//			HashMap<String, String> productvalues) {
//		ContentValues cv = new ContentValues();
//
//		cv.put(COLUMN_COMPANYCODE, productvalues.get("companycode"));
//		cv.put(COLUMN_SERVERDATEANDTIME, productvalues.get("datetime"));
//		cv.put(COLUMN_INVOICENO, productvalues.get("invoiceno"));
//		// cv.put(COLUMN_INVOICEDATE, productvalues.get("InvoiceDate"));
//		// cv.put(COLUMN_LOCATIONCODE, productvalues.get("LocationCode"));
//		cv.put(COLUMN_SLNO, productvalues.get("slno"));
//
//		cv.put(COLUMN_PRODUCTCODE, productvalues.get("productcode"));
//		cv.put(COLUMN_PRODUCTNAME, productvalues.get("productname"));
//		cv.put(COLUMN_SEQNO, productvalues.get("seqno"));
//
//		cv.put(COLUMN_BARCODE, productvalues.get("weightbarcode"));
//
//		cv.put(COLUMN_WEIGHT, productvalues.get("weight"));
//
//		getDatabase().insert(GETINVOICECARTONDETAIL_TABLE, null, cv);
//	}


    /**
     * End Store/Update Data
     **/
    ///////////////////
    public static void setMobileSettings(String cCode) {

        String selectQuery = "SELECT * FROM tblGetMobileSettings where CompanyCode ='" + cCode + "'";

        Log.d("selectQuery", selectQuery);

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MobileSettingsSetterGetter.setShowLogo(cursor.getString(cursor.getColumnIndex("ShowLogo")));
                MobileSettingsSetterGetter.setShowAddress1(cursor.getString(cursor.getColumnIndex("ShowAddress1")));
                MobileSettingsSetterGetter.setShowAddress2(cursor.getString(cursor.getColumnIndex("ShowAddress2")));
                MobileSettingsSetterGetter.setShowAddress3(cursor.getString(cursor.getColumnIndex("ShowAddress3")));
                MobileSettingsSetterGetter.setShowCountryPostal(cursor.getString(cursor.getColumnIndex("ShowCountryPostal")));
                MobileSettingsSetterGetter.setShowPhone(cursor.getString(cursor.getColumnIndex("ShowPhone")));
                MobileSettingsSetterGetter.setShowFax(cursor.getString(cursor.getColumnIndex("ShowFax")));
                MobileSettingsSetterGetter.setShowEmail(cursor.getString(cursor.getColumnIndex("ShowEmail")));
                MobileSettingsSetterGetter.setShowTaxRegNo(cursor.getString(cursor.getColumnIndex("ShowTaxRegNo")));
                MobileSettingsSetterGetter.setShowBizRegNo(cursor.getString(cursor.getColumnIndex("ShowBizRegNo")));
                MobileSettingsSetterGetter.setShowCustomerCode(cursor.getString(cursor.getColumnIndex("ShowCustomerCode")));
                MobileSettingsSetterGetter.setShowCustomerName(cursor.getString(cursor.getColumnIndex("ShowCustomerName")));
                MobileSettingsSetterGetter.setShowCustomerAddress1(cursor.getString(cursor.getColumnIndex("ShowCustomerAddress1")));
                MobileSettingsSetterGetter.setShowCustomerAddress2(cursor.getString(cursor.getColumnIndex("ShowCustomerAddress2")));
                MobileSettingsSetterGetter.setShowCustomerAddress3(cursor.getString(cursor.getColumnIndex("ShowCustomerAddress3")));
                MobileSettingsSetterGetter.setShowCustomerPhone(cursor.getString(cursor.getColumnIndex("ShowCustomerPhone")));
                MobileSettingsSetterGetter.setShowCustomerHP(cursor.getString(cursor.getColumnIndex("ShowCustomerHP")));
                MobileSettingsSetterGetter.setShowCustomerEmail(cursor.getString(cursor.getColumnIndex("ShowCustomerEmail")));
                MobileSettingsSetterGetter.setShowCustomerTerms(cursor.getString(cursor.getColumnIndex("ShowCustomerTerms")));
                MobileSettingsSetterGetter.setShowProductFullName(cursor.getString(cursor.getColumnIndex("ShowProductFullName")));
                MobileSettingsSetterGetter.setShowTotalOutstanding(cursor.getString(cursor.getColumnIndex("ShowTotalOutstanding")));
                MobileSettingsSetterGetter.setShowFooter(cursor.getString(cursor.getColumnIndex("ShowFooter")));
                MobileSettingsSetterGetter.setShowBatchDetails(cursor.getString(cursor.getColumnIndex("ShowBatchDetails")));
                MobileSettingsSetterGetter.setCompanyNameAlias(cursor.getString(cursor.getColumnIndex("CompanyNameAlias")));
                MobileSettingsSetterGetter.setDecimalPoints("3");

                Log.d("ShowTaxRegNo","-->"+cursor.getString(cursor.getColumnIndex("ShowTaxRegNo")));

            } while (cursor.moveToNext());
        }
    }

    public static Cursor getOnlineModeCount() {
        String[] columns = new String[]{COLUMN_TABLE_ID, COLUMN_ONLINEMODE};
        Cursor cursor = getDatabase().query(ONLINEMODE_TABLE, columns, null,
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getInternetModeCount() {
        String[] columns = new String[]{COLUMN_TABLE_ID,
                COLUMN_OFFLINE_DIALOG, COLUMN_ONLINE_DIALOG};
        Cursor cursor = getDatabase().query(INTERNET_MODE_TABLE, columns, null,
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getDownloadStatusCount() {
        String[] columns = new String[]{COLUMN_TABLE_ID,
                COLUMN_DOWNLOADSTATUS};
        Cursor cursor = getDatabase().query(DOWNLOADSTATUS_TABLE, columns,
                null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getUsermasterCount() {
        String selectQuery = "SELECT * FROM tblUserMaster";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getCompanyCount() {
        String selectQuery = "SELECT * FROM tblGetCompany";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getDatetimeCount() {
        String selectQuery = "SELECT * FROM tblDatetime";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getGeneralSettingsCount() {
        String selectQuery = "SELECT * FROM tblGetGeneralSettings";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getMobileSettingsCursor() {
        String selectQuery = "SELECT * FROM tblGetMobileSettings";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getProductMainImage() {
        String selectQuery = "SELECT * FROM tblGetProductMainImage";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static Cursor getProductSubImage() {
        String selectQuery = "SELECT * FROM tblGetProductSubImages";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static String getOnlineMode() {
        String onlinemode = "";
        String selectQuery = "SELECT * FROM tblOnlineMode";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                onlinemode = cursor.getString(cursor
                        .getColumnIndex("OnlineMode"));

            } while (cursor.moveToNext());
        }
        return onlinemode;
    }

    public static String getInternetMode(String columnName) {
        String dialogMode = "";
        String selectQuery = "SELECT * FROM tblInternetMode  WHERE slNo='1'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                dialogMode = cursor
                        .getString(cursor.getColumnIndex(columnName));

            } while (cursor.moveToNext());
        }
        return dialogMode;
    }

    public static int updateOnlineMode(String mode) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ONLINEMODE, mode);
        return getDatabase().update(ONLINEMODE_TABLE, cv,
                "slNo=" + "'" + "1" + "'", null);
    }

    public static int updateInternetMode(String columnName, String mode) {
        ContentValues cv = new ContentValues();
        cv.put(columnName, mode);
        return getDatabase().update(INTERNET_MODE_TABLE, cv, "slNo =  '1'",
                null);
    }

    /* Get CompanyName */
    public static ArrayList<HashMap<String, String>> getCompanyName() {

        ArrayList<HashMap<String, String>> cmpnyArr = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT CompanyCode,CompanyName FROM tblGetCompany";
        Cursor cursor = null;
        cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> hm = new HashMap<String, String>();

                String companycode = cursor.getString(cursor
                        .getColumnIndex("CompanyCode"));
                String companyname = cursor.getString(cursor
                        .getColumnIndex("CompanyName"));

                hm.put("CompanyCode", companycode);
                hm.put("CompanyName", companyname);

                cmpnyArr.add(hm);

            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return cmpnyArr;
    }

    /* Get Company */
    public static ArrayList<String> getCompanyDetail() {


            ArrayList<String> cmpnyArr = new ArrayList<String>();
            cmpnyArr.clear();
        try {
            String selectQuery = "SELECT * FROM tblGetCompany where CompanyCode = '" + comapanyCode + "'";
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    String TaxType = cursor.getString(cursor
                            .getColumnIndex("TaxType"));
                    String TaxValue = cursor.getString(cursor
                            .getColumnIndex("TaxValue"));
                    String CompanyName = cursor.getString(cursor
                            .getColumnIndex("CompanyName"));
                    String CompanyAddress1 = cursor.getString(cursor
                            .getColumnIndex("Address1"));
                    String CompanyAddress2 = cursor.getString(cursor
                            .getColumnIndex("Address2"));
                    String CompanyAddress3 = cursor.getString(cursor
                            .getColumnIndex("Address3"));
                    String CompanyCountry = cursor.getString(cursor
                            .getColumnIndex("Country"));
                    String CompanyZipCode = cursor.getString(cursor
                            .getColumnIndex("ZipCode"));
                    String CompanyPhoneno = cursor.getString(cursor
                            .getColumnIndex("PhoneNo"));

                    String fax = cursor.getString(cursor
                            .getColumnIndex("FaxNo"));
                    String email = cursor.getString(cursor
                            .getColumnIndex("Email"));
                    String TaxRegNo = cursor.getString(cursor
                            .getColumnIndex("TaxRegNo"));
                    String BusinessRegNo = cursor.getString(cursor
                            .getColumnIndex("BusinessRegNo"));
                    String AndroidVersion_SFA = cursor.getString(cursor
                            .getColumnIndex("AndroidVersion_SFA"));
                    String AndroidVersion_SFA_CheckPlayStore = cursor.getString(cursor
                            .getColumnIndex("AndroidVersion_SFA_CheckPlayStore"));

                    cmpnyArr.add(TaxType);
                    cmpnyArr.add(TaxValue);
                    cmpnyArr.add(CompanyName);
                    cmpnyArr.add(CompanyAddress1);
                    cmpnyArr.add(CompanyAddress2);
                    cmpnyArr.add(CompanyAddress3);
                    cmpnyArr.add(CompanyCountry);
                    cmpnyArr.add(CompanyZipCode);
                    cmpnyArr.add(CompanyPhoneno);

                    cmpnyArr.add(fax);
                    cmpnyArr.add(email);
                    cmpnyArr.add(TaxRegNo);
                    cmpnyArr.add(BusinessRegNo);
                    cmpnyArr.add(AndroidVersion_SFA);
                    cmpnyArr.add(AndroidVersion_SFA_CheckPlayStore);

                } while (cursor.moveToNext());
            }

            Log.d("cmpnyArr db", cmpnyArr.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return cmpnyArr;
    }

	/* Get LocationName */

    public static HashMap<String, String> getLocation(String spinnerCompanyText) {

        HashMap<String, String> locationArr = new HashMap<String, String>();
        String selectQuery = "SELECT LocationCode,LocationName FROM tblGetLocation where CompanyCode = "
                + "'" + spinnerCompanyText + "'";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String LocationCode = cursor.getString(cursor
                        .getColumnIndex("LocationCode"));
                String LocationName = cursor.getString(cursor
                        .getColumnIndex("LocationName"));

                locationArr.put(LocationName, LocationCode);

            } while (cursor.moveToNext());
        }

        Log.d("locationArr", locationArr.toString());

        return locationArr;
    }

    /* Get GeneralSettings */
    public static String getGeneralSettings() {
        String SettingID, SettingValue, gnrlStngs = "";
        String selectQuery = "SELECT * FROM tblGetGeneralSettings WHERE CompanyCode = '" + comapanyCode + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SettingID = cursor.getString(cursor.getColumnIndex("SettingID"));

                if (SettingID.matches("APPTYPE")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    if (SettingValue.matches("S")) {
                        gnrlStngs = SettingValue;
                        Log.d("result ", gnrlStngs);
                    } else if (SettingValue.matches("W")) {
                        gnrlStngs = SettingValue;
                        Log.d("result ", gnrlStngs);
                    }
                }

                if (SettingID.matches("CARTONPRICE")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setCartonpriceflag("1");
                        Log.d("CartonPriceFlag",
                                SalesOrderSetGet.getCartonpriceflag());
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setCartonpriceflag("0");
                    }
                }

                if (SettingID.matches("MOBILELOGINPAGE")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    Log.d("SettingValue mobile login", SettingValue);

                    if (SettingValue.matches("M")) {

                        SalesOrderSetGet.setMobileloginpage("M");

                    } else if (SettingValue.matches("S")) {
                        SalesOrderSetGet.setMobileloginpage("S");
                    } else {
                        SalesOrderSetGet.setMobileloginpage("S");
                    }
                }

                if (SettingID.matches("ENABLECUSTOMERCODE")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    Log.d("ENABLECUSTOMERCODE", SettingValue);

                    if (SettingValue.matches("0")) {

                        SalesOrderSetGet.setEnablecustomercode("0");

                    } else if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setEnablecustomercode("1");
                    } else {
                        SalesOrderSetGet.setEnablecustomercode("1");
                    }
                }

                if (SettingID.matches("TRANSFERCHANGEFROMLOC")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    Log.d("TRANSFERCHANGEFROMLOC", SettingValue);

                    if (SettingValue.matches("1")) {

                        SalesOrderSetGet.setTransferchangefromloc(SettingValue);

                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setTransferchangefromloc(SettingValue);
                    } else {
                        SalesOrderSetGet.setTransferchangefromloc("");
                    }
                }

                /*if (SettingID.matches("DEFAUTSHOWCARTONORLOOSE")) {
//				 if (SettingID.matches("CARTONPRICE")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    if (SettingValue.matches("C")) {
                        SalesOrderSetGet.setDefaultshowcartonorloose("C");
                    } else if (SettingValue.matches("L")) {
                        SalesOrderSetGet.setDefaultshowcartonorloose("L");
                    } else {
                        SalesOrderSetGet.setDefaultshowcartonorloose("");
                    }
                }*/


                if (SettingID.matches("CALCCARTON")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    if (SettingValue.matches("0")) {

                        LogOutSetGet.setCalcCarton("0");
                    } else if (SettingValue.matches("1")) {

                        LogOutSetGet.setCalcCarton("1");
                    }
                }

                if (SettingID.matches("RECEIPTONINVOICE")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setReceiptoninvoice("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setReceiptoninvoice("0");
                    }

                }

                if (SettingID.matches("MOBILEPRODUCTSTOCKPRINT")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setMobileproductstockprint("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setMobileproductstockprint("0");
                    }
                }

                if (SettingID.matches("AUTOBATCHNO")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setAutoBatchNo("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setAutoBatchNo("0");
                    } else {
                        SalesOrderSetGet.setAutoBatchNo("0");
                    }
                }

                if (SettingID.matches("MOBILEPRINTINVOICEDETAIL")) {
                    SettingValue = cursor.getString(cursor
                            .getColumnIndex("SettingValue"));

                    if (SettingValue.matches("1")) {

                        SalesOrderSetGet.setInvoiceprintdetail("1");

                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setInvoiceprintdetail("0");
                    } else {
                        SalesOrderSetGet.setInvoiceprintdetail("0");
                    }
                }

                if (SettingID.matches("HAVEMERCHANDISING")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));

                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setHaveMerchandising("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setHaveMerchandising("0");
                    } else {
                        SalesOrderSetGet.setHaveMerchandising("0");
                    }
                }

                if (SettingID.matches("HAVEMULTIPLECUSTOMERPRICE")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setHaveMultipleCustomerPrice("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setHaveMultipleCustomerPrice("0");
                        //      SalesOrderSetGet.setSchedulingType("DO");
                    } else {
                        SalesOrderSetGet.setHaveMultipleCustomerPrice("0");
                    }

                }



                if (SettingID.matches("LOCALCURRENCY")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    SalesOrderSetGet.setLocalCurrency(SettingValue);
                }

                if (SettingID.matches("TRAN_BLOCK_TERMS")) {
                    SettingValue =cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setTranBlockTerms("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setTranBlockTerms("0");
                    } else {
                        SalesOrderSetGet.setTranBlockTerms("0");
                    }
                }

                if (SettingID.matches("TRAN_BLOCK_CREDITLIMIT")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setTranBlockCreditLimit("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setTranBlockCreditLimit("0");
                    } else {
                        SalesOrderSetGet.setTranBlockCreditLimit("0");
                    }
                }

                if (SettingID.matches("MALAYSIASHOWGST")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setMalaysiaShowGST("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setMalaysiaShowGST("0");
                    } else {
                        SalesOrderSetGet.setMalaysiaShowGST("0");
                    }
                }

                if (SettingID.matches("APPPRINTGROUP")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("C")) {
                        SalesOrderSetGet.setAppPrintGroup("C");
                    } else if (SettingValue.matches("S")) {
                        SalesOrderSetGet.setAppPrintGroup("S");
                    } else if (SettingValue.matches("N")) {
                        SalesOrderSetGet.setAppPrintGroup("N");
                    } else {
                        SalesOrderSetGet.setAppPrintGroup(SettingValue);
                    }
                }

                if (SettingID.matches("HAVEEMAILINTEGRATION")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setHaveemailintegration("1");
                    } else if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setHaveemailintegration("0");
                    } else {
                        SalesOrderSetGet.setHaveemailintegration("0");
                    }
                }

                if (SettingID.matches("HAVEATTRIBUTE")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("2")) {
                        SalesOrderSetGet.setHaveAttribute("2");
                    } else if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setHaveAttribute("1");
                    } else {
                        SalesOrderSetGet.setHaveAttribute("1");
                    }
                }

                if (SettingID.matches("SHOW_UNITCOST_STOCKTAKE")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setShowUnitCostStockTake("0");
                    } else if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setShowUnitCostStockTake("1");
                    } else {
                        SalesOrderSetGet.setShowUnitCostStockTake("0");
                    }
                }
                if (SettingID.matches("SELFORDERSHOWADDTOCART")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setShowAddToCart("0");
                    } else if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setShowAddToCart("1");
                    } else {
                        SalesOrderSetGet.setShowAddToCart("0");
                    }
                }
                if (SettingID.matches("MOBILE_SHOW_CODE_ONSEARCH")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setMobileShowCodeOnSearch("0");
                    } else if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setMobileShowCodeOnSearch("1");
                    } else {
                        SalesOrderSetGet.setMobileShowCodeOnSearch("0");
                    }
                }
                if (SettingID.matches("SELFORDER_SHOW_PRODUCTCODE")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setSelfOrderShowProductCode("0");
                    } else if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setSelfOrderShowProductCode("1");
                    } else {
                        SalesOrderSetGet.setSelfOrderShowProductCode("0");
                    }
                }

                if (SettingID.matches("MOBILE_HAVE_OFFLINEMODE")) {
                    SettingValue =cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setMobileHaveOfflineMode("0");
                    } else if (SettingValue.matches("1")) {
                        SalesOrderSetGet.setMobileHaveOfflineMode("1");
                    } else {
                        SalesOrderSetGet.setMobileHaveOfflineMode("0");
                    }
//				SalesOrderSetGet.setMobileHaveOfflineMode("0");
                }

                if (SettingID.matches("RECEIPT_AUTO_CREDIT_AMOUNT")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setReceiptAutoCreditAmount("0");
                    } else {
                        SalesOrderSetGet.setReceiptAutoCreditAmount(SettingValue);
                    }
                }

                if (SettingID.matches("HOSTING_VALIDATION")) {
                    SettingValue = cursor.getString(cursor.getColumnIndex("SettingValue"));
                    if (SettingValue.matches("0")) {
                        SalesOrderSetGet.setHostingValidation("0");
                    } else {
                        SalesOrderSetGet.setHostingValidation(SettingValue);
                    }
                }

            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        return gnrlStngs;
    }

//	public static String getGnrlSettings() {
//		String SettingID, settingValue, gnrlStngs = "";
//		String selectQuery = "SELECT * FROM tblGetGeneralSettings";
//		Cursor cursor = null;
//		cursor = getDatabase().rawQuery(selectQuery, null);
//		if (cursor.moveToFirst()) {
//			do {
//				SettingID = cursor
//						.getString(cursor.getColumnIndex("SettingID"));
//
//				if (SettingID.matches("TRANSFERCHANGEFROMLOC")) {
//					settingValue = cursor.getString(cursor
//							.getColumnIndex("SettingValue"));
//
//					if (settingValue.matches("1")) {
//						gnrlStngs = settingValue;
//						Log.d("result ", gnrlStngs);
//					} else if (settingValue.matches("0")) {
//						gnrlStngs = settingValue;
//						Log.d("result ", gnrlStngs);
//					} else {
//						gnrlStngs = "";
//						Log.d("result ", gnrlStngs);
//					}
//
//					SalesOrderSetGet.setTransferchangefromloc(gnrlStngs);
//				}
//			} while (cursor.moveToNext());
//		}
//
//
//
//		 if(cursor != null)
//		    cursor.close();
////		Log.d("gnrlStngs", gnrlStngs.toString());
//
//		return gnrlStngs;
//	}

    /* Get UserPermission */
    public static ArrayList<String> getUserPermission(String companycode, String userGroup) {
        ArrayList<String> userGroupcodeArr = new ArrayList<String>();
        userGroupcodeArr.clear();
        String selectQuery = "SELECT * FROM tblGetUserPermission where UserGroupCode = "
                + "'" + userGroup + "'" + " AND CompanyCode = '" + companycode + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String formName = cursor.getString(cursor
                        .getColumnIndex("FormName"));

                String result = cursor.getString(cursor
                        .getColumnIndex("Result"));
                if (result.matches("True")) {
                    userGroupcodeArr.add(formName);
                }

            } while (cursor.moveToNext());
        }

        Log.d("userGroupcodeArr", userGroupcodeArr.toString());

        return userGroupcodeArr;
    }

    /* Get tblUserMaster */
    public static ArrayList<String> getUserMaster(String userId, String pwd, String companycode) {

        ArrayList<String> loginArr = new ArrayList<String>();
        String mobileLoginPage = SalesOrderSetGet.getMobileloginpage();
        Log.d("mobileLoginPage", "..." + mobileLoginPage);
        String selectQuery = "SELECT * FROM tblUserMaster where UserName = '" + userId + "' And Password= '" + pwd + "' And CompanyCode = '" + companycode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                // String Result =
                // cursor.getString(cursor.getColumnIndex("Result"));
                String userGroup = cursor.getString(cursor
                        .getColumnIndex("UserGroupCode"));
                String isActive = cursor.getString(cursor
                        .getColumnIndex("IsActive"));
                String comapnycode = cursor.getString(cursor
                        .getColumnIndex("CompanyCode"));
                String comapnyname = cursor.getString(cursor
                        .getColumnIndex("CompanyName"));
                String lastloginlocation = cursor.getString(cursor
                        .getColumnIndex("LastLoginLocation"));
                String locationname = cursor.getString(cursor
                        .getColumnIndex("LocationName"));

                loginArr.add("True");
                loginArr.add(userGroup);

                if (isActive.matches("True")) {
                    loginArr.add(isActive);
                } else {
                    loginArr.add("0");
                }

                if (mobileLoginPage.matches("M")) {
                    Log.d("db lastloginlocation Alert -->", ".." + lastloginlocation);
                    loginArr.add(comapnycode);
                    loginArr.add(comapnyname);
                    loginArr.add(lastloginlocation);
                    loginArr.add(locationname);
                }

            } while (cursor.moveToNext());
        } else {
            loginArr.add("False");
            loginArr.add("");
            loginArr.add("0");
            if (mobileLoginPage.matches("M")) {
                loginArr.add("");
                loginArr.add("");
                loginArr.add("");
                loginArr.add("");
            }
        }

        Log.d("loginArr", loginArr.toString());

        return loginArr;
    }

    /* Get tblUserMaster */
    public static ArrayList<String> getUserMasterForMobileLogin(String userId, String pwd) {

        ArrayList<String> loginArr = new ArrayList<String>();
        String mobileLoginPage = SalesOrderSetGet.getMobileloginpage();
        Log.d("mobileLoginPage", "..." + mobileLoginPage);
        String selectQuery = "SELECT * FROM tblUserMaster where UserName = '" + userId + "' And Password= '" + pwd + "' And CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                // String Result =
                // cursor.getString(cursor.getColumnIndex("Result"));
                String userGroup = cursor.getString(cursor
                        .getColumnIndex("UserGroupCode"));
                String isActive = cursor.getString(cursor
                        .getColumnIndex("IsActive"));
                String comapnycode = cursor.getString(cursor
                        .getColumnIndex("CompanyCode"));
                String comapnyname = cursor.getString(cursor
                        .getColumnIndex("CompanyName"));
                String lastloginlocation = cursor.getString(cursor
                        .getColumnIndex("LastLoginLocation"));
                String locationname = cursor.getString(cursor
                        .getColumnIndex("LocationName"));

                loginArr.add("True");
                loginArr.add(userGroup);

                if (isActive.matches("True")) {
                    loginArr.add(isActive);
                } else {
                    loginArr.add("0");
                }

                if (mobileLoginPage.matches("M")) {
                    Log.d("db lastloginlocation Alert -->", ".." + lastloginlocation);
                    loginArr.add(comapnycode);
                    loginArr.add(comapnyname);
                    loginArr.add(lastloginlocation);
                    loginArr.add(locationname);
                }

            } while (cursor.moveToNext());
        } else {
            loginArr.add("False");
            loginArr.add("");
            loginArr.add("0");
            if (mobileLoginPage.matches("M")) {
                loginArr.add("");
                loginArr.add("");
                loginArr.add("");
                loginArr.add("");
            }
        }

        Log.d("loginArr", loginArr.toString());

        return loginArr;
    }

    /* Get Serverdate */
    public static String getServerDate() {
        String datetime = "";
        String selectQuery = "SELECT * FROM tblDatetime WHERE CompanyCode = '" + comapanyCode + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                datetime = cursor
                        .getString(cursor.getColumnIndex("ServerDate"));

            } while (cursor.moveToNext());
        }
        return datetime;
    }

    /* Get DeviceId */
    public static String getDeviceId() {
        String deviceid = "";
        String selectQuery = "SELECT * FROM tblDevice";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                deviceid = cursor.getString(cursor.getColumnIndex("DeviceId"));

            } while (cursor.moveToNext());
        }
        return deviceid;
    }

    /* Get CompanyCode */
    public static ArrayList<String> getCompanyCode() {
        ArrayList<String> cmpnyArr = new ArrayList<String>();
        String selectQuery = "SELECT CompanyCode FROM tblGetCompany";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String companycode = cursor.getString(cursor.getColumnIndex("CompanyCode"));
                cmpnyArr.add(companycode);
            } while (cursor.moveToNext());
        }

        Log.d("Company Code", "" + cmpnyArr.toString());

        return cmpnyArr;
    }

    /* Get CompanyCode */
    public static ArrayList<String> getUserGroupCode() {
        ArrayList<String> usergrpcodeArr = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT UserGroupCode FROM tblUserMaster";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String usergrpcode = cursor.getString(cursor.getColumnIndex("UserGroupCode"));
                usergrpcodeArr.add(usergrpcode);
            } while (cursor.moveToNext());
        }
        Log.d("User Group Code", "" + usergrpcodeArr.size());
        return usergrpcodeArr;
    }

    public static ArrayList<SO> getRetriveInvoiceHeader(
            HashMap<String, String> hm) {
        double totalamount = 0.00;
        ArrayList<SO> invoiceheaderlist = new ArrayList<SO>();
        String selectCustomerCode = "", selectFromDate = "", selectToDate = "", selectBalanceFlag = "";
        selectCustomerCode = hm.get("CustomerCode");
        selectFromDate = hm.get("FromDate");
        selectToDate = hm.get("ToDate");
        selectBalanceFlag = hm.get("BalanceFlag");

        try {

            String selectQuery = "SELECT * FROM tblGetInvoiceHeader WHERE CompanyCode= '"
                    + comapanyCode + "'";// order by InvoiceDate

            if (selectCustomerCode != null && !selectCustomerCode.isEmpty()) {
                selectQuery += " AND CustomerCode = '" + selectCustomerCode
                        + "'";
            }

            if ((selectFromDate != null && !selectFromDate.isEmpty())
                    && (selectToDate != null && !selectToDate.isEmpty())) {
                selectQuery += " AND InvoiceDate BETWEEN '" + selectFromDate
                        + "' AND '" + selectToDate + "'";
            }

            if (selectBalanceFlag.matches("0")) {
                selectQuery += " AND BalanceAmount > '0.00'";
            } else if (selectBalanceFlag.matches("1")) {
                selectQuery += " AND BalanceAmount <= '0.00'";
            }

            selectQuery += " order by InvoiceDate DESC";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    String ccSno = cursor.getString(cursor
                            .getColumnIndex(COLUMN_INVOICENO));
                    String ccDate = cursor.getString(cursor
                            .getColumnIndex(COLUMN_INVOICEDATE));

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    Date myDate = dateFormat.parse(ccDate);
                    SimpleDateFormat timeFormat = new SimpleDateFormat(
                            "dd/MM/yyyy");
                    String finalDate = timeFormat.format(myDate);

                    String customerCode = cursor.getString(cursor
                            .getColumnIndex(COLUMN_CUSTOMERCODE));
                    String customerName = cursor.getString(cursor
                            .getColumnIndex(COLUMN_CUSTOMERNAME));
                    String amount = cursor.getString(cursor
                            .getColumnIndex(COLUMN_NETTOTAL));
                    String balanceamount = cursor.getString(cursor
                            .getColumnIndex(COLUMN_BALANCEAMOUNT));

                    String nettot = cursor.getString(cursor
                            .getColumnIndex(COLUMN_NETTOTAL));

                    double net = 0;
                    if (nettot != null && !nettot.isEmpty()) {
                        net = Double.parseDouble(nettot);
                    }


                    totalamount = totalamount + Double.valueOf(net);

//					totalamount = totalamount
//							+ Double.valueOf(cursor.getString(cursor
//									.getColumnIndex(COLUMN_NETTOTAL)));

                    SO so = new SO();
                    so.setSno(ccSno);
                    so.setDate(finalDate);
                    so.setCustomerCode(customerCode);
                    so.setCustomerName(customerName);
                    so.setNettotal(amount);
                    so.setBalanceamount(balanceamount);

                    invoiceheaderlist.add(so);
                } while (cursor.moveToNext());
            }
            SO.setTotalamount(totalamount);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return invoiceheaderlist;

    }

	/* Get Customer CursorTable */
//	public ArrayList<HashMap<String, String>> getCustomer() {
//		ArrayList<HashMap<String, String>> customerArrhm = new ArrayList<HashMap<String, String>>();
//		String selectQuery = "SELECT CustomerCode,CustomerName FROM tblGetCustomer WHERE CompanyCode = '"
//				+ comapanyCode + "'";
//		Log.d("selectQuery", selectQuery);
//		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
//		if (cursor.moveToFirst()) {
//			do {
//
//				String customercode = cursor.getString(cursor
//						.getColumnIndex(COLUMN_CUSTOMERCODE));
//				String customername = cursor.getString(cursor
//						.getColumnIndex(COLUMN_CUSTOMERNAME));
//
//				HashMap<String, String> customerhm = new HashMap<String, String>();
//				customerhm.put(customercode, customername);
//				customerArrhm.add(customerhm);
//
//			} while (cursor.moveToNext());
//		}
//		return customerArrhm;
//
//	}

    /* Get Currency CursorTable */
    public ArrayList<HashMap<String, String>> getCurrency() {
        ArrayList<HashMap<String, String>> crrncyArrhm = new ArrayList<HashMap<String, String>>();
        crrncyArrhm.clear();
        Log.d("crrncyArrhm-ff->", "ff" + crrncyArrhm.toString());
        String selectQuery = "SELECT CurrencyCode,CurrencyName FROM tblGetCurrency WHERE CompanyCode = '"
                + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String currrencycode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CURRENCYCODE));
                String currencyname = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CURRENCYNAME));
                HashMap<String, String> currencyhm = new HashMap<String, String>();
                currencyhm.put(currrencycode, currencyname);
                crrncyArrhm.add(currencyhm);

            } while (cursor.moveToNext());
        }
        Log.d("crrncyArrhm-ll->", "lllll" + crrncyArrhm.toString());
        return crrncyArrhm;

    }

    /* Get DEFAULT VALUE from GENERAL SETTTING CursorTable */
    public String getDefaultValue(String value) {
        String defaultValue = "";
        String selectQuery = "SELECT SettingValue FROM tblGetGeneralSettings WHERE CompanyCode = '" + comapanyCode + "' SettingID = '"
                + value + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                defaultValue = cursor.getString(cursor
                        .getColumnIndex(COLUMN_SETTINGVALUE));

            } while (cursor.moveToNext());
        }
        return defaultValue;

    }

    /* Get Currency Rate from Currency CursorTable */
    public String getCurrencyRate(String currencycode) {
        String currencyRate = "";
        String selectQuery = "SELECT CurrencyRate FROM tblGetCurrency WHERE CurrencyCode = '"
                + currencycode + "' AND CompanyCode= '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                currencyRate = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCYRATE));

            } while (cursor.moveToNext());
        }
        return currencyRate;

    }

    /* Get Currency Name from Currency CursorTable */
    public String getCurrencyName(String currencycode) {
        String currencyName = "";
        String selectQuery = "SELECT CurrencyName FROM tblGetCurrency WHERE CurrencyCode = '"
                + currencycode + "' AND CompanyCode= '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                currencyName = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CURRENCYNAME));

            } while (cursor.moveToNext());
        }
        return currencyName;

    }

    /* Get CustomerGroupCode from Customer CursorTable */
    public ArrayList<String> getCustGroupCode(String custcode) {
        ArrayList<String> custgroupcode = new ArrayList<String>();
        String selectQuery = "SELECT CustomerCode,CustomerGroupCode FROM tblGetCustomer WHERE CustomerCode = '"
                + custcode + "' AND CompanyCode= '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                custgroupcode.add(cursor.getString(cursor
                        .getColumnIndex(COLUMN_CUSTOMERCODE)));

                custgroupcode.add(cursor.getString(cursor
                        .getColumnIndex(COLUMN_CUSTOMERGROUPCODE)));
            } while (cursor.moveToNext());
        }

        return custgroupcode;
    }

    /* Get AllCustomerCode */
    public static ArrayList<String> getAllCustomer() {
        ArrayList<String> customerall_Arr = new ArrayList<String>();
        customerall_Arr.clear();
        String selectQuery = "SELECT CustomerCode FROM tblGetCustomer WHERE CompanyCode = '" + comapanyCode + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String customercode = cursor.getString(cursor.getColumnIndex("CustomerCode"));
                customerall_Arr.add(customercode);
            } while (cursor.moveToNext());
        }
        Log.d("customerall_Arr", customerall_Arr.toString());
        return customerall_Arr;
    }

    /* Get CustomerTax from Customer CursorTable */
    public void getCustomerTax(String custcode) {
        String selectQuery = "SELECT HaveTax,TaxType,TaxValue FROM tblGetCustomer WHERE CustomerCode = '"
                + custcode + "' AND CompanyCode= '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String HaveTax = cursor.getString(cursor
                        .getColumnIndex(COLUMN_HAVETAX));

                String TaxType = cursor.getString(cursor
                        .getColumnIndex(COLUMN_TAXTYPE));

                String TaxValue = cursor.getString(cursor
                        .getColumnIndex(COLUMN_TAXVALUE));

                if (HaveTax.matches("True") || HaveTax.matches("true")) {
//                    SalesOrderSetGet.setTaxValue(TaxValue);
                    SalesOrderSetGet.setCompanytax(TaxType);
                } else {
//                    SalesOrderSetGet.setTaxValue("");
                    SalesOrderSetGet.setCompanytax("Z");
                }
            } while (cursor.moveToNext());
        }

    }

    /* Get CategoryValues from Category CursorTable */
    public String getCategory() {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT CategoryCode,Description  FROM tblGetCategory WHERE CompanyCode = '"
                    + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"CategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CATEGORYCODE)))
                            .append("\",\"Description\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_DESCRIPTION)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        Log.d("Offline category", ":" + builder.toString());
        return builder.toString();

    }

    /* Get SubCategoryValue from SubCategory CursorTable */
    public String getSubCategory() {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT SubCategoryCode,Description FROM tblGetSubCategory WHERE CompanyCode = '"
                    + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"SubCategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBCATEGORYCODE)))
                            .append("\",\"Description\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_DESCRIPTION)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        Log.d("Offline subcategory", ":" + builder.toString());
        return builder.toString();

    }

    /* Get GeneralSettingValue from GeneralSetting CursorTable */
    public String getGeneralSettingsValue() {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT SettingID,SettingValue FROM tblGetGeneralSettings WHERE CompanyCode = '" + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"SettingID\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SETTINGID)))
                            .append("\",\"SettingValue\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SETTINGVALUE)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        Log.d("General settings", ":" + builder.toString());
        return builder.toString();

    }

	/* Get Product from Product CursorTable */
//	public String getProduct() {
//
//		StringBuilder builder = new StringBuilder("[");
//		try {
//			String selectQuery = "SELECT ProductCode,ProductName,Weight,WeightBarcodeStartsOn,WeightBarcodeEndsOn FROM tblGetProduct WHERE CompanyCode = '"
//					+ comapanyCode + "'";
//			Log.d("selectQuery", selectQuery);
//			Cursor cursor = getDatabase().rawQuery(selectQuery, null);
//			if (cursor.moveToFirst()) {
//				do {
//					builder.append("{")
//							.append("\"ProductCode\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_PRODUCTCODE)))
//							.append("\",\"ProductName\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_PRODUCTNAME)))
//							.append("\",\"Weight\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_WEIGHT)))
//							.append("\",\"WeightBarcodeStartsOn\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_WEIGHTBARCODESTARTSON)))
//							.append("\",\"WeightBarcodeEndsOn\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_WEIGHTBARCODEENDSON)))
//							.append("\"}");
//					if (!cursor.isLast()) {
//						builder.append(",");
//					}
//				} while (cursor.moveToNext());
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		builder.append("]");
//		Log.d("Prodct", ":" + builder.toString());
//		return builder.toString();
//
//	}

    public String getProduct() {
        String result = "";
        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetProduct  WHERE CompanyCode = '" + comapanyCode + "'";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
//				    String pn ="(TMC-00010A), Heavy-duty wiper arm, 11.25\\\" - 15\\\",";
                    String productname = cursor.getString(cursor
                            .getColumnIndex(COLUMN_PRODUCTNAME));

                    productname = productname.replace("\"", "\\\"");

                    builder.append("{")

                            .append("\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))

                            .append("\",\"ProductName\":\"")
                            .append(productname)

                            .append("\",\"Weight\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_WEIGHT)))

                            .append("\",\"WeightBarcodeStartsOn\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_WEIGHTBARCODESTARTSON)))

                            .append("\",\"WeightBarcodeEndsOn\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_WEIGHTBARCODEENDSON)))
                            .append("\",\"WholeSalePrice\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_WHOLESALEPRICE)))

                            .append("\",\"RetailPrice\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_RETAILPRICE)))

                            .append("\",\"PcsPerCarton\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PCSPERCARTON)))

                            .append("\",\"CategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CATEGORYCODE)))

                            .append("\",\"SubCategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBCATEGORYCODE)))

                            .append("\",\"UOMCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_UOMCODE)))

                            .append("\",\"TaxPerc\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAXPERC)))

                            .append("\",\"MinimumSellingPrice\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_MINIMUMSELLINGPRICE)))

                            .append("\",\"Qty\":\"").append("")

                            .append("\",\"CatName\":\"").append("")

                            .append("\",\"SubCatName\":\"").append("")

                            .append("\",\"CatDisplayOrder\":\"").append("")

                            .append("\",\"SubCatDisplayOrder\":\"").append("")

                            .append("\",\"ProdDisplayOrder\":\"").append("")


                            .append("\"}");


                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");

            result = builder.toString();
//			 result = " { SODetails: " + builder.toString() + "}";
//			Log.d("SO Details", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


    /* Get ProductBarcode from Product CursorTable */
    public String getProductBacode() {

        StringBuilder builder = new StringBuilder("[");
        try {
            String selectQuery = "SELECT ProductCode,Barcode FROM tblGetProductBarCode WHERE CompanyCode = '"
                    + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))
                            .append("\",\"Barcode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BARCODE)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        Log.d("ProdctBarcode", ":" + builder.toString());
        return builder.toString();

    }

    /* Get ProductBarcode from Product CursorTable */
    public String getProductBarcode(String productCode) {

        StringBuilder builder = new StringBuilder("[");
        try {
            String selectQuery = "SELECT ProductCode,Barcode FROM tblGetProductBarCode WHERE Barcode = '"
                    + productCode + "' AND CompanyCode = '" + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))
                            .append("\",\"Barcode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BARCODE)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        Log.d("ProdctBarcode", ":" + builder.toString());
        return builder.toString();

    }

    /* Get ProductSalesValue From Product CursorTable */
    public ArrayList<String> getSaleProduct(String productcode, String classname) {
        ArrayList<String> productArr = new ArrayList<String>();
        productArr.clear();
        String selectQuery = "SELECT ProductCode,ProductName,WholeSalePrice,UOMCode,PcsPerCarton,TaxPerc,RetailPrice,MinimumSellingPrice,HaveBatch,HaveExpiry,Weight,MinimumCartonSellingPrice FROM tblGetProduct WHERE ProductCode = '"
                + productcode + "' AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String slCode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRODUCTCODE));

                String slName = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRODUCTNAME));

                String wholeSalesPrice = cursor.getString(cursor
                        .getColumnIndex(COLUMN_WHOLESALEPRICE));

                String uomCode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_UOMCODE));

                String pcsPerCarton = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PCSPERCARTON));

                String taxPerc = cursor.getString(cursor
                        .getColumnIndex(COLUMN_TAXPERC));
                String retailprice = cursor.getString(cursor
                        .getColumnIndex(COLUMN_RETAILPRICE));

                String MinimumSellingPrice = cursor.getString(cursor
                        .getColumnIndex(COLUMN_MINIMUMSELLINGPRICE));

                String haveBatch = cursor.getString(cursor
                        .getColumnIndex(COLUMN_HAVEBATCH));

                String haveExpiry = cursor.getString(cursor
                        .getColumnIndex(COLUMN_HAVEEXPIRY));

                String Weight = cursor.getString(cursor
                        .getColumnIndex(COLUMN_WEIGHT));

                String MinimumCartonSellingPrice = cursor.getString(cursor
                        .getColumnIndex(COLUMN_MINIMUMCARTONSELLINGPRICE));
                Log.d("wholeSalesPrice", "" + wholeSalesPrice);

                productArr.add(wholeSalesPrice);
                productArr.add(uomCode);

                if (pcsPerCarton.matches("0")
                        || pcsPerCarton.matches("null")) {
                    productArr.add("1");
                } else {

//					slCartonPerQty = "12";
                    String part1 = "", part2 = "";
                    try {
                        Log.d("point", "...");
                        StringTokenizer tokens = new StringTokenizer(pcsPerCarton, ".");
                        part1 = tokens.nextToken(); // 12
                        part2 = tokens.nextToken(); // 000

                        if (part2.matches("0") || part2.matches("00") || part2.matches("000") || part2.matches("0000")
                                || part2.matches("00000")) {
                            productArr.add(part1);

                            Log.d("part1", "" + part1);
                            Log.d("part2", "" + part2);
                        } else {


                            if (classname.matches("SalesAddProduct")) {
                                productArr.add(pcsPerCarton);
                            } else {
                                productArr.add(part1);
                            }

                        }
                    } catch (Exception e) {
                        Log.d("no point", "" + pcsPerCarton);
                        Log.d("part1", "" + part1);
                        e.printStackTrace();
//						part1 = tokens.nextToken();
                        productArr.add(part1);


                    }

//					}

//					productArr.add(slCartonPerQty);
                }


//				if (pcsPerCarton.matches("0") || pcsPerCarton.matches("null")) {
//					productArr.add("1");
//				} else {
//					productArr.add(pcsPerCarton);
//				}
                productArr.add(taxPerc);
                productArr.add(retailprice);

                productArr.add(MinimumSellingPrice);
                productArr.add(haveBatch);
                productArr.add(haveExpiry);
                productArr.add(slCode);

                productArr.add(slName);
                productArr.add(Weight);
                productArr.add(MinimumCartonSellingPrice);
            } while (cursor.moveToNext());
        }
        return productArr;
    }

    /* Get SearchProductValue From Product CursorTable */
    public ArrayList<HashMap<String, String>> searchProduct(
            String categoryCode, String subcategoryCode) {
        ArrayList<HashMap<String, String>> searchproductArr = new ArrayList<HashMap<String, String>>();
        searchproductArr.clear();
//		String whereQuery = "";
//		if (!categoryCode.matches("") && !subcategoryCode.matches("")) {
//			whereQuery = "CategoryCode = '" + categoryCode
//					+ "' AND SubCategoryCode ='" + subcategoryCode + "'";
//		} else if (!categoryCode.matches("")) {
//			whereQuery = "CategoryCode = '" + categoryCode + "'";
//		} else if (!subcategoryCode.matches("")) {
//			whereQuery = "SubCategoryCode = '" + subcategoryCode + "'";
//		}
        String selectQuery = "SELECT ProductCode,ProductName,WholeSalePrice FROM tblGetProduct WHERE CompanyCode = '" + comapanyCode + "'";
        if (!categoryCode.matches("") && !subcategoryCode.matches("")) {
            selectQuery += "AND CategoryCode = '" + categoryCode + "' AND SubCategoryCode ='" + subcategoryCode + "'";
        } else if (!categoryCode.matches("")) {
            selectQuery += "AND CategoryCode = '" + categoryCode + "'";
        } else if (!subcategoryCode.matches("")) {
            selectQuery += "AND SubCategoryCode = '" + subcategoryCode + "'";
        }

        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String productCode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRODUCTCODE));

                String productname = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRODUCTNAME));

                String wholesaleprice = cursor.getString(cursor
                        .getColumnIndex(COLUMN_WHOLESALEPRICE));

                HashMap<String, String> hm = new HashMap<String, String>();

                hm.put("ProductCode", productCode);
                hm.put("ProductName", productname);
                hm.put("WholeSalePrice", wholesaleprice);
                hm.put("Qty", "0");
                searchproductArr.add(hm);

            } while (cursor.moveToNext());
        }
        return searchproductArr;
    }

    /* Get Category From Category CursorTable */
    public ArrayList<HashMap<String, String>> getCategoryValue() {
        ArrayList<HashMap<String, String>> subCategoryArr = new ArrayList<HashMap<String, String>>();
        subCategoryArr.clear();
        String selectQuery = "SELECT CategoryCode,Description FROM tblGetCategory WHERE CompanyCode = '"
                + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String subCatCode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CATEGORYCODE));

                String desc = cursor.getString(cursor
                        .getColumnIndex(COLUMN_DESCRIPTION));

                HashMap<String, String> producthm = new HashMap<String, String>();
                producthm.put(subCatCode, desc);
                subCategoryArr.add(producthm);

            } while (cursor.moveToNext());
        }
        return subCategoryArr;
    }

    /* Get SubCategory From SubCategory CursorTable */
    public ArrayList<HashMap<String, String>> getSubCategoryValue() {
        ArrayList<HashMap<String, String>> subCategoryArr = new ArrayList<HashMap<String, String>>();
        subCategoryArr.clear();
        String selectQuery = "SELECT SubCategoryCode,Description FROM tblGetSubCategory WHERE CompanyCode = '"
                + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String subCatCode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_SUBCATEGORYCODE));

                String desc = cursor.getString(cursor
                        .getColumnIndex(COLUMN_DESCRIPTION));

                HashMap<String, String> producthm = new HashMap<String, String>();
                producthm.put(subCatCode, desc);
                subCategoryArr.add(producthm);

            } while (cursor.moveToNext());
        }
        return subCategoryArr;
    }

    /* Update product start and end weight From Product CursorTable */
    public int updateProductWeight(String currentDate, String proCode,
                                   int stwght, int edwght) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SERVERDATEANDTIME, currentDate);
        cv.put(COLUMN_WEIGHTBARCODESTARTSON, stwght);
        cv.put(COLUMN_WEIGHTBARCODEENDSON, edwght);
        return getDatabase().update(GETPRODUCT_TABLE, cv,
                "ProductCode=" + "'" + proCode + "'", null);
    }

    /* Get InvoiceDetail From tblGetInvoiceDetail CursorTable */
    public ArrayList<HashMap<String, String>> getInvoiceDetails(String Inv_No) {
        ArrayList<HashMap<String, String>> INV_DetailsArr = new ArrayList<HashMap<String, String>>();
        INV_DetailsArr.clear();

        String selectQuery = "SELECT * FROM tblGetInvoiceDetail WHERE InvoiceNo = '"
                + Inv_No + "' AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String slNo = cursor.getString(cursor
                        .getColumnIndex(COLUMN_SLNO));
                String ProductCode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRODUCTCODE));
                String ProductName = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRODUCTNAME));
                String CQty = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CQTY));
                String LQty = cursor.getString(cursor
                        .getColumnIndex(COLUMN_LQTY));
                String Qty = cursor
                        .getString(cursor.getColumnIndex(COLUMN_QTY));
                String FOCQty = cursor.getString(cursor
                        .getColumnIndex(COLUMN_FOCQTY));
                String PcsPerCarton = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PCSPERCARTON));
                String RetailPrice = cursor.getString(cursor
                        .getColumnIndex(COLUMN_RETAILPRICE));
                String Price = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRICE));
                String CartonPrice = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CARTONPRICE));
                String Total = cursor.getString(cursor
                        .getColumnIndex(COLUMN_TOTAL));
                String ItemDiscount = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ITEMDISCOUNT));

                String BillDiscount = cursor.getString(cursor
                        .getColumnIndex(COLUMN_BILLDISCOUNT));
                String TotalDiscount = cursor.getString(cursor
                        .getColumnIndex(COLUMN_TOTALDISCOUNT));
                String SubTotal = cursor.getString(cursor
                        .getColumnIndex(COLUMN_SUBTOTAL));
                String Tax = cursor
                        .getString(cursor.getColumnIndex(COLUMN_TAX));
                String NetTotal = cursor.getString(cursor
                        .getColumnIndex(COLUMN_NETTOTAL));
                String TaxType = cursor.getString(cursor
                        .getColumnIndex(COLUMN_TAXTYPE));

                String TaxPerc = cursor.getString(cursor
                        .getColumnIndex(COLUMN_TAXPERC));
                String UOMCode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_UOMCODE));
                String ExchangeQty = cursor.getString(cursor
                        .getColumnIndex(COLUMN_EXCHANGEQTY));

                HashMap<String, String> hm = new HashMap<String, String>();

                hm.put("slNo", slNo);
                hm.put("ProductCode", ProductCode);
                hm.put("ProductName", ProductName);
                hm.put("CQty", CQty);
                hm.put("LQty", LQty);
                hm.put("Qty", Qty);
                hm.put("FOCQty", FOCQty);
                hm.put("ExchangeQty", ExchangeQty);
                hm.put("PcsPerCarton", PcsPerCarton);
                hm.put("RetailPrice", RetailPrice);
                hm.put("Price", Price);
                hm.put("Total", Total);
                hm.put("ItemDiscount", ItemDiscount);
                hm.put("BillDiscount", BillDiscount);
                hm.put("TotalDiscount", TotalDiscount);
                hm.put("SubTotal", SubTotal);
                hm.put("Tax", Tax);
                hm.put("NetTotal", NetTotal);
                hm.put("TaxType", TaxType);
                hm.put("TaxPerc", TaxPerc);

                hm.put("CartonPrice", CartonPrice);
                hm.put("UOMCode", UOMCode);

                INV_DetailsArr.add(hm);

            } while (cursor.moveToNext());
        }
        return INV_DetailsArr;
    }

    /* Get InvoiceHeaderDetails From tblGetInvoiceHeader CursorTable */
    public ArrayList<HashMap<String, String>> getInvoiceHeader(String Inv_No) {

        ArrayList<HashMap<String, String>> INV_HeaderArr = new ArrayList<HashMap<String, String>>();
        INV_HeaderArr.clear();
        try {
            String selectQuery = "SELECT * FROM tblGetInvoiceHeader WHERE InvoiceNo = '"
                    + Inv_No + "' AND CompanyCode = '" + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    String Invoive_No = cursor.getString(cursor
                            .getColumnIndex(COLUMN_INVOICENO));

                    String Invoive_Date = cursor.getString(cursor
                            .getColumnIndex(COLUMN_INVOICEDATE));
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    Date myDate = dateFormat.parse(Invoive_Date);
                    SimpleDateFormat timeFormat = new SimpleDateFormat(
                            "dd/MM/yyyy hh:mm:ss");
                    String finalDate = timeFormat.format(myDate);
                    StringTokenizer tokens = new StringTokenizer(finalDate, " ");
                    String date = tokens.nextToken();

                    String LocationCode = cursor.getString(cursor
                            .getColumnIndex(COLUMN_LOCATIONCODE));
                    String CustomerCode = cursor.getString(cursor
                            .getColumnIndex(COLUMN_CUSTOMERCODE));
                    String Total = cursor.getString(cursor
                            .getColumnIndex(COLUMN_TOTAL));
                    String ItemDiscount = cursor.getString(cursor
                            .getColumnIndex(COLUMN_ITEMDISCOUNT));
                    String BillDIscount = cursor.getString(cursor
                            .getColumnIndex(COLUMN_BILLDISCOUNT));
                    String TotalDiscount = cursor.getString(cursor
                            .getColumnIndex(COLUMN_TOTALDISCOUNT));
                    String SubTotal = cursor.getString(cursor
                            .getColumnIndex(COLUMN_SUBTOTAL));
                    String Tax = cursor.getString(cursor
                            .getColumnIndex(COLUMN_TAX));
                    String NetTotal = cursor.getString(cursor
                            .getColumnIndex(COLUMN_NETTOTAL));
                    String Remarks = cursor.getString(cursor
                            .getColumnIndex(COLUMN_REMARKS));
                    String CurrencyCode = cursor.getString(cursor
                            .getColumnIndex(COLUMN_CURRENCYCODE));
                    String CurrencyRate = cursor.getString(cursor
                            .getColumnIndex(COLUMN_CURRENCYRATE));

                    HashMap<String, String> hm = new HashMap<String, String>();

                    hm.put("Invoive_No", Invoive_No);
                    hm.put("Invoive_Date", date);
                    hm.put("LocationCode", LocationCode);
                    hm.put("CustomerCode", CustomerCode);
                    hm.put("Total", Total);
                    hm.put("ItemDiscount", ItemDiscount);
                    hm.put("BillDIscount", BillDIscount);
                    hm.put("TotalDiscount", TotalDiscount);
                    hm.put("SubTotal", SubTotal);
                    hm.put("Tax", Tax);
                    hm.put("NetTotal", NetTotal);
                    hm.put("Remarks", Remarks);
                    hm.put("CurrencyCode", CurrencyCode);
                    hm.put("CurrencyRate", CurrencyRate);

                    INV_HeaderArr.add(hm);

                } while (cursor.moveToNext());
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return INV_HeaderArr;
    }

    /* Get InvoiceProductBarcode From tblGetInvoiceCartonDetail CursorTable */
    public ArrayList<HashMap<String, String>> getInvoiceProductBarcode(
            String Inv_No) {

        ArrayList<HashMap<String, String>> INV_BarcodeArr = new ArrayList<HashMap<String, String>>();
        INV_BarcodeArr.clear();
        String selectQuery = "SELECT * FROM tblGetInvoiceCartonDetail  WHERE InvoiceNo = '"
                + Inv_No + "' AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String Invoice_slNo = cursor.getString(cursor
                        .getColumnIndex(COLUMN_SLNO));
                String Invoice_Prdcode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRODUCTCODE));
                String Invoice_Prdname = cursor.getString(cursor
                        .getColumnIndex(COLUMN_PRODUCTNAME));
                String Invoice_SeqNo = cursor.getString(cursor
                        .getColumnIndex(COLUMN_SEQNO));
                String Invoice_WeightBarcode = cursor.getString(cursor
                        .getColumnIndex(COLUMN_BARCODE));
                String Invoice_Weight = cursor.getString(cursor
                        .getColumnIndex(COLUMN_WEIGHT));

                HashMap<String, String> hm = new HashMap<String, String>();

                // hm.put("Invoive_No", Invoice_No);
                hm.put("slNo", Invoice_slNo);
                hm.put("SeqNo", Invoice_SeqNo);
                hm.put("ProductName", Invoice_Prdname);
                hm.put("Weight", Invoice_Weight);
                hm.put("ProductCode", Invoice_Prdcode);
                hm.put("WeightBarcode", Invoice_WeightBarcode);
                INV_BarcodeArr.add(hm);

            } while (cursor.moveToNext());

        }
        return INV_BarcodeArr;
    }

	/* Get Individual Customer from Customer CursorTable */
//	public String getCustomer(String customerCode) {
//
//		StringBuilder builder = new StringBuilder("[");
//		try {
//
//			String selectQuery = "SELECT CustomerCode,CustomerName FROM tblGetCustomer WHERE CustomerCode = '"
//					+ customerCode
//					+ "' AND CompanyCode = '"
//					+ comapanyCode
//					+ "'";
//			Log.d("selectQuery", selectQuery);
//			Cursor cursor = getDatabase().rawQuery(selectQuery, null);
//			if (cursor.moveToFirst()) {
//				do {
//					builder.append("{")
//							.append("\"CustomerCode\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_CUSTOMERCODE)))
//							.append("\",\"CustomerName\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_CUSTOMERNAME)))
//							.append("\"}");
//					if (!cursor.isLast()) {
//						builder.append(",");
//					}
//				} while (cursor.moveToNext());
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		builder.append("]");
//		Log.d("customer", ":" + builder.toString());
//		return builder.toString();
//
//	}

    /* Get Bank from Bank CursorTable */
    public String getBank() {

        String result = "";
        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblBank WHERE CompanyCode = '"
                    + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"Code\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BANKCODE)))
                            .append("\",\"Description\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_DESCRIPTION)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        result = " { JsonArray: " + builder.toString() + "}";
        Log.d("Bank", ":" + result);
        return result;

    }

    /* Get PayMode from Paymode CursorTable */
    public String getPaymode() {

        String result = "";
        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblPayMode WHERE CompanyCode = '"
                    + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"Code\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PAYMODECODE)))
                            .append("\",\"Description\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_DESCRIPTION)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        result = " { JsonArray: " + builder.toString() + "}";
        Log.d("Paymode", ":" + result);
        return result;

    }

    /* Update InvoiceHeaderOnlyHaveBalance Table */
    public static void update_InvoiceHeaderOnlyHaveBalance(
            HashMap<String, String> productvalues) {
        ContentValues cv = new ContentValues();
        String invoiceno = productvalues.get(("InvoiceNo"));
        cv.put(COLUMN_COMPANYCODE, productvalues.get("CompanyCode"));
        cv.put(COLUMN_SERVERDATEANDTIME, productvalues.get("DateTime"));
        cv.put(COLUMN_INVOICENO, productvalues.get("InvoiceNo"));
        cv.put(COLUMN_INVOICEDATE, productvalues.get("InvoiceDate"));
        cv.put(COLUMN_LOCATIONCODE, productvalues.get("LocationCode"));
        cv.put(COLUMN_CUSTOMERCODE, productvalues.get("CustomerCode"));
        cv.put(COLUMN_CUSTOMERNAME, productvalues.get("CustomerName"));
        cv.put(COLUMN_TOTAL, productvalues.get("Total"));
        cv.put(COLUMN_ITEMDISCOUNT, productvalues.get("ItemDiscount"));
        cv.put(COLUMN_BILLDISCOUNT, productvalues.get("BillDiscount"));
        cv.put(COLUMN_TOTALDISCOUNT, productvalues.get("TotalDiscount"));
        cv.put(COLUMN_SUBTOTAL, productvalues.get("SubTotal"));
        cv.put(COLUMN_TAX, productvalues.get("Tax"));
        cv.put(COLUMN_NETTOTAL, productvalues.get("NetTotal"));
        cv.put(COLUMN_PAIDAMOUNT, productvalues.get("PaidAmount"));
        cv.put(COLUMN_CREDITAMOUNT, productvalues.get("CreditAmount"));
        cv.put(COLUMN_BALANCEAMOUNT, productvalues.get("BalanceAmount"));
        cv.put(COLUMN_REMARKS, productvalues.get("Remarks"));
        cv.put(COLUMN_CURRENCYCODE, productvalues.get("CurrencyCode"));
        cv.put(COLUMN_CURRENCYRATE, productvalues.get("CurrencyRate"));
        cv.put(COLUMN_SONO, productvalues.get("SoNo"));
        cv.put(COLUMN_DONO, productvalues.get("DoNo"));
        cv.put(COLUMN_TOTALBALANCE, productvalues.get("TotalBalance"));
        getDatabase().update(GETINVOICEHEADERONLYHAVEBALANCE_TABLE, cv,
                "InvoiceNo = '" + invoiceno + "'", null);
    }

    /* Get InvoiceHeaderDetails from InvoiceHeader CursorTable */
    public String getInvoiceHeaderOnlyHaveBalance(String customerCode) {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT InvoiceNo,InvoiceDate,NetTotal,CreditAmount,NetTotal,BalanceAmount FROM tblInvoiceHeaderOnlyHaveBalance WHERE CustomerCode  = '"
                    + customerCode
                    + "' AND CompanyCode = '"
                    + comapanyCode
                    + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"InvoiceNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_INVOICENO)))
                            .append("\",\"InvoiceDate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_INVOICEDATE)))
                            .append("\",\"NetTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_NETTOTAL)))
                            .append("\",\"CreditAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CREDITAMOUNT)))
                            .append("\",\"BalanceAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BALANCEAMOUNT)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        Log.d("InvoiceHeaderByInvoiceNo", ":" + builder.toString());
        return builder.toString();
    }

    /* Get CompanyLogo */
    public static String getCompanyLogo() {
        String companylogo = "";
        Log.d("Login COMPANYCODE", " --> " + comapanyCode);
        String selectQuery = "SELECT Logo FROM tblGetCompanyLogo WHERE CompanyCode = '"
                + comapanyCode + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                companylogo = cursor.getString(cursor.getColumnIndex("Logo"));

            } while (cursor.moveToNext());
        }
        return companylogo;
    }

    /* Update InvoiceHeader Table */
    public static void update_invoiceheader(HashMap<String, String> HeaderValue) {
        // Log.d("InvoiceHearder Insert", "Inserting...");
        ContentValues cv = new ContentValues();
        String invoiceno = HeaderValue.get(("InvoiceNo"));
        cv.put(COLUMN_COMPANYCODE, HeaderValue.get("CompanyCode"));
        cv.put(COLUMN_SERVERDATEANDTIME, HeaderValue.get(("DateTime")));
        cv.put(COLUMN_INVOICENO, HeaderValue.get(("InvoiceNo")));
        cv.put(COLUMN_INVOICEDATE, HeaderValue.get(("InvoiceDate")));
        cv.put(COLUMN_LOCATIONCODE, HeaderValue.get(("LocationCode")));
        cv.put(COLUMN_CUSTOMERCODE, HeaderValue.get(("CustomerCode")));
        cv.put(COLUMN_CUSTOMERCODE, HeaderValue.get(("CustomerName")));
        cv.put(COLUMN_TOTAL, HeaderValue.get(("Total")));
        cv.put(COLUMN_ITEMDISCOUNT, HeaderValue.get(("ItemDiscount")));
        cv.put(COLUMN_BILLDISCOUNT, HeaderValue.get(("BillDiscount")));
        cv.put(COLUMN_TOTALDISCOUNT, HeaderValue.get(("TotalDiscount")));
        cv.put(COLUMN_SUBTOTAL, HeaderValue.get(("SubTotal")));
        cv.put(COLUMN_TAX, HeaderValue.get(("Tax")));
        cv.put(COLUMN_NETTOTAL, HeaderValue.get(("NetTotal")));
        cv.put(COLUMN_PAIDAMOUNT, HeaderValue.get(("PaidAmount")));
        cv.put(COLUMN_CREDITAMOUNT, HeaderValue.get(("CreditAmount")));
        cv.put(COLUMN_BALANCEAMOUNT, HeaderValue.get(("BalanceAmount")));
        cv.put(COLUMN_REMARKS, HeaderValue.get(("Remarks")));
        cv.put(COLUMN_CURRENCYCODE, HeaderValue.get(("CurrencyCode")));
        cv.put(COLUMN_CURRENCYRATE, HeaderValue.get(("CurrencyRate")));
        cv.put(COLUMN_SONO, HeaderValue.get(("SoNo")));
        cv.put(COLUMN_DONO, HeaderValue.get(("DoNo")));
        cv.put(COLUMN_TOTALBALANCE, HeaderValue.get(("TotalBalance")));
        // cv.put(COLUMN_USERID, HeaderValue.get(("UserName")));
        // cv.put(COLUMN_DEVICEID, HeaderValue.get(("DeviceId")));
        getDatabase().update(GETINVOICEHEADER_TABLE, cv,
                "InvoiceNo = '" + invoiceno + "'", null);
    }

    public static int updateNextRunningNumber(String columnName) {
        int num = 0;
        String selectQuery = "SELECT " + columnName
                + " FROM tblNextRunningNumber where slNo ='1'";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                num = cursor.getInt(cursor.getColumnIndex(columnName));
                num = num + 1;

            } while (cursor.moveToNext());
        }
        ContentValues cv = new ContentValues();
        cv.put(columnName, num);
        return getDatabase().update(GETNEXTRUNNINGNUMBER_TABLE, cv,
                " slNo =  '1'", null);
    }

    public static String getNextRunningNumber(String columnPrefix,
                                              String columnNo) {
        String NextNo = "";
        String selectQuery = "SELECT "
                + columnPrefix
                + " || replace(substr(quote(zeroblob((6 + 1) / 2)), 3, (6 - length("
                + columnNo + "))), '0', '0') || " + columnNo + " as "
                + columnNo + " FROM tblNextRunningNumber WHERE slNo='1'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NextNo = cursor.getString(cursor.getColumnIndex(columnNo));

            } while (cursor.moveToNext());
        }
        return NextNo;
    }

    public static String getNextCustomerRunningNumber(String columnPrefix,
                                                      String columnNo) {
        String NextNo = "";
        String selectQuery = "SELECT "
                + columnPrefix
                + " || replace(substr(quote(zeroblob((5 + 1) / 2)), 3, (5 - length("
                + columnNo + "))), '0', '0') || " + columnNo + " as "
                + columnNo + " FROM tblNextRunningNumber WHERE slNo='1'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NextNo = cursor.getString(cursor.getColumnIndex(columnNo));
                Log.d("NextRunningNo", "..." + NextNo);

            } while (cursor.moveToNext());
        }
        return NextNo;
    }

    /* Update ProductStock Table */
    public static void updateProductStock(
            ArrayList<HashMap<String, String>> invoiceDetails) {

        Log.d("ProductStock Update", "Updating...");
        for (int i = 0; i < invoiceDetails.size(); i++) {
            HashMap<String, String> detailValue = invoiceDetails.get(i);

            String locationCode = SalesOrderSetGet.getLocationcode();
            String invoiceNo = detailValue.get("InvoiceNo");
            String productCode = detailValue.get("ProductCode");
            String focQty = detailValue.get("FOCQty");
            String prodQty = detailValue.get("Qty");
            String exchangeQty = detailValue.get("ExchangeQty");

            double fQty = 0.00, pQty = 0.00, eQty = 0.00, qty = 0.00;
            try {
                if (!focQty.matches("")) {
                    fQty = Double.parseDouble(focQty);
                }
                if (!prodQty.matches("")) {
                    pQty = Double.parseDouble(prodQty);
                }
                if (!exchangeQty.matches("")) {
                    eQty = Double.parseDouble(exchangeQty);
                }
            } catch (Exception e) {

            }
            // double totalQty = checkInvoiceNo(invoiceNo,productCode);
            // Log.d("totalQty", "-->"+totalQty);
            qty = GetQty(locationCode, productCode);
            Log.d("Product stock qty", "...>" + qty);
            // qty = qty + totalQty;
            // Log.d("Before Product stock qty", "..." + qty);
            qty = qty - (pQty + fQty + eQty);
            Log.d("After Product stock qty", "..." + qty);

            String where = "LocationCode = '" + locationCode
                    + "' And ProductCode = '" + productCode
                    + "' And CompanyCode = '" + comapanyCode + "'";
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_QTY, qty);
            getDatabase().update(GETPRODUCTSTOCK_TABLE, cv, where, null);
        }
    }

    public static void updateProductStockforeditinvoice(
            ArrayList<HashMap<String, String>> invoiceDetails) {

        Log.d("ProductStock Update", "Updating...");
        for (int i = 0; i < invoiceDetails.size(); i++) {
            HashMap<String, String> detailValue = invoiceDetails.get(i);

            String locationCode = SalesOrderSetGet.getLocationcode();
            // String invoiceNo = detailValue.get("InvoiceNo");
            String productCode = detailValue.get("ProductCode");
            // String focQty = detailValue.get("FOCQty");
            String prodQty = detailValue.get("Qty");

            String where = "LocationCode = '" + locationCode
                    + "' And ProductCode = '" + productCode
                    + "' And CompanyCode = '" + comapanyCode + "'";
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_QTY, prodQty);
            getDatabase().update(GETPRODUCTSTOCK_TABLE, cv, where, null);
        }
    }

    public static double GetQty(String locationCode, String productCode) {
        double qty = 0;
        String selectQuery = "SELECT Qty FROM tblGetProductStock Where LocationCode = '"
                + locationCode
                + "' And ProductCode = '"
                + productCode
                + "' And CompanyCode = '" + comapanyCode + "'";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                qty = cursor.getInt(cursor.getColumnIndex("Qty"));
            } while (cursor.moveToNext());
        }

        return qty;
    }

    public static double checkInvoiceNo(String invoiceno, String prodcode) {
        double qty = 0.0, fqty = 0.0, exqty = 0.0, totalQty = 0.0;
        String selectQuery = "SELECT Qty,FOCQty,ExchangeQty FROM tblGetInvoiceDetail where InvoiceNo = '"
                + invoiceno
                + "'"
                + "' And CompanyCode = '"
                + comapanyCode
                + "'" + "' And ProductCode = '" + prodcode + "'";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                qty = cursor.getInt(cursor.getColumnIndex("Qty"));
                fqty = cursor.getInt(cursor.getColumnIndex("FOCQty"));
                exqty = cursor.getInt(cursor.getColumnIndex("ExchangeQty"));
            } while (cursor.moveToNext());
        }
        totalQty = qty + fqty + exqty;
        return totalQty;
    }

    public static double getTotalBalence(String invoiveNo) {
        String customerCode = "";
        double totalBalance = 0.0;
        String selectQuery = "SELECT CustomerCode FROM tblGetInvoiceHeader  WHERE InvoiceNo = '"
                + invoiveNo + "' And CompanyCode = '" + comapanyCode + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                customerCode = cursor.getString(cursor
                        .getColumnIndex("CustomerCode"));

            } while (cursor.moveToNext());
        }

        String Query = "SELECT BalanceAmount FROM tblGetInvoiceHeader  WHERE CustomerCode = '"
                + customerCode + "' And CompanyCode = '" + comapanyCode + "'";

        Cursor balncursor = getDatabase().rawQuery(Query, null);
        if (balncursor.moveToFirst()) {
            do {
                totalBalance += balncursor.getDouble(balncursor
                        .getColumnIndex("BalanceAmount"));

            } while (balncursor.moveToNext());
        }

        return totalBalance;
    }

    public static void updateInvoiceBalance(String invoiceno, String netvale,
                                            String paidvalue, String creditvalue) {

        double credit = 0.00, paid = 0.00, bal = 0.0;
        String selectQuery = "SELECT * FROM tblGetInvoiceHeader  WHERE InvoiceNo = '"
                + invoiceno + "' LIMIT 1";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                bal = cursor.getDouble(cursor.getColumnIndex("BalanceAmount"));
                paid = cursor.getDouble(cursor.getColumnIndex("PaidAmount"));
                credit = cursor
                        .getDouble(cursor.getColumnIndex("CreditAmount"));

            } while (cursor.moveToNext());
        }
        paid = paid + Double.parseDouble(paidvalue);
        credit = credit + Double.parseDouble(creditvalue);
        bal = bal
                - (Double.parseDouble(paidvalue) + Double
                .parseDouble(creditvalue));

        String spaid = twoDecimalPoint(paid);
        String scredit = twoDecimalPoint(credit);
        String sbal = twoDecimalPoint(bal);

        Log.d("balance paid-->credit-->bal", spaid + "-->" + scredit + "-->"
                + sbal);

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NETTOTAL, netvale);
        cv.put(COLUMN_PAIDAMOUNT, spaid);
        cv.put(COLUMN_CREDITAMOUNT, scredit);
        cv.put(COLUMN_BALANCEAMOUNT, sbal);
        getDatabase().update(GETINVOICEHEADER_TABLE, cv,
                "InvoiceNo = '" + invoiceno + "'", null);
    }

    public static void updateInvoiceHaveBalance(String invoiceno,
                                                String netvale, String paidvalue, String creditvalue,
                                                String balancevalue) {

        double credit = 0.00, paid = 0.00, bal = 0.0;
        String selectQuery = "SELECT * FROM tblInvoiceHeaderOnlyHaveBalance WHERE InvoiceNo = '"
                + invoiceno + "' LIMIT 1";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                bal = cursor.getDouble(cursor.getColumnIndex("BalanceAmount"));
                paid = cursor.getDouble(cursor.getColumnIndex("PaidAmount"));
                credit = cursor
                        .getDouble(cursor.getColumnIndex("CreditAmount"));

            } while (cursor.moveToNext());
        }
        paid = paid + Double.parseDouble(paidvalue);
        credit = credit + Double.parseDouble(creditvalue);
        bal = bal
                - (Double.parseDouble(paidvalue) + Double
                .parseDouble(creditvalue));
        Log.d("balance paid-->credit-->bal", paid + "-->" + credit + "-->"
                + bal);

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NETTOTAL, netvale);
        cv.put(COLUMN_PAIDAMOUNT, paid);
        cv.put(COLUMN_CREDITAMOUNT, credit);
        cv.put(COLUMN_BALANCEAMOUNT, bal);
        getDatabase().update(GETINVOICEHEADERONLYHAVEBALANCE_TABLE, cv,
                "InvoiceNo = '" + invoiceno + "'", null);

    }

    // @Offline
    public static String getCustomersList(
            HashMap<String, String> customerhashValue) {
        String customerCode = "", needOutstandingAmount = "", areaCode = "", vanCode = "";
        customerCode = customerhashValue.get("CustomerCode");
        needOutstandingAmount = customerhashValue.get("NeedOutstandingAmount");
        areaCode = customerhashValue.get("AreaCode");
        vanCode = customerhashValue.get("VanCode");

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetCustomer WHERE CompanyCode = '"
                    + comapanyCode + "'";
            if (customerCode != null && !customerCode.isEmpty()) {
                selectQuery += " AND CustomerCode = '" + customerCode + "'";
            }
            if (areaCode != null && !areaCode.isEmpty()) {
                selectQuery += " AND AreaCode IN (" + areaCode + ")";
            }
            if (vanCode != null && !vanCode.isEmpty()) {
                selectQuery += " AND VanCode  ='" + vanCode + "'";
            }

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"CustomerCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERCODE)))
                            .append("\",\"CustomerName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERNAME)))
                            .append("\",\"ContactPerson\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CONTACTPERSON)))
                            .append("\",\"Address1\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ADDRESS1)))
                            .append("\",\"Address2\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ADDRESS2)))
                            .append("\",\"Address3\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ADDRESS3)))
                            .append("\",\"PhoneNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PHONENO)))
                            .append("\",\"HandphoneNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_HANDPHONENO)))
                            .append("\",\"FaxNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_FAXNO)))
                            .append("\",\"Email\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_EMAIL)))
                            .append("\",\"HaveTax\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_HAVETAX)))
                            .append("\",\"CustomerGroupCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERGROUPCODE)))
                            .append("\",\"TermCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TERMCODE)))
                            .append("\",\"CreditLimit\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CREDITLIMIT)))
                            .append("\",\"TaxType\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAXTYPE)))
                            .append("\",\"TaxValue\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAXVALUE)))
                            .append("\",\"AreaCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_AREACODE)))
                            .append("\",\"VanCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_VANCODE)))
                            .append("\",\"ReferenceLocation\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_REFERENCELOCATION)))
                            .append("\",\"OutstandingAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_OUTSTANDINGAMOUNT)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        String result = " { JsonArray: " + builder.toString() + "}";
        Log.d("Offline Customer", ":" + result);
        return result;
    }

    public static String getArea(String areacode) {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT Code,Description FROM tblGetArea WHERE CompanyCode = '"
                    + comapanyCode + "'";
            if (!areacode.matches("")) {
                selectQuery += " AND Code = '" + areacode + "'";
            }
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"Code\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CODE)))
                            .append("\",\"Description\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_DESCRIPTION)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        String result = " { JsonArray: " + builder.toString() + "}";
        Log.d("Offline Area", ":" + result);
        return result;
    }

    public static String getTerms() {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT Code,Description FROM tblGetTerms WHERE CompanyCode = '"
                    + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"Code\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CODE)))
                            .append("\",\"Description\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_DESCRIPTION)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        String result = " { JsonArray: " + builder.toString() + "}";
        Log.d("Offline Terms", ":" + result);
        return result;
    }

    public static String getCustomerGroup() {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT Code,Description FROM tblGetCustomerGroup WHERE CompanyCode = '"
                    + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"Code\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CODE)))
                            .append("\",\"Description\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_DESCRIPTION)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        String result = " { JsonArray: " + builder.toString() + "}";
        Log.d("Offline CustomerGroupCode", ":" + result);
        return result;
    }

    /* Save Customer Table */
    public static void save_customer(HashMap<String, String> customervalues) {
        String customerCode = customervalues.get("OfflineCustomerCode");
        String cuser = SupplierSetterGetter.getUsername();
        Log.d("customerCode DB", "-->" + customerCode);

        String haveTax = customervalues.get("HaveTax");
        if (haveTax.matches("1")) {
            haveTax = "True";
        } else if (haveTax.matches("0")) {
            haveTax = "False";
        }
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SERVERDATEANDTIME, customervalues.get("DateTime"));
        cv.put(COLUMN_CUSTOMERCODE, customerCode);
        cv.put(COLUMN_CUSTOMERNAME, customervalues.get("CustomerName"));
        cv.put(COLUMN_CONTACTPERSON, customervalues.get("ContactPerson"));
        cv.put(COLUMN_ADDRESS1, customervalues.get("Address1"));
        cv.put(COLUMN_ADDRESS2, customervalues.get("Address2"));
        cv.put(COLUMN_ADDRESS3, customervalues.get("Address3"));
        cv.put(COLUMN_PHONENO, customervalues.get("PhoneNo"));
        cv.put(COLUMN_HANDPHONENO, customervalues.get("HandphoneNo"));
        cv.put(COLUMN_EMAIL, customervalues.get("Email"));
        cv.put(COLUMN_HAVETAX, haveTax);
        cv.put(COLUMN_CUSTOMERGROUPCODE,
                customervalues.get("CustomerGroupCode"));

        cv.put(CREATE_USER, cuser);
        cv.put(CREATE_DATE, customervalues.get("DateTime"));
        cv.put(MODIFY_USER, cuser);
        cv.put(MODIFY_DATE, customervalues.get("DateTime"));

        cv.put(COLUMN_TERMCODE, customervalues.get("TermCode"));
        cv.put(COLUMN_ISACTIVE, customervalues.get("IsActive"));
        cv.put(COLUMN_CREDITLIMIT, customervalues.get("CreditLimit"));
        cv.put(COLUMN_CUSTOMER_TAXTYPE, customervalues.get("TaxType"));
        cv.put(COLUMN_CUSTOMER_TAXVALUE, customervalues.get("TaxPerc"));
        cv.put(COLUMN_COMPANYCODE, customervalues.get("CompanyCode"));
        cv.put(COLUMN_FAXNO, customervalues.get("FaxNo"));
        cv.put(COLUMN_AREACODE, customervalues.get("AreaCode"));
        cv.put(COLUMN_VANCODE, customervalues.get("VanCode"));
        cv.put(COLUMN_DOWNLOADSTATUS, customervalues.get("DownloadStatus"));

        Log.d("Insert Customer", ": Inseting");
        getDatabase().insert(GETCUSTOMER_TABLE, null, cv);

    }

    public static void update_customer(HashMap<String, String> customervalues) {
        String customerCode = customervalues.get("CustomerCode");
        String cuser = SupplierSetterGetter.getUsername();
        Log.d("customerCode DB", "-->" + customerCode);

        String haveTax = customervalues.get("HaveTax");
        if (haveTax.matches("1")) {
            haveTax = "True";
        } else if (haveTax.matches("0")) {
            haveTax = "False";
        }
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SERVERDATEANDTIME, customervalues.get("DateTime"));
        cv.put(COLUMN_CUSTOMERCODE, customerCode);
        cv.put(COLUMN_CUSTOMERNAME, customervalues.get("CustomerName"));
        cv.put(COLUMN_CONTACTPERSON, customervalues.get("ContactPerson"));
        cv.put(COLUMN_ADDRESS1, customervalues.get("Address1"));
        cv.put(COLUMN_ADDRESS2, customervalues.get("Address2"));
        cv.put(COLUMN_ADDRESS3, customervalues.get("Address3"));
        cv.put(COLUMN_PHONENO, customervalues.get("PhoneNo"));
        cv.put(COLUMN_HANDPHONENO, customervalues.get("HandphoneNo"));
        cv.put(COLUMN_EMAIL, customervalues.get("Email"));
        cv.put(COLUMN_HAVETAX, haveTax);
        cv.put(COLUMN_CUSTOMERGROUPCODE,
                customervalues.get("CustomerGroupCode"));

        // cv.put(CREATE_USER,customervalues.get("CreateUser"));
        cv.put(CREATE_DATE, customervalues.get("DateTime"));
        cv.put(MODIFY_USER, cuser);
        cv.put(MODIFY_DATE, customervalues.get("DateTime"));

        cv.put(COLUMN_TERMCODE, customervalues.get("TermCode"));
        cv.put(COLUMN_ISACTIVE, customervalues.get("IsActive"));
        cv.put(COLUMN_CREDITLIMIT, customervalues.get("CreditLimit"));
        cv.put(COLUMN_CUSTOMER_TAXTYPE, customervalues.get("TaxType"));
        cv.put(COLUMN_CUSTOMER_TAXVALUE, customervalues.get("TaxPerc"));
        cv.put(COLUMN_COMPANYCODE, customervalues.get("CompanyCode"));
        cv.put(COLUMN_FAXNO, customervalues.get("FaxNo"));
        cv.put(COLUMN_AREACODE, customervalues.get("AreaCode"));
        cv.put(COLUMN_VANCODE, customervalues.get("VanCode"));
        cv.put(COLUMN_DOWNLOADSTATUS, customervalues.get("DownloadStatus"));

        Log.d("Update Customer", ": Updating");
        getDatabase().update(
                GETCUSTOMER_TABLE,
                cv,
                "CompanyCode = '" + comapanyCode + "' AND CustomerCode = '"
                        + customerCode + "'", null);

    }

    public static String getCustomerCode(String customercode) {
        String cust_code = "";
        String selectQuery = "SELECT CustomerCode FROM tblGetCustomer WHERE CompanyCode = '"
                + comapanyCode + "' AND CustomerCode = '" + customercode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                cust_code = cursor.getString(cursor
                        .getColumnIndex("CustomerCode"));
            } while (cursor.moveToNext());
        }

        return cust_code;
    }

    public static String getNextNo(String columnNo) {
        String NextNo = "";
        String selectQuery = "SELECT " + columnNo + " FROM tblNextRunningNumber WHERE slNo='1'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NextNo = cursor.getString(cursor.getColumnIndex(columnNo));
                Log.d("GetNextNo " + columnNo, "..." + NextNo);

            } while (cursor.moveToNext());
        }
        return NextNo;
    }

    public static Cursor getCustomersCursorvalue() {

        String selectQuery = "SELECT * FROM tblGetCustomer WHERE CompanyCode = '" + comapanyCode + "' AND download_status='1'";
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        return cursor;
    }

//	public static void updateDateTime(String datetime) {
//		ContentValues cv = new ContentValues();
//		cv.put(COLUMN_SERVERDATEANDTIME, datetime);
//		getDatabase().update(GETDATETIME_TABLE, cv, "_id ='" + "1" + "'", null);
//	}

    public static void deleteInvoiceHaveBalance(String invoiceno) {
        getDatabase().delete(GETINVOICEHEADERONLYHAVEBALANCE_TABLE,
                "InvoiceNo = '" + invoiceno + "'", null);

    }

    public static void getInvoiceHeaderByInvoice(String invoiceno) {

        String selectQuery = "SELECT * FROM tblGetInvoiceHeader WHERE InvoiceNo = '" + invoiceno + "' AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String NetTotal = cursor.getString(cursor
                        .getColumnIndex(COLUMN_NETTOTAL));
                String BalanceAmount = cursor.getString(cursor
                        .getColumnIndex(COLUMN_BALANCEAMOUNT));

                if (!BalanceAmount.matches("")) {
                    SalesOrderSetGet.setBalanceAmount(BalanceAmount);
                }
                if (!NetTotal.matches("")) {
                    SalesOrderSetGet.setNetTotal(NetTotal);
                }

            } while (cursor.moveToNext());
        }

    }

    /* Get Currency CursorTable */
    public String getAllCurrencyJson(String currencyCode) {
        StringBuilder builder = new StringBuilder("[");
        try {
            String selectQuery = "SELECT * FROM tblGetCurrency WHERE CompanyCode = '"
                    + comapanyCode + "'";
            if (!currencyCode.matches("")) {
                selectQuery += " AND CurrencyCode = '" + currencyCode + "'";
            }
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"CurrencyCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CURRENCYCODE)))
                            .append("\",\"CurrencyName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CURRENCYNAME)))
                            .append("\",\"CurrencyRate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CURRENCYRATE)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        Log.d("Currency json", ":" + builder.toString());
        return builder.toString();

    }

    public static HashMap<String, String> getLocationHaveBatchOffline(
            String locationCode) {

        HashMap<String, String> locationArr = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM tblGetLocation where CompanyCode = "
                + "'" + comapanyCode + "'";
        if (!locationCode.matches("")) {
            selectQuery += " AND LocationCode = '" + locationCode + "'";
        }
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String HaveBatchOnStockIn = cursor.getString(cursor
                        .getColumnIndex("HaveBatchOnStockIn"));
                String HaveBatchOnStockOut = cursor.getString(cursor
                        .getColumnIndex("HaveBatchOnStockOut"));
                String HaveBatchOnTransfer = cursor.getString(cursor
                        .getColumnIndex("HaveBatchOnTransfer"));
                String HaveBatchOnStockAdjustment = cursor.getString(cursor
                        .getColumnIndex("HaveBatchOnStockAdjustment"));
                String NextBatchNo = cursor.getString(cursor
                        .getColumnIndex("NextBatchNo"));
                String HaveBatchConsignmentOut = cursor.getString(cursor
                        .getColumnIndex("HaveBatchOnConsignmentOut"));

                locationArr.put("HaveBatchOnStockIn", HaveBatchOnStockIn);
                locationArr.put("HaveBatchOnStockOut", HaveBatchOnStockOut);
                locationArr.put("HaveBatchOnTransfer", HaveBatchOnTransfer);
                locationArr.put("HaveBatchOnStockAdjustment",
                        HaveBatchOnStockAdjustment);
                locationArr.put("NextBatchNo", NextBatchNo);
                locationArr.put("HaveBatchOnConsignmentOut",HaveBatchConsignmentOut);
            } while (cursor.moveToNext());
        }

        Log.d("locationBatchArr Offline", locationArr.toString());

        return locationArr;
    }

    /* Store InvoiceDetail Table */
    public static void saveInvoiceDetail(
            ArrayList<HashMap<String, String>> invoiceDetails) {

        for (int i = 0; i < invoiceDetails.size(); i++) {
            HashMap<String, String> detailValue = invoiceDetails.get(i);
            String invoiceNo = detailValue.get("InvoiceNo");
            String OffInvoiceNo = detailValue.get("OfflineInvoiceNo");
            String pCode = detailValue.get("ProductCode");
            Log.d("detail comapanyCode", "aa" + comapanyCode);
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_COMPANYCODE, comapanyCode);
            cv.put(COLUMN_SERVERDATEANDTIME, detailValue.get(("DateTime")));
            cv.put(COLUMN_INVOICENO, detailValue.get(("OfflineInvoiceNo")));
            cv.put(COLUMN_SLNO, detailValue.get(("slNo")));
            cv.put(COLUMN_PRODUCTCODE, detailValue.get(("ProductCode")));
            cv.put(COLUMN_PRODUCTNAME, detailValue.get(("ProductName")));
            cv.put(COLUMN_CQTY, detailValue.get(("CQty")));
            cv.put(COLUMN_LQTY, detailValue.get(("Lqty")));
            cv.put(COLUMN_QTY, detailValue.get(("Qty")));
            cv.put(COLUMN_FOCQTY, detailValue.get(("FOCQty")));
            cv.put(COLUMN_PCSPERCARTON, detailValue.get(("PcsPerCarton")));
            cv.put(COLUMN_RETAILPRICE, detailValue.get(("RetailPrice")));
            cv.put(COLUMN_PRICE, detailValue.get(("Price")));
            cv.put(COLUMN_TOTAL, detailValue.get(("Total")));
            cv.put(COLUMN_ITEMDISCOUNT, detailValue.get(("ItemDiscount")));
            cv.put(COLUMN_BILLDISCOUNT, detailValue.get(("BillDiscount")));
            cv.put(COLUMN_TOTALDISCOUNT, detailValue.get(("TotalDiscount")));
            cv.put(COLUMN_SUBTOTAL, detailValue.get(("SubTotal")));
            cv.put(COLUMN_TAX, detailValue.get(("Tax")));
            cv.put(COLUMN_NETTOTAL, detailValue.get(("NetTotal")));
            cv.put(COLUMN_TAXTYPE, detailValue.get(("TaxType")));
            cv.put(COLUMN_TAXPERC, detailValue.get(("TaxPerc")));
            cv.put(COLUMN_AVERAGECOST, detailValue.get(("AverageCost")));
            cv.put(COLUMN_CATEGORYCODE, detailValue.get(("CategoryCode")));
            cv.put(COLUMN_SUBCATEGORYCODE, detailValue.get(("SubCategoryCode")));
            cv.put(COLUMN_EXCHANGEQTY, detailValue.get(("ExchangeQty")));
            cv.put(COLUMN_CARTONPRICE, detailValue.get(("CartonPrice")));
            cv.put(COLUMN_DOWNLOADSTATUS, detailValue.get("DownloadStatus"));
            cv.put(MODIFY_USER, detailValue.get("ModifyUser"));
            cv.put(MODIFY_DATE, detailValue.get("ModifyDate"));
            cv.put(COLUMN_UOMCODE, detailValue.get("UOMCode"));

            if (invoiceNo != null && !invoiceNo.isEmpty()) {
                String productCode = getInvoiceProduct(invoiceNo, pCode);
                if (productCode != null && !productCode.isEmpty()) {
                    Log.d("InvoiceDetails Save", "Updating...");
                    getDatabase().update(
                            GETINVOICEDETAIL_TABLE,
                            cv,
                            "InvoiceNo = '" + invoiceNo
                                    + "' AND ProductCode = '" + pCode + "'",
                            null);
                } else {
                    cv.put(CREATE_USER, detailValue.get("CreateUser"));
                    cv.put(CREATE_DATE, detailValue.get("CreateDate"));
                    Log.d("InvoiceDetails Save", "Inserting...");
                    getDatabase().insert(GETINVOICEDETAIL_TABLE, null, cv);
                }
            } else {
                cv.put(CREATE_USER, detailValue.get("CreateUser"));
                cv.put(CREATE_DATE, detailValue.get("CreateDate"));
                Log.d("InvoiceDetails Save", "Inserting...");
                getDatabase().insert(GETINVOICEDETAIL_TABLE, null, cv);
            }
        }
    }

    public static String saveInvoiceheader(HashMap<String, String> HeaderValue) {

        String invoiceNo = HeaderValue.get("InvoiceNo");
        String OffInvoiceNo = HeaderValue.get("OfflineInvoiceNo");

        Log.d("InvoiceHeader date", HeaderValue.get("DateTime"));

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_COMPANYCODE, comapanyCode);
        cv.put(COLUMN_SERVERDATEANDTIME, HeaderValue.get("DateTime"));
        cv.put(COLUMN_INVOICENO, HeaderValue.get("OfflineInvoiceNo"));
        cv.put(COLUMN_INVOICEDATE, HeaderValue.get("InvoiceDate"));
        cv.put(COLUMN_LOCATIONCODE, HeaderValue.get("LocationCode"));
        cv.put(COLUMN_CUSTOMERCODE, HeaderValue.get("CustomerCode"));
        cv.put(COLUMN_CUSTOMERNAME, HeaderValue.get("CustomerName"));
        cv.put(COLUMN_TOTAL, HeaderValue.get("Total"));
        cv.put(COLUMN_ITEMDISCOUNT, HeaderValue.get("ItemDiscount"));
        cv.put(COLUMN_BILLDISCOUNT, HeaderValue.get("BillDiscount"));
        cv.put(COLUMN_TOTALDISCOUNT, HeaderValue.get("TotalDiscount"));
        cv.put(COLUMN_SUBTOTAL, HeaderValue.get("SubTotal"));
        cv.put(COLUMN_TAX, HeaderValue.get("Tax"));
        cv.put(COLUMN_NETTOTAL, HeaderValue.get("NetTotal"));
        cv.put(COLUMN_PAIDAMOUNT, HeaderValue.get("PaidAmount"));
        cv.put(COLUMN_CREDITAMOUNT, HeaderValue.get("CreditAmount"));
        cv.put(COLUMN_BALANCEAMOUNT, HeaderValue.get("BalanceAmount"));
        cv.put(COLUMN_REMARKS, HeaderValue.get("Remarks"));
        cv.put(COLUMN_CURRENCYCODE, HeaderValue.get("CurrencyCode"));
        cv.put(COLUMN_CURRENCYRATE, HeaderValue.get("CurrencyRate"));
        cv.put(COLUMN_SONO, HeaderValue.get("SoNo"));
        cv.put(COLUMN_DONO, HeaderValue.get("DoNo"));
        cv.put(COLUMN_TOTALBALANCE, HeaderValue.get("TotalBalance"));
        cv.put(COLUMN_DOWNLOADSTATUS, HeaderValue.get("DownloadStatus"));
        cv.put(MODIFY_USER, HeaderValue.get("ModifyUser"));
        cv.put(MODIFY_DATE, HeaderValue.get("ModifyDate"));

        if (invoiceNo != null && !invoiceNo.isEmpty()) {
            Log.d("InvoiceHeader Save", "Updating...");
            getDatabase().update(GETINVOICEHEADER_TABLE, cv,
                    "InvoiceNo = '" + invoiceNo + "'", null);
        } else {
            cv.put(CREATE_USER, HeaderValue.get("CreateUser"));
            cv.put(CREATE_DATE, HeaderValue.get("CreateDate"));
            Log.d("InvoiceHeader Save", "Inserting...");
            getDatabase().insert(GETINVOICEHEADER_TABLE, null, cv);
        }

        Log.d("offline invoice header", HeaderValue.toString());

        return OffInvoiceNo;

    }

    /* Get CustomerName */
    public String getCustomerName(String customercode) {
        String customername = "";
        String selectQuery = "SELECT CustomerName FROM tblGetCustomer WHERE CompanyCode = '"
                + comapanyCode + "' AND CustomerCode = '" + customercode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                customername = cursor.getString(cursor
                        .getColumnIndex(COLUMN_CUSTOMERNAME));

            } while (cursor.moveToNext());
        }
        return customername;

    }

    public static void saveReceiptdetail(HashMap<String, String> value) {
        Log.d("ReceiptDetails Details", "Inserting...");
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_COMPANYCODE, comapanyCode);
        cv.put(COLUMN_SERVERDATEANDTIME, value.get(("DateTime")));
        cv.put(COLUMN_RECEIPTNO, value.get(("ReceiptNo")));
        cv.put(COLUMN_INVOICENO, value.get(("InvoiceNo")));
        cv.put(COLUMN_NETTOTAL, value.get(("NetTotal")));
        cv.put(COLUMN_PAIDAMOUNT, value.get(("PaidAmount")));
        cv.put(COLUMN_CREDITAMOUNT, value.get(("CreditAmount")));

        cv.put(CREATE_USER, value.get("CreateUser"));
        cv.put(CREATE_DATE, value.get("CreateDate"));
        cv.put(MODIFY_USER, value.get("ModifyUser"));
        cv.put(MODIFY_DATE, value.get("ModifyDate"));
        cv.put(COLUMN_DOWNLOADSTATUS, value.get("DownloadStatus"));

        getDatabase().insert(GETRECEIPTDETAIL_TABLE, null, cv);
    }

    public static String saveReceiptheader(HashMap<String, String> HeaderValue) {

        String receiptNo = HeaderValue.get("ReceiptNo");
        Log.d("ReceiptDetails Header", "Inserting...");
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_COMPANYCODE, comapanyCode);
        cv.put(COLUMN_SERVERDATEANDTIME, HeaderValue.get("DateTime"));
        cv.put(COLUMN_RECEIPTNO, HeaderValue.get("ReceiptNo"));
        cv.put(COLUMN_RECEIPTDATE, HeaderValue.get("ReceiptDate"));
        cv.put(COLUMN_CUSTOMERCODE, HeaderValue.get("CustomerCode"));
        cv.put(COLUMN_CUSTOMERNAME, HeaderValue.get("CustomerName"));
        cv.put(COLUMN_PAIDAMOUNT, HeaderValue.get("PaidAmount"));
        cv.put(COLUMN_CREDITAMOUNT, HeaderValue.get("CreditAmount"));
        cv.put(COLUMN_PAYMODE, HeaderValue.get("Paymode"));
        cv.put(COLUMN_BANKCODE, HeaderValue.get("BankCode"));
        cv.put(COLUMN_CHEQUENO, HeaderValue.get("ChequeNo"));
        cv.put(COLUMN_CHEQUEDATE, HeaderValue.get("ChequeDate"));

        cv.put(CREATE_USER, HeaderValue.get("CreateUser"));
        cv.put(CREATE_DATE, HeaderValue.get("CreateDate"));
        cv.put(MODIFY_USER, HeaderValue.get("ModifyUser"));
        cv.put(MODIFY_DATE, HeaderValue.get("ModifyDate"));
        cv.put(COLUMN_DOWNLOADSTATUS, HeaderValue.get("DownloadStatus"));

        getDatabase().insert(GETRECEIPTEHEADER_TABLE, null, cv);

        return receiptNo;
    }

    public String getInvoiceHeaderCashCollection(String customercode) {
        String result = "";
        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetInvoiceHeader WHERE CompanyCode= '"
                    + comapanyCode + "'";// order by InvoiceDate

            if (customercode != null && !customercode.isEmpty()) {
                selectQuery += " AND CustomerCode = '" + customercode + "'";
            }
            selectQuery += " AND BalanceAmount > '0.00' order by InvoiceDate DESC";

            Log.d("selectQuery", selectQuery);

            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    String invoicedatetime = cursor.getString(cursor
                            .getColumnIndex(COLUMN_INVOICEDATE));
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    Date myDate = dateFormat.parse(invoicedatetime);

                    SimpleDateFormat timeFormat = new SimpleDateFormat(
                            "dd/MM/yyyy hh:mm:ss");
                    String finalDate = timeFormat.format(myDate);
                    StringTokenizer tokens = new StringTokenizer(finalDate, " ");
                    String date = tokens.nextToken();

                    builder.append("{")
                            .append("\"InvoiceNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_INVOICENO)))
                            .append("\",\"InvoiceDate\":\"")
                            .append(date)
                            .append("\",\"NetTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_NETTOTAL)))
                            .append("\",\"CreditAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CREDITAMOUNT)))
                            .append("\",\"BalanceAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BALANCEAMOUNT)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        builder.append("]");

        result = " { JsonArray: " + builder.toString() + "}";
        Log.d("Cash Collection", ":" + result);

        return result;

    }

    public static String getInvoiceDownloadStatus(String invoiceno) {

        String status = "";
        String selectQuery = "SELECT * FROM tblGetInvoiceHeader WHERE InvoiceNo = '"
                + invoiceno + "'" + "AND download_status='1'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                status = cursor.getString(cursor
                        .getColumnIndex("download_status"));

            } while (cursor.moveToNext());
        }
        return status;

    }

    public static ArrayList<String> getproductcodefrominvoiceno(String invoiceno) {

        ArrayList<String> addproduct = new ArrayList<String>();
        String ProductCode = "";
        String selectQuery = "SELECT * FROM tblGetInvoiceDetail WHERE InvoiceNo = '"
                + invoiceno + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ProductCode = cursor.getString(cursor
                        .getColumnIndex("ProductCode"));
                addproduct.add(ProductCode);
            } while (cursor.moveToNext());
        }
        return addproduct;

    }

    public void deleteInvoiceItem(String invoiceNo, String productcode) {

        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM tblGetInvoiceDetail where InvoiceNo ='"
                + invoiceNo + "' AND ProductCode = '" + productcode + "'";
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
        db.close();

    }

    public String getStockValue(String locationcode, String productcode) {
        StringBuilder builder = new StringBuilder("[");
        try {
            String selectQuery = "SELECT * FROM tblGetProductStock WHERE CompanyCode = '"
                    + comapanyCode + "' AND LocationCode = '" + locationcode + "' AND ProductCode = '" + productcode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"CompanyCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_COMPANYCODE)))
                            .append("\",\"LocationCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_LOCATIONCODE)))
                            .append("\",\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))
                            .append("\",\"NoOfCarton\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_NOOFCARTON)))
                            .append("\",\"Qty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_QTY))).append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");

        String result = " { JsonArray: " + builder.toString() + "}";
        Log.d("tblGetProductStock", ":" + result);

        return result;

    }

    public static String getAllUserList() {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblUserMaster";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"UserName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_USERID)))
                            .append("\",\"Password\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PASSWORD)))
                            .append("\",\"UserGroupCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_USERGROUPCODE)))
                            .append("\",\"IsActive\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ISACTIVE)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        String result = " { UserArray : " + builder.toString() + "}";
        Log.d("Offline UserArray", ":" + result);
        return result;
    }

    public String getIReceiptHeader(HashMap<String, String> hm) {

        String selectCustomerCode = "", selectFromDate = "", selectToDate = "", selectUser = "";
        selectCustomerCode = hm.get("CustomerCode");
        selectFromDate = hm.get("FromDate");
        selectToDate = hm.get("ToDate");
        selectUser = hm.get("User");

        StringBuilder builder = new StringBuilder("[");
        try {

            SimpleDateFormat dbDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = dbDateFormat.parse(selectFromDate);
            Date endDate = dbDateFormat.parse(selectToDate);

            SimpleDateFormat regateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String from = regateFormat.format(startDate) + " 00:00:00";
            String to = regateFormat.format(endDate) + " 24:00:00";

            Log.d("fromDate", from);
            Log.d("toDate", to);

            String selectQuery = "SELECT * FROM tblGetReceiptHeader WHERE CompanyCode = '"
                    + comapanyCode + "'";

            if (selectCustomerCode != null && !selectCustomerCode.isEmpty()) {
                selectQuery += " AND CustomerCode = '" + selectCustomerCode
                        + "'";
            }

            if ((selectFromDate != null && !selectFromDate.isEmpty())
                    && (selectToDate != null && !selectToDate.isEmpty())) {
                selectQuery += " AND ReceiptDate BETWEEN '" + from + "' AND '"
                        + to + "'";
            }

            // if (selectUser != null && !selectUser.isEmpty()) {
            // selectQuery += " AND BalanceAmount > '0.00'";
            // }

            selectQuery += " order by ReceiptDate DESC";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    String invoicedatetime = cursor.getString(cursor
                            .getColumnIndex(COLUMN_RECEIPTDATE));
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    Date myDate = dateFormat.parse(invoicedatetime);

                    SimpleDateFormat timeFormat = new SimpleDateFormat(
                            "dd/MM/yyyy hh:mm:ss");
                    String finalDate = timeFormat.format(myDate);
                    StringTokenizer tokens = new StringTokenizer(finalDate, " ");
                    String date = tokens.nextToken();

                    builder.append("{")
                            .append("\"ReceiptNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_RECEIPTNO)))
                            .append("\",\"ReceiptDate\":\"")
                            .append(date)
                            .append("\",\"CustomerCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERCODE)))
                            .append("\",\"CustomerName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERNAME)))
                            .append("\",\"PaidAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PAIDAMOUNT)))
                            .append("\",\"CreditAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CREDITAMOUNT)))
                            .append("\",\"Paymode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PAYMODE)))
                            .append("\",\"BankCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BANKCODE)))
                            .append("\",\"ChequeNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CHEQUENO)))
                            .append("\",\"ChequeDate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CHEQUEDATE)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        builder.append("]");
        String result = " { ReceiptHeader : " + builder.toString() + "}";
        Log.d("Offline ReceiptHeader", ":" + result);
        return result;
    }

    public static ArrayList<String> getCustomerPrice(String customergroupcode,
                                                     String prodcode) {

        ArrayList<String> price = new ArrayList<String>();
        String cartonprice = "", retailprice = "";
        String selectQuery = "SELECT * FROM tblGetCustomerPrice WHERE CompanyCode = '"
                + comapanyCode
                + "' AND CustomerGroupCode = '"
                + customergroupcode
                + "'"
                + " AND ProductCode = '"
                + prodcode
                + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        Log.d("price count", "-->" + cursor.getCount());
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    cartonprice = cursor.getString(cursor
                            .getColumnIndex("CartonPrice"));
                    retailprice = cursor.getString(cursor
                            .getColumnIndex("RetailPrice"));

                    if (retailprice.matches("null") || retailprice.matches("")
                            || retailprice.matches("0.0")
                            || retailprice.matches("0.00")) {
                        price.add("0");
                    } else {
                        price.add(retailprice);
                    }

                    if (cartonprice.matches("null") || cartonprice.matches("")
                            || cartonprice.matches("0.0")
                            || cartonprice.matches("0.00")) {
                        price.add("0");
                    } else {
                        price.add(cartonprice);
                    }

                } while (cursor.moveToNext());
            }
        } else {
            if (price.isEmpty()) {
                price.add("0");
                price.add("0");
            }
        }

        return price;
    }

    public static Cursor getCursorForUploadHeader(String tablename) {

        String selectQuery = "SELECT * FROM " + tablename + " WHERE CompanyCode = '" + comapanyCode + "' AND download_status = '1'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        return cursor;
    }

    public static Cursor getCursorForUploadDetails(String tablename,
                                                   HashMap<String, String> hm) {
        String columnName = "", columnValue = "";
        columnName = hm.get("ColumnName");
        columnValue = hm.get("ColumnValue");

        String selectQuery = "SELECT * FROM " + tablename + " WHERE CompanyCode = '" + comapanyCode + "' AND " + columnName + " = '" + columnValue + "' AND download_status = '1'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        return cursor;
    }

    public static void getCustomerUpdateStaus(ArrayList<String> hm) {

        for (int i = 0; i < hm.size(); i++) {

            String columnName = "", columnValue = "";
            columnName = "CustomerCode";
            columnValue = hm.get(i);

            String where = "CompanyCode = '" + comapanyCode + "' AND " + columnName + " = '" + columnValue + "'";

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_DOWNLOADSTATUS, "0");
            getDatabase().update("tblGetCustomer", cv, where, null);

        }


    }

    public static int getCursorForUpdateStaus(String tablename,
                                              HashMap<String, String> hm) {
        String columnName = "", columnValue = "";
        columnName = hm.get("ColumnName");
        columnValue = hm.get("ColumnValue");

        String where = "CompanyCode = '" + comapanyCode + "' AND " + columnName + " = '" + columnValue + "'";

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DOWNLOADSTATUS, "0");
        return getDatabase().update(tablename, cv, where, null);

    }

    public static String getInvoiceNo(String invoiceNo) {
        String invoiceno = "";
        String selectQuery = "SELECT * FROM tblGetInvoiceHeader  WHERE InvoiceNo = '"
                + invoiceNo + "' And CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                invoiceno = cursor
                        .getString(cursor.getColumnIndex("InvoiceNo"));

            } while (cursor.moveToNext());
        }

        return invoiceno;
    }

    public static String getInvoiceProduct(String invoiceNo, String pcode) {
        String productcode = "";
        String selectQuery = "SELECT ProductCode FROM tblGetInvoiceDetail WHERE InvoiceNo = '"
                + invoiceNo
                + "' AND ProductCode = '"
                + pcode + "'"
                + " AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                productcode = cursor.getString(cursor
                        .getColumnIndex("ProductCode"));

            } while (cursor.moveToNext());
        }

        return productcode;
    }

    public String getInvoiceHeaderDetail(HashMap<String, String> hashmap) {
        String result = "";
        String companyCode = hashmap.get("CompanyCode");
        String invoiceNo = hashmap.get("InvoiceNo");

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetInvoiceDetail  WHERE CompanyCode = '"
                    + companyCode + "' AND InvoiceNo = '" + invoiceNo + "'";


            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"slNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SLNO)))


                            .append("\",\"InvoiceNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_INVOICENO)))

                            .append("\",\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))

                            .append("\",\"ProductName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTNAME)))

                            .append("\",\"Qty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_QTY)))

                            .append("\",\"Price\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRICE)))

                            .append("\",\"UOMCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_UOMCODE)))

                            .append("\",\"Total\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TOTAL)))

                            .append("\",\"IssueQty\":\"")
                            .append("")

                            .append("\",\"ReturnQty\":\"")
                            .append("")

                            .append("\",\"FOCQty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_FOCQTY)))

                            .append("\",\"ExchangeQty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_EXCHANGEQTY)))

                            .append("\",\"CategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CATEGORYCODE)))

                            .append("\",\"SubCategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBCATEGORYCODE)))

                            .append("\",\"Tax\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAX)))

                            .append("\",\"SubTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBTOTAL)))

                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("Invoice Detail", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public String getInvoiceDetailPrint(HashMap<String, String> hashmap) {
        String result = "";
        String companyCode = hashmap.get("CompanyCode");
        String invoiceNo = hashmap.get("InvoiceNo");

        StringBuilder builder = new StringBuilder("[");
        try {

//			String selectQuery = "SELECT * FROM tblGetInvoiceDetail  WHERE CompanyCode = '"
//					+ companyCode + "' AND InvoiceNo = '" + invoiceNo + "'";
            String selectQuery = "SELECT 'Ctn' As TranType,* FROM tblGetInvoiceDetail WHERE CompanyCode ='" + companyCode + "' AND InvoiceNo = '" + invoiceNo + "' AND CQty>0 "
                    + "UNION ALL "
                    + "SELECT 'Loose' As TranType,* FROM tblGetInvoiceDetail WHERE CompanyCode ='" + companyCode + "' AND InvoiceNo = '" + invoiceNo + "' AND Lqty>0 "
                    + "UNION ALL "
                    + "SELECT 'FOC' As TranType,* FROM tblGetInvoiceDetail WHERE CompanyCode ='" + companyCode + "' AND InvoiceNo = '" + invoiceNo + "' AND FOCQty>0 "
                    + "UNION ALL "
                    + "SELECT 'Exc' As TranType,* FROM tblGetInvoiceDetail WHERE CompanyCode ='" + companyCode + "' AND InvoiceNo = '" + invoiceNo + "' AND ExchangeQty>0";


            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"TranType\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex("TranType")))

                            .append("\",\"slNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SLNO)))

                            .append("\",\"InvoiceNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_INVOICENO)))

                            .append("\",\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))

                            .append("\",\"ProductName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTNAME)))

                            .append("\",\"CQty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CQTY)))

                            .append("\",\"CartonPrice\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CARTONPRICE)))

                            .append("\",\"LQty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_LQTY)))

                            .append("\",\"Price\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRICE)))

                            .append("\",\"Qty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_QTY)))

                            .append("\",\"Total\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TOTAL)))

                            .append("\",\"UOMCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_UOMCODE)))

                            .append("\",\"IssueQty\":\"")
                            .append("")

                            .append("\",\"ReturnQty\":\"")
                            .append("")

                            .append("\",\"FOCQty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_FOCQTY)))

                            .append("\",\"ExchangeQty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_EXCHANGEQTY)))

                            .append("\",\"CategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CATEGORYCODE)))

                            .append("\",\"SubCategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBCATEGORYCODE)))

                            .append("\",\"Tax\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAX)))

                            .append("\",\"SubTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBTOTAL)))

                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("Invoice Detail", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


    public String getInvoiceHeader(HashMap<String, String> hashmap) {
        String result = "";
        String companyCode = hashmap.get("CompanyCode");
        String invoiceNo = hashmap.get("InvoiceNo");

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetInvoiceHeader  WHERE CompanyCode = '"
                    + companyCode + "' AND InvoiceNo = '" + invoiceNo + "'";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"InvoiceNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_INVOICENO)))

                            .append("\",\"InvoiceDate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_INVOICEDATE)).split("\\ ")[0])

                            .append("\",\"ItemDiscount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ITEMDISCOUNT)))

                            .append("\",\"BillDIscount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BILLDISCOUNT)))

                            .append("\",\"SubTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBTOTAL)))

                            .append("\",\"Tax\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAX)))

                            .append("\",\"NetTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_NETTOTAL)))

                            .append("\",\"PaidAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PAIDAMOUNT)))

                            .append("\",\"CreditAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CREDITAMOUNT)))

                            .append("\",\"Remarks\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_REMARKS)))


                            .append("\",\"TotalBalance\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TOTALBALANCE)))

                            .append("\",\"BalanceAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BALANCEAMOUNT)))

                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("Invoice Header ", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;

    }

    public String getCustomerDetail(HashMap<String, String> hashmap) {
        String result = "";

        String companyCode = hashmap.get("CompanyCode");
        String customerCode = hashmap.get("CustomerCode");

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetCustomer  WHERE CompanyCode = '"
                    + companyCode + "' AND CustomerCode = '" + customerCode + "'";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"CustomerCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERCODE)))

                            .append("\",\"CustomerName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERNAME)))

                            .append("\",\"Address1\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ADDRESS1)))

                            .append("\",\"Address2\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ADDRESS2)))

                            .append("\",\"Address3\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ADDRESS3)))

                            .append("\",\"PhoneNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PHONENO)))

                            .append("\",\"HandphoneNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_HANDPHONENO)))

                            .append("\",\"Email\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_EMAIL)))
                            .append("\",\"AreaCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_AREACODE)))
                            .append("\",\"VanCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_VANCODE)))
                            .append("\",\"TermName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TERMCODE)))

                            .append("\",\"OutstandingAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_OUTSTANDINGAMOUNT)))
                            .append("\",\"ReferenceLocation\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_REFERENCELOCATION)))

                            .append("\",\"TaxValue\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAXVALUE)))

                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("Customer Detail", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }
//	public static String getInvoicePrinterStatus(String tblName,String String invoiceNo) {
//		String invoiceno = "";
//		String selectQuery = "SELECT * FROM  "+tblName+" WHERE InvoiceNo = '"
//				+ invoiceNo + "' And CompanyCode = '" + comapanyCode + "'";
//		Log.d("selectQuery", selectQuery);
//		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
//		if (cursor.moveToFirst()) {
//			do {
//				invoiceno = cursor
//						.getString(cursor.getColumnIndex("InvoiceNo"));
//
//			} while (cursor.moveToNext());
//		}
//
//		return invoiceno;
//	}

    public static String getPrintStatus(String tblName, String columnName, String num) {
        String no = "";
        String selectQuery = "SELECT * FROM  " + tblName + " WHERE " + columnName + " = '"
                + num + "' And CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                no = cursor
                        .getString(cursor.getColumnIndex(columnName));

            } while (cursor.moveToNext());
        }

        return no;
    }

    public String getReceiptDetail(HashMap<String, String> hashmap) {
        String result = "";

        String companyCode = hashmap.get("CompanyCode");
        String receiptNo = hashmap.get("ReceiptNo");

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetReceiptDetail  WHERE CompanyCode = '"
                    + companyCode + "' AND ReceiptNo = '" + receiptNo + "'";


            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"ReceiptNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_RECEIPTNO)))

                            .append("\",\"InvoiceNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_INVOICENO)))

                            .append("\",\"NetTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_NETTOTAL)))

                            .append("\",\"PaidAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PAIDAMOUNT)))

                            .append("\",\"CreditAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CREDITAMOUNT)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("Receipt Detail ", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;

    }

    public String getReceiptHeader(HashMap<String, String> hashmap) {
        String result = "";

        String companyCode = hashmap.get("CompanyCode");
        String receiptNo = hashmap.get("ReceiptNo");
        String fromDate = hashmap.get("FromDate");
        String toDate = hashmap.get("ToDate");
        String user = hashmap.get("User");

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetReceiptHeader WHERE CompanyCode = '"
                    + companyCode /*+ "' AND ReceiptNo = '" + receiptNo*/ + "'";

            if (receiptNo != null && !receiptNo.isEmpty()) {
                selectQuery += " AND ReceiptNo = '" + receiptNo + "'";
            }


            if ((fromDate != null && !fromDate.isEmpty())
                    && (toDate != null && !toDate.isEmpty())) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate = dateFormat.parse(fromDate);
                Date endDate = dateFormat.parse(toDate);
                SimpleDateFormat regateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String from = regateFormat.format(startDate) + " 00:00:00";
                String to = regateFormat.format(endDate) + " 24:00:00";

                selectQuery += " AND ReceiptDate BETWEEN '" + from
                        + "' AND '" + to + "'";
            }
            if (user != null && !user.isEmpty()) {
                selectQuery += " AND CreateUser = '" + user + "'";
            }


            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"Paymode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PAYMODE)))

                            .append("\",\"BankCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BANKCODE)))

                            .append("\",\"ReceiptNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_RECEIPTNO)))

                            .append("\",\"ReceiptDate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_RECEIPTDATE)))

                            .append("\",\"CustomerCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERCODE)))

                            .append("\",\"CustomerName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERNAME)))

                            .append("\",\"PaidAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PAIDAMOUNT)))

                            .append("\",\"CreditAmount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CREDITAMOUNT)))

                            .append("\",\"ChequeNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CHEQUENO)))

                            .append("\",\"ChequeDate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CHEQUEDATE)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("Receipt Header ", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;

    }

    public static String getLastLoginLocation(String username, String password) {
        String result = "";

        String selectQuery = "SELECT * FROM tblUserMaster WHERE UserName '" + username + "'  And Password = '" + password + /*"' And CompanyCode = '" + comapanyCode +*/ "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                result = cursor
                        .getString(cursor.getColumnIndex(COLUMN_LASTLOGINLOCATION));

            } while (cursor.moveToNext());
        }


        return result;
    }

    public static String getHeaderDataStatus(String tableName, String columnName, String value) {
        String result = "";

        String selectQuery = "SELECT * FROM " + tableName + " WHERE CompanyCode = '"
                + comapanyCode + "' AND " + columnName + "= '" + value + "'";

        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                result = cursor
                        .getString(cursor.getColumnIndex(COLUMN_DOWNLOADSTATUS));

            } while (cursor.moveToNext());
        }


        return result;
    }


    public static String getHeaderData(HashMap<String, String> hashmap) {
        String result = "", columnName = "";
        String tableName = hashmap.get("TableName");
        String ColumnNo = hashmap.get("No");
        String ColumnDate = hashmap.get("Date");
        String fromDate = hashmap.get("FromDate");
        String toDate = hashmap.get("ToDate");
        String downloadStatus = hashmap.get("DownStatus");
        String flag = hashmap.get("Flag");

        if (flag.matches("Invoice") || flag.matches("SalesOrder")) {
            columnName = "NetTotal";
        } else if (flag.matches("Receipt")) {
            columnName = "PaidAmount";
        }

        Log.d("tableName", "-->" + tableName);
        Log.d("ColumnNo", "-->" + ColumnNo);
        Log.d("ColumnDate", "-->" + ColumnDate);
        Log.d("downloadStatus", "-->" + downloadStatus);
        Log.d("flag", "-->" + flag);


        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM " + tableName + " WHERE CompanyCode = '"
                    + comapanyCode + "'";


            if (downloadStatus != null && !downloadStatus.isEmpty()) {
                selectQuery += " AND download_status = '" + downloadStatus + "'";
            }
            if ((fromDate != null && !fromDate.isEmpty())
                    && (toDate != null && !toDate.isEmpty())) {

                selectQuery += " AND " + ColumnDate + " BETWEEN '" + fromDate
                        + "' AND '" + toDate + "'";
            }

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    if (flag.matches("Customer")) {

                        builder.append("{")
                                .append("\"CustomerCode\":\"")
                                .append(cursor.getString(cursor
                                        .getColumnIndex(COLUMN_CUSTOMERCODE)))
                                .append("\",\"CustomerName\":\"")
                                .append(cursor.getString(cursor
                                        .getColumnIndex(COLUMN_CUSTOMERNAME)))
                                .append("\"}");
                    } else if (flag.matches("Products")) {

                        String productname = cursor.getString(cursor
                                .getColumnIndex(COLUMN_PRODUCTNAME));
                        productname = productname.replace("\"", "\\\"");
                        builder.append("{")
                                .append("\"ProductCode\":\"")
                                .append(cursor.getString(cursor
                                        .getColumnIndex(COLUMN_PRODUCTCODE)))
                                .append("\",\"ProductName\":\"")
                                .append(productname)
                                .append("\"}");
                    } else {

                        builder.append("{")
                                .append("\"No\":\"")
                                .append(cursor.getString(cursor
                                        .getColumnIndex(ColumnNo)))
                                .append("\",\"Date\":\"")
                                .append(cursor.getString(
                                        cursor.getColumnIndex(ColumnDate))
                                        .split("\\ ")[0])
                                .append("\",\"CustomerCode\":\"")
                                .append(cursor.getString(cursor
                                        .getColumnIndex(COLUMN_CUSTOMERCODE)))
                                .append("\",\"CustomerName\":\"")
                                .append(cursor.getString(cursor
                                        .getColumnIndex(COLUMN_CUSTOMERNAME)))
                                .append("\",\"NetTotal\":\"")
                                .append(cursor.getString(cursor
                                        .getColumnIndex(columnName)))
                                .append("\"}");

                    }

                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("Invoice Header ", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;

    }
//	public  String getReceiptHeader(HashMap<String, String> hashmap) {
//		String result = "";
//
//		String companyCode = hashmap.get("CompanyCode");
//		String receiptNo  = hashmap.get("ReceiptNo");
//
//		StringBuilder builder = new StringBuilder("[");
//		try {
//
//			String selectQuery = "SELECT * FROM tblGetReceiptHeader  WHERE CompanyCode = '"
//					+ companyCode + "' AND ReceiptNo = '" + receiptNo + "'";
//
//			Log.d("selectQuery", selectQuery);
//			Cursor cursor = getDatabase().rawQuery(selectQuery, null);
//			if (cursor.moveToFirst()) {
//				do {
//					builder.append("{")
//
//							.append("\"Paymode\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_PAIDA)))
//
//							.append("\",\"BankCode\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_BANKCODE)))
//
//							.append("\",\"ChequeNo\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_CHEQUENO)))
//
//							.append("\",\"ChequeDate\":\"")
//							.append(cursor.getString(cursor
//									.getColumnIndex(COLUMN_CHEQUEDATE)))
//							.append("\"}");
//					if (!cursor.isLast()) {
//						builder.append(",");
//					}
//				} while (cursor.moveToNext());
//			}
//			builder.append("]");
//			 result = " { SODetails: " + builder.toString() + "}";
//			Log.d("Receipt Header ", ":" + result);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//
//
//
//
//		return result;
//
//	}

    //Added

    /* Get Van */
    public static ArrayList<HashMap<String, String>> getVan() {

        ArrayList<HashMap<String, String>> vanList = new ArrayList<HashMap<String, String>>();
        vanList.clear();
        String selectQuery = "SELECT Code,Description FROM tblGetVan WHERE CompanyCode = '" + comapanyCode + "'";
        Cursor cursor = null;
        cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String Code = cursor.getString(cursor
                        .getColumnIndex("Code"));
                String Description = cursor.getString(cursor
                        .getColumnIndex("Description"));

                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put(Code, Description);
                vanList.add(hm);

            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return vanList;
    }


    /**
     * SO START
     **/

    public static String saveSOheader(HashMap<String, String> HeaderValue) {

        String soNo = HeaderValue.get("SoNo");
        String OfflineSoNo = HeaderValue.get("OfflineSoNo");

        Log.d("SOHeader date", HeaderValue.get("DateTime"));

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_COMPANYCODE, comapanyCode);
        cv.put(COLUMN_SERVERDATEANDTIME, HeaderValue.get(("DateTime")));
        cv.put(COLUMN_SONO, OfflineSoNo);
        cv.put(COLUMN_SODATE, HeaderValue.get("SoDate"));
        cv.put(COLUMN_DELIVERYDATE, HeaderValue.get("DeliveryDate"));
        cv.put(COLUMN_LOCATIONCODE, HeaderValue.get("LocationCode"));
        cv.put(COLUMN_CUSTOMERCODE, HeaderValue.get("CustomerCode"));
        cv.put(COLUMN_CUSTOMERNAME, HeaderValue.get("CustomerName"));
        cv.put(COLUMN_TOTAL, HeaderValue.get("Total"));
        cv.put(COLUMN_ITEMDISCOUNT, HeaderValue.get("ItemDiscount"));
        cv.put(COLUMN_BILLDISCOUNT, HeaderValue.get("BillDiscount"));
        cv.put(COLUMN_TOTALDISCOUNT, HeaderValue.get("TotalDiscount"));
        cv.put(COLUMN_SUBTOTAL, HeaderValue.get("SubTotal"));
        cv.put(COLUMN_TAX, HeaderValue.get("Tax"));
        cv.put(COLUMN_NETTOTAL, HeaderValue.get("NetTotal"));
        cv.put(COLUMN_REMARKS, HeaderValue.get("Remarks"));
        cv.put(COLUMN_STATUS, HeaderValue.get("Status"));
        cv.put(COLUMN_CURRENCYCODE, HeaderValue.get("CurrencyCode"));
        cv.put(COLUMN_CURRENCYRATE, HeaderValue.get("CurrencyRate"));
        cv.put(COLUMN_DOWNLOADSTATUS, HeaderValue.get("DownloadStatus"));
        cv.put(MODIFY_USER, HeaderValue.get("ModifyUser"));
        cv.put(MODIFY_DATE, HeaderValue.get("ModifyDate"));

        if (soNo != null && !soNo.isEmpty()) {
            Log.d("Header Save", "Updating...");
            getDatabase().update(GETSOHEADER_TABLE, cv, "SoNo = '" + soNo + "'", null);
        } else {
            cv.put(CREATE_USER, HeaderValue.get("CreateUser"));
            cv.put(CREATE_DATE, HeaderValue.get("CreateDate"));
            Log.d("Header Save", "Inserting...");
            getDatabase().insert(GETSOHEADER_TABLE, null, cv);
        }

        Log.d("offline Header", HeaderValue.toString());

        return OfflineSoNo;

    }

    /* Update SoHeader Table */
    public static void update_soheader(HashMap<String, String> HeaderValue) {
        ContentValues cv = new ContentValues();
        String sono = HeaderValue.get(("SoNo"));
        cv.put(COLUMN_COMPANYCODE, comapanyCode);
        cv.put(COLUMN_SERVERDATEANDTIME, HeaderValue.get(("DateTime")));
        cv.put(COLUMN_SONO, HeaderValue.get("SoNo"));
        cv.put(COLUMN_SODATE, HeaderValue.get("SoDate"));
        cv.put(COLUMN_DELIVERYDATE, HeaderValue.get("DeliveryDate"));
        cv.put(COLUMN_LOCATIONCODE, HeaderValue.get("LocationCode"));
        cv.put(COLUMN_CUSTOMERCODE, HeaderValue.get("CustomerCode"));
        cv.put(COLUMN_CUSTOMERNAME, HeaderValue.get("CustomerName"));
        cv.put(COLUMN_TOTAL, HeaderValue.get("Total"));
        cv.put(COLUMN_ITEMDISCOUNT, HeaderValue.get("ItemDiscount"));
        cv.put(COLUMN_BILLDISCOUNT, HeaderValue.get("BillDIscount"));
        cv.put(COLUMN_TOTALDISCOUNT, HeaderValue.get("TotalDiscount"));
        cv.put(COLUMN_SUBTOTAL, HeaderValue.get("SubTotal"));
        cv.put(COLUMN_TAX, HeaderValue.get("Tax"));
        cv.put(COLUMN_NETTOTAL, HeaderValue.get("NetTotal"));
        cv.put(COLUMN_REMARKS, HeaderValue.get("Remarks"));
        cv.put(COLUMN_STATUS, HeaderValue.get("Status"));
        cv.put(COLUMN_CURRENCYCODE, HeaderValue.get("CurrencyCode"));
        cv.put(COLUMN_CURRENCYRATE, HeaderValue.get("CurrencyRate"));

        getDatabase().update(GETSOHEADER_TABLE, cv, "SoNo = '" + sono + "'", null);
    }

    /* get SoHeader Table */
    public static ArrayList<SO> getSOHeaderList(HashMap<String, String> hm) {
        double totalamount = 0.00;
        ArrayList<SO> soheaderlist = new ArrayList<SO>();
        soheaderlist.clear();
        String selectCustomerCode = "", selectFromDate = "", selectToDate = "", selectStatus = "";
        selectCustomerCode = hm.get("CustomerCode");
        selectFromDate = hm.get("FromDate");
        selectToDate = hm.get("ToDate");
        selectStatus = hm.get("Status");

        try {

            String selectQuery = "SELECT * FROM tblGetSOHeader WHERE CompanyCode= '"
                    + comapanyCode + "'";// order by InvoiceDate

            if (selectCustomerCode != null && !selectCustomerCode.isEmpty()) {
                selectQuery += " AND CustomerCode = '" + selectCustomerCode
                        + "'";
            }

            if ((selectFromDate != null && !selectFromDate.isEmpty())
                    && (selectToDate != null && !selectToDate.isEmpty())) {
                selectQuery += " AND SoDate BETWEEN '" + selectFromDate
                        + "' AND '" + selectToDate + "'";
            }

            if (selectStatus.matches("5")) {
                //show all
            } else {
                selectQuery += " AND Status ='" + selectStatus + "'";
            }

            selectQuery += " order by SoDate DESC";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    String ccSno = cursor.getString(cursor.getColumnIndex(COLUMN_SONO));
//							String ccDate = cursor.getString(cursor.getColumnIndex(COLUMN_SODATE));
                    String ccDate = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERYDATE));
                    String finalDate = "";
                    if (ccDate != null && !ccDate.isEmpty()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date myDate = dateFormat.parse(ccDate);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
                        finalDate = timeFormat.format(myDate);
                    }

                    String customerCode = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMERCODE));
                    String customerName = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMERNAME));
                    String amount = cursor.getString(cursor.getColumnIndex(COLUMN_NETTOTAL));
                    String status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));


                    SO so = new SO();
                    so.setSno(ccSno);
                    so.setDate(finalDate);
                    so.setCustomerCode(customerCode);
                    so.setCustomerName(customerName);
                    so.setNettotal(amount);

                    if (status.matches("0")) {
                        so.setStatus("open");
                    } else if (status.matches("1")) {
                        so.setStatus("InProgress Invoice");
                    } else if (status.matches("2")) {
                        so.setStatus("InProgress DO");
                    } else if (status.matches("3")) {
                        so.setStatus("closed");
                    }

                    soheaderlist.add(so);
                } while (cursor.moveToNext());
            }
            SO.setTotalamount(totalamount);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return soheaderlist;

    }

    public static String getSOProduct(String soNo, String pcode) {
        String productcode = "";
        String selectQuery = "SELECT ProductCode FROM tblGetSODetail WHERE SoNo = '" + soNo
                + "' AND ProductCode = '" + pcode + "'" + " AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                productcode = cursor.getString(cursor.getColumnIndex("ProductCode"));
            } while (cursor.moveToNext());
        }

        return productcode;
    }

    /* Store SODetail Table */
    public static void storeSODetail(ArrayList<HashMap<String, String>> soDetails) {

        for (int i = 0; i < soDetails.size(); i++) {

            HashMap<String, String> detailValue = soDetails.get(i);
            String soNo = detailValue.get("SoNo");
            String OffSONo = detailValue.get("OfflineSoNo");
            String pCode = detailValue.get("ProductCode");
            Log.d("detail comapanyCode", "aa" + comapanyCode);

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_COMPANYCODE, comapanyCode);
            cv.put(COLUMN_SERVERDATEANDTIME, detailValue.get(("DateTime")));
            cv.put(COLUMN_SONO, detailValue.get(("OfflineSoNo")));
            cv.put(COLUMN_SLNO, detailValue.get(("slNo")));
            cv.put(COLUMN_PRODUCTCODE, detailValue.get(("ProductCode")));
            cv.put(COLUMN_PRODUCTNAME, detailValue.get(("ProductName")));
            cv.put(COLUMN_CQTY, detailValue.get(("CQty")));
            cv.put(COLUMN_LQTY, detailValue.get(("Lqty")));
            cv.put(COLUMN_QTY, detailValue.get(("Qty")));
            cv.put(COLUMN_FOCQTY, detailValue.get(("FOCQty")));
            cv.put(COLUMN_PCSPERCARTON, detailValue.get(("PcsPerCarton")));
            cv.put(COLUMN_RETAILPRICE, detailValue.get(("RetailPrice")));
            cv.put(COLUMN_PRICE, detailValue.get(("Price")));
            cv.put(COLUMN_TOTAL, detailValue.get(("Total")));
            cv.put(COLUMN_ITEMDISCOUNT, detailValue.get(("ItemDiscount")));
            cv.put(COLUMN_BILLDISCOUNT, detailValue.get(("BillDiscount")));
            cv.put(COLUMN_TOTALDISCOUNT, detailValue.get(("TotalDiscount")));
            cv.put(COLUMN_SUBTOTAL, detailValue.get(("SubTotal")));
            cv.put(COLUMN_TAX, detailValue.get(("Tax")));
            cv.put(COLUMN_NETTOTAL, detailValue.get(("NetTotal")));
            cv.put(COLUMN_TAXTYPE, detailValue.get(("TaxType")));
            cv.put(COLUMN_TAXPERC, detailValue.get(("TaxPerc")));

            cv.put(COLUMN_DOQTY, detailValue.get(("DOQty")));
            cv.put(COLUMN_DOFOCQTY, detailValue.get(("DOFocQty")));
            cv.put(COLUMN_INVOICEQTY, detailValue.get(("InvoiceQty")));
            cv.put(COLUMN_INVOICEFOCQTY, detailValue.get(("InvoiceFocQty")));

            cv.put(COLUMN_EXCHANGEQTY, detailValue.get(("ExchangeQty")));
            cv.put(COLUMN_CARTONPRICE, detailValue.get(("CartonPrice")));
            cv.put(COLUMN_ITEMREMARKS, detailValue.get(("ItemRemarks")));
            cv.put(COLUMN_DOWNLOADSTATUS, detailValue.get("DownloadStatus"));
            cv.put(MODIFY_USER, detailValue.get("ModifyUser"));
            cv.put(MODIFY_DATE, detailValue.get("ModifyDate"));

            if (soNo != null && !soNo.isEmpty()) {
                String productCode = getSOProduct(soNo, pCode);
                if (productCode != null && !productCode.isEmpty()) {
                    Log.d("SODetails Save", "Updating...");
                    getDatabase().update(GETSODETAIL_TABLE, cv, "SoNo = '" + soNo + "' AND ProductCode = '" + pCode + "'", null);
                } else {
                    cv.put(CREATE_USER, detailValue.get("CreateUser"));
                    cv.put(CREATE_DATE, detailValue.get("CreateDate"));
                    Log.d("SODetails Save", "Inserting...");
                    getDatabase().insert(GETSODETAIL_TABLE, null, cv);
                }
            } else {
                cv.put(CREATE_USER, detailValue.get("CreateUser"));
                cv.put(CREATE_DATE, detailValue.get("CreateDate"));
                Log.d("SODetails Save", "Inserting...");
                getDatabase().insert(GETSODETAIL_TABLE, null, cv);
            }
        }
    }

    /* Get SOHeader From tblGetSOHeader CursorTable */
    public ArrayList<HashMap<String, String>> getSOHeader(String SONo) {
        ArrayList<HashMap<String, String>> SO_DetailsArr = new ArrayList<HashMap<String, String>>();
        SO_DetailsArr.clear();

        String selectQuery = "SELECT * FROM tblGetSOHeader WHERE SoNo = '"
                + SONo + "' AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String SoNo = cursor.getString(cursor.getColumnIndex(COLUMN_SONO));
                String SoDate = cursor.getString(cursor.getColumnIndex(COLUMN_SODATE));
                String DeliveryDate = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERYDATE));
                String LocationCode = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATIONCODE));
                String CustomerCode = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMERCODE));
                String Total = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL));
                String ItemDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_ITEMDISCOUNT));

                String BillDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_BILLDISCOUNT));
                String TotalDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_TOTALDISCOUNT));
                String SubTotal = cursor.getString(cursor.getColumnIndex(COLUMN_SUBTOTAL));
                String Tax = cursor.getString(cursor.getColumnIndex(COLUMN_TAX));
                String NetTotal = cursor.getString(cursor.getColumnIndex(COLUMN_NETTOTAL));
                String Remarks = cursor.getString(cursor.getColumnIndex(COLUMN_REMARKS));
                String Status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));

                String CurrencyCode = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCYCODE));
                String CurrencyRate = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCYRATE));
                String vancode = cursor.getString(cursor.getColumnIndex(COLUMN_VANCODE));

                String so_date = "", do_date = "";
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date mDate = dateFormat.parse(SoDate);
                    Date nDate = dateFormat.parse(DeliveryDate);
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");

                    so_date = timeFormat.format(mDate);
                    do_date = timeFormat.format(nDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d("getSOHeader ", "so_date" + so_date);
                Log.d("getSOHeader ", "do_date" + do_date);


                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("SoNo", SoNo);
                hm.put("SoDate", so_date);
                hm.put("DeliveryDate", do_date);
                hm.put("LocationCode", LocationCode);
                hm.put("CustomerCode", CustomerCode);
                hm.put("Total", Total);
                hm.put("ItemDiscount", ItemDiscount);
                hm.put("BillDIscount", BillDiscount);
                hm.put("TotalDiscount", TotalDiscount);
                hm.put("SubTotal", SubTotal);
                hm.put("Tax", Tax);
                hm.put("NetTotal", NetTotal);
                hm.put("Remarks", Remarks);
                hm.put("Status", Status);
                hm.put("CurrencyCode", CurrencyCode);
                hm.put("CurrencyRate", CurrencyRate);
                hm.put("VanCode", vancode);


                SO_DetailsArr.add(hm);

            } while (cursor.moveToNext());
        }
        return SO_DetailsArr;
    }

    /* Get SODetail From tblGetSODetail CursorTable */
    public ArrayList<HashMap<String, String>> getSODetails(String SONo) {
        ArrayList<HashMap<String, String>> SO_DetailsArr = new ArrayList<HashMap<String, String>>();
        SO_DetailsArr.clear();

        String selectQuery = "SELECT * FROM tblGetSODetail WHERE SoNo = '"
                + SONo + "' AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String slNo = cursor.getString(cursor.getColumnIndex(COLUMN_SLNO));
                String ProductCode = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTCODE));
                String ProductName = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTNAME));
                String CQty = cursor.getString(cursor.getColumnIndex(COLUMN_CQTY));
                String LQty = cursor.getString(cursor.getColumnIndex(COLUMN_LQTY));
                String Qty = cursor.getString(cursor.getColumnIndex(COLUMN_QTY));
                String FOCQty = cursor.getString(cursor.getColumnIndex(COLUMN_FOCQTY));
                String PcsPerCarton = cursor.getString(cursor.getColumnIndex(COLUMN_PCSPERCARTON));
                String RetailPrice = cursor.getString(cursor.getColumnIndex(COLUMN_RETAILPRICE));
                String Price = cursor.getString(cursor.getColumnIndex(COLUMN_PRICE));
                String Total = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL));
                String ItemDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_ITEMDISCOUNT));

                String BillDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_BILLDISCOUNT));
                String TotalDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_TOTALDISCOUNT));
                String SubTotal = cursor.getString(cursor.getColumnIndex(COLUMN_SUBTOTAL));
                String Tax = cursor.getString(cursor.getColumnIndex(COLUMN_TAX));
                String NetTotal = cursor.getString(cursor.getColumnIndex(COLUMN_NETTOTAL));
                String TaxType = cursor.getString(cursor.getColumnIndex(COLUMN_TAXTYPE));
                String TaxPerc = cursor.getString(cursor.getColumnIndex(COLUMN_TAXPERC));

                String ExchangeQty = cursor.getString(cursor.getColumnIndex(COLUMN_EXCHANGEQTY));
                String CartonPrice = cursor.getString(cursor.getColumnIndex(COLUMN_CARTONPRICE));
//						String MinimumSellingPrice = cursor.getString(cursor.getColumnIndex(COLUMN_MINIMUMSELLINGPRICE));
                String ItemRemarks = cursor.getString(cursor.getColumnIndex(COLUMN_ITEMREMARKS));

                String DoQty = cursor.getString(cursor.getColumnIndex(COLUMN_DOQTY));
                String DoFOCQty = cursor.getString(cursor.getColumnIndex(COLUMN_DOFOCQTY));
                String InvQty = cursor.getString(cursor.getColumnIndex(COLUMN_INVOICEQTY));
                String InvFOCQty = cursor.getString(cursor.getColumnIndex(COLUMN_INVOICEFOCQTY));

                if (DoQty != null && !DoQty.isEmpty()) {
                } else {
                    DoQty = "0.00";
                }

                if (DoFOCQty != null && !DoFOCQty.isEmpty()) {
                } else {
                    DoFOCQty = "0.00";
                }

                if (InvQty != null && !InvQty.isEmpty()) {
                } else {
                    InvQty = "0.00";
                }

                if (InvFOCQty != null && !InvFOCQty.isEmpty()) {
                } else {
                    InvFOCQty = "0.00";
                }

                if (ExchangeQty != null && !ExchangeQty.isEmpty()) {
                } else {
                    ExchangeQty = "0.00";
                }

                if (CartonPrice != null && !CartonPrice.isEmpty()) {
                } else {
                    CartonPrice = "0.00";
                }

                HashMap<String, String> hm = new HashMap<String, String>();

                hm.put("slNo", slNo);
                hm.put("ProductCode", ProductCode);
                hm.put("ProductName", ProductName);
                hm.put("CQty", CQty);
                hm.put("LQty", LQty);
                hm.put("Qty", Qty);
                hm.put("FOCQty", FOCQty);
                hm.put("PcsPerCarton", PcsPerCarton);
                hm.put("RetailPrice", RetailPrice);
                hm.put("Price", Price);
                hm.put("Total", Total);
                hm.put("ItemDiscount", ItemDiscount);
                hm.put("BillDiscount", BillDiscount);
                hm.put("TotalDiscount", TotalDiscount);
                hm.put("SubTotal", SubTotal);
                hm.put("Tax", Tax);
                hm.put("NetTotal", NetTotal);
                hm.put("TaxType", TaxType);
                hm.put("TaxPerc", TaxPerc);

                hm.put("DOQty", DoQty);
                hm.put("DOFocQty", DoFOCQty);
                hm.put("InvoiceQty", InvQty);
                hm.put("InvoiceFocQty", InvFOCQty);

                hm.put("ExchangeQty", ExchangeQty);
                hm.put("CartonPrice", CartonPrice);
                hm.put("MinimumSellingPrice", "0.00");
                hm.put("ItemRemarks", ItemRemarks);

                SO_DetailsArr.add(hm);

            } while (cursor.moveToNext());
        }
        return SO_DetailsArr;
    }

    public static ArrayList<String> getproductcodefromsono(String sono) {

        ArrayList<String> addproduct = new ArrayList<String>();
        String ProductCode = "";
        String selectQuery = "SELECT * FROM tblGetSODetail WHERE SoNo = '" + sono + "' AND CompanyCode = '" + comapanyCode + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ProductCode = cursor.getString(cursor
                        .getColumnIndex("ProductCode"));
                addproduct.add(ProductCode);
            } while (cursor.moveToNext());
        }
        return addproduct;

    }

    public static double getSOTotalQty(String sono) {
        double totalqty = 0.00;
        String Query = "SELECT sum(Qty) FROM tblGetSODetail WHERE SoNo = '" + sono + "' AND CompanyCode = '" + comapanyCode + "'";
        Cursor cursor = getDatabase().rawQuery(Query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                totalqty = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return totalqty;
    }

    public void deleteSOItem(String sono, String productcode) {

        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM tblGetSODetail where SoNo ='" + sono + "' AND ProductCode = '" + productcode + "'";
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
        db.close();

    }

    public int updateStatus(String sono, String status) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);
        return getDatabase().update("tblGetSOHeader", cv, "SoNo = '" + sono + "' AND CompanyCode = '" + comapanyCode + "'", null);

    }

    public static String getSODownloadStatus(String sono) {

        String status = "";
        String selectQuery = "SELECT * FROM tblGetSOHeader WHERE SoNo = '" + sono + "'" + "AND download_status='1'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                status = cursor.getString(cursor.getColumnIndex("download_status"));
            } while (cursor.moveToNext());
        }
        return status;
    }


    public String getSOHeaderJson(HashMap<String, String> hashmap) {
        String result = "";
        String companyCode = hashmap.get("CompanyCode");
        String soNo = hashmap.get("SoNo");

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetSOHeader  WHERE CompanyCode = '" + companyCode + "' AND SoNo = '" + soNo + "'";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"SoNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SONO)))

                            .append("\",\"SoDate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SODATE)).split("\\ ")[0])

                            .append("\",\"ItemDiscount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ITEMDISCOUNT)))

                            .append("\",\"BillDIscount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BILLDISCOUNT)))

                            .append("\",\"SubTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBTOTAL)))

                            .append("\",\"Tax\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAX)))

                            .append("\",\"NetTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_NETTOTAL)))

                            .append("\",\"Remarks\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_REMARKS)))

                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("SO Header ", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public String getAllSOHeaderJson(HashMap<String, String> hashmap) {
        String result = "";
        String companyCode = "", customerCode = "", vanCode = "";
        companyCode = hashmap.get("CompanyCode");
        customerCode = hashmap.get("CustomerCode");
        vanCode = hashmap.get("VanCode");

        StringBuilder builder = new StringBuilder("[");
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = dateFormat.parse(hashmap.get("FromDate"));
            Date endDate = dateFormat.parse(hashmap.get("ToDate"));

            SimpleDateFormat regateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String from = regateFormat.format(startDate) + " 00:00:00";
            String to = regateFormat.format(endDate) + " 24:00:00";

            Log.d("fromDate", from);
            Log.d("toDate", to);

            String selectQuery = "SELECT * FROM tblGetSOHeader  WHERE CompanyCode = '" + companyCode + "'";

            if (customerCode != null && !customerCode.isEmpty()) {
                selectQuery += " AND CustomerCode = '" + customerCode + "'";
            }
            if (vanCode != null && !vanCode.isEmpty()) {
                selectQuery += " AND VanCode = '" + vanCode + "'";
            }

            if ((from != null && !from.isEmpty())
                    && (to != null && !to.isEmpty())) {
                selectQuery += " AND SoDate BETWEEN '" + from + "' AND '" + to + "'";
            }

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"SoNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SONO)))

                            .append("\",\"SoDate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SODATE)).split("\\ ")[0])

                            .append("\",\"DeliveryDate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_DELIVERYDATE)).split("\\ ")[0])

                            .append("\",\"LocationCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_LOCATIONCODE)))

                            .append("\",\"CustomerCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERCODE)))

                            .append("\",\"CustomerName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CUSTOMERNAME)))

                            .append("\",\"Total\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TOTAL)))

                            .append("\",\"ItemDiscount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_ITEMDISCOUNT)))

                            .append("\",\"BillDIscount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_BILLDISCOUNT)))

                            .append("\",\"TotalDiscount\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TOTALDISCOUNT)))

                            .append("\",\"SubTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBTOTAL)))

                            .append("\",\"Tax\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAX)))

                            .append("\",\"NetTotal\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_NETTOTAL)))

                            .append("\",\"Remarks\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_REMARKS)))

                            .append("\",\"Status\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_STATUS)))

                            .append("\",\"CurrencyCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CURRENCYCODE)))

                            .append("\",\"CurrencyRate\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CURRENCYRATE)))

                            .append("\",\"VanCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_VANCODE)))

                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("SO Header ", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


    public String getSODetailJson(HashMap<String, String> hashmap) {
        String result = "";
        String companyCode = hashmap.get("CompanyCode");
        String soNo = hashmap.get("SoNo");

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetSODetail  WHERE CompanyCode = '" + companyCode + "' AND SoNo = '" + soNo + "'";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")

                            .append("\"slNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SLNO)))

                            .append("\",\"SoNo\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SONO)))

                            .append("\",\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))

                            .append("\",\"ProductName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTNAME)))

                            .append("\",\"Qty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_QTY)))

                            .append("\",\"Price\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRICE)))

                            .append("\",\"Total\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TOTAL)))

                            .append("\",\"FOCQty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_FOCQTY)))

                            .append("\",\"ExchangeQty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_EXCHANGEQTY)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");
            result = " { SODetails: " + builder.toString() + "}";
            Log.d("SO Details", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


    /** SO END **/

    /**
     * InvoiceHeaderWithoutDOSign Start
     **/

    public static ArrayList<SO> getRetriveInvoiceHeaderWithoutDOSign(HashMap<String, String> hm) {
        double totalamount = 0.00;
        ArrayList<SO> invoiceheaderlist = new ArrayList<SO>();
        String selectCustomerCode = "", selectFromDate = "", selectToDate = "", selectBalanceFlag = "";
        selectCustomerCode = hm.get("CustomerCode");
        selectFromDate = hm.get("FromDate");
        selectToDate = hm.get("ToDate");
        selectBalanceFlag = hm.get("BalanceFlag");

        try {

            String selectQuery = "SELECT * FROM tblGetInvoiceHeaderWithoutDOSign WHERE CompanyCode= '" + comapanyCode + "'";// order by InvoiceDate

            if (selectCustomerCode != null && !selectCustomerCode.isEmpty()) {
                selectQuery += " AND CustomerCode = '" + selectCustomerCode + "'";
            }

            if ((selectFromDate != null && !selectFromDate.isEmpty())
                    && (selectToDate != null && !selectToDate.isEmpty())) {
                selectQuery += " AND InvoiceDate BETWEEN '" + selectFromDate + "' AND '" + selectToDate + "'";
            }

//					if (selectBalanceFlag.matches("0")) {
//						selectQuery += " AND BalanceAmount > '0.00'";
//					} else if (selectBalanceFlag.matches("1")) {
//						selectQuery += " AND BalanceAmount <= '0.00'";
//					}

            selectQuery += " AND PODSign = '0' ORDER BY InvoiceDate DESC";

            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    String ccSno = cursor.getString(cursor
                            .getColumnIndex(COLUMN_INVOICENO));
                    String ccDate = cursor.getString(cursor
                            .getColumnIndex(COLUMN_INVOICEDATE));

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    Date myDate = dateFormat.parse(ccDate);
                    SimpleDateFormat timeFormat = new SimpleDateFormat(
                            "dd/MM/yyyy");
                    String finalDate = timeFormat.format(myDate);

                    String customerCode = cursor.getString(cursor
                            .getColumnIndex(COLUMN_CUSTOMERCODE));
                    String customerName = cursor.getString(cursor
                            .getColumnIndex(COLUMN_CUSTOMERNAME));
                    String amount = cursor.getString(cursor
                            .getColumnIndex(COLUMN_NETTOTAL));
                    String balanceamount = cursor.getString(cursor
                            .getColumnIndex(COLUMN_BALANCEAMOUNT));

                    String nettot = cursor.getString(cursor
                            .getColumnIndex(COLUMN_NETTOTAL));

                    double net = 0;
                    if (nettot != null && !nettot.isEmpty()) {
                        net = Double.parseDouble(nettot);
                    }


                    totalamount = totalamount + Double.valueOf(net);

                    SO so = new SO();
                    so.setSno(ccSno);
                    so.setDate(finalDate);
                    so.setCustomerCode(customerCode);
                    so.setCustomerName(customerName);
                    so.setNettotal(amount);
                    so.setBalanceamount(balanceamount);

                    invoiceheaderlist.add(so);
                } while (cursor.moveToNext());
            }
            SO.setTotalamount(totalamount);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return invoiceheaderlist;

    }

    public static void deleteInvoiceHeaderWithoutDOSign(String invoiceno) {
        getDatabase().delete(GETINVOICEHEADERWITHOUTDOSIGN_TABLE, "InvoiceNo = '" + invoiceno + "'", null);

    }

    public void updateSignStaus(String invoiceno) {

        String where = "CompanyCode = '" + comapanyCode + "' AND InvoiceNo = '" + invoiceno + "'";

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_POD_SIGN, "1");
        getDatabase().update("tblGetInvoiceHeaderWithoutDOSign", cv, where, null);

    }
    /** InvoiceHeaderWithoutDOSign End   **/


    /**
     * Signatue/Product Image Start
     **/

    public void storeImage(HashMap<String, String> hm) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_COMPANYCODE, comapanyCode);
        cv.put(COLUMN_IMAGE_NO, hm.get("ImageNo"));
        cv.put(COLUMN_IMAGE_TYPE, hm.get("ImageType"));
        cv.put(COLUMN_SIGNATURE_IMAGE, hm.get("SignatureImage"));
        cv.put(COLUMN_PRODUCT_IMAGE, hm.get("ProductImage"));
        cv.put(COLUMN_DOWNLOADSTATUS, "1");
        getDatabase().insert(IMAGE_TABLE, null, cv);
        Log.d("storeImage", "... storeImage");
    }


    public void updateImage(HashMap<String, String> hm) {
        String no = hm.get("ImageNo");
        String type = hm.get("ImageType");
        String where = "ImageNo ='" + no + "' AND ImageType ='" + type + "' AND CompanyCode = '" + comapanyCode + "'";
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SIGNATURE_IMAGE, hm.get("SignatureImage"));
        cv.put(COLUMN_PRODUCT_IMAGE, hm.get("ProductImage"));
        cv.put(COLUMN_DOWNLOADSTATUS, "1");

        getDatabase().update(IMAGE_TABLE, cv, where, null);
        Log.d("updateImage", "... updateImage");
    }

    public String getImageNo(String imageno) {

        String imageNo = "";
        String selectQuery = "SELECT ImageNo FROM tblGetImage WHERE ImageNo = '" + imageno + "' And CompanyCode = '" + comapanyCode + "' AND download_status='1'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                imageNo = cursor.getString(cursor.getColumnIndex("ImageNo"));

            } while (cursor.moveToNext());
        }
        return imageNo;

    }

    public String getSignImage(String imageno, String type) {

        String signImage = "";
        String selectQuery = "SELECT SignatureImage FROM tblGetImage WHERE ImageNo = '" + imageno + "' AND ImageType ='" + type + "' And CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                signImage = cursor.getString(cursor.getColumnIndex("SignatureImage"));

            } while (cursor.moveToNext());
        }
        return signImage;

    }

    public static Cursor getCursorForUploadImage(String tablename) {

        String selectQuery = "SELECT * FROM " + tablename + " WHERE CompanyCode = '" + comapanyCode + "' AND download_status = '1'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        return cursor;
    }

    public static void deleteUploadImage(HashMap<String, String> hm) {
        String no = hm.get("ImageNo");
        String type = hm.get("ImageType");
        String where = "ImageNo ='" + no + "' AND ImageType ='" + type + "' AND CompanyCode = '" + comapanyCode + "'";
        getDatabase().delete(IMAGE_TABLE, where, null);
    }


    /** Signatue/Product Image Start   **/


    /**
     * Catalog start
     **/

    public String getProductSubImages(HashMap<String, String> hm) {
        String result = "";
        String selectLocationCode = "", selectProductCode = "", selectCategoryCode = "", selectSubCategoryCode = "", selectCustomerGroupCode = "";
        selectLocationCode = hm.get("LocationCode");
        selectProductCode = hm.get("ProductCode");
        selectCategoryCode = hm.get("CategoryCode");
        selectSubCategoryCode = hm.get("SubCategoryCode");
        selectCustomerGroupCode = hm.get("CustomerGroupCode");

//						Log.d("PageNo", "=>"+params.get("PageNo"));
//						Log.d("ProductCode", "=>"+params.get("ProductCode"));
//						Log.d("CategoryCode", "=>"+params.get("CategoryCode"));
//						Log.d("SubCategoryCode", "=>"+params.get("SubCategoryCode"));
//						Log.d("CustomerGroupCode", "=>"+customerGroupCode);

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetProductSubImages WHERE CompanyCode = '" + comapanyCode + "'";

//							if (selectLocationCode != null && !selectLocationCode.isEmpty()) {
//								selectQuery += " AND LocationCode = '" + selectLocationCode+ "'";
//							}

            if (selectProductCode != null && !selectProductCode.isEmpty()) {
                selectQuery += " AND ProductCode = '" + selectProductCode + "'";
            }


//							if (selectCategoryCode != null && !selectCategoryCode.isEmpty()) {
//								selectQuery += " AND CategoryCode = '" + selectCategoryCode+ "'";
//							}
//
//
//							if (selectSubCategoryCode != null && !selectSubCategoryCode.isEmpty()) {
//								selectQuery += " AND SubCategoryCode = '" + selectSubCategoryCode+ "'";
//							}
//
//							if (selectCustomerGroupCode != null && !selectCustomerGroupCode.isEmpty()) {
//								selectQuery += " AND CustomerGroupCode = '" + selectCustomerGroupCode+ "'";
//							}


            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
//								    String pn ="(TMC-00010A), Heavy-duty wiper arm, 11.25\\\" - 15\\\",";
                    String productname = cursor.getString(cursor
                            .getColumnIndex(COLUMN_PRODUCTNAME));

                    if (productname != null && !productname.isEmpty()) {
                        productname = productname.replace("\"", "\\\"");
                    }

                    builder.append("{")

                            .append("\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))

                            .append("\",\"ProductName\":\"")
                            .append(productname)

                            .append("\",\"LocationCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_LOCATIONCODE)))

                            .append("\",\"CategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CATEGORYCODE)))

                            .append("\",\"SubCategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBCATEGORYCODE)))

                            .append("\",\"UomCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_UOMCODE)))

                            .append("\",\"PcsPerCarton\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PCSPERCARTON)))

                            .append("\",\"WholeSalePrice\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_WHOLESALEPRICE)))

                            .append("\",\"ProductImage\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCT_IMAGE)))

                            .append("\",\"Qty\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_QTY)))

                            .append("\"}");


                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");

            result = builder.toString();
//							 result = " { SODetails: " + builder.toString() + "}";
//							Log.d("SO Details", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


    public String getProductMainImage(HashMap<String, String> hm) {
        String result = "";
        String selectPageNo = "", selectProductCode = "", selectCategoryCode = "", selectSubCategoryCode = "", selectCustomerGroupCode = "";
        selectPageNo = hm.get("PageNo");
        selectProductCode = hm.get("ProductCode");
        selectCategoryCode = hm.get("CategoryCode");
        selectSubCategoryCode = hm.get("SubCategoryCode");
        selectCustomerGroupCode = hm.get("CustomerGroupCode");

        String pQuery = "SELECT * FROM tblGetProduct WHERE CompanyCode = '" + comapanyCode + "'"; // AND ProductCode='0000001'";
        Log.d("selectQuery", pQuery);
        Cursor pCursor = getDatabase().rawQuery(pQuery, null);
        Log.d("tblGetProduct count", "->" + pCursor.getCount());

        String mQuery = "SELECT * FROM tblGetProductMainImage WHERE CompanyCode = '" + comapanyCode + "'";// AND ProductCode='0000001'";
        Log.d("selectQuery", mQuery);
        Cursor mCursor = getDatabase().rawQuery(mQuery, null);
        Log.d("tblGetProductMainImage count", "->" + mCursor.getCount());

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT DISTINCT a.ProductCode, a.ProductName, a.CategoryCode, a.SubCategoryCode, a.UomCode, a.PcsPerCarton, a.WholeSalePrice, a.RetailPrice,"
                    + "a.Specification, b.ProductImage  FROM tblGetProduct a LEFT OUTER JOIN tblGetProductMainImage b on a.ProductCode == b.ProductCode WHERE a.CompanyCode = '" + comapanyCode + "'";

            //, a.*, b.ProductImage
//							if (selectPageNo != null && !selectPageNo.isEmpty()) {
//								selectQuery += " AND CustomerCode = '" + selectCustomerCode+ "'";
//							}

            if (selectProductCode != null && !selectProductCode.isEmpty()) {
                selectQuery += " AND a.ProductCode = '" + selectProductCode + "'";
            }


            if (selectCategoryCode != null && !selectCategoryCode.isEmpty()) {
                selectQuery += " AND a.CategoryCode = '" + selectCategoryCode + "'";
            }


            if (selectSubCategoryCode != null && !selectSubCategoryCode.isEmpty()) {
                selectQuery += " AND a.SubCategoryCode = '" + selectSubCategoryCode + "'";
            }

            selectQuery += " GROUP BY a.ProductCode, b.ProductCode";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            Log.d("tblGetProduct, tblGetProductMainImage count", "->" + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
//								    String pn ="(TMC-00010A), Heavy-duty wiper arm, 11.25\\\" - 15\\\",";
                    String productname = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTNAME));

                    productname = productname.replace("\"", "\\\"");

                    builder.append("{")

                            .append("\"ProductCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCTCODE)))

                            .append("\",\"ProductName\":\"")
                            .append(productname)

                            .append("\",\"CategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_CATEGORYCODE)))

                            .append("\",\"SubCategoryCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SUBCATEGORYCODE)))

                            .append("\",\"UomCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_UOMCODE)))

                            .append("\",\"PcsPerCarton\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PCSPERCARTON)))

                            .append("\",\"WholeSalePrice\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_WHOLESALEPRICE)))  //price

                            .append("\",\"RetailPrice\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_RETAILPRICE)))   //cprice

                            .append("\",\"Specification\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_SPECIFICATION)))

                            .append("\",\"ProductImage\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_PRODUCT_IMAGE)))

                            .append("\"}");


                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }
            builder.append("]");

            result = builder.toString();
//							 result = " { SODetails: " + builder.toString() + "}";
//							Log.d("SO Details", ":" + result);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public static String getCatalogModifyDate() {
        String datetime = "";
        String selectQuery = "SELECT ServerDate FROM tblGetProductMainImage WHERE CompanyCode = '" + comapanyCode + "'";

        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                datetime = cursor
                        .getString(cursor.getColumnIndex("ServerDate"));

            } while (cursor.moveToNext());
        }
        return datetime;
    }

    /**
     * Catalog End
     **/

    public String getPhotoImage(String imageno, String type) {

        String productImage = "";
        String selectQuery = "SELECT ProductImage FROM tblGetImage WHERE ImageNo = '" + imageno + "' AND ImageType ='" + type + "' And CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                productImage = cursor.getString(cursor.getColumnIndex("ProductImage"));

            } while (cursor.moveToNext());
        }
        return productImage;

    }

    /* Store SRHeader Table */
    public static void store_SRheader(String companyCode, String datetime, JSONArray jsonArray) {

        database.beginTransaction();
        try {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String srno = "", db_code = "";
                srno = object.getString("SalesReturnNo");
                String selectQuery = "SELECT SalesReturnNo FROM fncGetSalesReturnHeader WHERE CompanyCode = '" + companyCode + "' AND SalesReturnNo = '" + srno + "'";
                Log.d("selectQuery", selectQuery);
                Cursor cursor = null;
                cursor = getDatabase().rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        db_code = cursor.getString(cursor.getColumnIndex("SalesReturnNo"));
                    } while (cursor.moveToNext());
                }

                ContentValues cv = new ContentValues();

                cv.put(COLUMN_COMPANYCODE, companyCode);
                cv.put(COLUMN_SERVERDATEANDTIME, datetime);
                cv.put(COLUMN_SALESRETURNNO, object.getString(("SalesReturnNo")));
                String srDateStr = object.getString("SalesReturnDate");

                Log.d("SRDate", "--" + srDateStr);

                SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String so_finalDate = "", do_finalDate = "";

                if (srDateStr != null && !srDateStr.isEmpty()) {
                    Date soDate = oldFormat.parse(srDateStr);
                    so_finalDate = newFormat.format(soDate);
                }

                cv.put(COLUMN_SODATE, so_finalDate);
                cv.put(COLUMN_INVOICENO, object.getString(("InvoiceNo")));
                cv.put(COLUMN_LOCATIONCODE, object.getString(("LocationCode")));
                cv.put(COLUMN_CUSTOMERCODE, object.getString(("CustomerCode")));
                cv.put(COLUMN_CUSTOMERNAME, object.getString(("CustomerName")));
                cv.put(COLUMN_TOTAL, object.getString(("Total")));
                cv.put(COLUMN_ITEMDISCOUNT, object.getString(("ItemDiscount")));
                cv.put(COLUMN_BILLDISCOUNT, object.getString(("BillDIscount")));
                cv.put(COLUMN_TOTALDISCOUNT, object.getString(("TotalDiscount")));
                cv.put(COLUMN_SUBTOTAL, object.getString(("SubTotal")));
                cv.put(COLUMN_TAX, object.getString(("Tax")));
                cv.put(COLUMN_NETTOTAL, object.getString(("NetTotal")));
                cv.put(COLUMN_REMARKS, object.getString(("Remarks")));
                cv.put(COLUMN_PAIDAMOUNT, object.getString(("PaidAmount")));
                cv.put(COLUMN_CREDITAMOUNT, object.getString(("CreditAmount")));
                cv.put(COLUMN_BALANCEAMOUNT, object.getString(("BalanceAmount")));
                cv.put(COLUMN_TOTALBALANCE, object.getString(("TotalBalance")));
                cv.put(COLUMN_VANCODE, object.getString(("VanCode")));

                cv.put(CREATE_USER, "");
                cv.put(CREATE_DATE, "");
                cv.put(MODIFY_USER, "");
                cv.put(MODIFY_DATE, "");
                cv.put(COLUMN_DOWNLOADSTATUS, "0");

                if (db_code != null && !db_code.isEmpty()) {
                    String where = "CompanyCode = '" + companyCode + "' AND SalesReturnNo = '" + srno + "'";
                    getDatabase().update(GETSRHEADER_TABLE, cv, where, null);
                } else {
                    getDatabase().insert(GETSRHEADER_TABLE, null, cv);
                }
                if (cursor != null)
                    cursor.close();

            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error in transaction", e.toString());
        } finally {

            database.endTransaction();
        }
    }

    /* Get SOHeader From tblGetSalesReturnHeader CursorTable */
    public ArrayList<HashMap<String, String>> getSRHeader(String SRNo) {
        ArrayList<HashMap<String, String>> SR_DetailsArr = new ArrayList<HashMap<String, String>>();
        SR_DetailsArr.clear();

        String selectQuery = "SELECT * FROM tblGetSalesReturnHeader WHERE SalesReturnNo = '"
                + SRNo + "' AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String SrNo = cursor.getString(cursor.getColumnIndex(COLUMN_SALESRETURNNO));
                String SoDate = cursor.getString(cursor.getColumnIndex(COLUMN_SALESRETURNDATE));
                String InvoiceNo = cursor.getString(cursor.getColumnIndex(COLUMN_INVOICENO));
                String LocationCode = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATIONCODE));
                String CustomerCode = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMERCODE));
                String Total = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL));
                String ItemDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_ITEMDISCOUNT));

                String BillDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_BILLDISCOUNT));
                String TotalDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_TOTALDISCOUNT));
                String SubTotal = cursor.getString(cursor.getColumnIndex(COLUMN_SUBTOTAL));
                String Tax = cursor.getString(cursor.getColumnIndex(COLUMN_TAX));
                String NetTotal = cursor.getString(cursor.getColumnIndex(COLUMN_NETTOTAL));
                String Remarks = cursor.getString(cursor.getColumnIndex(COLUMN_REMARKS));

                String PaidAmount = cursor.getString(cursor.getColumnIndex(COLUMN_PAIDAMOUNT));
                String CreditAmount = cursor.getString(cursor.getColumnIndex(COLUMN_CREDITAMOUNT));
                String BalanceAmount = cursor.getString(cursor.getColumnIndex(COLUMN_BALANCEAMOUNT));
                String TotalBalance = cursor.getString(cursor.getColumnIndex(COLUMN_TOTALBALANCE));
                String VanCode = cursor.getString(cursor.getColumnIndex(COLUMN_VANCODE));


                String sr_date = "";
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date mDate = dateFormat.parse(SoDate);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");

                    sr_date = timeFormat.format(mDate);


                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d("getSOHeader ", "so_date" + sr_date);

                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("SalesReturnNo", SrNo);
                hm.put("SalesReturnDate", sr_date);
                hm.put("InvoiceNo", InvoiceNo);
                hm.put("LocationCode", LocationCode);
                hm.put("CustomerCode", CustomerCode);
                hm.put("Total", Total);
                hm.put("ItemDiscount", ItemDiscount);
                hm.put("BillDIscount", BillDiscount);
                hm.put("TotalDiscount", TotalDiscount);
                hm.put("SubTotal", SubTotal);
                hm.put("Tax", Tax);
                hm.put("NetTotal", NetTotal);
                hm.put("Remarks", Remarks);
                hm.put("PaidAmount", PaidAmount);
                hm.put("CreditAmount", CreditAmount);
                hm.put("BalanceAmount", BalanceAmount);
                hm.put("TotalBalance", TotalBalance);
                hm.put("VanCode", VanCode);

                SR_DetailsArr.add(hm);

            } while (cursor.moveToNext());
        }
        return SR_DetailsArr;
    }

    /* Get SRDetail From tblGetSalesReturnDetail CursorTable */
    public ArrayList<HashMap<String, String>> getSRDetails(String SONo) {
        ArrayList<HashMap<String, String>> SR_DetailsArr = new ArrayList<HashMap<String, String>>();
        SR_DetailsArr.clear();

        String selectQuery = "SELECT * FROM tblGetSalesReturnDetail WHERE SalesReturnNo = '"
                + SONo + "' AND CompanyCode = '" + comapanyCode + "'";
        Log.d("selectQuery", selectQuery);
        Cursor cursor = getDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String slNo = cursor.getString(cursor.getColumnIndex(COLUMN_SLNO));
                String ProductCode = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTCODE));
                String ProductName = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTNAME));
                String CQty = cursor.getString(cursor.getColumnIndex(COLUMN_CQTY));
                String LQty = cursor.getString(cursor.getColumnIndex(COLUMN_LQTY));
                String Qty = cursor.getString(cursor.getColumnIndex(COLUMN_QTY));
                String FOCQty = cursor.getString(cursor.getColumnIndex(COLUMN_FOCQTY));
                String PcsPerCarton = cursor.getString(cursor.getColumnIndex(COLUMN_PCSPERCARTON));
                String RetailPrice = cursor.getString(cursor.getColumnIndex(COLUMN_RETAILPRICE));
                String Price = cursor.getString(cursor.getColumnIndex(COLUMN_PRICE));
                String Total = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL));
                String ItemDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_ITEMDISCOUNT));

                String BillDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_BILLDISCOUNT));
                String TotalDiscount = cursor.getString(cursor.getColumnIndex(COLUMN_TOTALDISCOUNT));
                String SubTotal = cursor.getString(cursor.getColumnIndex(COLUMN_SUBTOTAL));
                String Tax = cursor.getString(cursor.getColumnIndex(COLUMN_TAX));
                String NetTotal = cursor.getString(cursor.getColumnIndex(COLUMN_NETTOTAL));
                String TaxType = cursor.getString(cursor.getColumnIndex(COLUMN_TAXTYPE));
                String TaxPerc = cursor.getString(cursor.getColumnIndex(COLUMN_TAXPERC));

                String CartonPrice = cursor.getString(cursor.getColumnIndex(COLUMN_CARTONPRICE));
                String StockAdjRefCode = cursor.getString(cursor.getColumnIndex(COLUMN_STOCKADJREFCODE));
                String TaxCode = cursor.getString(cursor.getColumnIndex(COLUMN_TAXCODE));

                if (CartonPrice != null && !CartonPrice.isEmpty()) {
                } else {
                    CartonPrice = "0.00";
                }

                HashMap<String, String> hm = new HashMap<String, String>();

                hm.put("slNo", slNo);
                hm.put("ProductCode", ProductCode);
                hm.put("ProductName", ProductName);
                hm.put("CQty", CQty);
                hm.put("LQty", LQty);
                hm.put("Qty", Qty);
                hm.put("FOCQty", FOCQty);
                hm.put("PcsPerCarton", PcsPerCarton);
                hm.put("RetailPrice", RetailPrice);
                hm.put("Price", Price);
                hm.put("Total", Total);
                hm.put("ItemDiscount", ItemDiscount);
                hm.put("BillDiscount", BillDiscount);
                hm.put("TotalDiscount", TotalDiscount);
                hm.put("SubTotal", SubTotal);
                hm.put("Tax", Tax);
                hm.put("NetTotal", NetTotal);
                hm.put("TaxType", TaxType);
                hm.put("TaxPerc", TaxPerc);
                hm.put("CartonPrice", CartonPrice);
                hm.put("StockAdjRefCode", StockAdjRefCode);
                hm.put("TaxCode", TaxCode);

                SR_DetailsArr.add(hm);

            } while (cursor.moveToNext());
        }
        return SR_DetailsArr;
    }

    /**
     * Start Default Functions
     **/
    public static String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }

    public String fourDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setMinimumFractionDigits(4);
        String tot = df.format(d);

        return tot;
    }
    /** End Default Functions **/




    public static String getTax() {

        StringBuilder builder = new StringBuilder("[");
        try {

            String selectQuery = "SELECT * FROM tblGetTax WHERE CompanyCode = '"
                    + comapanyCode + "'";
            Log.d("selectQuery", selectQuery);
            Cursor cursor = getDatabase().rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    builder.append("{")
                            .append("\"TaxCode\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAXCODE)))
                            .append("\",\"TaxName\":\"")
                            .append(cursor.getString(cursor
                                    .getColumnIndex(COLUMN_TAXNAME)))
                            .append("\"}");
                    if (!cursor.isLast()) {
                        builder.append(",");
                    }
                } while (cursor.moveToNext());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("]");
        String result = " { JsonArray: " + builder.toString() + "}";
        Log.d("OfflineTerms", ":" + result);
        return result;
    }

}