package com.winapp.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.model.CurrencyDeno;
import com.winapp.sot.Company;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SettlementMainHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettlementPrintPreview extends Activity {
    private ArrayList<CurrencyDeno> product, productdet;
    private ListView lv;
    private TextView no,date,lctnCode,user,cname,address,country,pincode,total;
    String sno_str,date_str,total_str,companyname_str, custcode_str, address1_str, address2_str, country_str, phone_str,zipcode_str,lctn_str,user_str;
    private LinearLayout companyname_ll, address1_ll, address2_ll, country_ll,
            phone_ll;
    SettlementPreviewAdapter adapter;
    ImageView print_iv;
    private UIHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settlement_print_preview);
        no = (TextView)findViewById(R.id.no);
        date = (TextView)findViewById(R.id.date);
        lctnCode = (TextView)findViewById(R.id.custcode);
        user = (TextView)findViewById(R.id.custname);
        lv = (ListView)findViewById(R.id.preview_list);
        cname = (TextView)findViewById(R.id.companyname);
        address = (TextView)findViewById(R.id.address1);
        country = (TextView)findViewById(R.id.country);
        pincode = (TextView)findViewById(R.id.phone);
        total = (TextView)findViewById(R.id.subtotal);
        companyname_ll = (LinearLayout) findViewById(R.id.companyname_ll);
        address1_ll = (LinearLayout) findViewById(R.id.address1_ll);
        address2_ll = (LinearLayout) findViewById(R.id.address2_ll);
        country_ll = (LinearLayout) findViewById(R.id.country_ll);
        phone_ll = (LinearLayout) findViewById(R.id.phone_ll);
        print_iv = (ImageView)findViewById(R.id.printer);
        helper = new UIHelper(SettlementPrintPreview.this);

        product = new ArrayList<CurrencyDeno>();
        productdet = new ArrayList<CurrencyDeno>();
        product.clear();
        productdet.clear();

        SOTDatabase.init(SettlementPrintPreview.this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            sno_str = b.getString("No");
            date_str = b.getString("Date");
            total_str = b.getString("Total");
//            lctn_str = b.getString("location");
//            user_str = b.getString("user");

            no.setText(sno_str);
            date.setText(date_str);
            total.setText(total_str);
//            lctnCode.setText(lctn_str);
//            user.setText(user_str);

        }
        product = PreviewPojo.getSettlementDetail();
        productdet = PreviewPojo.getSettlementHeader();

        for(int i =0;i<productdet.size();i++){
            lctn_str =productdet.get(i).getLocationcode();
            user_str = productdet.get(i).getSettlementBy();
            lctnCode.setText(productdet.get(i).getLocationcode());
            user.setText(productdet.get(i).getSettlementBy());
            total.setText(productdet.get(i).getTotlAmt());
        }

        Log.d("productCount","-->"+product.size()+"-->"+productdet.size());

        companyname_str = Company.getCompanyName();
        address1_str = Company.getAddress1();
        address2_str = Company.getAddress2();
        country_str = Company.getCountry();
        phone_str = Company.getPhoneNo();
        zipcode_str = Company.getZipCode();
        if (companyname_str.matches("")) {
            companyname_ll.setVisibility(View.GONE);
        } else {
            cname.setText(companyname_str);
        }

        if (address1_str.matches("")) {
            address1_ll.setVisibility(View.GONE);
        } else {
            address.setText(address1_str);
        }

        if (address2_str.matches("")) {
            address2_ll.setVisibility(View.GONE);
        } else {
            address.setText(address2_str);
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
            pincode.setText(phone_str);
        }

        print_iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                title_str = title.getText().toString();
                alertDialogPrint();
            }
        });

        adapter = new SettlementPreviewAdapter(SettlementPrintPreview.this,product);
        lv.setAdapter(adapter);

    }

    private void alertDialogPrint() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettlementPrintPreview.this);
        alertDialog.setTitle("Print");
        alertDialog.setMessage("Do you want to print the settlement");
        //alertDialog.setIcon(R.drawable.slidemenu_exit);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            helper.showProgressDialog(R.string.generating_settlement);
                            print();
                    } catch (Exception e) {
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

    private void print() {
        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
        helper.dismissProgressDialog();
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        Printer printer = new Printer(SettlementPrintPreview.this, macaddress);
        printer.printSettlement(sno_str, date_str, total_str,lctn_str,user_str, product, productdet, 1);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettlementPrintPreview.this,SettlementMainHeader.class);
        startActivity(intent);
        finish();
    }

    private class SettlementPreviewAdapter extends BaseAdapter{
    Context context;
    List<CurrencyDeno> demoList;
        private LayoutInflater mInflater;
        public SettlementPreviewAdapter(Context context, List<CurrencyDeno> product) {
            this.context = context;
            this.demoList = product;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return demoList.size();
        }

        @Override
        public Object getItem(int position) {
            return demoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final CurrencyDeno demoList = product.get(position);

            final ViewHolder holder;
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.settlement_preview, null);
            holder.sno = (TextView)convertView.findViewById(R.id.sno);
            holder.denomination = (TextView)convertView.findViewById(R.id.description);
            holder.count = (TextView)convertView.findViewById(R.id.qty);
            holder.total = (TextView)convertView.findViewById(R.id.price);

            holder.sno.setText(demoList.getSlno());
            holder.denomination.setText(demoList.getCurency());
            holder.count.setText(demoList.getDenomination());
            holder.total.setText(demoList.getTotal());

            return convertView;
        }
        class ViewHolder{
            TextView sno,denomination,count,total;
        }
    }
}
