package com.fantasticfive.shareback.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.Globals;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Sagar on 22-03-2016.
 */
public class EventSender extends AsyncTask<String, Void, String> {

    Socket skt;

    @Override
    protected String doInBackground(String... params) {

        String message = params[0];
        String result="";
        Log.e("EventSender","Message: "+message);
        for(String clientIp: Globals.clientIps){
            try {
                skt = new Socket(clientIp, Globals.EVENT_PORT);
                byte[] arr = message.getBytes();
                OutputStream os = skt.getOutputStream();
                os.write(arr);
                os.close();
                skt.close();
                Log.e("EventSender", "Event Sent to "+clientIp);
            } catch (IOException e) {
                e.printStackTrace();
                result += e.getMessage()+"\n";
                Log.e("EventSender", e.getMessage());
            }
        }
        if(result.equals(""))
            result = "RESULT_OK";

        return result;
    }

    public interface Callback{
        void onEventSent();
    }
}
