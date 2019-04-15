package com.example.myhandyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * This activity send the information to the fragment class.
 *
 */
public class DictionaryEmptyActivity extends AppCompatActivity {

    /**
     * thisis used when fragment is loaded in the phone
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        DictionaryFragment dFragment = new DictionaryFragment();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        dFragment.setTablet(false); //tell the Fragment that it's on a phone.
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentLocation, dFragment)
                .addToBackStack("AnyName")
                .commit();
    }

}
