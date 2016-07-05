package com.fantasticfive.shareback;

import android.net.wifi.WifiConfiguration;

import java.util.HashSet;

/**
 * Created by Sagar on 07-03-2016.
 */
public class Globals {
    public static WifiConfiguration oldWifiCon;
    public static int CONNECTION_NUM = 1;

    public static int DOC_PORT = 7117;
    public static int EVENT_PORT = 7112;
    public static int FEEDBACK_PORT = 7107;

    public static String RECENT_PREF_NAME = "recentFile";

    public static String REC_FILE_TABLE = "rec_files";
    public static String RF_FILE_NAME = "file_name";
    public static String RF_DATE = "session_date";

    public static String TABLE_SESSIONS = "Sessions";
    public static String SESSIONS_ID = "session_id";
    public static String SESSIONS_DATE = "session_date";
    public static String SESSIONS_DOC = "doc";
    public static String STAR1 = "star_1";
    public static String STAR2 = "star_2";
    public static String STAR3 = "star_3";
    public static String STAR4 = "star_4";
    public static String STAR5 = "star_5";

    public static String TABLE_COMMENTS = "Comments";
    public static String COMMENTS_SID = "session_id";
    public static String COMMENTS_COMMENT = "comment";

    public static String font = "quick_sand.ttf";
    public static String font_bold = "quick_sand_bold.ttf";

    public static String splash_trans_key = "splash_img";

    public static String serverIp = "";

    public static HashSet<String> clientIps = new HashSet<>();
    public static int sessionId = -1;

    public static String KEY_SESSION = "session_key";
}
