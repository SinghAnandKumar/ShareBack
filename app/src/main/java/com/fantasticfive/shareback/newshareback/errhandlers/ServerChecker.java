package com.fantasticfive.shareback.newshareback.errhandlers;

import android.content.Context;
import android.os.AsyncTask;

import com.fantasticfive.shareback.newshareback.Constants;

import java.io.IOException;

/**
 * Created by sagar on 2/8/16.
 */
public class ServerChecker extends AsyncTask<Void, Void, Boolean> {

    Context context;
    ServerCheckerCallback callback;
    public ServerChecker(Context context, ServerCheckerCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Runtime runtime = Runtime.getRuntime();
        Process proc = null; //<- Try ping -c 1 www.serverURL.com
        try {
            proc = runtime.exec("ping -c 1 "+ Constants.IP_FILE_SERVER);
            int mPingResult = proc .waitFor();
            if(mPingResult == 0){
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {

        if(!isCancelled()){
            callback.onServerCheckerResponse(aBoolean);
        }
        super.onPostExecute(aBoolean);
    }

    public interface ServerCheckerCallback{
        void onServerCheckerResponse(boolean response);
    }
}
