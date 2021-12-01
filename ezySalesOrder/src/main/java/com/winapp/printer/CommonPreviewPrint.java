package com.winapp.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.model.Product;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.sot.Company;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.TransferHeader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CommonPreviewPrint extends Activity {
    private List<ProductDetails> product, productdet;
    private ListView lv;
    private ImageButton back;
    private TextView title, no, date, custcode, custname, itemdisc, billdisc, subtotal,
            tax, nettotal, companyname, invoiceno_txt, no_label, date_label,
            custname_label, totalqty_val, listheader_sno, custcode_label,
            listheader_price, listheader_total, address1, address2, country,
            phone,remarks,remarks_txt,duration,custaddr ;
    private LinearLayout companyname_ll, address1_ll, address2_ll, country_ll,
            phone_ll, productdetails_ll, dotdot_ll, signature_ll, totalqty_lbl,remarks_lbl;
    private String /*jsonString = null, jsonStr = null,*/title_str, sno_str, custname_str, date_str, companyname_str, custcode_str, address1_str, address2_str, country_str, phone_str,cus_remarks,customeraddress,
            remark_str, zipcode_str, transfer_stockreq, taxPerc = "", taxType = "", taxName = "",durationDays="";
    double totalqty = 0d;
    private UIHelper helper;
    private List<String> sort;
    private ImageView print_iv;
    private String flag = "", appPrintGroup = "";
    private HashSet<String> hs;
    private List<String> listDataHeader, listExpHeader;
    private ExpandableListView mExpandableListView;
    private List<Product> mAttributeArr;
    private String tran_type="";
    LinearLayout durationLayout,customeraddresslayout;
    TextView custaddr_txt1, custaddrcol1, custaddr1;
    LinearLayout customeraddrlayout1;
    TextView custaddr_txt2, custaddrcol2, custaddr2;
    LinearLayout customeraddrlayout2;
    TextView custaddr_txt3, custaddrcol3, custaddr3;
    LinearLayout customeraddrlayout3;
    String Invoicetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_preview_print);
        title = (TextView) findViewById(R.id.title);
        no = (TextView) findViewById(R.id.no);
        date = (TextView) findViewById(R.id.date);
        no_label = (TextView) findViewById(R.id.no_label);
        date_label = (TextView) findViewById(R.id.date_label);
        custcode_label = (TextView) findViewById(R.id.custcode_label);
        custname_label = (TextView) findViewById(R.id.custname_label);
        custcode = (TextView) findViewById(R.id.custcode);
        custname = (TextView) findViewById(R.id.custname);
        itemdisc = (TextView) findViewById(R.id.itemdisc);
        billdisc = (TextView) findViewById(R.id.billdisc);
        subtotal = (TextView) findViewById(R.id.subtotal);
        tax = (TextView) findViewById(R.id.tax);
        nettotal = (TextView) findViewById(R.id.nettotal);
        print_iv = (ImageView) findViewById(R.id.printer);
        listheader_sno = (TextView) findViewById(R.id.sno_txt);
        listheader_price = (TextView) findViewById(R.id.price_txt);
        listheader_total = (TextView) findViewById(R.id.total_txt);
        totalqty_val = (TextView) findViewById(R.id.totalqty);
        companyname = (TextView) findViewById(R.id.companyname);
        address1 = (TextView) findViewById(R.id.address1);
        address2 = (TextView) findViewById(R.id.address2);
        country = (TextView) findViewById(R.id.country);
        phone = (TextView) findViewById(R.id.phone);
        remarks =(TextView)findViewById(R.id.remarks);
        remarks_txt =(TextView)findViewById(R.id.remk_label);
        dotdot_ll = (LinearLayout) findViewById(R.id.dot_ll);
        signature_ll = (LinearLayout) findViewById(R.id.signature_ll);
        productdetails_ll = (LinearLayout) findViewById(R.id.productdetails_ll);
        companyname_ll = (LinearLayout) findViewById(R.id.companyname_ll);
        address1_ll = (LinearLayout) findViewById(R.id.address1_ll);
        address2_ll = (LinearLayout) findViewById(R.id.address2_ll);
        country_ll = (LinearLayout) findViewById(R.id.country_ll);
        phone_ll = (LinearLayout) findViewById(R.id.phone_ll);
        totalqty_lbl = (LinearLayout) findViewById(R.id.totalqty_ll);
        lv = (ListView) findViewById(R.id.preview_list);
        remarks_lbl =(LinearLayout)findViewById(R.id.remarks_lbl);
        durationLayout = (LinearLayout)findViewById(R.id.durationLayout);
        duration = (TextView)findViewById(R.id.duration);
        customeraddresslayout = (LinearLayout) findViewById(R.id.customeraddresslayout);
