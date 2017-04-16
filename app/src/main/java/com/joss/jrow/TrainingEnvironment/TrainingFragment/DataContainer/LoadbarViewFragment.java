package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.R;

import java.util.ArrayList;

public class LoadbarViewFragment extends DataDisplayFragment{

    private volatile ArrayList<View> barLimits;
    private volatile ArrayList<View> barCatches;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_loadbar_view, parent, false);

        barLimits = new ArrayList<>();
        barLimits.add(v.findViewById(R.id.bar8limit));
        barLimits.add(v.findViewById(R.id.bar7limit));
        barLimits.add(v.findViewById(R.id.bar6limit));
        barLimits.add(v.findViewById(R.id.bar5limit));
        barLimits.add(v.findViewById(R.id.bar4limit));
        barLimits.add(v.findViewById(R.id.bar3limit));
        barLimits.add(v.findViewById(R.id.bar2limit));
        barLimits.add(v.findViewById(R.id.bar1limit));

        barCatches = new ArrayList<>();
        barCatches.add(v.findViewById(R.id.bar8catch));
        barCatches.add(v.findViewById(R.id.bar7catch));
        barCatches.add(v.findViewById(R.id.bar6catch));
        barCatches.add(v.findViewById(R.id.bar5catch));
        barCatches.add(v.findViewById(R.id.bar4catch));
        barCatches.add(v.findViewById(R.id.bar3catch));
        barCatches.add(v.findViewById(R.id.bar2catch));
        barCatches.add(v.findViewById(R.id.bar1catch));


        return v;
    }


    @Override
    public void onNewMeasureProcessed(Measure measure) {
        super.onNewMeasureProcessed(measure);
        if (barLimits != null) {
            for(View barLimit : barLimits){
                int position = barLimits.indexOf(barLimit);
                View barCatch = barCatches.get(position);
                if (sensorManager.isSensorActive(position)) {
                    int maxMargin = (int) (0.9*((RelativeLayout)barLimit.getParent()).getMeasuredWidth()/2);
                    //*
                    int margin = (int) (maxMargin*(1-(float)measure.getRowAngle(position)/1000));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) barLimit.getLayoutParams();
                    if(position%2 == 0){
                        params.setMarginStart(margin);
                        barLimit.setLayoutParams(params);
                        barLimit.invalidate();
                        if(barLimit.getX() < barCatch.getX()){
                            barCatch.setX(barLimit.getX());
                            barCatch.invalidate();
                        }
                    }else{
                        params.setMarginEnd(margin);
                        barLimit.setLayoutParams(params);
                        barLimit.invalidate();
                        if(barLimit.getX() > barCatch.getX()){
                            barCatch.setX(barLimit.getX());
                            barCatch.invalidate();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMovementChanged(int index, long time) {
        super.onMovementChanged(index, time);
        if(sensorManager.isSensorActive(index)){
            barCatches.get(index).setX(barCatches.get(index).getX());
        }
    }

    @Override
    public void onStartTraining() {

    }

    @Override
    public void onStopTraining() {
        onNewMeasureProcessed(new Measure());
    }
}
