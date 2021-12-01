package com.winapp.trackwithmap;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.RoutePrintPreview;
import com.winapp.printer.UIHelper;
import com.winapp.sot.CaptureSignature;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.util.CustomCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.app.Activity.RESULT_OK;

/**
 * Created by USER on 19/9/2017.
 */

public class ScheduleStatusAdapter extends RecyclerView.Adapter<ScheduleStatusAdapter.ViewHolder> {

    private List<ScheduleDataNew> scheduleDataList;
    private Activity activity;
    private static final int PICK_FROM_CAMERA = 1;
    public static final int SIGNATURE_ACTIVITY = 2;
    ImageView route_photo,route_signature;
    String postalcode,address,invoiceno,invoicedate,cust_code,customername,address_2,fulladdress,address_3,destendpoint = "";
    private Geocoder geoCoder = null;
    private double destinationLatitudeValue = 0.0, destinationLongitudeValue = 0.0;
    String jsonString = null, jsonStr = null, custjsonStr = null,jsonSt;
    CustomCalendar customCalendar;
    ArrayList<ProductDetails> product;
    ArrayList<ProductDetails> productdet,footerArr;
    JSONObject custjsonResp, jsonRespfooter;
    JSONArray jsonSecNodefooter,custjsonMainNode;
    HashMap<String, String> hashVl = new HashMap<String, String>();
    JSONObject jsonResponse, jsonResp;
    JSONArray jsonMainNode, jsonSecNode;
    HashMap<String, String> hashValue = new HashMap<String, String>();
    ArrayList<String> sort = new ArrayList<String>();
    private UIHelper helper;
    private String countryname = "";
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public ScheduleStatusAdapter(Activity activity , List<ScheduleDataNew> scheduleDataList) {
        this.activity = activity;
        this.scheduleDataList = scheduleDataList;
        product = new ArrayList<ProductDetails>();
        productdet = new ArrayList<ProductDetails>();
        footerArr = new ArrayList<ProductDetails>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_data,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try{
        geoCoder = new Geocoder(activity, Locale.getDefault());
        helper = new UIHelper(activity);
        countryname = ScheduleDataNew.getCountryname();
        Log.d("position", String.valueOf(position));
        if (position == 0) {
            holder.layout.setBackgroundColor(Color.parseColor("#ffffff"));
        }else{
//            holder.layout.setBackgroundColor(Color.parseColor("#CCCCCC"));
            holder.layout.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        ScheduleDataNew movie = scheduleDataList.get(position);
        holder.customercode.setText(movie.getCustomerCode());
        holder.delcustomername.setText(movie.getDelCustomerName());
        holder.address1.setText(movie.getDelAddress1());
        holder.address2.setText(movie.getDelAddress2());
        holder.remarks.setText(movie.getRemarks());

        String stTime = movie.getStartTime();
        String delTime = movie.getRequestedDeliveryTime();

//        String[] splited1 = stTime.split("\\s+");
//        String[] splited2 = delTime.split("\\s+");
//
//        String split_one=splited1[0];
//        String startTime=splited1[1];
//
//        String split_two=splited2[0];
//        String deliveryTime=splited2[1];

        holder.starttime.setText(stTime);
        holder.requestdeliverytime.setText(delTime);

        if(movie.getGotSignature().equalsIgnoreCase("True")){
            Log.d("truesign","truesign");
            holder.layout.setBackgroundResource(R.drawable.list_item_selected_bg);
        }else{
            Log.d("falsesign","falsesign");
            holder.layout.setBackgroundResource(R.mipmap.box_bg);
        }
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("positioncheck", String.valueOf(position));
                    setPosition(position);
                    activity.openContextMenu(view);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return scheduleDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView customercode,delcustomername,address1,address2,starttime,requestdeliverytime,remarks;
        public LinearLayout layout,option_layout;
        ImageView overflow;
        public ViewHolder(View view){
            super(view);
            customercode = (TextView) view.findViewById(R.id.customercode);
            delcustomername = (TextView) view.findViewById(R.id.delcustomername);
            address1 = (TextView) view.findViewById(R.id.address1);
            address2 = (TextView) view.findViewById(R.id.address2);
            starttime = (TextView) view.findViewById(R.id.starttime);
            requestdeliverytime = (TextView) view.findViewById(R.id.requestdeliverytime);
            remarks = (TextView) view.findViewById(R.id.remarks);
            layout = (LinearLayout) view.findViewById(R.id.listdata_layout);
            overflow = (ImageView) view.findViewById(R.id.image);
            option_layout= (LinearLayout) view.findViewById(R.id.option_layout);

            final PopupMenu popup = new PopupMenu(activity, overflow);
            popup.inflate(R.menu.deliverynewmenu);
            option_layout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public final void onClick(View v) {

                                                ScheduleDataNew movie = scheduleDataList.get(getPosition());

                                                postalcode = movie.getDelPostalCode();
                                                address =  movie.getDelAddress1();
                                                address_2 = movie.getDelAddress2();
                                                address_3 = movie.getDelAddress3();
                                                invoiceno = movie.getInvoiceNo();
                                                invoicedate = movie.getInvoiceDate();
                                                cust_code = movie.getCustomerCode();
                                                customername = movie.getDelCustomerName();
                                                destendpoint = movie.getDestendpoint();
                                                Log.d("postalcode",postalcode);
                                                Log.d("address",address);
                                                Log.d("invoiceno",invoiceno);
                                                Log.d("invoicedate",invoicedate);
                                                Log.d("cust_code",cust_code);
                                                Log.d("customername",customername);
                                                Log.d("destendpoint",destendpoint);

                                                fulladdress = countryname + " " + destendpoint ;

                                                Log.d("fulladdress",fulladdress);

                                              if(destendpoint != null && !destendpoint.isEmpty()){
                                                    getDestinationLatLngFromAddress(fulladdress);
                                                }else{
                                                    destinationLatitudeValue = 0.0;
                                                    destinationLongitudeValue = 0.0;
                                                }

                                                popup.show();
                                            }
                                        });
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.start:

                            Log.d("lat",""+destinationLatitudeValue);
                            Log.d("lon",""+destinationLongitudeValue);
                            Intent i = new Intent(activity,RouteMapActivity.class);
                            i.putExtra("destlat",destinationLatitudeValue);
                            i.putExtra("destlng",destinationLongitudeValue);
                            activity.startActivity(i);
                            activity.finish();
                            return true;
//                        case R.id.signature:
//                            showJobsDialog("Signature/Photo");
//                            return true;
                        case R.id.detail:

                           helper.showProgressDialog(R.string.invoice_printpreview);
                            AsyncPrintCall task = new AsyncPrintCall();
			                task.execute();

                            return true;

                        default:
                            return false;
                    }
                }
            });
                   // overflow.setOnTouchListener(popup.getDragToOpenListener());
        }
    }

    private void getDestinationAddressFromLatLog(String postal_code){
        List<Address> address = null;

        if (geoCoder != null) {
            try {
                address = geoCoder.getFromLocationName(postal_code, 10);
                if (address.size() > 0) {
                    Address first = address.get(0);
                    destinationLatitudeValue = first.getLatitude();
                    destinationLongitudeValue = first.getLongitude();
                    Log.d("lat","sPickUpPostal-->"+destinationLatitudeValue);
                    Log.d("lon","sPickUpPostal-->"+destinationLongitudeValue);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void getDestinationLatLngFromAddress(String addressvalue){
        List<Address> address = null;

        if (geoCoder != null) {
            try {
                address = geoCoder.getFromLocationName(addressvalue, 10);
                if (address.size() > 0) {
                    Address first = address.get(0);
                    destinationLatitudeValue = first.getLatitude();
                    destinationLongitudeValue = first.getLongitude();
                    Log.d("lat","sPickUpPostal-->"+destinationLatitudeValue);
                    Log.d("lon","sPickUpPostal-->"+destinationLongitudeValue);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
  /*  private void launchViewDialog(String title){
        LayoutInflater inflater= LayoutInflater.from(activity);
       View view=inflater.inflate(R.layout.dialog_launch, null);
        ListView listView = (ListView)view.findViewById(R.id.ListView);
        String[] items = { "Start Job", "View Job"};
        ArrayAdapter adapter_dest = new ArrayAdapter(activity,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adapter_dest);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setCancelable(false)
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(activity, ScheduleMapsActivity.class));
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }*/

    public void showJobsDialog(String title) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        TextView mTitle = (TextView) dialog.findViewById(R.id.title);

        Button route_save = (Button) dialog.findViewById(R.id.route_save);
        Button route_cancel = (Button) dialog.findViewById(R.id.cancel);

        route_photo = (ImageView) dialog.findViewById(R.id.route_photo);
        route_signature = (ImageView) dialog.findViewById(R.id.route_signature);
        EditText route_comments = (EditText) dialog.findViewById(R.id.route_comments);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.show();

//        mTitle.setText(title);

        route_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CameraAction();
            }
        });

        route_signature.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                Intent i = new Intent(activity,
                        CaptureSignature.class);
                activity.startActivityForResult(i, 2);

            }
        });

        route_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        route_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

    }
    public void CameraAction() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
            }

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {

                case PICK_FROM_CAMERA:
                    if (requestCode == PICK_FROM_CAMERA) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
                            route_photo.setImageBitmap(photo);

                        }
                    }
                    break;

                case SIGNATURE_ACTIVITY:
                    if (resultCode == RESULT_OK) {
                        //Bundle extras = data.getExtras();
                        byte[] bytes = data.getByteArrayExtra("status");
                        if (bytes != null) {
                            //Bitmap photo = extras.getParcelable("status");

                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80, true);

                            route_signature.setImageBitmap(bitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] bitMapData = stream.toByteArray();
                            String signature_image = Base64.encodeToString(
                                    bitMapData, Base64.DEFAULT);
                            SOTDatabase.init(activity);
                            Cursor ImgCursor = SOTDatabase.getImageCursor();
                            if (ImgCursor.getCount() > 0) {
                                String camera_image = SOTDatabase.getProductImage();
                                SOTDatabase.updateImage(1, signature_image,
                                        camera_image);
                            } else {
                                SOTDatabase.storeImage(1, signature_image, "");
                            }

                            Log.d("Signature Image", "Sig" + signature_image);
                        }
                    }
                    break;
            }
        }
    }

