package com.joss.jrow;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/*
 * Created by joss on 23/03/17.
 */

public class BluetoothListenThread extends Thread {

    private final BluetoothSocket socket;
    private volatile InputStream is;
    private DataProcessThread dataProcessThread;
    private DataReadThread dataReadThread;

    private Handler handler;

    private volatile boolean running = true;

    public BluetoothListenThread(BluetoothSocket socket){
        Looper.prepare();

        this.socket = socket;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler = new Handler();

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
            MainActivity.addToSerial("thread stopped listening");
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
