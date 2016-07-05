package com.fantasticfive.shareback.utils;

import android.os.AsyncTask;

/**
 * Created by Sagar on 12-04-2016.
 */
public class Sleeper extends AsyncTask {

    Callback callback;
    public Sleeper(Callback callback){
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        callback.onWakeup();
        super.onPostExecute(o);
    }

    public interface Callback{
        void onWakeup();
    }
}

