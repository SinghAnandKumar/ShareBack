package com.fantasticfive.shareback.newshareback.helpers;

import android.content.Context;
import android.util.Log;

import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.physical.DirPhysical;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by sagar on 13/8/16.
 */
public class DirManagerHelper
        implements DirPhysical.Callback {

    String currDir = "";
    DirPhysical lister = null;

    Context context;
    Callback callback;

    //Constructor for Instructor
    public DirManagerHelper(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }
    //-- Constructor for Instructor

    public void getItemList(String dir) {
        currDir = currDir + dir + "/";
        lister = new DirPhysical(context, this);
        lister.execute(currDir);
        Log.e("My Tag", "Getting Item List");
    }

    public void refresh() {
        lister = new DirPhysical(context, this);
        lister.execute(currDir);
    }

    public String getCurrentDir() {
        return currDir;
    }

    public void getParentList() {
        String parent = (new File(currDir)).getParent();
        if (parent != null) {
            currDir = parent + "/";
            lister = new DirPhysical(context, this);
            lister.execute(currDir);
        }
    }

    @Override
    public void onListReceive() {
        DirContentsBean bean;
        try {
            bean = lister.get();
            callback.onListReceive(bean);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void onListReceive(DirContentsBean bean);
    }
}

