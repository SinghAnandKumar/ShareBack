package com.fantasticfive.shareback.newshareback.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.activities.FileViewInstructor;
import com.fantasticfive.shareback.newshareback.helpers.SessionHelper;

import java.util.concurrent.ExecutionException;

/**
 * Created by sagar on 28/7/16.
 */
public class SessionInfoDialog extends DialogFragment implements SessionHelper.SessionHelperCallback{

    Activity activity;
    SessionHelper sessionHelper;

    ProgressBar progressBar;
    TextView progressText;
    AppCompatButton btnCreate;
    AppCompatEditText et;

    String sessionName = "";
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

        et = (AppCompatEditText) view.findViewById(R.id.etSessionName);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressText = (TextView) view.findViewById(R.id.progress_text);
        btnCreate = (AppCompatButton) view.findViewById(R.id.btnCreate);

        builder.setView(view);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et.getText().toString();
                if(s.contains(".")){
                    et.setError("Invalid dot(.) character");
                }
                else if(!s.trim().equals("")) {
                    sessionHelper = new SessionHelper(SessionInfoDialog.this);
                    sessionHelper.execute(s);

                    sessionName = s;

                    et.clearFocus();
                    showProgressBar(true);
                }
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(sessionHelper!=null) {
                    sessionHelper.cancel(true);
                    showProgressBar(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return builder.create();
    }

    @Override
    public void onSessionIdReceived() {
        try {
            showProgressBar(false);
            String sessionId = sessionHelper.get();
            if(sessionId != null && !sessionHelper.isCancelled()) {
                Intent intent = new Intent(activity, FileViewInstructor.class);
                intent.putExtra(Constants.KEY_SESSION_ID, sessionId);
                intent.putExtra(Constants.KEY_SESSION_NAME, sessionName);
                Toast.makeText(activity, "CreatedSession Id:" + sessionId, Toast.LENGTH_SHORT).show();
                dismiss();
                activity.startActivity(intent);
            }
            else{
                Toast.makeText(activity, "Can't Connect to Server", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar(final boolean show) {
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            progressText.setText(R.string.text_wait);
            progressText.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            progressText.setText(R.string.text_stopped);
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isVisible() && progressText.getText().equals(getResources().getString(R.string.text_stopped))){
                        progressText.setVisibility(View.INVISIBLE);
                    }
                }
            }, 1000);
        }
    }
}

