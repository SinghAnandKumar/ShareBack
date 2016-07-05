package com.fantasticfive.shareback.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.beans.RecentDoc;
import com.fantasticfive.shareback.beans.SessionDetails;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sagar on 18-04-2016.
 */
public class QueriesSessions {
    private DbInit dbCon;

    public QueriesSessions(Context context) {
        dbCon = new DbInit(context);
    }

    public int add(String filePath) {

        //Open connection to write data
        SQLiteDatabase db = dbCon.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(
                Globals.RF_DATE,
                Long.toString(
                        new Date().getTime()
                )
        );
        values.put(Globals.SESSIONS_DOC, filePath);

        // Inserting Row
        long id;
        try {
            id = db.insert(Globals.TABLE_SESSIONS, null, values);
        }
        catch (SQLiteConstraintException exception){
            id = -1;
        }
        finally {
            db.close(); // Closing database connection
        }
        if(id == -1){
            id = update(filePath);
        }
        return (int) id;
    }

    public int update(String filePath) {

        SQLiteDatabase db = dbCon.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Globals.SESSIONS_DATE, new Date().getTime() + "");

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Globals.TABLE_SESSIONS, values, Globals.SESSIONS_DOC + "= ?", new String[]{filePath});
        String findSessionId =  "SELECT  " +
                Globals.SESSIONS_ID +
                " FROM " + Globals.TABLE_SESSIONS +
                " WHERE " + Globals.SESSIONS_DOC + " = '"+filePath +"'"+
                " ORDER BY " + Globals.SESSIONS_DATE + " DESC";
        Cursor cursor = db.rawQuery(findSessionId, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(Globals.SESSIONS_ID));

        cursor.close();
        db.close(); // Closing database connection

        return id;
    }

    public ArrayList<RecentDoc> getRecDocs() {
        //Open connection to read only
        SQLiteDatabase db = dbCon.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Globals.SESSIONS_ID +", " +
                Globals.SESSIONS_DOC +", " +
                Globals.SESSIONS_DATE +
                " FROM " + Globals.TABLE_SESSIONS +
                " ORDER BY " + Globals.SESSIONS_DATE + " DESC";

        //Student student = new Student();
        ArrayList<RecentDoc> al = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                RecentDoc recentDoc = new RecentDoc();
                recentDoc.setSessionId(cursor.getInt(cursor.getColumnIndex(Globals.SESSIONS_ID)));
                recentDoc.setName(cursor.getString(cursor.getColumnIndex(Globals.SESSIONS_DOC)));
                recentDoc.setDate(
                        new Date(
                                cursor.getLong(cursor.getColumnIndex(Globals.SESSIONS_DATE))
                        )
                );
                al.add(recentDoc);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return al;
    }

    public SessionDetails getSessionDetails(int sessionId) {
        //Open connection to read only
        SQLiteDatabase db = dbCon.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                "*"+
                " FROM " + Globals.TABLE_SESSIONS +
                " WHERE " + Globals.SESSIONS_ID + " = " + sessionId;

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        cursor.moveToFirst();
        SessionDetails sessionDetails = new SessionDetails();
        sessionDetails.setSessionId(cursor.getInt(cursor.getColumnIndex(Globals.SESSIONS_ID)));
        sessionDetails.setDoc(cursor.getString(cursor.getColumnIndex(Globals.SESSIONS_DOC)));
        sessionDetails.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Globals.SESSIONS_DATE))));

        int stars[] = new int[5];
        stars[0] = cursor.getInt(cursor.getColumnIndex(Globals.STAR1));
        stars[1] = cursor.getInt(cursor.getColumnIndex(Globals.STAR2));
        stars[2] = cursor.getInt(cursor.getColumnIndex(Globals.STAR3));
        stars[3] = cursor.getInt(cursor.getColumnIndex(Globals.STAR4));
        stars[4] = cursor.getInt(cursor.getColumnIndex(Globals.STAR5));
        sessionDetails.setStars(stars);

        cursor.close();
        db.close();
        return sessionDetails;
    }

    public void updateStars(int[] stars){
        SQLiteDatabase db = dbCon.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Globals.STAR1, stars[0]);
        values.put(Globals.STAR2, stars[1]);
        values.put(Globals.STAR3, stars[2]);
        values.put(Globals.STAR4, stars[3]);
        values.put(Globals.STAR5, stars[4]);

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Globals.TABLE_SESSIONS, values, Globals.SESSIONS_ID + "= ?", new String[]{""+Globals.sessionId});
        db.close();
    }
}
