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
import com.joss.jrow.Bluetooth.BluetoothListenReceiver;
import com.joss.jrow.Bluetooth.JRowSocket;

import java.util.Set;

public abstract class BluetoothConnectionActivity extends AppCompatActivity implements
        BluetoothConnectThread.OnConnectionResponseListener {

    private static final long CONNECTION_DELAY = 10000;
    private final int REQUEST_ENABLE_BT = 12;
    private final String MAC_ADDRESS = "20:16:11:21:11:43";
    private BluetoothAdapter adapter;

    private ConnectProgressDialog progress;
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
                onConnectionError("Connect time expired");
            }
        };

        progress = new ConnectProgressDialog(this);

        if(savedInstanceState != null){
            if(savedInstanceState.getBoolean("IS_CONNECTING")){
                progress.show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean("IS_CONNECTING", progress.isShowing());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(connectReceiver);
        unregisterReceiver(bluetoothListenReceiver);
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
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
        BluetoothConnectThread.set(device, adapter);
        BluetoothConnectThread.getInstance().addListener(this);
        BluetoothConnectThread.getInstance().addListener(progress);
        BluetoothConnectThread.getInstance().start();
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
        connectionOvertimeHandler.removeCallbacks(cancelConnect);
        if(result){
            onConnectionEstablished();
            JRowSocket.getInstance().setSocket(socket);
            sendBroadcast(new Intent(BluetoothListenReceiver.START_LISTEN_BLUETOOTH));
        }else{
            BluetoothConnectThread.getInstance().interrupt();
            onConnectionError(message);
        }
    }

    protected void connect(){
        setUpBluetooth();
    }

    protected void disconnect(){
        BluetoothConnectThread.getInstance().interrupt();
        sendBroadcast(new Intent(BluetoothListenReceiver.STOP_LISTEN_BLUETOOTH));
    }

    protected abstract void onDeviceFound(BluetoothDevice device);
    protected abstract void onConnectionError(String error);
    protected abstract void onDevicePaired(BluetoothDevice device);
    protected abstract void onConnectionEstablished();

    private class ConnectProgressDialog extends ProgressDialog implements BluetoothConnectThread.OnConnectionResponseListener{

        ConnectProgressDialog(Context context) {
            super(context);
            setTitle("Connecting");
            setMessage("Wait while connecting to the Arduino..");
            setCancelable(false);
            if (BluetoothConnectThread.getInstance()!=null) {
                BluetoothConnectThread.getInstance().addListener(this);
            }
        }

        @Override
        public void onDetachedFromWindow(){
            super.onDetachedFromWindow();
            //connectThread.removeListener(this);
        }

        @Override
        public void onConnectionResponse(boolean result, String message, BluetoothSocket socket) {
            dismiss();
        }

        @Override
        public void onConnectionClosed(boolean result, String message) {

        }
    }
}
