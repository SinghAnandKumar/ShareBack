package com.fantasticfive.shareback.newshareback.helpers;

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
public class SessionHelper extends AsyncTask<String, Void, String> {

    SessionHelperCallback callback;
    public SessionHelper(SessionHelperCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Socket skt = new Socket(Constants.IP_FILE_SERVER, Constants.PORT_FEEDBACK);
            String sessionName = strings[0];

            //Sending Request
            JSONObject main = new JSONObject();
            main.put(Constants.JSON_FB_TYPE, Constants.FB_EVENT_CREATE_SESSION);
            main.put(Constants.JSON_FB_SESSION_NAME, sessionName);

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(skt.getOutputStream())
                    ), true);
            out.println( main.toString() + Constants.END_OF_MSG );
            Log.e("My Tag", "Request: "+main.toString());
            out.flush();
            //-- Sending Request

            //Reading Response
            BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            String temp = "";
            String result = "";
            while((temp = br.readLine()) != null){
                if(temp.contains(Constants.END_OF_MSG)){
                    temp = temp.replace(Constants.END_OF_MSG, "");
                    result+=temp;
                    break;
                }
                result += temp;
            }
            //-- Reading Response

            return result;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String sessionId) {
        callback.onSessionIdReceived();
        super.onPostExecute(sessionId);
    }

    public interface SessionHelperCallback{
        void onSessionIdReceived();
    }
}
