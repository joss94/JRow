package com.joss.jrow.TrainingEnvironment.LoadbarView;

import android.view.View;
import android.widget.RelativeLayout;

import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

import java.util.ArrayList;

/*
 * Created by joss on 12/04/17.
 */

public class LoadbarViewFragment extends TrainingFragment{
    private volatile ArrayList<View> barLimits;
    private volatile ArrayList<View> barCatches;

    public static LoadbarViewFragment newInstance() {
        return new LoadbarViewFragment();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_loadbar_view;
    }

    @Override
    protected void findViews(View v) {

        barLimits = new ArrayList<>();
        barLimits.add(v.findViewById(R.id.bar8limit));
        barLimits.add(v.findViewById(R.id.bar7limit));
        barLimits.add(v.findViewById(R.id.bar6limit));
        barLimits.add(v.findViewById(R.id.bar5limit));
        barLimits.add(v.findViewById(R.id.bar4limit));
        barLimits.add(v.findViewById(R.id.bar3limit));
        barLimits.add(v.findViewById(R.id.bar2limit));
        barLimits.add(v.findViewById(R.id.bar1limit));

        barCatches = new ArrayList<>();
        barCatches.add(v.findViewById(R.id.bar8catch));
        barCatches.add(v.findViewById(R.id.bar7catch));
        barCatches.add(v.findViewById(R.id.bar6catch));
        barCatches.add(v.findViewById(R.id.bar5catch));
        barCatches.add(v.findViewById(R.id.bar4catch));
        barCatches.add(v.findViewById(R.id.bar3catch));
        barCatches.add(v.findViewById(R.id.bar2catch));
        barCatches.add(v.findViewById(R.id.bar1catch));


    }

    @Override
    protected void setViews() {
    }

    @Override
    public void onMovementChanged(boolean ascending, int index, long time) {
        super.onMovementChanged(ascending, index, time);
        if(isSensorActive(index)){
            barCatches.get(index).setX(barCatches.get(index).getX());
        }
    }

    @Override
    public synchronized void showData() {
        super.showData();
        if (barLimits != null) {
            for(View barLimit : barLimits){
                int position = barLimits.indexOf(barLimit);
                View barCatch = barCatches.get(position);
                if (isSensorActive(position)) {
                    int maxMargin = (int) (0.9*((RelativeLayout)barLimit.getParent()).getMeasuredWidth()/2);
                    //*
                    int margin = (int) (maxMargin*(1-(float)lastMeasure.getRowAngle(position)/1000));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) barLimit.getLayoutParams();
                    if(position%2 == 0){
                        params.setMarginStart(margin);
                        barLimit.setLayoutParams(params);
                        barLimit.invalidate();
                        if(barLimit.getX() < barCatch.getX()){
                            barCatch.setX(barLimit.getX());
                            barCatch.invalidate();
                        }
                    }else{
                        params.setMarginEnd(margin);
                        barLimit.setLayoutParams(params);
                        barLimit.invalidate();
                        if(barLimit.getX() > barCatch.getX()){
                            barCatch.setX(barLimit.getX());
                            barCatch.invalidate();
                        }
                    }
                }
            }
        }
    }
}
