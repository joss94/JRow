package com.joss.jrow.TrainingEnvironment.TrainingFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Position;
import com.joss.jrow.Models.Training;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingActivity;
import com.joss.jrow.TrainingEnvironment.TrainingControler;

import java.util.Locale;

public class TrainingControlerFragment extends Fragment implements
        View.OnClickListener,
        Measures.OnNewMeasureProcessedListener,
        TrainingControler{

    private volatile TextView strokeRateView;
    private volatile TextView timeView;
    private RelativeLayout stopButton, startButton, pauseButton, calibrateButton;

    private TrainingControler listener;

    private Context context;

    public TrainingControlerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_training_controler, container, false);

        strokeRateView = (TextView) v.findViewById(R.id.stroke_rate);
        timeView = (TextView) v.findViewById(R.id.time);
        startButton = (RelativeLayout) v.findViewById(R.id.start_button);
        pauseButton = (RelativeLayout) v.findViewById(R.id.pause_button);
        stopButton = (RelativeLayout) v.findViewById(R.id.stop_button);
        calibrateButton = (RelativeLayout) v.findViewById(R.id.calibrate_button);

        startButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        calibrateButton.setOnClickListener(this);


        if(!Training.getTraining().isPaused() && Training.getTraining().isRecording()){
            startTraining();
        }

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(!(context instanceof TrainingControler)){
            throw new Error("TrainingFragment parent ativity must implement TrainingControler");
        }
        listener = (TrainingControler) context;
        this.context = context.getApplicationContext();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_button:
                listener.startTraining();
                break;

            case R.id.pause_button:
                listener.pauseTraining();
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
        timeView.setText(context.getString(R.string.time, (double)(measure.getTime())/1000));
    }

    @Override
    public void onMovementChanged(int index) {
        if (index == Position.STERN) {
            strokeRateView.setText(String.format(Locale.ENGLISH, context.getString(R.string.strokes_per_min), Measures.getMeasures().getStrokeRate()));
        }
    }

    @Override
    public void startTraining() {
        stopButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        calibrateButton.setVisibility(View.GONE);
    }

    @Override
    public void stopTraining() {
        calibrateButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.GONE);
    }

    @Override
    public void pauseTraining() {
        calibrateButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.GONE);
    }

    @Override
    public void resumeTraining() {
        startTraining();
    }
}
