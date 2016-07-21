package com.fantasticfive.shareback.newshareback;

import android.os.Environment;

/**
 * Created by sagar on 13/7/16.
 */
public class Constants {

    public static final String IP_FILE_SERVER = "192.168.43.128";

    public static final int PORT_FILE_C2S = 8110;
    public static final int PORT_LS = 8103;
    public static final int PORT_FILE_S2C=8104;
    public static final int PORT_TOKEN_DIST=8105;
    public static final int PORT_EVENT_DIST = 8106;

    public static final int BUFFER_SIZE=8192;
    public static final int MAX_CONNECTS = 4;
    public static final int SKT_TIME_OUT = 2 * 1000; //(2 secs)

    public static final String JSON_LIST_DIR = "path"; //for ls json object { "path" : "/Data Structure/"} incoming
    public static final String JSON_DIRS = "dirs"; // response { "dirs" : ["dir1","dir2",...] ,
    public static final String JSON_FILES = "files"; // "files" : [ "file1" , "file2", ... ] }
    public static final String JSON_FILE_DWNLD = "filename"; // for downloading file { "filename" : "abc.pdf" }
    public static final String JSON_TOKEN_NO = "token_no";
    public static final String JSON_PAGE_NOS = "page_nos";
    public static final String JSON_CURR_FILE = "curr_file";
    public static final String JSON_EVENT_ID = "id";
    public static final String JSON_EVENT_NAME = "event_name";
    public static final String JSON_EVENT_FILE = "file";
    public static final String JSON_EVENT_PAGE = "value";

    public static final String END_OF_MSG = "iluvuanand#wtf";
    public static final String NSD_BASE_NAME = "EShareback";

    public static final String DIR_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EShareback";
}
