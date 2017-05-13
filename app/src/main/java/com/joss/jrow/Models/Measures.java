package com.joss.jrow.Models;

import com.joss.jrow.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class Measures extends ArrayList<Measure>{

    private static final long serialVersionUID = -5836923295713874526L;

    private final int MAX_SIZE = 100;
    private final int LOCAL_MAX_RANGE = 30;

    private static volatile Measures measures;

    private volatile ArrayList<Measure> dataToProcess;
    private OnNewMeasureProcessedListener listener;

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
                dataToProcess.remove(0);
                return;
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
            //if(false){
            if (SensorManager.getInstance().isSensorActive(i)) {
                Measure max = localData.get(0);
                for(Measure measure : localData){
                    if(measure.getRawAngle(i) > max.getRawAngle(i)){
                        max = measure;
                    }
                }
                if(Math.abs(max.getRawAngle(i)-localData.get(0).getRawAngle(i))>20
                        && Math.abs(max.getRawAngle(i)-localData.get(localData.size()-1).getRawAngle(i))>20
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
        if(startTime<=0){
            startTime = measure.getTime();
        }
        return result;
    }


    public synchronized void wipeData(){
        clear();
        dataToProcess = new ArrayList<>();
        startTime = 0;
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
        this.backPosition = new Measure();
        if (backPosition != null) {
            for(int i=0; i<8; i++){
                if(SensorManager.getInstance().isSensorActive(i)){
                    this.backPosition.setRawAngle(i, backPosition.getRawAngle(i));
                }
                else{
                    this.backPosition.setRawAngle(i, 675);
                }
            }
        }
    }

    public Measure getFrontPosition() {
        return frontPosition;
    }

    public void setFrontPosition(Measure frontPosition) {
        this.frontPosition = new Measure();
        if (frontPosition != null) {
            for(int i=0; i<8; i++){
                if(SensorManager.getInstance().isSensorActive(i)){
                    this.frontPosition.setRawAngle(i, frontPosition.getRawAngle(i));
                }
                else{
                    this.frontPosition.setRawAngle(i, 225);
                }
            }
        }
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

    public double getMaxBack() {
        return getBackPosition().getMaxAngle();
    }

    public double getMinFront() {
        return getFrontPosition().getMinAngle();
    }

    public void setDefaultCalibration(){
        Measure back = new Measure();
        Measure front = new Measure();
        Measure neutral = new Measure();

        for(int i=0; i<8; i++){
            back.setRawAngle(i, 675);
            neutral.setRawAngle(i, 500);
            front.setRawAngle(i, 225);
        }

        setNeutralPosition(neutral);
        setBackPosition(back);
        setFrontPosition(front);
    }

    private void onNewMeasureProcessed(Measure measure){
        if (listener != null) {
            listener.onNewMeasureProcessed(measure);
        }
    }

    private void onMovementChangedDetected(int index, long time){
        if(index == Position.STERN){
            strokeRate = (float)60000/(((float)(time-measures.getCatchTimes()[Position.STERN])));
        }
        catchTimes[index] = time;
        if (listener != null) {
            listener.onMovementChanged(index, time);
        }
    }

    public void setOnNewMeasureProcessedListener(OnNewMeasureProcessedListener listener){
        this.listener = listener;
    }

    public void resetCalibration() {
        setBackPosition(null);
        setFrontPosition(null);
        setNeutralPosition(null);
    }

    public interface OnNewMeasureProcessedListener{
        void onNewMeasureProcessed(Measure measure);
        void onMovementChanged(int index, long time);
    }
}
