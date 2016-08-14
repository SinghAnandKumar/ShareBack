package com.fantasticfive.shareback.newshareback.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.adapters.DirExplorerAdapter;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.helpers.DirFileViewHelper;

import java.util.LinkedHashSet;

public class DirExplorerDialog extends Dialog
        implements DirFileViewHelper.Callback, DirExplorerAdapter.Callback{

    ListView dirList = null;
    AppCompatButton btnOk;
    ProgressBar progressBar;
    Context context;
    LinearLayout lvParents;
    HorizontalScrollView horizontalScrollView;

    DirExplorerActivityCallback callback;

    DirExplorerAdapter adapter = null;
    DirFileViewHelper helper;
    LinkedHashSet<String> set = new LinkedHashSet<>();

    public DirExplorerDialog(Activity activity){
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.context = activity;
        this.callback = (DirExplorerActivityCallback) activity;

        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        setContentView(R.layout.dialog_dir_explorer);
        init();

        helper = new DirFileViewHelper(context, this, (DirFileViewHelper.FileDwnldCallback) activity);
        getDirContents("");

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
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollview);

        lvParents  = (LinearLayout) findViewById(R.id.parent_list);
    }


    public void closeAndSendEvent(){
        callback.onFileFinallySelected(set);
        dismiss();
    }

    @Override
    public void onBackPressed() {
        Log.e("My Tag", "back presssed");
        if(!helper.getCurrentDir().equals("/")) {
            showProgressBar(true);
            helper.getParentList();
        }
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
        getDirContents(item);
    }

    @Override
    public void onListReceive(DirContentsBean bean) {

        //Change Parent View
        lvParents.removeAllViews();
        boolean skipFirstFlag = true;
        for (String parentName: helper.getCurrentDir().split("/")){

            if(skipFirstFlag){skipFirstFlag = false; continue;}
            lvParents.addView(getParentSeparator());
            lvParents.addView(getParentView(parentName));
        }
        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });

        //update list
        adapter = new DirExplorerAdapter(context, bean, this);
        dirList.setAdapter(adapter);
        showProgressBar(false);
    }
    //-- Callbacks

    private void showProgressBar(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private ImageView getParentSeparator(){
        ImageView imgSep = (ImageView) LayoutInflater.from(context).inflate(R.layout.inner_parent_list_sep, null);
        return imgSep;
    }

    private TextView getParentView(String parentName){
        TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.inner_parent_list, null);
        tv.setText(parentName);
        return tv;
    }

    private void getDirContents(String dir){
        showProgressBar(true);
        helper.getItemList(dir);
    }

    public interface DirExplorerActivityCallback{
        void onFileFinallySelected(LinkedHashSet<String> files);
    }
}
