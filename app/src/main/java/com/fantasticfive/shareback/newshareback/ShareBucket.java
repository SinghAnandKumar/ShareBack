package com.fantasticfive.shareback.newshareback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by sagar on 20/7/16.
 */
public class ShareBucket {

    String currentFile = "";
    LinkedHashMap<String, Integer> openedFileSet = new LinkedHashMap<>();

    public void pushFile(String file){
        openedFileSet.put(file, 0);
    }

    public void updatePageNo(String file, int pageNo){
        openedFileSet.put(file, pageNo);
    }

    public LinkedHashMap<String, Integer> getOpenedFileSet(){
        return openedFileSet;
    }

    public Collection<String> getFiles(){
        return openedFileSet.keySet();
    }

    public Collection<Integer> getPageNos(){
        Collection<Integer> pageNos = new ArrayList<>();
        Collection<String> files = openedFileSet.keySet();
        for(String s : files){
            pageNos.add(openedFileSet.get(s));
        }
        return pageNos;
    }

    public void setCurrentFile(String filePath){
        currentFile = filePath;
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
    }

    private void init(){
        openedFileSet.clear();
    }
    public void testCode(){
        pushFile("/folder/fist.pdf");
        pushFile("/folder/tick.pdf");
        pushFile("/folder/tock.pdf");
        pushFile("/folder/clock.pdf");
        setCurrentFile("/folder/tock.pdf");
    }
}
