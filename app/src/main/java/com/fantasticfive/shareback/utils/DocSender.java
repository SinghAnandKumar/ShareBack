package com.fantasticfive.shareback.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.ShareBucket;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sagar on 08-03-2016.
 */
public class DocSender extends AsyncTask<File, Void, String>{
    private int port = Globals.DOC_PORT;
    private Context context;
    private Callback callback;
    private ServerSocket servsock = null;
    public DocSender(Context context, Callback callback){
        this.context = context;
        this.callback = callback;

        //Clear Client IPs
        Globals.clientIps.clear();
        //-- Cliear Client IPs
    }

    @Override
    protected String doInBackground(File... params) {
        testServerSocket(params[0]);
        return null;
    }
    /*@Override
    protected Object doInBackground(Object[] params) {

        try {
            ServerSocket ss = new ServerSocket(port);
            Log.e("Success!!!", "Open Port "+port);

            Socket socket = ss.accept();
            OutputStream os = socket.getOutputStream();

            //Push Data to Client
            DataOutputStream dos = new DataOutputStream(os);

            dos.writeInt(ShareBucket.size());

            for(int i=0; i< ShareBucket.size(); i++){
                dos.writeUTF(ShareBucket.get(i));
            }

            //-- Push Data to Client

            //Wait For Event to occur
                //If event == terminate
                    //Get Input Steam
                    //Wait for Feedback from client
                    // Close Thread
                //Else
                    //Send Event to client
                    //Again wait for event
            //-- Wait For Event to occur

        } catch (IOException e) {
            Log.e("Socket Error", e.getMessage());
            //callback.open(false, port);
            e.printStackTrace();
        }
        //testServerSocket();
        return null;
    }*/

    void testServerSocket(File file){
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        Socket sock = null;
        File myFile = file;
        try {
            servsock = new ServerSocket(port);

            while (true) {
                System.out.println("Waiting...");
                try {
                    sock = servsock.accept();
                    
                    //Save Client Address
                    InetSocketAddress isa = (InetSocketAddress) sock.getRemoteSocketAddress();
                    Globals.clientIps.add(isa.getAddress().getHostAddress());
                    //-- Save Client Address

                    Log.e("MyTag","Accepted connection : " + sock);
                    // send file
                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    os = sock.getOutputStream();
                    System.out.println("Sending " + myFile.getName() + "(" + mybytearray.length + " bytes)");
                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();
                    System.out.println("Done.");

                    publishProgress();
                }
                finally {
                    if (bis != null) bis.close();
                    if (os != null) os.close();
                    if (sock!=null) sock.close();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("MyTag", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MyTag", e.getMessage());
        } finally {
            if (servsock != null) try {
                servsock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        callback.onDocSent(true, port);
        super.onProgressUpdate();
    }

    public void close() {
        //Send CLOSE_SESSION Event to clients
        //-- Send CLOSE_SESSION Event to clients

        //Close ServerSocket
        try {
            servsock.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DocSender", e.getMessage());
        }
        //Close ServerSocket
    }

    public interface Callback{
        void onDocSent(boolean result, int portNo);
    }
}
