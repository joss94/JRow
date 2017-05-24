package com.joss.jrow.DataProcessingThreads;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.SerialContent;

public class DataReadThread extends Thread {

    private static volatile String data;
    private Measures measures;

    private SerialContent serialContent;

    public DataReadThread(){
        data="";
        measures = Measures.getMeasures();
        serialContent = SerialContent.getInstance();
    }

    @Override
    public void run(){
        while (!isInterrupted()) {
            if(data.contains("&")){
                decode(data.substring(0, data.indexOf("&")+1));
                data = data.substring(data.indexOf('&')+1);
            }
        }
    }

    public synchronized void addData(String additional){
        data+=additional;
    }

    private void decode(String message){
        if(!message.startsWith("%") && message.endsWith("&")){
            return;
        }
        String[] substrings = message.split("\\%");
        //decodeRow(substrings[substrings.length-1]);
        //*
        for(String substring : substrings){
            if(!substring.isEmpty()){
                decodeRow(substring);
            }
        }/**/
    }

    private void decodeRow(String message){
        //*
        if(!isValidRow(message)){
            serialContent.addToSerial("Error: Invalid row...");
            return;
        }

        Measure measure = new Measure();


        String[] substrings = message.split("\\$");
        for(int i=1; i<substrings.length; i++){
            String substring = substrings[i];
            String key = substring.substring(0, substring.indexOf(":"));
            if(substring.endsWith("&")){
                substring = substring.substring(0, substring.indexOf('&'));
            }
            String value = substring.substring(substring.indexOf(':')+1);
            try {
                measure.put(key, (value.isEmpty())?0:Long.parseLong(value));
            } catch (NumberFormatException e) {
                return;
            }
        }

        measures.addToProcess(measure);/**/
    }

    private boolean isValidRow(String message){
        for(int i=0; i<8; i++){
            if(!message.contains("$row"+i+":")){
                serialContent.addToSerial("Row not found: " + i);
                return false;
            }
        }
        if(!message.contains("$time:")){
            serialContent.addToSerial("Time not found");
            return false;
        }

        String[] substrings = message.split("\\$");
        for(int i=1; i<substrings.length; i++){
            String substring = substrings[i];
            if(!substring.contains(":")){
                serialContent.addToSerial("Not found :");
                return false;
            }
        }
        return true;
    }
}
