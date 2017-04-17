package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;

import static android.view.View.GONE;

public class RaceViewFragment extends DataDisplayFragment implements View.OnClickListener {

    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private View askForConnect;
    private View startScreen;
    private TextView timeView;

    private boolean racing = false;
    private long startTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_race_view, parent, false);

        askForConnect = v.findViewById(R.id.ask_for_connect);
        startScreen = v.findViewById(R.id.start_screen);
        timeView = (TextView) v.findViewById(R.id.time);
        View startRaceButton = v.findViewById(R.id.start_race_button);
        View endRaceButton = v.findViewById(R.id.end_race_button);

        startRaceButton.setOnClickListener(this);
        endRaceButton.setOnClickListener(this);

        graph = (GraphView) v.findViewById(R.id.data_container);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0.0);
        graph.getViewport().setMaxY(50.0);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(30);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);

        series = new LineGraphSeries<>();
        graph.addSeries(series);
        series.setColor(context.getResources().getColor(R.color.red));
        series.setThickness(2);

        return v;
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        super.onNewMeasureProcessed(measure);
        if(startTime == 0 && racing){
            startTime = measure.getTime();
        }
        if (racing) {
            long time = measure.getTime()- startTime;
            series.appendData(new DataPoint((double) time/1000, Measures.getMeasures().getStrokeRate()), true, 2000);
            int millis = (int) (time % 1000);
            int seconds = (int) ((time/1000) % 60);
            int minutes = (int)(time/60000);
            timeView.setText(context.getResources().getString(R.string.race_time_format, minutes, seconds, millis));
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_race_button:
                startRace();
                break;

            case R.id.end_race_button:
                endRace();
                break;
        }
    }

    private void startRace() {
        startScreen.setVisibility(GONE);
        racing = true;
        startTime = 0;
    }

    private void endRace() {
        startScreen.setVisibility(View.VISIBLE);
        racing = false;
        startTime = 0;
        series = new LineGraphSeries<>();
        graph.removeAllSeries();
        graph.addSeries(series);
    }

    @Override
    public void startTraining() {
        askForConnect.setVisibility(GONE);
    }

    @Override
    public void stopTraining() {
        askForConnect.setVisibility(View.VISIBLE);
        endRace();
    }

    @Override
    public void pauseTraining() {
        askForConnect.setVisibility(View.VISIBLE);
        endRace();
    }

    @Override
    public void resumeTraining() {
        askForConnect.setVisibility(GONE);
    }
}
