package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.bean.ActiveUser;
import com.fantasticfive.shareback.concept2.bean.JoinedSession;
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
        this.userId = UserData.getUserId();
        this.userName = UserData.getName();
        this.context = context;
        this.callback = callback;
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference(sessionId);
    }

    public void register(ActiveSession activeSession){

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

        //Make Entry in JoinedSession
        JoinedSession joinedSession = new JoinedSession();
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
                else
                    Log.i(TAG, "onComplete: Joined Session Successfully Added in user List");
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

        DatabaseReference sessionFileRef = rootRef.child(FirebaseKeys.sessionFiles(activeSession));
        sessionFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SharedFile> sharedFiles = new ArrayList<>();
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

    public void download(SharedFile sharedFile){
        StorageReference ref = storageReference.child(sharedFile.getName());
        File file = new File(FileUtils.getSharebackDir(sharedFile.getName()));
        ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to Download File", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                long total = taskSnapshot.getTotalByteCount();
                long dwn = taskSnapshot.getBytesTransferred();
                double pct = (100d*dwn)/total;
                Toast.makeText(context, "Downloaded: "+pct, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public interface Callback{
        void onDocumentsChanged(ArrayList<SharedFile> sharedFiles);
    }

}
