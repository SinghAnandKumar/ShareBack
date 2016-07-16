package com.fantasticfive.shareback.newshareback.utils;

import android.content.Context;
import android.widget.ListAdapter;

import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.utils.DirLister;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by sagar on 14/7/16.
 */
public class DirTracker implements DirLister.Callback, FileReceiver.Callback{

    String currDir = "";
    DirLister lister = null;

    Context context;
    Callback callback;
    public DirTracker(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    public void getItemList(String dir){
        currDir += currDir + dir + "/";
        lister = new DirLister(context, this);
        lister.execute(currDir);
    }

    @Override
    public void onListReceive() {
        DirContentsBean bean = null;
        try {
            bean = lister.get();
            callback.onListReceive(bean);;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getFile(String fileName){
        FileReceiver receiver = new FileReceiver(context, this);
        receiver.execute(currDir + fileName);
    }

    @Override
    public void onFileReceive(String location) {
        callback.onFileReceive(new File(location));
    }

    public interface Callback{
        void onListReceive(DirContentsBean bean);
        void onFileReceive(File f);
    }
}
