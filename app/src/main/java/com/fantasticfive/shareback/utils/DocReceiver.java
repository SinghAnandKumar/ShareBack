package com.fantasticfive.shareback.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.ShareBucket;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Sagar on 10-03-2016.
 */
public class DocReceiver extends AsyncTask {
    private int port = Globals.DOC_PORT;
    private String serverIp=null;
    Callback callback = null;

    public DocReceiver(String serverIp, Callback callback){
        Globals.serverIp = serverIp;
        this.serverIp = serverIp;
        this.callback = callback;
    }

    public final static int FILE_SIZE = 6022386;

    @Override
    protected Object doInBackground(Object[] params) {

        testClientSocket();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        callback.onDocReceive();
        super.onPostExecute(o);
    }

    private void testClientSocket(){
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket sock = null;
        try {
            sock = new Socket(serverIp, port);
            Log.e("MyTag", "Connecting...");

            // receive file
            byte [] mybytearray  = new byte [FILE_SIZE];
            InputStream is = sock.getInputStream();

            File f = new File(Environment.getExternalStorageDirectory()+"/ShareBack");
            if(!f.exists())
                f.mkdir();

            fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/ShareBack/temp.pdf");

            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(mybytearray, 0, current);
            bos.flush();
            Log.e("myTag","File temp.pdf downloaded (" + current + " bytes read)");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
                if (bos != null) bos.close();
                if (sock != null) sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Callback{
        public void onDocReceive();
    }
}