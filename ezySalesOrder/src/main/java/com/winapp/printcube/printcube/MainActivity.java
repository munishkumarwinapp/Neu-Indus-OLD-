package com.winapp.printcube.printcube;





import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.winapp.SFA.R;

public class MainActivity extends TabActivity {


	public static TabHost mTabHost;  
    public static TabHost getmTabHost() {  
        return mTabHost;  
    }  
    private TextView mTitle;
    private RadioGroup main_radio;  
    private RadioButton tab_icon_wifi, tab_icon_bluetooth, tab_icon_setting;  
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	
		setContentView(R.layout.activity_main);
		
		
		mTabHost = getTabHost();  
	        final TabWidget tabWidget = mTabHost.getTabWidget();  
	        tabWidget.setStripEnabled(false);// 圆角边线不启用  
	        //添加n个tab选项卡，定义他们的tab名，指示名，目标屏对应的类  
	        mTabHost.addTab(mTabHost.newTabSpec("TAG1").setIndicator("0").setContent(new Intent(this, WifiActivity.class)));  
	        mTabHost.addTab(mTabHost.newTabSpec("TAG2").setIndicator("1").setContent(new Intent(this, BluetoothActivity.class)));  
	        mTabHost.addTab(mTabHost.newTabSpec("TAG3").setIndicator("2").setContent(new Intent(this, SettingActivity.class)));  
	       
	        // 视觉上,用单选按钮替代TabWidget  
	        main_radio = (RadioGroup) findViewById(R.id.main_radio);  
	        tab_icon_wifi = (RadioButton) findViewById(R.id.tab_icon_wifi);  
	        tab_icon_bluetooth = (RadioButton) findViewById(R.id.tab_icon_bluetooth);  
	        tab_icon_setting = (RadioButton) findViewById(R.id.tab_icon_setting);  
	       
	        main_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
	                    @Override  
	                    public void onCheckedChanged(RadioGroup group, int id) {  
	                        if (id == tab_icon_wifi.getId()) {  
	                            mTabHost.setCurrentTab(0);  
	                        } else if (id == tab_icon_bluetooth.getId()) {  
	                            mTabHost.setCurrentTab(1);  
	                        } else if (id == tab_icon_setting.getId()) {  
	                            mTabHost.setCurrentTab(2);  
	                        }
	                    }  
	                });  
	  
	        // 设置当前显示哪一个标签  
	        mTabHost.setCurrentTab(0);  
	        // 遍历tabWidget每个标签，设置背景图片 无  
	        for (int i = 0; i < tabWidget.getChildCount(); i++) {  
	            View vv = tabWidget.getChildAt(i);  
	            vv.getLayoutParams().height = 45;  
	            // vv.getLayoutParams().width = 65;  
	            vv.setBackgroundDrawable(null);  
	        }  
//	      findViewById(R.id.tab_icon_brand).setOnClickListener(this);  
	      
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actionmenu, menu);
		return true;
	}

}
