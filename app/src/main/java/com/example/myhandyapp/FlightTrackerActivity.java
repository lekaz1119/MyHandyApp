package com.example.myhandyapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhandyapp.listitems.Flight;
import com.example.myhandyapp.sql.FlightsDataSource;

import java.util.Arrays;
import java.util.List;

public class FlightTrackerActivity extends AbstractActivity {
    private List<Flight> flightList;
    private ListAdapter adt;
    private FlightsDataSource datasource;
    SharedPreferences sp;

    Button btnReset;
    Button btnSearch;//menu_flight_tracker
    EditText txtAirportCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_tracker_activity);

        //initilize shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

        datasource = new FlightsDataSource(this);
        datasource.open();

        flightList = datasource.getAllFlights();

        txtAirportCode=(EditText)findViewById(R.id.txtAirportCode);

        //reset button
        btnReset = (Button)findViewById(R.id.btnReset);
        btnReset.setOnClickListener( b -> dispatchResetAction());

        //search button
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener( b -> dispatchSearchAction());

        adt = new MyArrayAdapter(flightList);

        ListView theList = (ListView)findViewById(R.id.message_list);
        SwipeRefreshLayout refresher = (SwipeRefreshLayout)findViewById(R.id.refresher) ;
        refresher.setOnRefreshListener(()-> {
            ((MyArrayAdapter) adt).notifyDataSetChanged();
            refresher.setRefreshing( false );
        });

        theList.setAdapter(adt);

        //This listens for items being clicked in the list view
        theList.setOnItemClickListener(( parent,  view,  position,  id) -> {
            Log.e("you clicked on :" , "item "+ position);
            ((MyArrayAdapter) adt).notifyDataSetChanged();
        });

    }



    private void dispatchResetAction() {
        txtAirportCode.setText("");
        datasource = new FlightsDataSource(this);
        datasource.open();
        flightList = datasource.getAllFlights();
        //delete all flights from DB
        for (Flight flight : flightList ) {
            datasource.deleteFlight(flight);
        }
        datasource.close();

        //get an editor object
        SharedPreferences.Editor editor = sp.edit();
        //save what was typed under the name "ReserveName"
        editor.remove("AirportCode");
        //write it to disk:
        editor.commit();

    }

    private void dispatchSearchAction() {
        String airportCode = txtAirportCode.getText().toString();
        Flight flight;

        Log.e("you clicked on :" , "Button search");

    }


    @Override
    protected void onPause() { super.onPause();

        //get an editor object
        SharedPreferences.Editor editor = sp.edit();

        //save what was typed under the name "ReserveName"
        editor.putString("AirportCode", String.valueOf(txtAirportCode.getText()));

        //write it to disk:
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //A copy of ArrayAdapter. You just give it an array and it will do the rest of the work.
    protected class MyArrayAdapter<E> extends BaseAdapter
    {
        private List<E> dataCopy = null;

        //Keep a reference to the data:
        public MyArrayAdapter(List<E> originalData)
        {
            dataCopy = originalData;
        }

        //You can give it an array
        public MyArrayAdapter(E [] array)
        {
            dataCopy = Arrays.asList(array);
        }


        //Tells the list how many elements to display:
        public int getCount()
        {
            return dataCopy.size();
        }


        public E getItem(int position){
            return dataCopy.get(position);
        }

        public View getView(int position, View old, ViewGroup parent)
        {
            //get an object to load a layout:
            LayoutInflater inflater = getLayoutInflater();

            Flight flight = (Flight)this.getItem(position);

            String flightToShow = "Flight Data";// flight.getDetails();

            View root = inflater.inflate(R.layout.flight_tracker_details, parent, false);

            TextView rowText = (TextView) root.findViewById(R.id.textOnRow);

            rowText.setText( flightToShow );

            //Return the text view:
            return root;
        }


        //Return 0 for now. We will change this when using databases
        public long getItemId(int position)
        {
            return 0;
        }
    }




}