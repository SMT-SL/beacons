package com.smt.beacons.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.smt.beacons.R;

import android.support.v7.app.ActionBarActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity_NotUsed extends ActionBarActivity {

//	private BluetoothAdapter mBluetoothAdapter;
//
//	private boolean mScanning;
//	private Handler mHandler;
//	// Stops scanning after 10 seconds.
//	private static final long SCAN_PERIOD = 4000;
//	private static final long DELAY_BETWEEN_SCANS = 10000;
//
//	private static final int REQUEST_ENABLE_BT = 123456789;
//
//	private ListView listBeacons;
//
//	private LeDeviceListAdapter mLeDeviceListAdapter;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//		mLeDeviceListAdapter = new LeDeviceListAdapter();
//		mHandler = new Handler();
//
//		listBeacons = (ListView) findViewById(R.id.listView1);
//		listBeacons.setAdapter(mLeDeviceListAdapter);
//
//		// Initializes Bluetooth adapter.
//		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(MainActivity_NotUsed.BLUETOOTH_SERVICE);
//		mBluetoothAdapter = bluetoothManager.getAdapter();
//
//		// Ensures Bluetooth is available on the device and it is enabled. If
//		// not,
//		// displays a dialog requesting user permission to enable Bluetooth.
//		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//			Intent enableBtIntent = new Intent(
//					BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//		}
//
//		scanLeDevice(true);
//		
//		new java.util.Timer().schedule( 
//				new java.util.TimerTask() {
//		            @Override
//		            public void run() {
//		            	scanLeDevice(true);
//		            }
//		        },0,DELAY_BETWEEN_SCANS 
//		);
//
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	private void scanLeDevice(final boolean enable) {
//		if (enable) {
//
//			// Stops scanning after a pre-defined scan period.
//			mHandler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					mScanning = false;
//					mBluetoothAdapter.stopLeScan(mLeScanCallback);
//					Log.e("Stopping ", "ms" + System.currentTimeMillis());
//				}
//			}, SCAN_PERIOD);
//			mScanning = true;
//			mBluetoothAdapter.startLeScan(mLeScanCallback);
//			Log.e("Starting ", "ms" + System.currentTimeMillis());
//
//		} else {
//			mScanning = false;
//			mBluetoothAdapter.stopLeScan(mLeScanCallback);
//		}
//	}
//
//	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//		@Override
//		public void onLeScan(final BluetoothDevice device, int rssi,
//				byte[] scanRecord) {
//			Log.e("DEVICE rssi!!", String.valueOf(rssi));
//			Log.e("DEVICE Address!!", String.valueOf(device.getAddress()));
//			Log.e("DEVICE Name!!", String.valueOf(device.getName()));
//			BluetoothGatt bg = device.connectGatt(MainActivity_NotUsed.this, true, mGattCallback);
//			List<BluetoothGattService> bgs = bg.getServices();
//			for (int i = 0; i<bgs.size(); i++ ){
//				Log.e("DEVICE UUID!!",String.valueOf(i));
//				Log.e("DEVICE UUID!!",bgs.get(i).getUuid().toString());
//			}
//			
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					mLeDeviceListAdapter.addDevice(device);
//					mLeDeviceListAdapter.notifyDataSetChanged();
//				}
//			});
//		}
//	};
//	
//
//	// Adapter for holding devices found through scanning.
//	class LeDeviceListAdapter extends BaseAdapter {
//		private ArrayList<BluetoothDevice> mLeDevices;
//		private LayoutInflater mInflator;
//
//		public LeDeviceListAdapter() {
//			super();
//			mLeDevices = new ArrayList<BluetoothDevice>();
//			mInflator = MainActivity_NotUsed.this.getLayoutInflater();
//		}
//
//		public void addDevice(BluetoothDevice device) {
//			if (!mLeDevices.contains(device)) {
//				mLeDevices.add(device);
//			}
//		}
//
//		public BluetoothDevice getDevice(int position) {
//			return mLeDevices.get(position);
//		}
//
//		public void clear() {
//			mLeDevices.clear();
//		}
//
//		@Override
//		public int getCount() {
//			return mLeDevices.size();
//		}
//
//		@Override
//		public Object getItem(int i) {
//			return mLeDevices.get(i);
//		}
//
//		@Override
//		public long getItemId(int i) {
//			return i;
//		}
//
//		@Override
//		public View getView(int i, View view, ViewGroup viewGroup) {
//			ViewHolder viewHolder;
//			// General ListView optimization code.
//			if (view == null) {
//				view = mInflator.inflate(R.layout.listitem_device, viewGroup,
//						false);
//				viewHolder = new ViewHolder();
//				viewHolder.deviceAddress = (TextView) view
//						.findViewById(R.id.device_address);
////				viewHolder.deviceName = (TextView) view
////						.findViewById(R.id.device_name);
//				view.setTag(viewHolder);
//			} else {
//				viewHolder = (ViewHolder) view.getTag();
//			}
//
//			BluetoothDevice device = mLeDevices.get(i);
//			final String deviceName = device.getName();
//			if (deviceName != null && deviceName.length() > 0)
//				viewHolder.deviceName.setText(deviceName);
//			else
//				viewHolder.deviceName.setText("unknown device");
//			viewHolder.deviceAddress.setText(device.getAddress());
//
//			return view;
//		}
//	}
//
//	class ViewHolder {
//
//		public TextView deviceAddress;
//		public TextView deviceName;
//
//		ViewHolder() {
//		}
//	}
//	
//	// Various callback methods defined by the BLE API.
//    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
//    	
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status,
//                int newState) {
//            String intentAction;
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
////                intentAction = ACTION_GATT_CONNECTED;
////                mConnectionState = STATE_CONNECTED;
////                broadcastUpdate(intentAction);
//                Log.i("mGattCallBack", "Connected to GATT server.");
////                Log.i("mGattCallBack", "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
//
//            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
////                intentAction = ACTION_GATT_DISCONNECTED;
////                mConnectionState = STATE_DISCONNECTED;
//                Log.i("mGattCallBack", "Disconnected from GATT server.");
////                broadcastUpdate(intentAction);
//            }
//        }
//
//        @Override
//        // New services discovered
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
////                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
//            } else {
//                Log.w("mGattCallBack", "onServicesDiscovered received: " + status);
//            }
//        }
//
//        @Override
//        // Result of a characteristic read operation
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//            	Log.w("mGattCallBack", "UUID-->"+characteristic.getUuid().toString());
////                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//            }
//        }
//    };



}