//        custaddr = (TextView) findViewById(R.id.custaddr);

        custaddr_txt1 = (TextView) findViewById(R.id.custaddr1_txt);
        custaddrcol1 = (TextView) findViewById(R.id.custaddrcol1);
        custaddr1 = (TextView) findViewById(R.id.custaddr1);
        customeraddrlayout1 = (LinearLayout) findViewById(R.id.customeraddr1layout);
        custaddr_txt2 = (TextView) findViewById(R.id.custaddr2_txt);
        custaddrcol2 = (TextView) findViewById(R.id.custaddrcol2);
        custaddr2 = (TextView) findViewById(R.id.custaddr2);
        customeraddrlayout2 = (LinearLayout) findViewById(R.id.customeraddr2layout);
        custaddr_txt3 = (TextView) findViewById(R.id.custaddr3_txt);
        custaddrcol3 = (TextView) findViewById(R.id.custaddrcol3);
        custaddr3 = (TextView) findViewById(R.id.custaddr3);
        customeraddrlayout3 = (LinearLayout) findViewById(R.id.customeraddr3layout);

        GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        GlobalData.mService = new BluetoothService(this, mHandler);

        mExpandableListView = (ExpandableListView) findViewById(R.id.preview_expandablelist);
        mAttributeArr = new ArrayList<Product>();

        sort = new ArrayList<String>();
        listDataHeader = new ArrayList<>();
        hs = new HashSet<String>();
        listExpHeader = new ArrayList<>();
        product = new ArrayList<ProductDetails>();
        productdet = new ArrayList<ProductDetails>();
        product.clear();
        productdet.clear();

        SOTDatabase.init(CommonPreviewPrint.this);
        SOTDatabase.deleteImage();
        appPrintGroup = SalesOrderSetGet.getAppPrintGroup();
        //hashValue = new HashMap<String, String>();
        helper = new UIHelper(CommonPreviewPrint.this);
        Bundle b = getIntent().getExtras();
        tran_type =b.getString("trantype");

        if (b != null) {

            Invoicetype = b.getString("Invoicetype");
            if(Invoicetype.equals("Consignment"))
            {
                customeraddrlayout1.setVisibility(View.GONE);
                customeraddrlayout2.setVisibility(View.GONE);
                customeraddrlayout3.setVisibility(View.GONE);
            }
            else
            {

            }

            title.setText(b.getString("title"));
            if (title.getText().toString().matches("GRA")) {
                no_label.setText("GRA No");
                date_label.setText("GRA Date");
                custcode_label.setText("SupplierCode");
                custname_label.setText("SupplierName");
                listheader_sno.setText("Code");
            }
            if (title.getText().toString().matches("SalesOrder")) {
                no_label.setText("SO No");
                date_label.setText("SO Date");
                listheader_sno.setText("Code");

            }
            if (title.getText().toString().matches("DeliveryOrder")) {
                no_label.setText("DO No");
                date_label.setText("DO Date");
                listheader_sno.setText("Code");
            }
            if (title.getText().toString().matches("Consignment")) {
                no_label.setText("CG No");
                date_label.setText("CG Date");
                durationLayout.setVisibility(View.VISIBLE);
                durationDays = b.getString("duration");
                Log.d("durationDays",durationDays);
            }
            if (title.getText().toString().matches("Consignment Return")) {
                no_label.setText("CG-Return No");
                date_label.setText("CG-Return Date");
                durationLayout.setVisibility(View.VISIBLE);
                durationDays = b.getString("duration");
                Log.d("durationDays",durationDays);
            }
            if (title.getText().toString().matches("SalesReturn")) {
                no_label.setText("SR No");
                date_label.setText("SR Date");
                listheader_sno.setText("Code");
                sort = b.getStringArrayList("sort");

            }
            if (title.getText().toString().matches("StockRequest")) {
                no_label.setText("SR No");
                date_label.setText("SR Date");
                custcode_label.setText("FromLocation");
                custname_label.setText("ToLocation");
                remarks_txt.setText("SR Remarks");
                remarks_lbl.setVisibility(View.VISIBLE);
                listheader_sno.setText("Code");

            }
            if (title.getText().toString().matches("Transfer")) {
                no_label.setText("Transfer No");
                date_label.setText("Transfer Date");
                custcode_label.setText("FromLocation");
                custname_label.setText("ToLocation");
                listheader_sno.setText("Code");
            }
            sno_str = b.getString("No");
            date_str = b.getString("Date");
            custcode_str = b.getString("customerCode");
            custname_str = b.getString("customerName");
            cus_remarks =b.getString("cus_remarks");





            Log.d("cus_remarks","-->"+cus_remarks);
            no.setText(sno_str);
            date.setText(date_str);
            custcode.setText(custcode_str);
            custname.setText(custname_str);
            duration.setText(durationDays);

            if(cus_remarks.matches("")){
                remarks_lbl.setVisibility(View.GONE);
            }else{
                remarks.setText(cus_remarks);
            }

            //no.setText(b.getString("No"));
            //date.setText(b.getString("Date"));
            //custcode.setText(b.getString("customerCode"));
            //custname.setText(b.getString("customerName"));
        }

        if (title.getText().toString().matches("SalesOrder")) {
            mAttributeArr = PreviewPojo.getAttributeDetail();
        }

        product = PreviewPojo.getProducts();
        productdet = PreviewPojo.getProductsDetails();
        taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
        if (taxPerc == null || taxPerc.trim().equals("")) {
            taxPerc = "0.00";
        }
        if (title.getText().toString().matches("SalesOrder")
                || title.getText().toString().matches("DeliveryOrder")
                || title.getText().toString().matches("Consignment")
                || title.getText().toString().matches("Consignment Return")
                || title.getText().toString().matches("SalesReturn")) {

            totalqty_lbl.setVisibility(View.GONE);
            for (ProductDetails products : product) {
                taxType = products.getTaxType();
                //taxPerc = products.getTaxPerc();
            }
            for (int i = 0; i < productdet.size(); i++) {
                ProductDetails productdetails = productdet.get(i);
                if (taxType.equalsIgnoreCase("I")) {
                    taxName = "Incl " + taxPerc.split("\\.")[0] + "%";
                } else if (taxType.equalsIgnoreCase("E")) {
                    taxName = "Excl " + taxPerc.split("\\.")[0] + "%";
                } else {
                    taxName = "Tax";
                }
                ((TextView) findViewById(R.id.tax_txt)).setText(taxName + " :");
                Log.d("taxType", "-->" + taxType);
                /********************/
                //If taxtype is I then subtotal = netotal - tax
                String netTotal = productdetails.getNettot().toString();
                Log.e("Nettotal", netTotal);
                if (netTotal != null && !netTotal.isEmpty()) {

                } else {
                    netTotal = "0.00";
                }
                String taxStr = productdetails.getTax().toString();
                if (taxStr != null && !taxStr.isEmpty()) {

                } else {
                    taxStr = "0.00";
                }
                double dNetTotal = Double.valueOf(netTotal);
                double dTax = Double.valueOf(taxStr);
                double dSubtotal = dNetTotal - dTax;
                Log.d("dNetTotal", "-->" + dNetTotal);
                Log.d("dTax", "-->" + dTax);
                Log.d("subtotal", "-->" + dSubtotal);
                itemdisc.setText(productdetails.getItemdisc());
                billdisc.setText(productdetails.getBilldisc());
                if (taxType != null && !taxType.isEmpty()) {
                    if (taxType.matches("I")) {
                        subtotal.setText(twoDecimalPoint(dSubtotal));
                    } else {
                        subtotal.setText(productdetails.getSubtotal());
                    }
                } else {
                    subtotal.setText(productdetails.getSubtotal());
                }
                //subtotal.setText(productdetails.getSubtotal());
                tax.setText(productdetails.getTax());
                nettotal.setText(productdetails.getNettot());

                Log.e("productdetails", productdetails.getCustomeraddress1()+productdetails.getCustomeraddress2()+productdetails.getCustomeraddress3());

                if(productdetails.getCustomeraddress1()==null || productdetails.getCustomeraddress2()==null || productdetails.getCustomeraddress3()==null)
                {

                }
                else
                {
                    if(!productdetails.getCustomeraddress1().equals("") && !productdetails.getCustomeraddress2().equals("") && !productdetails.getCustomeraddress3().equals(""))
                    {
                        custaddr_txt2.setVisibility(View.INVISIBLE);
                        custaddr_txt3.setVisibility(View.INVISIBLE);
                        custaddrcol2.setVisibility(View.INVISIBLE);
                        custaddrcol3.setVisibility(View.INVISIBLE);
                    }
                    else if(!productdetails.getCustomeraddress1().equals("") && !productdetails.getCustomeraddress2().equals("") && productdetails.getCustomeraddress3().equals(""))
                    {
                        custaddr_txt2.setVisibility(View.INVISIBLE);
                        custaddr_txt3.setVisibility(View.INVISIBLE);
                        custaddrcol2.setVisibility(View.INVISIBLE);
                        custaddrcol3.setVisibility(View.INVISIBLE);
                    }
                    else if(!productdetails.getCustomeraddress1().equals("") && productdetails.getCustomeraddress2().equals("") && !productdetails.getCustomeraddress3().equals(""))
                    {
                        custaddr_txt2.setVisibility(View.INVISIBLE);
                        custaddr_txt3.setVisibility(View.INVISIBLE);
                        custaddrcol2.setVisibility(View.INVISIBLE);
                        custaddrcol3.setVisibility(View.INVISIBLE);
                    }
                    else if(productdetails.getCustomeraddress1().equals("") && !productdetails.getCustomeraddress2().equals("") && !productdetails.getCustomeraddress3().equals(""))
                    {
                        customeraddrlayout1.setVisibility(View.GONE);
                        custaddr_txt3.setVisibility(View.INVISIBLE);
                        custaddrcol3.setVisibility(View.INVISIBLE);
                    }
                    else if(productdetails.getCustomeraddress1().equals("") && productdetails.getCustomeraddress2().equals("") && !productdetails.getCustomeraddress3().equals(""))
                    {
                        customeraddrlayout1.setVisibility(View.GONE);
                        customeraddrlayout2.setVisibility(View.GONE);
                    }

                    if(!productdetails.getCustomeraddress1().equals(""))
                    {
                        custaddr1.setText(productdetails.getCustomeraddress1());
                    }
                    else
                    {
                        customeraddrlayout1.setVisibility(View.GONE);
                    }
                    if(!productdetails.getCustomeraddress2().equals(""))
                    {
                        custaddr2.setText(productdetails.getCustomeraddress2());
                    }
                    else
                    {
                        customeraddrlayout2.setVisibility(View.GONE);
                    }
                    if(!productdetails.getCustomeraddress3().equals(""))
                    {
                        custaddr3.setText(productdetails.getCustomeraddress3());
                    }
                    else
                    {
                        customeraddrlayout3.setVisibility(View.GONE);
                    }
                }

            }
        } else if (title.getText().toString().matches("GRA")) {
            totalqty_lbl.setVisibility(View.GONE);
            print_iv.setVisibility(View.INVISIBLE);
            for (int i = 0; i < productdet.size(); i++) {
                ProductDetails productdetails = productdet.get(i);
                itemdisc.setText(productdetails.getItemdisc());
                billdisc.setText(productdetails.getBilldisc());
                subtotal.setText(productdetails.getSubtotal());
                tax.setText(productdetails.getTax());
                nettotal.setText(productdetails.getNettot());

            }
        } else {
            listheader_price.setVisibility(View.GONE);
            listheader_total.setVisibility(View.GONE);
            productdetails_ll.setVisibility(View.GONE);
            dotdot_ll.setVisibility(View.GONE);
            signature_ll.setVisibility(View.GONE);
            totalqty_lbl.setVisibility(View.VISIBLE);
            for (ProductDetails prods : product) {
                totalqty += prods.getTotalqty();
            }
            totalqty_val.setText(String.valueOf((int) totalqty));
        }
        if (title.getText().toString().matches("DeliveryOrder")) {
            if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")) {
                listheader_price.setVisibility(View.GONE);
                listheader_total.setVisibility(View.GONE);
                productdetails_ll.setVisibility(View.GONE);
                totalqty_lbl.setVisibility(View.VISIBLE);
                for (ProductDetails prods : product) {
                    int qty = prods.getQty().equals("") ? 0 : Integer.valueOf(prods.getQty());
                    totalqty += qty;
                }
                totalqty_val.setText(String.valueOf((int) totalqty));

            } else {
                listheader_price.setVisibility(View.VISIBLE);
                listheader_total.setVisibility(View.VISIBLE);
            }
        } if (title.getText().toString().matches("Consignment")) {
            if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")) {
                listheader_price.setVisibility(View.GONE);
                listheader_total.setVisibility(View.GONE);
                productdetails_ll.setVisibility(View.GONE);
                totalqty_lbl.setVisibility(View.VISIBLE);
                for (ProductDetails prods : product) {
                    int qty = prods.getQty().equals("") ? 0 : Integer.valueOf(prods.getQty());
                    totalqty += qty;
                }
                totalqty_val.setText(String.valueOf((int) totalqty));

            } else {
                listheader_price.setVisibility(View.VISIBLE);
                listheader_total.setVisibility(View.VISIBLE);
            }
        }
        if (title.getText().toString().matches("Consignment Return")) {
//            if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")) {
            listheader_price.setVisibility(View.GONE);
            listheader_total.setVisibility(View.GONE);
//            productdetails_ll.setVisibility(View.GONE);
            totalqty_lbl.setVisibility(View.VISIBLE);
            for (ProductDetails prods : product) {
                int qty = prods.getQty().equals("") ? 0 : Integer.valueOf(prods.getQty());
                totalqty += qty;
            }
            totalqty_val.setText(String.valueOf((int) totalqty));

//            } else {
//                listheader_price.setVisibility(View.VISIBLE);
//                listheader_total.setVisibility(View.VISIBLE);
//            }
        }
        else if (title.getText().toString().matches("SalesOrder")) {
            if (FormSetterGetter.getHidePrice() != null && !FormSetterGetter.getHidePrice().isEmpty()) {
                if (FormSetterGetter.getHidePrice().matches("Hide Price")) {
                    listheader_price.setVisibility(View.GONE);
                    listheader_total.setVisibility(View.GONE);
                    productdetails_ll.setVisibility(View.GONE);
                    totalqty_lbl.setVisibility(View.VISIBLE);
                    for (ProductDetails prods : product) {
                        double qty = prods.getQty().equals("") ? 0 : Double.valueOf(prods.getQty());
                        totalqty += qty;
                    }
                    totalqty_val.setText(decimalPoint(totalqty));

                } else {
                    listheader_price.setVisibility(View.VISIBLE);
                    listheader_total.setVisibility(View.VISIBLE);
                }
            } else {
                listheader_price.setVisibility(View.VISIBLE);
                listheader_total.setVisibility(View.VISIBLE);
            }
        }
        companyname_str = Company.getCompanyName();
        address1_str = Company.getAddress1();
        address2_str = Company.getAddress2();
        country_str = Company.getCountry();
        phone_str = Company.getPhoneNo();
        zipcode_str = Company.getZipCode();
        if (companyname_str.matches("")) {
            companyname_ll.setVisibility(View.GONE);
        } else {
            companyname.setText(companyname_str);
        }

        if (address1_str.matches("")) {
            address1_ll.setVisibility(View.GONE);
        } else {
            address1.setText(address1_str);
        }

        if (address2_str.matches("")) {
            address2_ll.setVisibility(View.GONE);
        } else {
            address2.setText(address2_str);
        }

        if (country_str.matches("")) {
            country_ll.setVisibility(View.GONE);
        } else {
            //country.setText(country_str);
            country.setText(country_str + " " + zipcode_str);
        }

        if (phone_str.matches("")) {
            phone_ll.setVisibility(View.GONE);
        } else {
            phone.setText(phone_str);
        }

        print_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                title_str = title.getText().toString();
                alertDialogPrint(title_str);
            }
        });
