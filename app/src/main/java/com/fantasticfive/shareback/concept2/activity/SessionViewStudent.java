package com.fantasticfive.shareback.concept2.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.helper.FirebaseStudentHelper;
import com.fantasticfive.shareback.concept2.view.adapters.ShareFilesStudentAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sagar on 16/2/17.
 */
public class SessionViewStudent extends AppCompatActivity implements FirebaseStudentHelper.Callback {

    private static final String TAG = "MY TAG";
    ListViewCompat lv;
    View placeHolder;
    FirebaseStudentHelper helper;
    ShareFilesStudentAdapter adapter;
    ArrayList<SharedFile> sharedFiles;

    ActiveSession activeSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_session_view_student);
        init();

        helper.register(activeSession);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedFile file = sharedFiles.get(position);
                helper.download(file);
            }
        });
    }

    @Override
    protected void onStart() {
        helper.active(activeSession);
        super.onStart();
    }

    @Override
    protected void onStop() {
        helper.inactive();
        super.onStop();
    }

    protected void init(){
        //UI Data
        lv = (ListViewCompat) findViewById(R.id.lv);
        placeHolder = findViewById(R.id.place_holder);

        //Backend Data
        String strActiveSession = getIntent().getExtras().getString(Constants.ACTIVE_SESSION);
        activeSession = new Gson().fromJson(strActiveSession, ActiveSession.class);
        String sessionId = activeSession.getSessionId();
        helper = new FirebaseStudentHelper(this, sessionId, this);
        sharedFiles = new ArrayList<>();
        adapter = new ShareFilesStudentAdapter(this, sharedFiles);
    }

    @Override
    public void onDocumentsChanged(ArrayList<SharedFile> newSharedFiles) {
        if(newSharedFiles.size() != 0) {
            placeHolder.setVisibility(View.GONE);
            sharedFiles.clear();
            sharedFiles.addAll(newSharedFiles);
            adapter.notifyDataSetChanged();
        }
        else{
            placeHolder.setVisibility(View.VISIBLE);
        }
    }
}
