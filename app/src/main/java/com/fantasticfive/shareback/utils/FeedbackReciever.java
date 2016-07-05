package com.fantasticfive.shareback.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.Globals;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sagar on 14-04-2016.
 */
public class FeedbackReciever extends AsyncTask<String, Void, String> {

    ServerSocket ss = null;
    Callback callback;

    public FeedbackReciever(Activity activity){
        this.callback = (Callback) activity;
    }

    @Override
    protected String doInBackground(String... params) {
        String result="";
        try {
            Log.e("FeedbackReciever", "Waiting.......");
            ss = new ServerSocket(Globals.FEEDBACK_PORT);
            Socket skt = ss.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            result = br.readLine();
            br.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Feedback Recieved", result);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        String rating="", comment="";
        JsonFeedback jsonFb = null;
        try {
            jsonFb = new JsonFeedback(result);
            rating = jsonFb.getRating();
            comment = jsonFb.getComment();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callback.onFeedbackRecieve(rating, comment);
        super.onPostExecute(result);
    }

    public interface Callback{
        public void onFeedbackRecieve(String rating, String comment);
    }
}
