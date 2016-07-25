package com.fantasticfive.shareback.newshareback.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.connection.EventHelper;
import com.fantasticfive.shareback.newshareback.connection.InitConnectionHelper;
import com.fantasticfive.shareback.newshareback.helpers.PdfViewHelper;

import java.util.LinkedHashSet;

public class FileViewStudent extends AppCompatActivity
        implements PdfViewHelper.PdfHelperCallback, EventHelper.EventReceiveCallback {

    LinearLayout container = null;

    PdfViewHelper pdfViewHelper;
    EventHelper eventHelper;
    ShareBucket bucket;
    InitConnectionHelper initConnectionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view_student);

        init();

        //Search service, connect internally, start own service
        initConnectionHelper.startDiscovery();
        //-- Search service, connect internally, start own service
    }

    public void init(){
        container = (LinearLayout) findViewById(R.id.fullscreen_content);

        bucket = new ShareBucket();
        pdfViewHelper = new PdfViewHelper(this, bucket, this);
        initConnectionHelper = new InitConnectionHelper(this, bucket);
        eventHelper = new EventHelper(this, this, initConnectionHelper);
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
        pdfViewHelper.onFilesAddedS(arrFiles);
    }

    @Override
    public void onSessionClosedS() {
        pdfViewHelper.onSessionClosedS();
    }
}
