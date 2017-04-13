package com.joss.jrow.TrainingEnvironment.GraphView;

import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Position;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

import java.util.ArrayList;

/*
 * Created by joss on 11/04/17.
 */

@SuppressWarnings("deprecation")
public class GraphViewFragment extends TrainingFragment {

    private final int[] colors = {Color.GRAY, Color.rgb(255,102,0), Color.BLUE, Color.MAGENTA, Color.BLACK, Color.rgb(0, 150, 0), Color.rgb(100, 50, 130)};

    private GraphView graph;
    private TableLayout table;
    protected static ArrayList<LineGraphSeries<DataPoint>> graphData;


    public static GraphViewFragment newInstance(){
        return new GraphViewFragment();
    }

    @Override
    protected void findViews(View v){
        graph = (GraphView) v.findViewById(R.id.data_container);
        table = (TableLayout) v.findViewById(R.id.table);
    }

    @Override
    protected void setViews(){
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
            graphData.get(i).setColor((i<7)?colors[i]:getResources().getColor(R.color.red));
            namesLabels.get(i).setTextColor((i<7)?colors[i]:getResources().getColor(R.color.red));
            graphData.get(i).setThickness(2);
        }
    }


    @Override
    public void onMovementChanged(final boolean ascending, final int index, final long time) {
        super.onMovementChanged(ascending, index, time);
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
    public synchronized void showData() {
        super.showData();
        for(int i=0; i<1; i++){
            if (isSensorActive(i)) {
                try {
                    graphData.get(i).appendData(new DataPoint((double) (lastMeasure.getTime()- Measures.getMeasures().getStartTime())/1000, (float)lastMeasure.getRowAngle(i)/10), true, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
