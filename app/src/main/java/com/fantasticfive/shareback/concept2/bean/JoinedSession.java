package com.fantasticfive.shareback.concept2.bean;

/**
 * Created by sagar on 22/2/17.
 */
public class JoinedSession extends Session {
    String instructorId;
    String instructorName;
    String comment=null;
    int rating=0;

    public JoinedSession(){
        setType(JOINED);
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
