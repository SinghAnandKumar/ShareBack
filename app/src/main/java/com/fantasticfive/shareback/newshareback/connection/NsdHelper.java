package com.fantasticfive.shareback.newshareback.connection;

import android.content.Context;
import android.content.pm.ServiceInfo;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.newshareback.Constants;

import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by sagar on 18/7/16.
 */
public class NsdHelper {

    Context context;
    Callback callback;
    NsdManager mNsdManager;
    NsdManager.RegistrationListener mRegistrationListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.ResolveListener mResolveListener;

    String serviceName = "";
    String TAG = "My Tag";

    NsdServiceInfo nearestServ = null;
    int serviceCounter = 0;

    final String SERVICE_TYPE = "_http._tcp.";

    public NsdHelper(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void registerService(String serviceName, int port){
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(serviceName);
        this.serviceName = serviceName;
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        initRegistrationListener();

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void discoverServices(){
        initDiscoveryListener();
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener );
    }

    public void unregisterService(){
        mNsdManager.unregisterService(mRegistrationListener);
    }

    public void stopDiscovery(){
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    void initDiscoveryListener(){

        mDiscoveryListener = new NsdManager.DiscoveryListener(){
            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                Toast.makeText(context, "Discovery Start Failed", Toast.LENGTH_SHORT).show();http://mr-jatt.com/album/hindi-single-songs/amjad-nadeem-bandeyaa-jazbaa-hlux.html
                Log.e("My Tag", s);
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                Toast.makeText(context, "Discovery Stop Failed", Toast.LENGTH_SHORT).show();
                Log.e("My Tag", s);
            }

            @Override
            public void onDiscoveryStarted(String s) {
                //Toast.makeText(context, "Discovery Started", Toast.LENGTH_SHORT).show();
                Log.e("My Tag", "Discovery Started: "+s);
            }

            @Override
            public void onDiscoveryStopped(String s) {
                Toast.makeText(context, "Discovery Stoped", Toast.LENGTH_SHORT).show();
                Log.e("My Tag", s);
            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {

                if(!nsdServiceInfo.getServiceName().equals(serviceName)) {
                    Log.e(TAG, "Service Found: "+nsdServiceInfo.getServiceName());
                    Toast.makeText(context, "Service Found: " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();

                    if(nsdServiceInfo.getServiceName().contains(Constants.NSD_BASE_NAME)) {  //If EShareback service then resolve
                        initResolveListener();
                        mNsdManager.resolveService(nsdServiceInfo, mResolveListener);
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                if(!nsdServiceInfo.getServiceName().equals(serviceName)) {
                    Toast.makeText(context, nsdServiceInfo.getServiceName()+ " Service Lost", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    NsdServiceInfo getNearestServ(NsdServiceInfo serv1, NsdServiceInfo serv2){
        int a = serv1.getServiceName().length();
        int b = serv2.getServiceName().length();
        return (a<b ? serv1 : serv2);
    }

    void computeShortestService(NsdServiceInfo nsdServiceInfo){
        //Computing Shortest Service
        serviceCounter++;
        if(nsdServiceInfo.getServiceName().equals(Constants.NSD_BASE_NAME)){
            callback.onServiceDiscovered(nsdServiceInfo);
        }else{
            nearestServ = (nearestServ == null) ? nsdServiceInfo : getNearestServ(nearestServ, nsdServiceInfo);
            if(serviceCounter >= Constants.MAX_CONNECTS){
                callback.onServiceDiscovered(nearestServ);
            }
        }
        //-- Computing Shortest Service
    }

    void initRegistrationListener(){

        mRegistrationListener = new NsdManager.RegistrationListener(){

            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, nsdServiceInfo.toString());
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Toast.makeText(context, "Unregistration Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(context, "Service Registered "+nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(context, "Service Unregistered", Toast.LENGTH_SHORT).show();
            }
        };

    }

    void initResolveListener(){
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + nsdServiceInfo);
                computeShortestService(nsdServiceInfo); //Find Nearest Service
            }
        };
    }

    public interface Callback{
        void onServiceDiscovered(NsdServiceInfo service);
    }

}
