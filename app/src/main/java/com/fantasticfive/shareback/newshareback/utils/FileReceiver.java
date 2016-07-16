package com.fantasticfive.shareback.newshareback.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by sagar on 16/7/16.
 */
public class FileReceiver extends AsyncTask<String, Void, String> {

    Context context;
    Callback callback;

    public FileReceiver(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String path = strings[0];   //{ /d1/d2/t1.txt }

        String filename = downloadFile(path);

        return filename;
    }

    @Override
    protected void onPostExecute(String fileName) {

        callback.onFileReceive(fileName);
        super.onPostExecute(fileName);
    }

    private String downloadFile(String path){

        Socket skt = null;
        byte[] fileByte = new byte[8192];
        InputStream is;
        FileOutputStream fos;
        int readSize;

        //Encode into JSON
        String jsonStr = encode(path);
        //-- Encode into JSON

        try{
            skt = new Socket(Constants.IP_FILE_SERVER, Constants.PORT_FILE_S2C);

            is = skt.getInputStream();

            //Creating Local Dir Structure
            File file = new File(Constants.DIR_ROOT + path);
            String temp = file.getParent();
            File parent = new File(temp);
            parent.mkdirs();
            //-- Creating Local DIr Structure

            //Sending File Name
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(skt.getOutputStream())
                    ), true);
            out.println( jsonStr.toString() + Constants.END_OF_MSG );
            Log.e("My Tag", "Request: "+jsonStr.toString());
            out.flush();
            //-- Sending File Name

            //Downloading File
            fos = new FileOutputStream(file);//---actual fileName
            while((readSize=is.read(fileByte))>0) {
                Log.e("My Tag", "I am Here");
                fos.write(fileByte, 0, readSize);
                fos.flush();
            }
            fos.close();
            skt.close();
            //--Downloading File
        } catch (IOException ex) {
            Log.e("My Tag", ex.getMessage());
        }

        return Constants.DIR_ROOT + path; //{ /opt/phone/Shareback/d1/d2/t1.txt }
    }

    public String encode(String path){
        JSONObject main = null;
        try {
            main = new JSONObject();
            main.put(Constants.JSON_FILE_DWNLD, path);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return main.toString();
    }

    public interface Callback{
        void onFileReceive(String location);
    }
}
