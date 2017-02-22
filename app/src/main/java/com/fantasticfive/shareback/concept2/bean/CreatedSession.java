package com.fantasticfive.shareback.concept2.bean;

import java.util.ArrayList;

/**
 * Created by sagar on 19/2/17.
 */
public class CreatedSession extends Session{

    ArrayList<String> joinedUsers;
    ArrayList<String> comments;
    int[] rating;

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

    public int[] getRating() {
        return rating;
    }

    public void setRating(int[] rating) {
        this.rating = rating;
    }
}
