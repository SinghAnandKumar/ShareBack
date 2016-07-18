package com.fantasticfive.shareback.newshareback.connection;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import java.net.ServerSocket;

/**
 * Created by sagar on 18/7/16.
 */
public class NsdHelper {

    Context context;
    NsdManager mNsdManager;
    NsdManager.RegistrationListener mRegistrationListener;
    NsdManager.DiscoveryListener mDiscoveryListener;

    String serviceName = "";

    final String SERVICE_TYPE = ".http._tcp.";

    public NsdHelper(Context context, String serviceName){
        this.serviceName = serviceName;
        this.context = context;

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void registerService(int port){
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        initRegistrationListener();

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void discoverServices(){
        initDiscoveryListener();
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener );
    }

    void initDiscoveryListener(){

        mDiscoveryListener = new NsdManager.DiscoveryListener(){
            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                Toast.makeText(context, "Discovery Start Failed", Toast.LENGTH_SHORT).show();
                Log.e("My Tag", s);
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                Toast.makeText(context, "Discovery Stop Failed", Toast.LENGTH_SHORT).show();
                Log.e("My Tag", s);
            }

            @Override
            public void onDiscoveryStarted(String s) {
                Toast.makeText(context, "Discovery Started", Toast.LENGTH_SHORT).show();
                Log.e("My Tag", s);
            }

            @Override
            public void onDiscoveryStopped(String s) {
                Toast.makeText(context, "Discovery Stoped", Toast.LENGTH_SHORT).show();
                Log.e("My Tag", s);
            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(context, "Service Found: "+nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(context, nsdServiceInfo.getServiceName()+ " Service Lost", Toast.LENGTH_SHORT).show();
            }
        };

    }

    void initRegistrationListener(){

        mRegistrationListener = new NsdManager.RegistrationListener(){

            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show();
                Log.e("My Tag", nsdServiceInfo.toString());
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Toast.makeText(context, "Unregistration Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(context, "Service Registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(context, "Service Unregistered", Toast.LENGTH_SHORT).show();
            }
        };

    }

}
