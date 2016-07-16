package com.fantasticfive.shareback.newshareback.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.adapters.DirListAdapter;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.utils.DirLister;
import com.fantasticfive.shareback.newshareback.utils.FileReceiver;
import com.fantasticfive.shareback.newshareback.utils.DirTracker;

import java.io.File;

public class DirExplorerActivity extends AppCompatActivity
        implements DirTracker.Callback, DirListAdapter.Callback{

    ListView dirList = null;

    DirListAdapter adapter = null;
    DirTracker tracker;
    DirLister lister = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        init();

        tracker = new DirTracker(this, this);
        tracker.getItemList("");
    }

    public void init(){
        dirList = (ListView) findViewById(R.id.list_dir);
    }

    @Override
    public void onFileClicked(String item) {
        Toast.makeText(DirExplorerActivity.this, "Clicked File: "+item, Toast.LENGTH_SHORT).show();
        tracker.getFile(item);
    }

    @Override
    public void onDirClicked(String item) {
        Toast.makeText(DirExplorerActivity.this, "Clicked Dir: "+item, Toast.LENGTH_SHORT).show();
        tracker.getItemList(item);
    }

    @Override
    public void onListReceive(DirContentsBean bean) {
            adapter = new DirListAdapter(this, bean, this);
            dirList.setAdapter(adapter);
    }

    @Override
    public void onFileReceive(File file) {
        Toast.makeText(DirExplorerActivity.this, "File Downloaded", Toast.LENGTH_SHORT).show();
    }
}
