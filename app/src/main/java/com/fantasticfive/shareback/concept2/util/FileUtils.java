package com.fantasticfive.shareback.concept2.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

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

    public static boolean exists(String filename){
        String path = getSharebackDir(filename);
        File file = new File(path);
        return file.exists();
    }

    public static void openFile(Activity activity, String filename){
        String path = getSharebackDir(filename);
        File file = new File(path);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        String s[] = file.getName().split("\\.");
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(s[s.length-1]);
        Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);

        if (uri != null) {
            // Grant temporary read permission to the content URI
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setDataAndType(uri, activity.getContentResolver().getType(uri));
            //intent.setDataAndType(uri, mime);

            activity.setResult(Activity.RESULT_OK, intent);
            activity.startActivity(intent);
        }
        else {
            intent.setDataAndType(null, "");
            activity.setResult(Activity.RESULT_CANCELED,
                    intent);
        }

    }
}
