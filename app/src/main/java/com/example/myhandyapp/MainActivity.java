package com.example.myhandyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //initilize shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

        ImageButton nytitmes = (ImageButton)findViewById(R.id.btnNYT);
        nytitmes.setOnClickListener( c -> {
            //Give directions to go from this page, to flightTracker page
            Intent nytPage = new Intent(MainActivity.this, NYTActivity.class);
            //Now make the transition:
            startActivity(nytPage);
        });


        ImageButton btnNews = (ImageButton)findViewById(R.id.btnNews);
        btnNews.setOnClickListener( c -> {
            //Give directions to go from this page, to flightTracker page
            Intent newsPage = new Intent(MainActivity.this, NewsActivity.class);
            //Now make the transition:
            startActivity(newsPage);
        });


        ImageButton btnDictionary = (ImageButton)findViewById(R.id.btnDictionary);
        btnDictionary.setOnClickListener( c -> {
            //Give directions to go from this page, to flightTracker page
            Intent dictionaryPage = new Intent(MainActivity.this, DictionaryActivity.class);
            //Now make the transition:
            startActivity(dictionaryPage);
        });

        ImageButton btnFlights = (ImageButton)findViewById(R.id.btnFilights);
        btnFlights.setOnClickListener( c -> {
            //Give directions to go from this page, to flightTracker page
            Intent flightsPage = new Intent(MainActivity.this, FlightTrackerActivity.class);
            //Now make the transition:
            startActivity(flightsPage);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //View layout= findViewById(R.id.layout);

        switch (id) {
            case R.id.action_about:
                Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.toast_about), Toast.LENGTH_LONG).show();
                break;
            case R.id.action_main_help:
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, this.getResources().getString(R.string.main_help), Snackbar.LENGTH_LONG);
                snackbar.setAction("DISMISS", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if( snackbar != null && snackbar.isShown() ) {
                                    snackbar.dismiss();
                                }
                            }
                        });
                View snackbarView = snackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(15);  // show multiple line
                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
