package com.fantasticfive.shareback.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.adapters.ListViewComments;
import com.fantasticfive.shareback.beans.SessionDetails;
import com.fantasticfive.shareback.db.QueriesComments;
import com.fantasticfive.shareback.db.QueriesSessions;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class SessionDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Main Code
        TextView star1 = (TextView) findViewById(R.id.star1);
        TextView star2 = (TextView) findViewById(R.id.star2);
        TextView star3 = (TextView) findViewById(R.id.star3);
        TextView star4 = (TextView) findViewById(R.id.star4);
        TextView star5 = (TextView) findViewById(R.id.star5);
        ListView lv = (ListView) findViewById(R.id.lvComments);

        int sessionId = getIntent().getIntExtra(Globals.KEY_SESSION, 0);
        Toast.makeText(SessionDetailsActivity.this, "CreatedSession Id: "+sessionId, Toast.LENGTH_SHORT).show();
        SessionDetails sessionDetails = new QueriesSessions(this).getSessionDetails(sessionId);
        int stars[] = sessionDetails.getStars();

        star1.setText(stars[0]+"");
        star2.setText(stars[1]+"");
        star3.setText(stars[2]+"");
        star4.setText(stars[3]+"");
        star5.setText(stars[4]+"");

        //Add Comments
        ArrayList<String> comments = new QueriesComments(this).getComments(sessionId);
        ListViewComments adapter = new ListViewComments(this, comments);
        lv.setAdapter(adapter);

        //-- Add Comments
        // -- Main Code
    }

}
