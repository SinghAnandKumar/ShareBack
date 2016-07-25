package com.fantasticfive.shareback.newshareback.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.adapters.RecyclerViewAdapter;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.dialogs.DialogIp;

public class NewMainActivity extends AppCompatActivity {

    AppCompatButton btnCreateSession, btnJoinSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        init();
        setPreferredIp();


        btnCreateSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewMainActivity.this, FileViewClient.class);
                startActivity(intent);
            }
        });


        ListView recyclerView = (ListView) findViewById(R.id.cachedFiles);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for(int i=0; i<20; i++){
            adapter.add("Item "+i);
        }
        recyclerView.setAdapter(adapter);
    }

    public void init(){
        btnCreateSession = (AppCompatButton) findViewById(R.id.btnCreateSession);
        btnJoinSession = (AppCompatButton) findViewById(R.id.btnJoinSession);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_ip: setServerIP();  return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    private void setPreferredIp(){
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE );
        String str = prefs.getString(Constants.PREF_SERVER_IP, null);
        Constants.IP_FILE_SERVER = (str == null) ? "" : str;
    }

    private void setServerIP(){
        DialogIp dialog = new DialogIp();
        dialog.show(getSupportFragmentManager(), "Server Ip Dialog");
    }
}
