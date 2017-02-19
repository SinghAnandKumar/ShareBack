package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    public FirebaseInstructorHelper(Context context, String sessionId){
        this.context = context;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dbRef = database.getReference(sessionId);
        storageRef = storage.getReference(sessionId);

        taskMap = new HashMap<>();
    }

    public void uploadFile(Uri uri){
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
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long total = taskSnapshot.getTotalByteCount();
                long transferred = taskSnapshot.getBytesTransferred();
                double pcage = (100.0 * transferred)/total;
                Toast.makeText(context, "Transferred: "+pcage, Toast.LENGTH_SHORT).show();
            }
        });

        taskMap.put(uri.getPath(), task);
    }

}
