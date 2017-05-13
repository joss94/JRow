package com.joss.jrow.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Looper;
import android.util.Log;

import com.joss.jrow.DataProcessingThreads.DataProcessThread;
import com.joss.jrow.DataProcessingThreads.DataReadThread;
import com.joss.jrow.SerialContent;

import java.io.IOException;
import java.io.InputStream;

public class BluetoothListenThread extends Thread {

    private final BluetoothSocket socket;
    private volatile InputStream is;
    private final DataProcessThread dataProcessThread;
    private final DataReadThread dataReadThread;

    private volatile boolean running = true;

    public BluetoothListenThread(BluetoothSocket socket){
        Looper.prepare();

        this.socket = socket;
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

        running=true;
        byte[] buffer = new byte[90];
        if(!running){
            SerialContent.getInstance().addToSerial("thread stopped listening");
        }

        while(running){
            try {
                int numBytes = is.read(buffer);
                dataReadThread.addData(new String(buffer, 0, numBytes));
            } catch (IOException ignored) {
            }
        }
    }

    public void cancel(){
        running = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(this.getName(), "Could not close the connect socket", e);
            }
        }
        dataReadThread.cancel();
        dataProcessThread.cancel();
        interrupt();
    }
}
