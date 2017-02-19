package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.concept2.util.FirebaseConstants;
import com.fantasticfive.shareback.concept2.util.UserData;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by sagar on 19/2/17.
 */
public class FirebaseStudentHelper {

    private static final String TAG = "MY TAG";
    String sessionId;
    String userId;
    Context context;
    FirebaseDatabase database;
    DatabaseReference rootRef;
    DatabaseReference activeRef;
    DatabaseReference joinRef;

    public FirebaseStudentHelper(Context context, String sessionId){
        this.sessionId = sessionId;
        this.userId = UserData.getUserId();
        this.context = context;
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference(sessionId);
    }

    public void register(){
        Log.i(TAG, "register: trying "+userId);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        joinRef = rootRef.child(FirebaseConstants.JOINED_USERS).child(userId);
        joinRef.setValue(currentDateTimeString, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    Toast.makeText(context, "Join Registration Failed: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void active(){
        Log.i(TAG, "active: Adding Value "+userId);
        activeRef = rootRef.child(FirebaseConstants.ACTIVE_USERS).push();
        activeRef.setValue(userId, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    Toast.makeText(context, "Active Registration Failed: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void inactive(){
        Log.i(TAG, "inactive: Removing value "+userId);
        activeRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    Toast.makeText(context, "Unregister Failed: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
