package com.joss.jrow.Bluetooth;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BluetoothListenReceiver extends WakefulBroadcastReceiver {

    public static final String START_LISTEN_BLUETOOTH = "start_listen_bluetooth_jrow";
    public static final String STOP_LISTEN_BLUETOOTH = "stop_listen_bluetooth_jrow";

    public static BluetoothListenReceiver instance = new BluetoothListenReceiver();

    private Intent service;

    public static BluetoothListenReceiver getInstance(){
        return instance;
    }

    private BluetoothListenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case START_LISTEN_BLUETOOTH:
                if (service == null) {
                    service = new Intent(context, BluetoothListenService.class);
                }
                context.startService(service);
                //startWakefulService(context, service);
                break;

            case STOP_LISTEN_BLUETOOTH:
                try {
                    context.stopService(service);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
