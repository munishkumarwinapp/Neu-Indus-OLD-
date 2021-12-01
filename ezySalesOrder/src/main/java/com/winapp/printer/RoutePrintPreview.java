package com.winapp.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.sot.CaptureSignature;
import com.winapp.sot.Company;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SOTSummaryWebService;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.trackwithmap.DeliveryOrderNewHeader;
import com.winapp.trackwithmap.ScheduleDataNew;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RoutePrintPreview extends Activity {
    private List<ProductDetails> product;
    private List<ProductDetails> prodet;
    private List<ProductDetails> productdet;
    private ListView lv;
    private ImageButton back;
    private TextView title, invoiceno, invoicedate, custcode, custname,
            itemdisc, billdisc, subtotal, tax, nettotal, remarks,
            totaloutstanding, companyname, address1, address2, country, phone;
    private LinearLayout receiptno_ll, receiptdate_ll, companyname_ll,
            address1_ll, address2_ll, country_ll, phone_ll, remarks_ll,paymode_ll,bankcode_ll,chequeno_ll,chequedate_ll,
            expand_list_layout;
    private String companyname_str, address1_str, address2_str, country_str,
            phone_str, remark_str, zipcode_str, gnrlStngs,title_str,invoiceno_str,invoicedate_str,trantype,
            custcode_str,custname_str,taxType="",taxPerc="",taxName="",address_1 = "",address_2 = "",valid_url;
    private double mLatitude = 0.0,mLongitude = 0.0;
    private List<String> sort;
    private HashSet<String> hs;
    private List<String> listDataHeader;
    private HashMap<String, ArrayList<HashMap<String, String>>> listDataChild;
    private List<String> listExpHeader;

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;

    private int s = 1;
    private UIHelper helper;
    private ImageView print_iv;
    private LinearLayout sign_btn;
    public static final int SIGNATURE_ACTIVITY = 1;
    private TextView totalqty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.route_print_preview);

        GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        GlobalData.mService = new BluetoothService(this, mHandler);

        back = (ImageButton) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        invoiceno = (TextView) findViewById(R.id.invoiceno);
        invoicedate = (TextView) findViewById(R.id.invoicedate);
        custcode = (TextView) findViewById(R.id.custcode);
        custname = (TextView) findViewById(R.id.custname);
        itemdisc = (TextView) findViewById(R.id.itemdisc);
        billdisc = (TextView) findViewById(R.id.billdisc);
        subtotal = (TextView) findViewById(R.id.subtotal);
        tax = (TextView) findViewById(R.id.tax);
        nettotal = (TextView) findViewById(R.id.nettotal);
        remarks = (TextView) findViewById(R.id.remarks);
        totaloutstanding = (TextView) findViewById(R.id.totaloutstanding);

        companyname = (TextView) findViewById(R.id.companyname);
        address1 = (TextView) findViewById(R.id.address1);
        address2 = (TextView) findViewById(R.id.address2);
        country = (TextView) findViewById(R.id.country);
        phone = (TextView) findViewById(R.id.phone);
        totalqty = (TextView) findViewById(R.id.totalqty);
        sign_btn = (LinearLayout) findViewById(R.id.sign);
        print_iv = (ImageView) findViewById(R.id.printer);
        receiptno_ll = (LinearLayout) findViewById(R.id.receiptno_ll);
        receiptdate_ll = (LinearLayout) findViewById(R.id.receiptdate_ll);

        companyname_ll = (LinearLayout) findViewById(R.id.companyname_ll);
        address1_ll = (LinearLayout) findViewById(R.id.address1_ll);
        address2_ll = (LinearLayout) findViewById(R.id.address2_ll);
        country_ll = (LinearLayout) findViewById(R.id.country_ll);
        phone_ll = (LinearLayout) findViewById(R.id.phone_ll);
        remarks_ll = (LinearLayout) findViewById(R.id.remarks_ll);

        paymode_ll = (LinearLayout) findViewById(R.id.paymode_ll);
        bankcode_ll = (LinearLayout) findViewById(R.id.bankcode_ll);
        chequeno_ll = (LinearLayout) findViewById(R.id.chequeno_ll);
        chequedate_ll = (LinearLayout) findViewById(R.id.chequedate_ll);
        expand_list_layout  = (LinearLayout) findViewById(R.id.expand_list_layout);

        expListView = (ExpandableListView) findViewById(R.id.preview_list);
        lv = (ListView) findViewById(R.id.preview_list1);
        product = new ArrayList<ProductDetails>();
        prodet = new ArrayList<ProductDetails>();
        productdet = new ArrayList<ProductDetails>();
        sort = new ArrayList<String>();
        hs = new HashSet<String>();
        helper = new UIHelper(RoutePrintPreview.this);
        listDataHeader = new ArrayList<String>();
        listExpHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, ArrayList<HashMap<String, String>>>();
        listExpHeader.clear();

        SOTDatabase.init(RoutePrintPreview.this);
        SOTDatabase.deleteImage();
        valid_url = FWMSSettingsDatabase.getUrl();
        new SOTSummaryWebService(valid_url);

        paymode_ll.setVisibility(View.GONE);
        bankcode_ll.setVisibility(View.GONE);
        chequeno_ll.setVisibility(View.GONE);
        chequedate_ll.setVisibility(View.GONE);

        trantype = ScheduleDataNew.getTrantype();

        Bundle b = getIntent().getExtras();
        title.setText("Invoice");
        receiptno_ll.setVisibility(View.GONE);
        receiptdate_ll.setVisibility(View.GONE);
        if (b != null) {
            invoiceno_str = b.getString("invNo");
            invoicedate_str = b.getString("invDate");
            custcode_str = b.getString("customerCode");
            custname_str = b.getString("customerName");
            mLatitude = b.getDouble("destlat");
            mLongitude = b.getDouble("destlng");
            address_1 = b.getString("address1");
            address_2 = b.getString("address2");
            invoiceno.setText(invoiceno_str);
            invoicedate.setText(invoicedate_str);
            custcode.setText(custcode_str);
            custname.setText(custname_str);
			/*invoiceno.setText(b.getString("invNo"));
			invoicedate.setText(b.getString("invDate"));
			custcode.setText(b.getString("customerCode"));
			custname.setText(b.getString("customerName"));*/
            sort = b.getStringArrayList("sort");
            gnrlStngs = b.getString("gnrlStngs");
            Log.d("mLatitude", String.valueOf(mLatitude));
            Log.d("mLongitude", String.valueOf(mLongitude));
        }
        Log.d("sort", "" + sort);
        Log.d("gnrlStngs", gnrlStngs);
        product = PreviewPojo.getProducts();
        productdet = PreviewPojo.getProductsDetails();
        taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
        totalqty.setText(""+totalQty());
        Log.d("totalqty",totalqty.getText().toString());
        if(taxPerc==null || taxPerc.trim().equals("")){
            taxPerc = "0.00";
        }
        for (ProductDetails products : product) {
            taxType = products.getTaxType();
            //taxPerc = products.getTaxPerc();
        }
        for (int i = 0; i < productdet.size(); i++) {
            ProductDetails productdetails = productdet.get(i);
            if(taxType.equalsIgnoreCase("I")){
                taxName = "Incl "+taxPerc.split("\\.")[0]+"%";
            }else if(taxType.equalsIgnoreCase("E")){
                taxName = "Excl "+taxPerc.split("\\.")[0]+"%";
            }else{
                taxName = "Tax";
            }
            ((TextView) findViewById(R.id.tax_txt)).setText(taxName+" :");
            Log.d("taxType","-->"+taxType);
            /********************/
            //If taxtype is I then subtotal = netotal - tax
            String netTotal = productdetails.getNettot().toString();
            if(netTotal!=null && !netTotal.isEmpty()){

            }else{
                netTotal = "0.00";
            }
            String taxStr = productdetails.getTax().toString();
            if(taxStr!=null && !taxStr.isEmpty()){

            }else{
                taxStr = "0.00";
            }
            double dNetTotal = Double.valueOf(netTotal);
            double dTax = Double.valueOf(taxStr);
            double dSubtotal =  dNetTotal - dTax;
            Log.d("dNetTotal","-->"+dNetTotal);
            Log.d("dTax","-->"+dTax);
            Log.d("subtotal","-->"+dSubtotal);

            itemdisc.setText(productdetails.getItemdisc());
            billdisc.setText(productdetails.getBilldisc());

            if(taxType!=null && !taxType.isEmpty()){
                if (taxType.matches("I")) {
                    subtotal.setText(twoDecimalPoint(dSubtotal));
                }else{
                    subtotal.setText(productdetails.getSubtotal());
                }
            }else{
                subtotal.setText(productdetails.getSubtotal());
            }
            tax.setText(productdetails.getTax());
            nettotal.setText(productdetails.getNettot());
            remark_str = productdetails.getRemarks();
            if (remark_str.matches("")) {
                remarks_ll.setVisibility(View.GONE);
            } else {
                remarks.setText(remark_str);
            }
            totaloutstanding.setText(productdetails.getTotaloutstanding());
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
            // country.setText(country_str);
            country.setText(country_str + " " + zipcode_str);
        }

        if (phone_str.matches("")) {
            phone_ll.setVisibility(View.GONE);
        } else {
            phone.setText(phone_str);
        }
        if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {
            int no = 1;
			/* sort product by None */
            for (ProductDetails products : product) {

                ProductDetails prod = new ProductDetails();
                if ((products.getSortproduct().matches(""))
                        || (products.getSortproduct().matches("0"))) {
                    int i = 1;
                    // prod.setSno(products.getSno());
                    prod.setSno(String.valueOf(no));
                    prod.setDescription(products.getDescription());
                    prod.setQty(products.getQty());
                    prod.setPrice(products.getPrice());
                    prod.setTotal(products.getTotal());
                    prodet.add(prod);
                    s += i;
                    i++;
                    no++;
                }
            }
            if (prodet.size() > 0) {
                PrintPreviewAdapter ppa = new PrintPreviewAdapter(
                        RoutePrintPreview.this,
                        R.layout.route_printpreview_listitem, prodet);
                lv.setAdapter(ppa);
            } else {
                lv.setVisibility(View.GONE);
            }

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
            prepareListData();

            if (listDataChild.size() > 0) {
                listAdapter = new ExpandableListAdapter(this, listDataHeader,
                        listDataChild);
                expListView.setAdapter(listAdapter);
                for (int i = 0; i < listAdapter.getGroupCount(); i++) {
                    expListView.expandGroup(i);
                }
            } else {
                expListView.setVisibility(View.GONE);
                expand_list_layout.setVisibility(View.GONE);
            }
        } else {
            Log.d("product--<", "" + product.size());
            expListView.setVisibility(View.GONE);
            expand_list_layout.setVisibility(View.GONE);
            PrintPreviewAdapter ppa = new PrintPreviewAdapter(
                    RoutePrintPreview.this, R.layout.route_printpreview_listitem,
                    product);
            lv.setAdapter(ppa);
        }

        print_iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                title_str =title.getText().toString();
                alertDialogPrint(title_str);
            }
        });

        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RoutePrintPreview.this,
                        CaptureSignature.class);
                startActivityForResult(i, SIGNATURE_ACTIVITY);
            }
        });
    }

    public int totalQty() {
        int totalCharge = 0;
        Log.d("product", "product-->" + product.size());
        for (ProductDetails data : product) {
            String qty = data.getQty();
             int iqty = Integer.parseInt(qty);
            if(iqty != 0){
                totalCharge += iqty;
            }
        }
        return  totalCharge;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//			if (data != null) {
        switch (requestCode) {

            case SIGNATURE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    //Bundle extras = data.getExtras();
                    byte[] bytes = data.getByteArrayExtra("status");
                    if (bytes != null) {
                        //Bitmap photo = extras.getParcelable("status");
                       // helper.updateProgressDialog("");

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80, true);

                      //  mSignature.setImageBitmap(bitmap);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bitMapData = stream.toByteArray();
                        String signature_image = Base64.encodeToString(
                                bitMapData, Base64.DEFAULT);
                        SOTDatabase.init(RoutePrintPreview.this);
//                        Cursor ImgCursor = SOTDatabase.getImageCursor();
//                        if (ImgCursor.getCount() > 0) {
//                            String camera_image = SOTDatabase.getProductImage();
//                            SOTDatabase.updateImage(1, signature_image,
//                                    camera_image);
//                        } else {
//                            SOTDatabase.storeImage(1, signature_image, "");
//                        }

                        Log.d("Signature Image", "Sig" + signature_image);

                        String signatureResult = SOTSummaryWebService.saveSignatureImage(
                                invoiceno_str, "" + mLatitude, "" + mLongitude,
                                signature_image, "", "fncSaveInvoiceImages",trantype,address_1,address_2);

                        Log.d("signatureResult-->", ""+signatureResult);

//
                        if(!signatureResult.matches("")){
                            Toast.makeText(RoutePrintPreview.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RoutePrintPreview.this,
                                    DeliveryOrderNewHeader.class);
                            startActivity(i);
                            RoutePrintPreview.this.finish();
                        }else{
                            Toast.makeText(RoutePrintPreview.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                      //  helper.dismissProgressDialog();
                    }
                }
                break;

        }
    }
    public static String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }
    private void prepareListData() {

        for (int i = 0; i < listDataHeader.size(); i++) {
            ArrayList<HashMap<String, String>> sublistData = new ArrayList<HashMap<String, String>>();
            String headerString = listDataHeader.get(i);

            for (ProductDetails products : product) {

                if (headerString.matches(products.getSortproduct())) {
                    HashMap<String, String> sublistDataItem = new HashMap<String, String>();
                    sublistDataItem.put("Sno", String.valueOf(s));
                    // sublistDataItem.put("Sno",products.getSno());
                    sublistDataItem.put("Description",
                            products.getDescription());
                    sublistDataItem.put("Qty", products.getQty());
                    sublistDataItem.put("cat", products.getSortproduct());
                    sublistDataItem.put("Price", products.getPrice());
                    sublistDataItem.put("Total", products.getTotal());
                    sublistData.add(sublistDataItem);
                    s++;
                }
                Log.d("sublistData -->", "" + sublistData.size());
            }
            Log.d("sublistData", sublistData.toString());
            listDataChild.put(headerString, sublistData);
        }
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.sno.setText(productdetails.getSno());
            holder.description.setText(productdetails.getDescription());
            holder.qty.setText(productdetails.getQty());
            holder.price.setText(productdetails.getPrice());
            holder.total.setText(productdetails.getTotal());

            return convertView;
        }

        class ViewHolder {
            TextView sno;
            TextView description;
            TextView qty;
            TextView price;
            TextView total;

        }
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, ArrayList<HashMap<String, String>>> _listDataChild;
        List<ProductDetails> product = new ArrayList<ProductDetails>();

        public ExpandableListAdapter(
                Context context,
                List<String> listDataHeader,
                HashMap<String, ArrayList<HashMap<String, String>>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(
                    this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @SuppressWarnings("unchecked")
        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final HashMap<String, String> childValue = (HashMap<String, String>) getChild(
                    groupPosition, childPosition);
            Log.d("childValue", "" + childValue.size());
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(
                        R.layout.route_printpreview_listitem, null);
            }

            TextView sno = (TextView) convertView.findViewById(R.id.sno);
            TextView description = (TextView) convertView
                    .findViewById(R.id.description);
            TextView qty = (TextView) convertView.findViewById(R.id.qty);
            TextView price = (TextView) convertView.findViewById(R.id.price);
            TextView total = (TextView) convertView.findViewById(R.id.total);

            Log.d("_listDataChild value", "" + _listDataChild.toString());
            Log.d("childPosition", "" + childPosition);
            Log.d("childValue", "" + childValue.size());

            for (int i = 0; i < childValue.size(); i++) {
                sno.setText(childValue.get("Sno"));
                description.setText(childValue.get("Description"));
                qty.setText(childValue.get("Qty"));
                price.setText(childValue.get("Price"));
                total.setText(childValue.get("Total"));
            }
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(
                    this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(
                        R.layout.preview_exp_header, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
    private void print() throws IOException {
        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
        helper.dismissProgressDialog();
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        try {
            if(printertype.matches("Zebra iMZ320")) {
                Printer printer = new Printer(RoutePrintPreview.this, macaddress);

                List<ProductDetails> product_batch = new ArrayList<ProductDetails>();
                List<ProductDetails> footer = new ArrayList<ProductDetails>();

                printer.printInvoice(invoiceno_str, invoicedate_str, custcode_str, custname_str,
                        product, productdet, listDataHeader, gnrlStngs, 1, product_batch, footer,"","","","");

            }else if(printertype.matches("4 Inch Bluetooth")){
				/*helper.showProgressDialog(InvoiceHeader.this.getString(R.string.print),
						InvoiceHeader.this.getString(R.string.creating_file_for_printing));*/
                helper.updateProgressDialog(RoutePrintPreview.this.getString(R.string.creating_file_for_printing));
                if (BluetoothAdapter.checkBluetoothAddress(macaddress))
                {
                    BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
                    // Attempt to connect to the device
                    GlobalData.mService.setHandler(mHandler);
                    GlobalData.mService.connect(device,true);

                }
                helper.dismissProgressDialog();
                //helper.dismissProgressDialog();
            }
            else if(printertype.matches("3 Inch Bluetooth Generic")){
                final List<ProductDetails> product_batch = new ArrayList<ProductDetails>();
                final List<ProductDetails> footer = new ArrayList<ProductDetails>();
                helper.dismissProgressDialog();
                try {
                    final CubePrint print = new CubePrint(RoutePrintPreview.this,macaddress);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                        @Override
                        public void initCompleted() {
                            print.printInvoice(invoiceno_str, invoicedate_str, custcode_str, custname_str,
                                    product, productdet, listDataHeader, gnrlStngs, 1, product_batch, footer,"");

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
            else if(printertype.matches("Zebra iMZ320 4 Inch")){
                helper.dismissProgressDialog();
                PrinterZPL printer = new PrinterZPL(RoutePrintPreview.this, macaddress);

                List<ProductDetails> product_batch = new ArrayList<ProductDetails>();
                List<ProductDetails> footer = new ArrayList<ProductDetails>();

                printer.printInvoice(invoiceno_str, invoicedate_str, custcode_str, custname_str,
                        product, productdet, listDataHeader, gnrlStngs, 1, product_batch, footer);
            }

        } catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //if(D) Log.e(TAG, "--- onResume ---");

    }
    @Override
    public void onStart() {
        super.onStart();
        //if(D) Log.e(TAG, "--- onStart ---");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

        if(printertype.matches("4 Inch Bluetooth")) {
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
                    Log.d("case","MESSAGE_STATE_CHANGE");
                    //if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case GlobalData.STATE_CONNECTED:
                            //mTitle.setText(R.string.title_connected_to);
                            //mTitle.append(mConnectedDeviceName);
                            //Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
                            //intent.putExtra("COMM", 0);//0-BLUETOOTH
                            // Set result and finish this Activity
                            //	startActivity(intent);
                            Log.d("case","STATE_CONNECTED");
                            print4Inch();
                            //helper.dismissProgressDialog();
                            break;
                        case GlobalData.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            Log.d("case","STATE_CONNECTING");

                            break;
                        case GlobalData.STATE_LISTEN:
                            Log.d("case","STATE_LISTEN");
                            break;
                        case GlobalData.STATE_NONE:
                            Log.d("case","STATE_NONE");
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
					/*if (BluetoothAdapter.checkBluetoothAddress(macaddress))
					{
						BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
						// Attempt to connect to the device
						GlobalData.mService.setHandler(mHandler);
						GlobalData.mService.connect(device,true);

						//print4Inch();
					}*/
                    //	helper.dismissProgressDialog();
                    break;
            }
            //helper.dismissProgressDialog();
        }
    };
    protected void onDestroy(){
        super.onDestroy();
        if(GlobalData.mService!=null){
            GlobalData.mService.stop();
        }
    }
    public void reconnectDialog(String msg){
        final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        final Dialog dialog = new Dialog(RoutePrintPreview.this);

        dialog.setContentView(R.layout.dialog_reconnect);
        dialog.setTitle(msg);
        ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BluetoothAdapter.checkBluetoothAddress(macaddress))
                {
                    BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
                    // Attempt to connect to the device
                    GlobalData.mService.setHandler(mHandler);
                    GlobalData.mService.connect(device,true);

                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void print4Inch(){
        List<ProductDetails> product_batch = new ArrayList<ProductDetails>();
        List<ProductDetails> footer = new ArrayList<ProductDetails>();
        CubePrint mPrintCube = new CubePrint(RoutePrintPreview.this,FWMSSettingsDatabase.getPrinterAddress());
        mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
            @Override
            public void onCompleted() {
                helper.showLongToast(R.string.printed_successfully);

            }
        });
        mPrintCube.printInvoice(invoiceno_str, invoicedate_str, custcode_str, custname_str,
                product, productdet, listDataHeader, gnrlStngs, 1, product_batch, footer,"");
    }
    public void alertDialogPrint(final String titlestr) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RoutePrintPreview.this);
        alertDialog.setTitle("Print");
        alertDialog.setMessage("Do you want to print the"+" "+titlestr);
        //alertDialog.setIcon(R.drawable.slidemenu_exit);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            print();
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
    @Override
    public void onBackPressed() {
         Intent i = new Intent(RoutePrintPreview.this, DeliveryOrderNewHeader.class);
         startActivity(i);
        RoutePrintPreview.this.finish();
    }
}

