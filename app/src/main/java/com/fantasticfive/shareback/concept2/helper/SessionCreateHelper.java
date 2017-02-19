package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.util.Log;

import com.fantasticfive.shareback.concept2.SharebackURLs;
import com.fantasticfive.shareback.concept2.exception.HttpIOException;
import com.fantasticfive.shareback.concept2.exception.NoInternetException;
import com.fantasticfive.shareback.concept2.handler.HTTPConnectionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

/**
 * Created by sagar on 19/2/17.
 */
public class SessionCreateHelper implements HTTPConnectionHandler.Callback {

    //Input Params
    private static final String SESSION_NAME="sname";
    private static final String USER_NAME="uname";

    //Output Params
    private static final String SESSION_ID="sid";
    private static final String TAG = "MY TAG";

    //URL
    private static final String URL = SharebackURLs.CREATE_SESSION;


    Context context;
    Callback callback;
    HTTPConnectionHandler helper;
    String sessionName;
    public SessionCreateHelper(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    public void create(String sessionName) throws NoInternetException{
        this.sessionName = sessionName;
        try {
            helper = new HTTPConnectionHandler(context, this);
            JSONObject params = new JSONObject();
            params.put(SESSION_NAME, sessionName);
            params.put(USER_NAME, getUsername());
            helper.makeRequest(URL, params);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private String getUsername(){
        return "not_implemented";
    }

    @Override
    public void onHttpResponse() {
        try {
            String result = helper.get();
            JSONObject main = new JSONObject(result);
            String sessionId = main.getString(SESSION_ID);
            Log.i(TAG, "onHttpResponse: Session Created: "+sessionId);
            callback.onSessionCreated(sessionName, sessionId);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            callback.onSessionCreationFailed(e);
        }
    }

    @Override
    public void onHttpConnectionFailure(HttpIOException e) {
        callback.onSessionCreationFailed(e);
    }

    public interface Callback{
        void onSessionCreated(String sessionName, String sessionId);
        void onSessionCreationFailed(Exception e);
    }
}
