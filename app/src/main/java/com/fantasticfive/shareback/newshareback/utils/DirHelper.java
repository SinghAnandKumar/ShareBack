package com.fantasticfive.shareback.newshareback.utils;

import android.content.Context;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by sagar on 14/7/16.
 */
public class DirHelper implements DirPhysical.Callback, FileReceiver.Callback{

    String currDir = "";
    DirPhysical lister = null;

    Context context;
    Callback callback;
    FileDwnldCallback fdCallback;
    LinkedHashMap<String, Boolean> downloadQueue = new LinkedHashMap<>();

    //Constructor for Instructor
    public DirHelper(Context context, Callback callback, FileDwnldCallback fdCallback){
        this.context = context;
        this.callback = callback;
        this.fdCallback = fdCallback;
    }
    //-- Constructor for Instructor

    //Constructor for Student
    public DirHelper(Context context, FileDwnldCallback fdCallback){
        this.context = context;
        this.fdCallback = fdCallback;
    }
    //-- Constructor for Student



    public void getItemList(String dir){
        currDir = currDir + dir + "/";
        lister = new DirPhysical(context, this);
        lister.execute(currDir);
        Log.e("My Tag", "Getting Item List");
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

    public void getParentList(){
        currDir = (new File(currDir)).getParent() + "/";
        lister = new DirPhysical(context, this);
        lister.execute(currDir);
    }

    @Override
    public void onListReceive() {
        DirContentsBean bean = null;
        try {
            bean = lister.get();
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
