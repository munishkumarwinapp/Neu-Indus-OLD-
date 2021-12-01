package com.winapp.printcube.printcube;

import com.winapp.SFA.R;
import com.winapp.printcube.utils.Constants;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class SettingActivity extends Activity {
	private static final String TAG = /*Constants.TAG +*/"SettingActivity";
    private static final boolean D = true;
   
	
	
	
	
		private RadioGroup languagerg;
		private RadioButton englishrb;
		private RadioButton schineserb;
		private RadioButton thairb;
		private RadioButton vietnamrb;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		if(D) Log.e(TAG, "onCreate");
		
		languagerg=(RadioGroup)findViewById(R.id.radioGroup1);
		englishrb=(RadioButton)findViewById(R.id.Englishradio);
		schineserb=(RadioButton)findViewById(R.id.Schineseradio);
		thairb=(RadioButton)findViewById(R.id.Thairadio);
		vietnamrb=(RadioButton)findViewById(R.id.Vietnamradio);
	}
	@Override
	 public synchronized void onRestart() 
	 {
	        super.onRestart();
	        if(D) Log.e(TAG, "onRestart");
	 }
	@Override
	 public synchronized void onStart() 
	 {
	        super.onStart();
	        if(D) Log.e(TAG, "onStart");
	 }
	 @Override
	 public synchronized void onResume() 
	 {
	        super.onResume();
	        if(D) Log.e(TAG, "onResume");
	 }
	 @Override
	 public synchronized void onStop() 
	 {
	        super.onStop();
	        if(D) Log.e(TAG, "onStop");
	 }
	 @Override
	 public synchronized void onPause() 
	 {
	        super.onPause();
	        if(D) Log.e(TAG, "onPause");
	        if(englishrb.isChecked())
	        {
	        	GlobalData.languagevalue=Constants.ENGLISH;
	        }
	        else if(schineserb.isChecked())
	        {
	        	GlobalData.languagevalue=Constants.SCHINESE;
	        }
	        else if(thairb.isChecked())
	        {
	        	GlobalData.languagevalue=Constants.THAI;
	        }
	        else if(vietnamrb.isChecked())
	        {
	        	GlobalData.languagevalue=Constants.VIETNAM;
	        }
	        if(D) Log.e(TAG, "GlobalData.languagevalue:"+GlobalData.languagevalue);
	 }
	

}
