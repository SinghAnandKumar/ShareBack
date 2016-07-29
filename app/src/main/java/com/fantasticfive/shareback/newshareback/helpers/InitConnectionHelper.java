package com.fantasticfive.shareback.newshareback.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.physical.InitConnectionPhysical;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by sagar on 18/7/16.
 */
public class InitConnectionHelper
        implements InitConnectionPhysical.Callback, NsdHelper.Callback {

    int connections = Constants.MAX_CONNECTS;
    InitConnectionPhysical conPhysical;
    NsdHelper nsdHelper;

    Context context;
    InitConnectionHelperCallback callback;
    ShareBucket shareBucket;
    String nsdName = Constants.NSD_BASE_NAME;

    ArrayList<InetAddress> alClients = new ArrayList<>();

    //Constructor for Instructor
    public InitConnectionHelper(Context context, ShareBucket shareBucket){
        this.context = context;
        this.shareBucket = shareBucket;
        conPhysical = new InitConnectionPhysical(this);
        nsdHelper = new NsdHelper(context, this);
    }
    //-- Constructor for Instructor

    //Constructor for Student
    public InitConnectionHelper(Context context, InitConnectionHelperCallback callback, ShareBucket shareBucket){
        this.context = context;
        this.callback = callback;
        this.shareBucket = shareBucket;
        conPhysical = new InitConnectionPhysical(this);
        nsdHelper = new NsdHelper(context, this);
    }
    //Constructor for Student

    //Sending Methods

    public void openSocket(){  //To be called by root Node and InitConnectionHelper internally
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                socketOpenByInstructor();
            }
        });
        t.start();
    }

    private void socketOpenByInstructor(){
        conPhysical.openSocket(Constants.PORT_TOKEN_DIST);
        nsdHelper.registerService(nsdName, Constants.PORT_TOKEN_DIST);
        conPhysical.acceptConnections();
    }

    private void socketOpenByStudent(String nsdName){
        conPhysical.openSocket(Constants.PORT_TOKEN_DIST);
        nsdHelper.registerService(nsdName, Constants.PORT_TOKEN_DIST);
        conPhysical.acceptConnections();
    }

    @Override
    public void onTokenSendSuccess() {
        if(connections <= 0){ //Check if Max tokens sent
            conPhysical.closeSocket();
            nsdHelper.unregisterService();
        }
    }

    @Override
    public void onTokenSendFailed() {
        connections++;
    }


    public ArrayList<InetAddress> getClientList(){
        return alClients;
    }

    @Override
    public JSONObject onRequestReceived(Socket skt) {
        if(connections>0) {

            //Creating JSON token_no, session_id, files_array, page_array
            JSONObject main = new JSONObject();
            try {
                main.put(Constants.JSON_TOKEN_NO, connections--);
                JSONArray files = new JSONArray(shareBucket.getFiles());
                JSONArray pageNos = new JSONArray(shareBucket.getPageNos());

                main.put(Constants.JSON_SERVER_IP, Constants.IP_FILE_SERVER);

                //Sending ShareBucket
                main.put(Constants.JSON_FB_SESSION_ID, shareBucket.getSessionId());
                main.put(Constants.JSON_FILES, files);
                main.put(Constants.JSON_PAGE_NOS, pageNos);
                main.put(Constants.JSON_CURR_FILE, shareBucket.getCurrentFile());
                //Sending ShareBucket
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //-- Creating JSON token_no, session_id, files_array, page_array

            alClients.add(skt.getInetAddress()); //Store Client Address
            return main;
        }
        else
            return null; //No space to connect then reject request
    }
    //-- Sending Methods

    //Receiving Methods
    public void startDiscovery(){ //To be Called by Joining Node
        nsdHelper.discoverServices();
    }

    @Override
    public void onServiceDiscovered(final NsdServiceInfo service) {

        //Receive Token from Server
        Toast.makeText(context, "Connecting to "+service.getServiceName(), Toast.LENGTH_SHORT).show();
        JSONObject main = conPhysical.receiveToken(service);
        if(main == null){
            Toast.makeText(context, "TOKEN_RECEIVE_ERROR", Toast.LENGTH_SHORT).show();
            return;
        }
        //-- Receive Token from Server

        try {
            final int token = main.getInt(Constants.JSON_TOKEN_NO);
            if(token > 0){

                //Create ShareBucket
                Log.e("My Tag", "Creating ShareBucket...");
                shareBucket.copyFromJson(main);
                //-- Create ShareBucket

                //Stop Discovery
                nsdHelper.stopDiscovery();
                //-- Stop Discovery

                //Set File Server IP
                SharedPreferences pref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constants.PREF_SERVER_IP, main.getString(Constants.JSON_SERVER_IP));
                editor.commit();
                Constants.IP_FILE_SERVER = main.getString(Constants.JSON_SERVER_IP);
                //-- Set File Server IP

                //Register own Services
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        socketOpenByStudent(service.getServiceName() + "." + token);
                    }
                });
                t.start();
                //-- Register own Services

                //Callback to Activity
                callback.onServerFound(service.getHost(), main);
                //-- Callback to Activity

            }
            else{
                Toast.makeText(context, "No Slot To Connect, Try Again.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "TOKEN_RECEIVE_ERR: IMPROPER JSON FORMAT ", Toast.LENGTH_SHORT).show();
        }


    }
    //-- Receiving Methods

    public void packUp(){
        nsdHelper.unregisterService();
        conPhysical.closeSocket();
    }

    public interface InitConnectionHelperCallback{
        void onServerFound( InetAddress serverAddress, JSONObject main);
    }
}