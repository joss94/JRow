package com.joss.jrow.Models;

import android.util.SparseArray;

import com.joss.jrow.SensorManager;

public class ReportLine {

    private SparseArray<Double> catchDelays;
    private double strokeRate;
    private long time;

    public ReportLine() {
        catchDelays = new SparseArray<>();
    }

    public void addCatch(int index, Double delay){
        catchDelays.put(index, delay);
        if(catchDelays.size() == SensorManager.getInstance().numberOfActiveSensors()){
            if(Training.getTraining() != null){
                Training.getTraining().addToReport(this);
            }
            catchDelays.clear();
        }
    }

    public SparseArray<Double> getCatchDelays() {
        return catchDelays;
    }

    public double getStrokeRate() {
        return strokeRate;
    }

    public void setStrokeRate(double strokeRate) {
        this.strokeRate = strokeRate;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
