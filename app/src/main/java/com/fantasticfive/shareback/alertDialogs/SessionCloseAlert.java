package com.fantasticfive.shareback.alertDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.fantasticfive.shareback.R;

/**
 * Created by Sagar on 24-03-2016.
 */
public class SessionCloseAlert extends DialogFragment {

    Callback callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Close CreatedSession?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.onPositiveClick();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.onNegativeClick();
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        callback = (Callback) activity;
        super.onAttach(activity);
    }

    public interface Callback{
        public void onPositiveClick();
        public void onNegativeClick();
    }
}
