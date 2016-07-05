package com.fantasticfive.shareback.utils;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.fantasticfive.shareback.Globals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Sagar on 08-03-2016.
 */
public class HotspotManager {

    WifiManager wifi;

    public boolean start(WifiManager wifi){

        //wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        this.wifi = wifi;
        Method[] wmMethods  = wifi.getClass().getDeclaredMethods();

        //Start Backup old Configuration
        Globals.oldWifiCon = new WifiConfiguration();
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        Globals.oldWifiCon.SSID = wifiInfo.getSSID();
        //-- Stop Backup old Configuration

        Log.e("WifiAP", "Trying to Start");
        for (Method method : wmMethods){
            if (method.getName().equals("setWifiApEnabled")){
                try {
                    method.invoke(wifi, Globals.oldWifiCon, true);
                    Log.e("WifiAP", "Hotspot Started");
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e){
                    e.printStackTrace();
                    Log.e("WifiAP", "Unable to Start: "+e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    public boolean stop(){
        //Start Restore Wifi State
        Method[] wmMethods  = wifi.getClass().getDeclaredMethods();

        for (Method method : wmMethods){
            if (method.getName().equals("setWifiApEnabled")){
                try {
                    method.invoke(wifi, Globals.oldWifiCon, false);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e){
                    e.printStackTrace();
                    return false;
                }
            }
        }
        //-- Stop Restore Wifi State
        return true;
    }


}
