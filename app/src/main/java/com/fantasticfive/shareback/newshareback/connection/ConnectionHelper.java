package com.fantasticfive.shareback.newshareback.connection;

import android.content.Context;
import android.content.pm.ServiceInfo;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.widget.Toast;

import com.fantasticfive.shareback.newshareback.Constants;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by sagar on 18/7/16.
 */
public class ConnectionHelper
        implements Runnable, SocketHelper.Callback, NsdHelper.Callback {

    int connections = Constants.MAX_CONNECTS;
    SocketHelper sktHelper;
    NsdHelper nsdHelper;

    Context context;
    ArrayList<Socket> clients;
    String nsdName = Constants.NSD_BASE_NAME;

    public ConnectionHelper(Context context){
        sktHelper = new SocketHelper(this);
        this.context = context;
    }

    @Override
    public void run() {
        openSocket();
    }

    public void openSocket(){  //To be called by root Node and ConnectionHelper internally
        sktHelper.openSocket(Constants.PORT_TOKEN_DIST);
        nsdHelper = new NsdHelper(context, this);
        nsdHelper.registerService(nsdName, Constants.PORT_TOKEN_DIST);
    }

    public void startDiscovery(){ //To be Called by Joining Node
        nsdHelper.discoverServices();
    }

    @Override
    public int onRequestReceived(Socket skt) {

        clients.add(skt);

        if(connections>0)
            return connections--;
        else
            return -1; //No space to connect
    }

    @Override
    public void onTokenSendFailed() {
        connections++;
    }

    @Override
    public void onServerSocketClosed() {
        nsdHelper.unregisterService();
    }

    @Override
    public void onServiceDiscovered(NsdServiceInfo service) {

        Toast.makeText(context, "Connecting to "+service.getServiceName(), Toast.LENGTH_SHORT).show();
        nsdName = sktHelper.receiveToken(service);
        nsdHelper.stopDiscovery();
        Handler handler = new Handler();
        handler.post(this);
    }
}
