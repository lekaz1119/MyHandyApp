package com.example.myhandyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhandyapp.listitems.Flight;
import com.example.myhandyapp.sql.FlightsDataSource;

import java.util.Arrays;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);


    }


    //hide/show menu items to reflect current activity options
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            menu.findItem(R.id.action_flight_tracker).setVisible(true);
            menu.findItem(R.id.action_news).setVisible(false);
            menu.findItem(R.id.action_nyt).setVisible(true);
            menu.findItem(R.id.action_dictionary).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //View layout= findViewById(R.id.layout);

        switch (id) {
            case R.id.action_about:
                Toast.makeText(getApplicationContext(), "change it as you need ", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_main_help:
                Toast.makeText(getApplicationContext(), "change it as you need ", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_flight_tracker:
                //Give directions to go from this page, to flightTracker page
                Intent flightTrackerPage = new Intent(NewsActivity.this, FlightTrackerActivity.class);
                //Now make the transition:
                startActivity( flightTrackerPage);
                break;
            case R.id.action_news:
                break;
            case R.id.action_nyt:
                //Give directions to go from this page, to NYT page
                Intent nytPage = new Intent(NewsActivity.this, NYTActivity.class);
                //Now make the transition:
                startActivity( nytPage);
                break;
            case R.id.action_dictionary:
                //Give directions to go from this page, to Dictionary page
                Intent dictionaryPage = new Intent(NewsActivity.this, DictionaryActivity.class);
                //Now make the transition:
                startActivity( dictionaryPage);
                break;
        }

        return super.onOptionsItemSelected(item);
    }




}