package com.joss.jrow.TrainingEnvironment.SerialView;

import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

/*
 * Created by joss on 12/04/17.
 */

public class SerialViewFragment extends TrainingFragment {

    private TextView serial;
    private TextView measure;

    public static SerialViewFragment newInstance(){
        return new SerialViewFragment();
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
        if(serialContent != null && !serialContent.isEmpty()){
            serial.setText("");
            showData();
        }
    }

    @Override
    public void onMovementChanged(boolean ascending, int index, long time) {
        serialContent += "\n Catch detected at rower "+String.valueOf(index)+" at "+String.valueOf(time);
        showData();
    }

    @Override
    public void showData() {
        if (serial != null && measure != null) {
            if (lastMeasure != null) {
                String result="";
                result += "Time: " + String.valueOf((double) (lastMeasure.getTime()- Measures.getMeasures().getStartTime())/1000) + "\n";
                for(int i=0; i<8; i++){
                    result += i+": " + lastMeasure.getRowAngle(i) + "\n";
                }
                this.measure.setText(result);
            }

            serial.setText(serialContent);
            ((ScrollView)serial.getParent()).fullScroll(View.FOCUS_DOWN);
        }
    }
}
