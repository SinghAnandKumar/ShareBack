package com.fantasticfive.shareback.concept2.util;

/**
 * Created by sagar on 25/2/17.
 */
public class WordUtils {
    public static String capitalizeFirstChar(String sessionName){
        sessionName = sessionName.trim();

        String session[] = sessionName.split(" ");
        sessionName = "";
        for(String s: session){
            if(s.length()>1)
                s = Character.toUpperCase(s.charAt(0)) + s.substring(1,s.length());
            sessionName = sessionName + " " + s;
        }

        return  sessionName;
    }

    public static String firstChar(String str){
        for(int i=0; i<str.length(); i++){
            char c = str.charAt(i);
            if(('a'<=c && c<='z') || ('A'<=c && c<='Z')){
                return ""+c;
            }
        }
        return "A";
    }
}
