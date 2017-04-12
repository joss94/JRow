package com.joss.jrow.Models;

import java.util.ArrayList;
import java.util.List;

/*e
 * Created by joss on 24/03/17.
 */

public class Measures extends ArrayList<Measure>{

    private final int MAX_SIZE = 500;
    private final int LOCAL_MAX_RANGE = 50;

    private static volatile Measures measures;

    private volatile ArrayList<Measure> dataToProcess;
    private static ArrayList<OnNewMeasureProcessedListener> listeners;

    private ArrayList<ArrayList<Long>> maxsTimes;

    private long startTime = 0;

    private Measures() {
        super();
        dataToProcess = new ArrayList<>();
        listeners = new ArrayList<>();
        maxsTimes = new ArrayList<>();
        for(int i=0; i<8;i++){
            maxsTimes.add(new ArrayList<Long>());
        }
    }

    public static synchronized Measures getMeasures(){
        if(measures == null){
            measures = new Measures();
        }
        return measures;
    }

    synchronized Measure getLast() {
        if (!isEmpty()) {
            return get(size()-1);
        }
        return null;
    }

    public ArrayList<Measure> getDataToProcess() {
        return dataToProcess;
    }

    public void processData(){
        if(dataToProcess.size()>0){
            saveDataRow(dataToProcess.get(0));
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
            Measure max = localData.get(0);
            for(Measure measure : localData){
                if(measure.getRowAngle(i) > max.getRowAngle(i)){
                    max = measure;
                }
            }
            if(Math.abs(max.getRowAngle(i)-localData.get(0).getRowAngle(i))>50
                    && Math.abs(max.getRowAngle(i)-localData.get(localData.size()-1).getRowAngle(i))>50
                    && !maxsTimes.get(i).contains(max.getTime()-startTime)){
                maxsTimes.get(i).add(max.getTime()-startTime);
                onMovementChangedDetected(true, i, max.getTime()-startTime);
            }
        }
        /*
        for(int i=0; i<8; i++){
            for(Measure measure : this){
                if(measure.getRowAngle(i)>get(MAX_SIZE/2).getRowAngle(i)){
                    return;
                }
            }
            onMovementChangedDetected(true, i, get(MAX_SIZE/2).getTime()-startTime);
        }/**/
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

    private void onNewMeasureProcessed(Measure measure){
        for(OnNewMeasureProcessedListener listener : listeners){
            listener.onNewMeasureProcessed(measure);
        }
    }

    private void onMovementChangedDetected(boolean ascending, int index, long time){
        for(OnNewMeasureProcessedListener listener : listeners){
            listener.onMovementChanged(ascending, index, time);
        }
    }

    public void addOnNewMeasureProcessedListener(OnNewMeasureProcessedListener listener){
        listeners.add(listener);
    }

    public interface OnNewMeasureProcessedListener{
        void onNewMeasureProcessed(Measure measure);
        void onMovementChanged(boolean ascending, int index, long time);
    }
}
