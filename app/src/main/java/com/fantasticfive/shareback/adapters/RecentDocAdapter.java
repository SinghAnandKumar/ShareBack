package com.fantasticfive.shareback.adapters;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.activity.SessionDetailsActivity;
import com.fantasticfive.shareback.beans.RecentDoc;
import com.fantasticfive.shareback.db.QueriesSessions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Sagar on 25-03-2016.
 */


public class RecentDocAdapter{
    LinearLayout layout = null;
    Context context = null;
    ArrayList<RecentDoc> recentDocs;
    Callback callback = null;


    public RecentDocAdapter(Context context, LinearLayout layout, Callback callback){
        this.context = context;
        this.layout = layout;
        recentDocs = new QueriesSessions(context).getRecDocs();
        this.callback = callback;
    }

    public void addViews(){

        int count = 0;
        final ArrayList<View> views = new ArrayList<>();

        for (RecentDoc recentDoc : recentDocs) {
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.inner_start_rec_docs, null);
            LinearLayout cardView = (LinearLayout) view.findViewById(R.id.card_view);
            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
            TextView textAvgRating = (TextView) view.findViewById(R.id.textAvgRating);
            TextView tvAvgRating = (TextView) view.findViewById(R.id.tvRating);
            AppCompatButton btnStartSession = (AppCompatButton) view.findViewById(R.id.bStartSession);
            AppCompatButton btnViewDetails = (AppCompatButton) view.findViewById(R.id.bViewDetails);

            Typeface tf = Typeface.createFromAsset(context.getAssets(), Globals.font);
            tvName.setTypeface(tf);
            tvDate.setTypeface(tf);
            textAvgRating.setTypeface(tf);
            tvAvgRating.setTypeface(tf);
            btnStartSession.setTypeface(tf);
            btnViewDetails.setTypeface(tf);

            //Card View Background
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setBackground(context.getDrawable(R.drawable.ripple_selector));
            }
            //-- Card View Background

            //Add RecentDoc Name
            tvName.setText(new File(recentDoc.getName()).getName());
            layout.addView(view);
            //-- Add RecentDoc Name

            //Add RecentDoc Date And Time
            SimpleDateFormat format = new SimpleDateFormat("dd MMM, hh:mm a");
            String date = format.format(recentDoc.getDate());
            tvDate.setText(date);
            //-- Add RecentDoc Date And Time

            cardView.setId(count);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = views.get(v.getId());
                    final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.detailButtons);
                    if(layout.getVisibility() == View.VISIBLE){
                        layout.animate().scaleY(0).setDuration(200).setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(layout.getScaleY() == 0)
                                    layout.setVisibility(View.GONE);
                                else{
                                    layout.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                    }
                    else {
                        layout.setVisibility(View.VISIBLE);
                        layout.animate().scaleY(1).setDuration(200);
                    }
                }
            });

            btnStartSession.setId(count);
            btnStartSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    RecentDoc rd = recentDocs.get(i);
                    callback.onCardClicked(rd.getName());
                }
            });

            btnViewDetails.setId(count++);
            btnViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    RecentDoc rd = recentDocs.get(i);
                    Intent intent = new Intent(context, SessionDetailsActivity.class);
                    intent.putExtra(Globals.KEY_SESSION, rd.getSessionId());
                    context.startActivity(intent);
                }
            });

            views.add(view);
        }
    }

    public void notifyChange(){
        //recentDocs = new QueriesRecentDocs(context).getDocs();
        recentDocs = new QueriesSessions(context).getRecDocs();
        layout.removeAllViews();
        addViews();
    }

    public interface Callback{
        void onCardClicked(String fileName);
    }
}
