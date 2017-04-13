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


    }

    @Override
    protected void setViews() {
    }

    @Override
    public void onMovementChanged(boolean ascending, int index, long time) {

    }

    @Override
    public synchronized void showData() {
        super.showData();
        if (barLimits != null) {
            for(View barLimit : barLimits){
                int position = barLimits.indexOf(barLimit);
                if (isSensorActive(position)) {


                    int maxMargin = (int) (0.9*((RelativeLayout)barLimit.getParent()).getMeasuredWidth()/2);
                    //*
                    int margin = (int) (maxMargin*(1-(float)lastMeasure.getRowAngle(position)/1000));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) barLimit.getLayoutParams();
                    if(position%2 == 0){
                        params.setMarginEnd(margin);
                    }else{
                        params.setMarginStart(margin);
                    }
                    barLimit.setLayoutParams(params);
                    barLimit.invalidate();/**/
                    //barLimit.setTranslationX(maxMargin*(float)(lastMeasure.getRowAngle(position))/1000);

                }
            }
        }
    }


}
