package com.fantasticfive.shareback.newshareback.physical;

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
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by sagar on 8/8/16.
 */
public class MessagesPhysical {

    MessagesPhysicalCallback callback;

    public MessagesPhysical(MessagesPhysicalCallback callback) {
        this.callback = callback;
    }

    public void sendMessage(String ip, int port, String msg){
        new MessageSender(ip, port).execute(msg);
    }

    public void receiveMsg(String ip, int port){
        new MessageReceiver(ip, port).execute();
    }

    public void onMsgReceived(JSONObject main){
        callback.onMessageReceived(main);
    }

    private class MessageReceiver extends AsyncTask<Void, Void, JSONObject>{

        String ip;
        int port;
        public MessageReceiver(String ip, int port){
            this.ip = ip;
            this.port = port;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                Log.e("My Tag", "Connecting...");
                Socket skt = new Socket(ip, port);
                Log.e("My Tag", "Connected");

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
                //-- Reading Response

                JSONObject main = new JSONObject(result);
                return main;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject main) {
            onMsgReceived(main);
            super.onPostExecute(main);
        }
    }

    private class MessageSender extends  AsyncTask<String, Void, Void>{

        String ip;
        int port;

        public MessageSender(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        protected Void doInBackground(String... strings) {

            String msg = strings[0];
            //Sending Request
            try {
                Socket skt = new Socket(ip, port);
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(skt.getOutputStream())
                        ), true);
                out.println( msg.toString() + Constants.END_OF_MSG );
                Log.e("My Tag", "Msg Sent: "+msg.toString());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //-- Sending Request
            return null;
        }
    }

    public interface MessagesPhysicalCallback{
        void onMessageReceived(JSONObject msg);
    }
}
