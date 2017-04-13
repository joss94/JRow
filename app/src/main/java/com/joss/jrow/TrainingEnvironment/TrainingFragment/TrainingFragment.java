package com.joss.jrow.TrainingEnvironment.TrainingFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.DataDisplayFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.GraphViewFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.LoadbarViewFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.SerialViewFragment;
import java.util.ArrayList;
import java.util.List;

public class TrainingFragment extends Fragment implements Measures.OnNewMeasureProcessedListener{

    private boolean training = false;
    private boolean paused = false;

    private List<String> rowersNames;

    private TrainingTableFragment tableFragment;
    private TrainingControlerFragment controlerFragment;
    private DataDisplayFragment displayFragment;

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        if (rowersNames == null) {
            rowersNames = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_training, parent, false);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        tableFragment = new TrainingTableFragment();
        tableFragment.setRowersNames(rowersNames);
        controlerFragment = new TrainingControlerFragment();
        displayFragment = new SerialViewFragment();

        fm.beginTransaction().replace(R.id.table_fragment, tableFragment).disallowAddToBackStack().commit();
        fm.beginTransaction().replace(R.id.controler_fragment, controlerFragment).disallowAddToBackStack().commit();
        fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();

        return v;
    }

    public void setGraphView(){
        displayFragment = new GraphViewFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
    }

    public void setLoadbarView(){
        displayFragment = new LoadbarViewFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
    }

    public void setSerialView(){
        displayFragment = new SerialViewFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        tableFragment.onNewMeasureProcessed(measure);
        displayFragment.onNewMeasureProcessed(measure);
        controlerFragment.onNewMeasureProcessed(measure);
    }

    @Override
    public void onMovementChanged(int index, long time){
        tableFragment.onMovementChanged(index, time);
        displayFragment.onMovementChanged(index, time);
        controlerFragment.onMovementChanged(index, time);
    }

    public void setTraining(boolean training) {
        this.training = training;
        if(training){
            controlerFragment.onStartTraining();
            paused = false;
        }
        else{
            controlerFragment.onStopTraining();
        }
    }

    public boolean isTraining() {
        return training;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setRowersNames(List<String> rowersNames) {
        this.rowersNames = new ArrayList<>();
        this.rowersNames.addAll(rowersNames);
    }
}