//		PrintPreviewAdapter ppa = new PrintPreviewAdapter(
//				CommonPreviewPrint.this, R.layout.printpreview_listitem,
//				product);
//		lv.setAdapter(ppa);

        if (title.getText().toString().matches("SalesOrder")) {
            if (mAttributeArr != null && !mAttributeArr.isEmpty()) {
                mExpandableListView.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
                ArrayList<ProductDetails> mProductDetailArr = new ArrayList<>();
                for (ProductDetails productDetails : product) {
                    String productCode = productDetails.getItemcode();
                    ArrayList<Product> mAttributeList = new ArrayList<>();
                    for (Product prodAttribute : mAttributeArr) {
                        String productAttributeCode = prodAttribute.getProductCode();
                        if (productCode.matches(productAttributeCode)) {
                            mAttributeList.add(prodAttribute);
                        }
                    }
                    ProductDetails mProductDetails = new ProductDetails(productDetails, mAttributeList);
                    mProductDetailArr.add(mProductDetails);
                }
                PreviewAttributeAdapter mPreviewAttributeAdapter = new PreviewAttributeAdapter(CommonPreviewPrint.this, mProductDetailArr);
                mExpandableListView.setAdapter(mPreviewAttributeAdapter);
            } else {
                mExpandableListView.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                PrintPreviewAdapter mPrintPreviewAdapter = new PrintPreviewAdapter(CommonPreviewPrint.this, R.layout.printpreview_listitem, product);
                lv.setAdapter(mPrintPreviewAdapter);
            }
        } else {
            mExpandableListView.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            PrintPreviewAdapter mPrintPreviewAdapter = new PrintPreviewAdapter(CommonPreviewPrint.this, R.layout.printpreview_listitem, product);
            lv.setAdapter(mPrintPreviewAdapter);
        }
    }

    public void sort() {
     /* sort product by Catagory and Sub Catagory */
        for (int i = 0; i < sort.size(); i++) {
            String catagory = sort.get(i).toString();
            for (ProductDetails products : product) {

                if (catagory.matches(products.getSortproduct())) {

                    listExpHeader.add(catagory);
                }
            }
        }
        hs.addAll(listExpHeader);
        listDataHeader.clear();
        listDataHeader.addAll(hs);
    }

    public static String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }

    public class PrintPreviewAdapter extends BaseAdapter {
        List<ProductDetails> productdet;
        List<ProductDetails> product;
        private int mResource;
        List<String> invoiceheader;
        private LayoutInflater mInflater;
        Context activity;

        public PrintPreviewAdapter(Context context, int resource,
                                   List<ProductDetails> prodet) {
            this.product = prodet;
            mResource = resource;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.activity = context;
        }

        @Override
        public int getCount() {
            return product.size();
        }

        @Override
        public ProductDetails getItem(int position) {
            return product.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ProductDetails productdetails = product.get(position);

            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(mResource, parent, false);
                holder.sno = (TextView) convertView.findViewById(R.id.sno);
                holder.description = (TextView) convertView
                        .findViewById(R.id.description);
                holder.qty = (TextView) convertView.findViewById(R.id.qty);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.total = (TextView) convertView.findViewById(R.id.total);
                holder.consignmentNo = (TextView)convertView.findViewById(R.id.consignmentNo);
                holder.consignmentNoLayout =(LinearLayout)convertView.findViewById(R.id.consignmentNoLayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.description.setText(productdetails.getDescription());

            if (title.getText().toString().matches("SalesOrder")
                    || title.getText().toString().matches("DeliveryOrder")
                    || title.getText().toString().matches("Consignment")
                    || title.getText().toString().matches("Consignment Return")
                    || title.getText().toString().matches("SalesReturn")
                    || title.getText().toString().matches("GRA")) {
                holder.sno.setText(productdetails.getItemcode());
                String decimal= MobileSettingsSetterGetter.getDecimalPoints();
                String showcartonloose = SalesOrderSetGet
                        .getCartonpriceflag();
                Log.d("showcartonloose","-->"+showcartonloose+productdetails.getTotal()+productdetails.getPrice());
                if(showcartonloose.equalsIgnoreCase("1")){
                    holder.price.setText(String.format("%."+decimal+"f", Double.parseDouble(productdetails.getPrice())));
                    holder.total.setText(productdetails.getTotal());
                }else{
                    holder.price.setText(productdetails.getPrice());
                    holder.total.setText(productdetails.getTotal());
                }

                String consignmentNo = productdetails.getConsignmentNumber();
                Log.d("consignmentChecks",""+consignmentNo);
                if(consignmentNo.matches("")){
                }else{
                    holder.consignmentNoLayout.setVisibility(View.VISIBLE);
                    holder.consignmentNo.setText(productdetails.getConsignmentNumber());
                }

            } else {
                holder.sno.setText(productdetails.getItemcode());
                holder.price.setVisibility(View.GONE);
                holder.total.setVisibility(View.GONE);
            }
            if (title.getText().toString().matches("DeliveryOrder")) {
                if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")) {
                    holder.price.setVisibility(View.GONE);
                    holder.total.setVisibility(View.GONE);
                } else {
                    holder.price.setVisibility(View.VISIBLE);
                    holder.total.setVisibility(View.VISIBLE);
                }
                holder.qty.setText(productdetails.getQty());
            }
            else if (title.getText().toString().matches("Consignment")) {
                if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")) {
                    holder.price.setVisibility(View.GONE);
                    holder.total.setVisibility(View.GONE);
                } else {
                    holder.price.setVisibility(View.VISIBLE);
                    holder.total.setVisibility(View.VISIBLE);
                }
                holder.qty.setText(productdetails.getQty());
            }
            else if (title.getText().toString().matches("Consignment Return")) {
//                if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")) {
                holder.price.setVisibility(View.GONE);
                holder.total.setVisibility(View.GONE);
//                } else {
//                    holder.price.setVisibility(View.VISIBLE);
//                    holder.total.setVisibility(View.VISIBLE);
//                }
                holder.qty.setText(productdetails.getQty());
            }

            else if (title.getText().toString().matches("SalesOrder")) {
                if (FormSetterGetter.getHidePrice() != null && !FormSetterGetter.getHidePrice().isEmpty()) {
                    if (FormSetterGetter.getHidePrice().matches("Hide Price")) {
                        holder.price.setVisibility(View.GONE);
                        holder.total.setVisibility(View.GONE);
                    } else {
                        holder.price.setVisibility(View.VISIBLE);
                        holder.total.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.price.setVisibility(View.VISIBLE);
                    holder.total.setVisibility(View.VISIBLE);
                }
                double qty = productdetails.getQty().equals("") ? 0 : Double.valueOf(productdetails.getQty());

                holder.qty.setText(decimalPoint(qty));

            } else {
                holder.qty.setText(productdetails.getQty());
            }


            return convertView;
        }

        class ViewHolder {
            TextView sno;
            TextView description;
            TextView qty;
            TextView price;
            TextView total;
            LinearLayout consignmentNoLayout;
            TextView consignmentNo;

        }
    }

    private void print(String titlesr) throws IOException {
        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
        helper.dismissProgressDialog();
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        try {


            if (titlesr.matches("SalesOrder")) {
                Printer printer = new Printer(CommonPreviewPrint.this, macaddress);
                printer.printSalesOrder(sno_str, date_str, custcode_str,
                        custname_str, product, productdet, 1);
            } else if (titlesr.matches("DeliveryOrder")) {
                if (printertype.matches("Zebra iMZ320")) {
                    Printer printer = new Printer(CommonPreviewPrint.this, macaddress);
                    printer.printDeliveryOrder(sno_str, date_str, custcode_str,
                            custname_str, product, productdet, 1);
                } else if (printertype.matches("Zebra iMZ320 4 Inch")) {
                    PrinterZPL printer = new PrinterZPL(CommonPreviewPrint.this, macaddress);
                    printer.printDeliveryOrder(sno_str, date_str, custcode_str,
                            custname_str, product, productdet, 1);
                } else if (printertype.matches("3 Inch Bluetooth Generic")) {
                    final CubePrint print = new CubePrint(CommonPreviewPrint.this, macaddress);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                        @Override
                        public void initCompleted() {
                            print.printDeliveryOrder(sno_str, date_str, custcode_str,
                                    custname_str, product, productdet, 1);
                            print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                                @Override
                                public void onCompleted() {
                                    helper.showLongToast(R.string.printed_successfully);
                                }
                            });
                        }
                    });
                }
            }
            else if (titlesr.matches("Consignment")) {
                if (printertype.matches("Zebra iMZ320")) {
                    Printer printer = new Printer(CommonPreviewPrint.this, macaddress);
                    printer.printConsignmentOrder(sno_str, date_str, custcode_str,
                            custname_str, product, productdet, 1,tran_type,durationDays);
                } else if (printertype.matches("Zebra iMZ320 4 Inch")) {
                    PrinterZPL printer = new PrinterZPL(CommonPreviewPrint.this, macaddress);
                    printer.printConsignmentOrder(sno_str, date_str, custcode_str,
                            custname_str, product, productdet, 1);
                } else if (printertype.matches("3 Inch Bluetooth Generic")) {
                    final CubePrint print = new CubePrint(CommonPreviewPrint.this, macaddress);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                        @Override
                        public void initCompleted() {
                            print.printConsignment(sno_str, date_str, custcode_str,
                                    custname_str, product, productdet, 1);
                            print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                                @Override
                                public void onCompleted() {
                                    helper.showLongToast(R.string.printed_successfully);
                                }
                            });
                        }
                    });
                }
            }
            else if (titlesr.matches("Consignment Return")) {
                if (printertype.matches("Zebra iMZ320")) {
                    Printer printer = new Printer(CommonPreviewPrint.this, macaddress);
                    printer.printConsignmentOrder(sno_str, date_str, custcode_str,
                            custname_str, product, productdet, 1,tran_type,durationDays);
                } else if (printertype.matches("Zebra iMZ320 4 Inch")) {
                    PrinterZPL printer = new PrinterZPL(CommonPreviewPrint.this, macaddress);
                    printer.printConsignmentOrder(sno_str, date_str, custcode_str,
                            custname_str, product, productdet, 1);
                } else if (printertype.matches("3 Inch Bluetooth Generic")) {
                    final CubePrint print = new CubePrint(CommonPreviewPrint.this, macaddress);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                        @Override
                        public void initCompleted() {
                            print.printConsignment(sno_str, date_str, custcode_str,
                                    custname_str, product, productdet, 1);
                            print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                                @Override
                                public void onCompleted() {
                                    helper.showLongToast(R.string.printed_successfully);
                                }
                            });
                        }
                    });
                }
            }
            else if (titlesr.matches("SalesReturn")) {
                flag = "SalesReturn";
                if (printertype.matches("Zebra iMZ320")) {
                    Printer printer = new Printer(CommonPreviewPrint.this, macaddress);
                    printer.printSalesReturn(sno_str, date_str, custcode_str,
                            custname_str, product, productdet, listDataHeader, appPrintGroup, 1);
                } else if (printertype.matches("4 Inch Bluetooth")) {

                    helper.updateProgressDialog(CommonPreviewPrint.this.getString(R.string.creating_file_for_printing));
                    if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
                        BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
                        // Attempt to connect to the device
                        GlobalData.mService.setHandler(mHandler);
                        GlobalData.mService.connect(device, true);

                    }
                    //helper.dismissProgressDialog();
                } else if (printertype.matches("3 Inch Bluetooth Generic")) {
                    try {
                        final CubePrint print = new CubePrint(CommonPreviewPrint.this, macaddress);
                        print.initGenericPrinter();
                        print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                            @Override
                            public void initCompleted() {
                                print.printSalesReturn(sno_str, date_str, custcode_str,
                                        custname_str, product, productdet, 1);

                                print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                                    @Override
                                    public void onCompleted() {
                                        helper.showLongToast(R.string.printed_successfully);
                                    }
                                });
                            }
                        });

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (printertype.matches("Zebra iMZ320 4 Inch")) {
                    PrinterZPL printer1 = new PrinterZPL(CommonPreviewPrint.this, macaddress);
                    printer1.printSalesReturn(sno_str, date_str, custcode_str,
                            custname_str, product, productdet, listDataHeader, appPrintGroup, 1);
                }
            } else if (titlesr.matches("StockRequest") || titlesr.matches("Transfer")) {
                flag = "Transfer";
                if (printertype.matches("Zebra iMZ320")) {
                    Printer printer = new Printer(CommonPreviewPrint.this, macaddress);
                    printer.printStockRequest(sno_str, date_str, custcode_str,
                            custname_str, product, transfer_stockreq, 1, cus_remarks);
                } else if (printertype.matches("4 Inch Bluetooth")) {

                    helper.updateProgressDialog(CommonPreviewPrint.this.getString(R.string.creating_file_for_printing));
                    if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
                        BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
                        // Attempt to connect to the device
                        GlobalData.mService.setHandler(mHandler);
                        GlobalData.mService.connect(device, true);

                    }
                    //helper.dismissProgressDialog();
                } else if (printertype.matches("3 Inch Bluetooth Generic")) {
                    try {
                        final CubePrint print = new CubePrint(CommonPreviewPrint.this, macaddress);
                        print.initGenericPrinter();
                        print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                            @Override
                            public void initCompleted() {
                                try {
                                    print.printTransfer(sno_str, date_str, custcode_str,
                                            custname_str, product, transfer_stockreq, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                                    @Override
                                    public void onCompleted() {
                                        helper.showLongToast(R.string.printed_successfully);
                                    }
                                });
                            }
                        });

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            }

        } catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        }
    }
