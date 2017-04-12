package com.joss.jrow.TrainingEnvironment.GraphView;

import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

import java.util.ArrayList;

/*
 * Created by joss on 11/04/17.
 */

public class GraphViewFragment extends TrainingFragment {

    GraphView graph;
    ArrayList<LineGraphSeries<DataPoint>> data;

    public static GraphViewFragment newInstance(){
        GraphViewFragment fr = new GraphViewFragment();
        return fr;
    }

    @Override
    protected void findViews(View v){
        graph = (GraphView) v.findViewById(R.id.graph);
    }

    @Override
    protected void setViews(){
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-50.0);
        graph.getViewport().setMaxY(1500.0);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(10);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_graph_view;
    }

    private void initData(){
        data = new ArrayList<>();
        for(int i=0; i<8; i++){
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            data.add(series);
            graph.addSeries(series);
        }
        data.get(0).setColor(getResources().getColor(android.R.color.holo_green_dark));
    }


    @Override
    public void onMovementChanged(final boolean ascending, final int index, final long time) {
        if (index==0) {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            series.appendData(new DataPoint((double) (time)/1000, 10000), true, 200);
            series.appendData(new DataPoint((double) (time)/1000, -10000), true, 200);
            series.setColor(getResources().getColor(android.R.color.black));
            series.setThickness(3);
            graph.addSeries(series);
        }
    }

    @Override
    public void updateData(Measure measure) {
        for(int i=0; i<1; i++){
            if (isSensorActive(i)) {
                try {
                    data.get(i).appendData(new DataPoint((double) (measure.getTime()- Measures.getMeasures().getStartTime())/1000, measure.getRowAngle(i)), true, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
