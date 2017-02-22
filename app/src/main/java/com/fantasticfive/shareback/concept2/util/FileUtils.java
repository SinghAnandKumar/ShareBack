package com.fantasticfive.shareback.concept2.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by sagar on 22/2/17.
 */
public class FileUtils {

    private final static String SHAREBACK_DIR = "Shareback";

    public static void createDir(){
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/" +SHAREBACK_DIR);
        if(!dir.exists())
            dir.mkdir();
    }

    public static String getSharebackDir(String filename){
        createDir();
        return Environment.getExternalStorageDirectory().toString() +
                "/" + SHAREBACK_DIR +
                "/" + filename;
    }
}
