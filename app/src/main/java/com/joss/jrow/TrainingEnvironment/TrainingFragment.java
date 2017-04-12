package com.joss.jrow.TrainingEnvironment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joss.jrow.R;

import java.util.List;

/*
 * Created by joss on 11/04/17.
 */

public abstract class TrainingFragment extends Fragment{

    private int layoutID;

    protected TextView strokeRateView;
    private ImageView stopButton, startAndPauseButton, calibrateButton;

    protected List<String> rowersNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        View v = inflater.inflate(layoutID, parent, true);

        findViews(v);
        setViews();

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(!(context instanceof TrainingActivity){
            throw new Error("TrainingFragments can only be used inside a TrainingActivity");
        }
        rowersNames = ((TrainingActivity)getActivity()).getRowersNames();
    }


    protected void findViews(View v){
        strokeRateView = (TextView) v.findViewById(R.id.stroke_rate_view);
        if(strokeRateView == null){
            throw new Error("Fragment must contain TextView with stroke_rate_view id within its layout");
        }

        startAndPauseButton = (ImageView) v.findViewById(R.id.start_and_pause_button);
        if(startAndPauseButton == null){
            throw new Error("Fragment must contain ImageView with start_and_pause_button id within its layout");
        }

        stopButton = (ImageView) v.findViewById(R.id.stop_button);
        if(stopButton == null){
            throw new Error("Fragment must contain ImageView with stop_button id within its layout");
        }

        calibrateButton = (ImageView) v.findViewById(R.id.calibrate_button);
        if(calibrateButton == null){
            throw new Error("Fragment must contain ImageView with calibrate_button id within its layout");
        }
    }


    protected void setViews() {
        startAndPauseButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_rowing));
        calibrateButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_calibrate));
        stopButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_action_playback_stop));
    }

    public void setLayoutID(int layoutID) {
        this.layoutID = layoutID;
    }

    public boolean isActive(int index){
        ((TrainingActivity)getActivity()).isActive(index);
    }

    public void activateSensor(int index){
        ((TrainingActivity)getActivity()).activateSensor(index);
    }

    public void deactivateSensor(int index){
        ((TrainingActivity)getActivity()).deactivateSensor(index);
    }
}
