package com.fantasticfive.shareback.concept2.util;

import android.util.Log;

import com.fantasticfive.shareback.concept2.exception.NotImplementedException;

/**
 * Created by sagar on 19/2/17.
 */
public class UserData {

    private static final String TAG = "MY Tag";

    public static String getUserId(){

        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            e.printStackTrace();
        }
        String userId = "not_implemented";
        Log.e(TAG, "getUserId: "+userId);
        return userId ;
    }

    public static String getName(){

        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            e.printStackTrace();
        }
        String name="Naam Pata Ni";
        Log.i(TAG, "getName: "+name);
        return name;
    }


}
