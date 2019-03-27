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


        // get the delete button, and add a click listener:
        Button deleteButton = (Button)result.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener( clk -> {

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
        return result;
    }
}
