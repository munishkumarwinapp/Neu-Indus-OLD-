package com.winapp.printer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.Settings;
//import com.zebra.android.comm.ZebraPrinterConnectionException;
//import com.zebra.android.discovery.BluetoothDiscoverer;
//import com.zebra.android.discovery.DiscoveredPrinter;
//import com.zebra.android.discovery.DiscoveredPrinterBluetooth;
//import com.zebra.android.discovery.DiscoveryHandler;

public class PrinterFinder extends Activity {
	ListView mlistview;
    ImageView back;
	private UIHelper helper;
//	private static DiscoveryHandler discoveryHandler = null;
//	private static final int ENABLE_BLUETOOTH = 2001;
	private ArrayList<HashMap<String, String>> discoveredPrinters = new ArrayList<HashMap<String, String>>();
	BluetoothAdapter bluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 2001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_printer);
		mlistview = (ListView) findViewById(R.id.listView1);
		back = (ImageView) findViewById(R.id.back);
		mlistview.setEmptyView(findViewById(android.R.id.empty));
		
		helper = new UIHelper(PrinterFinder.this);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		bluetoothAdapter.startDiscovery();

		discoveredPrinters.clear();

		CheckBlueToothState();

		registerReceiver(ActionFoundReceiver,
				new IntentFilter(BluetoothDevice.ACTION_FOUND));

		mlistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent data = new Intent(PrinterFinder.this,Settings.class);
				data.putExtra("MACAddress", discoveredPrinters.get(position)
						.get("MACAddress"));
				data.putExtra("FriendlyName", discoveredPrinters.get(position)
						.get("FriendlyName"));
				data.putExtra("FromPrinter", "Macaddress");
				startActivity(data);
				PrinterFinder.this.finish();
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PrinterFinder.this,Settings.class);
				startActivity(i);
				PrinterFinder.this.finish();
			}
		});

		/*discoveryHandler = new DiscoveryHandler() {
			@Override
			public void discoveryError(String message) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
								.getDefaultAdapter();
						helper.dismissProgressDialog();
						if (mBluetoothAdapter == null) {
							Toast.makeText(PrinterFinder.this,
									R.string.no_bluetooth_support,
									Toast.LENGTH_LONG).show();
							finish();
						} else if (!mBluetoothAdapter.isEnabled()) {
							Intent enableBluetoothIntent = new Intent(
									BluetoothAdapter.ACTION_REQUEST_ENABLE);
							startActivityForResult(enableBluetoothIntent,
									ENABLE_BLUETOOTH);
						}
					}
				});
			}

			@Override
			public void discoveryFinished() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						helper.dismissProgressDialog();
					}
				});
			}

			@Override
			public void foundPrinter(final DiscoveredPrinter printer) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DiscoveredPrinterBluetooth discoveredBluetooth = (DiscoveredPrinterBluetooth) printer;
						BluetoothAdapter bluetoothAdapter = BluetoothAdapter
								.getDefaultAdapter();
						BluetoothDevice bluetoothDevice = bluetoothAdapter
								.getRemoteDevice(discoveredBluetooth.address);
						// Device Major Class - Imaging - 1536 && Device Minor
						// Class - Printer - 1664
						if (bluetoothDevice.getBluetoothClass()
								.getMajorDeviceClass() == 1536
								&& bluetoothDevice.getBluetoothClass()
										.getDeviceClass() == 1664) {
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("MACAddress", bluetoothDevice.getAddress());
							map.put("FriendlyName", bluetoothDevice.getName());
							discoveredPrinters.add(map);
						}
						SimpleAdapter adapter = new SimpleAdapter(
								PrinterFinder.this,
								discoveredPrinters, R.layout.printer_list_item,
								new String[] { "MACAddress", "FriendlyName" },
								new int[] { R.id.printerAddress,
										R.id.printerName });
						mlistview.setAdapter(adapter);
					}
				});
			}
		};
		try {
			helper.showProgressDialog(R.string.searching_printers);
			BluetoothDiscoverer.findPrinters(PrinterFinder.this,
					discoveryHandler);
		} catch (ZebraPrinterConnectionException e) {
			e.printStackTrace();
			finish();
		} catch (InterruptedException e) {
			e.printStackTrace();
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ENABLE_BLUETOOTH:
			if (resultCode == RESULT_OK) {
				try {
					helper.showProgressDialog(R.string.searching_printers);
					BluetoothDiscoverer.findPrinters(
							PrinterFinder.this, discoveryHandler);
				} catch (ZebraPrinterConnectionException e) {
					e.printStackTrace();
					finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
					finish();
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(PrinterFinder.this,
						R.string.bluetooth_disabled, Toast.LENGTH_LONG).show();
				finish();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}*/

	}

	private void CheckBlueToothState(){


		if (bluetoothAdapter == null){
//			stateBluetooth.setText("Bluetooth NOT support");
			Toast.makeText(PrinterFinder.this,
					R.string.no_bluetooth_support,
					Toast.LENGTH_LONG).show();
		}else{
			if (bluetoothAdapter.isEnabled()){
//				helper.showProgressDialog(R.string.searching_printers);

				if(bluetoothAdapter.isDiscovering()){
//					stateBluetooth.setText("Bluetooth is currently in device discovery process.");
//					Toast.makeText(PrinterFinder.this,
//							"Bluetooth is currently in device discovery process.",
//							Toast.LENGTH_LONG).show();
				}else{
//					stateBluetooth.setText("Bluetooth is Enabled.");
					Toast.makeText(PrinterFinder.this,
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == REQUEST_ENABLE_BT){
			CheckBlueToothState();
		}
	}

	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
				Log.d("Bluetooth found","Bluetooth found");
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//				btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//				btArrayAdapter.notifyDataSetChanged();
				if (device.getBluetoothClass()
						.getMajorDeviceClass() == 1536
						&& device.getBluetoothClass()
						.getDeviceClass() == 1664) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("MACAddress", device.getAddress());
					map.put("FriendlyName", device.getName());
					discoveredPrinters.add(map);
				}
					SimpleAdapter adapter = new SimpleAdapter(
							PrinterFinder.this,
							discoveredPrinters, R.layout.printer_list_item,
							new String[]{"MACAddress", "FriendlyName"},
							new int[]{R.id.printerAddress,
									R.id.printerName});

					mlistview.setAdapter(adapter);
//				helper.dismissProgressDialog();
			}else{
//				helper.dismissProgressDialog();
			}
		}};


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(ActionFoundReceiver);
	}


	@Override
	public void onBackPressed() {
		Intent i = new Intent(PrinterFinder.this, Settings.class);
		startActivity(i);
		PrinterFinder.this.finish();
		
	}
}
