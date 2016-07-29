package com.fantasticfive.shareback.newshareback.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.helpers.FeedbackHelper;

/**
 * Created by sagar on 28/7/16.
 */
public class FeedbackDialog extends DialogFragment implements FeedbackHelper.FeedbackHelperCallback {

    FeedbackCallback callback;
    Activity activity;
    private String comment="";
    private String sessionId = "";

    @Override
    public void onAttach(Activity activity) {
        callback  = (FeedbackCallback) activity;
        this.activity = activity;
        super.onAttach(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        sessionId = getArguments().getString(Constants.KEY_SESSION_ID);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_feedback, null);
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), Globals.font);
        Typeface tfBold = Typeface.createFromAsset(activity.getAssets(), Globals.font_bold);
        ((AppCompatEditText)view.findViewById(R.id.etFeedbackComment)).setTypeface(tf);
        ((TextView)view.findViewById(R.id.textFeedback)).setTypeface(tfBold);


        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) view.findViewById(R.id.ratingBar);
        LayerDrawable layerDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        layerDrawable.getDrawable(2).setColorFilter(getResources().getColor(R.color.orange_red), PorterDuff.Mode.SRC_ATOP);

        setCancelable(false);

        final AppCompatEditText etComment = (AppCompatEditText) view.findViewById(R.id.etFeedbackComment);
        etComment.setText(comment);

        builder.setView(view)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        FeedbackHelper fbHelper = new FeedbackHelper(FeedbackDialog.this);
                        fbHelper.execute(sessionId, ""+ratingBar.getRating(), etComment.getText().toString());
                        //callback.onFeedbackDone( ""+ratingBar.getRating(), etComment.getText().toString());
                    }
                });
        return builder.create();
    }

    public void setComment(String comment){
        this.comment = comment;
    }
    public void setSessionId(String sessionId){ this.sessionId = sessionId; }

    @Override
    public void onFeedbackSent() {
        Toast.makeText(activity, "Feedback Sent", Toast.LENGTH_SHORT).show();
        callback.onFeedbackDone();
    }


    public interface FeedbackCallback{
        void onFeedbackDone();
    }
}

