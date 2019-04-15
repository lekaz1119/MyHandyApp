package com.example.myhandyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.example.myhandyapp.listitems.Dictionary;
import com.example.myhandyapp.sql.DictionaryDataSource;
import com.example.myhandyapp.sql.FlightsDataSource;

import java.util.List;

public class DictionaryActivity extends CommonActivity {

    ProgressBar progressBar;
    private List<Dictionary> dictionaryList;
    protected ListAdapter adt;
    private DictionaryDataSource datasource;
    private  int progress=0;

    public static final String ITEM_ID = "ID";
    public static final String ITEM_POSITION = "POSITION";
    public static final String DICT_WORD = "WORD";
    public static final String DICT_ENTRY_ID = "DICT_ENTRY_ID";
    public static final String WORD = "WORD";
    public static final String PART_OF_SPEECH = "PART_OF_SPEECH";
    public static final String PRONUNCIATION = "PRONUNCIATION";
    public static final String DEFINITIONS = "DEFINITIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_activity);

        //check if the FrameLayout is loaded
        boolean isTablet = findViewById(R.id.dictFragmentLocation) != null;

//        //initilize shared preferences
//        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
//        String savedAirport = sp.getString(AIRPORT_CODE, "");
//
//        txtAirportCode= findViewById(R.id.txtAirportCode);
//        txtAirportCode.setText(savedAirport);

        datasource = new DictionaryDataSource(this);
        datasource.open();

        dictionaryList = datasource.getAllFlights();

    }

    private void refreshListAdapter(){
        ((DictionaryActivity.MyArrayAdapter) adt).notifyDataSetChanged();
    }

    public void deleteMessageId(long id, int position)
    {
        Log.d("Deleting ID :" , " id="+ id + " at position= "+position);
        datasource.deleteDefinition(id);
        dictionaryList.remove(position);
        Log.d("flightList Size :" , String.valueOf(dictionaryList.size()));
        refreshListAdapter();
    }


}