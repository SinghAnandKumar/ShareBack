package com.fantasticfive.shareback.newshareback.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by sagar on 28/7/16.
 */
public class FeedbackHelper extends AsyncTask<String, Void, Void> {

    FeedbackHelperCallback callback;
    public FeedbackHelper(FeedbackHelperCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... strings) {

        String sessionId = strings[0];
        int rating = Integer.parseInt(strings[1]);
        String comment = strings[2];

        try {
            Socket skt = new Socket(Constants.IP_FILE_SERVER, Constants.PORT_FEEDBACK);

            //Sending Request
            JSONObject main = new JSONObject();
            main.put(Constants.JSON_FB_TYPE, Constants.FB_SESSION_NAME);
            main.put(Constants.JSON_FB_SESSION_ID, sessionId);
            main.put(Constants.JSON_FB_RATING, rating);
            main.put(Constants.JSON_FB_COMMENT, comment);

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(skt.getOutputStream())
                    ), true);
            out.println( main.toString() + Constants.END_OF_MSG );
            Log.e("My Tag", "Request: "+main.toString());
            out.flush();
            //-- Sending Request

            return null;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.onFeedbackSent();
        super.onPostExecute(aVoid);
    }

    public interface FeedbackHelperCallback{
        void onFeedbackSent();
    }
}
