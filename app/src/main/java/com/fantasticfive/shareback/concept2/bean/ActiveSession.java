package com.fantasticfive.shareback.concept2.bean;

/**
 * Created by sagar on 20/2/17.
 */
public class ActiveSession extends Session {
    private String instructorId;
    private String instructorName;

    public ActiveSession(){
        setType(ACTIVE);
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
}
