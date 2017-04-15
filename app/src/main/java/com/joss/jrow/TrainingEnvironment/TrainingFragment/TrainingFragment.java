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
import com.joss.jrow.TrainingEnvironment.TrainingActivity;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.DataDisplayFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.GraphViewFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.LoadbarViewFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.RaceViewFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.SerialViewFragment;

import java.util.ArrayList;
import java.util.List;

public class TrainingFragment extends Fragment implements Measures.OnNewMeasureProcessedListener{

    private boolean recording = false;
    private boolean paused = false;

    private List<String> rowersNames;

    private TrainingTableFragment tableFragment;
    private TrainingControlerFragment controlerFragment;
    private DataDisplayFragment displayFragment;

    private FragmentManager fm;

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

        if (fm == null) {
            fm = getActivity().getSupportFragmentManager();
        }

        if (tableFragment == null) {
            tableFragment = new TrainingTableFragment();
            tableFragment.setRowersNames(rowersNames);
        }

        if (controlerFragment == null) {
            controlerFragment = new TrainingControlerFragment();
        }

        if (displayFragment == null) {
            displayFragment = new GraphViewFragment();
        }

        fm.beginTransaction().replace(R.id.table_fragment, tableFragment).disallowAddToBackStack().commit();
        fm.beginTransaction().replace(R.id.controler_fragment, controlerFragment).disallowAddToBackStack().commit();
        fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
        }
        else{
            ((TrainingActivity)getActivity()).goToGraphView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean("recording", recording);
        outState.putBoolean("paused", paused);
        getActivity().getSupportFragmentManager().putFragment(outState, "CONTROLER_FRAGMENT", controlerFragment);
        getActivity().getSupportFragmentManager().putFragment(outState, "TABLE_FRAGMENT", tableFragment);
        getActivity().getSupportFragmentManager().putFragment(outState, "DISPLAY_FRAGMENT", displayFragment);
    }

    public void setGraphView(){
        displayFragment = new GraphViewFragment();
        if (fm != null) {
            fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
        }
    }

    public void setLoadbarView(){
        displayFragment = new LoadbarViewFragment();
        if (fm != null) {
            fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
        }
    }

    public void setRaceView() {
        displayFragment = new RaceViewFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
    }

    public void setSerialView(){
        displayFragment = new SerialViewFragment();
        if (fm != null) {
            fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
        }
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

    public void setRecording(boolean recording) {
        this.recording = recording;
        if(recording){
            controlerFragment.onStartTraining();
            paused = false;
        }
        else{
            controlerFragment.onStopTraining();
        }
    }

    public boolean isRecording() {
        return recording;
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
