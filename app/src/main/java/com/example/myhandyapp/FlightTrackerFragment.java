package com.example.myhandyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhandyapp.listitems.Flight;
import com.example.myhandyapp.sql.FlightsDataSource;

public class FlightTrackerFragment extends Fragment {
    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    public void setTablet(boolean tablet) { isTablet = tablet; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(FlightTrackerActivity.ITEM_ID );

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.flight_tracker_fragment, container, false);

        //show the FLIGHT number
        TextView flightNum = (TextView)result.findViewById(R.id.flightNum);
        flightNum.setText(dataFromActivity.getString(FlightTrackerActivity.FLIGHT));

        //show the AIRPORT_FROM
        TextView airportFrom = (TextView)result.findViewById(R.id.airportFrom);
        airportFrom.setText(dataFromActivity.getString(FlightTrackerActivity.AIRPORT_FROM));

        //show the AIRPORT_TO
        TextView airportTo = (TextView)result.findViewById(R.id.airportTo);
        airportTo.setText(dataFromActivity.getString(FlightTrackerActivity.AIRPORT_TO));

        String lbl;
        //show the FLIGHT_LOCATION
        TextView location = (TextView)result.findViewById(R.id.location);
        lbl = location.getText().toString();
        location.setText(lbl + dataFromActivity.getString(FlightTrackerActivity.FLIGHT_LOCATION));

        //show the FLIGHT_SPEED
        TextView speed = (TextView)result.findViewById(R.id.speed);
        lbl = speed.getText().toString();
        speed.setText(lbl + dataFromActivity.getString(FlightTrackerActivity.FLIGHT_SPEED));

        //show the FLIGHT_ALTITUDE
        TextView altitude = (TextView)result.findViewById(R.id.altitude);
        lbl = altitude.getText().toString();
        altitude.setText(lbl + dataFromActivity.getString(FlightTrackerActivity.FLIGHT_ALTITUDE));

        //show the FLIGHT_STATUS
        TextView status = (TextView)result.findViewById(R.id.status);
        status.setText(dataFromActivity.getString(FlightTrackerActivity.FLIGHT_STATUS));

        Button saveButton = (Button)result.findViewById(R.id.saveButton);
        Button deleteButton = (Button)result.findViewById(R.id.deleteButton);

        // get the delete button, and add a click listener:
        deleteButton.setOnClickListener( clk -> {

            //isTablet is set to true if fragment is found ithe FlightTracke layout, it's on in the 720 layouts
            // in phone layouts, fragment appears separatly in new FlightTrackerFragment layout
            if(isTablet) { //both the list and details are on the screen:
                FlightTrackerActivity parent = (FlightTrackerActivity)getActivity();

                //this deletes the item and updates the list
                parent.deleteMessageId((long)id, dataFromActivity.getInt(FlightTrackerActivity.ITEM_POSITION));

                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                FlightTrackerEmptyActivity parent = (FlightTrackerEmptyActivity) getActivity();
                Intent backToChatRoomActivity = new Intent();
                backToChatRoomActivity.putExtra(FlightTrackerActivity.ITEM_ID, dataFromActivity.getLong(FlightTrackerActivity.ITEM_ID ));
                backToChatRoomActivity.putExtra(FlightTrackerActivity.ITEM_POSITION, dataFromActivity.getInt(FlightTrackerActivity.ITEM_POSITION ));
                parent.setResult(Activity.RESULT_OK, backToChatRoomActivity); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
        });


        /**
         * get the save button, and add a click listener:
         * used to save Flight item to database
         */
        saveButton.setOnClickListener( clk -> {
            FlightsDataSource datasource;
            datasource = new FlightsDataSource(getActivity().getApplicationContext());
            datasource.open();

            Flight flight = datasource.createFlight(
                    dataFromActivity.getString(FlightTrackerActivity.AIRPORT_FROM),
                    dataFromActivity.getString(FlightTrackerActivity.AIRPORT_TO),
                    dataFromActivity.getString(FlightTrackerActivity.FLIGHT),
                    dataFromActivity.getString(FlightTrackerActivity.FLIGHT_LOCATION),
                    dataFromActivity.getString(FlightTrackerActivity.FLIGHT_SPEED),
                    dataFromActivity.getString(FlightTrackerActivity.FLIGHT_ALTITUDE),
                    dataFromActivity.getString(FlightTrackerActivity.FLIGHT_STATUS));
            datasource.close();

            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.data_saved_toast), Toast.LENGTH_SHORT).show();
        });

        // if record already in DB, the id will be non zero, so we hide the add button, but show the delete button
        // otherwise, if id == 0 then record is not in DB, so we show ADD button and hide delete
        if(id == 0) {
            deleteButton.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
        }
        return result;
    }
}
