package com.fantasticfive.shareback.beans;

import java.util.Date;

/**
 * Created by Sagar on 18-04-2016.
 */
public class SessionDetails {

    public int sessionId = 0;
    public String doc;
    public Date date;
    public int stars[];

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int[] getStars() {
        return stars;
    }

    public void setStars(int[] stars) {
        this.stars = stars;
    }
}
