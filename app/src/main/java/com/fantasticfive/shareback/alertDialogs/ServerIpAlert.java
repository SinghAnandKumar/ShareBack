package com.fantasticfive.shareback.alertDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fantasticfive.shareback.R;

/**
 * Created by Sagar on 26-03-2016.
 */
public class ServerIpAlert extends DialogFragment {

    Callback callback;

    @Override
    public void onAttach(Activity activity) {
        callback  = (Callback) activity;
        super.onAttach(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ip, null);
        final AppCompatEditText et = (AppCompatEditText) view.findViewById(R.id.etServerIp);
        builder.setView(view)
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        callback.onJoin(et.getText().toString());
                    }
                });
        return builder.create();
    }



    public interface Callback{
        public void onJoin(String serverIp);
    }
}
