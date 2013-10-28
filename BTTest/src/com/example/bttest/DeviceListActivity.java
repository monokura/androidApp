package com.example.bttest;

import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;

public class DeviceListActivity extends ListActivity implements OnClickListener {

    BluetoothAdapter btAdapter;
    BTDevAdapter btDevAdapter;
    DevDiscoveryHandler devDiscoveryHandler;

    private class BTDevAdapter extends ArrayAdapter<BluetoothDevice> {
        private Context context;
        private int textViewResId;
        private LayoutInflater layoutInflater;

        public BTDevAdapter(Context context, int textViewResId) {
            super(context, textViewResId);
            this.context = context;
            this.textViewResId = textViewResId;
            this.layoutInflater = (LayoutInflater) this.context
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int pos, View convView, ViewGroup parent) {
            View resView = convView;
            if (resView == null)
                resView = layoutInflater.inflate(textViewResId, null);
            BluetoothDevice dev = getItem(pos);
            String name = dev.getName();
            String addr = dev.getAddress();
            ((TextView) resView.findViewById(R.id.device_name)).setText(name);
            ((TextView) resView.findViewById(R.id.mac_address)).setText(addr);
            return resView;
        }
    }

    private final class DevDiscoveryHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice dev = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (dev.getBondState() != BluetoothDevice.BOND_BONDED
                        && dev.getName() != null && !alreadyFound(dev))
                    btDevAdapter.add(dev);
            }
            else if (action.equals(BluetoothDevice.ACTION_NAME_CHANGED)) {
                BluetoothDevice dev = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (dev.getBondState() != BluetoothDevice.BOND_BONDED
                        && !alreadyFound(dev))
                    btDevAdapter.add(dev);
            }
            else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                setProgressBarIndeterminateVisibility(true);
            }
            else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                setProgressBarIndeterminateVisibility(false);
            }
        }

        void registerFor(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(this, filter);
        }

        boolean alreadyFound(BluetoothDevice dev) {
            String addr = dev.getAddress();
            int n = btDevAdapter.getCount();
            for (int i = 0; i < n; i++)
                if (btDevAdapter.getItem(i).getAddress().equals(addr))
                    return true;
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        ((Button) findViewById(R.id.search_button)).setOnClickListener(this);
        setResult(RESULT_CANCELED);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevAdapter = new BTDevAdapter(this, R.layout.device_list_item);
        setListAdapter(btDevAdapter);
        devDiscoveryHandler = new DevDiscoveryHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(devDiscoveryHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        devDiscoveryHandler.registerFor(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        btDevAdapter.clear();
        Set<BluetoothDevice> pairdDevs = btAdapter.getBondedDevices();
        for (BluetoothDevice dev : pairdDevs)
            btDevAdapter.add(dev);

    }

    @Override
    protected void onListItemClick(ListView lv, View v, int pos, long id) {
        BluetoothDevice dev = btDevAdapter.getItem(pos);
        Intent result = new Intent();
        result.putExtra(BluetoothDevice.EXTRA_DEVICE, dev);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.search_button:
            btDevAdapter.clear();
            Set<BluetoothDevice> pairdDevs = btAdapter.getBondedDevices();
            for (BluetoothDevice dev : pairdDevs)
                btDevAdapter.add(dev);
            if (btAdapter.isDiscovering())
                btAdapter.cancelDiscovery();
            btAdapter.startDiscovery();
            break;
        }
    }
}
