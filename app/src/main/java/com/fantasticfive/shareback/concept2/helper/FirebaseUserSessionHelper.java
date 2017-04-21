package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;

import com.fantasticfive.shareback.concept2.bean.CreatedSession;
import com.fantasticfive.shareback.concept2.bean.JoinedSession;
import com.fantasticfive.shareback.concept2.bean.Session;
import com.fantasticfive.shareback.concept2.util.FirebaseKeys;
import com.fantasticfive.shareback.concept2.util.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by sagar on 22/2/17.
 */
public class FirebaseUserSessionHelper {

    FirebaseDatabase db;
    DatabaseReference rootRef;
    String userId;
    Callback callback;
    public FirebaseUserSessionHelper(Context context, Callback callback){
        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference();
        userId = UserData.getInstance().getUserId();
        this.callback = callback;
    }

    public void getSessions(){
        DatabaseReference ref = rootRef.child(FirebaseKeys.userSessions());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Session> sessions = new ArrayList<>();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    Session session = child.getValue(Session.class);
                    switch (session.getType()){
                        case Session.CREATED: session = child.getValue(CreatedSession.class); break;
                        case Session.JOINED: session = child.getValue(JoinedSession.class); break;
                    }
                    sessions.add(session);
                }
                callback.onSessionFetched(sessions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface Callback{
        void onSessionFetched(ArrayList<Session> sessions);
    }
}
