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



/**
 *  In this class we define the column for our database which are column id , title, article and  link to the article.
 *  It creates the NYT article in database and get the id. It retrieve the database record, create new object and add it.
 */

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

    /**
     * It is the constructor
     * @param context
     */
    public NYTsDataSource(Context context) {
        dbHelper = new NYTsSQLiteHelper(context);
    }

    /**
     * It opens the database
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }


    /**
     * It closes the database
     */
    public void close() {
        dbHelper.close();
    }


    /**
     * It creates NYT article in database and return the full NYI item java object
     * Retrieve the DB  record, crete new object, add ID to it and return for further processing.
     * @param title
     * @param article
     * @param link
     * @return
     */
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

    /**
     * It is delete function that delete using database id , and that updates the table
     * @param id
     */

    public void deleteNYT(long id ) {
        Log.d(NYTsSQLiteHelper.TABLE_NYT, " deleted with id: " + id);
        database.delete(NYTsSQLiteHelper.TABLE_NYT, NYTsSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    /**
     * It is the List using the cursor that contains row from the query.
     * @return nytObject
     */
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


    /**
     * It adds the NYT object
     * @param cursor
     * @return
     */
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
