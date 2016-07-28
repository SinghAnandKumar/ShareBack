package com.fantasticfive.shareback.newshareback.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by sagar on 28/7/16.
 */
public class SessionCloseDialog extends DialogFragment {

    SessionCloseCallback callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Close Session?")
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
        callback = (SessionCloseCallback) activity;
        super.onAttach(activity);
    }

    public interface SessionCloseCallback{
        void onPositiveClick();
        void onNegativeClick();
    }
}

