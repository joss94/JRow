package com.joss.jrow.PrepareTraining;

/*
 * Created by joss on 13/04/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingActivity;

import java.util.ArrayList;
import java.util.List;

public class PrepareTrainingActivity extends AppCompatActivity{

    private List<EditText> names;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_prepare_training);

        names = new ArrayList<>();

        names.add((EditText)findViewById(R.id.name1));
        names.add((EditText)findViewById(R.id.name2));
        names.add((EditText)findViewById(R.id.name3));
        names.add((EditText)findViewById(R.id.name4));
        names.add((EditText)findViewById(R.id.name5));
        names.add((EditText)findViewById(R.id.name6));
        names.add((EditText)findViewById(R.id.name7));
        names.add((EditText)findViewById(R.id.name8));
        names.add((EditText)findViewById(R.id.name_timo));

        /*
        for(EditText name : names){
            name.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }/**/

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> rowers = new ArrayList<>();
                for(EditText name:names){
                    if(!name.getText().toString().isEmpty()){
                        rowers.add(name.getText().toString());
                    }
                    else{
                        rowers.add(name.getHint().toString());
                    }
                }

                Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
                intent.putExtra("rowers", rowers);
                startActivity(intent);
            }
        });

    }

}
