package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.graphics.Color;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class GraphData extends ArrayList<LineGraphSeries<DataPoint>> {

    private static final long serialVersionUID = -3630374071534032353L;
    public static final int[] colors = {Color.GRAY, Color.rgb(255,102,0), Color.BLUE, Color.MAGENTA, Color.BLACK, Color.rgb(0, 150, 0), Color.rgb(100, 50, 130), Color.RED};

    private static GraphData ourInstance = new GraphData();

    public static GraphData getInstance() {
        return ourInstance;
    }

    private GraphData() {
        for(int i=0; i<8; i++){
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            series.appendData(new DataPoint(0,0), true, 5000);
            add(series);
            get(i).setColor((i<7)?colors[i]:Color.RED);
            get(i).setThickness(2);
        }
    }

    public void reset() {
        GraphData.ourInstance = new GraphData();
    }
}
