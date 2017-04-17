package com.joss.jrow.TrainingEnvironment;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.joss.jrow.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

/**
 * Created by joss on 17/04/17.
 */

public class RequestCalibrationDialog extends AbstractDialogFragment {
    @Override
    public int getLayoutId() {
        return R.layout.dialog_request_calibration;
    }

    @Override
    public boolean callback() {
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK);
        return true;
    }

    @Override
    public void findViews(View view) {

    }

    @Override
    public void setViews() {

    }
}
