package com.fantasticfive.shareback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fantasticfive.shareback.newshareback.connection.NsdHelper;

public class NsdActivity extends AppCompatActivity {

    Button btnStart;
    Button btnDiscover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);

        init();

        final NsdHelper nsdHelper = new NsdHelper(this, "EShareback");
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nsdHelper.registerService(1024);
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nsdHelper.discoverServices();
            }
        });
    }

    void init(){
        btnStart = (Button) findViewById(R.id.btnStart);
        btnDiscover = (Button) findViewById(R.id.btnDiscover);
    }
}
