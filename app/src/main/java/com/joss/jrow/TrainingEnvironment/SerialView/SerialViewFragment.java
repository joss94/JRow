package com.joss.jrow.TrainingEnvironment.SerialView;

import android.view.View;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

/**
 * Created by joss on 12/04/17.
 */

public class SerialViewFragment extends TrainingFragment {

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_serial_view;
    }

    @Override
    protected void findViews(View v) {

    }

    @Override
    protected void setViews() {

    }

    @Override
    public void onMovementChanged(boolean ascending, int index, long time) {

    }

    @Override
    public void updateData(Measure measure) {

    }
}
