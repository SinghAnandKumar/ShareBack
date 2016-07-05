package com.fantasticfive.shareback.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fantasticfive.shareback.Globals;

import java.util.ArrayList;

/**
 * Created by Sagar on 18-04-2016.
 */
public class QueriesComments {
    private DbInit dbCon;
    public QueriesComments(Context context) {
        dbCon = new DbInit(context);
    }

    public void add(String comment) {

        //Open connection to write data
        SQLiteDatabase db = dbCon.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Globals.COMMENTS_SID, Globals.sessionId);
        values.put(Globals.COMMENTS_COMMENT, comment);

        // Inserting Row
        db.insert(Globals.TABLE_COMMENTS, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<String> getComments(int sessionId){

        SQLiteDatabase db = dbCon.getWritableDatabase();
        String findSessionId =  "SELECT  " +
                Globals.COMMENTS_COMMENT +
                " FROM " + Globals.TABLE_COMMENTS +
                " WHERE " + Globals.COMMENTS_SID + " = "+sessionId;

        Cursor cursor = db.rawQuery(findSessionId, null);
        ArrayList<String> comments = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                comments.add(cursor.getString(cursor.getColumnIndex(Globals.COMMENTS_COMMENT)));
            } while (cursor.moveToNext());
        }

        db.close(); // Closing database connection
        return comments;
    }
}
