package com.joss.jrow.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class BluetoothConnectThread extends Thread {

    private final BluetoothSocket socket;
    private final BluetoothAdapter adapter;
    private onConnectionResponseListener listener;

    public BluetoothConnectThread(BluetoothDevice device, BluetoothAdapter adapter, onConnectionResponseListener listener) {
        this.listener = listener;
        BluetoothSocket tmp = null;
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
            if (listener != null) {
                listener.onConnectionResponse(false, connectException.getMessage(), null);
            }
            return;
        }

        if (listener != null) {
            listener.onConnectionResponse(true, "success", socket);
        }
    }

    public void cancel() {
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
            JRowSocket.getInstance().setSocket(null);
            if (listener != null) {
                listener.onConnectionClosed(true, "Socket closed");
                listener = null;
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onConnectionClosed(false, "Could not close the client socket");
            }
        }
    }

    public interface onConnectionResponseListener{
        void onConnectionResponse(boolean result, String message, BluetoothSocket socket);
        void onConnectionClosed(boolean result, String message);
    }

}
