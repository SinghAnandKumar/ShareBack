package com.fantasticfive.shareback.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fantasticfive.shareback.Events;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.alertDialogs.FeedbackAlert;
import com.fantasticfive.shareback.alertDialogs.SessionCloseAlert;
import com.fantasticfive.shareback.utils.DocReceiver;
import com.fantasticfive.shareback.utils.EventReciever;
import com.fantasticfive.shareback.utils.FeedbackSender;
import com.fantasticfive.shareback.utils.JsonEvents;
import com.fantasticfive.shareback.utils.JsonFeedback;
import com.fantasticfive.shareback.utils.ShareBucketBuilder;
import com.joanzapata.pdfview.PDFView;

import org.json.JSONException;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class FileViewerActivityClient
        extends AppCompatActivity
        implements DocReceiver.Callback, EventReciever.Callback, SessionCloseAlert.Callback, FeedbackAlert.Callback, FeedbackSender.Callback{

    PDFView.Configurator con;
    EventReciever eventRecv=null;
    PDFView pdfView = null;
    SessionCloseAlert alert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer_client);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Main Content

        //-- Main Content
        String ip = getIntent().getStringExtra("ip");
        DocReceiver builder = new DocReceiver(ip, this);
        builder.execute();
    }


    @Override
    public void onDocReceive() {
        File f;

            //Prepare Pdf
            f = ShareBucketBuilder.getRecievedFile();
            //-- Prepare Pdf

            //Open Pdf
            pdfView = (PDFView) findViewById(R.id.pdfviewLib);
            con = pdfView.fromFile(f)
                    .defaultPage(1)
                    .showMinimap(false)
                    .enableSwipe(false);    //Disabled Swipe
            con.load();
            //-- Open Pdf

            //Receive Event
                Log.e("MyTag", "Starting Event Listener");
                eventRecv = new EventReciever(this);
                eventRecv.execute();
            //-- Receive Event
    }

    @Override
    public void onEventReceive() {
        try {

            String result = eventRecv.get().toString();
            Log.e("Event Received", result);

            //Perform Event Task
            JsonEvents util = new JsonEvents(result);
            int event = util.getEvent();
            int val = util.getValue();

            switch (event){
                case Events.PAGE_CHANGED: pdfView.jumpTo(val); break;
                case Events.CLOSE_SESSION: showFeedbackAlert(""); break;

            }

            //-- Perform Event Task

            //Again Receive Event
            Log.e("MyTag", "Starting Event Listener Again");
            eventRecv = new EventReciever(this);
            eventRecv.execute();
            Log.e("MyTag", "Starting Event Listener Again");
            //-- Again Receive Event

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("MyTag", "Back Pressed");
        /*alert = new SessionCloseAlert();
        alert.show(getSupportFragmentManager(), "Dismiss");*/
        //super.onBackPressed();

        Snackbar.make(findViewById(R.id.fab), "Close CreatedSession?", Snackbar.LENGTH_LONG)
                .setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileViewerActivityClient.this.onPositiveClick();
                    }
                }).show();
    }

    @Override
    public void onPositiveClick() {
        showFeedbackAlert("");
    }

    public void showFeedbackAlert(String comment){
        //Show Feedback Alert
        FeedbackAlert fbAlert = new FeedbackAlert();
        fbAlert.setComment(comment);
        fbAlert.show(getSupportFragmentManager(), "Feedback");
        //-- Show Feedback Alert
    }

    @Override
    public void onNegativeClick() {
        alert.dismiss();
    }

    @Override
    public void onFeedbackDone(String rating, String comment) {
        if(Float.parseFloat(rating) < 1){
            showFeedbackAlert(comment);
            Toast.makeText(FileViewerActivityClient.this, "Give rating first", Toast.LENGTH_SHORT).show();
        }
        else {
            //Send Feedback
            FeedbackSender feedSender = new FeedbackSender(this);
            feedSender.executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    new JsonFeedback(rating, comment).getJsonString());
            //-- Send Feedback
        }
    }

    @Override
    public void onFeedbackSent() {
        //Close CreatedSession
        eventRecv.close();
        super.onBackPressed();
        //-- Close CreatedSession
    }
}
