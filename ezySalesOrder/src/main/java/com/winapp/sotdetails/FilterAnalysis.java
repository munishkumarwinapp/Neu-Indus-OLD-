package com.winapp.sotdetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.winapp.adapter.FilterAdapter;
import com.winapp.crm.HistoryActivity;
import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.GetUserPermission;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.Filter;
import com.winapp.offline.OfflineDatabase;
import com.winapp.sot.FilterCS;
import com.winapp.sot.GraHeader;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.util.CustomCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sathish on 02-05-2018.
 */

public class FilterAnalysis {
    public interface OnFilterCompletionListener {
        public void onFilterCompleted(String loadtype, String costtype, String fromdate, String todate, String sortby, String suppliercode);
    }
    private Activity activity;
    private TextView mFilterCategory,mFilterSubCategory,mFilterProduct;
    private EditText mFilter,fromdate,todate;
    private ImageView mClose;
    private Dialog mDialog;
    private HashMap<String, String> mparam = new HashMap<String, String>();
    private String jsonString="",companyCode,validUrl,category = null,subcategory= null,mobileHaveOfflineMode="",sServerDate = "";
    private JSONObject jsonResponse;
    private JSONArray jsonMainNode;
    private ArrayList<Filter> mArrayListFilterCategory;
    private ArrayList<Filter> mArrayListFilterSubCategory;
    private ArrayList<Filter> mArrayListFilterProduct;
    private FilterAdapter filteradapter;
    private ListView mListView;
    private boolean mFilterFlag = false;
    private ImageView mClearFilter,mFilterApply;
    private Filter filter;
    private OnFilterCompletionListener listener;
    private static OfflineDatabase offlinedb;
    private TableRow mFilterCategoryTableRow, mFilterSubCategoryTableRow,
            filter_product_TableRow;
    private Spinner load_type,cost_type;
    private String loadtype,costtype,from_date,to_date,sortby = "Sales Quantity",suppliercode = "";
    private CustomCalendar customCalendar;
    private EditText ed_suppliercode,sl_namefield;
    ArrayList<HashMap<String, String>> searchResults;
    ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
    CustomAlertAdapterSupp arrayAdapterSupp;
    private AlertDialog myalertDialog = null;
    ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
    int textlength = 0;
    private TextView txt_productname,txt_purchasequantity,txt_salesquantity,txt_balancequantity,txt_profitamount,txt_marginpercentage;

    public FilterAnalysis(Activity activity){
        this.activity = activity;
        companyCode = SupplierSetterGetter.getCompanyCode();
        FWMSSettingsDatabase.init(activity);
        offlinedb = new OfflineDatabase(activity);
        validUrl = FWMSSettingsDatabase.getUrl();

    }
    public void OnFilterCompletionListener(OnFilterCompletionListener listener) {
        this.listener = listener;
    }
    public void filterDialog(){
        mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
        mDialog= new Dialog(activity);
        //dialog.setTitle("Filter");
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.filter_analysis_layout);

        mDialog.setCancelable(false);
        mDialog.show();

        mClose = (ImageView) mDialog.findViewById(R.id.close);
        mFilterApply = (ImageView) mDialog.findViewById(R.id.filter_apply);
        fromdate = (EditText) mDialog.findViewById(R.id.fromdate);
        todate = (EditText) mDialog.findViewById(R.id.todate);
        load_type = (Spinner) mDialog.findViewById(R.id.load_type);
        cost_type = (Spinner) mDialog.findViewById(R.id.cost_type);
        txt_productname = (TextView) mDialog.findViewById(R.id.txt_productname);
        txt_purchasequantity = (TextView) mDialog.findViewById(R.id.txt_purchasequantity);
        txt_salesquantity = (TextView) mDialog.findViewById(R.id.txt_salesquantity);
        txt_balancequantity = (TextView) mDialog.findViewById(R.id.txt_balancequantity);
        txt_profitamount = (TextView) mDialog.findViewById(R.id.txt_profitamount);
        txt_marginpercentage = (TextView) mDialog.findViewById(R.id.txt_marginpercentage);
        ed_suppliercode = (EditText) mDialog.findViewById(R.id.supcodefield);
        sl_namefield= (EditText) mDialog.findViewById(R.id.sl_namefield);
        sServerDate = SalesOrderSetGet.getServerDate();
        fromdate.setText(sServerDate);
        todate.setText(sServerDate);
        txt_salesquantity.setTextColor(activity.getResources().getColor(android.R.color.white));
        txt_salesquantity.setBackgroundResource(R.drawable.color_select);

