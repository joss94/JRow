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

    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_save_training;
    }

    @Override
    public boolean callback() {
        String fileName = trainingName.getText().toString();
        if(fileName.isEmpty()){
            Toast.makeText(getContext(), "Please select a name for your training", Toast.LENGTH_SHORT).show();
            return false ;
        }
        listener.onFragmentInteraction(AppCompatActivity.RESULT_OK, getRequestCode(), fileName);
        return true;
    }

    @Override
    public void findViews(View v){
        trainingName = (EditText) v.findViewById(R.id.training_name);
    }

    @Override
    public void setViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm", Locale.ENGLISH);
        trainingName.setText(sdf.format(training.getDate()));
    }

    public void setTraining(Training training) {
        this.training = training;
    }
}
