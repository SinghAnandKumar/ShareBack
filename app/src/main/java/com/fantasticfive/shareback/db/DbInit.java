package com.fantasticfive.shareback.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fantasticfive.shareback.Globals;

/**
 * Created by Sagar on 25-03-2016.
 */
public class DbInit extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "shareback_db";


    public DbInit(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE "+ Globals.REC_FILE_TABLE +" (" +
                Globals.RF_FILE_NAME +" TEXT NOT NULL," +
                Globals.RF_DATE +" TEXT," +
                "   PRIMARY KEY("+Globals.RF_FILE_NAME+")" +
                ")";
        db.execSQL(CREATE_TABLE);

        String createSessionTable = "CREATE TABLE "+ Globals.TABLE_SESSIONS+" (" +
                Globals.SESSIONS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                Globals.SESSIONS_DATE +" TEXT," +
                Globals.SESSIONS_DOC +" TEXT," +
                Globals.STAR1 +" INTEGER DEFAULT 0," +
                Globals.STAR2 +" INTEGER DEFAULT 0," +
                Globals.STAR3 +" INTEGER DEFAULT 0," +
                Globals.STAR4 +" INTEGER DEFAULT 0," +
                Globals.STAR5 +" INTEGER DEFAULT 0" +
                ")";
        db.execSQL(createSessionTable);

        String createCommentsTable = "CREATE TABLE "+ Globals.TABLE_COMMENTS +" (" +
                Globals.COMMENTS_SID +" INTEGER NOT NULL," +
                Globals.COMMENTS_COMMENT +" TEXT" +
                ")";
        db.execSQL(createCommentsTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Globals.REC_FILE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Globals.TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Globals.TABLE_COMMENTS);

        // Create tables again
        onCreate(db);

    }

}
