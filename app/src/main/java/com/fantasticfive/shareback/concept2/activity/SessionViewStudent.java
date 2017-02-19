package com.fantasticfive.shareback.concept2.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.helper.FirebaseStudentHelper;

/**
 * Created by sagar on 16/2/17.
 */
public class SessionViewStudent extends AppCompatActivity {

    ListViewCompat lv;
    FirebaseStudentHelper helper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_session_view_student);
        init();

        helper.register();
    }

    @Override
    protected void onStart() {
        helper.active();
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

        //Backend Data
        String sessionId = getIntent().getExtras().getString(Constants.SESSION_ID);
        helper = new FirebaseStudentHelper(this, sessionId);
    }

}
