package com.fantasticfive.shareback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fantasticfive.shareback.newshareback.connection.ConnectionHelper;
import com.fantasticfive.shareback.newshareback.connection.NsdHelper;

public class NsdActivity extends AppCompatActivity {

    Button btnStart;
    Button btnDiscover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);

        init();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionHelper connectionHelper = new ConnectionHelper(NsdActivity.this);
                connectionHelper.openSocket();
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionHelper connectionHelper = new ConnectionHelper(NsdActivity.this);
                connectionHelper.startDiscovery();
            }
        });
    }

    void init(){
        btnStart = (Button) findViewById(R.id.btnStart);
        btnDiscover = (Button) findViewById(R.id.btnDiscover);
    }
}
