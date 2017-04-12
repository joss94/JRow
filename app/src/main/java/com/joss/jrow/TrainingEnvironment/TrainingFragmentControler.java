package com.joss.jrow.TrainingEnvironment;

import java.util.List;

/**
 * Created by joss on 11/04/17.
 */

public interface TrainingFragmentControler {
    public void startTraining();
    public void stopTraining();
    public List<String> getRowersNames();
    public boolean isSensorActive(int index);
    public void activateSensor(int index);
    public void deactivateSensor(int index);
}
