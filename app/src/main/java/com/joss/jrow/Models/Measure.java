package com.joss.jrow.Models;

import java.util.HashMap;

public class Measure extends HashMap<String, Long> {

    private static final long serialVersionUID = 5047427052116388068L;

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

    void setRawAngle(int index, long angle){
        put("row"+index, angle);
    }

    public double getAngle(int i){
        double result;
        result = ((double)(getRawAngle(i)-Measures.getMeasures().getNeutralPosition().getRawAngle(i))/1000)*250;
        return result;
    }

    public double getAnglePercentage(int index){
        double percentage = (getRawAngle(index) - Measures.getMeasures().getMinFront())/(Measures.getMeasures().getMaxBack() - Measures.getMeasures().getMinFront());
        return Math.max(Math.min(1,percentage), percentage);
    }
}
