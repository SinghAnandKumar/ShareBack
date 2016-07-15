package com.fantasticfive.shareback.activity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.Events;
import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.alertDialogs.SessionCloseAlert;
import com.fantasticfive.shareback.db.QueriesComments;
import com.fantasticfive.shareback.db.QueriesSessions;
import com.fantasticfive.shareback.utils.EventSender;
import com.fantasticfive.shareback.utils.FeedbackReciever;
import com.fantasticfive.shareback.utils.JsonEvents;
import com.fantasticfive.shareback.utils.DocSender;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class FileViewerActivityServer
        extends AppCompatActivity
        implements DocSender.Callback, OnPageChangeListener, EventSender.Callback, SessionCloseAlert.Callback, FeedbackReciever.Callback {

    PDFView.Configurator con;
    DocSender docSender = null;
    PDFView pdfView;
    EventSender eventSender = null;
    SessionCloseAlert alert = null;
    int clientCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    //Main Code

        //Get Data from previous Activity
        Bundle basket = getIntent().getExtras();
        String path = basket.getString("path");
        //-- Get Data from previous Activity

        //Open Pdf
        pdfView = (PDFView) findViewById(R.id.pdfviewLib);
        File myFile = new File(path);
        con = pdfView.fromFile(myFile)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true);
        con.onPageChange(new OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                Log.e("MyTag", "Page Changed");
                eventSender = new EventSender();
                eventSender.execute(
                        new JsonEvents(Events.PAGE_CHANGED, page).getJsonString()
                );
            }
        });
        con.load();
        //-- Open Pdf

        //Initialize Server Sockets
        docSender = new DocSender(FileViewerActivityServer.this, FileViewerActivityServer.this);
        docSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, myFile);
        //-- Initialize Server Sockets

    //-- Main Code

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Show Local IP
        TextView tvLocalIp = (TextView) findViewById(R.id.tvLocalIp);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        String localIp = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        tvLocalIp.setText(localIp);
        //--  Show Local IP

    }




    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onDocSent(boolean result, int portNo) {
        if(result){
            Toast.makeText(FileViewerActivityServer.this, "Waiting For Event....", Toast.LENGTH_SHORT).show();
            Snackbar.make(pdfView, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onEventSent() {
        try {
            String result = eventSender.get().toString();
            Log.e("Event Received", result);
            //Check errors in event Sending
            //-- Check errors in event Sending


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("MyTag", "Back Pressed");
        alert = new SessionCloseAlert();
        alert.show(getSupportFragmentManager(), "Dismiss");
        //super.onBackPressed();
    }

    @Override
    public void onPositiveClick() {

        //Send SESSION_CLOSE event
        eventSender = new EventSender();
        eventSender.execute(
                new JsonEvents(Events.CLOSE_SESSION).getJsonString()
        );

        if(Globals.clientIps.size() < 1){
            closeSession();
        }
        else {
            FeedbackReciever reciever = new FeedbackReciever(this);
            reciever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        //-- Send SESSION_CLOSE event
    }

    @Override
    public void onNegativeClick() {
        alert.dismiss();
    }

    int stars[] = {0,0,0,0,0};
    @Override
    public void onFeedbackRecieve(String rating, String comment) {

        Toast.makeText(FileViewerActivityServer.this, "Rating: "+rating+" Comment:"+comment, Toast.LENGTH_SHORT).show();
        if(!comment.equals(""))
            new QueriesComments(this).add(comment);

        int rate = (int)Float.parseFloat(rating);
        switch(rate){
            case 1: stars[0]++; break;
            case 2: stars[1]++; break;
            case 3: stars[2]++; break;
            case 4: stars[3]++; break;
            case 5: stars[4]++; break;
        }

        if(Globals.clientIps.size() == ++clientCount) {
            new QueriesSessions(this).updateStars(stars);
            closeSession();
        }
        else{
            new FeedbackReciever(this).execute();
        }
    }

    public void closeSession(){
        docSender.close();
        super.onBackPressed();
    }
}
