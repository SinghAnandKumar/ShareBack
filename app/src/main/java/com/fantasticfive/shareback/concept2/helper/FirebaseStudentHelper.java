package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.bean.ActiveUser;
import com.fantasticfive.shareback.concept2.bean.JoinedSession;
import com.fantasticfive.shareback.concept2.bean.Rating;
import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.util.FileUtils;
import com.fantasticfive.shareback.concept2.util.FirebaseKeys;
import com.fantasticfive.shareback.concept2.util.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sagar on 19/2/17.
 */
public class FirebaseStudentHelper {

    private static final String TAG = "MY TAG";
    String sessionId;
    String userId;
    String userName;
    Context context;
    Callback callback;
    FirebaseDatabase database;
    DatabaseReference rootRef;
    StorageReference storageReference;

    public FirebaseStudentHelper(Context context, String sessionId, Callback callback){
        this.sessionId = sessionId;
        this.userId = UserData.getInstance().getUserId();
        this.userName = UserData.getInstance().getName();
        this.context = context;
        this.callback = callback;
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference(sessionId);
    }

    public void register(final ActiveSession activeSession){

        Log.i(TAG, "register: trying "+userId);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        DatabaseReference joinRef = rootRef.child(FirebaseKeys.joinedUsers(activeSession));
        joinRef = joinRef.child(userId);
        joinRef.setValue(currentDateTimeString, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    databaseError.toException().printStackTrace();
                else
                    Log.i(TAG, "onComplete: Successfully registered");
            }
        });

        DatabaseReference joinedSessionRef = rootRef.child(FirebaseKeys.userSessions());
        joinedSessionRef = joinedSessionRef.child(sessionId);
        joinedSessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null)
                    joinEntry(activeSession);
                else {
                    JoinedSession session = dataSnapshot.getValue(JoinedSession.class);
                    callback.onSessionJoined(session);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateJoinEntry(final JoinedSession joinedSession){
        DatabaseReference joinedSessionRef = rootRef.child(FirebaseKeys.userSessions());
        joinedSessionRef = joinedSessionRef.child(sessionId);
        joinedSessionRef.setValue(joinedSession, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    databaseError.toException().printStackTrace();
                else{
                    callback.onSessionJoined(joinedSession);
                }
            }
        });
    }

    private void joinEntry(ActiveSession activeSession){
        //Make Entry in JoinedSession
        final JoinedSession joinedSession = new JoinedSession();
        joinedSession.setInstructorId(activeSession.getInstructorId());
        joinedSession.setSessionName(activeSession.getSessionName());
        joinedSession.setSessionId(activeSession.getSessionId());
        joinedSession.setInstructorName(activeSession.getInstructorName());

        DatabaseReference joinedSessionRef = rootRef.child(FirebaseKeys.userSessions());
        joinedSessionRef = joinedSessionRef.child(sessionId);
        joinedSessionRef.setValue(joinedSession, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    databaseError.toException().printStackTrace();
                else{
                    callback.onSessionJoined(joinedSession);
                }
            }
        });
    }

    public void active(ActiveSession activeSession){
        Log.i(TAG, "active: Adding Value "+userId);
        ActiveUser user = new ActiveUser();
        user.setName(userName);
        user.setUserId(userId);

        DatabaseReference activeRef = rootRef.child(FirebaseKeys.activeUser(sessionId));
        activeRef.child(userId).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    databaseError.toException().printStackTrace();
                else
                    Log.i(TAG, "onComplete: Successfully Activated in Firebase");
            }
        });

        String key = FirebaseKeys.sessionFiles(activeSession);
        DatabaseReference sessionFileRef = rootRef.child(key);
        sessionFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SharedFile> sharedFiles = new ArrayList<>();
                Log.i(TAG, "onDataChange: Documents Changed Size");
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    SharedFile sharedFile = postSnapshot.getValue(SharedFile.class);
                    sharedFiles.add(sharedFile);
                }
                callback.onDocumentsChanged(sharedFiles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(databaseError !=null)
                    databaseError.toException().printStackTrace();
            }
        });
    }

    public void inactive(){
        Log.i(TAG, "inactive: Removing value "+userId);
        DatabaseReference activeRef = rootRef.child(FirebaseKeys.activeUser(sessionId));
        activeRef.child(userId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    databaseError.toException().printStackTrace();
                else
                    Log.i(TAG, "onComplete: Successfully Unactivated in Firebase");
            }
        });
    }

    public void sendComments(ActiveSession joinedSession, int rating, String comment){
        DatabaseReference ratingRef = rootRef.child(FirebaseKeys.rating(joinedSession, rating));
        updateRating(ratingRef, rating);

        if(comment!=null && !comment.trim().isEmpty()) {
            DatabaseReference commentRef = rootRef.child(FirebaseKeys.comment(joinedSession));
            commentRef.push().setValue(comment, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null) {
                        Log.e(TAG, "onComplete: "+databaseError.getMessage());
                        databaseError.toException().printStackTrace();
                    }
                    else
                        Log.i(TAG, "onComplete: Comment Successfully Sent");
                }
            });
        }
    }

    private void updateRating(DatabaseReference ratingRef, final int rating){

        ratingRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Rating currRating = mutableData.getValue(Rating.class);
                switch (rating){
                    case 1: currRating.setRating1(currRating.getRating1()+1); break;
                    case 2: currRating.setRating2(currRating.getRating2()+1); break;
                    case 3: currRating.setRating3(currRating.getRating3()+1); break;
                    case 4: currRating.setRating4(currRating.getRating4()+1); break;
                    case 5: currRating.setRating5(currRating.getRating5()+1); break;
                }
                mutableData.setValue(currRating);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError!=null){
                    Log.e(TAG, "onComplete: "+databaseError.getMessage() );
                    databaseError.toException().printStackTrace();
                }
                else {
                    Log.e(TAG, "onComplete: Success"+ dataSnapshot.getValue() );
                }
            }
        });
    }

    public interface Callback{
        void onDocumentsChanged(ArrayList<SharedFile> sharedFiles);
        void onSessionJoined(JoinedSession session);
    }

}
