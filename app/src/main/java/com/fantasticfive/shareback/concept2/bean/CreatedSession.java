package com.fantasticfive.shareback.concept2.bean;

import java.util.ArrayList;

/**
 * Created by sagar on 19/2/17.
 */
public class CreatedSession extends Session{

    ArrayList<String> joinedUsers = new ArrayList<>();
    ArrayList<String> comments = new ArrayList<>();
    Rating ratings = new Rating();

    public CreatedSession(){
        setType(CREATED);
    }

    public ArrayList<String> getJoinedUsers() {
        return joinedUsers;
    }

    public void setJoinedUsers(ArrayList<String> joinedUsers) {
        this.joinedUsers = joinedUsers;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public Rating getRatings() {
        return ratings;
    }

    public void setRatings(Rating ratings) {
        this.ratings = ratings;
    }
}
