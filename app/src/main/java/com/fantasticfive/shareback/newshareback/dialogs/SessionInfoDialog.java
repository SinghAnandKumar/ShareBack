package com.fantasticfive.shareback.newshareback.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.activities.FileViewInstructor;
import com.fantasticfive.shareback.newshareback.utils.SessionHelper;

import java.util.concurrent.ExecutionException;

/**
 * Created by sagar on 28/7/16.
 */
public class SessionInfoDialog extends DialogFragment implements SessionHelper.SessionHelperCallback{

    Activity activity;
    SessionHelper sessionHelper;
    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_session_info, null);
        final AppCompatEditText et = (AppCompatEditText) view.findViewById(R.id.etSessionName);

        builder.setView(view)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String s = et.getText().toString();
                        if(!s.equals("")) {
                            sessionHelper = new SessionHelper(SessionInfoDialog.this);
                            sessionHelper.execute(s);
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onSessionIdReceived() {
        try {
            String sessionId = sessionHelper.get();
            Intent intent = new Intent(activity, FileViewInstructor.class);
            intent.putExtra(Constants.KEY_SESSION_ID, sessionId);
            Toast.makeText(activity, "Session Id:"+sessionId, Toast.LENGTH_SHORT).show();
            activity.startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

