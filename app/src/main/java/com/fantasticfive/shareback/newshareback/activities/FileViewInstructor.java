package com.fantasticfive.shareback.newshareback.activities;

import android.app.Dialog;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.dialogs.DirExplorerDialog;
import com.fantasticfive.shareback.newshareback.helpers.EventHelper;
import com.fantasticfive.shareback.newshareback.helpers.InitConnectionHelper;
import com.fantasticfive.shareback.newshareback.dialogs.SessionCloseDialog;
import com.fantasticfive.shareback.newshareback.helpers.PdfViewHelper;
import com.fantasticfive.shareback.newshareback.helpers.DirFileViewHelper;

import java.util.LinkedHashSet;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FileViewInstructor extends AppCompatActivity
        implements DirFileViewHelper.FileDwnldCallback
        ,PdfViewHelper.PdfHelperCallback
        ,DirExplorerDialog.DirExplorerActivityCallback
        ,SessionCloseDialog.SessionCloseCallback {

    PdfViewHelper pdfViewHelper;
    EventHelper eventHelper;
    ShareBucket bucket;
    String sessionId, sessionName = "";
    InitConnectionHelper initConnectionHelper;

    RelativeLayout overlay;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_view_instructor);

        init();

        //Opening Socket and Services
        new Thread(new Runnable() {
            @Override
            public void run() {
                initConnectionHelper.openSocket(sessionName);
            }
        }).start();
        //-- Opening Socket and Services

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFileDialog();
            }
        });

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            SessionCloseDialog alert = new SessionCloseDialog();
            alert.show(getSupportFragmentManager(), "Dismiss");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.instructor_menus, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add_file: showAddFileDialog(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){

        overlay = (RelativeLayout) findViewById(R.id.overlay);
        img = (ImageView) overlay.findViewById(R.id.img);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        bucket = new ShareBucket();
        sessionId = getIntent().getStringExtra(Constants.KEY_SESSION_ID);
        sessionName = getIntent().getStringExtra(Constants.KEY_SESSION_NAME);
        bucket.setSessionId(sessionId);

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
        //Hide overlay
        if(overlay.getVisibility() == View.VISIBLE)
            overlay.setVisibility(View.GONE);

        //Send Event FILE_CHANGED
        eventHelper.sendEvent(Constants.EVENT_FILE_CHANGED, filePath, pageNo, initConnectionHelper.getClientList());
    }

    @Override
    public void onFileFinallySelected(LinkedHashSet<String> files) {
        Toast.makeText(FileViewInstructor.this, "All Files Finally Selected", Toast.LENGTH_SHORT).show();
        //Send Event FILES_ADDED
        eventHelper.sendFiles( files, initConnectionHelper.getClientList());
        //-- Send Event FILES_ADDED
    }

    @Override
    public void onPositiveClick() {
        Toast.makeText(FileViewInstructor.this, "Positive", Toast.LENGTH_SHORT).show();
        eventHelper.sendEvent(Constants.EVENT_SESSION_CLOSED, "", -1, initConnectionHelper.getClientList());

        //Closing Connections
        initConnectionHelper.packUp();
        finish();
        //-- Closing Connections
    }

    @Override
    public void onNegativeClick() {
        //Toast.makeText(FileViewInstructor.this, "Negative", Toast.LENGTH_SHORT).show();
    }
    //-- Callbacks

    public void showAddFileDialog(){
        Dialog dialog = new DirExplorerDialog(FileViewInstructor.this);
        dialog.show();
    }
}
