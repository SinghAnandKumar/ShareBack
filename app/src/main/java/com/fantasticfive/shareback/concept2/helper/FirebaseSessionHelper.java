package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;

import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.bean.CreatedSession;
import com.fantasticfive.shareback.concept2.util.FirebaseKeys;
import com.fantasticfive.shareback.concept2.util.UserData;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sagar on 2/3/17.
 */
public class FirebaseSessionHelper {
    Context context;
    String sessionId;
    DatabaseReference dbRef;
    FirebaseDatabase database;
    Callback callback;
    public FirebaseSessionHelper(Context context, String sessionId, Callback callback){
        this.context = context;
        this.sessionId = sessionId;
        this.callback = callback;

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
    }

    boolean created=false, activated=false;
    public void createSessionEntry(final String sessionName){
        String instructorId = UserData.getInstance().getUserId();
        //CreatedSession Entry
        final CreatedSession createdSession = new CreatedSession();
        createdSession.setSessionId(sessionId);
        createdSession.setSessionName(sessionName);

        DatabaseReference ref = dbRef.child(FirebaseKeys.userSessions());
        ref.child(sessionId).setValue(createdSession, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                created = true;
                sendCallback(sessionName);
            }
        });
        //-- CreatedSession Entry

        //Active CreatedSession Entry
        ActiveSession activeSession = new ActiveSession();
        activeSession.setInstructorId(instructorId);
        activeSession.setInstructorName(UserData.getInstance().getName());
        activeSession.setSessionName(sessionName);
        activeSession.setSessionId(sessionId);

        ref = dbRef.child(FirebaseKeys.activeSessions());
        ref.child(sessionId).setValue(activeSession, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                activated = true;
                sendCallback(sessionName);
            }
        });
        //-- Active CreatedSession Entry

    }

    void sendCallback(String sessionName){
        if(created && activated){
            callback.onSessionEntrySuccess(sessionId, sessionName);
        }
    }

    public interface Callback{
        void onSessionEntrySuccess(String sessionId, String sessionName);
    }

}
