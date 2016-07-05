package com.fantasticfive.shareback.alertDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.R;

/**
 * Created by Sagar on 14-04-2016.
 */
public class FeedbackAlert extends DialogFragment {

    Callback callback;
    Activity activity;
    private String comment="";

    @Override
    public void onAttach(Activity activity) {
        callback  = (Callback) activity;
        this.activity = activity;
        super.onAttach(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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
                        callback.onFeedbackDone( ""+ratingBar.getRating(), etComment.getText().toString());
                        Log.e("my Tag", "Here");
                    }
                });
        return builder.create();
    }

    public void setComment(String comment){
        this.comment = comment;
    }



    public interface Callback{
        public void onFeedbackDone(String ratings, String comment);
    }
}
