package com.example.myhandyapp.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.myhandyapp.listitems.Flight;

public class FlightsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private FlightsSQLiteHelper dbHelper;
    private String[] allColumns = {
            FlightsSQLiteHelper.COLUMN_ID,
            FlightsSQLiteHelper.COLUMN_MESSAGE_SOURCE,
            FlightsSQLiteHelper.COLUMN_MESSAGE};

    public FlightsDataSource(Context context) {
        dbHelper = new FlightsSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Flight createFlight(String message_source, String message) {
        ContentValues values = new ContentValues();
        values.put(FlightsSQLiteHelper.COLUMN_MESSAGE_SOURCE, message_source);
        values.put(FlightsSQLiteHelper.COLUMN_MESSAGE, message);
        long insertId = database.insert(FlightsSQLiteHelper.TABLE_FLIGHTS, null, values);
        Cursor cursor = database.query(FlightsSQLiteHelper.TABLE_FLIGHTS,
                allColumns, FlightsSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Flight newFlight = cursorToFlight(cursor);
        cursor.close();
        return newFlight;
    }

    public void deleteFlight(Flight flight) {
        long id = flight.getId();
        System.out.println(FlightsSQLiteHelper.TABLE_FLIGHTS + " deleted with id: " + id);
        database.delete(FlightsSQLiteHelper.TABLE_FLIGHTS, FlightsSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<Flight>();

        Cursor cursor = database.query(FlightsSQLiteHelper.TABLE_FLIGHTS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Flight flight = cursorToFlight(cursor);
            flights.add(flight);
            cursor.moveToNext();
        }
        printCursor(cursor);

        // Make sure to close the cursor
        cursor.close();
        return flights;
    }

    private Flight cursorToFlight(Cursor cursor) {
        Flight flight = new Flight();
        flight.setId(cursor.getLong(0));
        flight.setAirport(cursor.getString(1));
        flight.setAirline(cursor.getString(2));
        return flight;
    }

    public void printCursor(Cursor c){
        FlightsSQLiteHelper.printCursor(c);
    }

}