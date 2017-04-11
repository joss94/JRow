package com.joss.jrow;

/*
 * Created by joss on 27/03/17.
 */

class DataReadThread extends Thread {

    private final int PROCESS_DELAY = 20;

    public static volatile String data;
    private Measures measures;

    private volatile boolean running = true;


    DataReadThread(){
        data="";
        measures = Measures.getMeasures();
    }

    @Override
    public void run(){
        running = true;
        while (running) {
            if(data.contains("&")){
                decode(data.substring(0, data.indexOf("&")+1));
                data = data.substring(data.indexOf('&')+1);
            }
        }
    }

    public void cancel(){
        running = false;
    }

    synchronized void addData(String additional){
        data+=additional;
    }

    private void decode(String message){
        if(!message.startsWith("%") && message.endsWith("&")){
            return;
        }
        String[] substrings = message.split("\\%");
        for(String substring : substrings){
            if(!substring.isEmpty()){
                decodeRow(substring);
            }
        }
    }

    private void decodeRow(String message){
        //*
        if(!isValidRow(message)){
            MainActivity.addToSerial("error");
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
                MainActivity.addToSerial("not row" + i);
                return false;
            }
        }
        if(!message.contains("$time:")){
            MainActivity.addToSerial("not time");
            return false;
        }

        String[] substrings = message.split("\\$");
        for(int i=1; i<substrings.length; i++){
            String substring = substrings[i];
            if(!substring.contains(":")){
                MainActivity.addToSerial("no :");
                return false;
            }
        }
        return true;
    }
}
