package com.joss.jrow;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;

public class CalibrationActivity extends BluetoothConnectionActivity implements View.OnClickListener, Measures.OnNewMeasureProcessedListener {

    private Measure measure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        findViewById(R.id.OK_button).setOnClickListener(this);
        connect();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        disconnect();
    }

    @Override
    protected void onDeviceFound(BluetoothDevice device) {

    }

    @Override
    protected void onConnectionError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SerialContent.getInstance().addToSerial(error);
                Toast.makeText(getApplicationContext(), R.string.unable_to_connect_arduino, Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void onDevicePaired(BluetoothDevice device) {

    }

    @Override
    protected void onConnectionEstablished() {
        Measures.getMeasures().setOnNewMeasureProcessedListener(this);
    }


    @Override
    public void onConnectionClosed(boolean result, String message) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.OK_button:
                if (measure != null) {
                    Measures.getMeasures().setNeutralPosition(measure);
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        this.measure = measure;
    }

    @Override
    public void onMovementChanged(int index, long time) {

    }
}