        AsyncCallWSSupplierCode salesOAC = new AsyncCallWSSupplierCode();
        salesOAC.execute();

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        fromdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                customCalendar = new CustomCalendar(activity);
                customCalendar.showCalendarView();
                boolean mstatus = customCalendar.showDialog();
                Log.d("mstatus", mstatus+"");
                if (mstatus == true) {
                    String sDate = customCalendar.getSelectDate();
                    fromdate.setText(sDate);
                } else {
                    Log.d("False", "-->" + "False");
                }
            }
        });

        todate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                customCalendar = new CustomCalendar(activity);
                customCalendar.showCalendarView();
                boolean mstatus = customCalendar.showDialog();
                Log.d("mstatus", mstatus+"");
                if (mstatus == true) {
                    String sDate = customCalendar.getSelectDate();
                    todate.setText(sDate);
                } else {
                    Log.d("False", "-->" + "False");
                }
            }
        });

        ed_suppliercode
                .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                        ed_suppliercode) {
                    @Override
                    public boolean onDrawableClick() {
                        alertDialogSearch();
                        return true;
                    }
                });
//
        mFilterApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplyFilter();
            }
        });

        txt_productname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortby = "Product Name";
                txt_productname.setTextColor(activity.getResources().getColor(android.R.color.white));
                txt_productname.setBackgroundResource(R.drawable.color_select);

                txt_purchasequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_purchasequantity.setBackgroundResource(R.drawable.unselect);
                txt_salesquantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_salesquantity.setBackgroundResource(R.drawable.unselect);
                txt_balancequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_balancequantity.setBackgroundResource(R.drawable.unselect);
                txt_profitamount.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_profitamount.setBackgroundResource(R.drawable.unselect);
                txt_marginpercentage.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_marginpercentage.setBackgroundResource(R.drawable.unselect);
            }
        });

        txt_purchasequantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortby = "Purchase Quantity";
                txt_purchasequantity.setTextColor(activity.getResources().getColor(android.R.color.white));
                txt_purchasequantity.setBackgroundResource(R.drawable.color_select);

                txt_productname.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_productname.setBackgroundResource(R.drawable.unselect);
                txt_salesquantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_salesquantity.setBackgroundResource(R.drawable.unselect);
                txt_balancequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_balancequantity.setBackgroundResource(R.drawable.unselect);
                txt_profitamount.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_profitamount.setBackgroundResource(R.drawable.unselect);
                txt_marginpercentage.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_marginpercentage.setBackgroundResource(R.drawable.unselect);
            }
        });

        txt_salesquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortby = "Sales Quantity";
                txt_salesquantity.setTextColor(activity.getResources().getColor(android.R.color.white));
                txt_salesquantity.setBackgroundResource(R.drawable.color_select);

                txt_productname.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_productname.setBackgroundResource(R.drawable.unselect);
                txt_purchasequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_purchasequantity.setBackgroundResource(R.drawable.unselect);
                txt_balancequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_balancequantity.setBackgroundResource(R.drawable.unselect);
                txt_profitamount.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_profitamount.setBackgroundResource(R.drawable.unselect);
                txt_marginpercentage.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_marginpercentage.setBackgroundResource(R.drawable.unselect);
            }
        });

        txt_balancequantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortby = "Balance Quantity";
                txt_balancequantity.setTextColor(activity.getResources().getColor(android.R.color.white));
                txt_balancequantity.setBackgroundResource(R.drawable.color_select);

                txt_productname.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_productname.setBackgroundResource(R.drawable.unselect);
                txt_purchasequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_purchasequantity.setBackgroundResource(R.drawable.unselect);
                txt_salesquantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_salesquantity.setBackgroundResource(R.drawable.unselect);
                txt_profitamount.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_profitamount.setBackgroundResource(R.drawable.unselect);
                txt_marginpercentage.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_marginpercentage.setBackgroundResource(R.drawable.unselect);
            }
        });

        txt_profitamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortby = "Profit Amount";
                txt_profitamount.setTextColor(activity.getResources().getColor(android.R.color.white));
                txt_profitamount.setBackgroundResource(R.drawable.color_select);

                txt_productname.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_productname.setBackgroundResource(R.drawable.unselect);
                txt_purchasequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_purchasequantity.setBackgroundResource(R.drawable.unselect);
                txt_salesquantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_salesquantity.setBackgroundResource(R.drawable.unselect);
                txt_balancequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_balancequantity.setBackgroundResource(R.drawable.unselect);
                txt_marginpercentage.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_marginpercentage.setBackgroundResource(R.drawable.unselect);
            }
        });

        txt_marginpercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortby = "Margin Percentage";
                txt_marginpercentage.setTextColor(activity.getResources().getColor(android.R.color.white));
                txt_marginpercentage.setBackgroundResource(R.drawable.color_select);

                txt_productname.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_productname.setBackgroundResource(R.drawable.unselect);
                txt_purchasequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_purchasequantity.setBackgroundResource(R.drawable.unselect);
                txt_salesquantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_salesquantity.setBackgroundResource(R.drawable.unselect);
                txt_balancequantity.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_balancequantity.setBackgroundResource(R.drawable.unselect);
                txt_profitamount.setTextColor(activity.getResources().getColor(android.R.color.black));
                txt_profitamount.setBackgroundResource(R.drawable.unselect);
            }
        });

    }

    public void alertDialogSearch() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(activity);
        final EditText editText = new EditText(activity);
        final ListView listview = new ListView(activity);
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle("Supplier");
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0,
                0, 0);
        layout.addView(editText);
        layout.addView(listview);
        myDialog.setView(layout);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myalertDialog
                            .getWindow()
                            .setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        arrayAdapterSupp = new CustomAlertAdapterSupp(activity, al);
        listview.setAdapter(arrayAdapterSupp);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                myalertDialog.dismiss();
                getArraylsit = arrayAdapterSupp.getArrayList();
                HashMap<String, String> datavalue = getArraylsit.get(position);
                Set<Map.Entry<String, String>> keys = datavalue.entrySet();
                Iterator<Map.Entry<String, String>> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Map.Entry mapEntry = iterator.next();
                    String keyValues = (String) mapEntry.getKey();
                    String name = (String) mapEntry.getValue();

                    ed_suppliercode.setText(keyValues);
                    sl_namefield.setText(name);
                    ed_suppliercode.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s,
                                                      int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            textlength = ed_suppliercode.getText().length();
                            sl_namefield.setText("");
                        }
                    });
                }
            }
        });

        searchResults = new ArrayList<HashMap<String, String>>(al);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                textlength = editText.getText().length();
                searchResults.clear();
                for (int i = 0; i < al.size(); i++) {
                    String supplierName = al.get(i).toString();
                    if (textlength <= supplierName.length()) {
                        if (supplierName.toLowerCase().contains(
                                editText.getText().toString().toLowerCase()
                                        .trim()))
                            searchResults.add(al.get(i));
                    }
                }

                arrayAdapterSupp = new CustomAlertAdapterSupp(activity,
                        searchResults);
                listview.setAdapter(arrayAdapterSupp);
            }
        });
        myDialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        myalertDialog = myDialog.show();
    }

    private class AsyncCallWSSupplierCode extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            al.clear();

           /* spinnerLayout = new LinearLayout(GraHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(salesO_parent, false);
            progressBar = new ProgressBar(GraHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    R.drawable.greenprogress));
            spinnerLayout.addView(progressBar);*/

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            al = SalesOrderWebService.grasuppwebservice("fncGetSupplier");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }


    public void mApplyFilter(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    loadtype = load_type.getSelectedItem().toString();
                    costtype = cost_type.getSelectedItem().toString();
                    from_date = fromdate.getText().toString();
                    to_date = todate.getText().toString();
                    suppliercode = ed_suppliercode.getText().toString();
                    mDialog.dismiss();

//					mFilterApply.setBackgroundColor(Color.WHITE);
//					mFilterApply.setTextColor(Color.BLACK);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onFilterCompleted(loadtype,costtype,from_date,to_date,sortby,suppliercode);
                            }
                        }
                    });
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
}
