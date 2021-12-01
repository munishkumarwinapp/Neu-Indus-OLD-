package com.winapp.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.Settings;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent. 
 */
public class DeviceListActivity extends Activity {
    // Return Intent extra
//    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private UIHelper helper;
    // Address preference name 
    public static String PREF_DEVICE_ADDRESS = "device_address";
    private static final int REQUEST_ENABLE_BT = 2001;
    // Class that explain bluetooth device node
    private class DeviceNode { 
        private String mName;
        private String mAddress;
        private int mIconResId;
        
        public DeviceNode(String name, String address, int iconResId) {       
            mName = name;
            mAddress = address;
            mIconResId = iconResId;
        }
        
        public String getName() {
            return mName;
        }
        
        public void setName(String name) {
            mName = name;           
        }
        
        public String getAddress() {
            return mAddress;
        } 
               
        public int getIcon() {
            return mIconResId;
        }
        
        public void setIcon(int resId) {
            mIconResId = resId;
        }        
    }
    
    // Bluetooth device adapter
    public class DeviceAdapter extends BaseAdapter {
        private List<DeviceNode> mNodeList = new ArrayList<DeviceNode>();
        
        @Override
        public int getCount() {            
            return mNodeList.size();
        }

        @Override
        public Object getItem(int location) {
            return mNodeList.get(location);
        }

        @Override
        public long getItemId(int location) {
            return location;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get layout to populate
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = LayoutInflater.from(DeviceListActivity.this);
                v = vi.inflate(R.layout.device_node, null);
            }
            
            // Populate the layout with new data
            DeviceNode node = (DeviceNode)getItem(position);
            ((ImageView)v.findViewById(R.id.icon)).setImageResource(node.getIcon());
            ((TextView)v.findViewById(R.id.name)).setText(node.getName());
            ((TextView)v.findViewById(R.id.address)).setText(node.getAddress());
            
            return v;        
        }

        public void add(String name, String address, int iconResId) {
            DeviceNode node = new DeviceNode(name, address, iconResId);
            mNodeList.add(node);
        }
        
        public void clear() {
            mNodeList.clear();
        }
        
        public DeviceNode find(String address) {
            for (DeviceNode d : mNodeList) {
                if (address.equals(d.getAddress())) return d;
            }
            
            return null;
        }
    }
        
    // The on-click listener for all devices in the ListViews
    private final OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int location, long id) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            DeviceNode node = (DeviceNode)mDevicesAdapter.getItem(location);
            String address = node.getAddress();
            Intent data = new Intent(DeviceListActivity.this,Settings.class);
            data.putExtra("MACAddress", address);
            data.putExtra("FromPrinter", "Macaddress");
            startActivity(data);
            DeviceListActivity.this.finish();
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
                boolean bonded = device.getBondState() == BluetoothDevice.BOND_BONDED;
                int iconId = bonded ? R.mipmap.ic_menu_print : R.mipmap.ic_menu_print;
                // Find is device is already exists
                DeviceNode node = mDevicesAdapter.find(device.getAddress());
                // Skip if device is already in list                  
                if (node == null) {
                    mDevicesAdapter.add(device.getName(), device.getAddress(), iconId);
                } else {
                    node.setName(device.getName());
                    node.setIcon(iconId);
                }
                mDevicesAdapter.notifyDataSetChanged();
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                helper.dismissProgressDialog();
                //setTitle(R.string.title_select_device);
               // findViewById(R.id.scanLayout).setVisibility(View.VISIBLE);
            }           
        }
    };

    // Represents the local device Bluetooth adapter.
    private BluetoothAdapter mBtAdapter;
    // Device adapter which handle list of devices.
    private DeviceAdapter mDevicesAdapter;
    private ImageView back;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Setup the window
       // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        helper = new UIHelper(DeviceListActivity.this);
        // Set result CANCELED in case the user backs out
      //  setResult(Activity.RESULT_CANCELED);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setFinishOnTouchOutside(false);
        }        
            
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isBluetoothEnabled()) {
            startDiscovery();
        }else {
            /*DeviceListActivity.this.registerReceiver(bluetoothReceiver, new IntentFilter(
                    BluetoothAdapter.ACTION_STATE_CHANGED));*/
           // enableBluetooth();
            Intent enableBluetoothIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT
                    );
        }
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices 
        mDevicesAdapter = new DeviceAdapter();
        back = (ImageView) findViewById(R.id.back);
        // Initialize the button to perform connect
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final EditText addrView = (EditText) findViewById(R.id.device_address);
        addrView.setText(prefs.getString(PREF_DEVICE_ADDRESS, ""));
