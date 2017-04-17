package com.joss.jrow.Bluetooth;

import android.bluetooth.BluetoothSocket;

/**
 * Created by joss on 16/04/17.
 */

public class JRowSocket {
    private static final JRowSocket ourInstance = new JRowSocket();

    private BluetoothSocket socket;

    public static JRowSocket getInstance() {
        return ourInstance;
    }

    private JRowSocket() {
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }
}
