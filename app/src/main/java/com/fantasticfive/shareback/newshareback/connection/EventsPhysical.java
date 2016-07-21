package com.fantasticfive.shareback.newshareback.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * Created by sagar on 21/7/16.
 */
public class EventsPhysical {

    int latestEvent = 0;
    Callback callback;

    public EventsPhysical(Callback callback) {
        this.callback = callback;
    }

    public void sendEvent(final JSONObject main, final int eventId, final ArrayList<InetAddress> clients){
        (new AsyncEventSender(main, eventId, clients)).execute();
    }

    private int checkAndSend(JSONObject main, int eventId, ArrayList<InetAddress> clients){

        if(eventId > latestEvent){  //Check for latest event
            latestEvent = eventId;

            int failed = 0;
            for(InetAddress address : clients){
                try {

                    SocketAddress sktAddress = new InetSocketAddress(address, Constants.PORT_EVENT_DIST);
                    Socket skt = new Socket();
                    skt.connect(sktAddress, Constants.SKT_TIME_OUT);
                    sendMsg(skt, main.toString());
                } catch (IOException e) {
                    failed++;
                    e.printStackTrace();
                }
            }
            return failed;
        }
        else{
            return 0;
        }
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
            int failed = checkAndSend(main, eventId, clients);
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
    }
}
