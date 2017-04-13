package com.joss.jrow;

public class SerialContent {
    private static final SerialContent ourInstance = new SerialContent();

    public static SerialContent getInstance() {
        return ourInstance;
    }

    private volatile String serial;

    private SerialContent() {
    }

    public void addToSerial(String message){
        serial += message;
        serial+= "\n";
    }

    public String getSerial() {
        return serial;
    }
}
