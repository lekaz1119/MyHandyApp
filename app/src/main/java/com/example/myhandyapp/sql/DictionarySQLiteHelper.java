package com.example.myhandyapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myhandyapp.listitems.Dictionary;

public class DictionarySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_DICTIONARY = "dictionary";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DICT_WORD = "dict_word";
    public static final String COLUMN_ENTRY_NUMBER = "entry_number";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_PART_OF_SPEECH = "part_of_speech";
    public static final String COLUMN_PRONUNCIATION = "pronunciation";
    public static final String COLUMN_DEFINITIONS = "definitions";

    private static final String DATABASE_NAME = "dictionary.db";
    private static final int DATABASE_VERSION = 1; //change id to drop existing the database

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_DICTIONARY + "( " + COLUMN_ID  + " integer primary key autoincrement, "
            + COLUMN_DICT_WORD + " text not null, "
            + COLUMN_ENTRY_NUMBER + " text not null, "
            + COLUMN_WORD + " text not null, "
            + COLUMN_PART_OF_SPEECH + " text not null, "
            + COLUMN_PRONUNCIATION + " text not null, "
            + COLUMN_DEFINITIONS + " text not null);";

    public DictionarySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * method that  creates the table
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * method that makes changes to the database if the database version is greater that the one
     * stored
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DictionarySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
        onCreate(db);
    }

    /**
     * method that makes changes to the database if the database version is smaller that the one
     * that is stored
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DictionarySQLiteHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
        onCreate(db);
    }

    /**
     * prints the values from the cursor to LogCat
     * @param c
     */
    public static void printCursor(Cursor c){
        Log.d(DictionaryDataSource.class.getName()," Cursor information:");
        Log.d("Database Version: ",String.valueOf(DATABASE_VERSION));
        Log.d("Number of colums: ",String.valueOf(c.getColumnCount()));
        for(int i = 0; i < c.getColumnCount(); i++)
            Log.d("Column " + i + ": ", c.getColumnName(i));

        Log.d("Number of results: ",String.valueOf(c.getCount()));
        Log.d(" CURSOR DATA: ", DatabaseUtils.dumpCursorToString(c));
    }
}
