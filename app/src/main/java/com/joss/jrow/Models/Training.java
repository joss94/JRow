package com.joss.jrow.Models;

import android.os.Environment;
import android.util.Log;
import android.util.LongSparseArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Training implements Serializable {

    private static final long serialVersionUID = -7103085086747948876L;

    private List<ReportLine> report;

    private LongSparseArray<Double> strokeRates;
    private Date date;

    private static Training training = new Training();

    public static Training getTraining(){
        return training;
    }

    public static void resetTraining(){
        training = new Training();
    }

    private Training() {
        strokeRates = new LongSparseArray<>();
        date = Calendar.getInstance().getTime();
        report = new ArrayList<>();
    }

    public LongSparseArray<Double> getStrokeRates() {
        return strokeRates;
    }

    public Date getDate() {
        return date;
    }

    void addToReport(ReportLine line){
        report.add(line);
    }

    public boolean save(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        if(report == null || report.size() <=0 || !isExternalStorageWritable()){
            return false;
        }

        int numberOfStrokes = report.size();
        double time = (float)(report.get(report.size()-1).getTime()-Measures.getMeasures().getStartTime())/1000;
        double averageStroke =0.0;
        for(int i=0; i<getStrokeRates().size(); i++){
            averageStroke += getStrokeRates().valueAt(i);
        }
        averageStroke = averageStroke / getStrokeRates().size();

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), name);
        if (!file.mkdirs()) {
            Log.e("SAVE", "Directory not created");
        }

        try {
            FileOutputStream os = new FileOutputStream(file);

            os.write(("TRAINING OF " + sdf.format(date) + '\n').getBytes());
            os.write('\n');
            os.write(("ROWERS:" + '\n').getBytes());
            for(int i=0; i<9; i++){
                os.write((Session.getSession().getRowers().get(i) + '\n').getBytes());
            }
            os.write('\n');
            os.write(("DURATION OF TRAINING: " + time + " seconds"+ '\n').getBytes());
            os.write(("NUMBER OF STROKES: " + numberOfStrokes + '\n').getBytes());
            os.write(("AVERAGE STROKE: " + averageStroke + '\n').getBytes());
            os.write('\n');
            for(ReportLine line : report){
                os.write(String.valueOf(line.getTime()).getBytes());
                os.write(String.valueOf(line.getStrokeRate()).getBytes());
                for(int i = 0; i<8; i++){
                    os.write(String.valueOf(line.getCatchDelays().get(i, -1.0)).getBytes());
                    os.write('\n');
                }
            }

            os.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
