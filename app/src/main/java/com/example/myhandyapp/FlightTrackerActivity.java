package com.example.myhandyapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myhandyapp.listitems.Flight;
import com.example.myhandyapp.sql.FlightsDataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FlightTrackerActivity extends CommonActivity {
    ProgressBar progressBar;
    private List<Flight> flightList;
    private ListAdapter adt;
    private FlightsDataSource datasource;
    //used to slowdown process and show progress bar updates
    private static final int pause = 1800;
    private static final String AIRPORT_CODE = "AirportCode";
    private static final String API_KEY = "6922c9-61988f";
    private String airportCode;
    private String serviceURL = "http://aviation-edge.com/v2/public/flights?key="+API_KEY;
                                             //+"&depIata="+airportCode+"&arrIata="+airportCode;


    Button btnReset;
    Button btnSearch;//menu_flight_tracker
    EditText txtAirportCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_tracker_activity);

        //initilize shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String savedAirport = sp.getString(AIRPORT_CODE, "");

        txtAirportCode= findViewById(R.id.txtAirportCode);
        txtAirportCode.setText(savedAirport);

        datasource = new FlightsDataSource(this);
        datasource.open();

        flightList = datasource.getAllFlights();



        //reset button
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener( b -> dispatchResetAction());

        //search button
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener( b -> dispatchSearchAction());

        adt = new MyArrayAdapter(flightList);

        ListView theList = findViewById(R.id.message_list);
        SwipeRefreshLayout refresher = findViewById(R.id.refresher);
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

        progressBar = findViewById(R.id.flightProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(500);

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
        editor.remove(AIRPORT_CODE);
        //write it to disk:
        editor.apply();
        hideKeyboard(FlightTrackerActivity.this);

    }

    private void dispatchSearchAction() {
        airportCode = txtAirportCode.getText().toString();

        if (airportCode.length() > 0) {
            hideKeyboard(FlightTrackerActivity.this);

            //get an editor object
            SharedPreferences.Editor editor = sp.edit();
            //save what was typed under the name "ReserveName"
            editor.putString(AIRPORT_CODE, airportCode);
            //write it to disk:
            editor.apply();


            //flight = datasource.createFlight(airportCode, "test Airline");

            serviceURL += "&arrIata="+airportCode;//+"&arrIata="+airportCode;
            FlightQuery networkThread = new FlightQuery();
            networkThread.execute( serviceURL ); //this starts doInBackground on other thread

            ((MyArrayAdapter) adt).notifyDataSetChanged();
        }


        Log.e("you clicked on :" , "Button search");

    }


    @Override
    protected void onPause() { super.onPause();

        //get an editor object
        SharedPreferences.Editor editor = sp.edit();
        //save what was typed under the name "ReserveName"
        editor.putString(AIRPORT_CODE, String.valueOf(txtAirportCode.getText()));
        //write it to disk:
        editor.apply();
    }

    //A copy of ArrayAdapter. You just give it an array and it will do the rest of the work.
    //
    protected class MyArrayAdapter<E> extends CommonAdapter<E>
    {

        public MyArrayAdapter(List<E> originalData) {
            super(originalData);
        }

        public View getView(int position, View old, ViewGroup parent)
        {
            //get an object to load a layout:
            LayoutInflater inflater = getLayoutInflater();

            Flight flight = (Flight)this.getItem(position);

            String flightToShow = flight.getAirline();// flight.getDetails();
            View root = inflater.inflate(R.layout.flight_tracker_details, parent, false);

            TextView rowText = root.findViewById(R.id.textOnRow);
            rowText.setText( flightToShow );

            ImageButton btnPlane = root.findViewById(R.id.btnPlane);
            if(btnPlane != null) btnPlane.setFocusable(false);
            //Return the text view:
            return root;
        }


    }

    // a subclass of AsyncTask                  Type1    Type2    Type3
    private class FlightQuery extends AsyncTask<String, Integer, String>
    {
        String wind_speed;
        String min_temperature;
        String max_temperature;
        String current_temperature;
        String weather_icon;
        String uv_value;

        @Override
        protected String doInBackground(String ... params) {
            try {
                //get the string url:w
                String myUrl = params[0];
                Log.d("myUrl:", myUrl);
                //create the network connection:
                //Start of JSON reading

                //create the network connection:
                URL UVurl = new URL(myUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) UVurl.openConnection();
                //urlConnection.setReadTimeout(10000 /* milliseconds */);
                //urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                InputStream inStream = urlConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                int progress = 10;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                publishProgress(progress); //tell android to call onProgressUpdate with 3 as parameter
                String result = sb.toString();

                //now a JSON table:
                JSONObject jObject;
                JSONArray jsonArray = new JSONArray(result);
                Log.d("jsonArray size:", String.valueOf(jsonArray.length()));

                if(jsonArray != null) {
                    for (int i=0; i < jsonArray.length(); i++) {
                        jObject = jsonArray.getJSONObject(i);
                        Log.d("jObject :", jObject.toString(1));
                        Flight flight = new Flight();
                        flight.setAirport(jObject.getJSONObject("departure").getString("iataCode"));
                        flight.setAirline(jObject.getJSONObject("flight").getString("iataNumber"));
                        flightList.add(flight);
                        publishProgress(progress++);

                    }
                }

                //END of FlightsReadinf
                //pause();



            }catch (Exception ex)
            {
                Log.e("Crash!!", ex.getMessage() );
            }

            //return type 3, which is String:
            return "Finished task";
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            Log.d("AsyncTaskExample", "update progress bar:" + values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            //the parameter String s will be "Finished task" from line 27

//            currTemp.append(current_temperature);
//            minTemp.append(min_temperature);
//            maxTemp.append(max_temperature);
//            windSpeed.append(wind_speed);
//            uvIndex.append(uv_value);
            progressBar.setVisibility(View.INVISIBLE);
        }

        private void pause(){
            try {
                Log.d("Sleeping ", String.valueOf(pause));
                Thread.sleep(pause); //pause for # of milliseconds to watch the progress bar update
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}