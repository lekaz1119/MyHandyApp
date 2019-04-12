package com.example.myhandyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class NewsEmptyActivity extends AppCompatActivity {
    /**
     *
     * @param savedInstanceState
     * activity to load for a phone when a user clicks on a list item
     * loads the frame layout
     * sends information to newsFragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample


        NewsFragment dFragment = new NewsFragment();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        dFragment.setTablet(false); //tell the Fragment that it's on a phone.
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentLocation, dFragment)
                .addToBackStack("AnyName")
                .commit();
    }

}
