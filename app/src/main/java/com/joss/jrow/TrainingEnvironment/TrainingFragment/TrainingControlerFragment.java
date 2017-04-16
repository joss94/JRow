package com.joss.jrow.TrainingEnvironment.TrainingFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.SerialContent;
import com.joss.jrow.TrainingEnvironment.OnTrainingChangeListener;
import com.joss.jrow.TrainingEnvironment.TrainingActivity;
import com.joss.jrow.TrainingEnvironment.TrainingControler;

import java.util.Locale;

public class TrainingControlerFragment extends Fragment implements
        View.OnClickListener,
        Measures.OnNewMeasureProcessedListener,
        OnTrainingChangeListener{

    private volatile TextView strokeRateView;
    private volatile TextView timeView;
    private ImageView stopButton, startAndPauseButton, calibrateButton;

    private TrainingControler listener;

    private Measures measures;

    public TrainingControlerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        measures = Measures.getMeasures();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_training_controler, container, false);

        strokeRateView = (TextView) v.findViewById(R.id.stroke_rate);
        timeView = (TextView) v.findViewById(R.id.time);
        startAndPauseButton = (ImageView) v.findViewById(R.id.start_and_pause_button);
        stopButton = (ImageView) v.findViewById(R.id.stop_button);
        calibrateButton = (ImageView) v.findViewById(R.id.calibrate_button);

        startAndPauseButton.setImageResource(R.drawable.ic_rowing);
        calibrateButton.setImageResource(R.drawable.ic_calibrate);
        stopButton.setImageResource(R.drawable.ic_action_playback_stop);

        startAndPauseButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        calibrateButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(!(context instanceof TrainingControler)){
            throw new Error("TrainingFragment parent ativity must implement TrainingControler");
        }
        listener = (TrainingControler) context;
    }

    public void onStartTraining(){
        if (stopButton != null && calibrateButton != null && startAndPauseButton != null) {
            stopButton.setVisibility(View.VISIBLE);
            calibrateButton.setVisibility(View.GONE);
            startAndPauseButton.setImageResource(R.drawable.ic_pause);
        }
        SerialContent.getInstance().addToSerial("Started training");
    }

    public void onStopTraining(){
        if (stopButton != null && calibrateButton != null && startAndPauseButton != null) {
            calibrateButton.setVisibility(View.VISIBLE);
            startAndPauseButton.setImageResource(R.drawable.ic_rowing);
        }
        SerialContent.getInstance().addToSerial("Stopped training");
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_and_pause_button:
                listener.startTraining();
                break;

            case R.id.stop_button:
                listener.stopTraining();
                break;

            case R.id.calibrate_button:
                ((TrainingActivity)getActivity()).calibrate();
                break;
        }
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        if (isAdded()) {
            timeView.setText(getContext().getResources().getString(R.string.time, (float)measure.getTime()/1000));
        }
    }

    @Override
    public void onMovementChanged(int index, long time) {
        strokeRateView.setText(String.format(Locale.ENGLISH, getString(R.string.strokes_per_min), Measures.getMeasures().getStrokeRate()));
    }
}
