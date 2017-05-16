package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.SensorManager;
import com.joss.jrow.TrainingEnvironment.TrainingControler;

public abstract class DataDisplayFragment extends Fragment implements
        Measures.OnNewMeasureProcessedListener,
        TrainingControler{

    SensorManager sensorManager;
    private boolean ready;
    protected Context context;

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
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onStart(){
        super.onStart();
        ready = true;
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        if(!ready){
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    @Override
    public void onMovementChanged(final int index, final long time, double angle) {
        if (!ready) {
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }
}
