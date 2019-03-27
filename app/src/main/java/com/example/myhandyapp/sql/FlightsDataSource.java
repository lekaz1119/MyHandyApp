package com.example.myhandyapp.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myhandyapp.listitems.Flight;

public class FlightsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private FlightsSQLiteHelper dbHelper;
    private String[] allColumns = {
            FlightsSQLiteHelper.COLUMN_ID,
            FlightsSQLiteHelper.COLUMN_AIRPORT_FROM,
            FlightsSQLiteHelper.COLUMN_AIRPORT_TO,
            FlightsSQLiteHelper.COLUMN_FLIGHT,
            FlightsSQLiteHelper.COLUMN_FLIGHT_LOCATION,
            FlightsSQLiteHelper.COLUMN_FLIGHT_SPEED,
            FlightsSQLiteHelper.COLUMN_FLIGHT_ALTITUDE,
            FlightsSQLiteHelper.COLUMN_FLIGHT_STATUS
    };

    public FlightsDataSource(Context context) {
        dbHelper = new FlightsSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //create flight record in database and get it's DB id.
    //retrieve the DB flight record, crete new FlightObject, add ID to it and return for further processing.
    public Flight createFlight(String airportFrom, String airportTo, String flight,
                                String location, String speed, String altitude, String status) {
        ContentValues values = new ContentValues();
        values.put(FlightsSQLiteHelper.COLUMN_AIRPORT_FROM, airportFrom);
        values.put(FlightsSQLiteHelper.COLUMN_AIRPORT_TO, airportTo);
        values.put(FlightsSQLiteHelper.COLUMN_FLIGHT, flight);
        values.put(FlightsSQLiteHelper.COLUMN_FLIGHT_LOCATION, location);
        values.put(FlightsSQLiteHelper.COLUMN_FLIGHT_SPEED, speed);
        values.put(FlightsSQLiteHelper.COLUMN_FLIGHT_ALTITUDE, altitude);
        values.put(FlightsSQLiteHelper.COLUMN_FLIGHT_STATUS, status);
        long insertId = database.insert(FlightsSQLiteHelper.TABLE_FLIGHTS, null, values);
        Cursor cursor = database.query(FlightsSQLiteHelper.TABLE_FLIGHTS,
                allColumns, FlightsSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Flight newFlight = cursorToFlight(cursor);
        cursor.close();
        return newFlight;
    }

    public void deleteFlight(long id ) {
        Log.d(FlightsSQLiteHelper.TABLE_FLIGHTS, " deleted with id: " + id);
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


    //add DB is to the flight object
    private Flight cursorToFlight(Cursor cursor) {
        Flight flight = new Flight();
        flight.setId(cursor.getLong(0));
        flight.setAirportFrom(cursor.getString(1));
        flight.setAirportTo(cursor.getString(2));
        flight.setFlight(cursor.getString(3));
        flight.setLocation(cursor.getString(4));
        flight.setSpeed(cursor.getString(5));
        flight.setAltitude(cursor.getString(6));
        flight.setStatus(cursor.getString(7));

        return flight;
    }

    public void printCursor(Cursor c){
        FlightsSQLiteHelper.printCursor(c);
    }

}