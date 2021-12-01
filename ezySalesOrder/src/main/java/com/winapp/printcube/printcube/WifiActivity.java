package com.winapp.printcube.printcube;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import com.winapp.SFA.R;
import com.winapp.printcube.model.Module;
import com.winapp.printcube.net.UdpBroadcast;
import com.winapp.printcube.utils.Constants;
import com.winapp.printcube.utils.Utils;
import com.winapp.printcube.model.NetworkProtocol;
import com.winapp.printcube.model.TransparentTransmission;
import com.winapp.printcube.model.TransparentTransmissionListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class WifiActivity extends Activity {

	private static final String TAG = Constants.TAG + "WifiActivity";
	
	private ImageView mRefreshImageView;
	private ListView mIPsListView;
	
	private List<Module> mModules;
	private ListAdapter mAdapter;
	
	private long lastTime;
	private UdpBroadcast udpBroadcast;
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_wifi);
		Log.d(TAG, "onCreate");
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mAdapter = new ArrayAdapter<Module>(WifiActivity.this, R.layout.simple_list_item, mModules);
				mIPsListView.setAdapter(mAdapter);
				getParent().setProgressBarIndeterminateVisibility(false);
				
				//make toast
				int count = mAdapter.getCount();
				String text;
				if (count == 0) {
					text = getString(R.string.device_not_found);
				}else {
					text = String.format(getString(R.string.device_found), count, count==1?"":"s");
				}
				Utils.toast(getApplicationContext(), text);
			}
		};
		udpBroadcast = new UdpBroadcast() {
			
			@Override
			public void onReceived(List<DatagramPacket> packets) {
				Log.d(TAG, "UdpBroadcast onReceived");
				mModules = decodePackets(packets);
				saveDevices(mModules);
				//send message to display
				mHandler.sendEmptyMessage(0);
			}
		};
		
		setupViews();
	}
	
	@Override
	protected void onStart() {
		
		Log.d(TAG, "onStart");
		super.onStart();
		udpBroadcast.open();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
		udpBroadcast.close();
	}
	
	private void setupViews() {
		mRefreshImageView = (ImageView)findViewById(R.id.imageView1);
		mIPsListView = (ListView)findViewById(R.id.listView1);
		
		mRefreshImageView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mRefreshImageView.setImageResource(R.mipmap.refresh_2);
				}else if (event.getAction() == MotionEvent.ACTION_UP) {
					mRefreshImageView.setImageResource(R.mipmap.refresh_1);
					
					if (System.currentTimeMillis() - lastTime > 5000) {
						
						getParent().setProgressBarIndeterminateVisibility(true);
						String message=Utils.getCMDScanModules(WifiActivity.this);
						udpBroadcast.send(message);
						Log.d(TAG, "udpBroadcast send:"+message);
						lastTime = System.currentTimeMillis();
					}
				}
				return true;
			}
		});
		mIPsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				udpBroadcast.close();
				
				String ip = ((Module)mAdapter.getItem(position)).getIp();
				Log.d(TAG, "ip:" + ip);
				NetworkProtocol protocol=new NetworkProtocol("TCP", "Server", ip, 8899);
				GlobalData.mTTransmission = new TransparentTransmission();
				GlobalData.mTTransmission.setListener(new TransparentTransmissionListener() 
				{
					
					@Override
					public void onReceive(byte[] data, int length) 
					{
						//echo(Utils.gernerateEchoText(Utils.RESPONSE_TTS, new String(data, 0, length)));
					}
					
					@Override
					public void onOpen(boolean success) 
					{
						Log.d(TAG, "mTTransmission onopen");
						if (success) 
						{
							Log.d(TAG, "mTTransmission onopen success");
							Intent intent = new Intent(WifiActivity.this, PrintActivity.class);
							intent.putExtra("COMM", 1);//0-WIFI
							startActivity(intent);
						}
						else 
						{
							Log.d(TAG, "mTTransmission onopen failed");
						}
						
					}
				});
				GlobalData.mTTransmission.setProtocol(protocol);
				if (GlobalData.mTTransmission.init()) 
				{
					Log.d(TAG, "mTTransmission open");
					GlobalData.mTTransmission.open();
					
				}
			}
		});
		
		mModules = loadDevices();
		mAdapter = new ArrayAdapter<Module>(WifiActivity.this, R.layout.simple_list_item, mModules);
		mIPsListView.setAdapter(mAdapter);
	}
	
	
	
	
	
	/**
	 * decode pagkets to mudoles
	 * @param packets
	 * @return
	 */
	private List<Module> decodePackets(List<DatagramPacket> packets) {
		
		int i = 1;
		Module module;
		List<String> list = new ArrayList<String>();
		List<Module> modules = new ArrayList<Module>();
		
		DECODE_PACKETS:
		for (DatagramPacket packet : packets) {
			
			String data = new String(packet.getData(), 0, packet.getLength());
			Log.d(TAG, i + ": " + data);
			if (data.equals(Utils.getCMDScanModules(this))) {
				continue;
			}
			
			for (String item : list) {
				if (item.equals(data)) {
					continue DECODE_PACKETS;
				}
			}
			
			list.add(data);
			if ((module = Utils.decodeBroadcast2Module(data)) != null) {
				module.setId(i);
				modules.add(module);
				i++;
			}
		}
		
		return modules;
	}
	
	/**
	 * save modules' data to local
	 * @param modules
	 */
	private void saveDevices(List<Module> modules) {
		
		SharedPreferences preferences = getSharedPreferences("module_list", MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		if (modules.size() > 0) {
			int i = 0;
			for (Module module : modules) {
				editor.putInt(Constants.KEY_PRE_ID + i, module.getId());
				editor.putString(Constants.KEY_PRE_IP + i, module.getIp());
				editor.putString(Constants.KEY_PRE_MAC + i, module.getMac());
				editor.putString(Constants.KEY_PRE_MODULEID + i, module.getModuleID());
				i++;
			}
			
			editor.putInt(Constants.KEY_MODULE_COUNT, modules.size());
			editor.commit();
		}else {
			editor.clear().commit();
		}
	}
	
	/**
	 * Load modules' data from local
	 * @return
	 */
	private List<Module> loadDevices() {
		
		List<Module> modules = new ArrayList<Module>();
		SharedPreferences preferences = getSharedPreferences("module_list", MODE_PRIVATE);
		int count = preferences.getInt(Constants.KEY_MODULE_COUNT, 0);
		Module module;
		
		for (int i = 0; i < count; i++) {
			module = new Module();
			module.setId(preferences.getInt(Constants.KEY_PRE_ID + i, -1));
			module.setIp(preferences.getString(Constants.KEY_PRE_IP + i, null));
			module.setMac(preferences.getString(Constants.KEY_PRE_MAC + i, null));
			module.setModuleID(preferences.getString(Constants.KEY_PRE_MODULEID + i, null));
			modules.add(module);
		}
		
		return modules;
	}
}

