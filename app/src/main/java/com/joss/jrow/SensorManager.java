package com.joss.jrow;

public class SensorManager {

    private static final SensorManager ourInstance = new SensorManager();

    public static SensorManager getInstance() {
        return ourInstance;
    }

    private boolean[] activeSensors;

    private SensorManager() {
        activeSensors = new boolean[] {false, false, false, false, false, false, false, false};
    }

    public boolean isSensorActive(int index){
        if (activeSensors != null) {
            if (index > 7 || index < 0) {
                throw new Error("Index of " + String.valueOf(index) + " when the maximum number of sensors is 8");
            }
            return activeSensors[index];
        }
        return false;
    }

    public void activateSensor(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        activeSensors[index] = true;
    }

    public void deactivateSensor(int index){
        if(index > 7 || index <0){
            throw new Error("Index of "+String.valueOf(index)+" when the maximum number of sensors is 8");
        }
        activeSensors[index] = false;
    }

    public int numberOfActiveSensors(){
        int result = 0;
        for(int i=0; i<8; i++){
            if (isSensorActive(i)){
                result++;
            }
        }
        return result;
    }
}
