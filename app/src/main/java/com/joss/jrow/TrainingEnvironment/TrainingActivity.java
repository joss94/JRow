package com.joss.jrow.TrainingEnvironment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import com.joss.jrow.BluetoothConnectionActivity;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.SerialContent;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.TrainingFragment;
import com.joss.utils.SlidingDrawer.DrawerMenuItem;
import com.joss.utils.SlidingDrawer.DrawerSlidingPane;
import com.joss.utils.SlidingDrawer.OnDrawerItemClickListener;
import java.io.Serializable;
import java.util.List;

public class TrainingActivity extends BluetoothConnectionActivity implements
        OnDrawerItemClickListener,
        Measures.OnNewMeasureProcessedListener,
        TrainingControler {

    private TrainingFragment trainingFragment;

    private SerialContent serialContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        serialContent = SerialContent.getInstance();

        trainingFragment = new TrainingFragment();
        if(getIntent().hasExtra("rowers")){
            Serializable rowersNamesSerializable = getIntent().getSerializableExtra("rowers");
            List<String> rowersNames = (List<String>) rowersNamesSerializable;
            trainingFragment.setRowersNames(rowersNames);
        }

        DrawerSlidingPane drawer;
        drawer = (DrawerSlidingPane) findViewById(R.id.drawer);
        drawer.addDrawerItem(new DrawerMenuItem("Graph view", R.drawable.ic_menu_graph, R.drawable.ic_menu_graph_on));
        drawer.addDrawerItem(new DrawerMenuItem("Loadbar view", R.drawable.ic_menu_loadbar, R.drawable.ic_menu_loadbar_on));
        drawer.addDrawerItem(new DrawerMenuItem("Serial graphData", R.drawable.ic_menu_serial, R.drawable.ic_menu_serial_on));
        drawer.setOnDrawerItemClickListener(this);
        drawer.displayFragment(trainingFragment, "TRAINING_FRAGMENT");

        Measures.getMeasures().addOnNewMeasureProcessedListener(this);
    }


    //<editor-fold desc="DRAWER INTERFACE">
    @Override
    public void onDrawerItemClick(int i, DrawerMenuItem drawerMenuItem) {
        switch(i){
            case 0:
                trainingFragment.setGraphView();
                break;

            case 1:
                trainingFragment.setLoadbarView();
                break;

            case 2:
                trainingFragment.setSerialView();
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="BLUETOOTH INTERFACE">
    @Override
    protected void onDeviceFound(BluetoothDevice device) {
        serialContent.addToSerial("Found device "+device.getName());
    }

    @Override
    protected void onConnectionError(String error) {
        serialContent.addToSerial(error);
        stopTraining();
    }

    @Override
    protected void onDevicePaired(BluetoothDevice device) {
        serialContent.addToSerial("Paired with "+device.getName());
    }

    @Override
    protected void onConnectionEstablished() {
        serialContent.addToSerial("Connection established!");
        trainingFragment.setTraining(true);
    }
    //</editor-fold>


    //<editor-fold desc="TRAINING FRAGMENT CONTROLER INTERFACE">
    @Override
    public void startTraining() {
        if (!trainingFragment.isTraining() && !trainingFragment.isPaused()) {
            Measures.getMeasures().wipeData();
            Measures.getMeasures().addOnNewMeasureProcessedListener(this);

            connect();
        }
        else if(!trainingFragment.isTraining() && trainingFragment.isPaused()){
            resumeTraining();
        }
    }

    @Override
    public void stopTraining() {
        if (trainingFragment.isTraining()) {
            disconnect();
            trainingFragment.setTraining(false);
            trainingFragment.setPaused(false);
        }
    }

    @Override
    public void pauseTraining() {
        if (trainingFragment.isTraining()) {
            disconnect();
            trainingFragment.setTraining(false);
            trainingFragment.setPaused(true);
        }
    }

    @Override
    public void resumeTraining() {
        connect();
    }
    //</editor-fold>

    //<editor-fold desc="ON NEW MEASURE PROCESSED LISTENER INTERFACE">
    @Override
    public void onNewMeasureProcessed(final Measure measure) {
        trainingFragment.onNewMeasureProcessed(measure);
    }

    @Override
    public void onMovementChanged(int index, long time) {
        trainingFragment.onMovementChanged(index, time);
    }

    @Override
    public void onConnectionClosed(boolean result, String message) {
        serialContent.addToSerial((result?"Socket closed successfully":"Socket not closing...: "+message));
    }
    //</editor-fold>
}
