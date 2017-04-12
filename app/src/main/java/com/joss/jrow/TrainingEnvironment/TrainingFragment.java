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

    protected String[] rowersNames = new String[8];

    protected boolean receivingData=false;

    protected TrainingFragmentControler listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        View v = inflater.inflate(getLayoutID(), parent, true);
        findAndSetViews(v);

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(!(context instanceof TrainingFragmentControler)){
            throw new Error("TrainingFragment parent ativity must implement TrainingFragmentControler");
        }
        rowersNames = ((TrainingActivity)getActivity()).getRowersNames();
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

        startAndPauseButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        calibrateButton.setOnClickListener(this);

        findViews(v);
        setViews();
    }

    public boolean isSensorActive(int index){
        return ((TrainingActivity)getActivity()).isSensorActive(index);
    }

    public void activateSensor(int index){
        ((TrainingActivity)getActivity()).activateSensor(index);
    }

    public void deactivateSensor(int index){
        ((TrainingActivity)getActivity()).deactivateSensor(index);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_and_pause_button:
                if(receivingData){
                    listener.stopTraining();
                    stopButton.setVisibility(View.INVISIBLE);
                    calibrateButton.setVisibility(View.VISIBLE);
                    startAndPauseButton.setImageResource(R.drawable.ic_rowing);
                }
                else{
                    listener.startTraining();
                    stopButton.setVisibility(View.VISIBLE);
                    calibrateButton.setVisibility(View.INVISIBLE);
                    startAndPauseButton.setImageResource(R.drawable.ic_pause);
                }
                break;

            case R.id.stop_button:
                if(receivingData){
                    listener.startTraining();
                    calibrateButton.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.INVISIBLE);
                    startAndPauseButton.setImageResource(R.drawable.ic_rowing);
                }
                break;

            case R.id.calibrate_button:
                Intent intent = new Intent(getContext(), CalibrationActivity.class);
                startActivity(intent);
                break;
        }
    }


    protected abstract int getLayoutID();
    protected abstract void findViews(View v);
    protected abstract void setViews();
    public abstract void onMovementChanged(boolean ascending, int index, long time);
    public abstract void updateData(Measure measure);

}
