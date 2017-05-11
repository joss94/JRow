package com.joss.jrow.Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Session {

    private static Session session = new Session();

    private static List<String> rowers;
    private static Date date;

    public static Session getSession(){
        return session;
    }

    private Session() {
        rowers = new ArrayList<>();
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
}
