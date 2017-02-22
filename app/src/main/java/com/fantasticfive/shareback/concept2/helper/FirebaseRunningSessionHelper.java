package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.util.Log;

import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.util.FirebaseKeys;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by sagar on 20/2/17.
 */
public class FirebaseRunningSessionHelper {

    private static final String TAG = "MY TAG";
    Context context;
    Callback callback;
    FirebaseDatabase database;
    DatabaseReference rootRef;
    public FirebaseRunningSessionHelper(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
    }

    public void readSessions(){
        Log.i(TAG, "readSessions: Reading Files from Firebase");
        DatabaseReference ref = rootRef.child(FirebaseKeys.activeSessions());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<ActiveSession> sessions = new ArrayList<>();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    ActiveSession aSession = postSnapshot.getValue(ActiveSession.class);
                    sessions.add(aSession);
                }
                callback.onSessionChange(sessions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface Callback{
        void onSessionChange(ArrayList<ActiveSession> al);
    }
}
