package com.joss.jrow.Calibration;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingActivity;

import java.util.ArrayList;

public class CalibrationFragment extends Fragment implements Measures.OnNewMeasureProcessedListener, View.OnClickListener {

    private View okButton, backButton;
    private TextView instructions;

    private Measure measure;

    private ArrayList<String> rowersNames;

    private int step = 0;


    public CalibrationFragment() {
        rowersNames = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calibration, container, false);
        okButton = v.findViewById(R.id.OK_button);
        backButton = v.findViewById(R.id.back_button);
        instructions = (TextView) v.findViewById(R.id.instructions);

        okButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        instructions.setText(getContext().getString(R.string.rows_neutral));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            step = savedInstanceState.getInt("step");
        }
        switch(step){
            case 1:
                instructions.setText(getContext().getResources().getString(R.string.rows_back));
                break;

            case 2:
                instructions.setText(getContext().getResources().getString(R.string.rows_front));
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("step", step);
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        this.measure = measure;
    }

    @Override
    public void onMovementChanged(int index, long time) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.OK_button:
                switch(step){
                    case 0:
                        if (measure != null) {
                            Measures.getMeasures().setNeutralPosition(measure);
                            instructions.setText(R.string.rows_back);
                            step++;
                        }
                        break;

                    case 1:
                        if (measure != null) {
                            Measures.getMeasures().setBackPosition(measure);
                            for(int i=0; i<8; i++){
                                if(measure.getRawAngle(i)>=Measures.getMeasures().getNeutralPosition().getRawAngle(i)){
                                    Toast.makeText(getContext(), getContext().getString(R.string.calibration_wrong_angle_front, rowersNames.get(i)), Toast.LENGTH_SHORT).show();
                                }
                            }
                            instructions.setText(R.string.rows_front);
                            step++;
                        }
                        break;

                    case 2:
                        if (measure != null) {
                            Measures.getMeasures().setFrontPosition(measure);
                            for(int i=0; i<8; i++){
                                if(measure.getRawAngle(i)<=Measures.getMeasures().getBackPosition().getRawAngle(i)
                                        || measure.getRawAngle(i) <= Measures.getMeasures().getNeutralPosition().getRawAngle(i)){
                                    Toast.makeText(getContext(), getContext().getString(R.string.calibration_wrong_angle_behind, rowersNames.get(i)), Toast.LENGTH_SHORT).show();
                                }
                            }
                            ((TrainingActivity)getActivity()).calibrationFinished();
                        }
                        break;
                }
                break;

            case R.id.back_button:
                switch(step){
                    case 0:
                        getActivity().onBackPressed();
                        break;

                    case 1:
                        Measures.getMeasures().setBackPosition(null);
                        Measures.getMeasures().setFrontPosition(null);
                        Measures.getMeasures().setNeutralPosition(null);
                        instructions.setText(R.string.rows_neutral);
                        step--;
                        break;

                    case 2:
                        Measures.getMeasures().setFrontPosition(null);
                        Measures.getMeasures().setBackPosition(null);
                        instructions.setText(R.string.rows_back);
                        step--;
                        break;
                }
                break;
        }
    }

    public void setRowersNames(ArrayList<String> rowersNames) {
        this.rowersNames = rowersNames;
    }
}
