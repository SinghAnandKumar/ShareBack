package com.fantasticfive.shareback.newshareback.activities;

import android.content.Intent;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.helpers.InitConnectionHelper2;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by sagar on 5/8/16.
 */
public class NsdDiscoverActivity
        extends AppCompatActivity
        implements InitConnectionHelper2.Callback{

    HashMap<String, NsdServiceInfo> hmServices;
    HashMap<String, Boolean> hmReady;
    ImageView centerImage;
    RippleBackground rippleBackground;
    InitConnectionHelper2 connHelper;

    int sessionCount = 0;
    String parentNsdName = "NOT_INITIALIZED";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd_discover);
        init();

        rippleBackground.startRippleAnimation();
        connHelper.startDiscovery();

       // testCode();
    }

    private void init(){
        centerImage = (ImageView) findViewById(R.id.centerImage);
        rippleBackground = (RippleBackground) findViewById(R.id.ripple_background);

        connHelper = new InitConnectionHelper2(this, this);
        hmServices = new HashMap<>();
        hmReady = new HashMap<>();
    }

    private void addSession(String sessionName){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.inner_nsd_discover, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        switch (sessionCount++%4){
            case 0: params.addRule(RelativeLayout.ABOVE, centerImage.getId()); break;
            case 1: params.addRule(RelativeLayout.BELOW, centerImage.getId()); break;
            case 2:
                params.addRule(RelativeLayout.LEFT_OF, centerImage.getId());
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                break;
            case 3: params.addRule(RelativeLayout.RIGHT_OF, centerImage.getId()); break;
            default: break;
        }
        view.setLayoutParams(params);

        ImageView imgView = (ImageView) view.findViewById(R.id.circular_dp);
        final TextView tvSessionName = (TextView) view.findViewById(R.id.session_name);
        tvSessionName.setText(sessionName);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NsdDiscoverActivity.this, tvSessionName.getText(), Toast.LENGTH_SHORT).show();
                onSessionSelected(tvSessionName.getText().toString());
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rippleBackground.addView(view);
            }
        });
    }

    @Override
    public void onServiceDiscovered(NsdServiceInfo service) {

        //Find Most Suitable Service
        String sessionName = service.getServiceName().split(Constants.NSD_SEPERATOR)[1]; //Sample service name: EShareBack.Data Structures.1.2
        if(hmServices.containsKey(sessionName)){
            String oldName = hmServices.get(sessionName).getServiceName();
            String newName = service.getServiceName();
            if(newName.length() < oldName.length()){ //if newLength is smaller [like EShareBack.Data Structures.1.2 & EShareBack.Data Structures.1.2.2]
                hmServices.put(sessionName, service);
                connHelper.resolveService(service);
            }
            else if(newName.length() == newName.length()){
                if(newName.charAt(newName.length() -1 ) < oldName.charAt(oldName.length() - 1)){ //if last char is smaller
                    hmServices.put(sessionName, service);
                    connHelper.resolveService(service);
                }
            }
        }
        else{
            hmServices.put(sessionName, service);
            hmReady.put(sessionName, false);
            addSession(sessionName);
            connHelper.resolveService(service);
        }
        //-- Find Most Suitable Service


    }

    @Override
    public void onServiceResolved(NsdServiceInfo service) {
        String temp[] = service.getServiceName().split(Constants.NSD_SEPERATOR);
        String sessionName = temp[1]; //Sample service name: EShareBack.Data Structures.1.2

        hmServices.put(sessionName, service);
        hmReady.put(sessionName, true);
    }

    private void onSessionSelected(String sessionName){

        /***
        SHow progress bar
         */

        if(hmReady.get(sessionName)) {
            NsdServiceInfo service = hmServices.get(sessionName);
            parentNsdName = service.getServiceName();
            connHelper.receiveMetaData(service.getHost().getHostAddress());
        }else {
            Toast.makeText(NsdDiscoverActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBucketReceived(JSONObject jsonShareBucket) throws JSONException{

        //Create New NSD NAME
        int token = jsonShareBucket.getInt(Constants.JSON_TOKEN_NO);
        String newSessionName = parentNsdName + "." + token;

        //Start Student Activity
        Intent intent = new Intent(this, FileViewStudent.class);
        intent.putExtra(Constants.KEY_SHAREBUCKET, jsonShareBucket.toString());
        intent.putExtra(Constants.KEY_NEW_NSD_NAME, newSessionName);
        startActivity(intent);
        finish();

    }

    private void testCode(){
        for(int i=1; i<5; i++) {
            final int c = i;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (c){
                        case 1: addSession("Data Structures And Programming Development");
                            NsdServiceInfo info = new NsdServiceInfo();
                            info.setServiceName(Constants.NSD_BASE_NAME+"-Data Structures And Programming Development");
                        try {
                            InetAddress address = InetAddress.getByName("192.168.137.175");
                            Toast.makeText(NsdDiscoverActivity.this, address.getHostAddress()+"", Toast.LENGTH_SHORT).show();
                            info.setHost(address);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                            hmServices.put("Data Structures And Programming Development", info);
                            break;
                        case 2: addSession("Object Oriented Programming"); break;
                        case 3: addSession("Databases And Management Systems"); break;
                        case 4: addSession("Advanced Object Oriented Technology"); break;
                    }
                }
            }, 1000*i);
        }

    }

}
