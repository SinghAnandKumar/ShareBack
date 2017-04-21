package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.util.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by sagar on 4/3/17.
 */
public class FirebaseDownloadHelper {
    Context context;
    Callback callback;
    StorageReference storageReference;
    FirebaseStorage storage;
    public FirebaseDownloadHelper(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public void download(final SharedFile sharedFile, final View view){

        StorageReference ref = storageReference.child(sharedFile.getPath());
        //Toast.makeText(context, "Downloading From: "+sharedFile.getPath(), Toast.LENGTH_SHORT).show();
        File file = new File(FileUtils.getSharebackDir(sharedFile.getName()));
        ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                callback.onComplete(view);
                //Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(sharedFile, view, e);
                Toast.makeText(context, "Failed to Download File", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                long total = taskSnapshot.getTotalByteCount();
                long dwn = taskSnapshot.getBytesTransferred();
                double pct = (100d*dwn)/total;
                //Toast.makeText(context, "Downloaded: "+pct, Toast.LENGTH_SHORT).show();
                callback.ondProgressUpdate(pct, view);
            }
        });
    }

    public interface Callback{
        void ondProgressUpdate(double downloadPC, View view);
        void onComplete(View view);
        void onFailure(SharedFile file, View view, Exception e);
    }
}
