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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joss.jrow.CalibrationActivity;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Position;
import com.joss.jrow.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Created by joss on 11/04/17.
 */

public abstract class TrainingFragment extends Fragment implements View.OnClickListener {

    protected volatile TextView strokeRateView, timeView;
    private ImageView stopButton, startAndPauseButton, calibrateButton;

    protected static boolean training =false;

    protected TrainingFragmentControler listener;

    protected static volatile String serialContent;
    protected static volatile Measure lastMeasure;

    protected static boolean[] activeSensors;
    protected static List<String> rowersNames;

    protected volatile List<CheckBox> checkBoxes;
    protected volatile List<TextView> delays;
    protected List<TextView> namesLabels;

    protected static volatile long[] catchTimes;

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        if(activeSensors == null){
            activeSensors = new boolean[] {false, false, false, false, false, false, false, false};
        }
        if (rowersNames == null) {
            rowersNames = new ArrayList<>();
        }
        if(catchTimes == null){
            catchTimes = new long[] {0,0,0,0,0,0,0,0};
        }
        checkBoxes = new ArrayList<>();
        delays = new ArrayList<>();
        namesLabels = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        View v = inflater.inflate(getLayoutID(), parent, false);

        View border= new View(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.drawer_border_size), RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        border.setLayoutParams(params);
        border.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));

        ((RelativeLayout)v).addView(border);

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

        timeView = (TextView) v.findViewById(R.id.time);
        if(timeView == null){
            throw new Error("Fragment must contain TextView with time id within its layout");
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

        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox1));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox2));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox3));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox4));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox5));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox6));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox7));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox8));

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

        delays.add((TextView)v.findViewById(R.id.angle1));
        delays.add((TextView)v.findViewById(R.id.angle2));
        delays.add((TextView)v.findViewById(R.id.angle3));
        delays.add((TextView)v.findViewById(R.id.angle4));
        delays.add((TextView)v.findViewById(R.id.angle5));
        delays.add((TextView)v.findViewById(R.id.angle6));
        delays.add((TextView)v.findViewById(R.id.angle7));
        delays.add((TextView)v.findViewById(R.id.angle8));

        namesLabels.add((TextView)v.findViewById(R.id.name1));
        namesLabels.add((TextView)v.findViewById(R.id.name2));
        namesLabels.add((TextView)v.findViewById(R.id.name3));
        namesLabels.add((TextView)v.findViewById(R.id.name4));
        namesLabels.add((TextView)v.findViewById(R.id.name5));
        namesLabels.add((TextView)v.findViewById(R.id.name6));
        namesLabels.add((TextView)v.findViewById(R.id.name7));
        namesLabels.add((TextView)v.findViewById(R.id.name8));

        for(TextView nameLabel : namesLabels){
            String name = rowersNames.get(namesLabels.indexOf(nameLabel));
            nameLabel.setText(name);
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

    public void onMovementChanged(boolean ascending, int index, long time){
        if(index == Position.STERN){
            float frequency = (float)60000/(((float)(time-catchTimes[Position.STERN])));
            strokeRateView.setText(String.format(Locale.ENGLISH, getString(R.string.strokes_per_min), frequency));
        }
        catchTimes[index] = time;
        if (isSensorActive(index)) {
            if(index == Position.STERN){
                delays.get(index).setText(String.valueOf((double)time/1000));
                for(int i=0; i<8; i++){
                    if(time - catchTimes[Position.STERN]<1000 && isSensorActive(i)){
                        delays.get(index).setText(String.format(Locale.ENGLISH, getString(R.string.delay), ((double)(time - catchTimes[Position.STERN]))/1000));
                    }
                }
            }
            else{
                if(time - catchTimes[Position.STERN]<1000){
                    delays.get(index).setText(String.format(Locale.ENGLISH, getString(R.string.delay), ((double)(time - catchTimes[Position.STERN]))/1000));
                }
            }
        }
    }

    public synchronized void showData(){
    }

}
