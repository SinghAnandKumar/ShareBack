package com.fantasticfive.shareback.newshareback;

import android.os.Environment;

/**
 * Created by sagar on 13/7/16.
 */
public class Constants {

    public static String IP_FILE_SERVER = "192.168.0.2";
    public static final String PREFERENCES = "MyPrefs";
    public static final String PREF_SERVER_IP = "ip";

    public static final int PORT_FILE_C2S = 8110;
    public static final int PORT_LS = 8103;
    public static final int PORT_FILE_S2C=8104;
    public static final int PORT_TOKEN_DIST=8105;
    public static final int PORT_EVENT_DIST = 8106;
    public static final int PORT_FEEDBACK = 8107;
    public static final int PORT_FILE_OEPRATIONS = 8108;
    public static final int PORT_LOGIN = 8109;

    public static final int BUFFER_SIZE=8192;
    public static final int MAX_CONNECTS = 4;
    public static final int SKT_TIME_OUT = 2 * 1000; //(2 secs)

    public static final int EVENT_PAGE_CHANGED = 1;
    public static final int EVENT_FILE_CHANGED = 2;
    public static final int EVENT_FILE_ADDED = 3;
    public static final int EVENT_FILES_ADDED = 4;
    public static final int EVENT_SESSION_CLOSED = 5;
    public static final int EVENT_FILE_REMOVED = 6;

    public static final String JSON_LIST_DIR = "path"; //for ls json object { "path" : "/Data Structure/"} incoming
    public static final String JSON_DIRS = "dirs"; // response { "dirs" : ["dir1","dir2",...] ,
    public static final String JSON_FILES = "files"; // "files" : [ "file1" , "file2", ... ] }
    public static final String JSON_SERVER_IP = "server_ip";
    public static final String JSON_FILE_DWNLD = "filename"; // for downloading file { "filename" : "abc.pdf" }
    public static final String JSON_FB_TYPE = "type";
    public static final String JSON_FB_RATING = "rating";
    public static final String JSON_FB_COMMENT = "comment";
    public static final String JSON_FB_SESSION_ID = "session_id";
    public static final String JSON_FB_FILES = "files";
    public static final String JSON_FB_SESSION_NAME = "session_name";
    public static final String JSON_FB_SESSIONS_INFO = "sm_request";
    public static final String JSON_FB_INSTRUCTOR_ID = "instructor_id";
    public static final String JSON_FB_TIMESTAMP = "timestamp";


    public static final String JSON_FO_OPERATION = "operation";
    public static final String JSON_FO_OLD_FILE = "old_file";
    public static final String JSON_FO_NEW_FILE = "new_file";
    public static final String JSON_FO_DIR_PATH = "new_dir";

    public static final String JSON_TOKEN_NO = "token_no";
    public static final String JSON_PAGE_NOS = "page_nos";
    public static final String JSON_CURR_FILE = "curr_file";
    public static final String JSON_EVENT_ID = "event_no";
    public static final String JSON_EVENT_NAME = "event_name";
    public static final String JSON_EVENT_FILE = "file";
    public static final String JSON_EVENT_PAGE = "value";

    public static final String JSON_USERNAME = "username";
    public static final String JSON_PASSWORD = "password";

    public static final String FB_EVENT_CREATE_SESSION = "create_session";
    public static final String FB_EVENT_FEEBACK = "feedback";
    public static final String FB_EVENT_FILE_ADDED = "file_added";
    public static final String FB_SESSION_NAME = "session_name";

    public static final String FO_MKDIR = "mkdir";
    public static final String FO_DELETE = "delete";
    public static final String FO_MOVE = "move";
    public static final String FO_RENAME = "rename";
    public static final String FO_COPY = "copy";

    public static final String END_OF_MSG = "iluvuanand#wtf";
    public static final String NSD_BASE_NAME = "EShareback";

    public static final String DIR_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EShareback";
    public static final String KEY_SESSION_ID = "session_id";
    public static final String KEY_SESSION_IP = "session_ip";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_NEW_NSD_NAME = "new_name";
    public static final String KEY_SHAREBUCKET = "share_bucket";

    public static final int NUM_OF_RATINGS = 5;

    public static final String[] SUPPORTED_FORMATS= {".pdf", ".txt", ".ppt", ".pptx", ".doc", ".docx", ".xls", ".xlsx", ".rtf", ".wpd"};
}
