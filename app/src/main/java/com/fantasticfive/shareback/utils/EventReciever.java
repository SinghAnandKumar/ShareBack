package com.fantasticfive.shareback.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.Globals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sagar on 22-03-2016.
 */
public class EventReciever extends AsyncTask<String, Void, String>{

    ServerSocket ss = null;
    Callback callback;
    public EventReciever(Callback callback){
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String result="";
        try {
            ss = new ServerSocket(Globals.EVENT_PORT);
            Socket skt = ss.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            result = br.readLine();
            br.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Event Asynctask", result);
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        callback.onEventReceive();
        super.onPostExecute(s);
    }

    public void close(){
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface Callback{
        public void onEventReceive();
    }
}