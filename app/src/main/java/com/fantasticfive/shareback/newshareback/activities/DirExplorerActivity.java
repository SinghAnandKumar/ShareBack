package com.fantasticfive.shareback.newshareback.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.adapters.DirListAdapter;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.utils.DirHelper;

public class DirExplorerActivity extends Dialog
        implements DirHelper.Callback, DirListAdapter.Callback{

    ListView dirList = null;
    AppCompatButton btnOk;
    Context context;

    DirListAdapter adapter = null;
    DirHelper tracker;

    public DirExplorerActivity(Activity activity, DirHelper.FileDwnldCallback fdCallback){
        super(activity);
        this.context = activity;
        setContentView(R.layout.activity_explorer);

        init();

        tracker = new DirHelper(context, this, fdCallback);
        tracker.getItemList("");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAndDownload();
            }
        });
    }

    public void init(){
        dirList = (ListView) findViewById(R.id.list_dir);
        btnOk = (AppCompatButton) findViewById(R.id.btnOk);
    }


    public void closeAndDownload(){
        dismiss();
    }

    @Override
    public void onBackPressed() {
        Log.e("My Tag", "back presssed");
        tracker.getParentList();
    }

    //Callbacks
    @Override
    public void onFileClicked(String item, boolean isChecked) {
        Toast.makeText(context, "Clicked File: "+item, Toast.LENGTH_SHORT).show();

        tracker.downloadFile(item, isChecked);
    }

    @Override
    public void onDirClicked(String item) {
        Toast.makeText(context, "Clicked Dir: "+item, Toast.LENGTH_SHORT).show();
        tracker.getItemList(item);
    }

    @Override
    public void onListReceive(DirContentsBean bean) {
            adapter = new DirListAdapter(context, bean, this);
            dirList.setAdapter(adapter);
    }
    //-- Callbacks
}
