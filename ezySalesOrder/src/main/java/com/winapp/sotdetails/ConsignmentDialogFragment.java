package com.winapp.sotdetails;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.winapp.SFA.R;
import com.winapp.model.EditModel;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sathish on 15-12-2018.
 */

public class ConsignmentDialogFragment extends DialogFragment {
    String pCode="",cust_Code="",pName="",qty ="";
    TextView pName_txt,pCode_txt,pCode_qty,return_name,s_return;
    ListView listView;
    static ArrayList<HashMap<String, String>> stockConsignmentArr;
    ImageView close,ok_btn;
    LinearLayout spinnerLayout,no_data;
    ProgressBar progressBar;
    StockDetilConsignmentAdapter adapter;
    String quans,get_quan="";
    String name ="";
    double quans_value=0;
    static SherlockFragmentActivity context;
    OnMyDialogResult mDialogResult;
    ArrayList<HashMap<String, String>> hashMap =new ArrayList<>();
    ArrayList<HashMap<String, String>> hashMaps =new ArrayList<>();

    public static ConsignmentDialogFragment newInstance(SherlockFragmentActivity context, String id, String cust_code, String pName, String qty, String consignmentStockHeader) {
        Log.d("productCode","-->"+id+"cust"+cust_code+"name"+pName+"qty"+qty);
        ConsignmentDialogFragment frag = new ConsignmentDialogFragment();
        Bundle args = new Bundle();
        args.putString("productCode", id);
        args.putString("customer",cust_code);
        args.putString("ProductName",pName);
        args.putString("quantity",qty);
        args.putString("class_name",consignmentStockHeader);
        frag.setArguments(args);
        ConsignmentDialogFragment.context = context;
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater
                .inflate(R.layout.stock_alert, container, false);

        pCode = getArguments().getString("productCode");
        cust_Code=getArguments().getString("customer");
        pName =getArguments().getString("ProductName");
        qty =getArguments().getString("quantity");
        name =getArguments().getString("class_name");

        Log.d("QuantityCount","-->"+qty);

        if(qty!=null){
            double quantity =Double.parseDouble(qty);
            long quan= Math.round(quantity);
            quans = new Long(quan).toString();
        }else{
            quans="0";
        }

        SOTDatabase.init(getActivity());

        Log.d("Quantity","-->"+quans);

        pName_txt=(TextView)dialogView.findViewById(R.id.sl_namefield);
        pCode_txt =(TextView)dialogView.findViewById(R.id.pCode);
        pCode_qty=(TextView)dialogView.findViewById(R.id.sl_qty);
        listView =(ListView)dialogView.findViewById(R.id.list);
        close = (ImageView)dialogView.findViewById(R.id.close);
        no_data =(LinearLayout) dialogView.findViewById(R.id.no_data);
        ok_btn =(ImageView)dialogView.findViewById(R.id.ok);
        return_name =(TextView)dialogView.findViewById(R.id.ss_return);
        s_return=(TextView)dialogView.findViewById(R.id.ss_return);

        if(name.matches("COR")){
            s_return.setText("Return");
        }else if(name.matches("COI")){
            s_return.setText("Invoice");
        }



        if(name.matches("COR")||name.matches("COI")){
                return_name.setVisibility(View.VISIBLE);

        }else{
            return_name.setVisibility(View.GONE);
        }

        close.setOnClickListener(mDismissOnClickListener);
        ok_btn.setOnClickListener(mUpdateOnClickListener);

        pCode_txt.setText(pCode);
        pName_txt.setText(pName);
        pCode_qty.setText(quans);

        new AsyncStockTask().execute();



    return dialogView;
    }

