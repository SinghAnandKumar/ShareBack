package com.fantasticfive.shareback.newshareback.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRatingBar;
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
import android.widget.Toast;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.dto.SessionDTO;
import com.fantasticfive.shareback.newshareback.beans.SessionInfoBean;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.dialogs.DialogIp;
import com.fantasticfive.shareback.newshareback.dialogs.SessionInfoDialog;
import com.fantasticfive.shareback.newshareback.fileoperation.SessionDetailsFetcher;
import com.fantasticfive.shareback.newshareback.fileoperation.SessionInfoChecker;
import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.google.gson.Gson;
import com.mingle.widget.LoadingView;

import java.util.ArrayList;

public class NewMainActivity
        extends AppCompatActivity
        implements SessionInfoChecker.SessionInfoCallback {

    AppCompatButton btnCreateSession, btnJoinSession;
    AppBarLayout layout;
    Snackbar snackbar;
    ProgressDialog progress;
    LoadingView loadingView;
    int NSD_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        init();
        actionBarAnimate();
        setPreferredIp();

        btnCreateSession.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionInfoDialog dialog = new SessionInfoDialog();
                dialog.show(getSupportFragmentManager(), "CreatedSession Info");
            }
        });

        btnJoinSession.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewMainActivity.this, NsdDiscoverActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        //Get CreatedSession Info
        SessionInfoChecker sessionInfoChecker = new SessionInfoChecker(this, this);
        sessionInfoChecker.execute();
        loadingView.setVisibility(View.VISIBLE);
        super.onStart();
    }

    private void refreshSessionList(ArrayList<SessionInfoBean> sessionInfoBeans){

        Log.e("My Tag", "Refreshing List");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.recent_session);
        linearLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        for(SessionInfoBean bean: sessionInfoBeans ){
            View view = inflater.inflate(R.layout.inner_new_main, null);
            TextView tvSessionName = (TextView) view.findViewById(R.id.session_name);
            TextView tvSessionId = (TextView) view.findViewById(R.id.session_id);
            AppCompatRatingBar ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rbSession);
            MaterialLetterIcon letter = (MaterialLetterIcon) view.findViewById(R.id.letter);

            tvSessionName.setText(bean.getSessionName());
            tvSessionId.setText(bean.getSessionId());
            float avg = avg(bean.getRating());
            ratingBar.setRating(avg);
            letter.setLetter( bean.getSessionName().charAt(0)+"");

            view.setOnClickListener(new CardListener());
            linearLayout.addView(view);
        }
    }

    private float avg(int r[]){

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

    public void init(){
        btnCreateSession = (AppCompatButton) findViewById(R.id.btnCreateSession);
        btnJoinSession = (AppCompatButton) findViewById(R.id.btnJoinSession);
        layout = (AppBarLayout) findViewById(R.id.app_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        loadingView = (LoadingView) findViewById(R.id.sessionInfoLoadingView);

        snackbar = Snackbar.make(findViewById(R.id.top_parent), "Can't Connect to Server", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
    }

    private void actionBarAnimate(){

            getSupportActionBar().show();

            final CollapsingToolbarLayout collapse = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            final TextView tv = (TextView) findViewById(R.id.tvHeader);
            Typeface tf = Typeface.createFromAsset(getAssets(), Globals.font_bold);
            tv.setTypeface(tf);
    }

    private void startManageFilesActivity(){
        Intent intent = new Intent(NewMainActivity.this, ManageFilesActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_ip: showServerIpDialog();  return true;
            case R.id.action_manage_files: startManageFilesActivity(); return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    private void setPreferredIp(){
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE );
        String str = prefs.getString(Constants.PREF_SERVER_IP, null);
        Constants.IP_FILE_SERVER = (str == null) ? "" : str;
    }

    private void showServerIpDialog(){
        DialogIp dialog = new DialogIp();
        dialog.show(getSupportFragmentManager(), "Server Ip Dialog");
    }

    @Override
    public void onSessionInfoResponse(ArrayList<SessionInfoBean> sessionInfoBean) {
        refreshSessionList(sessionInfoBean);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onConnectErr() {
        Toast.makeText(NewMainActivity.this, "Can't Connect to Server", Toast.LENGTH_SHORT).show();
        loadingView.setVisibility(View.GONE);
    }

    class CardListener implements OnClickListener, SessionDetailsFetcher.Callback{
        @Override
        public void onClick(View view) {
            String sessionId = ((TextView) view.findViewById(R.id.session_id)).getText().toString();

            final SessionDetailsFetcher fetcher = new SessionDetailsFetcher(this);
            progress = ProgressDialog.show(NewMainActivity.this, "", "Just a Moment...");
            progress.setCancelable(true);
            progress.setOnCancelListener(new DialogInterface.OnCancelListener(){
                @Override
                public void onCancel(DialogInterface arg0) {
                    fetcher.cancel();
                }
            });
            fetcher.fetch(sessionId);
        }

        @Override
        public void onSessionDetailsReceived(SessionDTO dto) {

            progress.dismiss();

            Intent intent = new Intent(NewMainActivity.this, SessionDetailsActivity.class);
            intent.putExtra(Constants.KEY_SESSION_DETAILS, new Gson().toJson(dto)); //Example {"sessionId":"0fc522262ab7f1446ee858743d040d77","sessionName":"DSPD","date":"Nov 3, 2016","rating":[5,0,0,0,5],"comments":[]}
            startActivity(intent);
        }
    }
}