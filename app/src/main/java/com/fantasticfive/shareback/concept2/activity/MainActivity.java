package com.fantasticfive.shareback.concept2.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.bean.CreatedSession;
import com.fantasticfive.shareback.concept2.bean.JoinedSession;
import com.fantasticfive.shareback.concept2.bean.Session;
import com.fantasticfive.shareback.concept2.helper.FirebaseUserSessionHelper;
import com.fantasticfive.shareback.concept2.util.MathUtils;
import com.fantasticfive.shareback.concept2.util.UserData;
import com.fantasticfive.shareback.concept2.util.WordUtils;
import com.fantasticfive.shareback.concept2.view.dialogs.ActiveSessionDialog;
import com.fantasticfive.shareback.concept2.view.dialogs.SessionNameDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mingle.widget.LoadingView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity
        extends AppCompatActivity
        implements FirebaseUserSessionHelper.Callback{

    private static final String TAG = "MY TAG";
    AppCompatButton btnCreateSession, btnJoinSession;
    AppBarLayout layout;
    Snackbar snackbar;
    LoadingView loadingView;
    LinearLayout llRecentSessions;

    FirebaseUserSessionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_main);
        init();
        actionBarAnimate();
        helper.getSessions();

        btnCreateSession.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionNameDialog dialog = new SessionNameDialog();
                dialog.show(getSupportFragmentManager(), "CreatedSession Create");
            }
        });

        btnJoinSession.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /*SessionFetchDialog dialog = new SessionFetchDialog();
                dialog.show(getSupportFragmentManager(), "CreatedSession Fetch");*/
                ActiveSessionDialog dialog = new ActiveSessionDialog();
                dialog.show(getSupportFragmentManager(), "ActiveSession Dialog");
            }
        });
    }

    @Override
    protected void onStart() {
        /*//Get CreatedSession Info
        SessionInfoChecker sessionInfoChecker = new SessionInfoChecker(this, this);
        sessionInfoChecker.execute();
        loadingView.setVisibility(View.VISIBLE);*/
        super.onStart();
    }

    public void init(){
        btnCreateSession = (AppCompatButton) findViewById(R.id.btnCreateSession);
        btnJoinSession = (AppCompatButton) findViewById(R.id.btnJoinSession);
        layout = (AppBarLayout) findViewById(R.id.app_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        loadingView = (LoadingView) findViewById(R.id.sessionInfoLoadingView);
        llRecentSessions = (LinearLayout) findViewById(R.id.recent_session);

        helper = new FirebaseUserSessionHelper(this, this);

        snackbar = Snackbar.make(findViewById(R.id.top_parent), "Can't Connect to Server", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
    }

    private void actionBarAnimate(){

            final TextView tv = (TextView) findViewById(R.id.tvHeader);
            Typeface tf = Typeface.createFromAsset(getAssets(), Globals.font_bold);
            tv.setTypeface(tf);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        *//*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start, menu);*//*

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            default: return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public void onSessionFetched(ArrayList<Session> sessions) {
        View view = null;
        llRecentSessions.removeAllViews();
        for(Session session: sessions){
            switch (session.getType()){
                case Session.CREATED: view = getCreatedSessionCard((CreatedSession) session); break;
                case Session.JOINED: view = getJoinedSessionCard((JoinedSession) session); break;
            }
            llRecentSessions.addView(view);
        }
    }

    public View getCreatedSessionCard(CreatedSession createdSession){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.c2_inner_recent_sessions, null);
        TextView tvSessionName = (TextView) view.findViewById(R.id.session_name);
        TextView tvSessionId = (TextView) view.findViewById(R.id.session_id);
        TextView tvSessionType = (TextView) view.findViewById(R.id.session_type);
        TextView tvLetterIcon = (TextView) view.findViewById(R.id.firstChar);
        View viewRating = view.findViewById(R.id.vgRating);
        TextView tvRating = (TextView) view.findViewById(R.id.tvRating);
        TextView tvUserCount = (TextView) view.findViewById(R.id.tvUsers);

        String sessionName = createdSession.getSessionName();
        sessionName = WordUtils.capitalizeFirstChar(sessionName);
        tvSessionName.setText(sessionName);
        tvSessionId.setText(createdSession.getSessionId());

        tvLetterIcon.setText(WordUtils.firstChar(sessionName));
        tvSessionType.setText(getSessionTypeString(createdSession));
        tvSessionType.setTextColor(ContextCompat.getColor(this, R.color.teal));

        //Setting Rating, UserCount
        viewRating.setVisibility(View.VISIBLE);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        double d = MathUtils.avg(createdSession.getRatings());
        tvRating.setText(df.format(d)+"");
        tvUserCount.setText(createdSession.getJoinedUsers().size()+"");

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: I AM HERE" );
                String sessionId = ((TextView) view.findViewById(R.id.session_id)).getText().toString();
                String sessionName = ((TextView) view.findViewById(R.id.session_name)).getText().toString();

                Intent intent = new Intent(MainActivity.this, FeedbackViewActivity.class);
                intent.putExtra(Constants.SESSION_ID, sessionId);
                intent.putExtra(Constants.SESSION_NAME, sessionName);
                startActivity(intent);
            }
        });

        return view;
    }

    public View getJoinedSessionCard(JoinedSession joinedSession){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.c2_inner_recent_sessions, null);
        TextView tvSessionName = (TextView) view.findViewById(R.id.session_name);
        TextView tvSessionType = (TextView) view.findViewById(R.id.session_type);
        TextView tvLetterIcon = (TextView) view.findViewById(R.id.firstChar);
        TextView tvSessionId = (TextView) view.findViewById(R.id.session_id);
        TextView tvInstructorName = (TextView) view.findViewById(R.id.instructor_name);
        TextView tvInstructorId = (TextView) view.findViewById(R.id.instructor_id);

        String sessionName = joinedSession.getSessionName();
        sessionName = WordUtils.capitalizeFirstChar(sessionName);
        tvSessionName.setText(sessionName);
        tvLetterIcon.setText(WordUtils.firstChar(sessionName));
        tvSessionId.setText(joinedSession.getSessionId());
        tvSessionType.setText(getSessionTypeString(joinedSession));
        tvInstructorName.setVisibility(View.VISIBLE);
        tvInstructorName.setText(joinedSession.getInstructorName());
        tvInstructorId.setText(joinedSession.getInstructorId());

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: I AM HERE" );
                String sessionId = ((TextView) view.findViewById(R.id.session_id)).getText().toString();
                String sessionName = ((TextView) view.findViewById(R.id.session_name)).getText().toString();
                String instructorId = ((TextView) view.findViewById(R.id.instructor_id)).getText().toString();

                Intent intent = new Intent(MainActivity.this, SessionViewStudent.class);
                ActiveSession activeSession = new ActiveSession();
                activeSession.setSessionId(sessionId);
                activeSession.setSessionName(sessionName);
                activeSession.setInstructorId(instructorId);
                intent.putExtra(Constants.ACTIVE_SESSION, new Gson().toJson(activeSession));
                startActivity(intent);
            }
        });

        return view;
    }

    private String getSessionTypeString(Session session){
        switch (session.getType()){
            case Session.CREATED: return getString(R.string.session_created);
            case Session.JOINED: return getString(R.string.session_joined);
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.c2_menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.logout: logout();

            default: return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        UserData.getInstance().removeInstance();
        finish();
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}