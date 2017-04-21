package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.util.FirebaseKeys;
import com.fantasticfive.shareback.concept2.util.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sagar on 19/2/17.
 */
public class FirebaseInstructorHelper {

    private static final String TAG = "MY TAG";
    Context context;
    FirebaseDatabase database;
    FirebaseStorage storage;

    DatabaseReference dbRootRef;
    StorageReference storageRef;

    HashMap<String, UploadTask> taskMap;
    String sessionId;
    Callback callback;

    public FirebaseInstructorHelper(Context context, String sessionId, Callback callback){
        this.context = context;
        this.sessionId = sessionId;
        this.callback = callback;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dbRootRef = database.getReference();
        storageRef = storage.getReference(sessionId);

        taskMap = new HashMap<>();
    }

    public void removeSessionEntry(){
        DatabaseReference ref = dbRootRef.child(FirebaseKeys.activeSessions());
        ref.child(sessionId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(context, "Session Closed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void insertInSharedFile(SharedFile sharedFile){
        String instructorId = UserData.getInstance().getUserId();
        ActiveSession activeSession = new ActiveSession();
        activeSession.setInstructorId(instructorId);
        activeSession.setSessionId(sessionId);

        DatabaseReference ref = dbRootRef.child(FirebaseKeys.sessionFiles(activeSession));
        ref.push().setValue(sharedFile);
    }

    public void listenForDocChange(){
        String userId = UserData.getInstance().getUserId();
        String key = FirebaseKeys.sessionFiles(userId, sessionId);
        DatabaseReference sessionFileRef = dbRootRef.child(key);
        sessionFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SharedFile> sharedFiles = new ArrayList<>();
                Log.i(TAG, "onDataChange: Documents Changed Size");
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    SharedFile sharedFile = postSnapshot.getValue(SharedFile.class);
                    sharedFiles.add(sharedFile);
                }
                callback.onDocChange(sharedFiles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(databaseError !=null)
                    databaseError.toException().printStackTrace();
            }
        });
    }

    public interface Callback{
        void onDocChange(ArrayList<SharedFile> sharedFiles);
    }

}
