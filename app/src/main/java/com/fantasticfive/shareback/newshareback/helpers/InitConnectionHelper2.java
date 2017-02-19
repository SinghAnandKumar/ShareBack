package com.fantasticfive.shareback.newshareback.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.physical.InitConnectionPhysical;
import com.fantasticfive.shareback.newshareback.physical.MessagesPhysical;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by sagar on 18/7/16.
 */
public class InitConnectionHelper2
        implements NsdHelper2.Callback
        ,MessagesPhysical.MessagesPhysicalCallback {

    NsdHelper2 nsdHelper;

    Context context;
    Callback callback;

    //Constructor for Student
    public InitConnectionHelper2(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
        nsdHelper = new NsdHelper2(context, this);
    }
    //Constructor for Student

    //Receiving Methods
    public void startDiscovery(){ //To be Called by Joining Node
        nsdHelper.discoverServices();
    }
    @Override
    public void onServiceDiscovered(final NsdServiceInfo service) {
        callback.onServiceDiscovered(service);
    }

    public void resolveService(NsdServiceInfo nsdServiceInfo){
        Log.e("My Tag", "Resolving "+nsdServiceInfo.getServiceName()+"...");
        nsdHelper.resolveService(nsdServiceInfo);
    }
    @Override
    public void onServiceResolved(NsdServiceInfo service) {

        Log.e("My Tag", "Resolved "+service.getServiceName()+"...");
        callback.onServiceResolved(service);
    }

    public void receiveMetaData(String serverIp){
        new MessagesPhysical(this).receiveMsg(serverIp, Constants.PORT_TOKEN_DIST);
    }
    @Override
    public void onMessageReceived(JSONObject main) {
        try {
            //Extract values
            int token = main.getInt(Constants.JSON_TOKEN_NO);

            //Stop Service Discovery
            nsdHelper.stopDiscovery();

            //Set File Server Ip
            Constants.IP_FILE_SERVER = main.getString(Constants.JSON_SERVER_IP);

            callback.onBucketReceived(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //-- Receiving Methods

    public interface Callback{
        void onBucketReceived(JSONObject shareBucket) throws JSONException;
        void onServiceDiscovered(NsdServiceInfo serviceInfo);
        void onServiceResolved(NsdServiceInfo serviceInfo);
    }
}