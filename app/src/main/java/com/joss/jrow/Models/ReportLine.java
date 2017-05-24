package com.joss.jrow.Models;

import android.util.SparseArray;

public class ReportLine {

    private SparseArray<Double> catchDelays;
    private SparseArray<Double> angles;
    private double strokeRate;
    private long time;

    public ReportLine() {
        catchDelays = new SparseArray<>();
        angles = new SparseArray<>();
    }

    public void addCatch(int index, Double delay, Double angle){
        catchDelays.put(index, delay);
        angles.put(index, angle);
    }

    public SparseArray<Double> getCatchDelays() {
        return catchDelays;
    }

    public SparseArray<Double> getAngles() {
        return angles;
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
