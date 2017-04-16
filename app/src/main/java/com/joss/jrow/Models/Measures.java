package com.joss.jrow.Models;

import com.joss.jrow.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class Measures extends ArrayList<Measure>{

    private static final long serialVersionUID = -5836923295713874526L;
    private static final double W_LIMIT = 20;

    private final int MAX_SIZE = 500;
    private final int LOCAL_MAX_RANGE = 50;

    private static volatile Measures measures;

    private volatile ArrayList<Measure> dataToProcess;
    private static ArrayList<OnNewMeasureProcessedListener> listeners;

    private ArrayList<ArrayList<Long>> maxsTimes;

    private long startTime = 0;
    private volatile long[] catchTimes;
    private volatile float strokeRate;

    private Measure backPosition;
    private Measure frontPosition;
    private Measure neutralPosition;

    private Measures() {
        super();
        strokeRate = 0;
        dataToProcess = new ArrayList<>();
        listeners = new ArrayList<>();
        maxsTimes = new ArrayList<>();
        for(int i=0; i<8;i++){
            maxsTimes.add(new ArrayList<Long>());
        }
        catchTimes = new long[] {0,0,0,0,0,0,0,0};
    }

    public static synchronized Measures getMeasures(){
        if(measures == null){
            measures = new Measures();
        }
        return measures;
    }

    public ArrayList<Measure> getDataToProcess() {
        return dataToProcess;
    }

    public void processData(){
        if(dataToProcess.size()>0){
            Measure measure = dataToProcess.get(0);
            if(measure == null){
                return;
            }

            if (size()>0) {
                Measure lastMeasure = get(size()-1);
                double alpha = 0.5;
                for(int i =0; i<8; i++){
                    long angle = measure.getRawAngle(i);
                    long lastAngle = lastMeasure.getRawAngle(i);
                    measure.setRawAngle(i, (long) ((double)angle*(1-alpha)+(double)lastAngle*alpha));
                }
            }
            saveDataRow(measure);
        }
    }

    private void saveDataRow(Measure dataRow){
        add(dataRow);
        dataToProcess.remove(dataRow);
        //*
        if (size()>2) {
            onNewMeasureProcessed(get(size()-2));
        }
        if (measures.size() >= MAX_SIZE) {
            detectTangents();
        }/**/
    }

    private void detectTangents(){
        List<Measure> localData = this.subList(size()-LOCAL_MAX_RANGE, size()-1);
        for (int i=0; i<8; i++) {
            if (SensorManager.getInstance().isSensorActive(i)) {
                Measure max = localData.get(0);
                for(Measure measure : localData){
                    if(measure.getRawAngle(i) > max.getRawAngle(i)){
                        max = measure;
                    }
                }
                if(Math.abs(max.getRawAngle(i)-localData.get(0).getRawAngle(i))>50
                        && Math.abs(max.getRawAngle(i)-localData.get(localData.size()-1).getRawAngle(i))>80
                        && !maxsTimes.get(i).contains(max.getTime()-startTime)){
                    maxsTimes.get(i).add(max.getTime()-startTime);
                    onMovementChangedDetected(i, max.getTime()-startTime);
                }
            }
        }
    }

    @Override
    public synchronized boolean add(Measure measure){
        boolean result = super.add(measure);
        if(size()>MAX_SIZE){
            remove(0);
        }
        if(size()==1){
            startTime = measure.getTime();
        }
        return result;
    }


    public synchronized void wipeData(){
        measures = new Measures();
        dataToProcess = new ArrayList<>();
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

    public Measure getBackPosition() {
        return backPosition;
    }

    public void setBackPosition(Measure backPosition) {
        this.backPosition = backPosition;
    }

    public Measure getFrontPosition() {
        return frontPosition;
    }

    public void setFrontPosition(Measure frontPosition) {
        this.frontPosition = frontPosition;
    }

    public Measure getNeutralPosition() {
        return neutralPosition;
    }

    public float getStrokeRate() {
        return strokeRate;
    }

    public boolean isCalibrated(){
        return(getBackPosition() != null && getFrontPosition() != null && getNeutralPosition() != null);
    }

    public void setNeutralPosition(Measure neutralPosition) {
        this.neutralPosition = neutralPosition;
    }

    private void onNewMeasureProcessed(Measure measure){
        for(OnNewMeasureProcessedListener listener : listeners){
            listener.onNewMeasureProcessed(measure);
        }
    }

    private void onMovementChangedDetected(int index, long time){
        catchTimes[index] = time;
        if(index == Position.STERN){
            strokeRate = (float)60000/(((float)(time-measures.getCatchTimes()[Position.STERN])));
        }
        for(OnNewMeasureProcessedListener listener : listeners){
            listener.onMovementChanged(index, time);
        }
    }

    public void addOnNewMeasureProcessedListener(OnNewMeasureProcessedListener listener){
        listeners.add(listener);
    }

    public interface OnNewMeasureProcessedListener{
        void onNewMeasureProcessed(Measure measure);
        void onMovementChanged(int index, long time);
    }
}
