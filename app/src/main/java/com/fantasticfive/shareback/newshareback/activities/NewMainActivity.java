package com.fantasticfive.shareback.newshareback.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.errhandlers.ServerChecker;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.dialogs.DialogIp;
import com.fantasticfive.shareback.newshareback.dialogs.SessionInfoDialog;

public class NewMainActivity
        extends AppCompatActivity
        implements ServerChecker.ServerCheckerCallback {

    AppCompatButton btnCreateSession, btnJoinSession;
    AppBarLayout layout;
    Snackbar snackbar;
    int NSD_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        init();
        actionBarAnimate();
        setPreferredIp();


        btnCreateSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionInfoDialog dialog = new SessionInfoDialog();
                dialog.show(getSupportFragmentManager(), "Session Info");
            }
        });

        btnJoinSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewMainActivity.this, NsdDiscoverActivity.class);
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
        layout = (AppBarLayout) findViewById(R.id.app_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        snackbar = Snackbar.make(findViewById(R.id.top_parent), "Can't Connect to Server", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
    }

    private void actionBarAnimate(){

            getSupportActionBar().show();

            final CollapsingToolbarLayout collapse = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            final TextView tv = (TextView) findViewById(R.id.tvHeader);
            Typeface tf = Typeface.createFromAsset(getAssets(), Globals.font_bold);
            tv.setTypeface(tf);
    }

    private void startManageFilesActivity(){
        Intent intent = new Intent(NewMainActivity.this, ManageFilesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        ServerChecker errChecker = new ServerChecker(this, this);
        errChecker.execute();
        super.onStart();
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
            case R.id.action_manage_files: startManageFilesActivity(); return true;

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

    @Override
    public void onServerCheckerResponse(boolean isOnline) {
        if(!isOnline && !snackbar.isShownOrQueued())
            snackbar.show();
        else if(isOnline)
            snackbar.dismiss();
    }
}