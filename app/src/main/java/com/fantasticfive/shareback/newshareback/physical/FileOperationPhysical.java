package com.fantasticfive.shareback.newshareback.physical;

import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by sagar on 30/7/16.
 */
public class FileOperationPhysical extends AsyncTask<JSONObject, Void, Void> {

    Callback callback;
    public FileOperationPhysical(Callback callback){
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(JSONObject... jsonObjects) {

        try {
            Socket skt = new Socket(Constants.IP_FILE_SERVER, Constants.PORT_FILE_OEPRATIONS);
            send(jsonObjects[0], skt);

            //receive***
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.onOperationPerformed();
        super.onPostExecute(aVoid);
    }

    public void send(JSONObject main, Socket skt) throws IOException {

        PrintWriter out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(skt.getOutputStream())
                ), true);
        out.println( main.toString() + Constants.END_OF_MSG );
        Log.e("My Tag", "Request: "+main.toString());
        out.flush();
    }

    public String receive(Socket skt) throws IOException {
        String result = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
        String temp = "";
        while((temp = br.readLine()) != null){
            if(temp.contains(Constants.END_OF_MSG)){
                temp = temp.replace(Constants.END_OF_MSG, "");
                result+=temp;
                break;
            }
            result += temp;
        }

        return result;
    }

    public interface Callback{
        void onOperationPerformed();
    }
}
