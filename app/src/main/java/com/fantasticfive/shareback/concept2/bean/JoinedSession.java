package com.fantasticfive.shareback.concept2.bean;

/**
 * Created by sagar on 22/2/17.
 */
public class JoinedSession extends Session {
    String instructorId;

    public JoinedSession(){
        setType(JOINED);
    }
    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }
}
