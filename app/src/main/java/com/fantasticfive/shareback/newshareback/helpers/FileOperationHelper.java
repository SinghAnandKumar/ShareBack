package com.fantasticfive.shareback.newshareback.helpers;

import android.util.Log;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.physical.FileOperationPhysical;

import org.json.JSONArray;
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

    private String operation;

    public FileOperationHelper(Callback callback) {
        this.callback = callback;
    }

    public void mkDir(String filePath){
        fileOperationPhysical = new FileOperationPhysical(this, Constants.FO_MKDIR);
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_MKDIR);
            main.put(Constants.JSON_FO_DIR_PATH,filePath);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void del(ArrayList<String> filePath){
        fileOperationPhysical = new FileOperationPhysical(this, Constants.FO_DELETE);
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_DELETE);
            JSONArray arr = new JSONArray(filePath);
            main.put(Constants.JSON_FILES,arr);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void copy(String destPath, ArrayList<String> filePaths){
        fileOperationPhysical = new FileOperationPhysical(this, Constants.FO_COPY);

        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_COPY);
            main.put(Constants.JSON_FO_DEST_PATH, destPath);
            JSONArray arr = new JSONArray(filePaths);
            main.put(Constants.JSON_FILES, arr);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void move(String destPath, ArrayList<String> filePaths){
        fileOperationPhysical = new FileOperationPhysical(this, Constants.FO_MOVE);
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FO_OPERATION,Constants.FO_MOVE);
            main.put(Constants.JSON_FO_DEST_PATH,destPath);
            JSONArray arr = new JSONArray(filePaths);
            main.put(Constants.JSON_FILES, arr);
            fileOperationPhysical.execute(main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void rename(String oldPath, String newPath){

        Log.e("My Tag", "Renaming "+oldPath+" to "+newPath);
        fileOperationPhysical = new FileOperationPhysical(this, Constants.FO_RENAME);
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
    public void onOperationPerformed(String operation, boolean success) {
        Log.e("My Tag","File operation done");
        callback.onOperationPerformed(operation, success);
    }

    public interface Callback{
        void onOperationPerformed(String operation, Boolean success);
    }
}