/*	private class AsyncPrintCall extends AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			
			product.clear();
			productdet.clear();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("SoNo", sno_str);

			jsonString = SalesOrderWebService.getSODetail(hashValue,
					"fncGetSODetail");
			jsonStr = SalesOrderWebService.getSODetail(hashValue,
					"fncGetSOHeaderBySoNo");

			Log.d("jsonString ", jsonString);
			Log.d("jsonStr ", jsonStr);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

				jsonResp = new JSONObject(jsonStr);
				jsonSecNode = jsonResp.optJSONArray("SODetails");

			} catch (JSONException e) {

				e.printStackTrace();
			}

			*//*********** Process each JSON Node ************//*
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				*/

    /****** Get Object for each JSON node. ***********//*
				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonMainNode.getJSONObject(i);

					productdetail.setItemcode(jsonChildNode.optString(
							"ProductCode").toString());
					productdetail.setDescription(jsonChildNode.optString(
							"ProductName").toString());

					String salesOrderqty = jsonChildNode.optString("Qty")
							.toString();
					if (salesOrderqty.contains(".")) {
						StringTokenizer tokens = new StringTokenizer(
								salesOrderqty, ".");
						String qty = tokens.nextToken();
						productdetail.setQty(qty);
					} else {
						productdetail.setQty(salesOrderqty);
					}

					productdetail.setPrice(jsonChildNode.optString("Price")
							.toString());
					productdetail.setTotal(jsonChildNode.optString("Total")
							.toString());
					product.add(productdetail);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			int lengJsonArr = jsonSecNode.length();
			for (int i = 0; i < lengJsonArr; i++) {

				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonSecNode.getJSONObject(i);
					productdetail.setItemdisc(jsonChildNode.optString(
							"ItemDiscount").toString());
					productdetail.setBilldisc(jsonChildNode.optString(
							"BillDIscount").toString());
					productdetail.setSubtotal(jsonChildNode.optString(
							"SubTotal").toString());
					productdetail.setTax(jsonChildNode.optString("Tax")
							.toString());
					productdetail.setNettot(jsonChildNode.optString("NetTotal")
							.toString());
					productdet.add(productdetail);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			hashValue.clear();
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			try {

				print();
			} catch (IOException e) {

				e.printStackTrace();

			}

		}

	}*/
    public void alertDialogPrint(final String titlestr) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CommonPreviewPrint.this);
        alertDialog.setTitle("Print");
        alertDialog.setMessage("Do you want to print the" + " " + titlestr);
        //alertDialog.setIcon(R.drawable.slidemenu_exit);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (titlestr.matches("SalesOrder")) {
                                helper.showProgressDialog(R.string.generating_so);
                            } else if (titlestr.matches("DeliveryOrder")) {
                                helper.showProgressDialog(R.string.generating_do);
                            } else if (titlestr.matches("SalesReturn")) {
                                dialog.dismiss();
                                helper.showProgressDialog(R.string.generating_sr);

                            } else if (titlestr.matches("StockRequest")) {
                                helper.showProgressDialog(R.string.stockrequest_printpreview);
                                transfer_stockreq = "STOCK REQUEST";
                            } else if (titlestr.matches("Transfer")) {
                                helper.showProgressDialog(R.string.generating_transfer);
                                transfer_stockreq = "TRANSFER";
                            } else if (titlestr.matches("Consignment")) {
                                helper.showProgressDialog(R.string.generating_consignment);
//                                transfer_stockreq = "TRANSFER";
                            }
                            else if (titlestr.matches("Consignment Return")) {
                                helper.showProgressDialog(R.string.generating_consignment_return);
//                                transfer_stockreq = "TRANSFER";
                            }
                            print(titlestr);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static String decimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("###.#");
        String tot = df.format(d);

        return tot;
    }

    @Override
    public void onStart() {
        super.onStart();

        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

        if (printertype.matches("4 Inch Bluetooth")) {
            if (!GlobalData.mBluetoothAdapter.isEnabled()) {
                GlobalData.mBluetoothAdapter.enable();
                //Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivity(enableIntent);*/
                // Otherwise, setup the chat session
            } else {
                if (GlobalData.mService == null) {
                    GlobalData.mService = new BluetoothService(this, mHandler);
                }
            }
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GlobalData.MESSAGE_STATE_CHANGE:
                    Log.d("case", "MESSAGE_STATE_CHANGE");
                    switch (msg.arg1) {
                        case GlobalData.STATE_CONNECTED:

                            Log.d("case", "STATE_CONNECTED");
                            print4Inch();
                            //helper.dismissProgressDialog();
                            break;
                        case GlobalData.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            Log.d("case", "STATE_CONNECTING");

                            break;
                        case GlobalData.STATE_LISTEN:
                            Log.d("case", "STATE_LISTEN");
                            break;
                        case GlobalData.STATE_NONE:
                            Log.d("case", "STATE_NONE");
                            //mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case GlobalData.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case GlobalData.MESSAGE_TOAST:

                    //String macaddress = FWMSSettingsDatabase.getPrinterAddress();
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();

                    reconnectDialog(msg.getData().getString("toast"));

                    break;
            }

        }
    };

    protected void onDestroy() {
        super.onDestroy();
        if (GlobalData.mService != null) {
            GlobalData.mService.stop();
        }
    }

    public void reconnectDialog(String msg) {
        final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        final Dialog dialog = new Dialog(CommonPreviewPrint.this);

        dialog.setContentView(R.layout.dialog_reconnect);
        dialog.setTitle(msg);
        ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

        reconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
                    BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
                    // Attempt to connect to the device
                    GlobalData.mService.setHandler(mHandler);
                    GlobalData.mService.connect(device, true);

                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void print4Inch() {
        CubePrint mPrintCube = new CubePrint(CommonPreviewPrint.this, FWMSSettingsDatabase.getPrinterAddress());
        mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
            @Override
            public void onCompleted() {
                helper.showLongToast(R.string.printed_successfully);

            }
        });
		/*printer.printSalesReturn(sno_str, date_str, custcode_str,
							custname_str, product, productdet, 1);*/

        if (flag.matches("Transfer")) {
            try {
                mPrintCube.printTransfer(sno_str, date_str, custcode_str,
                        custname_str, product, transfer_stockreq, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mPrintCube.printSalesReturn(sno_str, date_str, custcode_str,
                    custname_str, product, productdet, 1);

        }

    }

    public class PreviewAttributeAdapter extends BaseExpandableListAdapter {

        private Context context;
        private ArrayList<ProductDetails> prodList;

        public PreviewAttributeAdapter(Context context, ArrayList<ProductDetails> productList) {
            this.context = context;
            this.prodList = productList;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            ArrayList<Product> productList = prodList.get(groupPosition).getAttributeList();
            return productList.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {

            Product product = (Product) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.item_preview_attribute, null);
            }

            TextView txtColorName = (TextView) convertView.findViewById(R.id.colorName);
            TextView txtSizeName = (TextView) convertView.findViewById(R.id.sizeName);
            TextView txtQty = (TextView) convertView.findViewById(R.id.qty);

            txtColorName.setText("Color : " + product.getColorName());

            Log.d(product.getColorName(), " -- " + product.getSizeName() + " --" + product.getQty());
            txtSizeName.setText("Size : " + product.getSizeName());
            txtQty.setText("Qty : " + product.getQty());


            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            ArrayList<Product> productList = prodList.get(groupPosition).getAttributeList();
            return productList.size();

        }

        @Override
        public Object getGroup(int groupPosition) {
            return prodList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return prodList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {

            ProductDetails productdetails = (ProductDetails) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.printpreview_listitem, null);
            }

            TextView sno = (TextView) convertView.findViewById(R.id.sno);
            TextView description = (TextView) convertView
                    .findViewById(R.id.description);
            TextView qty = (TextView) convertView.findViewById(R.id.qty);
            TextView price = (TextView) convertView.findViewById(R.id.price);
            TextView total = (TextView) convertView.findViewById(R.id.total);

            description.setText(productdetails.getDescription());

            sno.setText(productdetails.getItemcode());
            price.setText(productdetails.getPrice());
            total.setText(productdetails.getTotal());


            if (FormSetterGetter.getHidePrice() != null && !FormSetterGetter.getHidePrice().isEmpty()) {
                if (FormSetterGetter.getHidePrice().matches("Hide Price")) {
                    price.setVisibility(View.GONE);
                    total.setVisibility(View.GONE);
                } else {
                    price.setVisibility(View.VISIBLE);
                    total.setVisibility(View.VISIBLE);
                }
            } else {
                price.setVisibility(View.VISIBLE);
                total.setVisibility(View.VISIBLE);
            }
            double qt = productdetails.getQty().equals("") ? 0 : Double.valueOf(productdetails.getQty());

            qty.setText(decimalPoint(qt));

            ExpandableListView eLV = (ExpandableListView) parent;
            eLV.expandGroup(groupPosition);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


    }

    @Override
    public void onBackPressed() {

        if (title.getText().toString().matches("Transfer")) {
            Intent i = new Intent(this, TransferHeader.class);
            startActivity(i);
        }

        CommonPreviewPrint.this.finish();

    }
}
