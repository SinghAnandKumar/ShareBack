package com.fantasticfive.shareback.concept2.bean;

/**
 * Created by sagar on 22/2/17.
 */
public class Session {
    final String JOINED = "joined";
    final String CREATED = "created";
    final String ACTIVE = "active";

    String sessionId;
    String sessionName;
    String type;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
