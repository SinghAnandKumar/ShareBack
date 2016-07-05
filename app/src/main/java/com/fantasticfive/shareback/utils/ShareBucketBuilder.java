package com.fantasticfive.shareback.utils;

import android.os.Environment;

import com.fantasticfive.shareback.ShareBucket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Created by Sagar on 09-03-2016.
 */
public class ShareBucketBuilder {
    private static File file = null;
    public static boolean build(File f) throws IOException {
        file = f;

        /*BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)), 1024*20);
        int count = 0;
        String line = "";

        while((line = br.readLine())!=null){
            String str="";
            for(int i=0; i<count && ((str=br.readLine())!=null) && count<6; i++){
                line+=str;
            }
            ShareBucket.put(line.toString());
            line = "";
            count++;
        }

        //Test
        Scanner sc = new Scanner(f);
        while(sc.hasNext()){
            ShareBucket.put(sc.nextLine());
        }
        //-- Test
        */
        return true;
    }

    public static File getFile() throws IOException {
        File f = new File(Environment.getExternalStorageDirectory()+"/ShareBack");
        if(!f.canRead())
            f.mkdir();
        f = new File(Environment.getExternalStorageDirectory()+"/ShareBack/temp.pdf");
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        for(int i=0 ; i<ShareBucket.size(); i++){
            osw.write(ShareBucket.get(i));
        }
        osw.close();
        fos.close();

        return f;
    }

    public static File getFileToSend(){
        return file;
    }

    public static File getRecievedFile(){
        return new File(Environment.getExternalStorageDirectory()+"/ShareBack/temp.pdf");
    }
}