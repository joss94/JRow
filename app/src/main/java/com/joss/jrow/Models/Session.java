package com.joss.jrow.Models;

import android.content.Context;
import android.content.SharedPreferences;

import com.joss.jrow.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Session{
    private static Session session = new Session();

    private static List<String> rowers = new ArrayList<>(9);
    private static Date date;

    public static Session getSession(){
        return session;
    }

    private Session() {
        rowers = new ArrayList<>(9);
    }

    public List<String> getRowers() {
        return rowers;
    }

    public void setRowers(List<String> rowers) {
        Session.rowers = rowers;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        Session.date = date;
    }

    public static void initializeSession(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("JROW_SESSION", Context.MODE_PRIVATE);
        for (int i=0; i<9; i++) {
            if(i == 8){
                session.getRowers().add(sharedPrefs.getString("COX", context.getString(R.string.cox)));
            }
            else{
                session.getRowers().add(sharedPrefs.getString("ROWER_"+i, context.getString(R.string.rower) + String.valueOf(i+1)));
            }
        }

    }

    public static void save(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("JROW_SESSION", Context.MODE_PRIVATE);
        for(int i=0; i<9; i++){
            if(i==8){
                sharedPreferences.edit().putString("COX", rowers.get(i)).apply();
            }
            else{
                sharedPreferences.edit().putString("ROWER_"+i, rowers.get(i)).apply();
            }
        }
    }
}
