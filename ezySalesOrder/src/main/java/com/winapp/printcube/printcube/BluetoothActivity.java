package com.winapp.printcube.printcube;

import java.util.Set;
import com.winapp.SFA.R;
import com.winapp.printcube.utils.Constants;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BluetoothActivity extends Activity {
	
	private static final String TAG = /*Constants.TAG +*/ "BluetoothActivity";
    private static final boolean D = true;
    public static final String DEVICE_NAME = "device_name";

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String TOAST = "toast";
    
    //private TextView mTitle;
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private static final int REQUEST_ENABLE_BT = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        // Setup the window
              
        setContentView(R.layout.activity_bluetooth);
        if(D) Log.e(TAG, "---onCreate ---");
        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);
        
        GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (GlobalData.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        GlobalData.mService = new BluetoothService(this, mHandler);
        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
       
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
                doDiscovery();
                v.setVisibility(View.GONE);
                findViewById(R.id.new_devices).setVisibility(View.VISIBLE);
            }
        });
       
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        
        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
       
        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
       
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
       
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }
    @Override
    public void onResume()
    {
    	super.onResume();
    	if(D) Log.e(TAG, "--- onResume ---");
    	
    }
    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "--- onStart ---");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!GlobalData.mBluetoothAdapter.isEnabled()) {
        	GlobalData.mBluetoothAdapter.enable();
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        }else {
            if (GlobalData.mService == null)
            {
            	GlobalData.mService = new BluetoothService(this, mHandler);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
        
        if (GlobalData.mService != null) GlobalData.mService.stop();
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        getParent().setProgressBarIndeterminateVisibility(true);
        getParent().setTitle(R.string.scanning);
        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String noDevices = getResources().getText(R.string.none_paired).toString();
            String noNewDevice = getResources().getText(R.string.none_found).toString();
            Log.i(TAG, info);
            
            if (! info.equals(noDevices) && ! info.equals(noNewDevice)) {
				String address = info.substring(info.length() - 17);
				// Create the result Intent and include the MAC address
				
				
				if (BluetoothAdapter.checkBluetoothAddress(address)) 
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(address);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);
					 
				}
			}
        }
    };

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GlobalData.MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case GlobalData.STATE_CONNECTED:
                	//mTitle.setText(R.string.title_connected_to);
					//mTitle.append(mConnectedDeviceName);
                	Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
                	intent.putExtra("COMM", 0);//0-BLUETOOTH
    				// Set result and finish this Activity
    				startActivity(intent);
    				
                    break;
                case GlobalData.STATE_CONNECTING:
                	//mTitle.setText(R.string.title_connecting);
                    break;
                case GlobalData.STATE_LISTEN:
                case GlobalData.STATE_NONE:
                	//mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case GlobalData.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case GlobalData.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	getParent().setProgressBarIndeterminateVisibility(false);
            	getParent().setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
   
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
	
		switch (requestCode) 
		{
		
		
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) 
			{
				// Bluetooth is now enabled, so set up a session
				//init();
			} 
			else 
			{
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
    
}
