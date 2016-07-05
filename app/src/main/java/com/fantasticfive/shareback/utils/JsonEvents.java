package com.fantasticfive.shareback.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sagar on 22-03-2016.
 */
public class JsonEvents {

    int event, value=0;

    public JsonEvents(int event, int value){
        this.event = event;
        this.value = value;
    }

    public JsonEvents(String str) throws JSONException {
        JSONObject jo = new JSONObject(str);
        this.event = jo.getInt("event");
        this.value = jo.getInt("value");
    }

    public JsonEvents(int event){
        this.event = event;
    }

    public String getJsonString(){
        JSONObject ob = new JSONObject();
        try {
            ob.put("event", event);
            ob.put("value", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ob.toString();
    }

    public int getValue() {
        return value;
    }

    public int getEvent() {
        return event;
    }
}
