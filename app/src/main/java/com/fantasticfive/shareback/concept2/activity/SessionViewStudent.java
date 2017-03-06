package com.fantasticfive.shareback.concept2.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.bean.JoinedSession;
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
    View feedbackCard;
    AppCompatButton btnFeedbackSave;
    AppCompatEditText etFeedbackComment;
    AppCompatRatingBar rbFeedbackRating;

    FirebaseStudentHelper helper;
    ShareFilesStudentAdapter adapter;
    ArrayList<SharedFile> sharedFiles;

    ActiveSession activeSession;
    JoinedSession joinedSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_session_view_student);
        init();

        helper.register(activeSession);
        lv.setAdapter(adapter);
        addListeners();
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
        btnFeedbackSave = (AppCompatButton) findViewById(R.id.btn_save);
        etFeedbackComment = (AppCompatEditText) findViewById(R.id.et_feedback_comment);
        rbFeedbackRating = (AppCompatRatingBar) findViewById(R.id.rb_feedback_rating);
        feedbackCard = findViewById(R.id.feedback_card);

        //Backend Data
        String strActiveSession = getIntent().getExtras().getString(Constants.ACTIVE_SESSION);
        activeSession = new Gson().fromJson(strActiveSession, ActiveSession.class);
        String sessionId = activeSession.getSessionId();
        helper = new FirebaseStudentHelper(this, sessionId, this);
        sharedFiles = new ArrayList<>();
        adapter = new ShareFilesStudentAdapter(this, sharedFiles);
    }

    protected void addListeners(){
        btnFeedbackSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rating = (int)Math.floor((double)rbFeedbackRating.getRating());
                if(rating<=0) {
                    Toast.makeText(SessionViewStudent.this, "Please Rate First", Toast.LENGTH_SHORT).show();
                    return;
                }
                String comment = etFeedbackComment.getText().toString();
                helper.sendComments(activeSession, rating, comment);
                feedbackCard.setVisibility(View.GONE);
                joinedSession.setRating(rating);
                joinedSession.setComment(comment);
                helper.updateJoinEntry(joinedSession);
            }
        });
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

    @Override
    public void onSessionJoined(JoinedSession session) {
        joinedSession = session;
        if(session.getRating() !=0){
            feedbackCard.setVisibility(View.GONE);
        }
    }
}
