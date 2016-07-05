package com.fantasticfive.shareback;

import java.util.ArrayList;

/**
 * Created by Sagar on 09-03-2016.
 */
public class ShareBucket {

    private static ArrayList<String> al = new ArrayList<>();

    public static void put(String s){
        al.add(s);
    }

    public static int size(){
        return al.size();
    }

    public static String get(int index){
        return al.get(index);
    }
}
