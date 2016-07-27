package com.fantasticfive.shareback.newshareback.connection;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by sagar on 21/7/16.
 */
public class EventHelper implements EventsPhysical.Callback{

    int eventId = 1;
    Context context;
    EventReceiveCallback callback;

    EventsPhysical sender;
    EventsPhysical receiver;

    //Constructor for Instructor
    public EventHelper(Context context){
        this.context = context;
    }
    //-- Constructor for Instructor

    //Constructor for Student
    public EventHelper(Context context, EventReceiveCallback callback, InitConnectionHelper initConnectionHelper){
        this.context = context;
        this.callback = callback;
        this.initConnectionHelper = initConnectionHelper;

        receiver = new EventsPhysical(this);
        sender = new EventsPhysical(this);
    }
    //-- Constructor for Student

    InitConnectionHelper initConnectionHelper;



    public void sendEvent(int eventName, String eventFile, int eventValue, ArrayList<InetAddress> clients){
        //Encode data
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_EVENT_ID, eventId);
            main.put(Constants.JSON_EVENT_NAME, eventName);
            main.put(Constants.JSON_EVENT_FILE, eventFile);
            main.put(Constants.JSON_EVENT_PAGE, eventValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //-- Encode data

        //Send Data
        EventsPhysical sender = new EventsPhysical(this);
        sender.sendEvent(main, eventId++, clients);
        //-- Send Data
    }

    public void sendFiles( LinkedHashSet<String> set, ArrayList<InetAddress> clients){
        //Encode data
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_EVENT_ID, eventId);
            main.put(Constants.JSON_EVENT_NAME, Constants.EVENT_FILES_ADDED);
            JSONArray arr = new JSONArray(set);
            main.put(Constants.JSON_EVENT_FILE, arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //-- Encode data

        //Send Data
        EventsPhysical sender = new EventsPhysical(this);
        sender.sendEvent(main, eventId++, clients);
        //-- Send Data
    }

    public void listenForEvents(){
        receiver.listenForEvents();
    }

    @Override
    public void onEventSent(JSONObject errMsg, int failedMsgs) {
        Toast.makeText(context, "Message sent. Failed:"+failedMsgs, Toast.LENGTH_SHORT).show();
        Log.e("My Tag", errMsg.toString());
    }

    @Override
    public void onEventReceive(JSONObject main) throws JSONException {

        String fileName="NOT_INITIALIZED";
        int pageNo = -1;
        LinkedHashSet<String> arrFiles = new LinkedHashSet<>();

        //Check if event is FILE_ADDED
        int eventType = main.getInt(Constants.JSON_EVENT_NAME);
        if(eventType == Constants.EVENT_FILES_ADDED){
            JSONArray arr = main.getJSONArray(Constants.JSON_EVENT_FILE);
            for(int i=0 ; i<arr.length(); i++){
                arrFiles.add(arr.getString(i));
            }
        }
        else{
            fileName = main.getString(Constants.JSON_EVENT_FILE);
            pageNo = main.getInt(Constants.JSON_EVENT_PAGE);
        }
        //-- Check if event is FILE_ADDED

        //Perform corresponding action
        switch (eventType){
            case Constants.EVENT_PAGE_CHANGED:
                Log.e("My Tag", "EventHelper: EVENT_PAGE_CHANGED");
                callback.onPageChangedS(fileName, pageNo);
                sendEvent(Constants.EVENT_PAGE_CHANGED, fileName, pageNo, initConnectionHelper.getClientList());
                break;
            case Constants.EVENT_FILE_CHANGED:
                Log.e("My Tag", "EventHelper: EVENT_FILE_CHANGED");
                callback.onFileChangedS(fileName, pageNo);
                sendEvent(Constants.EVENT_FILE_CHANGED, fileName, pageNo, initConnectionHelper.getClientList());
                break;
            case Constants.EVENT_FILES_ADDED:
                Log.e("My Tag", "EventHelper: EVENT_FILES_ADDED");
                callback.onFilesAddedS(arrFiles);
                sendFiles(arrFiles, initConnectionHelper.getClientList());
                break;
            case Constants.EVENT_SESSION_CLOSED:
                Log.e("My Tag", "EventHelper: EVENT_SESSION_CLOSED");
                callback.onSessionClosedS();
                sendEvent(Constants.EVENT_SESSION_CLOSED, fileName, pageNo, initConnectionHelper.getClientList());
                break;
        }
        //-- Perform corresponding action
    }

    public interface EventReceiveCallback{
        void onPageChangedS(String fileName, int pageNo);
        void onFileChangedS(String fileName, int pageNo);
        void onFilesAddedS(LinkedHashSet<String> arrFiles);
        void onSessionClosedS();
    }
}
