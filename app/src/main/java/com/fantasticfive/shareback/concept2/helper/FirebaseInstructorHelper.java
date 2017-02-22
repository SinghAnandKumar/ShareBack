package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fantasticfive.shareback.concept2.bean.ActiveSession;
import com.fantasticfive.shareback.concept2.bean.CreatedSession;
import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.util.FirebaseKeys;
import com.fantasticfive.shareback.concept2.util.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

/**
 * Created by sagar on 19/2/17.
 */
public class FirebaseInstructorHelper {

    Context context;
    FirebaseDatabase database;
    FirebaseStorage storage;

    DatabaseReference dbRef;
    StorageReference storageRef;

    HashMap<String, UploadTask> taskMap;
    String sessionId;

    public FirebaseInstructorHelper(Context context, String sessionId){
        this.context = context;
        this.sessionId = sessionId;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dbRef = database.getReference();
        storageRef = storage.getReference(sessionId);

        taskMap = new HashMap<>();
    }

    public void uploadThenShareFile(Uri uri){
        StorageReference fileRef = storageRef.child(uri.getLastPathSegment());
        UploadTask task = fileRef.putFile(uri);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to Upload: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(context, "Uploaded Successfully: "+downloadUrl, Toast.LENGTH_SHORT).show();

                String relativePath = downloadUrl.getLastPathSegment();
                SharedFile sharedFile = new SharedFile();
                sharedFile.setName( new File(relativePath).getName());
                sharedFile.setPath(relativePath);
                addSessionFileEntry(sharedFile);
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long total = taskSnapshot.getTotalByteCount();
                long transferred = taskSnapshot.getBytesTransferred();
                double pcage = ((double)transferred/total)*100d;
                Toast.makeText(context, "Transferred: "+pcage, Toast.LENGTH_SHORT).show();
            }
        });

        taskMap.put(uri.getPath(), task);
    }

    public void createSessionEntry(String sessionName){
        String instructorId = UserData.getUserId();
        //CreatedSession Entry
        CreatedSession createdSession = new CreatedSession();
        createdSession.setSessionId(sessionId);
        createdSession.setSessionName(sessionName);

        DatabaseReference ref = dbRef.child(FirebaseKeys.userSessions());
        ref.child(sessionId).setValue(createdSession);
        //-- CreatedSession Entry

        //Active CreatedSession Entry
        ActiveSession activeSession = new ActiveSession();
        activeSession.setInstructorId(instructorId);
        activeSession.setSessionName(sessionName);
        activeSession.setSessionId(sessionId);

        ref = dbRef.child(FirebaseKeys.activeSessions());
        ref.child(sessionId).setValue(activeSession);
        //-- Active CreatedSession Entry

    }

    public void removeSessionEntry(){
        DatabaseReference ref = dbRef.child(FirebaseKeys.activeSessions());
        ref.child(sessionId).removeValue();
    }

    public void addSessionFileEntry(SharedFile sharedFile){
        String instructorId = UserData.getUserId();
        ActiveSession activeSession = new ActiveSession();
        activeSession.setInstructorId(instructorId);
        activeSession.setSessionId(sessionId);

        DatabaseReference ref = dbRef.child(FirebaseKeys.sessionFiles(activeSession));
        ref.push().setValue(sharedFile);
    }

}
