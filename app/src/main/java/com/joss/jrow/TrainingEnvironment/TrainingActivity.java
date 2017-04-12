package com.joss.jrow.TrainingEnvironment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.GraphView.GraphViewFragment;
import com.joss.utils.SlidingDrawer.DrawerMenuItem;
import com.joss.utils.SlidingDrawer.DrawerSlidingPane;
import com.joss.utils.SlidingDrawer.OnDrawerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class TrainingActivity extends AppCompatActivity implements OnDrawerItemClickListener {

    private DrawerSlidingPane drawer;

    private List<String> rowersNames;
    private boolean[] activeSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        drawer = (DrawerSlidingPane) findViewById(R.id.drawer);
        drawer.addDrawerItem(new DrawerMenuItem("Graph view", R.drawable.ic_action_line_chart, R.drawable.ic_action_line_chart_on));

        drawer.setOnDrawerItemClickListener(this);

        rowersNames = new ArrayList<>();
        activeSensors = new boolean[] {false, false, false, false, false, false, false, false};
    }


    @Override
    public void onDrawerItemClick(int i, DrawerMenuItem drawerMenuItem) {
        switch(i){
            case 0:
                drawer.replaceFragment(GraphViewFragment.newInstance(R.layout.fragment_graph_view), "GRAPH_VIEW");
                break;
        }
    }

    public List<String> getRowersNames() {
        return rowersNames;
    }

    public boolean isActive(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        return activeSensors[index];
    }

    public void activateSensor(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        activeSensors[index] = true;
    }

    public void deactivateSensor(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        activeSensors[index] = false;
    }
}
