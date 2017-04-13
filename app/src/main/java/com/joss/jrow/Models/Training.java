package com.joss.jrow.Models;

import android.util.LongSparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Training implements Serializable {

    private static final long serialVersionUID = -7103085086747948876L;

    private LongSparseArray<Double> strokeRates;
    private List<String> rowers;
    private Date date;

    public Training() {
        strokeRates = new LongSparseArray<>();
        rowers = new ArrayList<>();
        date = Calendar.getInstance().getTime();
    }

    public LongSparseArray<Double> getStrokeRates() {
        return strokeRates;
    }

    public List<String> getRowers() {
        return rowers;
    }

    public void setRowers(List<String> rowers) {
        this.rowers = rowers;
    }

    public Date getDate() {
        return date;
    }
}
