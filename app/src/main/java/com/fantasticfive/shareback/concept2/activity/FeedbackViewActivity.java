package com.fantasticfive.shareback.concept2.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;

import com.fantasticfive.shareback.concept2.bean.CreatedSession;
import com.fantasticfive.shareback.concept2.bean.Rating;
import com.fantasticfive.shareback.concept2.helper.FirebaseFeedbackHelper;
import com.fantasticfive.shareback.concept2.util.MathUtils;
import com.fantasticfive.shareback.concept2.view.adapters.CommentsAdapter;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FeedbackViewActivity extends AppCompatActivity implements FirebaseFeedbackHelper.Callback
{

    TextView star1, star2, star3, star4, star5;
    View star1Bar, star2Bar, star3Bar, star4Bar, star5Bar;
    ListView lvComments;
    CommentsAdapter adapter;
    ImageView imgBkg;
    TextView textComment;
    TextView tvRating;
    TextView tvUsers;
    AppCompatRatingBar rbAvgRating;

    String sessionId, sessionName;
    ArrayList<String> comments;

    CreatedSession createdSession;
    FirebaseFeedbackHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_feedback);
        init();
        lvComments.setAdapter(adapter);
        helper.listenForFeedbacks(createdSession);
        getSupportActionBar().setTitle(sessionName);
    }

    private void init(){
        star1 = (TextView) findViewById(R.id.star1);
        star2 = (TextView) findViewById(R.id.star2);
        star3 = (TextView) findViewById(R.id.star3);
        star4 = (TextView) findViewById(R.id.star4);
        star5 = (TextView) findViewById(R.id.star5);
        star1Bar = findViewById(R.id.star1Bar);
        star2Bar = findViewById(R.id.star2Bar);
        star3Bar = findViewById(R.id.star3Bar);
        star4Bar = findViewById(R.id.star4Bar);
        star5Bar = findViewById(R.id.star5Bar);
        lvComments = (ListView) findViewById(R.id.lvComments);
        imgBkg = (ImageView) findViewById(R.id.bkg_img);
        textComment = (TextView) findViewById(R.id.textComment);
        tvRating = (TextView) findViewById(R.id.tvRating);
        rbAvgRating = (AppCompatRatingBar) findViewById(R.id.ratingBar);
        tvUsers = (TextView) findViewById(R.id.tvUsers);

        sessionId = getIntent().getExtras().getString(Constants.SESSION_ID);
        sessionName = getIntent().getExtras().getString(Constants.SESSION_NAME);
        helper = new FirebaseFeedbackHelper(this, this);
        comments = new ArrayList<>();
        adapter = new CommentsAdapter(this, comments);
        createdSession = new CreatedSession();
        createdSession.setSessionId(sessionId);
        createdSession.setSessionName(sessionName);
    }

    @Override
    public void onCommentChange(ArrayList<String> comments) {
        this.comments.clear();
        if(comments!=null) {
            this.comments.addAll(comments);
            textComment.setVisibility(View.VISIBLE);
            imgBkg.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
        else{
            Toast.makeText(FeedbackViewActivity.this, "Nothing in Comments", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRatingChange(Rating ratings) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(1);
        double avgRating = MathUtils.avg(ratings);

        tvRating.setText(format.format(avgRating));
        rbAvgRating.setRating((float)avgRating);

        star1.setText(ratings.getRating1()+"");
        star2.setText(ratings.getRating2()+"");
        star3.setText(ratings.getRating3()+"");
        star4.setText(ratings.getRating4()+"");
        star5.setText(ratings.getRating5()+"");

        ViewGroup.LayoutParams params;
        float star1pc = MathUtils.percentage(ratings.getRating1(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star1pc);
        star1Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star1pc);
        star1.setLayoutParams(params);

        float star2pc = MathUtils.percentage(ratings.getRating2(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star2pc);
        star2Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star2pc);
        star2.setLayoutParams(params);

        float star3pc = MathUtils.percentage(ratings.getRating3(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star3pc);
        star3Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star3pc);
        star3.setLayoutParams(params);

        float star4pc = MathUtils.percentage(ratings.getRating4(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star4pc);
        star4Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star4pc);
        star4.setLayoutParams(params);

        float star5pc = MathUtils.percentage(ratings.getRating5(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star5pc);
        star5Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star5pc);
        star5.setLayoutParams(params);

    }

    @Override
    public void onUsersJoined(ArrayList<String> users) {
        tvUsers.setText(users.size()+"");
    }
}
