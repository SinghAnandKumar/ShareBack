package com.fantasticfive.shareback;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.connection.ConnectionHelper;

public class NsdActivity extends AppCompatActivity {

    Button btnStart;
    Button btnDiscover;
    com.fantasticfive.shareback.newshareback.ShareBucket shareBucket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);

        init();
        shareBucket = new ShareBucket();
        shareBucket.testCode();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionHelper connectionHelper = new ConnectionHelper(NsdActivity.this, shareBucket);
                (new Thread(connectionHelper)).start();
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionHelper connectionHelper = new ConnectionHelper(NsdActivity.this, shareBucket);
                connectionHelper.startDiscovery();
            }
        });
    }

    void init(){
        btnStart = (Button) findViewById(R.id.btnStart);
        btnDiscover = (Button) findViewById(R.id.btnDiscover);
    }
}
