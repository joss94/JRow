package com.joss.jrow.TrainingEnvironment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joss.jrow.CalibrationActivity;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.R;

/*
 * Created by joss on 11/04/17.
 */

public abstract class TrainingFragment extends Fragment implements View.OnClickListener {

    protected TextView strokeRateView;
    private ImageView stopButton, startAndPauseButton, calibrateButton;

    protected static boolean training =false;

    protected TrainingFragmentControler listener;

    protected static String serialContent;
    protected static Measure lastMeasure;

    protected static boolean[] activeSensors;
    protected static String[] rowersNames;

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        if(activeSensors == null){
            activeSensors = new boolean[] {false, false, false, false, false, false, false, false};
        }
        if(rowersNames == null){
            rowersNames = new String[] {"", "", "", "", "", "", "", ""};
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        View v = inflater.inflate(getLayoutID(), parent, false);
        findAndSetViews(v);

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(!(context instanceof TrainingFragmentControler)){
            throw new Error("TrainingFragment parent ativity must implement TrainingFragmentControler");
        }
        listener = (TrainingFragmentControler) context;
    }


    private void findAndSetViews(View v){
        strokeRateView = (TextView) v.findViewById(R.id.stroke_rate);
        if(strokeRateView == null){
            throw new Error("Fragment must contain TextView with stroke_rate_view id within its layout");
        }

        startAndPauseButton = (ImageView) v.findViewById(R.id.start_and_pause_button);
        if(startAndPauseButton == null){
            throw new Error("Fragment must contain ImageView with start_and_pause_button id within its layout");
        }

        stopButton = (ImageView) v.findViewById(R.id.stop_button);
        if(stopButton == null){
            throw new Error("Fragment must contain ImageView with stop_button id within its layout");
        }

        calibrateButton = (ImageView) v.findViewById(R.id.calibrate_button);
        if(calibrateButton == null){
            throw new Error("Fragment must contain ImageView with calibrate_button id within its layout");
        }

        startAndPauseButton.setImageResource(R.drawable.ic_rowing);
        calibrateButton.setImageResource(R.drawable.ic_calibrate);
        stopButton.setImageResource(R.drawable.ic_action_playback_stop);

        if(training){
            onStartTraining();
        }
        else{
            onStopTraining();
        }

        startAndPauseButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        calibrateButton.setOnClickListener(this);

        findViews(v);
        setViews();
    }

    public static boolean isSensorActive(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        return activeSensors[index];
    }

    public static void activateSensor(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        activeSensors[index] = true;
    }

    public static void deactivateSensor(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        activeSensors[index] = false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_and_pause_button:
                if(training){
                    listener.stopTraining();
                }
                else{
                    listener.startTraining();
                }
                break;

            case R.id.stop_button:
                if(training){
                    listener.stopTraining();
                }
                break;

            case R.id.calibrate_button:
                Intent intent = new Intent(getContext(), CalibrationActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void onStartTraining(){
        training =true;
        if (stopButton != null && calibrateButton != null && startAndPauseButton != null) {
            stopButton.setVisibility(View.VISIBLE);
            calibrateButton.setVisibility(View.GONE);
            startAndPauseButton.setImageResource(R.drawable.ic_pause);
        }
    }

    public void onStopTraining(){
        training =false;
        if (stopButton != null && calibrateButton != null && startAndPauseButton != null) {
            calibrateButton.setVisibility(View.VISIBLE);
            startAndPauseButton.setImageResource(R.drawable.ic_rowing);
        }
    }

    public static void updateData(Measure measure, String serial){
        if (measure != null) {
            lastMeasure = measure;
        }
        serialContent += serial;
    }

    protected abstract int getLayoutID();
    protected abstract void findViews(View v);
    protected abstract void setViews();
    public abstract void onMovementChanged(boolean ascending, int index, long time);
    public abstract void showData();

}
