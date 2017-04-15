package com.joss.jrow.Calibration;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingActivity;

public class CalibrationFragment extends Fragment implements Measures.OnNewMeasureProcessedListener, View.OnClickListener {

    private View ok_button;
    private TextView instructions;

    private Measure measure;

    private int step = 0;


    public CalibrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calibration, container, false);
        ok_button = v.findViewById(R.id.OK_button);
        instructions = (TextView) v.findViewById(R.id.instructions);

        ok_button.setOnClickListener(this);

        return v;
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
                        Measures.getMeasures().setBackPosition(measure);
                        instructions.setText(R.string.rows_front);
                        break;

                    case 1:
                        Measures.getMeasures().setFrontPosition(measure);
                        instructions.setText(R.string.rows_neutral);
                        break;

                    case 2:
                        Measures.getMeasures().setNeutralPosition(measure);
                        ((TrainingActivity)getActivity()).cabrationFinished();
                        break;
                }
                break;
        }
    }
}
