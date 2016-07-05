package com.fantasticfive.shareback.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.adapters.RecentDocAdapter;
import com.fantasticfive.shareback.alertDialogs.ServerIpAlert;
import com.fantasticfive.shareback.db.QueriesRecentDocs;
import com.fantasticfive.shareback.db.QueriesSessions;
import com.fantasticfive.shareback.utils.ShareBucketBuilder;
import com.fantasticfive.shareback.utils.Sleeper;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class StartActivity
        extends AppCompatActivity
        implements ServerIpAlert.Callback, RecentDocAdapter.Callback, Sleeper.Callback {

    private final int FILE_SELECT_CODE = 1;
    private RecentDocAdapter adapter = null;
    private AppBarLayout layout = null;
    private FloatingActionButton fabJoinSession = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        layout = (AppBarLayout) findViewById(R.id.app_bar);


        fabJoinSession = (FloatingActionButton) findViewById(R.id.fabJoin);

        animatorWork();
        //-- Main Content
    }

    private void initWork(){

    }

    private void animatorWork(){

        getSupportActionBar().hide();

        final CollapsingToolbarLayout collapse = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        layout.setExpanded(false);

        new Sleeper(this).execute();
    }

    private void containerWork(){

        getSupportActionBar().show();

        final CollapsingToolbarLayout collapse = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        final TextView tv = (TextView) findViewById(R.id.tvHeader);
        Typeface tf = Typeface.createFromAsset(getAssets(), Globals.font_bold);
        tv.setTypeface(tf);
        ((TextView)findViewById(R.id.textRecentFiles)).setTypeface(tf);

        layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(collapse.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapse)) {
                    tv.animate().alpha(1).setDuration(400);
                } else {
                    tv.animate().alpha(0).setDuration(400);
                }
            }
        });

        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Main Contents
        FloatingActionButton fabStartSession = (FloatingActionButton) findViewById(R.id.fabStart);
        fabStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show FileChooser
                Intent intent = new Intent(StartActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                intent.setType("mime/pdf");

                intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                startActivityForResult(intent, FILE_SELECT_CODE);
                //-- Show FileChooser
            }
        });


        fabJoinSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerIpAlert dialog = new ServerIpAlert();
                dialog.show(StartActivity.this.getSupportFragmentManager(), "Server Ip Dialog");
                long l = new Date().getTime();
            }
        });

        //Set List Content
        LinearLayout llMain = (LinearLayout) findViewById(R.id.ll_recent_doc);
        adapter = new RecentDocAdapter(this, llMain, this);
        adapter.addViews();
        //-- Set List Content
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filePath = uri.getPath();

                    startSession( filePath );
                }
                break;
            default: Toast.makeText(this, "Cannot Start Session: Unsupported File Type", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startSession(String filePath){

        try {
            ShareBucketBuilder.build(new File(filePath));
            Toast.makeText(StartActivity.this, "Share Bucket Ready", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* *//*-----RowId is SessionId-----*//*
        Globals.sessionId = new QueriesRecentDocs(this).add(new File(filePath));*/

        //Insert file in Database
        Globals.sessionId = new QueriesSessions(this).add(filePath);
        //-- Insert file in Database
        Toast.makeText(StartActivity.this, "Session Id: "+Globals.sessionId, Toast.LENGTH_SHORT).show();

        adapter.notifyChange();

        Intent intent = new Intent(this, FileViewerActivityServer.class);
        Bundle basket = new Bundle();
        basket.putString("path", filePath);
        intent.putExtras(basket);
        startActivity(intent);
    }

    public void joinSession(String serverIp){
        /*Search for SSIDs
                 *  If any SSID is found with the name containing "Share Back Instructor <session_name>"
                 *      Connect with SSID
                 *      Start FileViewer Activity
                 */
        Intent intent = new Intent(StartActivity.this, FileViewerActivityClient.class);
        intent.putExtra("ip",serverIp);
        startActivity(intent);
    }


    @Override
    public void onJoin( String serverIp) {
        joinSession(serverIp);
    }

    @Override
    public void onCardClicked(String filePath) {
        startSession(filePath);
    }

    @Override
    public void onWakeup() {
        //Expand AppLayout
        layout.setExpanded(true, true);
        //-- Expand AppLayout

        //Animate Splash Image
        findViewById(R.id.splash_img).animate().alpha(0).setDuration(300);
        //-- Animate Splash Image

        //Animate RecentDoc List
        LinearLayout ll= (LinearLayout) findViewById(R.id.ll_main);
        ll.setVisibility(View.VISIBLE);
        ll.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).setDuration(1200);
        ll.animate().alpha(1).setDuration(2000);
        //-- Animate RecentDoc List

        containerWork();

        fabJoinSession.animate().scaleX(1).scaleY(1).alpha(1).setDuration(400);

    }
}
