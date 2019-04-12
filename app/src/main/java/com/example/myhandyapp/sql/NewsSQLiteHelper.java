package com.example.myhandyapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NewsSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NEWS = "news";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NEWS_TITLE = "news_title";
    public static final String COLUMN_NEWS_AUTHOR = "news_author";
    public static final String COLUMN_NEWS_ARTICLE = "news_article";
    public static final String COLUMN_NEWS_URL = "news_url";

    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 2; //change id to drop existing the database


    private static final String DATABASE_CREATE = "create table "
            + TABLE_NEWS + "( " + COLUMN_ID  + " integer primary key autoincrement, "
            + COLUMN_NEWS_TITLE + " text not null, "
            + COLUMN_NEWS_AUTHOR + " text not null, "
            + COLUMN_NEWS_URL + " text not null, "
            + COLUMN_NEWS_ARTICLE + " text not null);";

    public NewsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //database.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);

        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NewsSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NewsSQLiteHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        onCreate(db);
    }

    public static void printCursor(Cursor c){
        Log.d(NewsSQLiteHelper.class.getName()," Cursor information:");
        Log.d("Database Version: ",String.valueOf(DATABASE_VERSION));
        Log.d("Number of colums: ",String.valueOf(c.getColumnCount()));
        for(int i = 0; i < c.getColumnCount(); i++)
            Log.d("Column " + i + ": ", c.getColumnName(i));

        Log.d("Number of results: ",String.valueOf(c.getCount()));
        Log.d(" CURSOR DATA: ", DatabaseUtils.dumpCursorToString(c));
    }
}
