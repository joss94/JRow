package com.joss.jrow.TrainingEnvironment.SerialView;

import android.view.View;
import android.widget.TextView;

import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

/*
 * Created by joss on 12/04/17.
 */

public class SerialViewFragment extends TrainingFragment {

    private volatile TextView serial;
    private volatile TextView measure;

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
    public synchronized void showData() {
        super.showData();

        if(serial != null){
            serial.setText(serialContent);
        }
    }
}
