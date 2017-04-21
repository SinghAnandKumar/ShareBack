package com.fantasticfive.shareback.concept2.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.concept2.exception.NotInitializedException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by sagar on 19/2/17.
 */
public class UserData implements GoogleApiClient.ConnectionCallbacks{

    private static final String TAG = "MY TAG";
    private static FirebaseUser acc = null;
    private static GoogleApiClient googleApiClient;

    private static UserData userData = null;
    //Default Constructor
    private UserData(FirebaseUser acc, GoogleApiClient googleApiClient){
        this.acc = acc;
        this.googleApiClient = googleApiClient;
        googleApiClient.registerConnectionCallbacks(this);
    }

    public static UserData getInstance(FirebaseUser acc, GoogleApiClient googleApiClient){
        if(userData==null){
            userData = new UserData(acc, googleApiClient);
        }
        return userData;
    }

    public static UserData getInstance(){
        if(userData == null){
            try {
                throw new NotInitializedException();
            } catch (NotInitializedException e) {
                e.printStackTrace();
                Log.e(TAG, "getInstance: UserData Not Initialized");
            }
        }
        return userData;
    }

    public String getUserId(){
        return acc.getUid();
    }

    public String getName(){
        return acc.getDisplayName();
    }

    public boolean isMe(String userId){
        return getUserId().equals(userId);
    }

    public void removeInstance(){

        if (googleApiClient.isConnected())
            disconnectGoogleApiClient();
        else
            googleApiClient.connect();

        userData = null;
    }

    private void disconnectGoogleApiClient(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if(status.isSuccess())
                            Log.e(TAG, "onResult: Signed Out Successfully" );
                        else
                            Log.e(TAG, "onResult: Error in Signing Out" );
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        disconnectGoogleApiClient();
        Log.e(TAG, "onConnected: GoogleApiClient Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended: GoogleApiClient Suspended");
    }
}
