package com.example.myhandyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myhandyapp.listitems.NYT;
import com.example.myhandyapp.sql.NYTsDataSource;

public class NYTActivity extends CommonActivity {

    //private static final String AIRPORT_CODE = "AirportCode";


    Button btnReset;
    Button btnSearch;
    EditText txtNYTCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nyt_activity);

        //shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        //String savedAirport = sp.getString(AIRPORT_CODE, "");

        //reset button
        btnReset = findViewById(R.id.btnReset);
       // btnReset.setOnClickListener( b -> dispatchResetAction());

        //search button
        btnSearch = findViewById(R.id.btnSearch);
        //btnSearch.setOnClickListener( b -> dispatchSearchAction());


    }


}