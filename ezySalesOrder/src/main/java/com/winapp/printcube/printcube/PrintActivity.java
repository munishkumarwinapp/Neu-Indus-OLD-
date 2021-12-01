package com.winapp.printcube.printcube;

import java.io.UnsupportedEncodingException;


import com.winapp.SFA.R;
import com.winapp.printcube.model.TransparentTransmissionListener;
import com.winapp.printcube.utils.Constants;



import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class PrintActivity extends Activity {
	
	private long exitTime = 0;
	
	private static final String TAG =Constants.TAG +"PrintActivity";
    private static final boolean D = true;
    private int commValue=0;
    private int BLUETOOTH=0;
    private int WIFI=1;
    
    private static final short RS232ERR_READ_TIMEOUT	=						-553;
    private static final short SPRS232_ERR_ERROR		=						-560;
    private static final short ILV_ERROR_NOT_ENOUGH_MEMORY=					-602;	/*0xDA6*/
    private static final short ILVERR_STATUS              = -605;
    private static final short MESSAGE_PLACE_FINGER= 10;
    private static final short MESSAGE_MOVE_UP= 11;
    private static final short MESSAGE_MOVE_DOWN= 12;
    private static final short MESSAGE_MOVE_RIGHT= 13;
    private static final short MESSAGE_MOVE_LEFT= 14;
    private static final short MESSAGE_PRESS_HARDER= 15;
    private static final short MESSAGE_MOVE_FINGER= 16;
    private static final short MESSAGE_REMOVE_FINGER= 17;
    private static final short MESSAGE_UNKNOWN_MESSAGE= 18;
    private static final short MESSAGE_VERIFY_HIT= 0;
    private static final short MESSAGE_VERIFY_NOHIT= 1;
    private static final short MESSAGE_VERIFY_UNKNOWN_MATCH_RESULT= 2;
    
    private static final short SC_VCCERR	=-2100;
    private static final short SC_SLOTERR	=-2101;
    private static final short SC_PARERR	=-2102;
    private static final short SC_PARAERR	=-2103;
    private static final short SC_PROTOCALERR	=-2104;
    private static final short SC_DATALENERR	=-2105;
    private static final short SC_CARDOUT	=-2106;
    private static final short SC_NORESET	=-2107;
    private static final short SC_TIMEOUT	=-2108;
    private static final short SC_PPSERR	=-2109;
    private static final short SC_ATRERR	=-2110;
    private static final short SC_APDUERR	=-2111;
    private static final short SC_T0_TIMEOUT	=-2200;
    private static final short SC_T0_MORESENDERR	=-2201;
    private static final short SC_T0_MORERECEERR	=-2202;
    private static final short SC_T0_PARERR	=-2203;
    private static final short SC_T0_INVALIDSW	=-2204;
    private static final short SC_ATR_TSERR	=-2205;
    private static final short SC_ATR_TCKERR	=-2206;
    private static final short SC_ATR_TIMEOUT	=-2207;
    private static final short SC_ATR_TA1ERR	=-2208;
    private static final short SC_ATR_TA2ERR	=-2209;
    private static final short SC_ATR_TA3ERR	=-2210;
    private static final short SC_ATR_TB1ERR	=-2211;
    private static final short SC_ATR_TB2ERR	=-2212;
    private static final short SC_ATR_TB3ERR	=-2213;
    private static final short SC_ATR_TC1ERR	=-2214;
    private static final short SC_ATR_TC2ERR	=-2215;
    private static final short SC_ATR_TC3ERR	=-2216;
    private static final short SC_ATR_TD1ERR	=-2217;
    private static final short SC_ATR_TD2ERR	=-2218;
    private static final short SC_ATR_LENGTHERR	=-2219;
    private static final short SC_T1_BWTERR	=-2220;
    private static final short SC_T1_CWTERR	=-2221;
    private static final short SC_T1_ABORTERR	=-2222;
    private static final short SC_T1_EDCERR	=-2223;
    private static final short SC_T1_SYNCHERR	=-2224;
    private static final short SC_T1_EGTERR	=-2225;
    private static final short SC_T1_BGTERR	=-2226;
    private static final short SC_T1_NADERR	=-2227;
    private static final short SC_T1_PCBERR	=-2228;
    private static final short SC_T1_LENGTHERR	=-2229;
    private static final short SC_T1_IFSCERR	=-2230;
    private static final short SC_T1_IFSDERR	=-2231;
    private static final short SC_T1_MOREERR	=-2232;
    private static final short SC_T1_PARITYERR	=-2233;
    private static final short SC_T1_INVALIDBLOCK	=-2234;

    
      
    
    private static final int MSG_DATA = 0x0a;
    
    private static final String KEY_BUFFER = "KEY_BUFFER";
	private static final String KEY_LENGTH = "KEY_LENGTH";
	
		private EditText mOutEditText;
		private Button mReceiptButton;
		private Button mInstantImageButton;
		private Button mPreloadImageButton;
	
		private Button mPrintButton;
		private Button mOneDButton;
		private Button mTwoDButton;
		private Button mGs1Button;
		private Button mLeftFVButton;
		private Button mRightFVButton;
		private Button mGetDesButton;
		private TextView fvView;
		private CheckBox emphaCheckbox;
		private CheckBox underlineCheckbox; 
		private RadioGroup adjustrg;
		private RadioButton leftrb;
		private RadioButton centerrb;
		private RadioButton rightrb;
		private Spinner charsizespinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		
		setContentView(R.layout.activity_print);
		//ExitAQuitApplication.add(this);
		if(commValue==BLUETOOTH)
			GlobalData.mService.setHandler(mHandler);
		else
		{
			GlobalData.mTTransmission.setListener(new TransparentTransmissionListener() 
			{
				
				@Override
				public void onReceive(byte[] data, int length) 
				{
					String message = new String(data, 0, length);
		            fvView.setText(message);
				}
				
				@Override
				public void onOpen(boolean success) 
				{
					
					
				}
			});
		}
		Intent intent=getIntent();
		commValue=intent.getIntExtra("COMM", 0);
		
		emphaCheckbox=(CheckBox) findViewById(R.id.emphercheckBox);
		underlineCheckbox=(CheckBox) findViewById(R.id.undercheckBox);
		
		adjustrg=(RadioGroup)findViewById(R.id.radioGroup1);
    	leftrb=(RadioButton)findViewById(R.id.leftradio);
    	centerrb=(RadioButton)findViewById(R.id.centerradio);
    	rightrb=(RadioButton)findViewById(R.id.rightradio);
    	
    	charsizespinner=(Spinner) findViewById(R.id.charsizespinner);
		
    	init();
		 
	}
	 @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }
	

	 @Override
	 public synchronized void onResume() 
	 {
	        super.onResume();
	        if(D) Log.e(TAG, "+ ON RESUME +");

	        // Performing this check in onResume() covers the case in which BT was
	        // not enabled during onStart(), so we were paused to enable it...
	        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
	        if(commValue==BLUETOOTH)
	        {
	        	if (GlobalData.mService != null) 
	        	{
	        		// Only if the state is STATE_NONE, do we know that we haven't started already
	        		if (GlobalData.mService.getState() == GlobalData.STATE_NONE) 
	        		{
	        			// Start the Bluetooth chat services
	        			GlobalData.mService.start();
	        		}
	        	}
	        }
	    }

	
	private void init() {
       
        mOutEditText = (EditText) findViewById(R.id.chareditText);
        mPrintButton = (Button) findViewById(R.id.printbutton);
		mPrintButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				if(emphaCheckbox.isChecked())
				{
				byte[] send=new byte[3] ;
	            send[0]=0x1b;send[1]=0x45;
	            send[2]=1;
	            if(commValue==BLUETOOTH)
	            	GlobalData.mService.write(send);
	            else
	            	GlobalData.mTTransmission.sendBytes(send);
				}
				if(underlineCheckbox.isChecked())
				{ 
					byte[] send=new byte[3] ;
		            send[0]=0x1b;send[1]=0x2d;
		            send[2]=1;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
					}
				if(leftrb.isChecked()){ 
					byte[] send=new byte[3] ;
		            send[0]=0x1b;send[1]=0x61;
		            send[2]=0;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
					}
				else if(centerrb.isChecked()){ 
					byte[] send=new byte[3] ;
		            send[0]=0x1b;send[1]=0x61;
		            send[2]=1;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
					}
				else if(rightrb.isChecked()){ 
					byte[] send=new byte[3] ;
		            send[0]=0x1b;send[1]=0x61;
		            send[2]=2;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
					}
				
				int charsizeselect=charsizespinner.getSelectedItemPosition();
				
			
				byte[] send=new byte[3] ;
	            send[0]=0x1d;send[1]=0x21;
	            send[2]=(byte)(charsizeselect&0x000000ff);
	            if(commValue==BLUETOOTH)
	            	GlobalData.mService.write(send);
	            else
	            	GlobalData.mTTransmission.sendBytes(send);
				
				String message = mOutEditText.getText().toString();
				if(commValue==BLUETOOTH)
					sendMessage(message+"\r\n");
				else
					GlobalData.mTTransmission.send(message+"\r\n");
				
					//byte[] send=new byte[3] ;
		            send[0]=0x1b;send[1]=0x45;
		            send[2]=0;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
					
					
						send[0]=0x1b;send[1]=0x2d;
			            send[2]=0;
			            if(commValue==BLUETOOTH)
			            	GlobalData.mService.write(send);
			            else
			            	GlobalData.mTTransmission.sendBytes(send);
			          
			           
			            send[0]=0x1b;send[1]=0x61;
			            send[2]=0;
			            if(commValue==BLUETOOTH)
			            	GlobalData.mService.write(send);
			            else
			            	GlobalData.mTTransmission.sendBytes(send);
			           	
			         
			           	send[0]=0x1d;send[1]=0x21;
			            send[2]=0;
			            if(commValue==BLUETOOTH)
			            	GlobalData.mService.write(send);
			            else
			            	GlobalData.mTTransmission.sendBytes(send);
						

			}
		});
    
     		mReceiptButton = (Button) findViewById(R.id.receiptbutton);
     		mReceiptButton.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				byte[] send=new byte[3] ;
     				if(D) Log.e(TAG, "GlobalData.languagevalue:"+GlobalData.languagevalue);
     				   	              	         
    	            String message="";
    	            if(GlobalData.languagevalue==Constants.ENGLISH)
    	            {
    	            	send[0]=0x1b;send[1]=0x21;
         	            send[2]=0x01;  //old
//    	            	send[2]=1;  //new
         	            if(commValue==BLUETOOTH)
         	            	GlobalData.mService.write(send);
       	                else
       	            	   GlobalData.mTTransmission.sendBytes(send); 
    	            	send[0]=0x1b;send[1]=0x52;
	    	            send[2]=0x00;  //old
//    	            	send[2]=1;  //new
	    	            if(commValue==BLUETOOTH)
	    	            	GlobalData.mService.write(send);
	    	            else
	    	            	GlobalData.mTTransmission.sendBytes(send); 
	    	            send[0]=0x1b;send[1]=0x74;
	    	            send[2]=0x00; //old
//	    	            send[2]=1;  //new
	    	            if(commValue==BLUETOOTH)
	    	            	GlobalData.mService.write(send);
	    	            else
	    	            	/*GlobalData.mTTransmission.sendBytes(send); 
     				message = "WINAPP SOLUTIONS PTE LTD\r\n" +
     						         "MERCHANT NAME    Printcube Coffee\r\n"+
     						         "MASTER           Visitor\r\n" +
     						         "Address Unit A, 1st floor, Block B,\r\n"+
     						         "        OPQR Industrial Plant,\r\n" +
     						         "        123456, UVWXYZ\r\n" +
    						         "HELP DESK        +01-23456789\r\n"+
    						         "\r\n" +
    						         "----------------------\r\n" +
    						         "Product   Sale   Price\r\n" +
    						         "----------------------\r\n" +
    						         "Cafe mocha   2   7.5$\r\n" +
    						         "Cafe latte   1   7.0$\r\n" +
    						         "Cappuccino   1   7.5$\r\n" +
    						         "----------------------\r\n" +
    						         "Total            29.5$\r\n" +
    						         "----------------------\r\n" +
    						         "\r\n" +
    						         "Thankkkkkkkk youuuuuuuuuu\r\n"+
    						         "\r\n"+
    						         "\r\n"+
    						         "\r\n";*/
	    	            	
	    	            	GlobalData.mTTransmission.sendBytes(send); 
	     				message = "									WINAPP SOLUTIONS PTE LTD\r\n" +
	     						         "							MERCHANT NAME    Printcube Coffee\r\n"+
	     						         "							MASTER           Visitor\r\n" +
	     						         "							Address Unit A, 1st floor, Block B, OPQR Industrial Plant 123456, UVWXYZ\r\n"+
	    						         "							HELP DESK        +01-23456789\r\n"+
	    						         "\r\n" +
	    						         "-------------------------------------------------------------------------------\r\n" +
	    						         "					Product Name   		Quantity			Price   		Total\r\n" +
	    						         "-------------------------------------------------------------------------------\r\n" +
	    						         "					Cafe mocha   			2   		 	 7.5$			15.00\r\n" +
	    						         "					Cafe latte   			1   		 	 7.0$			14.00\r\n" +
	    						         "					Cappuccino   			1   		 	 7.5$			15.00\r\n" +
	    						         "-------------------------------------------------------------------------------\r\n" +
	    						         "					Total            										44.00$\r\n" +
	    						         "-------------------------------------------------------------------------------\r\n" +
	    						         "\r\n" +
	    						         "										Thank you\r\n"+
	    						         "\r\n"+
	    						         "\r\n"+
	    						         "\r\n";
    	            }
    	            else if(GlobalData.languagevalue==Constants.THAI)
    	            {
    	            	send[0]=0x1b;send[1]=0x21;
         	            send[2]=0x00;
         	            if(commValue==BLUETOOTH)
         	            	GlobalData.mService.write(send);
       	                else
       	            	   GlobalData.mTTransmission.sendBytes(send); 
    	            	 send[0]=0x1b;send[1]=0x52;
    	    	            send[2]=0x0b;
    	    	            if(commValue==BLUETOOTH)
    	    	            	GlobalData.mService.write(send);
    	    	            else
    	    	            	GlobalData.mTTransmission.sendBytes(send); 
    	    	            send[0]=0x1b;send[1]=0x74;
    	    	            send[2]=0x01;
    	    	            if(commValue==BLUETOOTH)
    	    	            	GlobalData.mService.write(send);
    	    	            else
    	    	            	GlobalData.mTTransmission.sendBytes(send); //é€‰æ‹©ç¼–ç �æ ¼å¼�utf-8
     				message = "        à¹ƒà¸šà¹€à¸ªà¸£à¹‡à¸ˆà¸£à¸±à¸šà¹€à¸‡à¸´à¸™à¸�à¸²à¸£à¸‚à¸²à¸¢\r\n" +
     						         "à¸Šà¸·à¹ˆà¸­à¸œà¸¹à¹‰à¸‚à¸²à¸¢ Printcube à¸�à¸²à¹�à¸Ÿ\r\n"+
     						         "à¹€à¸ˆà¹‰à¸²à¸™à¸²à¸¢  à¸œà¸¹à¹‰à¸¡à¸²à¹€à¸¢à¸·à¸­à¸™\r\n" +
     						         "à¸—à¸µà¹ˆà¸­à¸¢à¸¹à¹ˆ Unit A, 1st floor,\r\n " +
     						         "   Block B,\r\n"+
     						         "   OPQR Industrial Plant,\r\n" +
     						         "   123456, UVWXYZ\r\n" +
    						         "à¹‚à¸—à¸£à¸¨à¸±à¸žà¸—à¹Œ    +01-23456789\r\n"+
    						         "\r\n" +
    						         "----------------------\r\n" +
    						         "à¸‚à¸²à¸¢à¸ªà¸´à¸™à¸„à¹‰à¸²à¸£à¸²à¸„à¸²\r\n" +
    						         "----------------------\r\n" +
    						         "Cafe mocha   2   7.5$\r\n" +
    						         "Cafe latte   1   7.0$\r\n" +
    						         "Cappuccino   1   7.5$\r\n" +
    						         "----------------------\r\n" +
    						         "à¸£à¸§à¸¡  29.5$\r\n" +
    						         "----------------------\r\n" +
    						         "\r\n" +
    						         "              à¸‚à¸­à¸šà¸„à¸¸à¸“\r\n"+
    						         "\r\n"+
    						         "\r\n"+
    						         "\r\n";
    	            }
    	            else if(GlobalData.languagevalue==Constants.VIETNAM)
    	            {
    	            	send[0]=0x1b;send[1]=0x21;
         	            send[2]=0x00;
         	            if(commValue==BLUETOOTH)
         	            	GlobalData.mService.write(send);
       	                else
       	            	   GlobalData.mTTransmission.sendBytes(send); //é€‰æ‹©å­—ä½“å¤§å°� 
    	            	 send[0]=0x1b;send[1]=0x52;
    	    	            send[2]=0x0c;
    	    	            if(commValue==BLUETOOTH)
    	    	            	GlobalData.mService.write(send);
    	    	            else
    	    	            	GlobalData.mTTransmission.sendBytes(send); //é€‰æ‹©å­—ç¬¦é›† 0x0bä¸ºæ³°æ–‡,0x0cä¸ºè¶Šå�—æ–‡
    	    	            send[0]=0x1b;send[1]=0x74;
    	    	            send[2]=0x01;
    	    	            if(commValue==BLUETOOTH)
    	    	            	GlobalData.mService.write(send);
    	    	            else
    	    	            	GlobalData.mTTransmission.sendBytes(send); //é€‰æ‹©ç¼–ç �æ ¼å¼�utf-8
     				message = "       hoÃ¡ Ä‘Æ¡n bÃ¡n hÃ ng\r\n" +
     						         "tÃªn thÆ°Æ¡ng Printcube cÃ  phÃª\r\n"+
     						         "chu           khÃ¡ch\r\n" +
     						         "Ä‘ia chi      Unit A, 1st floor,\r\n" +
     						         "        Block B,\r\n"+
     						         "        OPQR Industrial Plant,\r\n" +
     						         "        123456, UVWXYZ\r\n" +
    						         "Ä‘iÃªn thoai       +01-23456789\r\n"+
    						         "\r\n" +
    						         "----------------------\r\n" +
    						         "GiÃ¡ bÃ¡n san pham\r\n" +
    						         "----------------------\r\n" +
    						         "Cafe mocha   2   7.5$\r\n" +
    						         "Cafe latte   1   7.0$\r\n" +
    						         "Cappuccino   1   7.5$\r\n" +
    						         "----------------------\r\n" +
    						         "tÃ´ng            29.5$\r\n" +
    						         "----------------------\r\n" +
    						         "\r\n" +
    						         "             cam Æ¡n ban\r\n"+
    						         "\r\n"+
    						         "\r\n"+
    						         "\r\n";
    	            }	
    	           
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
     				
     				if(commValue==BLUETOOTH)
     					sendMessage("\r\n\r\n");
     				else
    	            	GlobalData.mTTransmission.send("\r\n\r\n");
    	           	
     			}
     		});
     		
     		mInstantImageButton = (Button) findViewById(R.id.instantimagebutton);
     		mInstantImageButton.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				byte gImage_a[] = { 
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x7F,
     						(byte)0xC0,(byte)0x7F,(byte)0xE0,(byte)0x38,(byte)0x1F,(byte)0x87,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0xFF,(byte)0xE0,(byte)0xFF,(byte)0xF0,(byte)0x78,(byte)0x1F,(byte)0xCF,(byte)0x8F,(byte)0xFF,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0xF0,(byte)0x00,(byte)0xFF,(byte)0xF0,(byte)0xFF,(byte)0xF0,(byte)0x78,(byte)0x1F,(byte)0xC7,(byte)0x8F,(byte)0xFF,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xF0,(byte)0x00,(byte)0xFF,(byte)0xF0,(byte)0xFF,(byte)0xF0,(byte)0x78,
     						(byte)0x1F,(byte)0xE7,(byte)0x8F,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xF8,(byte)0xF0,(byte)0xF8,(byte)0xF8,(byte)0x78,(byte)0x1F,(byte)0xE7,(byte)0x81,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0xF8,(byte)0xF8,(byte)0xF8,(byte)0xF8,(byte)0x78,(byte)0x1F,(byte)0xE7,(byte)0x80,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0xF8,(byte)0xF8,(byte)0xF8,(byte)0xF0,(byte)0x78,(byte)0x1F,(byte)0xF7,(byte)0x80,(byte)0xF0,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0xF0,(byte)0x00,(byte)0xFF,(byte)0xF0,(byte)0xFF,(byte)0xF0,(byte)0x78,(byte)0x1F,(byte)0xF7,(byte)0x80,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0xF0,(byte)0x00,(byte)0xFF,(byte)0xF0,(byte)0xFF,(byte)0xF0,(byte)0x78,(byte)0x1E,(byte)0xFF,(byte)0x80,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xF0,(byte)0x00,(byte)0xFF,(byte)0xE0,(byte)0xFF,(byte)0xF0,(byte)0x78,(byte)0x1E,(byte)0xFF,(byte)0x80,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xFF,(byte)0xC0,(byte)0xFF,(byte)0xF0,(byte)0x78,(byte)0x1E,(byte)0xFF,(byte)0x80,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFE,(byte)0x01,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0xF8,(byte)0x00,(byte)0xF8,(byte)0xF8,(byte)0x78,(byte)0x1E,(byte)0x7F,(byte)0x80,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x81,(byte)0xFF,(byte)0xB0,(byte)0x00,(byte)0xF8,(byte)0x00,(byte)0xF8,(byte)0xF8,(byte)0x78,(byte)0x1E,(byte)0x7F,(byte)0x80,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x80,(byte)0x7F,(byte)0xF0,(byte)0x00,(byte)0xF8,(byte)0x00,(byte)0xF8,(byte)0xF8,(byte)0x78,(byte)0x1E,(byte)0x3F,(byte)0x80,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xE0,(byte)0x7F,(byte)0xF0,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0x70,(byte)0x70,(byte)0x38,(byte)0x1C,(byte)0x3F,(byte)0x00,(byte)0x60,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xC3,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,
     						(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF8,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFE,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFF,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0xE3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0xFE,(byte)0x0F,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xE3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x3E,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xE3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x3E,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF8,
     						(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x3E,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x3E,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xBC,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x3E,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE3,
     						(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x3E,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x7E,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFD,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0xFE,(byte)0x0F,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,
     						(byte)0xC7,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF8,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFE,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xFF,(byte)0xFF,(byte)0xFF,
     						(byte)0xFF,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x8F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xE3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x8F,(byte)0x80,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xF3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x8F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xF3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x8F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xF3,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x8F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xF3,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x8F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xF3,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x8F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xE3,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0x0F,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFE,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xE1,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x87,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC1,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x87,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFE,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x07,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x00,(byte)0x3F,(byte)0xFF,(byte)0xFF,(byte)0xFC,(byte)0x03,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF0,(byte)0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
     						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,


};

     				byte[] send=new byte[gImage_a.length+5] ;
    	            send[0]=0x1b;send[1]=0x58;
    	            send[2]=0x34;
    	            send[3]=32;send[4]=100;
    	            System.arraycopy(gImage_a, 0, send, 5, gImage_a.length);
    	            if(commValue==BLUETOOTH)
    	            	GlobalData.mService.write(send);
    	            else
    	            	GlobalData.mTTransmission.sendBytes(send);
     				String message = "\r\n"+
    						         "\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
     				
     			}
     		});
     		mPreloadImageButton = (Button) findViewById(R.id.preloadimagebutton);
     		mPreloadImageButton.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				
     				byte[] sendx=new byte[3] ;
    	            sendx[0]=0x1b;sendx[1]=0x66;
    	            sendx[2]=0x0;
    	           	//æ‰“å�°ç¬¬0å¼ å›¾ç‰‡
    	           	if(commValue==BLUETOOTH)
    	            	GlobalData.mService.write(sendx);
    	            else
    	            	GlobalData.mTTransmission.sendBytes(sendx);
    	           	String messagex = "\r\n"+
					         "\r\n";
    	           	sendMessage(messagex);
    	           	if(commValue==BLUETOOTH)
     					sendMessage(messagex);
     				else
    	            	GlobalData.mTTransmission.send(messagex);
     			}
     		});
     		
     		
     		mOneDButton = (Button) findViewById(R.id.onedbutton);
     		mOneDButton.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				
     			
     				String message = "UPC-A Barcode\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		            byte[] send=new byte[15] ;
		            send[0]=0x1d;send[1]=0x6b;
		            send[2]=65;
		            send[3]=11;
		            send[4]=0x30;send[5]=0x31;send[6]=0x32;send[7]=0x33;send[8]=0x34;send[9]=0x35;
		            send[10]=0x36;send[11]=0x37;send[12]=0x38;send[13]=0x39;send[14]=0x30;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
		           	
		           	message = "\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	message = "UPC-E Barcode\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	byte[] sendx=new byte[10] ;
		            sendx[0]=0x1d;sendx[1]=0x6b;
		            sendx[2]=66;
		            sendx[3]=6;
		            sendx[4]=0x30;sendx[5]=0x31;sendx[6]=0x32;sendx[7]=0x33;sendx[8]=0x34;sendx[9]=0x35;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(sendx);
		            else
		            	GlobalData.mTTransmission.sendBytes(sendx);
		           	
		           	message = "\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	message = "EAN13 Barcode\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	byte[] sendy=new byte[16] ;
		            sendy[0]=0x1d;sendy[1]=0x6b;
		            sendy[2]=67;
		            sendy[3]=12;
		            sendy[4]=0x30;sendy[5]=0x31;sendy[6]=0x32;sendy[7]=0x33;sendy[8]=0x34;sendy[9]=0x35;
		            sendy[10]=0x36;sendy[11]=0x37;sendy[12]=0x38;sendy[13]=0x39;sendy[14]=0x30;sendy[15]=0x31;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(sendy);
		            else
		            	GlobalData.mTTransmission.sendBytes(sendy);
		           	
		        	message = "\r\n";
		        	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	message = "EAN8 Barcode\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	byte[] sendz=new byte[16] ;
		            sendz[0]=0x1d;sendz[1]=0x6b;
		            sendz[2]=68;
		            sendz[3]=12;
		            sendz[4]=0x30;sendz[5]=0x31;sendz[6]=0x32;sendz[7]=0x33;sendz[8]=0x34;sendz[9]=0x35;
		            sendz[10]=0x36;sendz[11]=0x37;sendz[12]=0x38;sendz[13]=0x39;sendz[14]=0x30;sendz[15]=0x31;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(sendz);
		            else
		            	GlobalData.mTTransmission.sendBytes(sendz);
		           	
		        	message = "\r\n";
		        	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	message = "CODE39 Barcode\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	byte[] sendm=new byte[15] ;
		            sendm[0]=0x1d;sendm[1]=0x6b;
		            sendm[2]=69;
		            sendm[3]=10;
		            sendm[4]=0x30;sendm[5]=0x31;sendm[6]=0x32;sendm[7]=0x33;sendm[8]=0x34;sendm[9]=0x35;
		            sendm[10]=0x36;sendm[11]=0x37;sendm[12]=0x38;sendm[13]=0x39;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(sendm);
		            else
		            	GlobalData.mTTransmission.sendBytes(sendm);
		           	
		           	message = "\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	message = "ITF Barcode\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	byte[] sendn=new byte[16] ;
		            sendn[0]=0x1d;sendn[1]=0x6b;
		            sendn[2]=70;
		            sendn[3]=12;
		            sendn[4]=0x30;sendn[5]=0x31;sendn[6]=0x32;sendn[7]=0x33;sendn[8]=0x34;sendn[9]=0x35;
		            sendn[10]=0x36;sendn[11]=0x37;sendn[12]=0x38;sendn[13]=0x39;sendn[14]=0x30;sendn[15]=0x31;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(sendn);
		            else
		            	GlobalData.mTTransmission.sendBytes(sendn);
		           	
		           	message = "\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	message = "CODEBAR Barcode\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	byte[] sendp=new byte[15] ;
		            sendp[0]=0x1d;sendp[1]=0x6b;
		            sendp[2]=71;
		            sendp[3]=11;
		            sendp[4]='A';sendp[5]=0x31;sendp[6]=0x32;sendp[7]=0x33;sendp[8]=0x34;sendp[9]=0x35;
		            sendp[10]=0x36;sendp[11]=0x37;sendp[12]=0x38;sendp[13]=0x39;sendp[14]='D';
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(sendp);
		            else
		            	GlobalData.mTTransmission.sendBytes(sendp);
		           	
		           	
		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);		
     				
     			}
     		});
     		
     		mTwoDButton = (Button) findViewById(R.id.twodbutton);
     		mTwoDButton.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				
     				String message = "PDF417 2D Barcode\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		            byte[] send=new byte[3] ;
		            send[0]=0x1d;send[1]=0x5a;
		            send[2]=0;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
		           	
		           	byte[] senddata=new byte[19] ;
		           	senddata[0]=0x1b;senddata[1]=0x5a;
		           	senddata[2]=0;
		           	senddata[3]='H';
		           	senddata[4]=5;
		           	senddata[5]=12;
		           	senddata[6]=0;
		           	senddata[7]=0x33;senddata[8]=0x34;senddata[9]=0x35;
		           	senddata[10]=0x36;senddata[11]=0x37;senddata[12]=0x38;
		           	senddata[13]=0x39;senddata[14]=0x30;senddata[15]=0x31;
		           	senddata[16]=0x36;senddata[17]=0x37;senddata[18]=0x38;
		        
		           	if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
     				message = "DATAMATRIX 2D Barcode\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		            send[0]=0x1d;send[1]=0x5a;
		            send[2]=1;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
		           	           		        
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
     				message = "QR-CODE 2D Barcode\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		            send[0]=0x1d;send[1]=0x5a;
		            send[2]=2;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
		           			           			        
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
     				
     				message = "Micro PDF417 2D Barcode\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		            send[0]=0x1d;send[1]=0x5a;
		            send[2]=3;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
		           			           			        
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
     				message = "Truncated PDF417 2D Barcode\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		            send[0]=0x1d;send[1]=0x5a;
		            send[2]=4;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
		           			           			        
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
     				message = "Maxicode 2D Barcode\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		            send[0]=0x1d;send[1]=0x5a;
		            send[2]=5;
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(send);
		            else
		            	GlobalData.mTTransmission.sendBytes(send);
		           			           			        
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
     			}
     		});
     		
     		mGs1Button = (Button) findViewById(R.id.gs1button);
     		mGs1Button.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				
     				String message = "GS1 Databar type=0(RSS-14)\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           		           	
		           	byte[] senddata=new byte[14] ;
		           	senddata[0]=0x1d;senddata[1]=0x31;
		           	senddata[2]=0;
		           	senddata[3]=1;
		           	senddata[4]=0x31;senddata[5]=0x32;senddata[6]=0x33;
		           	senddata[7]=0x33;senddata[8]=0x34;senddata[9]=0x35;
		           	senddata[10]=0x36;senddata[11]=0x37;senddata[12]=0x38;
		           	senddata[13]=0x00;
		        
		           	if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	
		           	message = "GS1 Databar type=1(RSS-14)\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		           	if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
     				
     				message = "GS1 Databar type=2(RSS-14 Stacked)\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		          
     				senddata[2]=2;           		        
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
		           	message = "GS1 Databar type=3(RSS-14 Stacked Omnidirectional)\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		          
     				senddata[2]=3;           		        
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
     				
		           	message = "GS1 Databar type=4(RSS-14 Stacked Limited)\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		          
     				senddata[2]=4;           		        
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(senddata);
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
		           	message = "GS1 Databar type=5(RSS-14 Expanded)\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		          
     				byte[] sendx=new byte[45] ;
		           	sendx[0]=0x1d;sendx[1]=0x31;
		           	sendx[2]=5;
		           	sendx[3]=1;
		           	sendx[4]=0x5b;sendx[5]=0x30;sendx[6]=0x31;sendx[7]=0x5d;
		           	sendx[8]=0x39;sendx[9]=0x30;sendx[10]=0x30;sendx[11]=0x31;sendx[12]=0x32;
		           	sendx[13]=0x33;sendx[14]=0x34;sendx[15]=0x35;sendx[16]=0x36;sendx[17]=0x37;
		           	sendx[18]=0x38;sendx[19]=0x39;sendx[20]=0x30;sendx[21]=0x38;sendx[22]=0x5b;
		           	sendx[23]=0x33;sendx[24]=0x31;sendx[25]=0x30;sendx[26]=0x33;sendx[27]=0x5d;
		           	sendx[28]=0x30;sendx[29]=0x31;sendx[30]=0x32;sendx[31]=0x32;sendx[32]=0x33;
		           	sendx[33]=0x33;sendx[34]=0x5b;sendx[35]=0x31;sendx[36]=0x35;sendx[37]=0x5d;
		           	sendx[38]=0x39;sendx[39]=0x39;sendx[40]=0x31;sendx[41]=0x32;sendx[42]=0x33;
		           	sendx[43]=0x31;sendx[44]=0x00;
		           	
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(sendx);
		            else
		            	GlobalData.mTTransmission.sendBytes(sendx);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
		           	message = "GS1 Databar type=6(RSS-14 Stacked Expanded)\r\n";
     				if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);
		          
     				byte[] sendy=new byte[45] ;
		           	sendy[0]=0x1d;sendy[1]=0x31;
		           	sendy[2]=6;
		           	sendy[3]=2;
		           	sendy[4]=0x5b;sendy[5]=0x30;sendy[6]=0x31;sendy[7]=0x5d;
		           	sendy[8]=0x39;sendy[9]=0x30;sendy[10]=0x30;sendy[11]=0x31;sendy[12]=0x32;
		           	sendy[13]=0x33;sendy[14]=0x34;sendy[15]=0x35;sendy[16]=0x36;sendy[17]=0x37;
		           	sendy[18]=0x38;sendy[19]=0x39;sendy[20]=0x30;sendy[21]=0x38;sendy[22]=0x5b;
		           	sendy[23]=0x33;sendy[24]=0x31;sendy[25]=0x30;sendy[26]=0x33;sendy[27]=0x5d;
		           	sendy[28]=0x30;sendy[29]=0x31;sendy[30]=0x32;sendy[31]=0x32;sendy[32]=0x33;
		           	sendy[33]=0x33;sendy[34]=0x5b;sendy[35]=0x31;sendy[36]=0x35;sendy[37]=0x5d;
		           	sendy[38]=0x39;sendy[39]=0x39;sendy[40]=0x31;sendy[41]=0x32;sendy[42]=0x33;
		           	sendy[43]=0x31;sendy[44]=0x00;
		           	
		            if(commValue==BLUETOOTH)
		            	GlobalData.mService.write(sendy);
		            else
		            	GlobalData.mTTransmission.sendBytes(sendy);
		           		           	
		           	
		           	message = "\r\n\r\n\r\n";
		           	if(commValue==BLUETOOTH)
     					sendMessage(message);
     				else
    	            	GlobalData.mTTransmission.send(message);	
     				
     			}
     		});
     		mLeftFVButton = (Button) findViewById(R.id.leftfingerverifybutton);
     		mLeftFVButton.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				fvView.setText("");			           		           	
		           	byte[] senddata=new byte[4] ;
		           	senddata[0]=0x1d;senddata[1]=0x46;
		           	senddata[2]=0x56;	senddata[3]=0;
		           		        
		           	if(commValue==BLUETOOTH)
		           	{
		           		GlobalData.mService.mConnectedThread.setCommandId(GlobalData.GSFV);
		           		GlobalData.mService.write(senddata);
		            	
		           	}
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           		
     			}
     		});
     		mRightFVButton = (Button) findViewById(R.id.Rightfingerverifybutton);
     		mRightFVButton.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				fvView.setText("");				           		           	
		           	byte[] senddata=new byte[4] ;
		           	senddata[0]=0x1d;senddata[1]=0x46;
		           	senddata[2]=0x56;	senddata[3]=1;
		           		        
		           	if(commValue==BLUETOOTH)
		           	{
		           		GlobalData.mService.mConnectedThread.setCommandId(GlobalData.GSFV);
		           		GlobalData.mService.write(senddata);
		           	}
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           		
     			}
     		});
     		fvView=(TextView)findViewById(R.id.fingerouttextview);
     		/****
     		mGetDesButton = (Button) findViewById(R.id.getdesbutton);
     		mGetDesButton.setOnClickListener(new View.OnClickListener() {
     			@Override
     			public void onClick(View v) {
     				fvView.setText("");				           		           	
		           	byte[] senddata=new byte[3] ;
		           	senddata[0]=0x1d;senddata[1]=0x46;
		           	senddata[2]=0x44;
		           		        
		           	if(commValue==BLUETOOTH)
		           	{
		           		GlobalData.mService.mConnectedThread.setCommandId(GlobalData.GSFV);
		           		GlobalData.mService.write(senddata);
		           	}
		            else
		            	GlobalData.mTTransmission.sendBytes(senddata);
		           		           		
     			}
     		});
     		****/
    }
	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 * 
	 */
	private void sendMessage(String message) 
	{
		// Check that we're actually connected before trying anything
		if (GlobalData.mService.getState() != GlobalData.STATE_CONNECTED) 
		{
			Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		
		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothService to write
			byte [] send ;
			try {
				send = message.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				send = message.getBytes();

			}
				
			GlobalData.mService.write(send);
				
		}
	}
	

	@Override
	public void onBackPressed() {
        super.onBackPressed();
        if(commValue==BLUETOOTH)
        	GlobalData.mService.stop();
        else
        	GlobalData.mTTransmission.close();
        finish();
       
    }
	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case GlobalData.MESSAGE_STATE_CHANGE:
				if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) 
				{
				case GlobalData.STATE_CONNECTED:
	               
	                break;
				case GlobalData.STATE_CONNECTING:
	               
	                break;
				case GlobalData.STATE_LISTEN:
				case GlobalData.STATE_NONE:
	                
	                break;
	            }
	            break;
	        case GlobalData.MESSAGE_WRITE:
	           
	             break;
	        case GlobalData.MESSAGE_READ:
	        	
	            byte[] readBuf = (byte[]) msg.obj;
	            int commandId=readBuf[0];
	            switch(commandId)
	            {
	            case GlobalData.GSFV:
	            	int tmpreturncode=(readBuf[1]&0x00ff)+(readBuf[2]&0x00ff)*256;
		            short returncode=(short)(tmpreturncode&0xffff);
		            
		            switch(returncode)
		            {
		            	case RS232ERR_READ_TIMEOUT:
		            		Log.e(TAG, "RS232ERR_READ_TIMEOUT");
		            		fvView.setText("RS232ERR_READ_TIMEOUT");
		            		break;
		            	case SPRS232_ERR_ERROR:
		            		Log.e(TAG, "SPRS232_ERR_ERROR");
		            		fvView.setText("SPRS232_ERR_ERROR");
		            		break;
		            	case ILV_ERROR_NOT_ENOUGH_MEMORY:
		            		Log.e(TAG, "ILV_ERROR_NOT_ENOUGH_MEMORY");
		            		fvView.setText("ILV_ERROR_NOT_ENOUGH_MEMORY");
		            		break;
		            	case ILVERR_STATUS:
		            		Log.e(TAG, "ILVERR_STATUS");
		            		fvView.setText("ILVERR_STATUS");
		            		break;
		            	case MESSAGE_PLACE_FINGER:
		            		Log.e(TAG, "MESSAGE_PLACE_FINGER");
		            		fvView.setText("MESSAGE_PLACE_FINGER");
		            		break;
		            	case MESSAGE_MOVE_UP:
		            		Log.e(TAG, "MESSAGE_MOVE_UP");
		            		fvView.setText("MESSAGE_MOVE_UP");
		            		break;
		            	case  MESSAGE_MOVE_DOWN:
		            		Log.e(TAG, "MESSAGE_MOVE_DOWN");
		            		fvView.setText("MESSAGE_MOVE_DOWN");
		            		break;
		            	case  MESSAGE_MOVE_RIGHT:
		            		Log.e(TAG, "MESSAGE_MOVE_RIGHT");
		            		fvView.setText("MESSAGE_MOVE_RIGHT");
		            		break;
		            	case  MESSAGE_MOVE_LEFT:
		            		Log.e(TAG, "MESSAGE_MOVE_LEFT");
		            		fvView.setText("MESSAGE_MOVE_LEFT");
		            		break;
		            	case  MESSAGE_PRESS_HARDER:
		            		Log.e(TAG, "MESSAGE_PRESS_HARDER");
		            		fvView.setText("MESSAGE_PRESS_HARDER");
		            		break;
		            	case  MESSAGE_MOVE_FINGER:
		            		Log.e(TAG, "MESSAGE_MOVE_FINGER");
		            		fvView.setText("MESSAGE_MOVE_FINGER");
		            		break;
		            	case  MESSAGE_REMOVE_FINGER:
		            		Log.e(TAG, "MESSAGE_REMOVE_FINGER");
		            		fvView.setText("MESSAGE_REMOVE_FINGER");
		            		break;
		            	case  MESSAGE_UNKNOWN_MESSAGE:
		            		Log.e(TAG, "MESSAGE_UNKNOWN_MESSAGE");
		            		fvView.setText("MESSAGE_UNKNOWN_MESSAGE");
		            		break;
		            	case  MESSAGE_VERIFY_HIT:
		            		Log.e(TAG, "MESSAGE_VERIFY_HIT");
		            		fvView.setText("MESSAGE_VERIFY_HIT");
		            		break;
		            	case  MESSAGE_VERIFY_NOHIT:
		            		Log.e(TAG, "MESSAGE_VERIFY_NOHIT");
		            		fvView.setText("MESSAGE_VERIFY_NOHIT");
		            		break;
		            	case  MESSAGE_VERIFY_UNKNOWN_MATCH_RESULT:
		            		Log.e(TAG, "MESSAGE_VERIFY_UNKNOWN_MATCH_RESULT");
		            		fvView.setText("MESSAGE_VERIFY_UNKNOWN_MATCH_RESULT");
		            		break;
		            	case SC_VCCERR:
		            		
		                case SC_SLOTERR	:
		                	
		                case SC_PARERR	:
		                	
		                case SC_PARAERR	:
		                	
		                case SC_PROTOCALERR:
		                	
		                case SC_DATALENERR:
		                	
		                case SC_CARDOUT:
		                	
		                case SC_NORESET:
		                
		                case SC_TIMEOUT:
		                	
		                case SC_PPSERR:
		                	
		                case SC_ATRERR	:
		                	
		                case SC_APDUERR:
		                	
		                case SC_T0_TIMEOUT	:
		                	
		                case SC_T0_MORESENDERR	:
		                	
		                case SC_T0_MORERECEERR	:
		                	
		                case SC_T0_PARERR	:
		                	
		                case SC_T0_INVALIDSW	:
		                	
		                case SC_ATR_TSERR	:
		                	
		                case SC_ATR_TCKERR	:
		                	
		                case SC_ATR_TIMEOUT:
		                	
		                case SC_ATR_TA1ERR	:
		                	
		                case SC_ATR_TA2ERR	:
		                	
		                case SC_ATR_TA3ERR:
		                	
		                case SC_ATR_TB1ERR	:
		                	
		                case SC_ATR_TB2ERR	:
		                	
		                case SC_ATR_TB3ERR	:
		                	
		                case SC_ATR_TC1ERR:
		                case SC_ATR_TC2ERR	:
		                case SC_ATR_TC3ERR	:
		                case SC_ATR_TD1ERR	:
		                case SC_ATR_TD2ERR	:
		                case SC_ATR_LENGTHERR	:
		                case SC_T1_BWTERR	:
		                case SC_T1_CWTERR	:
		                case SC_T1_ABORTERR	:
		                case SC_T1_EDCERR	:
		                case SC_T1_SYNCHERR	:
		                case SC_T1_EGTERR	:
		                case SC_T1_BGTERR	:
		                case SC_T1_NADERR	:
		                case SC_T1_PCBERR	:
		                case SC_T1_LENGTHERR	:
		                case SC_T1_IFSCERR	:
		                case SC_T1_IFSDERR	:
		                case SC_T1_MOREERR	:
		                case SC_T1_PARITYERR	:
		                case SC_T1_INVALIDBLOCK	:
		                	Log.e(TAG, "ICC CARD ERROR");
		            		fvView.setText("ICC CARD ERROR");
		            		break;
		            }
	            	break;
	            case GlobalData.GSSO:
	            case GlobalData.GSSC:
	            case GlobalData.GSSR:
	            case GlobalData.GSST:
	            	break;
	            }
	            
	            
	            break;
	        case MSG_DATA:
	    		byte[] data=msg.getData().getByteArray(KEY_BUFFER);
	    		int length=msg.getData().getInt(KEY_LENGTH);
	            String message = new String(data, 0, length);
	            fvView.setText(message);
	            break;
	        case GlobalData.MESSAGE_DEVICE_NAME:
	            
	            break;
	        case GlobalData.MESSAGE_TOAST:
	           
	            break;
	       }
	   }
	};


	
	    @Override
		public void onDestroy() 
	    {
	        super.onDestroy();
	      	 // Stop the Bluetooth chat services
	        if(commValue==BLUETOOTH)
	        {
	        	if (GlobalData.mService != null) 
	        	{
	        		GlobalData.mService.stop();
	        		
	        	}
	        }
	        else
	        {
	        	if (GlobalData.mTTransmission != null) 
	        	{
	        		GlobalData.mTTransmission.close();
	        		
	        	}
	        	
	        }
        if(D) Log.e(TAG, "--- ON DESTROY ---");
	    }
	    /****
	    @Override  
		public boolean onKeyDown(int keyCode, KeyEvent event) {  
		    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){  
		                  
		    if((System.currentTimeMillis()-exitTime) > 2000){  
		        Toast.makeText(getApplicationContext(), "exit", Toast.LENGTH_SHORT).show();                                
		        exitTime = System.currentTimeMillis();  
		    }  
		    else{  
		    	ExitAQuitApplication.finishAll();
		        }  
		                  
		    return true;  
		    }  
		    return super.onKeyDown(keyCode, event);  
		}  
		***/
}
