package com.joss.jrow.Models;

import android.content.Context;

import com.joss.jrow.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class Measures extends ArrayList<Measure>{

    private static final long serialVersionUID = -5836923295713874526L;

    private final int LOCAL_SIZE = 100;

    private static volatile Measures measures = new Measures();

    private volatile ArrayList<Measure> dataToProcess;
    private List<OnNewMeasureProcessedListener> listeners;

    private long startTime = 0;
    private volatile long[] catchTimes;
    private volatile double[] catchAngles;
    private volatile long[] localCatchTimes;
    private volatile double[] localCatchAngles;
    private volatile int[] localIndexes;
    private volatile float strokeRate;

    private Measure neutralPosition;

    private double maxBack;
    private double minFront;

    private Measures() {
        super();
        listeners = new ArrayList<>();
        wipeData();
    }

    public static synchronized Measures getMeasures(){
        return measures;
    }

    public ArrayList<Measure> getDataToProcess() {
        return dataToProcess;
    }

    public void processData(){
        if(dataToProcess.size()>0){
            Measure measure = dataToProcess.get(0);
            if(measure == null){
                dataToProcess.remove(0);
                return;
            }
            saveDataRow(measure);
        }
    }

    private void saveDataRow(Measure dataRow){
        add(dataRow);
        dataToProcess.remove(dataRow);
        for(int i=0; i<8; i++){
            maxBack = Math.max(maxBack, dataRow.getAngle(i));
            minFront = Math.min(minFront, dataRow.getAngle(i));
        }
        //*
        if (size()>2) {
            onNewMeasureProcessed(get(size()-2));
        }
        if (measures.size() >= LOCAL_SIZE) {
            detectTangents(dataRow);
        }
    }

    private void detectTangents(Measure measure){
        for (int i=0; i<8; i++) {
            if (SensorManager.getInstance().isSensorActive(i)) {
                if(measure.getRawAngle(i) > localCatchAngles[i]){
                    localCatchAngles[i] = measure.getRawAngle(i);
                    localCatchTimes[i] = measure.getTime();
                    localIndexes[i]=0;
                }
                else{
                    localIndexes[i] = Math.min(100, localIndexes[i]++);
                    if(localIndexes[i] == 100){
                        localCatchTimes[i] = get(0).getTime();
                        localCatchAngles[i] = get(0).getRawAngle(i);
                    }
                }

                if(localIndexes[i] == 50){
                    catchTimes[i] = localCatchTimes[i];
                    catchAngles[i] = localCatchAngles[i];
                    onMovementChangedDetected(i, catchTimes[i], catchAngles[i]);
                }
            }
        }
    }

    @Override
    public synchronized boolean add(Measure measure){
        boolean result = super.add(measure);
        if(size()>LOCAL_SIZE){
            remove(0);
        }
        if(startTime<=0){
            startTime = measure.getTime();
        }
        return result;
    }


    public synchronized void wipeData(){
        clear();
        strokeRate = 0;
        startTime = 0;
        dataToProcess = new ArrayList<>();
        catchTimes = new long[] {0,0,0,0,0,0,0,0};
        catchAngles = new double[] {0,0,0,0,0,0,0,0};
        localCatchTimes = new long[] {0,0,0,0,0,0,0,0};
        localCatchAngles = new double[] {0,0,0,0,0,0,0,0};
        localIndexes = new int[] {0,0,0,0,0,0,0,0};
    }

    public synchronized void addToProcess(Measure measure){
        dataToProcess.add(measure);
    }

    public long getStartTime() {
        return startTime;
    }

    public long[] getCatchTimes() {
        return catchTimes;
    }

    public double[] getCatchAngles() {
        return catchAngles;
    }

    Measure getNeutralPosition() {
        return neutralPosition;
    }

    public float getStrokeRate() {
        return strokeRate;
    }

    public boolean isCalibrated(){
        return getNeutralPosition() != null;
    }

    public void setNeutralPosition(Measure neutralPosition) {
        this.neutralPosition = new Measure();
        if(neutralPosition != null){
            for(int i=0; i<8; i++){
                if(SensorManager.getInstance().isSensorActive(i)){
                    this.neutralPosition.setRawAngle(i, neutralPosition.getRawAngle(i));
                }
                else{
                    this.neutralPosition.setRawAngle(i, 500);
                }
            }
        }
    }

    double getMaxBack() {
        return maxBack;
    }

    double getMinFront() {
        return minFront;
    }

    public void setDefaultCalibration(Context context){
        Measure neutral = new Measure();
        for(int i=0; i<8; i++){
            //neutral.setRawAngle(i, 500c1);
            neutral.setRawAngle(i, context.getSharedPreferences("JROW_CALIB", Context.MODE_PRIVATE).getLong("calib"+i, 500));
        }
        setNeutralPosition(neutral);
    }

    private void onNewMeasureProcessed(Measure measure){
        for (OnNewMeasureProcessedListener listener : listeners) {
            if (listener != null) {
                listener.onNewMeasureProcessed(measure);
            }
        }
    }

    private void onMovementChangedDetected(int index, long time, double angle){
        if(index == Position.STERN){
            strokeRate = (float)60000/(((float)(time-catchTimes[Position.STERN])));
        }
        catchTimes[index] = time;
        for (OnNewMeasureProcessedListener listener : listeners) {
            if (listener != null) {
                listener.onMovementChanged(index);
            }
        }
    }

    public void addOnNewMeasureProcessedListener(OnNewMeasureProcessedListener listener){
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnNewMeasureProcessedListener(OnNewMeasureProcessedListener listener){
        listeners.remove(listener);
    }

    public void resetCalibration() {
        setNeutralPosition(null);
        minFront = 0;
        maxBack = 0;
    }

    public interface OnNewMeasureProcessedListener{
        void onNewMeasureProcessed(Measure measure);
        void onMovementChanged(int index);
    }
}