//    SOTDatabase.deleteImage();
//			helper.showProgressDialog(R.string.invoice_printpreview);
//    AsyncPrintCall task = new AsyncPrintCall();
//			task.execute();

    private class AsyncPrintCall extends
            AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            product.clear();
            productdet.clear();
            footerArr.clear();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... arg0) {

            String decimalpts = ".00";
            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            String showcartonloose = SalesOrderSetGet
                    .getCartonpriceflag();

            hashValue.put("CompanyCode", cmpnyCode);
            hashValue.put("InvoiceNo", invoiceno);

            HashMap<String, String> custhash = new HashMap<String, String>();
            custhash.put("CompanyCode", cmpnyCode);
            custhash.put("CustomerCode", cust_code);

            hashVl.put("CompanyCode", cmpnyCode);

            if (showcartonloose.equalsIgnoreCase("1")) {
                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetInvoiceDetailWithCarton");
            } else {
                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetInvoiceDetail");
            }
            jsonStr = SalesOrderWebService.getSODetail(hashValue,
                    "fncGetInvoiceHeaderByInvoiceNo");
            custjsonStr = SalesOrderWebService.getSODetail(custhash,
                    "fncGetCustomer");

            jsonSt = SalesOrderWebService.getSODetail(hashVl,
                    "fncGetMobilePrintFooter");

            Log.d("jsonSt", jsonSt);
            try {
                jsonRespfooter = new JSONObject(jsonSt);
                jsonSecNodefooter = jsonRespfooter
                        .optJSONArray("SODetails");

                /*********** Print Footer ************/
                int lengJsonArr1 = jsonSecNodefooter.length();
                for (int i = 0; i < lengJsonArr1; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;
                    ProductDetails productdetail = new ProductDetails();
                    try {
                        jsonChildNode = jsonSecNodefooter
                                .getJSONObject(i);

                        String ReceiptMessage = jsonChildNode
                                .optString("ReceiptMessage").toString();
                        String SortOrder = jsonChildNode.optString(
                                "SortOrder").toString();

                        productdetail.setReceiptMessage(ReceiptMessage);
                        productdetail.setSortOrder(SortOrder);
                        footerArr.add(productdetail);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

            Log.d("jsonString ", "" + jsonString);
            Log.d("jsonStr ", "" + jsonStr);
            Log.d("custjsonStr ", "" + custjsonStr);

            try {

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");

                jsonResp = new JSONObject(jsonStr);
                jsonSecNode = jsonResp.optJSONArray("SODetails");

                custjsonResp = new JSONObject(custjsonStr);
                custjsonMainNode = custjsonResp.optJSONArray("SODetails");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            /********
             * Show Default carton,loose,foc,exchange qty and price Based On
             * General settings
             *********/
            /*********** Process each JSON Node ************/
            int lengthJsonArr = jsonMainNode.length();
            if (showcartonloose.equalsIgnoreCase("1")) {
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    ProductDetails productdetail = new ProductDetails();
                    try {
                        JSONObject jsonChildNode = jsonMainNode
                                .getJSONObject(i);
                        String transType = jsonChildNode.optString("TranType")
                                .toString();
                        int s = i + 1;
                        productdetail.setSno(String.valueOf(s));
                        String slNo = jsonChildNode.optString("slNo")
                                .toString();
                        String productCode = jsonChildNode.optString(
                                "ProductCode").toString();
                        productdetail.setItemcode(productCode);
                        /*if (onlineMode.matches("True")) {
                            if (checkOffline == true) {
                                // Offline
                            } else {
                                // Online
                                // Show batch number
                                String showbatch = MobileSettingsSetterGetter
                                        .getShowBatchDetails();
                                if (showbatch.matches("True")) {
                                    hashValue.put("slNo", slNo);
                                    hashValue.put("ProductCode", productCode);
                                    Log.d("hashValue ", "" + hashValue);
                                    batchjsonStr = SalesOrderWebService
                                            .getSODetail(hashValue,
                                                    "fncGetInvoiceBatchDetail");
                                    Log.d("batchjsonStr ", "bat test "
                                            + batchjsonStr);
                                    try {
                                        batch_jsonResp = new JSONObject(
                                                batchjsonStr);
                                        batch_jsonSecNode = batch_jsonResp
                                                .optJSONArray("SODetails");

                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }
                                    int batch_lengthJsonArr = batch_jsonSecNode
                                            .length();
                                    for (int k = 0; k < batch_lengthJsonArr; k++) {
                                        *//****** Get Object for each JSON node. ***********//*
                                        ProductDetails prodBatch = new ProductDetails();
                                        try {
                                            JSONObject batch_jsonChildNode = batch_jsonSecNode
                                                    .getJSONObject(k);
                                            prodBatch
                                                    .setProduct_batchno(batch_jsonChildNode
                                                            .optString(
                                                                    "BatchNo")
                                                            .toString());
                                            prodBatch
                                                    .setBatch_productcode(batch_jsonChildNode
                                                            .optString(
                                                                    "ProductCode")
                                                            .toString());

                                            product_batchArr.add(prodBatch);

                                            Log.d("BatchNo",
                                                    batch_jsonChildNode
                                                            .optString(
                                                                    "BatchNo")
                                                            .toString());

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }
                            }
                        }*/
                        productdetail.setDescription(jsonChildNode.optString(
                                "ProductName").toString());
                        productdetail.setUOMCode(jsonChildNode.optString(
                                "UOMCode").toString());

                        String uomCode = jsonChildNode.optString("UOMCode")
                                .toString();

                        if (uomCode != null && !uomCode.isEmpty()) {

                        } else {
                            uomCode = "";
                        }

                        if (uomCode.matches("null")) {
                            uomCode = "";
                        }

                        Log.d("uomCode", "u " + uomCode);

                        if (transType.equalsIgnoreCase("Ctn")) {
                            String cQty = jsonChildNode.optString("CQty")
                                    .toString();

                            String cPrice = jsonChildNode.optString(
                                    "CartonPrice").toString();

                            Log.d("cPrice", "-->" + cPrice);

                            if (cQty != null && !cQty.isEmpty()
                                    && cPrice != null && !cPrice.isEmpty()) {
                                productdetail.setQty(cQty.split("\\.")[0]);
                                productdetail.setPrice(twoDecimalPoint(Double
                                        .valueOf(cPrice)));
                                double cqty = Double.valueOf(cQty);
                                double cprice = Double.valueOf(cPrice);
                                double total = cqty * cprice;
                                String tot = twoDecimalPoint(total);
                                productdetail.setTotal(tot);
                            } else {
                                productdetail.setQty("0");
                                productdetail.setPrice("0.00");
                                productdetail.setTotal("0.00");
                            }

                        } else if (transType.equalsIgnoreCase("Loose")) {

                            String lQty = jsonChildNode.optString("LQty")
                                    .toString();
                            String lPrice = jsonChildNode.optString("Price")
                                    .toString();

                            Log.d("lPrice", "--->" + lPrice);

                            if (lQty != null && !lQty.isEmpty()
                                    && lPrice != null && !lPrice.isEmpty()) {
                                productdetail.setQty(lQty.split("\\.")[0] + " "
                                        + uomCode);
                                productdetail.setPrice(twoDecimalPoint(Double
                                        .valueOf(lPrice)));
                                double lqty = Double.valueOf(lQty);
                                double lprice = Double.valueOf(lPrice);
                                double total = lqty * lprice;
                                String tot = twoDecimalPoint(total);
                                productdetail.setTotal(tot);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                                productdetail.setPrice("0.00");
                                productdetail.setTotal("0.00");
                            }

                        } else if (transType.equalsIgnoreCase("FOC")) {
                            String focQty = jsonChildNode.optString("FOCQty")
                                    .toString();
                            if (focQty != null && !focQty.isEmpty()) {
                                productdetail.setQty(focQty.split("\\.")[0]);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                            }
                            productdetail.setPrice("0.00");
                            productdetail.setTotal("0.00");

                        } else if (transType.equalsIgnoreCase("Exc")) {
                            String excQty = jsonChildNode.optString(
                                    "ExchangeQty").toString();
                            if (excQty != null && !excQty.isEmpty()) {
                                productdetail.setQty(excQty.split("\\.")[0]);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                            }
                            productdetail.setPrice("0.00");
                            productdetail.setTotal("0.00");

                        }
                        String issueQty = jsonChildNode.optString("IssueQty")
                                .toString();
                        String returnQty = jsonChildNode.optString("ReturnQty")
                                .toString();

                        if (issueQty != null && !issueQty.isEmpty()) {

                            productdetail.setIssueQty(issueQty.split("\\.")[0]);
                        } else {
                            productdetail.setIssueQty("0");
                        }
                        if (returnQty != null && !returnQty.isEmpty()) {

                            productdetail
                                    .setReturnQty(returnQty.split("\\.")[0]);
                        } else {
                            productdetail.setReturnQty("0");
                        }
//                        if (gnrlStngs.matches("C")) {
//                            productdetail.setSortproduct(jsonChildNode
//                                    .optString("CategoryCode").toString());
//                        } else if (gnrlStngs.matches("S")) {
//                            productdetail.setSortproduct(jsonChildNode
//                                    .optString("SubCategoryCode").toString());
//                        } else if (gnrlStngs.matches("N")) {
//                            productdetail.setSortproduct("");
//                        } else {
//                            productdetail.setSortproduct("");
//                        }
                        productdetail.setTax(jsonChildNode.optString("Tax").toString());
                        productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                        productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
                        productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                        product.add(productdetail);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                /******** Print qty and price *********/
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;
                    ProductDetails productdetail = new ProductDetails();
                    try {
                        jsonChildNode = jsonMainNode.getJSONObject(i);
                        int s = i + 1;
                        productdetail.setSno(String.valueOf(s));

                        String slNo = jsonChildNode.optString("slNo")
                                .toString();
                        String productCode = jsonChildNode.optString(
                                "ProductCode").toString();
                        productdetail.setItemcode(productCode);

                        productdetail.setUOMCode(jsonChildNode.optString(
                                "UOMCode").toString());

                        /*if (onlineMode.matches("True")) {
                            if (checkOffline == true) {
                                // Offline
                            } else {
                                // Online
                                // Show batch number

                                String showbatch = MobileSettingsSetterGetter
                                        .getShowBatchDetails();

                                if (showbatch.matches("True")) {

                                    hashValue.put("slNo", slNo);
                                    hashValue.put("ProductCode", productCode);
                                    Log.d("hashValue ", "" + hashValue);
                                    batchjsonStr = SalesOrderWebService
                                            .getSODetail(hashValue,
                                                    "fncGetInvoiceBatchDetail");
                                    Log.d("batchjsonStr ", "bat test "
                                            + batchjsonStr);
                                    try {
                                        batch_jsonResp = new JSONObject(
                                                batchjsonStr);
                                        batch_jsonSecNode = batch_jsonResp
                                                .optJSONArray("SODetails");

                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }

                                    int batch_lengthJsonArr = batch_jsonSecNode
                                            .length();
                                    for (int k = 0; k < batch_lengthJsonArr; k++) {
                                        *//****** Get Object for each JSON node. ***********//*
                                        ProductDetails prodBatch = new ProductDetails();
                                        try {
                                            JSONObject batch_jsonChildNode = batch_jsonSecNode
                                                    .getJSONObject(k);
                                            prodBatch
                                                    .setProduct_batchno(batch_jsonChildNode
                                                            .optString(
                                                                    "BatchNo")
                                                            .toString());
                                            prodBatch
                                                    .setBatch_productcode(batch_jsonChildNode
                                                            .optString(
                                                                    "ProductCode")
                                                            .toString());

                                            product_batchArr.add(prodBatch);

                                            Log.d("BatchNo",
                                                    batch_jsonChildNode
                                                            .optString(
                                                                    "BatchNo")
                                                            .toString());

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }
                            }
                        }*/

                        productdetail.setDescription(jsonChildNode.optString(
                                "ProductName").toString());

                        String invoiceqty = jsonChildNode.optString("Qty")
                                .toString();
                        if (invoiceqty.contains(".")) {
                            StringTokenizer tokens = new StringTokenizer(
                                    invoiceqty, ".");
                            String qty = tokens.nextToken();
                            productdetail.setQty(qty);
                        } else {
                            productdetail.setQty(invoiceqty);
                        }

                        String pricevalue = jsonChildNode.optString("Price")
                                .toString();
                        Log.d("pricevalue", "--->" + pricevalue);

                        String totalvalve = jsonChildNode.optString("Total")
                                .toString();

                        String issueQty = jsonChildNode.optString("IssueQty")
                                .toString();
                        String returnQty = jsonChildNode.optString("ReturnQty")
                                .toString();

                        if (issueQty != null && !issueQty.isEmpty()) {

                            productdetail.setIssueQty(issueQty.split("\\.")[0]);
                        } else {
                            productdetail.setIssueQty("0");
                        }
                        if (returnQty != null && !returnQty.isEmpty()) {

                            productdetail
                                    .setReturnQty(returnQty.split("\\.")[0]);
                        } else {
                            productdetail.setReturnQty("0");
                        }

                        productdetail.setFocqty(jsonChildNode
                                .optDouble("FOCQty"));

                        productdetail.setExchangeqty(jsonChildNode
                                .optDouble("ExchangeQty"));
                        if (pricevalue.contains(".")) {
                            productdetail.setPrice(pricevalue);
                        } else {
                            productdetail.setPrice(pricevalue + decimalpts);
                        }
                        if (totalvalve.contains(".")) {

                            productdetail.setTotal(totalvalve);
                        } else {

                            productdetail.setTotal(totalvalve + decimalpts);
                        }
//                        if (gnrlStngs.matches("C")) {
//                            productdetail.setSortproduct(jsonChildNode
//                                    .optString("CategoryCode").toString());
//                        } else if (gnrlStngs.matches("S")) {
//                            productdetail.setSortproduct(jsonChildNode
//                                    .optString("SubCategoryCode").toString());
//                        } else if (gnrlStngs.matches("N")) {
//                            productdetail.setSortproduct("");
//                        } else {
//                            productdetail.setSortproduct("");
//                        }
                        productdetail.setTax(jsonChildNode.optString("Tax").toString());
                        productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
                        productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                        productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                        product.add(productdetail);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }

            int lengJsonArr = jsonSecNode.length();
            for (int i = 0; i < lengJsonArr; i++) {

                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonSecNode.getJSONObject(i);

                    String itemdiscvalue = jsonChildNode.optString(
                            "ItemDiscount").toString();
                    String billdiscvalue = jsonChildNode.optString(
                            "BillDIscount").toString();
                    String subtotalvalue = jsonChildNode.optString("SubTotal")
                            .toString();

                    productdetail.setTax(twoDecimalPoint(jsonChildNode
                            .optDouble("Tax")));

                    String nettotalvalue = jsonChildNode.optString("NetTotal")
                            .toString();

                    String paidamount = jsonChildNode.optString("PaidAmount")
                            .toString();

                    String BalanceAmount = jsonChildNode.optString("BalanceAmount")
                            .toString();

                    productdetail.setPaidamount(paidamount);

                    productdetail.setBalanceAmount(BalanceAmount);

                    if (itemdiscvalue.contains(".")
                            && billdiscvalue.contains(".")
                            && subtotalvalue.contains(".")
                            && nettotalvalue.contains(".")) {
                        productdetail.setItemdisc(itemdiscvalue);
                        productdetail.setBilldisc(billdiscvalue);
                        productdetail.setSubtotal(subtotalvalue);
                        productdetail.setNettot(nettotalvalue);
                    } else {
                        productdetail.setItemdisc(itemdiscvalue + decimalpts);
                        productdetail.setBilldisc(billdiscvalue + decimalpts);
                        productdetail.setSubtotal(subtotalvalue + decimalpts);
                        productdetail.setNettot(nettotalvalue + decimalpts);
                    }
                    productdetail.setRemarks(jsonChildNode.optString("Remarks")
                            .toString());
                    productdetail.setTotaloutstanding(jsonChildNode.optString(
                            "TotalBalance").toString());
                    productdetail.setCreateDate(jsonChildNode.optString(
                            "CreateDate").toString());

                    productdet.add(productdetail);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            int custJsonArr = custjsonMainNode.length();
            for (int i = 0; i < custJsonArr; i++) {

                JSONObject jsonChildNode;

                try {
                    jsonChildNode = custjsonMainNode.getJSONObject(i);

                    String CustomerCode = jsonChildNode.optString(
                            "CustomerCode").toString();
                    String CustomerName = jsonChildNode.optString(
                            "CustomerName").toString();
                    String Address1 = jsonChildNode.optString("Address1")
                            .toString();
                    String Address2 = jsonChildNode.optString("Address2")
                            .toString();
                    String Address3 = jsonChildNode.optString("Address3")
                            .toString();
                    String PhoneNo = jsonChildNode.optString("PhoneNo")
                            .toString();
                    String HandphoneNo = jsonChildNode.optString("HandphoneNo")
                            .toString();
                    String Email = jsonChildNode.optString("Email").toString();
                    String TermName = jsonChildNode.optString("TermName")
                            .toString();
                    String OutstandingAmount = jsonChildNode.optString(
                            "OutstandingAmount").toString();
                    String TaxValue = jsonChildNode.optString("TaxValue")
                            .toString();

                    CustomerSetterGetter.setCustomerCode(CustomerCode);
                    CustomerSetterGetter.setCustomerName(CustomerName);
                    CustomerSetterGetter.setCustomerAddress1(Address1);
                    CustomerSetterGetter.setCustomerAddress2(Address2);
                    CustomerSetterGetter.setCustomerAddress3(Address3);
                    CustomerSetterGetter.setCustomerPhone(PhoneNo);
                    CustomerSetterGetter.setCustomerHP(HandphoneNo);
                    CustomerSetterGetter.setCustomerEmail(Email);
                    CustomerSetterGetter.setCustomerTerms(TermName);
                    CustomerSetterGetter.setTotalOutstanding(OutstandingAmount);
                    SalesOrderSetGet.setCustomerTaxPerc(TaxValue);
					/*
					 * Log.d("mobile settings Customer code",
					 * CustomerSetterGetter.getCustomerCode());
					 */
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            hashVl.clear();
            hashValue.clear();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

//            if (printid == R.id.printer) {
//
//                try {
//                    Log.d("isPrint", "isPrint");
//                    printid = 0;
//                    sortCatSubCat();
//                    if (isInvoicePrint) {
//                        print();
//                    } else {
//                        invoiceOnDeliveryOrderPrint();
//                    }
//
//                } catch (IOException e) {
//
//                }
//            } else {
            Log.d("receiptprintpreview", "receiptprintpreview");
            Log.d("product", "" + product);

            // for(ProductDetails prod: product){
            // Log.d("price",""+prod.getPrice());
            // }

            Log.d("productdet", "" + productdet);
            helper.dismissProgressDialog();
            Intent i = new Intent(activity,
                   RoutePrintPreview.class);
            i.putExtra("invNo", invoiceno);
            i.putExtra("invDate", invoicedate);
            i.putExtra("customerCode", cust_code);
            i.putExtra("customerName", customername);
            i.putExtra("sort", sort);
            i.putExtra("gnrlStngs", "");
            i.putExtra("destlat",destinationLatitudeValue);
            i.putExtra("destlng",destinationLongitudeValue);
            i.putExtra("address1",address);
            i.putExtra("address2",address_2);
            PreviewPojo.setProducts(product);
            PreviewPojo.setProductsDetails(productdet);
            activity.startActivity(i);

//            }
        }

    }
    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }

}
