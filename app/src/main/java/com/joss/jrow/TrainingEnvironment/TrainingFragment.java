package com.joss.jrow.TrainingEnvironment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.joss.jrow.CalibrationActivity;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.R;

import java.util.ArrayList;

/*
 * Created by joss on 11/04/17.
 */

public abstract class TrainingFragment extends Fragment implements View.OnClickListener {

    protected TextView strokeRateView;
    private ImageView stopButton, startAndPauseButton, calibrateButton;

    protected static boolean training =false;

    protected TrainingFragmentControler listener;

    protected static volatile String serialContent;
    protected static volatile Measure lastMeasure;

    protected static boolean[] activeSensors;
    protected static String[] rowersNames;

    protected volatile ArrayList<CheckBox> checkBoxes;
    protected volatile CheckBox checkBox1;
    protected volatile CheckBox checkBox2;
    protected volatile CheckBox checkBox3;
    protected volatile CheckBox checkBox4;
    protected volatile CheckBox checkBox5;
    protected volatile CheckBox checkBox6;
    protected volatile CheckBox checkBox7;
    protected volatile CheckBox checkBox8;

    protected volatile ArrayList<TextView> angles;
    protected volatile TextView angle1;
    protected volatile TextView angle2;
    protected volatile TextView angle3;
    protected volatile TextView angle4;
    protected volatile TextView angle5;
    protected volatile TextView angle6;
    protected volatile TextView angle7;
    protected volatile TextView angle8;

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        if(activeSensors == null){
            activeSensors = new boolean[] {false, false, false, false, false, false, false, false};
        }
        if(rowersNames == null){
            rowersNames = new String[] {"", "", "", "", "", "", "", ""};
        }
        checkBoxes = new ArrayList<>();
        angles = new ArrayList<>();
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

        checkBox1 = (CheckBox) v.findViewById(R.id.checkbox1);
        checkBox2 = (CheckBox) v.findViewById(R.id.checkbox2);
        checkBox3 = (CheckBox) v.findViewById(R.id.checkbox3);
        checkBox4 = (CheckBox) v.findViewById(R.id.checkbox4);
        checkBox5 = (CheckBox) v.findViewById(R.id.checkbox5);
        checkBox6 = (CheckBox) v.findViewById(R.id.checkbox6);
        checkBox7 = (CheckBox) v.findViewById(R.id.checkbox7);
        checkBox8 = (CheckBox) v.findViewById(R.id.checkbox8);

        checkBoxes.add(checkBox1);
        checkBoxes.add(checkBox2);
        checkBoxes.add(checkBox3);
        checkBoxes.add(checkBox4);
        checkBoxes.add(checkBox5);
        checkBoxes.add(checkBox6);
        checkBoxes.add(checkBox7);
        checkBoxes.add(checkBox8);

        for(final CheckBox checkBox : checkBoxes){
            if(isSensorActive(checkBoxes.indexOf(checkBox))){
                checkBox.setChecked(true);
            }
            else{
                checkBox.setChecked(false);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        activateSensor(checkBoxes.indexOf(checkBox));
                    }
                    else{
                        deactivateSensor(checkBoxes.indexOf(checkBox));
                    }
                }
            });
        }

        angle1 = (TextView)v.findViewById(R.id.angle1);
        angle2 = (TextView)v.findViewById(R.id.angle2);
        angle3 = (TextView)v.findViewById(R.id.angle3);
        angle4 = (TextView)v.findViewById(R.id.angle4);
        angle5 = (TextView)v.findViewById(R.id.angle5);
        angle6 = (TextView)v.findViewById(R.id.angle6);
        angle7 = (TextView)v.findViewById(R.id.angle7);
        angle8 = (TextView)v.findViewById(R.id.angle8);

        angles.add(angle1);
        angles.add(angle2);
        angles.add(angle3);
        angles.add(angle4);
        angles.add(angle5);
        angles.add(angle6);
        angles.add(angle7);
        angles.add(angle8);

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
        if (activeSensors != null) {
            if (index > 7 || index < 0) {
                throw new Error("Index of " + String.valueOf(index) + " when the maximum number of sensors is 8");
            }
            return activeSensors[index];
        }
        return false;
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

    public synchronized void showData(){
        if (lastMeasure!=null) {
            for(int i=0; i<8; i++){
                if(isSensorActive(i) && angles != null && angles.size()==8){
                    angles.get(i).setText(String.valueOf(lastMeasure.getRowAngle(i)/10)+"°");
                }
            }
        }
    }

}
