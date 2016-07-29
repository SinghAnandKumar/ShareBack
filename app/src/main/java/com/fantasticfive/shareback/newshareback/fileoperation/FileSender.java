package com.fantasticfive.shareback.newshareback.fileoperation;

import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by sagar on 16/7/16.
 */
public class FileSender extends AsyncTask<String, Void, Boolean> {

    Callback callback;
    public FileSender(Callback callback){
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String localPath = strings[0];
        String servPath = strings[1] + (new File(strings[0])).getName();

        boolean status = uploadFile(localPath, servPath);

        return status;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        callback.onFileUploaded(aBoolean);
        super.onPostExecute(aBoolean);
    }

    private boolean uploadFile(String localPath, String servPath ){

        try {

            Socket skt = new Socket(Constants.IP_FILE_SERVER, Constants.PORT_FILE_C2S);

            //Sending File Name
            JSONObject main = new JSONObject();
            main.put(Constants.JSON_FILE_DWNLD, servPath);

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(skt.getOutputStream())
                    ), true);
            out.println( main.toString() + Constants.END_OF_MSG );
            Log.e("My Tag", "Request: "+main.toString());
            out.flush();
            //-- Sending File Name

            //Receiving Dummy Packet
            BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            String result = "";
            Log.e("My Tag",br.readLine());
            //-- Receiving Dummy Packet

            //Sending File
            File dwnldFile = new File(localPath); //Actual File_Path to Upload
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dwnldFile));
            BufferedOutputStream bos =new BufferedOutputStream(skt.getOutputStream());

            int readSize;
            byte fileBytes[] = new byte[Constants.BUFFER_SIZE];

            while((readSize=bis.read(fileBytes))>0){ //Sending File Data
                bos.write(fileBytes,0,readSize);
                bos.flush();
            }
            bos.close();
            skt.close();
            //--Sending File

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface Callback{
        void onFileUploaded(boolean status);
    }
}
