package com.fantasticfive.shareback.newshareback.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.helpers.EventHelper;
import com.fantasticfive.shareback.newshareback.helpers.InitConnectionHelper;
import com.fantasticfive.shareback.newshareback.dialogs.FeedbackDialog;
import com.fantasticfive.shareback.newshareback.helpers.PdfViewHelper;
import com.fantasticfive.shareback.newshareback.helpers.DirFileViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class FileViewStudent extends AppCompatActivity
        implements PdfViewHelper.PdfHelperCallback
        ,EventHelper.EventReceiveCallback
        ,InitConnectionHelper.InitConnectionHelperCallback
        ,DirFileViewHelper.FileDwnldCallback
        ,FeedbackDialog.FeedbackCallback{

    LinearLayout container = null;

    PdfViewHelper pdfViewHelper;
    EventHelper eventHelper;
    ShareBucket bucket;
    InitConnectionHelper initConnectionHelper;
    DirFileViewHelper dirHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view_student);

        String newNsdName = getIntent().getStringExtra(Constants.KEY_NEW_NSD_NAME);
        String strShareBucket = getIntent().getStringExtra(Constants.KEY_SHAREBUCKET);

        //Create ShareBucketBucket
        try {
            bucket = new ShareBucket();
            bucket.createFromJSON(new JSONObject(strShareBucket));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //-- Create ShareBucket

        init();

        //Search service, connect internally, start own service
        //initConnectionHelper.startDiscovery();

        //Download Files
        Collection<String> files = bucket.getFiles();
        Iterator<String> itr = files.iterator();
        while(itr.hasNext()){
            String file = itr.next();
            dirHelper.downloadFile(file, true);
        }
        //-- Download Files

        //Listen For Events
        eventHelper.listenForEvents();

        //Start Services(Act as server)
        initConnectionHelper.startServices_S(newNsdName);
    }

    public void init(){
        container = (LinearLayout) findViewById(R.id.fullscreen_content);

        pdfViewHelper = new PdfViewHelper(this, bucket, this);
        initConnectionHelper = new InitConnectionHelper(this,this, bucket);
        eventHelper = new EventHelper(this, this, initConnectionHelper);
        dirHelper = new DirFileViewHelper(this, this);
    }

    @Override
    public void onPageChanged(String filePath, int pageNo) {
        Toast.makeText(this, "Page Changed: "+pageNo, Toast.LENGTH_SHORT).show();
        //Send Event to Clients
        eventHelper.sendEvent(Constants.EVENT_PAGE_CHANGED, filePath, pageNo, initConnectionHelper.getClientList());
        //-- Send Event to Clients
    }

    @Override
    public void onFileChanged(String filePath, int pageNo) {
        Toast.makeText(this, "File Changed", Toast.LENGTH_SHORT).show();
        eventHelper.sendEvent(Constants.EVENT_FILE_CHANGED, filePath, pageNo, initConnectionHelper.getClientList());
    }


    @Override
    public void onPageChangedS(String fileName, int pageNo) {
        pdfViewHelper.onPageChangedS(fileName, pageNo);
    }

    @Override
    public void onFileChangedS(String fileName, int pageNo) {
        pdfViewHelper.onFileChangedS(fileName, pageNo);
    }

    @Override
    public void onFilesAddedS(LinkedHashSet<String> arrFiles) {

        //Add Files to Bucket
        pdfViewHelper.onFilesAddedS(arrFiles);
        //-- Add Files to Bucket

        //Download File
        for(String file:  arrFiles) {
            dirHelper.downloadFile(file, true);
        }
        //-- Download File
    }

    @Override
    public void onSessionClosedS() {

        showFeedbackAlert(""); //Pop Up for Feedback

        pdfViewHelper.onSessionClosedS();
    }

    @Override
    public void onServerFound(InetAddress serverAddress, JSONObject main) {
        try {
            JSONArray arr = main.getJSONArray(Constants.JSON_FILES);
            //Download File
            for(int i=0; i<arr.length(); i++) {
                dirHelper.downloadFile(arr.getString(i), true);
            }
            //-- Download File
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventHelper.listenForEvents();
    }

    @Override
    public void onFileDownloaded(String relLocation) {
        pdfViewHelper.setDownloadFlag(relLocation);
    }

    @Override
    public void onFileAdded(String fileName, boolean isSessionFile) {

    }

    private void showFeedbackAlert(String comment){
        //Show Feedback Alert
        FeedbackDialog fbDialog = new FeedbackDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_SESSION_ID, bucket.getSessionId());
        fbDialog.setArguments(bundle);
        fbDialog.setComment(comment);
        fbDialog.show(getSupportFragmentManager(), "Feedback");
        //-- Show Feedback Alert
    }

    @Override
    public void onFeedbackDone() {

        Toast.makeText(FileViewStudent.this, "Closing Session", Toast.LENGTH_SHORT).show();
        //Closing Session
        eventHelper.packUp();
        initConnectionHelper.packUp();
        finish();
        //-- Closing Session
    }
}
