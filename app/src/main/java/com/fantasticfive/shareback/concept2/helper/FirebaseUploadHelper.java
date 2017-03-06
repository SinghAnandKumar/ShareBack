package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.fantasticfive.shareback.concept2.bean.UploadingFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by sagar on 5/3/17.
 */
public class FirebaseUploadHelper {
    Context context;
    Callback callback;
    StorageReference storageRef;

    public FirebaseUploadHelper(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void upload(UploadingFile uploadingFile, final View view){

        Uri uri = uploadingFile.getLocalUri();
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
                        callback.onUploadComplete(view, relativePath);

                       /* SharedFile sharedFile = new SharedFile();
                        sharedFile.setName( new File(relativePath).getName());
                        sharedFile.setPath(relativePath);
                        addSessionFileEntry(sharedFile);*/
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        long total = taskSnapshot.getTotalByteCount();
                        long transferred = taskSnapshot.getBytesTransferred();
                        double pcage = ((double)transferred/total)*100d;
                        Toast.makeText(context, "Transferred: "+pcage, Toast.LENGTH_SHORT).show();
                        callback.onUploadProgress(view, pcage);
                    }
                });

        //taskMap.put(uri.getPath(), task);
    }

    public interface Callback{
        void onUploadProgress(View view, double pcage);
        void onUploadComplete(View view, String downloadPath);
    }
}
