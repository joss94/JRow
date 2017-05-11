package com.joss.jrow.PrepareTraining;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.joss.jrow.Models.Session;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PrepareTrainingActivity extends AppCompatActivity{

    private List<EditText> names;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_prepare_training);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/JuliusSansOne-Regular.ttf");
        ((TextView)findViewById(R.id.crew)).setTypeface(custom_font);

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

        if(Session.getSession().getRowers() != null && Session.getSession().getRowers().size()>8){
            for(int i =0; i<9; i++){
                names.get(i).setText(Session.getSession().getRowers().get(i));
            }
        }

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
                Session.getSession().setRowers(rowers);
                Session.getSession().setDate(Calendar.getInstance().getTime());

                Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
                startActivity(intent);
            }
        });

    }

}
