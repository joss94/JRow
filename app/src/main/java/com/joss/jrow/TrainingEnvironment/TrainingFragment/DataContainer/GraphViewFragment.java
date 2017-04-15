package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Position;
import com.joss.jrow.R;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class GraphViewFragment extends DataDisplayFragment {

    private final int[] colors = {Color.GRAY, Color.rgb(255,102,0), Color.BLUE, Color.MAGENTA, Color.BLACK, Color.rgb(0, 150, 0), Color.rgb(100, 50, 130)};

    private GraphView graph;
    private ArrayList<LineGraphSeries<DataPoint>> graphData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_graph_view, parent, false);

        graph = (GraphView) v.findViewById(R.id.data_container);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-5.0);
        graph.getViewport().setMaxY(100.0);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(10);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        initData();

        return v;
    }


    private void initData(){
        graphData = new ArrayList<>();
        for(int i=0; i<8; i++){
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            graphData.add(series);
            graph.addSeries(series);
            graphData.get(i).setColor((i<7)?colors[i]:getResources().getColor(R.color.red));
            graphData.get(i).setThickness(2);
        }
    }


    @Override
    public void onNewMeasureProcessed(Measure measure) {
        for(int i=0; i<1; i++){
            if (sensorManager.isSensorActive(i)) {
                try {
                    graphData.get(i).appendData(new DataPoint((double) (measure.getTime()- Measures.getMeasures().getStartTime())/1000, (float)measure.getRowAngle(i)/10), true, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMovementChanged(final int index, final long time) {
        if (index == Position.STERN) {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            series.appendData(new DataPoint((double) (time)/1000, 10000), true, 200);
            series.appendData(new DataPoint((double) (time)/1000, -10000), true, 200);
            series.setColor(getResources().getColor(android.R.color.black));
            series.setThickness(3);
            graph.addSeries(series);
        }
    }

    @Override
    public void onStartTraining() {

    }

    @Override
    public void onStopTraining() {

    }
}
