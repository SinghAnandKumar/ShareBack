package com.fantasticfive.shareback.newshareback.activities;

import android.content.Intent;
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
import com.fantasticfive.shareback.newshareback.helpers.NsdHelper;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by sagar on 5/8/16.
 */
public class NsdDiscoverActivity
        extends AppCompatActivity implements NsdHelper.Callback{

    HashMap<String, NsdServiceInfo> hmServices;
    ImageView centerImage;
    RippleBackground rippleBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nsd_discover);

        hmServices = new HashMap<>();

        centerImage = (ImageView) findViewById(R.id.centerImage);
        rippleBackground = (RippleBackground) findViewById(R.id.ripple_background);
        rippleBackground.startRippleAnimation();

        testCode();
    }

    int sessionCount = 0;
    private void addSession(String sessionName){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.inner_nsd_discover, null);
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
            }
        });

        rippleBackground.addView(view);
    }

    @Override
    public void onServiceDiscovered(NsdServiceInfo service) {

        //Find Most Suitable Service
        String sessionName = service.getServiceName().split("-")[1]; //Sample service name: EShareBack.Data Structures.1.2
        if(hmServices.containsKey(sessionName)){
            String oldName = hmServices.get(sessionName).getServiceName();
            String newName = service.getServiceName();
            if(newName.length() < oldName.length()){ //if newLength is smaller
                hmServices.put(sessionName, service);
            }
            else if(newName.length() == newName.length()){
                if(newName.charAt(newName.length() -1 ) < oldName.charAt(oldName.length() - 1)){ //if last char is smaller
                    hmServices.put(sessionName, service);
                }
            }
        }
        else{
            hmServices.put(sessionName, service);
            addSession(sessionName);
        }
        //-- Find Most Suitable Service


    }

    private void testCode(){
        for(int i=1; i<5; i++) {
            final int c = i;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (c){
                        case 1: addSession("Data Structures And Programming Development"); break;
                        case 2: addSession("Object Oriented Programming"); break;
                        case 3: addSession("Databases And Management Systems"); break;
                        case 4: addSession("Advanced Object Oriented Technology"); break;
                    }
                }
            }, 1000*i);
        }

    }
}
