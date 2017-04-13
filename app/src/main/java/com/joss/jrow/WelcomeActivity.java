package com.joss.jrow;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.joss.jrow.PrepareTraining.PrepareTrainingActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TextView title = (TextView) findViewById(R.id.title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/JuliusSansOne-Regular.ttf");
        title.setTypeface(custom_font);

        ((TextView)findViewById(R.id.start_training_text)).setTypeface(custom_font);
        findViewById(R.id.start_training).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrepareTrainingActivity.class);
                startActivity(intent);
            }
        });
    }
}
