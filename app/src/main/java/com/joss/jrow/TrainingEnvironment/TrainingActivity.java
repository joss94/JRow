package com.joss.jrow.TrainingEnvironment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;

import com.joss.jrow.BluetoothConnectionActivity;
import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.GraphView.GraphViewFragment;
import com.joss.jrow.TrainingEnvironment.LoadbarView.LoadbarViewFragment;
import com.joss.jrow.TrainingEnvironment.SerialView.SerialViewFragment;
import com.joss.utils.SlidingDrawer.DrawerMenuItem;
import com.joss.utils.SlidingDrawer.DrawerSlidingPane;
import com.joss.utils.SlidingDrawer.OnDrawerItemClickListener;

public class TrainingActivity extends BluetoothConnectionActivity implements
        OnDrawerItemClickListener,
        Measures.OnNewMeasureProcessedListener,
        TrainingFragmentControler{

    private DrawerSlidingPane drawer;

    private static final int SERIAL_DISPLAY_DELAY = 0;

    private static String serialContent;

    boolean calibrated = false;

    private TrainingFragment fragment;
    private GraphViewFragment graphViewFragment;
    private SerialViewFragment serialViewFragment;
    private LoadbarViewFragment loadbarViewFragment;

    private Handler serialDisplayHandler;

    private Runnable displaySerial = new Runnable() {
        @Override
        public void run() {
            TrainingFragment.updateData(null, serialContent);
            serialViewFragment.showData();
            serialContent = "";
            serialDisplayHandler.postDelayed(displaySerial, SERIAL_DISPLAY_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        graphViewFragment = GraphViewFragment.newInstance();
        serialViewFragment = SerialViewFragment.newInstance();
        loadbarViewFragment = LoadbarViewFragment.newInstance();

        drawer = (DrawerSlidingPane) findViewById(R.id.drawer);
        drawer.addDrawerItem(new DrawerMenuItem("Graph view", R.drawable.ic_menu_graph, R.drawable.ic_menu_graph_on));
        drawer.addDrawerItem(new DrawerMenuItem("Serial graphData", R.drawable.ic_menu_serial, R.drawable.ic_menu_serial_on));
        drawer.addDrawerItem(new DrawerMenuItem("Loadbar view", R.drawable.ic_menu_loadbar, R.drawable.ic_menu_loadbar_on));

        drawer.setOnDrawerItemClickListener(this);

        drawer.goTo(0);

        Measures.getMeasures().addOnNewMeasureProcessedListener(this);

        serialContent="";

        serialDisplayHandler = new Handler();
        runOnUiThread(displaySerial);
    }

    @Override
    public void onResume(){
        super.onResume();
        calibrated = false;
        serialContent="";
    }


    public static synchronized void addToSerial(String message){
        serialContent += message;
        serialContent += '\n';
    }


    //<editor-fold desc="DRAWER INTERFACE">
    @Override
    public void onDrawerItemClick(int i, DrawerMenuItem drawerMenuItem) {
        switch(i){
            case 0:
                fragment = graphViewFragment;
                drawer.replaceFragment(fragment, "GRAPH_VIEW");
                break;

            case 1:
                fragment = loadbarViewFragment;
                drawer.replaceFragment(fragment, "LOADBAR_VIEW");
                break;

            case 2:
                fragment = serialViewFragment;
                drawer.replaceFragment(fragment, "SERIAL_VIEW");
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="BLUETOOTH INTERFACE">
    @Override
    protected void onDeviceFound(BluetoothDevice device) {
        addToSerial("Found device "+device.getName());
    }

    @Override
    protected void onConnectionError(String error) {
        addToSerial(error);
    }

    @Override
    protected void onDevicePaired(BluetoothDevice device) {
        addToSerial("Paired with "+device.getName());
    }

    @Override
    protected void onConnectionEstablished() {
        addToSerial("Connection established!");
    }
    //</editor-fold>

    //<editor-fold desc="TRAINING FRAGMENT CONTROLER INTERFACE">
    @Override
    public void startTraining() {
        Measures.getMeasures().wipeData();
        Measures.getMeasures().addOnNewMeasureProcessedListener(this);

        graphViewFragment.onStartTraining();
        loadbarViewFragment.onStartTraining();
        serialViewFragment.onStartTraining();

        connect();
    }

    @Override
    public void stopTraining() {
        graphViewFragment.onStopTraining();
        loadbarViewFragment.onStopTraining();
        serialViewFragment.onStopTraining();

        disconnect();
    }
    //</editor-fold>

    //<editor-fold desc="ON NEW MEASURE PROCESSED LISTENER INTERFACE">
    @Override
    public void onNewMeasureProcessed(final Measure measure) {
        TrainingFragment.updateData(measure, "");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fragment == graphViewFragment) {
                    graphViewFragment.showData();
                }
                else if (fragment == loadbarViewFragment) {
                    loadbarViewFragment.showData();
                }
                else if (fragment == serialViewFragment) {
                    serialViewFragment.showData();
                }
            }
        });
    }

    @Override
    public void onMovementChanged(final boolean ascending, final int index, final long time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.onMovementChanged(ascending, index, time);
            }
        });
    }
    //</editor-fold>
}
