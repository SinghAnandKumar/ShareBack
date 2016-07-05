package com.fantasticfive.shareback.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sagar on 15-04-2016.
 */
public class JsonFeedback{

    String rating;
    String comment;

    public JsonFeedback(String rating, String comment){
        this.rating = rating;
        this.comment = comment;
    }

    public JsonFeedback(String str) throws JSONException {
        JSONObject jo = new JSONObject(str);
        this.rating = jo.getString("rating");
        this.comment = jo.getString("comment");
    }

    public String getJsonString(){
        JSONObject ob = new JSONObject();
        try {
            ob.put("rating", rating);
            ob.put("comment", comment);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return ob.toString();
    }

    public String getComment() {
        return comment;
    }

    public String getRating() {
        return rating;
    }
}

