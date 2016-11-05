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
import com.fantasticfive.shareback.newshareback.adapters.CopyMoveAdapter;
import com.fantasticfive.shareback.newshareback.adapters.DirExplorerAdapter;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.helpers.DirFileViewHelper;
import com.fantasticfive.shareback.newshareback.helpers.DirManagerHelper;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class CopyMoveDialog extends Dialog
        implements DirManagerHelper.Callback,
        CopyMoveAdapter.Callback{

    public static final int MOVE = 1;
    public static final int COPY = 2;

    ListView dirList = null;
    AppCompatButton btnOk;
    ProgressBar progressBar;
    Context context;
    LinearLayout lvParents;
    HorizontalScrollView horizontalScrollView;

    CopyMoveDialogCallback callback;

    CopyMoveAdapter adapter = null;
    DirManagerHelper helper;

    private int operation = 0;
    ArrayList<String> files;

    public CopyMoveDialog(Activity activity, String initPath, int operation, ArrayList<String> files){
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.context = activity;
        this.callback = (CopyMoveDialogCallback) activity;
        this.operation = operation;
        this.files = files;

        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        setContentView(R.layout.dialog_copy_move_explorer);
        init();

        helper = new DirManagerHelper(context, this);
        getDirContents(initPath);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dirSelected();
            }
        });
    }

    public void init(){
        dirList = (ListView) findViewById(R.id.list_dir);
        btnOk = (AppCompatButton) findViewById(R.id.btnOk);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollview);

        lvParents  = (LinearLayout) findViewById(R.id.parent_list);

        switch (operation){
            case COPY: btnOk.setText(R.string.text_copy_here); break;
            case MOVE: btnOk.setText(R.string.text_move_here); break;
        }
    }


    public void dirSelected(){

        switch (operation){
            case COPY: callback.copyFiles(helper.getCurrentDir(), files); break;
            case MOVE: callback.moveFiles(helper.getCurrentDir(), files); break;
            default:
                Toast.makeText(context, "No Operation to perform", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    @Override
    public void onBackPressed() {
        showProgressBar(true);
        helper.getParentList();
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
        adapter = new CopyMoveAdapter(context, bean, this);
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

    public interface CopyMoveDialogCallback{
        void moveFiles(String destDir, ArrayList<String> al);
        void copyFiles(String destDir, ArrayList<String> al);
    }
}