//        Button connButton = (Button) findViewById(R.id.connect);

        /*connButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	String address  = addrView.getText().toString();
            	finishActivityWithResult(address);
            }
        });*/

//        mDevicesAdapter.clear();

        // Find and set up the ListView for paired devices
        ListView devicesView = (ListView) findViewById(R.id.devices_list);
        devicesView.setAdapter(mDevicesAdapter);
        devicesView.setOnItemClickListener(mDeviceClickListener);

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startDiscovery();                
            }
        });
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(DeviceListActivity.this,Settings.class);
                startActivity(i);
                DeviceListActivity.this.finish();
            }
        });
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);     

        /*if (mBtAdapter != null && mBtAdapter.isEnabled()) {
            // Get a set of currently paired devices
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
    
            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    mDevicesAdapter.add(device.getName(), device.getAddress(), R.mipmap.ic_menu_print);
                }
            }           
            findViewById(R.id.title_disabled).setVisibility(View.GONE);
        } else {
            findViewById(R.id.scanLayout).setVisibility(View.GONE);
        }*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    try {
                        startDiscovery();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(DeviceListActivity.this,
                            R.string.bluetooth_disabled, Toast.LENGTH_LONG).show();

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

       /* final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = prefs.edit();
        final EditText addrView = (EditText) findViewById(R.id.device_address);
        edit.putString(PREF_DEVICE_ADDRESS, addrView.getText().toString());
        edit.commit();*/
        
        // Make sure we're not doing discovery anymore
        cancelDiscovery();

        // Unregister broadcast listeners
        unregisterReceiver(mReceiver);
    }

    /*@Override
    public boolean onKeyUp( int keyCode, KeyEvent event) {
        if( keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_FIRST_USER);
            finish();
            return true;
        }
        return super.onKeyUp( keyCode, event );
    }*/
        
   /* private void finishActivityWithResult(String address) {
    	// Create the result Intent and include the MAC address
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        
        // Set result and finish this Activity
        setResult(RESULT_OK, intent);
      //  finish();
    }*/
   
    private void startDiscovery() {
        // Indicate scanning in the title
       // setProgressBarIndeterminateVisibility(true);
        helper.showProgressDialog(R.string.searching_printers);
      //  setTitle(R.string.title_scanning);
      //  findViewById(R.id.scanLayout).setVisibility(View.GONE);
       
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    private void cancelDiscovery() {
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
       }
    }

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        context.unregisterReceiver(bluetoothReceiver);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        startDiscovery();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };
    private boolean isBluetoothEnabled() {
        if (mBtAdapter != null) {
            return mBtAdapter.isEnabled();
        }
        return false;
    }

    private boolean enableBluetooth() {
        if (mBtAdapter == null) {
            helper.showErrorDialog(R.string.no_bluetooth_support);
            return false;
        } else if (!mBtAdapter.isEnabled()) {
            return mBtAdapter.enable();
        }
        return false;
    }
/*
    private void CheckBlueToothState(){


        if (mBtAdapter == null){
//			stateBluetooth.setText("Bluetooth NOT support");
            Toast.makeText(DeviceListActivity.this,
                    R.string.no_bluetooth_support,
                    Toast.LENGTH_LONG).show();
        }else{
            if (mBtAdapter.isEnabled()){
//				helper.showProgressDialog(R.string.searching_printers);

                if(mBtAdapter.isDiscovering()){
//					stateBluetooth.setText("Bluetooth is currently in device discovery process.");
//					Toast.makeText(PrinterFinder.this,
//							"Bluetooth is currently in device discovery process.",
//							Toast.LENGTH_LONG).show();
                }else{
//					stateBluetooth.setText("Bluetooth is Enabled.");
                    Toast.makeText(DeviceListActivity.this,
                            "Bluetooth is Enabled.",
                            Toast.LENGTH_LONG).show();
//					btnScanDevice.setEnabled(true);
                }
            }else{
//				Toast.makeText(PrinterFinder.this,
//						"Bluetooth is NOT Enabled!",
//						Toast.LENGTH_LONG).show();
//				stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }*/
    @Override
    public void onBackPressed() {
        Log.d("back","back pressed");
        Intent i = new Intent(DeviceListActivity.this, Settings.class);
        startActivity(i);
        DeviceListActivity.this.finish();
    }
}
