package com.fantasticfive.shareback.concept2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.view.dialogs.SessionFetchDialog;
import com.fantasticfive.shareback.concept2.view.dialogs.SessionNameDialog;

import com.mingle.widget.LoadingView;

public class MainActivity
        extends AppCompatActivity{

    AppCompatButton btnCreateSession, btnJoinSession;
    AppBarLayout layout;
    Snackbar snackbar;
    LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_main);
        init();
        actionBarAnimate();

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
                SessionFetchDialog dialog = new SessionFetchDialog();
                dialog.show(getSupportFragmentManager(), "CreatedSession Fetch");
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

    /*private float avg(int r[]){

        int sum = 0;
        int count = 0;
        for(int i=0; i<r.length; i++){
            sum += r[i]*(i+1);
            count += r[i];
        }

        try {
            float avg = (float) sum / count;
            return avg;
        }
        catch (ArithmeticException e){
            e.printStackTrace();
        }
        return 0f;
    }
*/
    public void init(){
        btnCreateSession = (AppCompatButton) findViewById(R.id.btnCreateSession);
        btnJoinSession = (AppCompatButton) findViewById(R.id.btnJoinSession);
        layout = (AppBarLayout) findViewById(R.id.app_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        loadingView = (LoadingView) findViewById(R.id.sessionInfoLodingView);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start, menu);*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            default: return super.onOptionsItemSelected(item);
        }
    }
}