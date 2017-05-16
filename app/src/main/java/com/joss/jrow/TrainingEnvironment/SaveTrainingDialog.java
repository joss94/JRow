package com.joss.jrow.TrainingEnvironment;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.joss.jrow.Models.Training;
import com.joss.jrow.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class SaveTrainingDialog extends AbstractDialogFragment {

    private EditText trainingName;
    private Training training;

    public SaveTrainingDialog() {
        training = Training.getTraining();
        setCancelable(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_save_training;
    }

    @Override
    public boolean callback() {
        String fileName = trainingName.getText().toString();
        if(fileName.isEmpty()){
            Toast.makeText(getContext(), R.string.select_training_name, Toast.LENGTH_SHORT).show();
            return false ;
        }
        if(fileName.contains("\\")){
            Toast.makeText(getContext(), "Invalid file name", Toast.LENGTH_SHORT).show();
            return false ;
        }
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, fileName);
        return true;
    }

    @Override
    public void findViews(View v){
        trainingName = (EditText) v.findViewById(R.id.training_name);
    }

    @Override
    public void setViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.FRANCE);
        trainingName.setText(sdf.format(training.getDate()));
    }
}