    private class AsyncStockTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(getActivity());
            spinnerLayout.setGravity(Gravity.CENTER);
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            progressBar = new ProgressBar(getActivity());
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    R.drawable.greenprogress));
            spinnerLayout.addView(progressBar);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("cust_codes",""+cust_Code +"  "+pCode);
            stockConsignmentArr= SalesOrderWebService.
                    getStockConsignmentDetailArr("fncGetConsignmentProductStock",cust_Code,pCode);
            if(stockConsignmentArr.size()==0){
                no_data.setVisibility(View.VISIBLE);
            }
            Log.d("stockConsignmentArr","->"+stockConsignmentArr.size());
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if (getActivity()!=null) {
                headerCustCode();
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);


            }
        }
    }

    private void headerCustCode() {

        adapter=new StockDetilConsignmentAdapter(getActivity(),stockConsignmentArr,name,get_quan,hashMap,hashMaps);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    private View.OnClickListener mDismissOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            dismiss();
        }
    };

    private View.OnClickListener mUpdateOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {


            hashMap =SalesOrderSetGet.getHashMap();
            hashMaps=SalesOrderSetGet.getHashMap_list();
            String values =String.valueOf(SalesOrderSetGet.getCon_qty());
//            Log.d("DoubleValues","-->"+SalesOrderSetGet.getCon_qty()+hashMap.size()+hashMaps.size());
//            if( mDialogResult != null ){
//                mDialogResult.finish(values,hashMap,hashMaps);
//
//            }

            dismiss();
        }
    };

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result, ArrayList<HashMap<String, String>> hashMap, ArrayList<HashMap<String, String>> hashMaps);

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void enableViews(View v, boolean enabled) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                enableViews(vg.getChildAt(i), enabled);
            }
        }
        v.setEnabled(enabled);
    }


    private class StockDetilConsignmentAdapter extends BaseAdapter {
        Activity context;
        ArrayList<HashMap<String, String>> consignmentArr;
        LayoutInflater mInflater;
        double quan=0.00;
        String data_name,consignment_no="";
        double ret_check;
        ArrayList<HashMap<String, String>> hashMap =new ArrayList<>();
        ArrayList<HashMap<String, String>> hashMap_list =new ArrayList<>();
        String get_Quan,consignmentNo="",slNo="";
        public ArrayList<EditModel> editModelArrayList=new ArrayList<>();
        double ent_quant,check_qty;
        String key,value;

        boolean data =false;
        public StockDetilConsignmentAdapter(Activity activity, ArrayList<HashMap<String, String>> stockConsignmentArr, String name, String get_quan, ArrayList<HashMap<String, String>> hashMap, ArrayList<HashMap<String, String>> hashMaps) {
            Log.d("stockConsignmentArr",""+stockConsignmentArr.size());
            this.consignmentArr=stockConsignmentArr;
            this.mInflater = LayoutInflater.from(activity);
            this.context=activity;
            this.data_name=name;
            this.get_Quan=get_quan;
            this.hashMap =hashMap;
            this.hashMap_list=hashMaps;
        }

        @Override
        public int getCount() {
            return consignmentArr.size();
        }

        @Override
        public Object getItem(int position) {
            return consignmentArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                editModelArrayList.clear();
                convertView = mInflater.inflate(R.layout.actiity_stock_detail, null);
                holder.sl_no =(TextView)convertView.findViewById(R.id.ss_no);
                holder.con_no = (TextView)convertView.findViewById(R.id.ss_con_no);
                holder.con_name =(TextView)convertView.findViewById(R.id.ss_name);
                holder.duration =(TextView)convertView.findViewById(R.id.ss_duration);
                holder.return_qty=(EditText) convertView.findViewById(R.id.sl_qty);
                convertView.setTag(holder);

                if(data_name.matches("COR")||data_name.matches("COI")){
                    holder.return_qty.setVisibility(View.VISIBLE);
                    holder.return_qty.requestFocus();
                }else{
                    holder.return_qty.setVisibility(View.GONE);
                }


                holder.expiry_date =(TextView)convertView.findViewById(R.id.ss_date);
                holder.cqty = (TextView)convertView.findViewById(R.id.ss_cqty);
                holder.lqty =(TextView)convertView.findViewById(R.id.ss_lqty);
                holder.qty =(TextView)convertView.findViewById(R.id.ss_qty);
                holder.cqty_layout =(LinearLayout) convertView.findViewById(R.id.cqty_layout);
                holder.lqty_layout=(LinearLayout)convertView.findViewById(R.id.lQty_layout);

                holder.sl_no.setText(consignmentArr.get(position).get("SlNo"));
                holder.con_no.setText(consignmentArr.get(position).get("consignmentNo"));


//                SalesOrderSetGet.setConsignmentNo(con_no);

                final String date =consignmentArr.get(position).get("consignmentDate");
                String[] arrOfStr = date.split(" ", 2);
                holder.con_name.setText(arrOfStr[0]);
                holder.duration.setText(consignmentArr.get(position).get("durationDays"));


                String dates =consignmentArr.get(position).get("Con_ExpiryDate");
                String[] arrOfStrs = dates.split(" ", 2);
                holder.expiry_date.setText(arrOfStrs[0]);

                holder.cqty.setText(consignmentArr.get(position).get("cQty").split("\\.")[0]);
                holder.lqty.setText(consignmentArr.get(position).get("lqty").split("\\.")[0]);



                holder.return_qty.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String ab=consignmentArr.get(position).get("qty").split("\\.")[0];
                        check_qty=Double.parseDouble(ab);
                        final String ret_quan =holder.return_qty.getText().toString();

                        EditModel editModel = new EditModel();
                        if(ret_quan!=""){
                            editModel.setEditTextValue(String.valueOf(position));
                            editModelArrayList.add(editModel);
                            Log.d("editModel","-->"+"editModel");
                        }else{

                        }

                        try{
                            if((ret_quan!="")&& (!ret_quan.matches("0"))){
                                ret_check =Double.parseDouble(ret_quan);
                                editModelArrayList.get(position).setEditTextValue(ret_quan);
                                Log.d("get_QuanValue","-->"+editModelArrayList.size()+" "+ret_check);

                            }else{
                                Log.d("get_QuanValue","-->"+editModelArrayList.size()+" "+ret_check);
                                editModelArrayList.clear();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if(check_qty<ret_check){
                            data=true;
                            int check =(int)(ret_check/10);
                            quan=quan-check;
                            try{
//                                holder.return_qty.setText("0");
                            }catch (StackOverflowError e){
                                e.printStackTrace();
                            }
                            Toast.makeText(context,"Return qty should be less than Qty",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        consignmentNo =consignmentArr.get(position).get("consignmentNo");
                        slNo = consignmentArr.get(position).get("SlNo");
                        get_Quan= editModelArrayList.get(position).getEditTextValue();

                        Log.d("checkdata","-->"+get_Quan+" "+consignmentNo);

                        if((get_Quan!="") && (!get_Quan.matches("0"))){
                            if(data==false){
                                if(check_qty>=ret_check) {
                                    HashMap<String, String> customerhm = new HashMap<String, String>();
                                    if(hashMap.size()>0) {
                                        Log.d("editModelArrayList", "-->" + hashMap.size());
                                        for (HashMap<String, String> map : hashMap)
                                            for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                                                 key = mapEntry.getKey();
                                                 value = mapEntry.getValue();
                                            }
                                                Log.d("key&Value","-->"+key+"  "+value);
                                                if(key.contains(consignmentNo)){
//                                                    hashMap.get(position).remove(value);
                                                    hashMap.get(position).put(key,get_Quan);
                                                    Log.d("key&Value1","-->"+key+"  "+value);
                                                }else{
                                                    customerhm.put(consignmentNo, get_Quan);
                                                    hashMap.add(customerhm);
                                                    SalesOrderSetGet.setHashMap(hashMap);

                                            }

                                    }else{
                                        Log.d("ent_quant","-->"+quan);
                                        customerhm.put(consignmentNo, get_Quan);
                                        hashMap.add(customerhm);
                                        SalesOrderSetGet.setHashMap(hashMap);
                                    }


                                            HashMap<String, String> customer = new HashMap<>();
                                            customer.put(consignmentNo, slNo);
                                            hashMap_list.add(customer);
                                            SalesOrderSetGet.setHashMap_list(hashMap_list);


                                            ent_quant = Double.parseDouble(get_Quan);
                                            quan = quan + ent_quant;
                                            SalesOrderSetGet.setCon_qty(quan);

                                }
                            }
                            Log.d("ret_quan","-->"+check_qty+" "+ret_check+" "+data);

                        }else{
                            Log.d("SalesOrderSetGet", "" + quan);
                            editModelArrayList.clear();
                            int check =(int)(ret_check/10);
                            quan=quan-check;
                        }
                        Log.d("check_qty","-->"+consignmentNo +"  "+data +"  "+hashMap.size());

                    }

                });

                String qty =consignmentArr.get(position).get("qty");
                double quantity =Double.parseDouble(qty);
                long quan= Math.round(quantity);
                String quans = new Long(quan).toString();

                holder.qty.setText(quans);

            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            return convertView;
        }


        private class ViewHolder {
            TextView sl_no,con_no,con_name,duration,batch_no,expiry_date,cqty,lqty,qty;
            LinearLayout cqty_layout,lqty_layout;
            EditText return_qty;

        }
    }
    }

