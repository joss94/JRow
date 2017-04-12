package com.joss.jrow.TrainingEnvironment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;

import com.joss.jrow.BluetoothConnectionActivity;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.GraphView.GraphViewFragment;
import com.joss.jrow.TrainingEnvironment.SerialView.SerialViewFragment;
import com.joss.utils.SlidingDrawer.DrawerMenuItem;
import com.joss.utils.SlidingDrawer.DrawerSlidingPane;
import com.joss.utils.SlidingDrawer.OnDrawerItemClickListener;

public class TrainingActivity extends BluetoothConnectionActivity implements
        OnDrawerItemClickListener,
        Measures.OnNewMeasureProcessedListener,
        TrainingFragmentControler{

    private DrawerSlidingPane drawer;

    private String[] rowersNames;
    private boolean[] activeSensors;

    private static final int SERIAL_DISPLAY_DELAY = 0;

    private static String serialContent;

    boolean calibrated = false;

    private TrainingFragment fragment;

    Handler serialDisplayHandler;

    private Runnable displaySerial = new Runnable() {
        @Override
        public void run() {
            if(fragment instanceof SerialViewFragment){
                ((SerialViewFragment)fragment).displaySerial(serialContent);
                serialContent = "";
            }
            serialDisplayHandler.postDelayed(displaySerial, SERIAL_DISPLAY_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        drawer = (DrawerSlidingPane) findViewById(R.id.drawer);
        drawer.addDrawerItem(new DrawerMenuItem("Graph view", R.drawable.ic_menu_graph, R.drawable.ic_menu_graph_on));
        drawer.addDrawerItem(new DrawerMenuItem("Serial data", R.drawable.ic_menu_serial, R.drawable.ic_menu_serial_on));

        drawer.setOnDrawerItemClickListener(this);

        rowersNames = new String[] {"", "", "", "", "", "", "", ""};
        activeSensors = new boolean[] {false, false, false, false, false, false, false, false};

        Measures.getMeasures().addOnNewMeasureProcessedListener(this);

        serialContent="";

        serialDisplayHandler = new Handler();
        runOnUiThread(displaySerial);
    }

    @Override
    public void onResume(){
        super.onResume();
        calibrated = false;
        serialContent="";
    }


    public static synchronized void addToSerial(String message){
        serialContent += message;
        serialContent += '\n';
    }


    //<editor-fold desc="DRAWER INTERFACE">
    @Override
    public void onDrawerItemClick(int i, DrawerMenuItem drawerMenuItem) {
        switch(i){
            case 0:
                fragment = GraphViewFragment.newInstance();
                drawer.replaceFragment(fragment, "GRAPH_VIEW");
                break;

            case 1:
                fragment = SerialViewFragment.newInstance();
                drawer.replaceFragment(fragment, "SERIAL_VIEW");
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="BLUETOOTH INTERFACE">
    @Override
    protected void onDeviceFound(BluetoothDevice device) {
        addToSerial("Found device "+device.getName());
    }

    @Override
    protected void onConnectionError(String error) {
        addToSerial(error);
    }

    @Override
    protected void onDevicePaired(BluetoothDevice device) {
        addToSerial("Paired with "+device.getName());
    }

    @Override
    protected void onConnectionEstablished() {
        addToSerial("Connection established!");
    }
    //</editor-fold>

    //<editor-fold desc="TRAINING FRAGMENT CONTROLER INTERFACE">
    @Override
    public void startTraining() {
        Measures.getMeasures().wipeData();
        Measures.getMeasures().addOnNewMeasureProcessedListener(this);
        connect();
    }

    @Override
    public void stopTraining() {
        disconnect();
    }

    @Override
    public String[] getRowersNames() {
        return rowersNames;
    }

    @Override
    public boolean isSensorActive(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        return activeSensors[index];
    }

    @Override
    public void activateSensor(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        activeSensors[index] = true;
    }

    @Override
    public void deactivateSensor(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        activeSensors[index] = false;
    }
    //</editor-fold>

    //<editor-fold desc="ON NEW MEASURE PROCESSED LISTENER INTERFACE">
    @Override
    public void onNewMeasureProcessed(final Measure measure) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.updateData(measure);
            }
        });
    }

    @Override
    public void onMovementChanged(final boolean ascending, final int index, final long time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.onMovementChanged(ascending, index, time);
            }
        });
    }
    //</editor-fold>
}
