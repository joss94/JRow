package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Position;
import com.joss.jrow.R;
import com.joss.jrow.SerialContent;

public class SerialViewFragment extends DataDisplayFragment {

    private static final int SERIAL_DISPLAY_DELAY = 0;
    private volatile TextView serial, currentMeasure;
    private SerialContent serialContent;

    private Handler serialDisplayHandler;

    private Runnable displaySerial = new Runnable() {
        @Override
        public void run() {
            serial.setText(serialContent.getSerial());
            serialDisplayHandler.postDelayed(displaySerial, SERIAL_DISPLAY_DELAY);
        }
    };

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        serialContent = SerialContent.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_serial_view, parent, false);

        serial = (TextView) v.findViewById(R.id.serial);
        currentMeasure = (TextView) v.findViewById(R.id.current_measure);
        serial.setText(serialContent.getSerial());

        serialDisplayHandler = new Handler();
        getActivity().runOnUiThread(displaySerial);

        return v;
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        super.onNewMeasureProcessed(measure);
        String result="";
        result += "Time: " + String.valueOf((double) (measure.getTime()- Measures.getMeasures().getStartTime())/1000) + "\n";
        for(int i=0; i<8; i++){
            result += i+": " + measure.getRowAngle(i) + "\n";
        }
        currentMeasure.setText(result);
    }

    @Override
    public void onMovementChanged(int index, long time) {
        super.onMovementChanged(index, time);
        if (index == Position.STERN) {
            serialContent.addToSerial("Catch detected at rower "+String.valueOf(index)+" at "+String.valueOf(time));
        }
    }

    @Override
    public void onStartTraining() {
    }

    @Override
    public void onStopTraining() {

    }
}
