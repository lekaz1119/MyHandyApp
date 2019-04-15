package com.example.myhandyapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/**
 * This class if for creating and opening a database for the application. we define the file that will contain the data and
 * the version that starts from 1 the columns that are id, title, article and link
 */
public class NYTsSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_NYT = "NYT_TAB";
    public static final String COLUMN_ID = "_id";
    public static final String TITLE    = "nyt_title";
    public static final String ARTICLE = "nyt_article";
    public static final String LINK = "nyt_link";


    //database name. it is used in the constructor.
    //this is the file that will contain the data
    private static final String DATABASE_NAME = "nyt.db";
    //the version number start at 1
    private static final int DATABASE_VERSION = 1;

    /**
     * It is the constructor and we pass the Activity where the database is being opened, the file that will contain the data, An object to create Cursor objects(null)
     * and the database version
     * @param context
     */

    public NYTsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * First time when the database file doesn't exist yet, this method gets called .
     * It creates the table with the id, title, article and link.
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL("create table "
                + TABLE_NYT + "( " + COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT not null, " + ARTICLE + " TEXT not null, " + LINK + " TEXT not null);");

        Log.d(NYTsSQLiteHelper.class.getName()," creating DB \n\n\n:");
    }

    /**
     * If the database does exist, and the version in the constructor is newer than the version that exists on the device, then onUpgrade gets called
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(NYTsSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NYT);
        onCreate(db);
    }

    /**
     * If the database does exist, and the version number in the constructor is lower than the version number that exists on the device, then onDowngrade gets called.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(NYTsSQLiteHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NYT);
        onCreate(db);
    }


    /**
     * helper method for debugging, prints all database columns and records
     * @param c
     */

    public static void printCursor(Cursor c){
        Log.d(NYTsSQLiteHelper.class.getName()," Cursor information:");
        Log.d("Database Version: ",String.valueOf(DATABASE_VERSION));
        Log.d("Number of colums: ",String.valueOf(c.getColumnCount()));
        for(int i = 0; i < c.getColumnCount(); i++)
            Log.d("Column " + i + ": ", c.getColumnName(i));

        Log.d("Number of results: ",String.valueOf(c.getCount()));
        Log.d(" CURSOR DATA: ", DatabaseUtils.dumpCursorToString(c));
    }
}
