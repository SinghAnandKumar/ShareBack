package com.fantasticfive.shareback.newshareback.physical;

import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by sagar on 5/11/16.
 */
public class RequestResponsePhysical extends AsyncTask<String, Void, String> {

    String ip;
    int port;
    Callback callback;
    public RequestResponsePhysical(String ip, int port, Callback callback){
        this.ip = ip;
        this.port = port;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String requestDto = strings[0];


        try {
            //Sending Request
            Socket skt = new Socket(ip, port);
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(skt.getOutputStream())
                    ), true);
            out.println( requestDto.toString() + Constants.END_OF_MSG );
            //-- Sending Request
            Log.e("My Tag", "Msg Sent: "+requestDto.toString());

            //Reading Response
            BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            String temp;
            String result = "";
            while ((temp = br.readLine()) != null) {
                if (temp.contains(Constants.END_OF_MSG)) {
                    temp = temp.replace(Constants.END_OF_MSG, "");
                    result += temp;
                    break;
                }
                result += temp;
            }
            //-- Reading Response
            Log.e("My Tag", "Msg Received:" + result);

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //-- Sending Request
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onResponse(result);
        super.onPostExecute(result);
    }

    public interface Callback{
        void onResponse(String result);
    }
}
