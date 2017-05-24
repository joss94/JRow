package com.joss.jrow.TrainingEnvironment;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.joss.jrow.BluetoothConnectionActivity;
import com.joss.jrow.CalibrationActivity;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
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
    public static final int CALIBRATION_DONE_REQUEST_CODE = 545;
    private TrainingFragment trainingFragment;

    private boolean calibrating = false;

    private SerialContent serialContent;

    private int drawerPosition;

    private int saveTry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        serialContent = SerialContent.getInstance();
        Measures.getMeasures().addOnNewMeasureProcessedListener(this);
        try {
            trainingFragment = (TrainingFragment) getSupportFragmentManager().getFragment(savedInstanceState, "TRAINING_FRAGMENT");
        } catch (Exception e) {
            trainingFragment = new TrainingFragment();
        }

        DrawerSlidingPane drawer = (DrawerSlidingPane) findViewById(R.id.drawer);
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
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("drawer_position", drawerPosition);
        try {
            getSupportFragmentManager().putFragment(outState, "TRAINING_FRAGMENT", trainingFragment);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Measures.getMeasures().removeOnNewMeasureProcessedListener(this);
    }

    private void askForSaving() {
        SaveTrainingDialog d = new SaveTrainingDialog();
        d.setTitle(saveTry == 0 ? "Save" : "Save ("+saveTry+")");
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
        if(calibrating){
            Intent intent = new Intent(this, CalibrationActivity.class);
            startActivityForResult(intent, TrainingActivity.CALIBRATION_DONE_REQUEST_CODE);
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    trainingFragment.startTraining();
                    saveTry = 0;
                }
            });

        }
    }
    //</editor-fold>

    //<editor-fold desc="TRAINING FRAGMENT CONTROLER INTERFACE">
    @Override
    public void startTraining() {
        if(!Measures.getMeasures().isCalibrated()){
            askForCalibration();
        }
        else{
            Measures.getMeasures().addOnNewMeasureProcessedListener(this);
            connect();
        }
    }

    @Override
    public void stopTraining() {
        disconnect();
        if (Training.getTraining().getDuration() >0) {
            askForSaving();
        }
        trainingFragment.stopTraining();
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
        //Training.getTraining().onNewMeasureProcessed(measure);
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
    public void onMovementChanged(final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (trainingFragment != null && SensorManager.getInstance().isSensorActive(index)) {
                    trainingFragment.onMovementChanged(index);
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
                    if (!Training.getTraining().save((String)objects[0]) && saveTry <3) {
                        saveTry++;
                        askForSaving();
                    }
                    else{
                        saveTry = 0;
                    }
                }
                break;

            case CALIBRATION_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Measures.getMeasures().setDefaultCalibration(this);
                    startTraining();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode){
            case CALIBRATION_DONE_REQUEST_CODE:
                stopTraining();
                calibrating = false;
                if(resultCode == RESULT_OK){
                    if(Measures.getMeasures().isCalibrated()){
                        Toast.makeText(this, R.string.calibration_done, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Measures.getMeasures().resetCalibration();
                    Toast.makeText(this, R.string.calibration_failed, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void calibrate() {
        calibrating = true;
        connect();
    }
}
