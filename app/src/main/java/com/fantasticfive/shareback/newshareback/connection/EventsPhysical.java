package com.fantasticfive.shareback.newshareback.connection;

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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * Created by sagar on 21/7/16.
 */
public class EventsPhysical {

    int latestEvent = 0;
    Callback callback;
    ServerSocket servSkt;

    public EventsPhysical(Callback callback) {
        this.callback = callback;
    }

    public void sendEvent(final JSONObject main, final int eventId, final ArrayList<InetAddress> clients){
        (new AsyncEventSender(main, eventId, clients)).execute();
    }

    public void waitForEvent(){
        try {
            servSkt = new ServerSocket(Constants.PORT_EVENT_DIST);
            Log.e("My Tag", "Waiting For Event...");
            Socket skt = servSkt.accept();
            Log.e("My String", "Connected");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAndCheck(Socket skt){
        try {

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

            //Filter Latest Event
            JSONObject main = new JSONObject(result);
            int eventId = main.getInt(Constants.JSON_EVENT_ID);
            if(eventId > latestEvent){
                latestEvent = eventId; //Update latest eventId
                callback.onEventReceive(main);
            }
            //-- Filter Latest Event

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int checkAndSend(JSONObject main, int eventId, ArrayList<InetAddress> clients) throws JSONException {

        if((main.getInt(Constants.JSON_EVENT_NAME) == Constants.EVENT_PAGE_CHANGED) && eventId > latestEvent  ){  //Check for latest event
            latestEvent = eventId;

            return send(main, clients);
        }
        else{
            return send(main, clients);
        }
    }

    private int send(JSONObject main, ArrayList<InetAddress> clients){
        int failed = 0;
        for(InetAddress address : clients){
            try {

                SocketAddress sktAddress = new InetSocketAddress(address, Constants.PORT_EVENT_DIST);
                Socket skt = new Socket();
                skt.connect(sktAddress, Constants.SKT_TIME_OUT);
                sendMsg(skt, main.toString());
                skt.close();
            } catch (IOException e) {
                failed++;
                e.printStackTrace();
            }
        }
        return failed;
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

    class AsyncEventSender extends AsyncTask<Void, Void, Integer>{
        JSONObject main;
        int eventId;
        ArrayList<InetAddress> clients;

        public AsyncEventSender(JSONObject main, int eventId, ArrayList<InetAddress> clients) {
            this.main = main;
            this.eventId = eventId;
            this.clients = clients;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int failed=clients.size();
            try {
                failed = checkAndSend(main, eventId, clients);
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }
            return failed;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            callback.onEventSent(main, integer);
            super.onPostExecute(integer);
        }
    }

    public interface Callback{
        void onEventSent(JSONObject msg, int failedMsgs);
        void onEventReceive(JSONObject main) throws JSONException;
    }
}
