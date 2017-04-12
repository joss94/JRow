package com.joss.jrow.TrainingEnvironment.GraphView;

import android.os.Bundle;

import com.joss.jrow.TrainingEnvironment.TrainingFragment;

/**
 * Created by joss on 11/04/17.
 */

public class GraphViewFragment extends TrainingFragment {

    public GraphViewFragment() {
    }

    public static GraphViewFragment newInstance(int layoutId){
        GraphViewFragment fr = new GraphViewFragment();
        Bundle args = new Bundle();
        args.putInt("layoutId", layoutId);
        fr.setArguments(args);
        return fr;
    }


}
