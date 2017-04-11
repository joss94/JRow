package com.joss.jrow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BluetoothConnectThread.onConnectionResponseListener, View.OnClickListener, Measures.OnNewMeasureProcessedListener {

    private final int REQUEST_ENABLE_BT = 12;
    private final String MAC_ADDRESS = "20:16:11:21:11:43";
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final int SERIAL_DISPLAY_DELAY = 0;

    private static String serialContent;

    boolean connecting;
    boolean receive=true;
    boolean calibrated = false;

    BluetoothAdapter adapter;
    BluetoothSocket socket;

    TextView serial, lastCatch;
    Button connectButton;
    GraphView graph;
    View load1;
    View load2;

    ArrayList<LineGraphSeries<DataPoint>> data;

    Handler serialDisplayHandler;

    BluetoothConnectThread connectThread;
    BluetoothListenThread listenThread;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                addToSerial("Found device: "+deviceName);
            }
        }
    };

    private Runnable displaySerial = new Runnable() {
        @Override
        public void run() {
            serialContent = serialContent.substring(Math.max(0, serialContent.length() - 100));
            serial.setText(serialContent);
            serialDisplayHandler.postDelayed(displaySerial, SERIAL_DISPLAY_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Measures.getMeasures().addOnNewMeasureProcessedListener(this);

        connecting = false;
        receive = true;
        serialContent="";

        serial = (TextView) findViewById(R.id.serial);
        lastCatch = (TextView) findViewById(R.id.last_catch);
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);
        load1 = findViewById(R.id.load1);
        load2 = findViewById(R.id.load2);

        setGraph();


        serialDisplayHandler = new Handler();
        runOnUiThread(displaySerial);

        IntentFilter filter  = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    private void setGraph(){
        graph = (GraphView) findViewById(R.id.graph);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-50.0);
        graph.getViewport().setMaxY(1500.0);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(10);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);

    }


    @Override
    public void onResume(){
        super.onResume();
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        connecting = false;
        calibrated = false;
        receive = true;
        serialContent="";
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    showDevicesAndConnect();
                }
                break;
        }
    }

    private void setUpBluetooth() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null){
            addToSerial("Device not compatible with bluetooth");
            return;
        }

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else{
            showDevicesAndConnect();
        }
    }

    public void showDevicesAndConnect(){
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        if(devices.size()>0){
            for(BluetoothDevice device : devices){
                if (device.getAddress().equals(MAC_ADDRESS)) {
                    String message = "Paired to ";
                    message += device.getName();
                    message += ", connecting...";
                    addToSerial(message);
                    connect(device);
                    break;
                }
            }
        }else{
            addToSerial("No pairing found, now scanning for new devices");
            if(!adapter.startDiscovery()){
                addToSerial("Scan failed...");
            }
        }
    }

    public void connect(BluetoothDevice device){
        connectThread = new BluetoothConnectThread(device, adapter, uuid, this);
        connectThread.start();
    }

    @Override
    public void onConnectionResponse(final boolean result, final String message, final BluetoothSocket socket) {
        if(result){
            addToSerial("Connection established!");
            this.socket = socket;
            listenThread = new BluetoothListenThread(socket);
            listenThread.start();
        }else{
            connectThread.cancel();
            addToSerial(message);
        }
    }

    private void adjustViews(final Measure measure){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                load1.setScaleX(((float)measure.getRowAngle(0)/1000));
                load2.setScaleX(((float)measure.getRowAngle(1)/1000));
            }
        });
    }

    private void showMeasureInSerial(Measure measure){
        String result="";
        result += "Time: " + String.valueOf((double) (measure.getTime()-Measures.getMeasures().getStartTime())/1000) + "\n";
        for(int i=0; i<8; i++){
            result += i+": " + measure.getRowAngle(i) + "\n";
        }
        serialContent = result;
    }

    public void startConnection(){
        Measures.getMeasures().wipeData();
        Measures.getMeasures().addOnNewMeasureProcessedListener(this);
        connectButton.setText(R.string.disconnect);
        setUpBluetooth();
        connecting = true;
        graph.removeAllSeries();
        initGraphData();
    }

    public void stopConnection(){
        connectButton.setText(R.string.connect);
        if(connectThread!=null){
            connectThread.cancel();
        }
        if(listenThread != null){
            listenThread.cancel();
        }
        connecting = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect_button:
                if(connecting){
                    stopConnection();
                }else{
                    startConnection();
                }
                break;
        }
    }

    public static synchronized void addToSerial(String message){
        serialContent += message;
        serialContent += '\n';
    }

    private void initGraphData(){
        data = new ArrayList<>();
        for(int i=0; i<8; i++){
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            data.add(series);
            graph.addSeries(series);
        }
        data.get(0).setColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    @Override
    public void onNewMeasureProcessed(final Measure measure) {
        //*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adjustViews(measure);
                showMeasureInSerial(measure);
                //*
                for(int i=0; i<1; i++){
                    try {
                        data.get(i).appendData(new DataPoint((double) (measure.getTime()-Measures.getMeasures().getStartTime())/1000, measure.getRowAngle(i)), true, 2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }/**/
            }
        });/**/
    }

    @Override
    public void onMovementChanged(final boolean ascending, final int index, final long time) {
        //*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (index==0) {
                    lastCatch.setText(((ascending)?"Ascending":"Descending") + " at "+ (double)(Measures.getMeasures().get(index).getTime()-Measures.getMeasures().get(0).getTime())/1000);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                    series.appendData(new DataPoint((double) (time)/1000, 10000), true, 200);
                    series.appendData(new DataPoint((double) (time)/1000, -10000), true, 200);
                    series.setColor(getResources().getColor(android.R.color.black));
                    series.setThickness(3);
                    graph.addSeries(series);
                }
            }
        });
        /**/
    }
}
