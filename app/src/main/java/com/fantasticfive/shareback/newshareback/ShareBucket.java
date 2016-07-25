package com.fantasticfive.shareback.newshareback;

import android.view.View;

import com.fantasticfive.shareback.beans.BucketItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by sagar on 20/7/16.
 */
public class ShareBucket {

    String currentFile = "";
    int DEFAULT_PAGE = 1;
//    ShareBucketCallback callback = null;

    //Constructor for Instructor
    public ShareBucket(){

    }
    //-- Constructor for Instructor

   /* //Constructor for Student
    public ShareBucket(ShareBucketCallback callback){
        this.callback = callback;
    }
    //-- Constructor for Student*/

    LinkedHashMap<String, BucketItem> openedFileSet = new LinkedHashMap<>();

    public void add(String file, BucketItem item){
        openedFileSet.put(file, item);

        /*if(isStudent()){
            callback.
        }*/
    }

    public void popFile(String file) { openedFileSet.remove(file); }

    public void updatePageNo(String file, int pageNo){
        BucketItem item  = openedFileSet.get(file);
        item.setPageNo(pageNo);
        openedFileSet.put(file, item);

        /*if(isStudent())
            callback.onPageChanged(file, pageNo);*/
    }

    public LinkedHashMap<String, Integer> getOpenedFileSet(){
        LinkedHashMap<String, Integer> set = new LinkedHashMap<String, Integer>();
        for(String filePath : openedFileSet.keySet()){
            BucketItem item = openedFileSet.get(filePath);
            set.put(filePath, item.getPageNo());
        }
        return set;
    }

    public Collection<String> getFiles(){
        return openedFileSet.keySet();
    }

    public Collection<Integer> getPageNos(){
        Collection<Integer> pageNos = new ArrayList<>();
        Collection<String> files = openedFileSet.keySet();
        for(String s : files){
            BucketItem item = openedFileSet.get(s);
            pageNos.add(item.getPageNo());
        }
        return pageNos;
    }

    public void setCurrentFile(String filePath, int pageNo){
        currentFile = filePath;
        updatePageNo(filePath, pageNo);
    }

    public void setCurrentFile(String filePath){
        currentFile = filePath;
    }

    public int getCurrFilePage(){
        int a = openedFileSet.get(currentFile).getPageNo();
        return  a;
    }

    public String getCurrentFile(){
        return currentFile;
    }

    public void copyFromJson(JSONObject main){
        //Decode JSON and create ShareBucket
        try {
            JSONArray arrFiles = main.getJSONArray(Constants.JSON_FILES);
            JSONArray arrPageNos = main.getJSONArray(Constants.JSON_PAGE_NOS);
            String currentFile = main.getString(Constants.JSON_CURR_FILE);

            init();

            for(int i=0; i<arrFiles.length(); i++){
                updatePageNo(arrFiles.getString(i) , arrPageNos.getInt(i) );
            }
            setCurrentFile(currentFile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //-- Decode JSON and create ShareBucket


        /*
        *
        * Remaining Code:-
         * Bucket created callback
        *
         */
    }

    public View getView(String filePath){
        return openedFileSet.get(filePath).getView();
    }

    public boolean contains(String filePath){
        return openedFileSet.containsKey(filePath);
    }

    public void setDownloadFlag( String filePath){
        openedFileSet.get(filePath).setDownloadFlag(true);
    }

    public void deleteData(){
        File f = new File(Constants.DIR_ROOT);
        deleteRecursive(f);
    }

    private void deleteRecursive(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteRecursive(f);
            }
        }
        file.delete();
    }

    private void init(){
        openedFileSet.clear();
    }

    /*private boolean isStudent(){
        return ((callback != null) ? true : false);
    }*/
    /*public void testCode(){
        add("/folder/fist.pdf");
        add("/folder/tick.pdf");
        add("/folder/tock.pdf");
        add("/folder/clock.pdf");
        setCurrentFile("/folder/tock.pdf");
    }*/

    /*public interface ShareBucketCallback{
        void onPageChanged(String filePath, int pageNo);
        void onFileChanged(String filePath, int pageNo);
        void onFileAdded(String filePath);

    }*/
}
