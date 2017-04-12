package com.joss.jrow.TrainingEnvironment.SerialView;

import android.view.View;
import android.widget.TextView;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

/*
 * Created by joss on 12/04/17.
 */

public class SerialViewFragment extends TrainingFragment {

    String serialContent;
    TextView serial;
    TextView measure;

    public static SerialViewFragment newInstance(){
        SerialViewFragment fr = new SerialViewFragment();
        return fr;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_serial_view;
    }

    @Override
    protected void findViews(View v) {
        serial = (TextView) v.findViewById(R.id.serial);
        measure = (TextView) v.findViewById(R.id.measure);
    }

    @Override
    protected void setViews() {

    }

    @Override
    public void onMovementChanged(boolean ascending, int index, long time) {
        displaySerial("Catch detected at rower "+String.valueOf(index)+" at "+String.valueOf(time));
    }

    @Override
    public void updateData(Measure measure) {
        String result="";
        result += "Time: " + String.valueOf((double) (measure.getTime()- Measures.getMeasures().getStartTime())/1000) + "\n";
        for(int i=0; i<8; i++){
            result += i+": " + measure.getRowAngle(i) + "\n";
        }
        this.measure.setText(result);
    }

    public void displaySerial(String message) {
        serialContent += message + "\n";
        serial.setText(serialContent);
    }
}
