package com.joss.jrow.TrainingEnvironment.TrainingFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.series.DataPoint;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Training;
import com.joss.jrow.R;
import com.joss.jrow.SensorManager;
import com.joss.jrow.SerialContent;
import com.joss.jrow.TrainingEnvironment.TrainingActivity;
import com.joss.jrow.TrainingEnvironment.TrainingControler;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.DataDisplayFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.GraphData;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.GraphViewFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.LoadbarViewFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.RaceViewFragment;
import com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer.SerialViewFragment;

public class TrainingFragment extends Fragment implements
        Measures.OnNewMeasureProcessedListener,
        TrainingControler{

    private static boolean paused = false;
    private static boolean recording = false;
    private boolean ready;

    private TrainingTableFragment tableFragment;
    private TrainingControlerFragment controlerFragment;
    private DataDisplayFragment displayFragment;

    private FragmentManager fm;

    private int wait = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        ready=false;

        return inflater.inflate(R.layout.fragment_training, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (fm == null) {
            fm = getActivity().getSupportFragmentManager();
        }
        //if(false){
        if(savedInstanceState != null){
            controlerFragment = (TrainingControlerFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState, "CONTROLER_FRAGMENT");
            displayFragment = (DataDisplayFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState, "DISPLAY_FRAGMENT");
            tableFragment = (TrainingTableFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState, "TABLE_FRAGMENT");
        }
        else{
            controlerFragment = new TrainingControlerFragment();
            tableFragment = new TrainingTableFragment();
            displayFragment = new GraphViewFragment();
        }
        fm.beginTransaction().replace(R.id.table_fragment, tableFragment).disallowAddToBackStack().commit();
        fm.beginTransaction().replace(R.id.controler_fragment, controlerFragment).disallowAddToBackStack().commit();
        fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();

        //((TrainingActivity)getActivity()).goToGraphView();
    }

    @Override
    public void onResume(){
        super.onResume();
        ready=true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean("paused", paused);
        getActivity().getSupportFragmentManager().putFragment(outState, "CONTROLER_FRAGMENT", controlerFragment);
        getActivity().getSupportFragmentManager().putFragment(outState, "TABLE_FRAGMENT", tableFragment);
        getActivity().getSupportFragmentManager().putFragment(outState, "DISPLAY_FRAGMENT", displayFragment);
    }

    public void setGraphView(){
        if(!(displayFragment instanceof GraphViewFragment)){
            displayFragment = new GraphViewFragment();
        }
        if (fm != null) {
            fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
        }
    }

    public void setLoadbarView(){
        if (!(displayFragment instanceof LoadbarViewFragment)) {
            displayFragment = new LoadbarViewFragment();
        }
        if (fm != null) {
            fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
        }
    }

    public void setRaceView() {
        if (!(displayFragment instanceof RaceViewFragment)) {
            displayFragment = new RaceViewFragment();
        }
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
    }

    public void setSerialView(){
        if (!(displayFragment instanceof SerialViewFragment)) {
            displayFragment = new SerialViewFragment();
        }
        if (fm != null) {
            fm.beginTransaction().replace(R.id.display_fragment, displayFragment).disallowAddToBackStack().commit();
        }
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        if (ready) {
            tableFragment.onNewMeasureProcessed(measure);
            displayFragment.onNewMeasureProcessed(measure);
            controlerFragment.onNewMeasureProcessed(measure);

            if(wait>=3){
                wait=0;
                for(int i=0; i<8; i++){
                    if (SensorManager.getInstance().isSensorActive(i)) {
                        try {
                            GraphData.getInstance().get(i).appendData(new DataPoint((double) (measure.getTime()- Measures.getMeasures().getStartTime())/1000, measure.getAngle(i)), true, 200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            wait++;
        }
    }

    @Override
    public void onMovementChanged(int index, long time){
        if (ready) {
            tableFragment.onMovementChanged(index, time);
            displayFragment.onMovementChanged(index, time);
            controlerFragment.onMovementChanged(index, time);
        }
    }

    @Override
    public void startTraining() {
        if(paused){
            resumeTraining();
        }
        else{
            paused = false;
            recording = true;
            SerialContent.getInstance().addToSerial("Training started");
            Measures.getMeasures().wipeData();
            Measures.getMeasures().setOnNewMeasureProcessedListener((TrainingActivity)getActivity());
            Training.resetTraining();
            controlerFragment.startTraining();
            displayFragment.startTraining();
        }
    }

    @Override
    public void stopTraining() {
        paused = false;
        recording = false;
        SerialContent.getInstance().addToSerial("Training stopped");
        controlerFragment.stopTraining();
        displayFragment.stopTraining();
    }

    @Override
    public void pauseTraining() {
        paused=true;
        recording = false;
        SerialContent.getInstance().addToSerial("Training paused");
        controlerFragment.pauseTraining();
        displayFragment.pauseTraining();
    }

    @Override
    public void resumeTraining() {
        paused = false;
        recording = true;
        SerialContent.getInstance().addToSerial("Training resumed");
        controlerFragment.resumeTraining();
        displayFragment.resumeTraining();
    }

    public static boolean isPaused() {
        return paused;
    }

    public static boolean isRecording() {
        return recording;
    }
}
