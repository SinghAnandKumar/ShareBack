package com.fantasticfive.shareback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;

public class NewMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cachedFiles);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for(int i=0; i<100; i++){
            adapter.add("Item "+i);
        }
        //recyclerView.setAdapter(adapter);
    }
}
