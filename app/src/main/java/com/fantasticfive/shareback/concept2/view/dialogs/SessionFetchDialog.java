package com.fantasticfive.shareback.concept2.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.activity.RunningSessionsActivity;
import com.fantasticfive.shareback.concept2.helper.SessionFetchHelper;
import com.fantasticfive.shareback.concept2.bean.Session;
import com.fantasticfive.shareback.concept2.exception.NoInternetException;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * Created by sagar on 28/7/16.
 */
public class SessionFetchDialog extends DialogFragment implements SessionFetchHelper.Callback{

    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.c2_dialog_session_fetch);

        SessionFetchHelper adapter = new SessionFetchHelper(activity, this);
        try {
            adapter.fetch();
        } catch (NoInternetException e) {
            showNoInternetAlert();
        }
        return builder.create();
    }

    private void showNoInternetAlert(){
        Toast.makeText(activity, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSessionFetched(ArrayList<Session> sessions) {
        String strSessions = new Gson().toJson(sessions);
        dismiss();
        Intent intent = new Intent(activity, RunningSessionsActivity.class);
        intent.putExtra(Constants.SESSION_LIST, strSessions);
        startActivity(intent);
    }

    @Override
    public void onSessionFetchFailed(Exception e) {
        dismiss();
        Toast.makeText(activity, "Can't Fetch: "+e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}

