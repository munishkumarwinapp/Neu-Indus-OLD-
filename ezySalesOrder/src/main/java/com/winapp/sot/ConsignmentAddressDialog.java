package com.winapp.sot;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;

import java.text.DecimalFormat;

/**
 * Created by Sathish on 2/4/2020.
 */

public class ConsignmentAddressDialog extends DialogFragment {
    String customerId,curncyCode,deno,total;
    EditText denomination,total_edt;
    TextView currency,slno;
    static String curr_str;
    ImageView ok,close;
    ConsignmentAddressListener  consignmentAddressListener;
    double code_int,demo_int;
    public static ConsignmentAddressDialog newInstance(String ids, String curCode, String deno, String total) {
        ConsignmentAddressDialog frag = new ConsignmentAddressDialog();
        Bundle args = new Bundle();
        args.putString("ID", ids);
        args.putString("pCode",curCode);
        args.putString("pName",deno);
        args.putString("Qty",total);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.denimination_dialog, container, false);

        customerId = getArguments().getString("ID");
        curncyCode = getArguments().getString("pCode");
        deno = getArguments().getString("pName");
        total = getArguments().getString("Qty");

        denomination = (EditText)dialogView.findViewById(R.id.sl_RetQty);
        ok = (ImageView)dialogView.findViewById(R.id.ok);
        close = (ImageView)dialogView.findViewById(R.id.close);
        total_edt = (EditText)dialogView.findViewById(R.id.sl_stock_qty);
        currency = (TextView)dialogView.findViewById(R.id.sl_defQty);
        slno = (TextView)dialogView.findViewById(R.id.sl_no);

        slno.setText(customerId);
        currency.setText(curncyCode);
        denomination.setText(deno);
        total_edt.setText(total);

        ok.setOnClickListener(mUpdateOnClickListener);
        close.setOnClickListener(mDismissOnClickListener);


        denomination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!denomination.getText().toString().matches("0") || !denomination.getText().toString().matches("")){
                    try {
                        String demo = denomination.getText().toString();
                        Log.d("checkvaluesDemo", "-->" + demo);
                        String code = currency.getText().toString();
                         code_int = Double.parseDouble(code);
                         demo_int = Double.parseDouble(demo);
                        Log.d("valueCheck", "-->" + code_int + "-->" + demo_int);

                        double value = code_int * demo_int;
//                        int values = (int) value;
                        Log.d("totalValue", "-->" + value);

                        total_edt.setText("" + twoDecimalPoint(value));

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return  dialogView;
    }

    private View.OnClickListener mUpdateOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            storeDatabase();
            Log.d("pcodeCheck",curr_str);
            consignmentAddressListener = (ConsignmentAddressListener) getActivity();
            consignmentAddressListener.refreshAdapter(curr_str);
            dismiss();
        }
    };
    private View.OnClickListener mDismissOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            dismiss();
        }
    };

    private void storeDatabase() {
        curr_str = currency.getText().toString();
        String deno = denomination.getText().toString();
        String tot = total_edt.getText().toString();
        String slnos = slno.getText().toString();

        Log.d("checkData","-->"+curr_str+"-->"+deno+"tot"+tot+"slno"+slnos);

        SOTDatabase.updateDenomination(slnos,curr_str,deno,tot);
        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();


    }

    public interface ConsignmentAddressListener {
        void refreshAdapter(String curCode);
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }
}
