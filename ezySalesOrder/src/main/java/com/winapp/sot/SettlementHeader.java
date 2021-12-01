package com.winapp.sot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationListener;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.fwms.ValidateWebService;
import com.winapp.helper.Constants;
import com.winapp.SFA.R.drawable;
import com.winapp.model.CurrencyDeno;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SettlementHeader extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace,Constants,ConsignmentAddressDialog.ConsignmentAddressListener {
    ImageButton searchIcon, custsearchIcon, printer, mInvoiceProductPrint, qrcode,save;
    SlidingMenu menu;
    String valid_url, cmpnyCode;
    List<CurrencyDeno> demoList = new ArrayList<>();
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
        Mint.initAndStartSession(SettlementHeader.this, "29088aa0");
        setContentView(R.layout.activity_settlement_header);

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
        sl_user = (EditText)findViewById(R.id.sl_user);
        SOTDatabase.deleteSettlement();
        sl_user.setText(SupplierSetterGetter.getUsername());

//        list.setHasFixedSize(true);
//        list.setLayoutManager(new LinearLayoutManager(this));


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


        FWMSSettingsDatabase.init(SettlementHeader.this);
        SOTDatabase.init(SettlementHeader.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        startCalendar = Calendar.getInstance();
        new SOTSummaryWebService(valid_url);

        AsyncDenomination salesOAC = new AsyncDenomination();
        salesOAC.execute();

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
                    new DatePickerDialog(SettlementHeader.this, startDate,
                            startCalendar.get(Calendar.YEAR), startCalendar
                            .get(Calendar.MONTH), startCalendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ids =  ((TextView) view
                        .findViewById(R.id.text4)).getText().toString();
                String curCode = ((TextView) view
                        .findViewById(R.id.text1)).getText().toString();
                String deno = ((TextView) view
                        .findViewById(R.id.text2)).getText().toString();
                String total =((TextView) view
                        .findViewById(R.id.text3)).getText().toString();

                Log.d("takeData","-->"+curCode+"-->"+ids);

                ConsignmentAddressDialog dialogFragment = ConsignmentAddressDialog.newInstance(ids,curCode,deno,total);
                dialogFragment.show(getFragmentManager(), "dialog");
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
            Intent intent = new Intent(SettlementHeader.this,SettlementMainHeader.class);
            startActivity(intent);
        }
    }

    private void strtDate() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        date.setText(sdf.format(startCalendar.getTime()));
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(SettlementHeader.this, SettlementMainHeader.class);
        startActivity(i);
        SettlementHeader.this.finish();
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

    @Override
    public void refreshAdapter(String curCode) {
        Cursor cursor = SOTDatabase.getDenominationCursors(curCode);
        Cursor curso1 = SOTDatabase.getDenominationCursor();
        Log.d("getCountCheck","-->"+cursor.getCount()+"-->"+curso1.getCount());
        adapter = new DenominationAdapter(SettlementHeader.this,curso1);
        list.setAdapter(adapter);
        cursor.requery();
        adapter.notifyDataSetChanged();
        double count = SOTDatabase.getTotalAmt();
        Log.d("countcheck","-->"+count);
        totalAmt.setText(""+twoDecimalPoint(count));
    }

    private class AsyncDenomination extends AsyncTask<Void, Void, Void> {
        String vld_rslt;
        @Override
        protected void onPreExecute() {
            demoList = new ArrayList<>();
            demoList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                demoList = ValidateWebService.validateCurrency(valid_url,
                        "fncGetCurrencyDinamination");

                for(int i=0;i<demoList.size();i++){
                    SOTDatabase.storeDenomination(demoList.get(i).getSlno(),demoList.get(i).getCurency(),
                            demoList.get(i).getDenomination(),demoList.get(i).getTotal());
                }

           /*     SoapObject request = new SoapObject(NAMESPACE, "fncGetCurrencyDinamination");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                envelope.bodyOut = request;

                HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);

                androidHttpTransport = new HttpTransportSE(valid_url,TimeOut);
                androidHttpTransport.call(SOAP_ACTION + "fncGetCurrencyDinamination", envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                resTxt = response.toString();
                Log.d("Validate", resTxt);
                String result = " { Validate : " + resTxt + "}";

                JSONObject jsonResponse;

                jsonResponse = new JSONObject(result);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("Validate");
                int lengthJsonArr = jsonMainNode.length();
                for (int i = 0; i < lengthJsonArr; i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    CurrencyDeno demo = new CurrencyDeno();
                    vld_rslt = jsonChildNode.optString("Currency").toString();
                    String slno = String.valueOf(i+1);
                    demo.setSlno(slno);
                    demo.setCurency(vld_rslt);
                    demo.setDenomination("");
                    demo.setTotal("");
                    demoList.add(demo);

                    SOTDatabase.storeDenomination(slno,vld_rslt,"","");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }*/

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

            @Override
        protected void onPostExecute(Void aVoid) {
            cursor = SOTDatabase.getDenominationCursor();
            Log.d("demoList", "-->" + cursor.getCount());
            adapter = new DenominationAdapter(SettlementHeader.this, cursor);
            list.setAdapter(adapter);

        }
    }

    private class DenominationAdapter extends ResourceCursorAdapter{

        public DenominationAdapter(Context context, Cursor cursor) {
            super(context,R.layout.denomination,cursor);

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d("cursor.getCount345","-->"+cursor.getCount());
            TextView slno = (TextView) view.findViewById(R.id.text4);
            slno.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO)));
            TextView currncy = (TextView) view.findViewById(R.id.text1);
            currncy.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));
            TextView deno = (TextView) view.findViewById(R.id.text2);
            deno.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));
            TextView total = (TextView) view.findViewById(R.id.text3);
            total.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));


        }
    }

    /*private class DenominationAdapter extends BaseAdapter{
        Context context;
        List<CurrencyDeno> denoList;
        LayoutInflater inflater;
        public DenominationAdapter(SettlementHeader settlementHeader, List<CurrencyDeno> demoList) {
            Log.d("denoList","-->"+demoList.size());
            this.context = settlementHeader;
            this.denoList = demoList;

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
            final CurrencyDeno deno = denoList.get(position);
            final MyViewHolder holder;

            holder = new MyViewHolder();
            inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.denomination, null);


            holder.currecy = (TextView) convertView.findViewById(R.id.text1);
            holder.denomination = (TextView) convertView.findViewById(R.id.text2);
            holder.total = (TextView) convertView.findViewById(R.id.text3);
            holder.slno = (TextView) convertView.findViewById(R.id.text4);

                holder.currecy.setText(deno.getCurency());
                holder.denomination.setText(deno.getDenomination());
                holder.total.setText(deno.getTotal());
                holder.slno.setText(deno.getSlno());





            return convertView;
        }
    }



    private class MyViewHolder {
        TextView currecy,slno;
        TextView denomination,total;
    }*/

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }


   /* public class DenominationAdapter extends RecyclerView.Adapter<CheckViewHolder> {
        Context context;
        List<CurrencyDeno> denoList;
        LayoutInflater inflater;

        public DenominationAdapter(Context settlementHeader, List<CurrencyDeno> demoList) {
            this.context = settlementHeader;
            this.denoList = demoList;
        }

        @Override
        public CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.denomination, null);
            return new CheckViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CheckViewHolder holder, int position) {
            final CurrencyDeno productStockGetSet = denoList.get(position);
            holder.currecy.setText(productStockGetSet.getCurency());
            holder.denomination.setText(productStockGetSet.getDenomination());
            holder.total.setText(productStockGetSet.getTotal());

            holder.denomination.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if(!holder.denomination.getText().toString().matches("")){
                     String code = holder.currecy.getText().toString();
                     String demo = holder.denomination.getText().toString();
                     double code_int = Double.parseDouble(code);
                     double demo_int = Double.parseDouble(demo);
                     Log.d("valueCheck","-->"+code_int+"-->"+demo_int);

                     double value = code_int*demo_int;
                     Log.d("totalValue","-->"+value);

                     holder.total.setText(""+twoDecimalPoint(value));

                 }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return denoList.size();
        }
    }

    private class CheckViewHolder extends RecyclerView.ViewHolder {
        TextView currecy;
        EditText denomination,total;
        public CheckViewHolder(View itemView) {
            super(itemView);
            currecy = (TextView) itemView.findViewById(R.id.text1);
            denomination = (EditText) itemView.findViewById(R.id.text2);
            total =(EditText)itemView.findViewById(R.id.text3);
        }
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }*/


}
