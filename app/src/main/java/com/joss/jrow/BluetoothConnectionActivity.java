package com.joss.jrow;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.joss.jrow.Bluetooth.BluetoothConnectThread;
import com.joss.jrow.Bluetooth.JRowSocket;
import com.joss.jrow.Bluetooth.BluetoothListenReceiver;

import java.util.Set;

public abstract class BluetoothConnectionActivity extends AppCompatActivity implements
        BluetoothConnectThread.onConnectionResponseListener {

    private static final long CONNECTION_DELAY = 10000;
    private final int REQUEST_ENABLE_BT = 12;
    private final String MAC_ADDRESS = "20:16:11:21:11:43";
    private BluetoothAdapter adapter;

    private BluetoothConnectThread connectThread;
    private ProgressDialog progress;
    private Handler connectionOvertimeHandler;
    private Runnable cancelConnect;

    private final BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                onDeviceFound(device);
            }
        }
    };
    private BluetoothListenReceiver bluetoothListenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(connectReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        bluetoothListenReceiver = new BluetoothListenReceiver();
        registerReceiver(bluetoothListenReceiver, new IntentFilter(BluetoothListenReceiver.START_LISTEN_BLUETOOTH));

        connectionOvertimeHandler = new Handler();

        cancelConnect = new Runnable() {
            @Override
            public void run() {
                disconnect();
                progress.dismiss();
                onConnectionError("Connect time expired");
            }
        };

        progress = new ProgressDialog(this);
        progress.setTitle("Connecting");
        progress.setMessage("Wait while connecting to the Arduino..");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(connectReceiver);
        unregisterReceiver(bluetoothListenReceiver);
    }

    private void setUpBluetooth() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null){
            onConnectionError("Device not compatible with bluetooth");
            return;
        }

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else{
            showDevicesAndConnect();
        }
    }

    private void showDevicesAndConnect(){
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        if(devices.size()>0){
            for(BluetoothDevice device : devices){
                if (device.getAddress().equals(MAC_ADDRESS)) {
                    onDevicePaired(device);
                    connectToDevice(device);
                    break;
                }
            }
        }else{
            onConnectionError("No pairing found, now scanning for new devices");
            if(!adapter.startDiscovery()){
                onConnectionError("Scan failed...");
            }
        }
    }

    private void connectToDevice(BluetoothDevice device){
        progress.show();
        connectThread = new BluetoothConnectThread(device, adapter, this);
        connectThread.start();
        connectionOvertimeHandler.postDelayed(cancelConnect, CONNECTION_DELAY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    showDevicesAndConnect();
                }
                else{
                    onConnectionError("Bluetooth must be turned on");
                }
                break;
        }
    }

    @Override
    public void onConnectionResponse(final boolean result, final String message, final BluetoothSocket socket) {
        progress.dismiss();
        connectionOvertimeHandler.removeCallbacks(cancelConnect);
        if(result){
            onConnectionEstablished();
            JRowSocket.getInstance().setSocket(socket);
            sendBroadcast(new Intent(BluetoothListenReceiver.START_LISTEN_BLUETOOTH));
        }else{
            connectThread.interrupt();
            onConnectionError(message);
        }
    }

    protected void connect(){
        setUpBluetooth();
    }

    protected void disconnect(){
        connectThread.interrupt();
        sendBroadcast(new Intent(BluetoothListenReceiver.STOP_LISTEN_BLUETOOTH));
    }


    protected abstract void onDeviceFound(BluetoothDevice device);
    protected abstract void onConnectionError(String error);
    protected abstract void onDevicePaired(BluetoothDevice device);
    protected abstract void onConnectionEstablished();
}
