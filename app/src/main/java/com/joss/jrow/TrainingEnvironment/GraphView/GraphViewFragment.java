package com.joss.jrow.TrainingEnvironment.GraphView;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

import java.util.ArrayList;

/*
 * Created by joss on 11/04/17.
 */

@SuppressWarnings("deprecation")
public class GraphViewFragment extends TrainingFragment {

    private GraphView graph;
    private TableLayout table;
    protected static ArrayList<LineGraphSeries<DataPoint>> graphData;


    public static GraphViewFragment newInstance(){
        return new GraphViewFragment();
    }

    @Override
    protected void findViews(View v){
        graph = (GraphView) v.findViewById(R.id.graph);
        table = (TableLayout) v.findViewById(R.id.table);
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
        initData();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_graph_view;
    }

    private void initData(){
        graphData = new ArrayList<>();
        for(int i=0; i<8; i++){
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            graphData.add(series);
            graph.addSeries(series);
        }
        graphData.get(0).setColor(getResources().getColor(R.color.colorAccent));
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
    public void showData() {
        for(int i=0; i<1; i++){
            if (isSensorActive(i)) {
                try {
                    graphData.get(i).appendData(new DataPoint((double) (lastMeasure.getTime()- Measures.getMeasures().getStartTime())/1000, lastMeasure.getRowAngle(i)), true, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for(int j=0; j<table.getChildCount(); j++){
                    ((TextView)((TableRow)table.getChildAt(j)).getChildAt(1)).setText(String.valueOf(lastMeasure.getRowAngle(j)));
                }
            }
        }
    }
}
