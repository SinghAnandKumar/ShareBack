package com.fantasticfive.shareback.newshareback.physical;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar on 13/7/16.
 */
public class DirPhysical extends AsyncTask<String, Void, DirContentsBean> {

    Context context = null;
    Callback callback;
    public DirPhysical(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected DirContentsBean doInBackground(String... strings) {
        return getFiles(strings[0]);
    }

    private DirContentsBean getFiles(String dir){
        String result = sendReq(dir);
        DirContentsBean bean = decodeResponse(result);
        return bean;
    }

    private String sendReq(String path){

        String result = "";
        try {
            Log.e("My Tag", "Connecting....");
            Socket skt = new Socket(Constants.IP_FILE_SERVER , Constants.PORT_LS);
            Log.e("My Tag", "Connected");


            //Sending Request
            JSONObject main = new JSONObject();
            main.put(Constants.JSON_LIST_DIR, path);

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
            while((temp = br.readLine()) != null){
                if(temp.contains(Constants.END_OF_MSG)){
                    temp = temp.replace(Constants.END_OF_MSG, "");
                    result+=temp;
                    break;
                }
                result += temp;
            }
            //-- Reading Response

        } catch (IOException e) {
//            Toast.makeText(context, "Can't Connect to Server. Please Retry", Toast.LENGTH_SHORT).show();
            Log.e("My Tag", "**"+e.getMessage()+"**");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("My Tag", "Result: "+result);
        return result;
    }

    private DirContentsBean decodeResponse(String result){

        DirContentsBean bean = new DirContentsBean();
        try {
            JSONObject main = new JSONObject(result);
            JSONArray dirs = main.getJSONArray(Constants.JSON_DIRS);
            JSONArray files = main.getJSONArray(Constants.JSON_FILES);
            for(int i=0; i<dirs.length(); i++){
                bean.addDir(dirs.getString(i));
            }
            for(int i=0; i<files.length(); i++){
                bean.addFile(files.getString(i));
            }

        } catch (JSONException e) {
            Log.e("My Tag", "JSON Error: "+result);
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    protected void onPostExecute(DirContentsBean dirContentsBean) {

        callback.onListReceive();
        super.onPostExecute(dirContentsBean);
    }

    public interface Callback{
        void onListReceive();
    }
}
