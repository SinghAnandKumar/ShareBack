package com.fantasticfive.shareback.concept2.bean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sagar on 19/2/17.
 */
public class CreatedSession extends Session{

    ArrayList<String> joinedUsers = new ArrayList<>();
    HashMap<String, String> comments = new HashMap<>();
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

    public HashMap<String, String> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, String> comments) {
        this.comments = comments;
    }

    public Rating getRatings() {
        return ratings;
    }

    public void setRatings(Rating ratings) {
        this.ratings = ratings;
    }
}
