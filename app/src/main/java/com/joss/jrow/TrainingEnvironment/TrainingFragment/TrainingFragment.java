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
    public void onPause(){
        super.onPause();
        ready = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
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
            controlerFragment.onNewMeasureProcessed(measure);

            if(isResumed()){
                displayFragment.onNewMeasureProcessed(measure);
                //if(wait>= SensorManager.getInstance().numberOfActiveSensors()){

                if(true){
                    wait=0;
                    for(int i=0; i<8; i++){
                        if (SensorManager.getInstance().isSensorActive(i)) {
                            GraphData.getInstance().get(i).appendData(new DataPoint((double) (measure.getTime())/1000, measure.getAngle(i)), true, 200);

                        }
                    }
                }
                wait++;
            }
        }
    }

    @Override
    public void onMovementChanged(int index){
        if (ready) {
            tableFragment.onMovementChanged(index);
            displayFragment.onMovementChanged(index);
            controlerFragment.onMovementChanged(index);
        }
    }

    @Override
    public void startTraining() {
        if(Training.getTraining().isPaused()){
            resumeTraining();
        }
        else{
            SerialContent.getInstance().addToSerial("Training started");
            Measures.getMeasures().wipeData();
            Measures.getMeasures().addOnNewMeasureProcessedListener((TrainingActivity)getActivity());
            Training.resetTraining();
            Training.getTraining().setPaused(false);
            Training.getTraining().setRecording(true);
            controlerFragment.startTraining();
            displayFragment.startTraining();
        }
    }

    @Override
    public void stopTraining() {
        Training.getTraining().setPaused(false);
        Training.getTraining().setRecording(false);
        SerialContent.getInstance().addToSerial("Training stopped");
        controlerFragment.stopTraining();
        displayFragment.stopTraining();
    }

    @Override
    public void pauseTraining() {
        Training.getTraining().setPaused(true);
        Training.getTraining().setRecording(false);
        SerialContent.getInstance().addToSerial("Training paused");
        controlerFragment.pauseTraining();
        displayFragment.pauseTraining();
    }

    @Override
    public void resumeTraining() {
        Training.getTraining().setPaused(false);
        Training.getTraining().setRecording(true);
        SerialContent.getInstance().addToSerial("Training resumed");
        controlerFragment.resumeTraining();
        displayFragment.resumeTraining();
    }
}
