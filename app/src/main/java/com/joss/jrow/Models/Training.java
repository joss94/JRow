package com.joss.jrow.Models;

import android.os.Environment;

import com.joss.jrow.DataProcessingThreads.DataWriteThread;
import com.joss.jrow.SensorManager;

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

public class Training implements Serializable, Measures.OnNewMeasureProcessedListener {

    private static final long serialVersionUID = -7103085086747948876L;

    private static List<ReportLine> report;

    private static Date date;

    private static SimpleDateFormat sdf;
    private static FileOutputStream os;
    private static File file;

    private static Training training = new Training();

    private boolean paused = false, recording = false;

    public static Training getTraining(){
        return training;
    }

    public static void resetTraining() {
        date = Calendar.getInstance().getTime();
        report = new ArrayList<>();
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), sdf.format(date) + ".txt");
        try {
            if (file.createNewFile()) {
                os = new FileOutputStream(file);
                os.write(("TRAINING OF " + sdf.format(date) + '\n').getBytes());
                os.write('\n');
                os.write(("ROWERS:" + '\n').getBytes());
                for (int i = 0; i < 9; i++) {
                    os.write((((i < 8) ? i + ": " : "Timo: ") + Session.getSession().getRowers().get(i) + '\n').getBytes());
                }
                os.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Training() {
        sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.FRANCE);
        //resetTraining();
    }

    public Date getDate() {
        return date;
    }

    public void addToReport(ReportLine line){
        report.add(line);
        /*
        try {
            if (os != null) {
                os.write(String.valueOf(line.getTime()).getBytes());
                os.write(';');
                os.write(String.valueOf(line.getStrokeRate()).getBytes());
                os.write(';');
                for(int i = 0; i<8; i++){
                    os.write(String.valueOf(line.getCatchDelays().get(i, -1.0)).getBytes());
                    os.write(';');
                }
                for(int i = 0; i<8; i++){
                    os.write(String.valueOf(line.getAngles().get(i, -1.0)).getBytes());
                    os.write(';');
                }
                os.write('\n');
            }
        } catch (IOException ignored) {
        }/**/
    }

    public void save() {
        DataWriteThread t = DataWriteThread.getInstance();
        t.write("DURATION OF TRAINING: " + getDuration() + " seconds"+ '\n');
        t.write("NUMBER OF STROKES: " + getNumberOfStrokes() + '\n');
        t.write("AVERAGE STROKE: " + getAverageStrokeRate() + '\n');
        t.newLine();
        t.write("AVERAGE STERN DELAYS:" + '\n');
        for(int i=0; i<8; i++){
            t.write(i+" (" + Session.getSession().getRowers().get(i)+") : "+
                    String.valueOf(getAverageAbsoluteSternDelayOf(i)) + '\n');
        }
        t.newLine();
        t.write("AVERAGE DELAYS:" + '\n');
        for(int i=0; i<8; i++){
            t.write(i+" (" + Session.getSession().getRowers().get(i)+") : "+
                    String.valueOf(getAverageAbsoluteDelayOf(i)) + '\n');
        }
        t.newLine();
        t.write("AVERAGE STERN DEVIATION:" + '\n');
        for(int i=0; i<8; i++){
            t.write(i+" (" + Session.getSession().getRowers().get(i)+") : "+
                    String.valueOf(getStandardSternDeviationOf(i)) + '\n');
        }
        t.newLine();
        t.write("AVERAGE DEVIATION:" + '\n');
        for(int i=0; i<8; i++){
            t.write(i+" (" + Session.getSession().getRowers().get(i)+") : "+
                    String.valueOf(getStandardDeviationOf(i)));
        }
        t.newLine();
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public double getDuration(){
        return (Measures.getMeasures().size() >0 ? Measures.getMeasures().get(Measures.getMeasures().size()-1).getTime():0);
    }

    private int getNumberOfStrokes(){
        return report.size();
    }

    private double getAverageStrokeRate(){
        double averageStroke =0.0;
        for(ReportLine line : report){
            averageStroke += line.getStrokeRate();
        }
        averageStroke = averageStroke / getNumberOfStrokes();
        return averageStroke;
    }

    private double getAverageAbsoluteSternDelayOf(int index){
        double result = 0.0;
        int counter = 0;
        for(ReportLine line : report){
            if (line.getCatchDelays().get(index) != null) {
                result += Math.abs(line.getCatchDelays().get(index));
                counter += 1;
            }
        }
        return counter == 0 ? -1.0:result/counter;
    }

    private double getAverageSternDelayOf(int index){
        double result = 0.0;
        int counter = 0;
        for(ReportLine line : report){
            if (line.getCatchDelays().get(index) != null) {
                result += line.getCatchDelays().get(index);
                counter += 1;
            }
        }
        return counter == 0 ? -1.0:result/counter;
    }

    private double getStandardSternDeviationOf(int index){
        double result = 0.0;
        int counter = 0;
        double average = getAverageSternDelayOf(index);
        for(ReportLine line : report){
            if (line.getCatchDelays().get(index) != null) {
                result += Math.pow(line.getCatchDelays().get(index)-average, 2);
                counter += 1;
            }
        }
        return counter == 0 ? -1.0:Math.sqrt(result/counter);
    }

    private double getAverageAbsoluteDelayOf(int index){
        double result = 0.0;
        int counter = 0;
        for(ReportLine line : report){
            if (index < Position.STERN
                    && line.getCatchDelays().get(index) != null
                    && line.getCatchDelays().get(index+1) != null) {

                result += Math.abs(line.getCatchDelays().get(index) - line.getCatchDelays().get(index+1));
                counter += 1;
            }
        }
        return counter == 0 ? -1.0:result/counter;
    }

    private double getAverageDelayOf(int index){
        double result = 0.0;
        int counter = 0;
        for(ReportLine line : report){
            if (line.getCatchDelays().get(index) != null && line.getCatchDelays().get(index+1) != null) {
                result += line.getCatchDelays().get(index) - line.getCatchDelays().get(index+1);
                counter += 1;
            }
        }
        return counter == 0 ? -1.0:result/counter;
    }

    private double getStandardDeviationOf(int index){
        double result = 0.0;
        int counter = 0;
        double average = getAverageDelayOf(index);
        for(ReportLine line : report){
            if (line.getCatchDelays().get(index) != null && line.getCatchDelays().get(index+1) != null) {
                result += Math.pow(line.getCatchDelays().get(index) - line.getCatchDelays().get(index+1)-average, 2);
                counter += 1;
            }
        }
        return counter == 0 ? -1.0:Math.sqrt(result/counter);
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    @Override
    public void onNewMeasureProcessed(Measure measure) {
        try{
            if (os != null) {
                os.write(String.valueOf(measure.getTime()).getBytes());
                os.write(';');
                for(int i=0; i<8; i++){
                    os.write(SensorManager.getInstance().isSensorActive(i)?String.valueOf(measure.getAngle(i)).getBytes():"-1.0".getBytes());
                    os.write(';');
                }
                os.write('\n');
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onMovementChanged(int index) {

    }

    public void stop() {
        if(os!= null){
            try {
                os.close();
            } catch (IOException e) {
            }
        }
    }
}
