package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.SensorManager;
import com.joss.jrow.TrainingEnvironment.OnTrainingChangeListener;

public abstract class DataDisplayFragment extends Fragment implements
        Measures.OnNewMeasureProcessedListener,
        OnTrainingChangeListener{

    SensorManager sensorManager;
    private boolean ready;

    public DataDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        sensorManager = SensorManager.getInstance();
        ready = false;
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        ready = true;
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        if(!ready){
            return;
        }
    }

    @Override
    public void onMovementChanged(final int index, final long time) {
        if (!ready) {
            return;
        }
    }
}
