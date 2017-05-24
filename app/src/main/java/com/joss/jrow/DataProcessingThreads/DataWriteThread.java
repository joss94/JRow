package com.joss.jrow.DataProcessingThreads;

import android.os.Environment;

import com.joss.jrow.Models.Measure;
import com.joss.jrow.Models.Measures;
import com.joss.jrow.Models.Session;
import com.joss.jrow.SensorManager;
import com.joss.jrow.SerialContent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DataWriteThread extends Thread implements Measures.OnNewMeasureProcessedListener {

    private final static int BUFFER_SIZE = 20;

    private static DataWriteThread instance = new DataWriteThread();
    private static volatile List<String> data;
    private static SimpleDateFormat sdf;
    private static OutputStream os;
    private byte[] buffer = new byte[BUFFER_SIZE];

    private SerialContent serialContent;


    public static DataWriteThread getInstance(){
        return instance;
    }

    private DataWriteThread(){
        data=new ArrayList<>();
        sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.FRANCE);
        serialContent = SerialContent.getInstance();
    }

    @Override
    public void run(){
        startWriting();
        while (!isInterrupted()) {
            if(os != null){
                if (!data.isEmpty()) {
                    try {
                        if (data.get(0) != null) {
                            os.write(data.get(0).getBytes());
                            data.remove(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void startWriting(){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                sdf.format(Calendar.getInstance().getTime()) + ".txt");
        try {
            if (file.createNewFile()) {
                os = new FileOutputStream(file);
                os.write(("TRAINING OF " + sdf.format(Calendar.getInstance().getTime()) + '\n').getBytes());
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

    public void stopWriting(){

    }

    public static void reset(){
        try{
            data = new ArrayList<>();
            if (os != null) {
                os.close();
            }
            os = null;
        }
        catch (IOException ignored){
        }
        instance = new DataWriteThread();
    }


    @Override
    public synchronized void onNewMeasureProcessed(Measure measure) {
        String value = "";
        value += (String.valueOf(measure.getTime()));
        value += (";");
        for(int i=0; i<8; i++){
            value += (SensorManager.getInstance().isSensorActive(i)?String.valueOf(measure.getAngle(i)):"-1.0");
            value += (";");
        }
        value += ("\n");
        data.add(value);
    }

    public void write(String line){
        data.add(line);
    }

    public void newLine(){
        data.add("\n");
    }

    @Override
    public void onMovementChanged(int index) {

    }
}
