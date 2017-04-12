package com.joss.jrow.Models;

import java.util.HashMap;

/**
 * Created by joss on 24/03/17.
 */

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

    public Long getRowAngle(int i){
        if(containsKey("row"+i)){
            return get("row"+i);
        }
        return (long) -1;
    }

    void setRowAngle(int i, long value){
        put("row"+i, value);
    }
}
