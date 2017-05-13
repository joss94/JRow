package com.joss.jrow.Bluetooth;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BluetoothListenReceiver extends WakefulBroadcastReceiver {

    public static final String START_LISTEN_BLUETOOTH = "start_listen_bluetooth_jrow";
    public static final String STOP_LISTEN_BLUETOOTH = "stop_listen_bluetooth_jrow";

    Intent service;


    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case START_LISTEN_BLUETOOTH:
                service = new Intent(context, BluetoothListenService.class);
                startWakefulService(context, service);
                break;

            case STOP_LISTEN_BLUETOOTH:
                context.stopService(service);
                break;
        }
    }
}
