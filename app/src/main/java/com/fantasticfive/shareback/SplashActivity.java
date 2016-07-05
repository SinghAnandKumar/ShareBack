package com.fantasticfive.shareback;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fantasticfive.shareback.activity.StartActivity;
import com.fantasticfive.shareback.utils.Sleeper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity implements Sleeper.Callback{

    AppBarLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        layout = (AppBarLayout) findViewById(R.id.app_bar);
        final CollapsingToolbarLayout collapse = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        layout.setExpanded(false);
        layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (    (collapse.getHeight() - appBarLayout.getBottom()) == 0  ) {

                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });


        new Sleeper(this).execute();
    }


    @Override
    public void onWakeup() {
        layout.setExpanded(true, true);
        findViewById(R.id.splash_img).animate().alpha(0).setDuration(200);
    }


}