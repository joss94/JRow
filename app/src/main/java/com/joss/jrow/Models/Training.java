package com.joss.jrow.Models;

import android.os.Environment;

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

    private Date date;

    private static Training training = new Training();

    public static Training getTraining(){
        return training;
    }

    public static void resetTraining(){
        training = new Training();
    }

    private Training() {
        date = Calendar.getInstance().getTime();
        report = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    void addToReport(ReportLine line){
        report.add(line);
    }

    public boolean save(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.FRANCE);

        int numberOfStrokes = report.size();
        double averageStroke =0.0;
        /*for(int i=0; i<getStrokeRates().size(); i++){
            averageStroke += getStrokeRates().valueAt(i);
        }
        averageStroke = averageStroke / getStrokeRates().size();*/

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), name + ".txt");

        try {
            if (file.createNewFile()) {
                FileOutputStream os = new FileOutputStream(file);

                os.write(("TRAINING OF " + sdf.format(date) + '\n').getBytes());
                os.write('\n');
                os.write(("ROWERS:" + '\n').getBytes());
                for(int i=0; i<9; i++){
                    os.write(((i==8)?i+": ":"Timo: " + Session.getSession().getRowers().get(i) + '\n').getBytes());
                }
                os.write('\n');
                os.write(("DURATION OF TRAINING: " + getDuration() + " seconds"+ '\n').getBytes());
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
            }
            else{
                return false;
            }
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

    public double getDuration(){
        double time = 0;
        if (report.size()>0) {
            time = (float)(report.get(report.size()-1).getTime()- Measures.getMeasures().getStartTime())/1000;
        }
        return time;
    }
}
