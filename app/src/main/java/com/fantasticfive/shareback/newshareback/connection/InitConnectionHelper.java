package com.fantasticfive.shareback.newshareback.connection;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;

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
        implements Runnable, InitConnectionPhysical.Callback, NsdHelper.Callback {

    int connections = Constants.MAX_CONNECTS;
    InitConnectionPhysical sktHelper;
    NsdHelper nsdHelper;

    Context context;
    ShareBucket shareBucket;
    String nsdName = Constants.NSD_BASE_NAME;

    ArrayList<InetAddress> alClients = new ArrayList<>();

    public InitConnectionHelper(Context context, ShareBucket shareBucket){
        this.context = context;
        this.shareBucket = shareBucket;
        sktHelper = new InitConnectionPhysical(this);
        nsdHelper = new NsdHelper(context, this);
    }

    @Override
    public void run() {
        openSocket();
    }

    public void openSocket(){  //To be called by root Node and InitConnectionHelper internally
        sktHelper.openSocket(Constants.PORT_TOKEN_DIST);
        nsdHelper.registerService(nsdName, Constants.PORT_TOKEN_DIST);
        sktHelper.acceptConnections();
    }

    public void startDiscovery(){ //To be Called by Joining Node
        nsdHelper.discoverServices();
    }

    @Override
    public JSONObject onRequestReceived(Socket skt) {
        if(connections>0) {

            //Creating JSON token_no, files_array, page_array
            JSONObject main = new JSONObject();
            try {
                main.put(Constants.JSON_TOKEN_NO, connections--);
                JSONArray files = new JSONArray(shareBucket.getFiles());
                JSONArray pageNos = new JSONArray(shareBucket.getPageNos());
                main.put(Constants.JSON_FILES, files);
                main.put(Constants.JSON_PAGE_NOS, pageNos);
                main.put(Constants.JSON_CURR_FILE, shareBucket.getCurrentFile());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //-- Creating JSON token_no, files_array, page_array

            alClients.add(skt.getInetAddress()); //Store Client Address
            return main;
        }
        else
            return null; //No space to connect then reject request
    }

    @Override
    public void onTokenSendSuccess() {
        if(connections <= 0){ //Check if Max tokens sent
            sktHelper.closeSocket();
            nsdHelper.unregisterService();
        }
    }


    @Override
    public void onTokenSendFailed() {
        connections++;
    }

    @Override
    public void onServiceDiscovered(NsdServiceInfo service) {

        Toast.makeText(context, "Connecting to "+service.getServiceName(), Toast.LENGTH_SHORT).show();
        JSONObject main = sktHelper.receiveToken(service);
        if(main == null){
            Toast.makeText(context, "TOKEN_RECEIVE_ERROR", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int token = main.getInt(Constants.JSON_TOKEN_NO);
            if(token > 0){
                Log.e("My Tag", "Creating ShareBucket...");
                shareBucket.copyFromJson(main);
                (new Thread(this)).start(); //Now Act as a server node
            }
            else{
                Toast.makeText(context, "No Slot To Connect, Try Again.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "TOKEN_RECEIVE_ERR: IMPROPER JSON FORMAT ", Toast.LENGTH_SHORT).show();
        }

    }

    public ArrayList<InetAddress> getClientList(){
        return alClients;
    }
}
