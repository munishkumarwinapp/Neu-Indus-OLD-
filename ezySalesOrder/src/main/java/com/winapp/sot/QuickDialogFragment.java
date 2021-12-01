package com.winapp.sot;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.winapp.SFA.R;

/**
 * Created by Sathish on 1/14/2020.
 */

public class QuickDialogFragment extends DialogFragment {
    String pro_code,lctnCode;
    public static QuickDialogFragment newInstance(String pro_code) {
        Log.d("productCodeCheck",pro_code);
        QuickDialogFragment dialogFragment= new QuickDialogFragment();
        Bundle args = new Bundle();
        args.putString("productCode", pro_code);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    public static QuickDialogFragment newInstatnt(String location_code) {
        Log.d("location_codeCheck",location_code);
        QuickDialogFragment dialog= new QuickDialogFragment();
        Bundle args = new Bundle();
        args.putString("locationCode", location_code);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater
                .inflate(R.layout.stock_alert, container, false);
        pro_code = getArguments().getString("productCode");
        lctnCode = getArguments().getString("locationCode");

        return  dialogView;
    }
}
