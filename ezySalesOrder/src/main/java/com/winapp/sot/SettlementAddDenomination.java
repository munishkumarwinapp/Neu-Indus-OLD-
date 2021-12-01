package com.winapp.sot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.fwms.ValidateWebService;
import com.winapp.helper.Constants;
import com.winapp.model.CurrencyDeno;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettlementAddDenomination extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace,Constants {
    ImageButton searchIcon, custsearchIcon, printer, mInvoiceProductPrint, qrcode,save;
    SlidingMenu menu;
    String valid_url, cmpnyCode;
    List<CurrencyDeno> demoList;
    DenominationAdapter adapter;
    ListView list;
    private static String NAMESPACE = "http://tempuri.org/";
    private static String SOAP_ACTION = "http://tempuri.org/";
    static String resTxt = null;
    private static int TimeOut=200000;
    Cursor cursor;
    EditText sl_namefield,date,location,sl_user;
    Calendar startCalendar;
    static String result,sum_result;
    TextView totalAmt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        Mint.initAndStartSession(SettlementAddDenomination.this, "29088aa0");
        setContentView(R.layout.activity_settlement_add_denomination);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.invoice_slidemenu, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Add Denomination");
        searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
        qrcode = (ImageButton) customNav.findViewById(R.id.qrcode);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
        custsearchIcon = (ImageButton) customNav
                .findViewById(R.id.custcode_img);
        save = (ImageButton)customNav.findViewById(R.id.save);
        save.setVisibility(View.VISIBLE);
        list = (ListView) findViewById(R.id.recyclerView);
        sl_namefield = (EditText)findViewById(R.id.sl_namefield);
        date = (EditText)findViewById(R.id.starteditTextDate);
        location = (EditText)findViewById(R.id.locationcode_filter);
        location.setText(SalesOrderSetGet.getLocationcode());
        custsearchIcon.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);
        totalAmt = (TextView)findViewById(R.id.totalAmt);


        ab.setCustomView(customNav);
        ab.setDisplayShowCustomEnabled(true);

        ab.setDisplayHomeAsUpEnabled(false);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidemenufragment);
        menu.setSlidingEnabled(false);

        FWMSSettingsDatabase.init(SettlementAddDenomination.this);
        SOTDatabase.init(SettlementAddDenomination.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        startCalendar = Calendar.getInstance();
        new SOTSummaryWebService(valid_url);
        sl_user = (EditText)findViewById(R.id.sl_user);
        sl_user.setText(SupplierSetterGetter.getUsername());


        cursor = SOTDatabase.getDenominationCursor();
        Log.d("getCount()","-->"+cursor.getCount());

        AsyncDenomination salesOAC = new AsyncDenomination();
        salesOAC.execute();

        String dates = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        Log.d("Datecheck","--?>"+date);
        date.setText(dates);



        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                strtDate();
            }
        };

        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    new DatePickerDialog(SettlementAddDenomination.this, startDate,
                            startCalendar.get(Calendar.YEAR), startCalendar
                            .get(Calendar.MONTH), startCalendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlertDialog();
            }
        });


    }



    private void saveAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.mipmap.ic_save);
        alertDialog.setTitle("Save");
        alertDialog.setMessage("Do you want to Save");
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected) {
//                save.setVisibility(View.INVISIBLE);
                String date_edt = date.getText().toString();
                if(date_edt.matches("")){
                    Toast.makeText(getApplicationContext(), "Choose date!!!", Toast.LENGTH_LONG).show();
                }else{
                    saveData();
                }

            }
        });

        alertDialog.show();
    }

    private void saveData() {
        String date_edt = date.getText().toString();
        String totl_amt = totalAmt.getText().toString();

        Log.d("date_edt","-->"+date_edt);

        sum_result= SOTSummaryWebService.saveSettlement(date_edt,totl_amt,"fncSaveSettlement");
        Log.d("sum_result",sum_result);

        if (sum_result.matches("")) {
            sum_result = "Failed!!!!";
            Log.d("SummaryResult", sum_result);
            Toast.makeText(getApplicationContext(), sum_result, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Saved Successfully!!!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SettlementAddDenomination.this,SettlementMainHeader.class);
            startActivity(intent);
        }
    }

    private void strtDate() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        date.setText(sdf.format(startCalendar.getTime()));
    }

    @Override
    public void onListItemClick(String item) {
        menu.toggle();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        menu.toggle();
        return super.onOptionsItemSelected(item);
    }




    private class AsyncDenomination extends AsyncTask<Void, Void, Void> {
        String vld_rslt;
        @Override
        protected void onPreExecute() {
            demoList = new ArrayList<>();
            demoList.clear();
            Log.d("initialCount","-->"+demoList.size());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                demoList = ValidateWebService.validateCurrency(valid_url,
                        "fncGetCurrencyDinamination");
                Log.d("demolistSizecount","-->"+demoList.size());



                for(int i=0;i<demoList.size();i++) {
                    SOTDatabase.storeDenomination(demoList.get(i).getSlno(), demoList.get(i).getCurency(),
                            demoList.get(i).getDenomination(), demoList.get(i).getTotal());
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

         @Override
         protected void onPostExecute(Void aVoid) {

             cursor = SOTDatabase.getDenominationCursor();
             if(demoList.size()  !=0) {
                 adapter = new DenominationAdapter(SettlementAddDenomination.this, demoList, cursor, totalAmt);
                 list.setAdapter(adapter);
             }

         }
         }


    private static class DenominationAdapter extends BaseAdapter{
        Activity context;
        List<CurrencyDeno> denoList;
        LayoutInflater mInflater;
        private OnItemClick mCallback;
        double checking = 0.00;
        Cursor cursors;
        TextView text;


        public DenominationAdapter(Activity activity, List<CurrencyDeno> demoList, Cursor cursor, TextView totalAmt) {
            this.context = activity;
            this.denoList = demoList;
            mInflater = (LayoutInflater.from(context));
            SOTDatabase.init(this.context);
            this.cursors = cursor;
            this.text = totalAmt;
            Log.d("cursorsCount","--?>"+denoList.size()+"-->"+cursors.getCount());


//            mCallback = (OnItemClick)context;

        }

        @Override
        public int getCount() {
            return denoList.size();
        }

        @Override
        public Object getItem(int position) {
            return denoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.denomination_edit, null);

            holder.slno = (TextView)convertView.findViewById(R.id.text4);
            holder.deno = (TextView)convertView.findViewById(R.id.text1);
            holder.total = (TextView)convertView.findViewById(R.id.text3);
            holder.count = (EditText)convertView.findViewById(R.id.text2);

            CurrencyDeno deno = denoList.get(position);
            holder.slno.setText(deno.getSlno());
            holder.deno.setText(deno.getCurency());
            holder.total.setText(deno.getTotal());

            holder.count.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    String data = s.toString().trim();
                    String value = holder.deno.getText().toString();
                    if(!data.matches("")) {
                        Log.d("dataCheck", data + "value:" + value);
                        double vdeno = Double.parseDouble(value);
                        double vcount = Double.parseDouble(data);

                        double check = vdeno * vcount;
                        Log.d("countCheck", "-->" + check);
                        holder.total.setText("" + twoDecimalPoint(check));

                        String slnos = holder.slno.getText().toString();
                        String curr_str = holder.deno.getText().toString();
                        String denos = holder.count.getText().toString();
                        String tot = holder.total.getText().toString();

                        Log.d("getvalues", "-->" + slnos + "-->" + curr_str + "denos:" + denos + "tot:" + tot);

                        SOTDatabase.updateDenomination(slnos, curr_str, denos, tot);

                        if (cursors.getCount() > 0) {
                            double count = SOTDatabase.getTotalAmt();
                            Log.d("countcheck", "-->" + count);
                            text.setText("" + twoDecimalPoint(count));
                        }
                    }else{
                        holder.total.setText("0.00");
                        String slnos = holder.slno.getText().toString();
                        String curr_str = holder.deno.getText().toString();
                        String denos = holder.count.getText().toString();
                        String tot = holder.total.getText().toString();
                        SOTDatabase.updateDenomination(slnos, curr_str, denos, tot);
                        if (cursors.getCount() > 0) {
                            double count = SOTDatabase.getTotalAmt();
                            Log.d("countcheck", "-->" + count);
                            text.setText("" + twoDecimalPoint(count));
                        }

                    }
                }
            });


            return convertView;
        }

        class ViewHolder{
            TextView slno,deno,total;
            EditText count;
        }

        public interface OnItemClick {
            void onClick (double addValue);
        }
    }

    public static String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettlementAddDenomination.this,SettlementMainHeader.class);
        startActivity(intent);
        finish();
    }
}
