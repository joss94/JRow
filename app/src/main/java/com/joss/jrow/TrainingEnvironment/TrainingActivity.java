package com.joss.jrow.TrainingEnvironment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.Toast;

import com.joss.jrow.BluetoothConnectionActivity;
import com.joss.jrow.Calibration.CalibrationFragment;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Position;
import com.joss.jrow.Models.Training;
import com.joss.jrow.R;
import com.joss.jrow.SensorManager;
import com.joss.jrow.SerialContent;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.TrainingFragment;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;
import com.joss.utils.SlidingDrawer.DrawerMenuItem;
import com.joss.utils.SlidingDrawer.DrawerSlidingPane;
import com.joss.utils.SlidingDrawer.OnDrawerItemClickListener;

public class TrainingActivity extends BluetoothConnectionActivity implements
        OnDrawerItemClickListener,
        Measures.OnNewMeasureProcessedListener,
        TrainingControler, OnDialogFragmentInteractionListener {

    private static final int SAVE_REQUEST_CODE = 6854;
    private static final int CALIBRATION_REQUEST_CODE = 21356;
    private TrainingFragment trainingFragment;
    //test
    private SerialContent serialContent;

    private DrawerSlidingPane drawer;

    private int drawerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        serialContent = SerialContent.getInstance();

        if (savedInstanceState != null &&
                getSupportFragmentManager().getFragment(savedInstanceState, "TRAINING_FRAGMENT") != null) {
            trainingFragment = (TrainingFragment) getSupportFragmentManager().getFragment(savedInstanceState, "TRAINING_FRAGMENT");
        }
        else{
            trainingFragment = new TrainingFragment();
        }

        drawer = (DrawerSlidingPane) findViewById(R.id.drawer);
        drawer.addDrawerItem(new DrawerMenuItem(getString(R.string.graph), R.drawable.ic_menu_graph, R.drawable.ic_menu_graph_on));
        drawer.addDrawerItem(new DrawerMenuItem(getString(R.string.loadbar), R.drawable.ic_menu_loadbar, R.drawable.ic_menu_loadbar_on));
        drawer.addDrawerItem(new DrawerMenuItem(getString(R.string.race), R.drawable.ic_race, R.drawable.ic_race_on));
        drawer.addDrawerItem(new DrawerMenuItem(getString(R.string.terminal), R.drawable.ic_menu_serial, R.drawable.ic_menu_serial_on));
        drawer.setOnDrawerItemClickListener(this);
        drawer.replaceFragment(trainingFragment, trainingFragment.getTag());

        if (savedInstanceState!=null) {
            if(savedInstanceState.containsKey("drawer_position")){
                drawer.goTo(savedInstanceState.getInt("drawer_position"));
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (TrainingFragment.isPaused()) {
            resumeTraining();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if (TrainingFragment.isRecording()) {
            pauseTraining();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("drawer_position", drawerPosition);
        try {
            getSupportFragmentManager().putFragment(outState, "TRAINING_FRAGMENT", trainingFragment);
        } catch (Exception ignored) {
        }
    }

    private void askForSaving() {
        SaveTrainingDialog d = new SaveTrainingDialog();
        d.setTitle("Save");
        d.setRequestCode(SAVE_REQUEST_CODE);
        d.setOnFragmentInteractionListener(this);
        d.show(getSupportFragmentManager(), "save_dialog");
    }

    private void askForCalibration(){
        RequestCalibrationDialog d = new RequestCalibrationDialog();
        d.setTitle("Calibration");
        d.setRequestCode(CALIBRATION_REQUEST_CODE);
        d.setOnFragmentInteractionListener(this);
        d.show(getSupportFragmentManager(), "calibration_dialog");
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
                trainingFragment.setRaceView();
                break;

            case 3:
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
                Toast.makeText(getApplicationContext(), R.string.unable_to_connect_arduino, Toast.LENGTH_SHORT).show();
                stopTraining();
            }
        });
    }

    @Override
    protected void onDevicePaired(BluetoothDevice device) {
        serialContent.addToSerial("Paired with "+device.getName());
    }

    @Override
    public void onConnectionClosed(boolean result, String message) {
        serialContent.addToSerial((result?"Socket closed successfully":"Socket not closing...: "+message));
    }

    @Override
    protected void onConnectionEstablished() {
        Measures.getMeasures().setOnNewMeasureProcessedListener(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serialContent.addToSerial("Connection established!");
                trainingFragment.startTraining();
            }
        });

    }
    //</editor-fold>

    //<editor-fold desc="TRAINING FRAGMENT CONTROLER INTERFACE">
    @Override
    public void startTraining() {
        if(!Measures.getMeasures().isCalibrated()){
            askForCalibration();
        }
        else{
            Measures.getMeasures().setOnNewMeasureProcessedListener(this);
            connect();
        }
    }

    @Override
    public void stopTraining() {
        disconnect();
        trainingFragment.stopTraining();
        if (Training.getTraining().getDuration() >0) {
            askForSaving();
        }
    }

    @Override
    public void pauseTraining() {
        disconnect();
        trainingFragment.pauseTraining();
    }

    @Override
    public void resumeTraining() {
        connect();
    }

    //</editor-fold>

    //<editor-fold desc="ON NEW MEASURE PROCESSED LISTENER INTERFACE">
    @Override
    public void onNewMeasureProcessed(final Measure measure) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (trainingFragment!=null) {
                    trainingFragment.onNewMeasureProcessed(measure);
                }
            }
        });
    }

    @Override
    public void onMovementChanged(final int index, final long time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!calibrating && trainingFragment != null && SensorManager.getInstance().isSensorActive(index)) {
                    trainingFragment.onMovementChanged(index, time);
                    if(index == Position.STERN && Training.getTraining() != null){
                        double frequency = (float)60000/(((float)(time-Measures.getMeasures().getCatchTimes()[Position.STERN])));
                        //Training.getTraining().getStrokeRates().put(time, frequency);
                    }
                }
            }
        });
    }

    //</editor-fold>


    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... objects) {
        switch(requestCode){
            case SAVE_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Training.getTraining().save((String)objects[0]);
                }
                break;

            case CALIBRATION_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Measures.getMeasures().setDefaultCalibration();
                    startTraining();
                }
                break;
        }
    }

    public void calibrate() {
        calibrating = true;
        Measures.getMeasures().resetCalibration();
        connect();
    }

    public void calibrationFinished() {
        calibrating = false;
        disconnect();
        drawer.popFragment();
        if(Measures.getMeasures().isCalibrated()){
            Toast.makeText(this, R.string.calibration_done, Toast.LENGTH_SHORT).show();
        }
        else{
            Measures.getMeasures().resetCalibration();
            Toast.makeText(this, R.string.calibration_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        if(calibrating){
            calibrationFinished();
        }
        else{
            super.onBackPressed();
        }
    }
}
