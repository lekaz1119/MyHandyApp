package com.example.myhandyapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public NYTsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL("create table "
                + TABLE_NYT + "( " + COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT not null, " + ARTICLE + " TEXT not null, " + LINK + " TEXT not null);");

        Log.d(NYTsSQLiteHelper.class.getName()," creating DB \n\n\n:");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(NYTsSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NYT);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(NYTsSQLiteHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NYT);
        onCreate(db);
    }

    // helper method for debugging, prints all database colms and records
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
