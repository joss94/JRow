package com.joss.jrow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Training;

public class CalibrationActivity extends AppCompatActivity implements View.OnClickListener, Measures.OnNewMeasureProcessedListener {

    private Measure measure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        findViewById(R.id.OK_button).setOnClickListener(this);
        Measures.getMeasures().addOnNewMeasureProcessedListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Measures.getMeasures().removeOnNewMeasureProcessedListener(this);
        Training.resetTraining();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.OK_button:
                if (measure != null) {
                    Measures.getMeasures().setNeutralPosition(measure);
                    SharedPreferences sharedPrefs = getSharedPreferences("JROW_CALIB", MODE_PRIVATE);
                    for(int i=0; i<8; i++){
                        sharedPrefs.edit().putLong("calib"+i, measure.getRawAngle(i)).apply();
                    }
                }
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        this.measure = measure;
    }

    @Override
    public void onMovementChanged(int index, long time, double angle) {

    }
}
