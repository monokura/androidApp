package com.example.bttest;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    static final int REQUEST_ENABLE_BLUETOOTH = 1;
    static final int REQUEST_SELECT_DEVICE = 2;

    public static final UUID SPP_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805f9b34fb");

    ListView nodeList = null;
    BluetoothAdapter btAdapter = null;
    BluetoothDevice selectedDev = null;
    TextView selectedDevName, selectedDevAddr;
    EditText inputText;

    private BluetoothSocket socket = null;
    boolean connected = false;

    ConnectingTask connectingTask = null;
    Handler handler;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedDevName = (TextView) findViewById(R.id.selected_dev_name);
        selectedDevAddr = (TextView) findViewById(R.id.selected_dev_addr);
        inputText = (EditText) findViewById(R.id.input_text);

        ((Button) this.findViewById(R.id.select_button))
                .setOnClickListener(this);
        ((Button) this.findViewById(R.id.connect_button))
                .setOnClickListener(this);
        ((Button) this.findViewById(R.id.disconnect_button)).setOnClickListener(this);
        ((Button) this.findViewById(R.id.send_button)).setOnClickListener(this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Connecting");
        progressDialog.setMessage("Please be patient. I'm working now.");
        progressDialog.setCancelable(false);

        handler = new Handler();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (btAdapter.isEnabled())
            setup();
        else
            startActivityForResult(new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    REQUEST_ENABLE_BLUETOOTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
        case REQUEST_ENABLE_BLUETOOTH:
            if (resCode == Activity.RESULT_OK)
                setup();
            else {
                Toast.makeText(this, "I need Bluetooth to be enabled to work.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        case REQUEST_SELECT_DEVICE:
            if (resCode == Activity.RESULT_OK) {
                selectedDev = data
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                selectedDevName.setText(selectedDev.getName());
                selectedDevAddr.setText(selectedDev.getAddress());
                Log.d("DEBUG", "Selected device=" + selectedDev);
            }
            else {
                Log.d("DEBUG", "Canceled");
            }
            break;
        }
    }

    private void setup() {
        Log.d("DEBUG", "setup()");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.select_button:
            startActivityForResult(new Intent(this, DeviceListActivity.class),
                    REQUEST_SELECT_DEVICE);
            break;
        case R.id.connect_button:
            if (selectedDev == null)
                Toast.makeText(this, "Select a device first.",
                        Toast.LENGTH_SHORT).show();
            progressDialog.show();
            new Thread(new ConnectingTask(selectedDev, SPP_UUID)).start();
            break;
        case R.id.send_button:
            if (connected) {
                String text = inputText.getText().toString();
                try {
                    OutputStreamWriter out = new OutputStreamWriter(
                            socket.getOutputStream());
                    out.write(text);
                    out.flush();
                }
                catch (IOException e) {
                }
            }
            break;
        case R.id.disconnect_button:
            if (connected && socket != null && socket.isConnected()) {
                // TODO: disconnection task
            }
            break;
        }
    }

    private final class ConnectingTask implements Runnable {
        public ConnectingTask(BluetoothDevice dev, UUID uuid) {
            try {
                socket = dev.createRfcommSocketToServiceRecord(uuid);
            }
            catch (IOException e) {
                Log.e("Error", "cannot create socket", e);
                socket = null;
            }
        }

        public void run() {
            if (btAdapter.isDiscovering())
                btAdapter.cancelDiscovery();
            try {
                socket.connect();
                handler.post(new Runnable() {
                    public void run() {
                        Log.d("DEBUG", "connected");
                        progressDialog.dismiss();
                        connected = true;
                    }
                });
            }
            catch (IOException e) {
                Log.e("Error", "connection failed", e);
                socket = null;
            }
        }
    }

}
