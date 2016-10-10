package com.fantasticfive.shareback.newshareback.helpers;

import android.content.Context;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.physical.DirPhysical;
import com.fantasticfive.shareback.newshareback.fileoperation.FileReceiver;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by sagar on 14/7/16.
 */
public class DirFileViewHelper implements DirPhysical.Callback, FileReceiver.Callback {

    String currDir = "";
    DirPhysical lister = null;

    Context context;
    Callback callback;
    FileDwnldCallback fdCallback;
    LinkedHashMap<String, Boolean> downloadQueue = new LinkedHashMap<>();

    //Constructor for Instructor
    public DirFileViewHelper(Context context, Callback callback, FileDwnldCallback fdCallback){
        this.context = context;
        this.callback = callback;
        this.fdCallback = fdCallback;
    }
    //-- Constructor for Instructor

    //Constructor for Student
    public DirFileViewHelper(Context context, FileDwnldCallback fdCallback){
        this.context = context;
        this.fdCallback = fdCallback;
    }
    //-- Constructor for Student

    //Constructor for Management Activity
    public DirFileViewHelper(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    //-- Constructor for Management Activity


    public void getItemList(String dir){
        String newDir= currDir + dir + "/";
        lister = new DirPhysical(context, this);
        lister.execute(newDir);
        Log.e("My Tag", "Getting Item List");
    }

    public void refresh(){
        lister = new DirPhysical(context, this);
        lister.execute(currDir);
    }

    private void getFile(String fileName){
        FileReceiver receiver = new FileReceiver(context, this);
        receiver.execute(fileName);
    }

    public void downloadFile(String fileName, boolean downloadFlag){

        String absoluteName = currDir +fileName;
        if(downloadFlag){
            downloadQueue.put(absoluteName, false);
            getFile(absoluteName);
        }
        else{
            //Remove From Download Queue
            downloadQueue.remove(absoluteName);
            //-- Remove From Download Queue
        }

        //Add to ShareBucket
        fdCallback.onFileAdded(absoluteName, downloadFlag);
        //-- Add to ShareBucket

    }

    public String getCurrentDir(){
        return currDir;
    }

    public void getParentList(){
        String parent = (new File(currDir)).getParent();
        if(parent!=null) {
            currDir = parent + "/";
            lister = new DirPhysical(context, this);
            lister.execute(currDir);
        }
    }

    @Override
    public void onListReceive(String newDir) {
        DirContentsBean bean;
        try {
            bean = lister.get();
            currDir = newDir;
            callback.onListReceive(bean);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileReceive(String relLocation) {
        downloadQueue.put(relLocation, true);
        fdCallback.onFileDownloaded(relLocation);
    }

    public interface Callback{
        void onListReceive(DirContentsBean bean);
    }

    public interface FileDwnldCallback{
        void onFileDownloaded(String relLocation);
        void onFileAdded(String fileName, boolean isSessionFile);
    }
}
