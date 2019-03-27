package com.example.myhandyapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhandyapp.listitems.Flight;
import com.example.myhandyapp.sql.FlightsDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FlightTrackerActivity extends CommonActivity {
    ProgressBar progressBar;
    private List<Flight> flightList;
    protected ListAdapter adt;
    private FlightsDataSource datasource;
    private  int progress=0;
    private static final String AIRPORT_CODE = "AirportCode";
    private static final String API_KEY = "6922c9-61988f";
    private static final String PARAM_DEPART = "&depIata=";
    private static final String PARAM_ARRIVE = "&arrIata=";

    public static final String FLIGHT = "FLIGHT";
    public static final String AIRPORT_FROM = "AIRPORT_FROM";
    public static final String AIRPORT_TO = "AIRPORT_TO";
    public static final String FLIGHT_LOCATION= "FLIGHT_LOCATION";
    public static final String FLIGHT_SPEED = "FLIGHT_SPEED";
    public static final String FLIGHT_ALTITUDE = "FLIGHT_ALTITUDE";
    public static final String FLIGHT_STATUS = "FLIGHT_STATUS";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final int EMPTY_ACTIVITY = 345;

    private String airportCode;
    private String serviceURL = "http://aviation-edge.com/v2/public/flights?key="+API_KEY;
                                                         //+PARAM_DEPART+airportCode
                                                         //+PARAM_ARRIVE+airportCode;

    //used to slowdown process and show progress bar updates
    private static final int pause = 1800; //milliseconds

    Button btnReset;
    Button btnSearch;
    EditText txtAirportCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_tracker_activity);

        //check if the FrameLayout is loaded
        boolean isTablet = findViewById(R.id.fragmentLocation) != null;

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

        ListView theList = findViewById(R.id.flight_list);
        SwipeRefreshLayout refresher = findViewById(R.id.refresher);
        refresher.setOnRefreshListener(()-> {
            refreshListAdapter();
            refresher.setRefreshing( false );
        });

        theList.setAdapter(adt);

        //This listens for items being clicked in the list view
        theList.setOnItemClickListener(( list, item,  position,  id) -> {

            Log.d("you clicked on :" , "item "+ position + ", db_ID: " + id);
            Flight flight = (Flight) adt.getItem(position);
            Bundle dataToPass = new Bundle();

            dataToPass.putString(FLIGHT, flight.getFlight());
            dataToPass.putString(AIRPORT_FROM, flight.getAirportFrom());
            dataToPass.putString(AIRPORT_TO, flight.getAirportTo());
            dataToPass.putString(FLIGHT_LOCATION, flight.getLocation());
            dataToPass.putString(FLIGHT_SPEED, flight.getSpeed());
            dataToPass.putString(FLIGHT_ALTITUDE, flight.getAltitude());
            dataToPass.putString(FLIGHT_STATUS, flight.getStatus());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            if(isTablet)
            {
                FlightTrackerFragment dFragment = new FlightTrackerFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(FlightTrackerActivity.this, FlightTrackerEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
            }
            //refreshListAdapter();
        });


        progressBar = findViewById(R.id.flightProgressBar);
    }

    private void refreshListAdapter(){
        ((MyArrayAdapter) adt).notifyDataSetChanged();
    }

    private void dispatchResetAction() {
        txtAirportCode.setText("");
        datasource = new FlightsDataSource(this);
        datasource.open();
        flightList = datasource.getAllFlights();
        //delete all flights from DB
        for (Flight flight : flightList ) {
            datasource.deleteFlight(flight.getId());
        }
        datasource.close();

        refreshListAdapter();

        //clear shared preffs object
        removeSharedPreference(AIRPORT_CODE);

        hideKeyboard(FlightTrackerActivity.this);
        Toast.makeText(getApplicationContext(),  this.getResources().getString(R.string.reset_toast), Toast.LENGTH_SHORT).show();
    }

    private void dispatchSearchAction() {
        airportCode = txtAirportCode.getText().toString();

        if (airportCode.length() > 0) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressBar.setMax(100);

            hideKeyboard(FlightTrackerActivity.this);

            //save to shared prefs
            saveSharedPreference(AIRPORT_CODE, airportCode);

            String arriveURL = serviceURL + PARAM_ARRIVE + airportCode;
            String departURL = serviceURL + PARAM_DEPART + airportCode;

            FlightQuery networkThread = new FlightQuery();

            progress = 30; //assume dataService takes 30%
            networkThread.execute( arriveURL,  departURL); //this starts doInBackground on other thread

        }
        Log.d("you clicked on :" , "Button search");
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_loaded_toast), Toast.LENGTH_SHORT).show();
    }

    //This function only gets called on the phone. The tablet never goes to a new activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra(ITEM_ID, 0);
                int position = data.getIntExtra(ITEM_POSITION, 0);
                deleteMessageId((long)id, position);
            }
        }
    }

    public void deleteMessageId(long id, int position)
    {
        Log.d("Deleting ID :" , " id="+ id + " at position= "+position);
        datasource.deleteFlight(id);
        flightList.remove(position);
        //Log.d("flightList Size :" , String.valueOf(flightList.size()));
        refreshListAdapter();
    }

    //A copy of ArrayAdapter. You just give it an array and it will do the rest of the work.
    protected class MyArrayAdapter<E> extends CommonAdapter<E>
    {

        public MyArrayAdapter(List<E> originalData) {
            super(originalData);
        }

        //you need to implement this method individually, based on your list item
        public View getView(int position, View old, ViewGroup parent)
        {
            //get an object to load a layout:
            LayoutInflater inflater = getLayoutInflater();

            Flight flight = (Flight)this.getItem(position);
            String flightToShow = flight.getFlight();
            flightToShow +="   [ " + flight.getAirportFrom() + "   -->   " + flight.getAirportTo() + " ]";

            View root = inflater.inflate(R.layout.flight_tracker_item, parent, false);

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
        @Override
        protected String doInBackground(String ... params) {
            try {
                //get the string url
                String strUrlArr = params[0];
                String strUrlDep = params[0];
                Log.d("strUrl arrive:", strUrlArr);
                Log.d("strUrl depart:", strUrlDep);

                // --------------------   now load arriving flights
                //create the network connection:
                URL flightsURL = new URL(strUrlArr);
                HttpURLConnection urlConnection = (HttpURLConnection) flightsURL.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                InputStream inStream = urlConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                urlConnection.disconnect();
                publishProgress(progress); //tell android to call onProgressUpdate with 3 as parameter

                JSONArray jsonArray = new JSONArray(result);
                Log.d("jsonArray arrive size:", String.valueOf(jsonArray.length()));

                if(jsonArray != null) {
                    processJsonArray(jsonArray);
                }

                // --------------------   now load departing flights
                //create the network connection:
                flightsURL = new URL(strUrlDep);
                urlConnection = (HttpURLConnection) flightsURL.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                inStream = urlConnection.getInputStream();

                //create a JSON object from the response
                reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8), 8);
                sb = new StringBuilder();

                line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                urlConnection.disconnect();

                progress+=20;
                publishProgress(progress); //tell android to call onProgressUpdate with 3 as parameter

                jsonArray = new JSONArray(result);
                Log.d("jsonArray Depart size:", String.valueOf(jsonArray.length()));

                if(jsonArray != null) {
                    processJsonArray(jsonArray);
                }
                publishProgress(progress); //tell android to call onProgressUpdate with 3 as parameter
                //END of FlightsReadinf
                //pause();

            }catch (Exception ex)
            {
                Log.e("Crash!!", ex.getMessage() );
            }

            //return type 3, which is String:
            return "Finished task";
        }

        private void processJsonArray (JSONArray jsonArray) throws JSONException {
            JSONObject jObject;
            String latLon;

            for (int i=0; i < jsonArray.length(); i++) {
                jObject = jsonArray.getJSONObject(i);
                Log.d("jObject :", jObject.toString(1));

                latLon= jObject.getJSONObject("geography").getString("latitude");
                latLon+=", " + jObject.getJSONObject("geography").getString("longitude");

                Flight flight = datasource.createFlight(
                        jObject.getJSONObject("departure").getString("iataCode"),
                        jObject.getJSONObject("arrival").getString("iataCode"),
                        jObject.getJSONObject("flight").getString("iataNumber"),
                        latLon, // location
                        jObject.getJSONObject("speed").getString("horizontal"),
                        jObject.getJSONObject("geography").getString("altitude"),
                        jObject.getString("status"));

                flightList.add(flight);
                publishProgress(progress++);

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            Log.d("AsyncTaskExample", "update progress bar:" + values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            refreshListAdapter();
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