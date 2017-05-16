package com.joss.jrow.DataProcessingThreads;

import com.joss.jrow.Models.Measures;

public class DataProcessThread extends Thread {

    private Measures measures;

    public DataProcessThread(){
        measures = Measures.getMeasures();
    }

    @Override
    public void run(){
        while (!isInterrupted()) {
            if(!measures.getDataToProcess().isEmpty()){
                measures.processData();
            }
        }
    }
}
