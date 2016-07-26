package com.fantasticfive.shareback.newshareback.connection;

import android.net.nsd.NsdServiceInfo;
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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sagar on 19/7/16.
 */
public class InitConnectionPhysical {

    ServerSocket servSkt;

    Callback callback;
    public InitConnectionPhysical(Callback callback){
        this.callback = callback;
    }

    public void openSocket(int port){
        try {
            Log.e("My Tag", "Opening Port: "+port);
            servSkt = new ServerSocket(port);
            Log.e("My Tag", "Server Started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptConnections(){
        try {
            //Sending Tokens
            while (true) {
                Log.e("My Tag", "Waiting...");
                Socket skt = servSkt.accept();
                Log.e("My Tag", "Connected");
                JSONObject msg = callback.onRequestReceived(skt);
                boolean success = sendMsg(skt, msg);
                if (success) {
                    callback.onTokenSendSuccess();
                }
                else {
                    callback.onTokenSendFailed();
                }
            }
            //-- Sending Tokens
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void closeSocket(){
        try {
            servSkt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject receiveToken(NsdServiceInfo serviceInfo){
        String parentName = serviceInfo.getServiceName();
        String ip = serviceInfo.getHost().getHostAddress();
        int port = serviceInfo.getPort();

        try {
            Log.e("My Tag", "Connecting to "+serviceInfo.getServiceName());
            Socket skt = new Socket(ip, port);
            Log.e("My String", "Connected");
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

    private boolean sendMsg(Socket skt, JSONObject msg){   //For Server node
        //Sending Request
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(skt.getOutputStream())
                    ), true);
            out.println( msg.toString() + Constants.END_OF_MSG );
            Log.e("My Tag", "Msg Sent: "+msg.toString());
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
        //-- Sending Request
    }

    public interface Callback{
        JSONObject onRequestReceived(Socket skt);
        void onTokenSendSuccess();
        void onTokenSendFailed();
    }
}
