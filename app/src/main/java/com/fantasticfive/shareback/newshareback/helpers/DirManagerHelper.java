package com.fantasticfive.shareback.newshareback.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
        String newDir = currDir + dir + "/";;
        lister = new DirPhysical(context, this);
        lister.execute(newDir);
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
        else{
            Toast.makeText(context, "In root folder", Toast.LENGTH_SHORT).show();
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

    public interface Callback {
        void onListReceive(DirContentsBean bean);
    }
}

