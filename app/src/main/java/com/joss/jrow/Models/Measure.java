package com.joss.jrow.Models;

import java.util.HashMap;

public class Measure extends HashMap<String, Long> {

    public Measure() {
        put("time", (long) 0);

        put("row0", (long) 0);
        put("row1", (long) 0);
        put("row2", (long) 0);
        put("row3", (long) 0);
        put("row4", (long) 0);
        put("row5", (long) 0);
        put("row6", (long) 0);
        put("row7", (long) 0);
    }

    public long getTime(){
        if(get("time") != null){
            return get("time");
        }
        else{
            return -1;
        }
    }

    public Long getRawAngle(int i){
        if(containsKey("row"+i)){
            return get("row"+i);
        }
        return (long) -1;
    }

    public void setRawAngle(int index, long angle){
        put("row"+index, angle);
    }

    public double getAngle(int i){
        double result = 0;
        result = ((double)getRawAngle(i)/1000)*220;
        return result;
    }
}
