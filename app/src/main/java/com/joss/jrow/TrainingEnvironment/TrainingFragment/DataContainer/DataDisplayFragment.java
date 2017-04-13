package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.joss.jrow.Models.Measures;
import com.joss.jrow.SensorManager;

public abstract class DataDisplayFragment extends Fragment implements Measures.OnNewMeasureProcessedListener{

    SensorManager sensorManager;

    public DataDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        sensorManager = SensorManager.getInstance();
    }
}
