package com.winapp.sot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.GetUserPermission;
import com.winapp.fwms.LoginWebService;
import com.winapp.fwms.SupplierSetterGetter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 17-Jul-17.
 */

public class DialogPcsPerCarton {
    public interface OnCompleteListener{
        public void OnCompleted(boolean result, String pcspercarton);
    }
    private OnCompleteListener listener;
    private  AlertDialog.Builder builder;
    private Activity activity;
    private LinearLayout spinnerLayout;
    private ProgressBar progressBar;
    private LinearLayout layout_parent;
    private EditText pcspercarton_edt;
    private String pcs="",companyCode="",userId="";
    private HashMap<String,String> hashMap;
    private boolean bResult;
    public void OnCompleteListener(OnCompleteListener listener){
        this.listener = listener;
    }
    public DialogPcsPerCarton(Activity activity,LinearLayout layout_parent){
        this.activity = activity;
        this.layout_parent = layout_parent;

    }
    public void pcsDialog(final String productCode,final String pcsctn) {
        builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.pcspercarton));
        LayoutInflater adbInflater = LayoutInflater.from(activity);
        View view = adbInflater.inflate(R.layout.dialog_pcs, null);
        pcspercarton_edt = (EditText) view.findViewById(R.id.sl_pcspercarton);
        builder.setView(view);
        hashMap = new HashMap<>();
        companyCode = SupplierSetterGetter.getCompanyCode();
        userId = SupplierSetterGetter.getUsername();
        pcspercarton_edt.requestFocus();
        pcspercarton_edt.setText(pcsctn);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setPositiveButton(activity.getResources().getString(R.string.update),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pcs = pcspercarton_edt.getText().toString();
                        if (pcs!= null && !pcs.isEmpty()) {
                                new UpdateProduct().execute(productCode,pcs);
                        }
                        else {
                            Toast.makeText(activity,
                                    "Please Enter the PcsPerCarton",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private class UpdateProduct extends AsyncTask<String, String, String> {
        private String resultStatus = "",pcspercarton="",productCode="";
        @Override
        protected String doInBackground(String... params) {
            try{
                productCode = params[0];
                pcspercarton = params[1];
                Log.d("productCode", productCode);
                Log.d("userId", userId);
                hashMap.put("CompanyCode",companyCode);
                hashMap.put("ProductCode",productCode);
                hashMap.put("PcsPerCarton",pcspercarton);
                hashMap.put("User",userId);
                String result = SalesOrderWebService.getSODetail(hashMap, "fncUpdateProduct");
                Log.d("result", ""+result);
                JSONObject mJSONObject = new JSONObject(result);
                JSONArray mJSONArray = mJSONObject.optJSONArray("SODetails");
                JSONObject jsonChildNode = mJSONArray.getJSONObject(0);
                resultStatus = jsonChildNode.optString("Result").toString();

            }catch(Exception e){
                e.printStackTrace();
            }
            return resultStatus;
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                if(result!=null && !result.isEmpty()){
                    if(result.equalsIgnoreCase("True")){
                        bResult = true;
                    }else{
                        bResult = false;
                    }
                }else{
                    bResult = false;
                }
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(layout_parent, true);

            }catch(Exception e){
                e.printStackTrace();
            }finally {
                Log.d("bResult", ""+bResult);
                Log.d("pcspercarton", pcspercarton);
                listener.OnCompleted(bResult,pcspercarton);
            }

        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(activity);
            spinnerLayout.setGravity(Gravity.CENTER);
            activity.addContentView(spinnerLayout, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(layout_parent, false);
            progressBar = new ProgressBar(activity);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(activity.getResources()
                    .getDrawable(R.drawable.greenprogress));
            spinnerLayout.addView(progressBar);
        }
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
}
