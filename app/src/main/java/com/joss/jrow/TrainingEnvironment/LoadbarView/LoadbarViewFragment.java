package com.joss.jrow.TrainingEnvironment.LoadbarView;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.R;
import com.joss.jrow.TrainingEnvironment.TrainingFragment;

import java.util.ArrayList;

/*
 * Created by joss on 12/04/17.
 */

public class LoadbarViewFragment extends TrainingFragment{

    private TableLayout bars;
    private TableLayout table;
    private ArrayList<View> barViews;

    public static LoadbarViewFragment newInstance() {
        return new LoadbarViewFragment();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_loadbar_view;
    }

    @Override
    protected void findViews(View v) {
        bars = (TableLayout)v.findViewById(R.id.bars_container);
        table = (TableLayout)v.findViewById(R.id.table);

        barViews = new ArrayList<>();
        barViews.add(v.findViewById(R.id.bar1));
        barViews.add(v.findViewById(R.id.bar2));
        barViews.add(v.findViewById(R.id.bar3));
        barViews.add(v.findViewById(R.id.bar4));
        barViews.add(v.findViewById(R.id.bar5));
        barViews.add(v.findViewById(R.id.bar6));
        barViews.add(v.findViewById(R.id.bar7));
        barViews.add(v.findViewById(R.id.bar8));
    }

    @Override
    protected void setViews() {
        for(int i=0; i<table.getChildCount(); i++){
            ((TextView)((TableRow)table.getChildAt(i)).getChildAt(0)).setText(rowersNames[i].isEmpty()?"Rower i:":rowersNames[i]+" :");
            table.getChildAt(i).setVisibility(isSensorActive(i)?View.VISIBLE:View.GONE);
        }
    }

    @Override
    public void onMovementChanged(boolean ascending, int index, long time) {

    }

    @Override
    public void updateData(Measure measure) {
        for(int i=0; i<1; i++){
            if (isSensorActive(i)) {
                try {
                    int alignId = 0;
                    switch((i+1)/2){
                        case 1:
                            alignId = R.id.bow;
                            break;

                        case 2:
                            alignId = R.id.center_bow;
                            break;

                        case 3:
                            alignId = R.id.center_stern;
                            break;

                        case 4:
                            alignId = R.id.stern;
                    }

                    int rule = (i%2 == 0)?RelativeLayout.END_OF:RelativeLayout.START_OF;

                    int maxLength = (int) (0.9*(bars.getMeasuredWidth()/2));
                    int length = (int) ((measure.getRowAngle(i)/1000)*maxLength);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(length, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(rule, alignId);

                    barViews.get(i).setLayoutParams(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for(int j=0; j<table.getChildCount(); j++){
                    ((TextView)((TableRow)table.getChildAt(j)).getChildAt(1)).setText(String.valueOf(measure.getRowAngle(j)));
                }
            }
        }
    }


}
