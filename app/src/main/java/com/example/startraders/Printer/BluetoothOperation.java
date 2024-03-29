package com.example.startraders.Printer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.android.print.sdk.PrinterConstants.Connect;
import com.android.print.sdk.PrinterInstance;

import java.lang.reflect.Method;

public class BluetoothOperation implements IPrinterOpertion {
	private static final String TAG = "BluetoothOpertion";
	private BluetoothAdapter adapter;
	private Context mContext;
	private boolean hasRegBoundReceiver;
	private boolean rePair;

	private BluetoothDevice mDevice;
	private String deviceAddress;
	private Handler mHandler;
	private PrinterInstance mPrinter;
	private boolean hasRegDisconnectReceiver;
	private IntentFilter filter;


    public BluetoothOperation(Context context, Handler handler) {
		adapter = BluetoothAdapter.getDefaultAdapter();
		mContext = context;
		mHandler = handler;
		hasRegDisconnectReceiver = false;
		Log.d(TAG, "BluetoothOperation: ");
		filter = new IntentFilter();
		//filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		//filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
	}

	public void open(Intent data) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		deviceAddress = data.getExtras().getString(
				BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
		mDevice = adapter.getRemoteDevice(deviceAddress);
		Log.d(TAG, "open: asdasdsad");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
		if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "open: no per 1111");
			return;
		}
		if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
			Log.i(TAG, "device.getBondState() is BluetoothDevice.BOND_NONE");
			PairOrRePairDevice(false, mDevice);
		} else if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
			rePair = data.getExtras().getBoolean(
					BluetoothDeviceList.EXTRA_RE_PAIR);
			if (rePair) {
				PairOrRePairDevice(true, mDevice);
			} else {
				openPrinter();
			}
		}
	}

	// use device to init printer.
	private void openPrinter() {
		mPrinter = new PrinterInstance(mContext, mDevice, mHandler);
		// default is gbk...
		// mPrinter.setEncoding("gbk");
		mPrinter.openConnection();
	}

	private boolean PairOrRePairDevice(boolean re_pair, BluetoothDevice device) {
		boolean success = false;
		try {
			if (!hasRegBoundReceiver) {
				mDevice = device;
				IntentFilter boundFilter = new IntentFilter(
						BluetoothDevice.ACTION_BOND_STATE_CHANGED);
				mContext.registerReceiver(boundDeviceReceiver, boundFilter);
				hasRegBoundReceiver = true;
			}

			if (re_pair) {
				// cancel bond
				Method removeBondMethod = BluetoothDevice.class
						.getMethod("removeBond");
				success = (Boolean) removeBondMethod.invoke(device);
				Log.i(TAG, "removeBond is success? : " + success);
			} else {
				// Input password
				// Method setPinMethod =
				// BluetoothDevice.class.getMethod("setPin");
				// setPinMethod.invoke(device, 1234);
				// create bond
				Method createBondMethod = BluetoothDevice.class
						.getMethod("createBond");
				success = (Boolean) createBondMethod.invoke(device);
				Log.i(TAG, "createBond is success? : " + success);
			}
		} catch (Exception e) {
			Log.i(TAG, "removeBond or createBond failed.");
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	// receive bound broadcast to open connect.
	private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (!mDevice.equals(device)) {
					return;
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
					Log.d(TAG, "open: no per 2222");
					return;
				}
				switch (device.getBondState()) {
					case BluetoothDevice.BOND_BONDING:
						Log.i(TAG, "bounding......");
						break;
					case BluetoothDevice.BOND_BONDED:
						Log.i(TAG, "bound success");
						// if bound success, auto init BluetoothPrinter. open
						// connect.
						if (hasRegBoundReceiver) {
							mContext.unregisterReceiver(boundDeviceReceiver);
							hasRegBoundReceiver = false;
						}
						openPrinter();
						break;
					case BluetoothDevice.BOND_NONE:
						if (rePair) {
							rePair = false;
							Log.i(TAG, "removeBond success, wait create bound.");
							PairOrRePairDevice(false, device);
						} else if (hasRegBoundReceiver) {
							mContext.unregisterReceiver(boundDeviceReceiver);
							hasRegBoundReceiver = false;
							// bond failed
							mHandler.obtainMessage(Connect.FAILED).sendToTarget();
							Log.i(TAG, "bound cancel");
						}
					default:
						break;
				}
			}
		}
	};

	public void close() {
		if (mPrinter != null) {
			mPrinter.closeConnection();
			mPrinter = null;
		}
		if (hasRegDisconnectReceiver) {
			mContext.unregisterReceiver(myReceiver);
			hasRegDisconnectReceiver = false;
		}
	}

	public PrinterInstance getPrinter() {
		if (mPrinter != null && mPrinter.isConnected()) {
			if (!hasRegDisconnectReceiver) {
				mContext.registerReceiver(myReceiver, filter);
				hasRegDisconnectReceiver = true;
			}
		}
		return mPrinter;
	}

	// receive the state change of the bluetooth.
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			Log.i(TAG, "receiver is: " + action);
			if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				if (device != null && mPrinter != null && mPrinter.isConnected() && device.equals(mDevice)) {
					close();
				}
			}
		}
	};

	@Override
	public void chooseDevice() {
		Log.d(TAG, "chooseDevice "+adapter.isEnabled());
		if (!adapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "open: no per 3333");
				return;
			}
			((Activity) mContext).startActivityForResult(enableIntent,
					MainActivity.ENABLE_BT);
		} else {
			Intent intent = new Intent(mContext, BluetoothDeviceList.class);
			((Activity) mContext).startActivityForResult(intent,
					MainActivity.CONNECT_DEVICE);
		}
	}
}
