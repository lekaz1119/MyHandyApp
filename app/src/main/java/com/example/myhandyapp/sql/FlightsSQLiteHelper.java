package com.example.myhandyapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FlightsSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_FLIGHTS = "flights";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_MESSAGE_SOURCE = "source";

    private static final String DATABASE_NAME = "flights.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_FLIGHTS + "( " + COLUMN_ID  + " integer primary key autoincrement, "
            + COLUMN_MESSAGE_SOURCE + " text not null, "
            + COLUMN_MESSAGE + " text not null);";

    public FlightsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FlightsSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLIGHTS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FlightsSQLiteHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLIGHTS);
        onCreate(db);
    }

    public static void printCursor(Cursor c){
        Log.d(FlightsDataSource.class.getName()," Cursor information:");
        Log.d("Database Version: ",String.valueOf(DATABASE_VERSION));
        Log.d("Number of colums: ",String.valueOf(c.getColumnCount()));
        for(int i = 0; i < c.getColumnCount(); i++)
            Log.d("Column " + i + ": ", c.getColumnName(i));

        Log.d("Number of results: ",String.valueOf(c.getCount()));
        Log.d(" CURSOR DATA: ",DatabaseUtils.dumpCursorToString(c));
    }
}

