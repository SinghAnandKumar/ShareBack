package com.fantasticfive.shareback.concept2.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.activity.SessionViewStudent;
import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.helper.FirebaseRunningSessionHelper;
import com.fantasticfive.shareback.concept2.util.UserData;
import com.fantasticfive.shareback.concept2.view.adapters.RunningSessionsAdapter;
import com.google.gson.Gson;
import com.mingle.widget.LoadingView;

/**
 * Created by sagar on 23/2/17.
 */
import java.util.ArrayList;

public class ActiveSessionDialog extends DialogFragment implements FirebaseRunningSessionHelper.Callback{

    private static final String TAG = "MY TAG";
    FirebaseRunningSessionHelper helper;
    RunningSessionsAdapter adapter;
    ArrayList<ActiveSession> activeSessions;
    View loadingView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.c2_activity_session_list, null);
        ListViewCompat lv = (ListViewCompat) view.findViewById(R.id.lv);
        loadingView = view.findViewById(R.id.loading_view);

        builder.setView(view);
        builder.setTitle(getString(R.string.title_active_sessions));

        activeSessions = new ArrayList<>();
        adapter = new RunningSessionsAdapter(getActivity(), activeSessions);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemClick: Position:"+i);
                ActiveSession activeSession = activeSessions.get(i);
                if(activeSession.getInstructorId().equals(UserData.getUserId() )){
                    Toast.makeText(getActivity(), "You Started this Activity. You cannot Join", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), SessionViewStudent.class);

                String strActiveSession = new Gson().toJson(activeSession);
                intent.putExtra(Constants.ACTIVE_SESSION, strActiveSession);
                startActivity(intent);
                dismiss();
            }
        });

        helper = new FirebaseRunningSessionHelper(getActivity(), this);
        helper.readSessions();
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams .FLAG_FULLSCREEN);
        return dialog;
    }

    @Override
    public void onSessionChange(ArrayList<ActiveSession> al) {
        if(al.size()!=0){
            loadingView.setVisibility(View.GONE);
        }
        else{
            loadingView.setVisibility(View.VISIBLE);
        }
        activeSessions.clear();
        activeSessions.addAll(al);
        adapter.notifyDataSetChanged();
    }
}
