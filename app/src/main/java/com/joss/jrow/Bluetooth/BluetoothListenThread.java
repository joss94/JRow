package com.joss.jrow.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Looper;
import android.util.Log;

import com.joss.jrow.DataProcessingThreads.DataProcessThread;
import com.joss.jrow.DataProcessingThreads.DataReadThread;

import java.io.IOException;
import java.io.InputStream;

public class BluetoothListenThread extends Thread {

    private final BluetoothSocket socket;
    private volatile InputStream is;
    private final DataProcessThread dataProcessThread;
    private final DataReadThread dataReadThread;

    public BluetoothListenThread(){
        Looper.prepare();
        socket = JRowSocket.getInstance().getSocket();
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataProcessThread = new DataProcessThread();
        dataProcessThread.start();

        dataReadThread = new DataReadThread();
        dataReadThread.start();
    }

    @Override
    public void run(){
        byte[] buffer = new byte[90];

        while(!isInterrupted()){
            try {
                int numBytes = is.read(buffer);
                dataReadThread.addData(new String(buffer, 0, numBytes));
            } catch (IOException ignored) {
            }
        }
    }

    public void cancel(){
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(this.getName(), "Could not close the connect socket", e);
            }
        }
        dataReadThread.interrupt();
        dataProcessThread.interrupt();
        interrupt();
    }
}
