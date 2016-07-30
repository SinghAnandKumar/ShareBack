package com.fantasticfive.shareback.newshareback.helpers;

import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.physical.FileOperationPhysical;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sagar on 30/7/16.
 */
public class FileOperationHelper
    implements FileOperationPhysical.Callback{

    FileOperationPhysical fileOperationPhysical = null;
    Callback callback;

    public FileOperationHelper(Callback callback) {
        this.callback = callback;
    }

    public void mkDir(String filePath){
        fileOperationPhysical = new FileOperationPhysical(this);
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_MKDIR);
            main.put(Constants.JSON_FO_DIR_PATH,filePath);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void del(String filePath){ //replace String with arraylist
        fileOperationPhysical = new FileOperationPhysical(this);
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_DELETE);
            main.put(Constants.JSON_FO_OLD_FILE,filePath);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void copy(String oldPath, String newPath){
        fileOperationPhysical = new FileOperationPhysical(this);

        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_COPY);
            main.put(Constants.JSON_FO_OLD_FILE,oldPath);
            main.put(Constants.JSON_FO_NEW_FILE,newPath);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void move(String oldPath, String newPath){
        fileOperationPhysical = new FileOperationPhysical(this);
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_MOVE);
            main.put(Constants.JSON_FO_OLD_FILE,oldPath);
            main.put(Constants.JSON_FO_NEW_FILE,newPath);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void rename(String oldPath, String newPath){
        fileOperationPhysical = new FileOperationPhysical(this);
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_RENAME);
            main.put(Constants.JSON_FO_OLD_FILE,oldPath);
            main.put(Constants.JSON_FO_NEW_FILE,newPath);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOperationPerformed() {
        Log.e("My Tag","File operation done");
        callback.onOperationPerformed();
    }

    public interface Callback{
        void onOperationPerformed();
    }
}
