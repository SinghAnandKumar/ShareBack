package com.fantasticfive.shareback.newshareback.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;

/**
 * Created by sagar on 24/7/16.
 */
public class DialogIp extends DialogFragment {

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

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ip, null);
        final AppCompatEditText et = (AppCompatEditText) view.findViewById(R.id.etServerIp);

        SharedPreferences pref = activity.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        String str = pref.getString(Constants.PREF_SERVER_IP, "192.168.0.2");
        et.setText(str);

        builder.setView(view)
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences pref = activity.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(Constants.PREF_SERVER_IP, et.getText().toString());
                        editor.commit();
                        Constants.IP_FILE_SERVER = et.getText().toString();
                    }
                });
        return builder.create();
    }
}
