package com.joss.jrow.TrainingEnvironment.TrainingFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Position;
import com.joss.jrow.R;
import com.joss.jrow.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class TrainingTableFragment extends Fragment implements Measures.OnNewMeasureProcessedListener {

    private volatile List<CheckBox> checkBoxes;
    private volatile List<TextView> delays;
    private List<TextView> namesLabels;

    private ArrayList<String> rowersNames;

    private SensorManager sensorManager;
    private Measures measures;

    private Context context;

    public TrainingTableFragment() {
        checkBoxes = new ArrayList<>();
        delays = new ArrayList<>();
        namesLabels = new ArrayList<>();
        rowersNames = new ArrayList<>();

        sensorManager = SensorManager.getInstance();
        measures = Measures.getMeasures();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_training_table, container, false);

        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox1));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox2));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox3));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox4));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox5));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox6));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox7));
        checkBoxes.add((CheckBox) v.findViewById(R.id.checkbox8));

        for(int i=0; i<8; i++){
            final CheckBox checkBox = checkBoxes.get(i);
            if(sensorManager.isSensorActive(checkBoxes.indexOf(checkBox))){
                checkBox.setChecked(true);
            }
            else{
                checkBox.setChecked(false);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        sensorManager.activateSensor(checkBoxes.indexOf(checkBox));
                    }
                    else{
                        sensorManager.deactivateSensor(checkBoxes.indexOf(checkBox));
                    }
                }
            });
        }

        delays.add((TextView)v.findViewById(R.id.angle1));
        delays.add((TextView)v.findViewById(R.id.angle2));
        delays.add((TextView)v.findViewById(R.id.angle3));
        delays.add((TextView)v.findViewById(R.id.angle4));
        delays.add((TextView)v.findViewById(R.id.angle5));
        delays.add((TextView)v.findViewById(R.id.angle6));
        delays.add((TextView)v.findViewById(R.id.angle7));
        delays.add((TextView)v.findViewById(R.id.angle8));

        namesLabels.add((TextView)v.findViewById(R.id.name1));
        namesLabels.add((TextView)v.findViewById(R.id.name2));
        namesLabels.add((TextView)v.findViewById(R.id.name3));
        namesLabels.add((TextView)v.findViewById(R.id.name4));
        namesLabels.add((TextView)v.findViewById(R.id.name5));
        namesLabels.add((TextView)v.findViewById(R.id.name6));
        namesLabels.add((TextView)v.findViewById(R.id.name7));
        namesLabels.add((TextView)v.findViewById(R.id.name8));

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable("rowers_names", rowersNames);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey("rowers_names")){
            rowersNames = (ArrayList<String>) savedInstanceState.getSerializable("rowers_names");
        }

        if (rowersNames.size()>7) {
            for(TextView nameLabel : namesLabels){
                String name = rowersNames.get(namesLabels.indexOf(nameLabel));
                nameLabel.setText(name);
            }
        }
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
    }

    @Override
    public void onMovementChanged(int index, long time) {
        if (sensorManager.isSensorActive(index)) {
            if(index == Position.STERN){
                delays.get(index).setText(String.valueOf((double)time/1000));
                for(int i=0; i<8; i++){
                    if(time - measures.getCatchTimes()[Position.STERN]<700 && sensorManager.isSensorActive(i)){
                        delays.get(index).setText(context.getString(R.string.delay, (double)(time - measures.getCatchTimes()[Position.STERN])/1000));
                    }
                }
            }
            else{
                if(time - measures.getCatchTimes()[Position.STERN]<700){
                    delays.get(index).setText(context.getString(R.string.delay, ((double)(time - measures.getCatchTimes()[Position.STERN]))/1000));
                }
            }
        }
    }

    public void setRowersNames(ArrayList<String> rowersNames) {
        this.rowersNames = rowersNames;
    }
}
