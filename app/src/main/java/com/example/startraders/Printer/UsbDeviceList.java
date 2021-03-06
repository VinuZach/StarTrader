package com.example.startraders.Printer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.print.sdk.usb.USBPort;
import com.example.startraders.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 * @param <V>
 * @param
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class UsbDeviceList<V> extends Activity {
    private ArrayAdapter<String> deviceArrayAdapter;
    private ListView mFoundDevicesListView;
    private Button scanButton;
    private List<UsbDevice> deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        setTitle(R.string.select_device);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	doDiscovery();
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        deviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_item);
        // mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        mFoundDevicesListView = (ListView) findViewById(R.id.paired_devices);
        mFoundDevicesListView.setAdapter(deviceArrayAdapter);
        mFoundDevicesListView.setOnItemClickListener(mDeviceClickListener);
        doDiscovery();
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
    	deviceArrayAdapter.clear();
    	UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
    	HashMap<String, UsbDevice> devices = manager.getDeviceList();
    	deviceList = new ArrayList<UsbDevice>();
    	for (UsbDevice device : devices.values()) {
    		if (USBPort.isUsbPrinter(device)) {
    			deviceArrayAdapter.add(device.getDeviceName() + "\nvid: " + device.getVendorId() + " pid: " + device.getProductId());
    			deviceList.add(device);
			}
		}
    }

    private void returnToPreviousActivity(UsbDevice device)
    {
        // Create the result Intent and include the MAC address
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(UsbManager.EXTRA_DEVICE, device);
        // Set result and finish this Activity
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long id) {
        	returnToPreviousActivity(deviceList.get(position));
        }
    };
}
