package com.joss.jrow.TrainingEnvironment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.Toast;

import com.joss.jrow.BluetoothConnectionActivity;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Position;
import com.joss.jrow.Models.Training;
import com.joss.jrow.R;
import com.joss.jrow.SerialContent;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.TrainingFragment;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;
import com.joss.utils.SlidingDrawer.DrawerMenuItem;
import com.joss.utils.SlidingDrawer.DrawerSlidingPane;
import com.joss.utils.SlidingDrawer.OnDrawerItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrainingActivity extends BluetoothConnectionActivity implements
        OnDrawerItemClickListener,
        Measures.OnNewMeasureProcessedListener,
        TrainingControler, OnDialogFragmentInteractionListener {

    private static final int SAVE_REQUEST_CODE = 6854;
    private TrainingFragment trainingFragment;

    private SerialContent serialContent;

    private Training training;

    private List<String> rowersNames;

    private DrawerSlidingPane drawer;

    private int drawerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        serialContent = SerialContent.getInstance();

        if (savedInstanceState != null) {
            trainingFragment = (TrainingFragment) getSupportFragmentManager().getFragment(savedInstanceState, "TRAINING_FRAGMENT");
        }
        else{
            trainingFragment = new TrainingFragment();
        }
        rowersNames = new ArrayList<>();
        if(getIntent().hasExtra("rowers")){
            Serializable rowersNamesSerializable = getIntent().getSerializableExtra("rowers");
            rowersNames = (List<String>) rowersNamesSerializable;
            trainingFragment.setRowersNames(rowersNames);
        }

        drawer = (DrawerSlidingPane) findViewById(R.id.drawer);
        drawer.addDrawerItem(new DrawerMenuItem("Graph view", R.drawable.ic_menu_graph, R.drawable.ic_menu_graph_on));
        drawer.addDrawerItem(new DrawerMenuItem("Loadbar view", R.drawable.ic_menu_loadbar, R.drawable.ic_menu_loadbar_on));
        drawer.addDrawerItem(new DrawerMenuItem("Serial graphData", R.drawable.ic_menu_serial, R.drawable.ic_menu_serial_on));
        drawer.setOnDrawerItemClickListener(this);
        drawer.displayFragment(trainingFragment, "TRAINING_FRAGMENT");

        Measures.getMeasures().addOnNewMeasureProcessedListener(this);

        if (savedInstanceState!=null) {
            if(savedInstanceState.containsKey("drawer_position")){
                drawer.goTo(savedInstanceState.getInt("drawer_position"));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("drawer_position", drawerPosition);
        getSupportFragmentManager().putFragment(outState, "TRAINING_FRAGMENT", trainingFragment);
    }

    private void askForSaving() {
        SaveTrainingDialog d = new SaveTrainingDialog();
        d.setTitle("Save");
        d.setTraining(training);
        d.setRequestCode(SAVE_REQUEST_CODE);
        d.setOnFragmentInteractionListener(this);
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
        drawerPosition = i;
    }
    //</editor-fold>

    //<editor-fold desc="BLUETOOTH INTERFACE">
    @Override
    protected void onDeviceFound(BluetoothDevice device) {
        serialContent.addToSerial("Found device "+device.getName());
    }

    @Override
    protected void onConnectionError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serialContent.addToSerial(error);
                Toast.makeText(getApplicationContext(), "Unable to connect to Arduino", Toast.LENGTH_SHORT).show();
                stopTraining();
            }
        });
    }

    @Override
    protected void onDevicePaired(BluetoothDevice device) {
        serialContent.addToSerial("Paired with "+device.getName());
    }

    @Override
    protected void onConnectionEstablished() {
        serialContent.addToSerial("Connection established!");
        trainingFragment.setRecording(true);
    }
    //</editor-fold>

    //<editor-fold desc="TRAINING FRAGMENT CONTROLER INTERFACE">
    @Override
    public void startTraining() {
        training = new Training();
        training.setRowers(rowersNames);
        if (!trainingFragment.isRecording() && !trainingFragment.isPaused()) {
            Measures.getMeasures().wipeData();
            Measures.getMeasures().addOnNewMeasureProcessedListener(this);

            connect();
        }
        else if(!trainingFragment.isRecording() && trainingFragment.isPaused()){
            resumeTraining();
        }
    }

    @Override
    public void stopTraining() {
        if (trainingFragment.isRecording()) {
            disconnect();
            trainingFragment.setRecording(false);
            trainingFragment.setPaused(false);
            askForSaving();
        }
    }

    @Override
    public void pauseTraining() {
        if (trainingFragment.isRecording()) {
            disconnect();
            trainingFragment.setRecording(false);
            trainingFragment.setPaused(true);
        }
    }

    @Override
    public void resumeTraining() {
        connect();
    }
    //</editor-fold>

    public void goToGraphView(){
        drawer.goTo(0);
    }

    //<editor-fold desc="ON NEW MEASURE PROCESSED LISTENER INTERFACE">
    @Override
    public void onNewMeasureProcessed(final Measure measure) {
        trainingFragment.onNewMeasureProcessed(measure);
    }

    @Override
    public void onMovementChanged(int index, long time) {
        trainingFragment.onMovementChanged(index, time);
        if(index == Position.STERN && training != null){
            double frequency = (float)60000/(((float)(time-Measures.getMeasures().getCatchTimes()[Position.STERN])));
            training.getStrokeRates().put(time, frequency);
        }
    }

    @Override
    public void onConnectionClosed(boolean result, String message) {
        serialContent.addToSerial((result?"Socket closed successfully":"Socket not closing...: "+message));
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... objects) {
        switch(requestCode){
            case SAVE_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    saveTraining();
                }
                else{
                    training = null;
                }
                break;
        }
    }

    private void saveTraining() {
        //TODO implement method
        training = null;
    }

    public void calibrate() {
        trainingFragment.calibrate();
    }
    //</editor-fold>
}
