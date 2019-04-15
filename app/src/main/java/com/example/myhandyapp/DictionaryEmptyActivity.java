package com.example.myhandyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DictionaryEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        DictionaryFragment dictFragment = new DictionaryFragment();
        dictFragment.setArguments( dataToPass ); //pass data to the the fragment
        dictFragment.setTablet(false); //tell the Fragment that it's on a phone.
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.dictFragmentLocation, dictFragment)
                .addToBackStack("AnyName")
                .commit();
    }

}
