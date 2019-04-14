package com.example.myhandyapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myhandyapp.listitems.Flight;
import com.example.myhandyapp.listitems.NYT;

import java.util.ArrayList;
import java.util.List;

public class NYTsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private NYTsSQLiteHelper dbHelper;
    private String[] allColumns = {
            NYTsSQLiteHelper.COLUMN_ID,
            NYTsSQLiteHelper.TITLE,
            NYTsSQLiteHelper.ARTICLE,
            NYTsSQLiteHelper.LINK
    };

    public NYTsDataSource(Context context) {
        dbHelper = new NYTsSQLiteHelper(context);
    }


    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }


    public void close() {
        dbHelper.close();
    }


    //create NYT article in database and return the full NYI item java object
    public NYT createNYT(String title, String article, String link) {
        ContentValues values = new ContentValues();
        values.put(NYTsSQLiteHelper.TITLE, title);
        values.put(NYTsSQLiteHelper.ARTICLE, article);
        values.put(NYTsSQLiteHelper.LINK, link);

        long insertId = database.insert(NYTsSQLiteHelper.TABLE_NYT, null, values);
        Cursor cursor = database.query(NYTsSQLiteHelper.TABLE_NYT,
                allColumns, NYTsSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        NYT newNYT = cursorToNYT(cursor);
        cursor.close();
        return newNYT;
    }
    public void deleteNYT(long id ) {
        Log.d(NYTsSQLiteHelper.TABLE_NYT, " deleted with id: " + id);
        database.delete(NYTsSQLiteHelper.TABLE_NYT, NYTsSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<NYT> getAllNYTs() {
        List<NYT> nytObject = new ArrayList<NYT>();

        Cursor cursor = database.query(NYTsSQLiteHelper.TABLE_NYT,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NYT nyt = cursorToNYT(cursor);
            nytObject.add(nyt);
            cursor.moveToNext();
        }
        printCursor(cursor);

        // Make sure to close the cursor
        cursor.close();
        return nytObject;
    }


    //add DB is to the t object
    private NYT cursorToNYT(Cursor cursor) {
        NYT nyt = new NYT();
        nyt.setId(cursor.getLong(0));
        nyt.setTitle(cursor.getString(1));
        nyt.setArticle(cursor.getString(2));
        nyt.setLink(cursor.getString(3));


        return nyt;
    }

    public void printCursor(Cursor c){
        NYTsSQLiteHelper.printCursor(c);
    }

}
