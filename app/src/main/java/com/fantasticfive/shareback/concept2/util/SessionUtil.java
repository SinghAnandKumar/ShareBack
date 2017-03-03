package com.fantasticfive.shareback.concept2.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sagar on 2/3/17.
 */
public class SessionUtil {

    public static String generateSessionKey(String sessionName){
        String errMsg="";
        try {
            //Get currentTime
            long time = System.currentTimeMillis();
            String key = sessionName+time;

            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            errMsg = e.getMessage();
        }
        return "Cannot Generate: "+errMsg;
    }
}
