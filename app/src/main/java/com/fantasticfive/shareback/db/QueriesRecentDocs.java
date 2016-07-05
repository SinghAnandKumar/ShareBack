package com.fantasticfive.shareback.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.fantasticfive.shareback.Globals;
import com.fantasticfive.shareback.beans.RecentDoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sagar on 25-03-2016.
 */
public class QueriesRecentDocs {
    private DbInit dbCon;

    public QueriesRecentDocs(Context context) {
        dbCon = new DbInit(context);
    }

    public int add(File f) {

        //Open connection to write data
        SQLiteDatabase db = dbCon.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Globals.RF_FILE_NAME, f.getAbsolutePath());
        values.put(
                Globals.RF_DATE,
                Long.toString(
                    new Date().getTime()
                )
        );

        // Inserting Row

        long id;
        try {
            id = db.insert(Globals.REC_FILE_TABLE, null, values);
        }
        catch (SQLiteConstraintException exception){
            id = -1;
        }
        finally {
            db.close(); // Closing database connection
        }
        if(id == -1){
            update(f);
        }
        return (int) id;
    }

    public void update(File file) {

        SQLiteDatabase db = dbCon.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Globals.RF_DATE, new Date().getTime()+"");

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Globals.REC_FILE_TABLE, values, Globals.RF_FILE_NAME + "= ?", new String[]{file.getAbsolutePath()});
        db.close(); // Closing database connection
    }

    public ArrayList<RecentDoc>  getDocs() {
        //Open connection to read only
        SQLiteDatabase db = dbCon.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Globals.RF_FILE_NAME +", " +
                Globals.RF_DATE +
                " FROM " + Globals.REC_FILE_TABLE +
                " ORDER BY " + Globals.RF_DATE + " DESC";

        //Student student = new Student();
        ArrayList<RecentDoc> al = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        int count=0;
        if (cursor.moveToFirst()) {
            do {
                RecentDoc recentDoc = new RecentDoc();
                recentDoc.setName(cursor.getString(cursor.getColumnIndex(Globals.RF_FILE_NAME)));
                recentDoc.setDate(
                        new Date(
                                cursor.getLong(cursor.getColumnIndex(Globals.RF_DATE))
                        )
                );
                al.add(recentDoc);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return al;
    }
}
