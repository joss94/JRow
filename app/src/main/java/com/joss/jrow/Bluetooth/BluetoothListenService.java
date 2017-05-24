package com.joss.jrow.Bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.joss.jrow.DataProcessingThreads.DataWriteThread;
import com.joss.jrow.Models.Measures;

public class BluetoothListenService extends Service {

    private BluetoothListenThread listenThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        listenThread = new BluetoothListenThread();
        listenThread.start();

        DataWriteThread.reset();
        DataWriteThread.getInstance().start();
        Measures.getMeasures().addOnNewMeasureProcessedListener(DataWriteThread.getInstance());

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        listenThread.interrupt();
        DataWriteThread.getInstance().interrupt();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
