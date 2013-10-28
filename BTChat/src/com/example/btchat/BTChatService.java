// BTChat
// Taken from Android Example Projects

package com.example.btchat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BTChatService {
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    private static final UUID MY_UUID_SECURE = UUID
            .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID
            .fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter adapter;
    private final Handler handler;
    private AcceptThread secureAcceptThread;
    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    public enum State {
        NONE, LISTEN, CONNECTING, CONNECTED;

        public static State fromInt(int i) {
            for (State s : values())
                if (s.ordinal() == i)
                    return s;
            return null;
        }
    };

    private State state;

    public BTChatService(Context context, Handler handler) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        state = State.NONE;
        this.handler = handler;
    }

    private synchronized void setState(State state) {
        this.state = state;
        handler.obtainMessage(BTChat.MESSAGE_STATE_CHANGE, state.ordinal(), -1)
                .sendToTarget();
    }

    public synchronized State getState() {
        return state;
    }

    public synchronized void start() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(State.LISTEN);

        if (secureAcceptThread == null) {
            secureAcceptThread = new AcceptThread(true);
            secureAcceptThread.start();
        }
        if (insecureAcceptThread == null) {
            insecureAcceptThread = new AcceptThread(false);
            insecureAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if (state == State.CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        setState(State.CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket,
            BluetoothDevice device, final String socketType) {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (secureAcceptThread != null) {
            secureAcceptThread.cancel();
            secureAcceptThread = null;
        }
        if (insecureAcceptThread != null) {
            insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }

        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();

        Message msg = handler.obtainMessage(BTChat.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BTChat.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(State.CONNECTED);
    }

    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (secureAcceptThread != null) {
            secureAcceptThread.cancel();
            secureAcceptThread = null;
        }

        if (insecureAcceptThread != null) {
            insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }
        setState(State.NONE);
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (state != State.CONNECTED)
                return;
            r = connectedThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
        Message msg = handler.obtainMessage(BTChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BTChat.TOAST, "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);
        BTChatService.this.start();
    }

    private void connectionLost() {
        Message msg = handler.obtainMessage(BTChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BTChat.TOAST, "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        BTChatService.this.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        private String socketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";

            try {
                if (secure) {
                    tmp = adapter.listenUsingRfcommWithServiceRecord(
                            NAME_SECURE, MY_UUID_SECURE);
                }
                else {
                    tmp = adapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
                }
            }
            catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {

            setName("AcceptThread" + socketType);

            BluetoothSocket socket = null;

            while (state != BTChatService.State.CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                }
                catch (IOException e) {
                    break;
                }

                if (socket != null) {
                    synchronized (BTChatService.this) {
                        switch (state) {
                        case LISTEN:
                        case CONNECTING:
                            connected(socket, socket.getRemoteDevice(),
                                    socketType);
                            break;
                        case NONE:
                        case CONNECTED:
                            try {
                                socket.close();
                            }
                            catch (IOException e) {
                            }
                            break;
                        }
                    }
                }
            }

        }

        public void cancel() {
            try {
                mmServerSocket.close();
            }
            catch (IOException e) {
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String socketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";

            try {
                if (secure) {
                    tmp = device
                            .createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                }
                else {
                    tmp = device
                            .createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            }
            catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            setName("ConnectThread" + socketType);

            adapter.cancelDiscovery();

            try {
                mmSocket.connect();
            }
            catch (IOException e) {
                try {
                    mmSocket.close();
                }
                catch (IOException e2) {
                }
                connectionFailed();
                return;
            }

            synchronized (BTChatService.this) {
                connectThread = null;
            }

            connected(mmSocket, mmDevice, socketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    handler.obtainMessage(BTChat.MESSAGE_READ, bytes, -1,
                            buffer).sendToTarget();
                }
                catch (IOException e) {
                    connectionLost();
                    BTChatService.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                handler.obtainMessage(BTChat.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            }
            catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException e) {
            }
        }
    }
}
