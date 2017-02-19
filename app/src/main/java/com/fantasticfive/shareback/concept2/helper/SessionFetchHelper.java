package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.util.Log;

import com.fantasticfive.shareback.concept2.SharebackURLs;
import com.fantasticfive.shareback.concept2.bean.Session;
import com.fantasticfive.shareback.concept2.exception.HttpIOException;
import com.fantasticfive.shareback.concept2.exception.NoInternetException;
import com.fantasticfive.shareback.concept2.handler.HTTPConnectionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by sagar on 19/2/17.
 */
public class SessionFetchHelper implements HTTPConnectionHandler.Callback {

    private static final String TAG = "MY TAG";

    //Input
    private static final String USERNAME = "uname";

    //Output
    private static final String SESSION_NAME = "sname";
    private static final String SESSION_ID = "sid";
    private static final String INSTRUCTOR_NAME = "iname";

    //URLs
    private static final String URL = SharebackURLs.FETCH_SESSION;

    Context context;
    Callback callback;
    HTTPConnectionHandler helper;
    public SessionFetchHelper(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    public void fetch() throws NoInternetException {
        String username = testGetUsername();
        JSONObject params = new JSONObject();
        try {
            params.put(USERNAME, username);
            helper = new HTTPConnectionHandler(context, this);
            helper.makeRequest(URL, params);
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String testGetUsername(){
        String username = "not_implemented";
        Log.i(TAG, "testGetUsername: "+username);
        return username;
    }

    @Override
    public void onHttpResponse() {
        try {
            String result = helper.get();
            JSONArray array = new JSONArray(result);
            ArrayList<Session> sessions = new ArrayList<>();
            for(int i=0; i<array.length(); i++){
                Session session = new Session();
                JSONObject obj = array.getJSONObject(i);
                session.setSessionId(obj.getString(SESSION_ID));
                session.setSessionName(obj.getString(SESSION_NAME));
                session.setInstructorName(obj.getString(INSTRUCTOR_NAME));
                sessions.add(session);
            }
            callback.onSessionFetched(sessions);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            callback.onSessionFetchFailed(e);
        }
    }

    @Override
    public void onHttpConnectionFailure(HttpIOException e) {
        callback.onSessionFetchFailed(e);
    }

    public interface Callback{
        void onSessionFetched(ArrayList<Session> sessions);
        void onSessionFetchFailed(Exception e);
    }
}
