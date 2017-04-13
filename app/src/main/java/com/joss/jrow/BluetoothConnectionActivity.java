package com.joss.jrow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joss.jrow.Bluetooth.BluetoothConnectThread;
import com.joss.jrow.Bluetooth.BluetoothListenThread;

import java.io.IOException;
import java.util.Set;

public abstract class BluetoothConnectionActivity extends AppCompatActivity implements BluetoothConnectThread.onConnectionResponseListener {

    private final int REQUEST_ENABLE_BT = 12;
    private final String MAC_ADDRESS = "20:16:11:21:11:43";
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;

    private BluetoothConnectThread connectThread;
    private BluetoothListenThread listenThread;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                onDeviceFound(device);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter  = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException ignored) {
            }
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
        connectThread = new BluetoothConnectThread(device, adapter, this);
        connectThread.start();
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
        if(result){
            onConnectionEstablished();
            this.socket = socket;
            listenThread = new BluetoothListenThread(socket);
            listenThread.start();
        }else{
            connectThread.cancel();
            onConnectionError(message);
        }
    }

    protected void connect(){
        setUpBluetooth();
    }

    protected void disconnect(){
        if(connectThread!=null){
            connectThread.cancel();
        }
        if(listenThread != null){
            listenThread.cancel();
        }
    }


    protected abstract void onDeviceFound(BluetoothDevice device);
    protected abstract void onConnectionError(String error);
    protected abstract void onDevicePaired(BluetoothDevice device);
    protected abstract void onConnectionEstablished();
}
