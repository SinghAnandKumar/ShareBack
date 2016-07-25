package com.fantasticfive.shareback.newshareback.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.connection.EventHelper;
import com.fantasticfive.shareback.newshareback.connection.InitConnectionHelper;

import java.util.LinkedHashSet;

public class NsdActivity extends AppCompatActivity
                        implements EventHelper.EventReceiveCallback{

    Button btnStart;
    Button btnDiscover;
    Button btnSendEvent;
    com.fantasticfive.shareback.newshareback.ShareBucket shareBucket;

    int pageNo = 0;
    String currFile;

    InitConnectionHelper connectionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);

        init();

        shareBucket = new ShareBucket();

        connectionHelper = new InitConnectionHelper(NsdActivity.this, shareBucket);
        //shareBucket.testCode();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new Thread(connectionHelper)).start();
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectionHelper = new InitConnectionHelper(NsdActivity.this, shareBucket);
                connectionHelper.startDiscovery();
            }
        });

        btnSendEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventHelper eventHelper = new EventHelper(NsdActivity.this);


                //testCode();


                eventHelper.sendEvent(Constants.EVENT_PAGE_CHANGED, currFile, pageNo, connectionHelper.getClientList() );
            }
        });
    }

    void init(){
        btnStart = (Button) findViewById(R.id.btnStart);
        btnDiscover = (Button) findViewById(R.id.btnDiscover);
        btnSendEvent = (Button) findViewById(R.id.btnSendEvent);
    }

    /*private void testCode(){
        currFile = "/test/Abc.pdf";
        pageNo++;
        //connectionHelper.

        //connectionHelper.getClientList().clear();
        try {
            connectionHelper.getClientList().add(InetAddress.getByName("192.168.43.119"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onPageChangedS(String fileName, int pageNo) {
        shareBucket.updatePageNo(fileName, pageNo);
    }

    @Override
    public void onFileChangedS(String fileName, int pageNo) {
        shareBucket.setCurrentFile(fileName, pageNo);
    }

    @Override
    public void onFilesAddedS(LinkedHashSet<String> fileName) {
        //shareBucket.pushFile(fileName);
    }

    @Override
    public void onSessionClosedS() {
        //Close Session and Take Feedback
        //-- Close Session and Take Feedback
    }
}
