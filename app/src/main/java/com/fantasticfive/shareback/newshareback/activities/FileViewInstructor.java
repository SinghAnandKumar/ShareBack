package com.fantasticfive.shareback.newshareback.activities;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.connection.EventHelper;
import com.fantasticfive.shareback.newshareback.connection.InitConnectionHelper;
import com.fantasticfive.shareback.newshareback.helpers.PdfViewHelper;
import com.fantasticfive.shareback.newshareback.utils.DirHelper;

import java.util.LinkedHashSet;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FileViewInstructor extends AppCompatActivity
        implements DirHelper.FileDwnldCallback
        ,PdfViewHelper.PdfHelperCallback
        ,DirExplorerActivity.DirExplorerActivityCallback {

    LinearLayout scrollView = null;
    ImageButton addFileButton = null;
    LinearLayout container = null;

    PdfViewHelper pdfViewHelper;
    EventHelper eventHelper;
    ShareBucket bucket;
    InitConnectionHelper initConnectionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_view_instructor);

        init();

        //Opening Socket and Services
        initConnectionHelper.openSocket();
        //-- Opening Socket and Services

        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new DirExplorerActivity(FileViewInstructor.this);
                dialog.setTitle("Server files");
                dialog.show();
            }
        });

    }

    private void init(){
        scrollView = (LinearLayout) findViewById(R.id.horizontalScrollview);
        addFileButton = (ImageButton) findViewById(R.id.add_file);
        container = (LinearLayout) findViewById(R.id.fullscreen_content);

        bucket = new ShareBucket();
        pdfViewHelper = new PdfViewHelper(this, bucket, this);
        initConnectionHelper = new InitConnectionHelper(this, bucket);
        eventHelper = new EventHelper(this);
    }


    //Callbacks
    @Override
    public void onFileDownloaded(String relLocation) {
        Log.e("My Tag", "File Downloaded "+relLocation );
        Toast.makeText(FileViewInstructor.this, "File Downloaded", Toast.LENGTH_SHORT).show();

        pdfViewHelper.setDownloadFlag(relLocation);
    }

    @Override
    public void onFileAdded(String fileName, boolean isSessionFile) {
        if(isSessionFile) {
            pdfViewHelper.addFile(fileName);
        }
        else {
            pdfViewHelper.removeFile(fileName);
        }
    }

    @Override
    public void onPageChanged(String filePath, int pageNo) {

        Toast.makeText(FileViewInstructor.this, "Page Changed: "+pageNo, Toast.LENGTH_SHORT).show();
        //Send Event PAGE_CHANGED
        eventHelper.sendEvent(Constants.EVENT_PAGE_CHANGED, filePath, pageNo, initConnectionHelper.getClientList());
        //-- Send Event PAGE_CHANGED

    }

    @Override
    public void onFileChanged(String filePath, int pageNo) {
        Toast.makeText(FileViewInstructor.this, "File Changed", Toast.LENGTH_SHORT).show();
        //Send Event FILE_CHANGED
        eventHelper.sendEvent(Constants.EVENT_FILE_CHANGED, filePath, pageNo, initConnectionHelper.getClientList());
        //-- Send Event FILE_CHANGED
    }

    @Override
    public void onFileFinallySelected(LinkedHashSet<String> files) {
        Toast.makeText(FileViewInstructor.this, "File Changed", Toast.LENGTH_SHORT).show();
        //Send Event FILES_ADDED
        eventHelper.sendFiles( files, initConnectionHelper.getClientList());
        //-- Send Event FILES_ADDED
    }
    //-- Callbacks
}
