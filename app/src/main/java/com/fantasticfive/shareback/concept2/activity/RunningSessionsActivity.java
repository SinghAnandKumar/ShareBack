package com.fantasticfive.shareback.concept2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.helper.FirebaseRunningSessionHelper;
import com.fantasticfive.shareback.concept2.view.adapters.RunningSessionsAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sagar on 16/2/17.
 */
public class RunningSessionsActivity extends AppCompatActivity implements FirebaseRunningSessionHelper.Callback{

    ListViewCompat lv;
    ArrayList<ActiveSession> sessions;
    FirebaseRunningSessionHelper firebaseHelper;
    RunningSessionsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_session_list);
        init();

        /*String strSessions = getIntent().getExtras().getString(Constants.SESSION_LIST);
        sessions = new Gson().fromJson(strSessions, new TypeToken<ArrayList<CreatedSession>>(){}.getType());*/

        firebaseHelper.readSessions();
    }

    protected void init(){
        lv = (ListViewCompat) findViewById(R.id.lv);

        firebaseHelper = new FirebaseRunningSessionHelper(this, this);
        sessions = new ArrayList<>();
        adapter = new RunningSessionsAdapter(this, sessions);
    }

    @Override
    public void onSessionChange(ArrayList<ActiveSession> newSessions) {
        sessions.clear();
        sessions.addAll(newSessions);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ActiveSession session = sessions.get(position);
                //Toast.makeText(RunningSessionsActivity.this, "Clicked Position "+position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RunningSessionsActivity.this, SessionViewStudent.class);

                String strActiveSession = new Gson().toJson(session);
                intent.putExtra(Constants.ACTIVE_SESSION, strActiveSession);
                startActivity(intent);
            }
        });
    }
}
