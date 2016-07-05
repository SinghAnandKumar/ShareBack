package com.fantasticfive.shareback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.fantasticfive.shareback.adapters.RecyclerViewAdapter;

public class NewMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        ListView recyclerView = (ListView) findViewById(R.id.cachedFiles);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for(int i=0; i<20; i++){
            adapter.add("Item "+i);
        }
        recyclerView.setAdapter(adapter);
    }
}
