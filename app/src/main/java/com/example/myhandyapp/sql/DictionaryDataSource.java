package com.example.myhandyapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myhandyapp.listitems.Dictionary;
import com.example.myhandyapp.listitems.Flight;

import java.util.ArrayList;
import java.util.List;


/**
 * class to manipulate database
 */
public class DictionaryDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DictionarySQLiteHelper dbHelper;
    private String[] allColumns = {
            DictionarySQLiteHelper.COLUMN_ID,
            DictionarySQLiteHelper.COLUMN_ENTRY_NUMBER,
            DictionarySQLiteHelper.COLUMN_WORD,
            DictionarySQLiteHelper.COLUMN_PART_OF_SPEECH,
            DictionarySQLiteHelper.COLUMN_PRONUNCIATION,
            DictionarySQLiteHelper.COLUMN_DEFINITIONS
    };

    /**
     * constructor
     * @param context
     */
    public DictionaryDataSource(Context context) {
        dbHelper = new DictionarySQLiteHelper(context);
    }

    /**
     * method to open database as writable
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * method to close the database
     */
    public void close() {
        dbHelper.close();
    }

    //create a definition record in the database and get it's DB id.
    //retrieve the DB definition record, crete new DictionaryObject, add ID to it and return for further processing.
    public Dictionary createDefinition(String entryNumber, String word, String partOfSpeech, String pronunciation, String definitions) {
        ContentValues values = new ContentValues();
        values.put(DictionarySQLiteHelper.COLUMN_ENTRY_NUMBER, entryNumber);
        values.put(DictionarySQLiteHelper.COLUMN_WORD, word);
        values.put(DictionarySQLiteHelper.COLUMN_PART_OF_SPEECH, partOfSpeech);
        values.put(DictionarySQLiteHelper.COLUMN_PRONUNCIATION, pronunciation);
        values.put(DictionarySQLiteHelper.COLUMN_DEFINITIONS, definitions);
        long insertId = database.insert(DictionarySQLiteHelper.TABLE_DICTIONARY, null, values);
        Cursor cursor = database.query(DictionarySQLiteHelper.TABLE_DICTIONARY,
                allColumns, DictionarySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Dictionary newDefinition = cursorToDictionary(cursor);
        cursor.close();
        return newDefinition;
    }

    /**
     * method to delete definition
     * @param id
     */
    public void deleteDefinition(long id ) {
        Log.d(DictionarySQLiteHelper.TABLE_DICTIONARY, " deleted with id: " + id);
        database.delete(DictionarySQLiteHelper.TABLE_DICTIONARY, DictionarySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    /**
     * method to get all the definitions stored in the database
     * @return
     */
    public List<Dictionary> getAllFlights() {
        List<Dictionary> definitions = new ArrayList<Dictionary>();

        Cursor cursor = database.query(DictionarySQLiteHelper.TABLE_DICTIONARY,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Dictionary definition = cursorToDictionary(cursor);
            definitions.add(definition);
            cursor.moveToNext();
        }
        printCursor(cursor);

        // Make sure to close the cursor
        cursor.close();
        return definitions;
    }

    /**
     * method that uses the values from the cursor to populate the Dictionary class
     * @param cursor
     * @return
     */
    private Dictionary cursorToDictionary(Cursor cursor) {
        Dictionary definition = new Dictionary();
        definition.setId(cursor.getLong(0));
        definition.setEntryNumber(cursor.getString(1));
        definition.setWord(cursor.getString(2));
        definition.setPartOfSpeech(cursor.getString(3));
        definition.setPronunciation(cursor.getString(4));
        definition.setDefinitions(cursor.getString(5));

        return definition;
    }

    public void printCursor(Cursor c){
        DictionarySQLiteHelper.printCursor(c);
    }

}
