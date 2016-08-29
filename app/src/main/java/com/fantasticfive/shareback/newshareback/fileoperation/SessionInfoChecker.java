package com.fantasticfive.shareback.newshareback.fileoperation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.beans.SessionInfoBean;
import com.fantasticfive.shareback.newshareback.physical.MessagesPhysical;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by sagar on 2/8/16.
 */
public class SessionInfoChecker
        extends AsyncTask<String, Void, ArrayList<SessionInfoBean>>{

    Context context;
    SessionInfoCallback callback;
    MessagesPhysical msgs;
    ArrayList<SessionInfoBean> alSessionInfo;

    public SessionInfoChecker(Context context, SessionInfoCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected ArrayList<SessionInfoBean> doInBackground(String... strings) {

        JSONObject main;
        try {
            //Request Message for SESSION_INFO
            main = new JSONObject();
            main.putOpt(Constants.JSON_FB_TYPE, Constants.JSON_FB_SESSIONS_INFO);
            main.put(Constants.JSON_FB_INSTRUCTOR_ID, "Test Id");

            Log.e("My Tag", "Retrieving Sessions...");
            //Send Request
            Socket skt = new Socket(Constants.IP_FILE_SERVER, Constants.PORT_FEEDBACK);
            sendMsg(skt, main.toString());
            String msg = receiveMsg(skt);

            //Prepare ArrayList
            JSONArray arrSessionInfo = new JSONArray(msg);
            ArrayList<SessionInfoBean> al = new ArrayList<>();
            for(int i=0; i<arrSessionInfo.length(); i++){
                SessionInfoBean bean = new SessionInfoBean();

                main = arrSessionInfo.getJSONObject(i);
                bean.setSessionId(main.getString(Constants.JSON_FB_SESSION_ID));
                bean.setSessionName(main.getString(Constants.JSON_FB_SESSION_NAME));
                bean.setTimeStamp(main.getString(Constants.JSON_FB_TIMESTAMP));

                int rating[] = new int[Constants.NUM_OF_RATINGS];
                JSONArray arr = main.getJSONArray(Constants.JSON_FB_RATING);
                for(int j=0; j<arr.length(); j++){
                    rating[j] = arr.getInt(j);
                }
                bean.setRating(rating);

                al.add(bean);
            }
            //-- Prepare ArrayList
            return al;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<SessionInfoBean> sessionInfoBean) {
        if(!isCancelled()){
            callback.onSessionInfoResponse(sessionInfoBean);
        }
        super.onPostExecute(sessionInfoBean);
    }

    private void sendMsg(Socket skt, String msg) throws IOException {
        //Sending Request
        PrintWriter out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(skt.getOutputStream())
                ), true);
        out.println( msg.toString() + Constants.END_OF_MSG );
        Log.e("My Tag", "Msg Sent: "+msg.toString());
        out.flush();
        //-- Sending Request
    }

    private String receiveMsg(Socket skt){
        try {
            Log.e("My Tag", "Receiving Sessions...");
            //Reading Response
            BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            String temp;
            String result = "";
            while((temp = br.readLine()) != null){
                if(temp.contains(Constants.END_OF_MSG)){
                    temp = temp.replace(Constants.END_OF_MSG, "");
                    result+=temp;
                    break;
                }
                result += temp;
            }
            Log.e("My Tag", "Msg Received:"+result);
            return result;
            //-- Reading Response
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface SessionInfoCallback {
        void onSessionInfoResponse(ArrayList<SessionInfoBean> sessionInfoBean);
    }
}
