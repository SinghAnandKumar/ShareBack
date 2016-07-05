package com.fantasticfive.shareback.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.Globals;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Sagar on 14-04-2016.
 */
public class FeedbackSender extends AsyncTask<String, Void, String> {

    Socket skt;

    Callback callback = null;
    Activity activity = null;
    public FeedbackSender(Activity activity){
        this.callback = (Callback)activity;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {

        String message = params[0];
        String result="";
        Log.e("FeedbackSender","Message: "+message);

        try {
            skt = new Socket(Globals.serverIp, Globals.FEEDBACK_PORT);
            byte[] arr = message.getBytes();
            OutputStream os = skt.getOutputStream();
            os.write(arr);
            os.close();
            skt.close();
            Log.e("FeedbackSender", "Feedback Sent to "+Globals.serverIp);
        } catch (IOException e) {
            e.printStackTrace();
            result += e.getMessage()+"\n";
            Log.e("EventSender", e.getMessage());
        }

        if(result.equals(""))
            result = "RESULT_OK";

        return result;
    }

    @Override
    protected void onPostExecute(String s) {

        callback.onFeedbackSent();
        super.onPostExecute(s);
    }

    public interface Callback{
        void onFeedbackSent();
    }
}
