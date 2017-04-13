package com.joss.jrow.DataProcessingThreads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/*
 * Created by joss on 23/03/17.
 */

public class BluetoothConnectThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final BluetoothAdapter adapter;
    private final onConnectionResponseListener listener;

    public BluetoothConnectThread(BluetoothDevice device, BluetoothAdapter adapter, UUID uuid, onConnectionResponseListener listener) {
        this.listener = listener;
        BluetoothSocket tmp = null;
        this.adapter = adapter;

        try {
            tmp = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        adapter.cancelDiscovery();

        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            try {
                listener.onConnectionResponse(false, connectException.getMessage(), null);
                mmSocket.close();
            } catch (IOException closeException) {
                listener.onConnectionResponse(false, "Could not close the client socket", null);
            }
            return;
        }

        listener.onConnectionResponse(true, "success", mmSocket);
    }

    public void cancel() {
        try {
            mmSocket.close();
            listener.onConnectionClosed(true, "Socket closed");
        } catch (IOException e) {
            listener.onConnectionClosed(false, "Could not close the client socket");
        }
    }

    public interface onConnectionResponseListener{
        void onConnectionResponse(boolean result, String message, BluetoothSocket socket);
        void onConnectionClosed(boolean result, String message);
    }

}
