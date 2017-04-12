package com.joss.jrow.DataProcessingThreads;

/*
 * Created by joss on 27/03/17.
 */

import com.joss.jrow.Models.Measures;

class DataProcessThread extends Thread {

    private final int PROCESS_DELAY = 20;

    private String data;
    private Measures measures;

    private volatile boolean running = true;

    DataProcessThread(){
        data="";
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
