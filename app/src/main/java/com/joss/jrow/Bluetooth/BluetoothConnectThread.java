package com.joss.jrow.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BluetoothConnectThread extends Thread implements Serializable {

    private BluetoothSocket socket;
    private BluetoothAdapter adapter;
    private List<OnConnectionResponseListener> listeners;

    private static BluetoothConnectThread instance;

    public static BluetoothConnectThread getInstance(){
        return instance;
    }

    public static BluetoothConnectThread set(BluetoothDevice device, BluetoothAdapter adapter){
        instance = new BluetoothConnectThread(device, adapter);
        return instance;
    }

    private BluetoothConnectThread(BluetoothDevice device, BluetoothAdapter adapter) {
        BluetoothSocket tmp = null;
        listeners = new ArrayList<>();
        this.adapter = adapter;

        try {
            tmp = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        socket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        adapter.cancelDiscovery();

        try {
            socket.connect();
            } catch (IOException connectException) {
            for (OnConnectionResponseListener listener:listeners) {
                listener.onConnectionResponse(false, connectException.getMessage(), null);
            }
            return;
        }

        for (OnConnectionResponseListener listener:listeners) {
            listener.onConnectionResponse(true, "success", socket);
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
            JRowSocket.getInstance().setSocket(null);
            for (OnConnectionResponseListener listener:listeners) {
                listener.onConnectionClosed(true, "Socket closed");
            }
        } catch (IOException e) {
            for (OnConnectionResponseListener listener:listeners) {
                listener.onConnectionClosed(false, "Could not close the client socket");
            }
        }
    }

    public void addListener(OnConnectionResponseListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OnConnectionResponseListener listener){
        listeners.remove(listener);
    }

    public interface OnConnectionResponseListener {
        void onConnectionResponse(boolean result, String message, BluetoothSocket socket);
        void onConnectionClosed(boolean result, String message);
    }

}
