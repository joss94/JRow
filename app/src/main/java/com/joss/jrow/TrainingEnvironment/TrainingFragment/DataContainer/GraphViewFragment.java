package com.joss.jrow.TrainingEnvironment.TrainingFragment.DataContainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Position;
import com.joss.jrow.R;
import com.joss.jrow.SensorManager;

@SuppressWarnings("deprecation")
public class GraphViewFragment extends DataDisplayFragment {

    private GraphView graph;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_graph_view, parent, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        View v=getView();
        graph = (GraphView) v.findViewById(R.id.data_container);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-70.0);
        graph.getViewport().setMaxY(70.0);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(2);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getGridLabelRenderer().setNumVerticalLabels(10);

        graph.removeAllSeries();
        for(LineGraphSeries series : GraphData.getInstance()){
            graph.addSeries(series);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }


    private void initData(){
        graph.removeAllSeries();
        GraphData.getInstance().reset();
        for(LineGraphSeries series : GraphData.getInstance()){
            graph.addSeries(series);
        }
    }

    @Override
    public void registerForContextMenu(View view) {

    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        super.onNewMeasureProcessed(measure);
    }

    @Override
    public void onMovementChanged(final int index) {
        super.onMovementChanged(index);
        double time = Measures.getMeasures().getCatchTimes()[index];
        if (index == Position.STERN && SensorManager.getInstance().isSensorActive(Position.STERN)) {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            series.appendData(new DataPoint(time/1000, 500), true, 200);
            series.appendData(new DataPoint(time/1000, -500), true, 200);
            series.setColor(context.getResources().getColor(android.R.color.black));
            series.setThickness(3);
            graph.addSeries(series);
        }
    }

    @Override
    public void startTraining() {
        initData();
    }

    @Override
    public void stopTraining() {

    }

    @Override
    public void pauseTraining() {

    }

    @Override
    public void resumeTraining() {

    }
}
