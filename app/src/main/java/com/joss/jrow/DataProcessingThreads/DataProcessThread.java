package com.joss.jrow.DataProcessingThreads;

import com.joss.jrow.Models.Measures;

public class DataProcessThread extends Thread {

    private Measures measures;

    private volatile boolean running = true;

    public DataProcessThread(){
        measures = Measures.getMeasures();
    }

    @Override
    public void run(){
        running = true;
        while (running) {
            if(!measures.getDataToProcess().isEmpty()){
                measures.processData();
            }
        }
    }

    public void cancel(){
        running = false;
    }

}
