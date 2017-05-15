package com.joss.jrow.Models;

import android.util.SparseArray;

public class ReportLine {

    private SparseArray<Double> catchDelays;
    private double strokeRate;
    private long time;

    public ReportLine() {
        catchDelays = new SparseArray<>();
    }

    public void addCatch(int index, Double delay){
        catchDelays.put(index, delay);

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
