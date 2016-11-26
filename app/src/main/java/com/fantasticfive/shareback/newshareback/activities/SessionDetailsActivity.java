package com.fantasticfive.shareback.newshareback.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.dto.SessionDTO;
import com.fantasticfive.shareback.newshareback.adapters.CommentsAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SessionDetailsActivity extends AppCompatActivity {

    TextView star1, star2, star3, star4, star5;
    ListView lvComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details2);
        init();
        setData();

    }

    private void init(){
        star1 = (TextView) findViewById(R.id.star1);
        star2 = (TextView) findViewById(R.id.star2);
        star3 = (TextView) findViewById(R.id.star3);
        star4 = (TextView) findViewById(R.id.star4);
        star5 = (TextView) findViewById(R.id.star5);

        lvComments = (ListView) findViewById(R.id.lvComments);
    }

    private void setData(){
        String dtoStr = getIntent().getExtras().getString(Constants.KEY_SESSION_DETAILS);
        SessionDTO dto = new Gson().fromJson(dtoStr, SessionDTO.class);

        //Session Name
        getSupportActionBar().setTitle(dto.getSessionName());

        //Ratings
        int[] rating = dto.getRating();
        star1.setText(rating[0]+"");
        star2.setText(rating[1]+"");
        star3.setText(rating[2]+"");
        star4.setText(rating[3]+"");
        star5.setText(rating[4]+"");

        //Comments
        ArrayList<String> comments = dto.getComments();
        if(comments.size() < 1){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.err_no_comments);
            adapter.add("No Comments Yet");
            lvComments.setAdapter(adapter);
        }
        else{
            CommentsAdapter adapter = new CommentsAdapter(this, dto.getComments());
            lvComments.setAdapter(adapter);
        }
    }
}
