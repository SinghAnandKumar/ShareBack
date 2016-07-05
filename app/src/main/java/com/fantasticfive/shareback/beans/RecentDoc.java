package com.fantasticfive.shareback.beans;

import java.util.Date;

/**
 * Created by Sagar on 25-03-2016.
 */
public class RecentDoc {
    int sessionId;
    String name;
    Date date;

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date d) {
        this.date = d;
    }
}
