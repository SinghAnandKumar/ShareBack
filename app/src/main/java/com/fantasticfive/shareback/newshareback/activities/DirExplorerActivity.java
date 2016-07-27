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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class DirExplorerActivity extends Dialog
        implements DirHelper.Callback, DirListAdapter.Callback{

    ListView dirList = null;
    AppCompatButton btnOk;
    Context context;
    DirExplorerActivityCallback callback;

    DirListAdapter adapter = null;
    DirHelper helper;
    LinkedHashSet<String> set = new LinkedHashSet<>();

    public DirExplorerActivity(Activity activity){
        super(activity);

        this.context = activity;
        this.callback = (DirExplorerActivityCallback) activity;

        setContentView(R.layout.activity_explorer);
        init();

        helper = new DirHelper(context, this, (DirHelper.FileDwnldCallback) activity);
        helper.getItemList("");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAndSendEvent();
            }
        });
    }

    public void init(){
        dirList = (ListView) findViewById(R.id.list_dir);
        btnOk = (AppCompatButton) findViewById(R.id.btnOk);
    }


    public void closeAndSendEvent(){
        callback.onFileFinallySelected(set);
        dismiss();
    }

    @Override
    public void onBackPressed() {
        Log.e("My Tag", "back presssed");
        helper.getParentList();
    }

    //Callbacks
    @Override
    public void onFileClicked(String item, boolean isChecked) {
        Toast.makeText(context, "Clicked File: "+item, Toast.LENGTH_SHORT).show();

        if(isChecked){
            set.add(helper.getCurrentDir() + item);
        }
        else{
            set.remove(helper.getCurrentDir() + item);
        }

        helper.downloadFile(item, isChecked);
    }

    @Override
    public void onDirClicked(String item) {
        Toast.makeText(context, "Clicked Dir: "+item, Toast.LENGTH_SHORT).show();
        helper.getItemList(item);
    }

    @Override
    public void onListReceive(DirContentsBean bean) {
            adapter = new DirListAdapter(context, bean, this);
            dirList.setAdapter(adapter);
    }
    //-- Callbacks

    public interface DirExplorerActivityCallback{
        void onFileFinallySelected(LinkedHashSet<String> files);
    }
}
