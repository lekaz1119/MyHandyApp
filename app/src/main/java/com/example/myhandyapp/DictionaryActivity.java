package com.example.myhandyapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.example.myhandyapp.listitems.Dictionary;
import com.example.myhandyapp.listitems.Flight;
import com.example.myhandyapp.sql.DictionaryDataSource;
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

public class DictionaryActivity extends CommonActivity {

    ProgressBar progressBar;
    private List<Dictionary> dictionaryList;
    protected ListAdapter adt;
    private DictionaryDataSource datasource;
    private  int progress=0;

    private static final String LOOKUP_WORD = "LOOOKUP_WORD";
    private static final String API_KEY = "aebb5302-df83-4d43-b410-74074f5cdd24";

    public static final String ITEM_ID = "ID";
    public static final String ITEM_POSITION = "POSITION";
    public static final String DICT_WORD = "WORD";
    public static final String DICT_ENTRY_ID = "DICT_ENTRY_ID";
    public static final String WORD = "WORD";
    public static final String PART_OF_SPEECH = "PART_OF_SPEECH";
    public static final String PRONUNCIATION = "PRONUNCIATION";
    public static final String DEFINITIONS = "DEFINITIONS";
    public static final int EMPTY_ACTIVITY = 345;

    private String lookupWord = "pasta";
    private String serviceURL = "https://www.dictionaryapi.com/api/v3/references/collegiate/json/";

    /**
     * used to slowdown process and show progress bar updates
     */
    private static final int pause = 1700; //milliseconds

    Button btnReset;
    Button btnSearch;
    EditText txtLookupWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_activity);

        //check if the FrameLayout is loaded
        boolean isTablet = findViewById(R.id.dictFragmentLocation) != null;

        //initilize shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String savedWord = sp.getString(LOOKUP_WORD, "");

        // locate editText lookup word field
        txtLookupWord= findViewById(R.id.dictLookupWordTxt);
        txtLookupWord.setText(savedWord);

        datasource = new DictionaryDataSource(this);
        datasource.open();

        dictionaryList = datasource.getAllDefinitions();

        //reset button
        btnReset = findViewById(R.id.dictBtnReset);
        btnReset.setOnClickListener( b -> dispatchResetAction());

        //search button
        btnSearch = findViewById(R.id.dictBtnSearch);
        btnSearch.setOnClickListener( b -> dispatchSearchAction());

        adt = new DictionaryActivity.MyArrayAdapter(dictionaryList);

        ListView theList = findViewById(R.id.dict_list);
        SwipeRefreshLayout refresher = findViewById(R.id.dictRefresher);
        refresher.setOnRefreshListener(()-> {
            refreshListAdapter();
            refresher.setRefreshing( false );
        });

        theList.setAdapter(adt);

        //This listens for items being clicked in the list view
        theList.setOnItemClickListener(( list, item,  position,  id) -> {

            Log.d("you clicked on :" , "item "+ position + ", db_ID: " + id);
            Dictionary definition = (Dictionary) adt.getItem(position);
            Bundle dataToPass = new Bundle();

            dataToPass.putString(DICT_WORD, definition.getDictWord());
            dataToPass.putString(DICT_ENTRY_ID, definition.getEntryNumber());
            dataToPass.putString(WORD, definition.getWord());
            dataToPass.putString(PART_OF_SPEECH, definition.getPartOfSpeech());
            dataToPass.putString(PRONUNCIATION, definition.getPronunciation());
            dataToPass.putString(DEFINITIONS, definition.getDefinitions());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            if(isTablet)
            {
                DictionaryFragment dictFragment = new DictionaryFragment(); //add a DetailFragment
                dictFragment.setArguments( dataToPass ); //pass it a bundle for information
                dictFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.dictFragmentLocation, dictFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(DictionaryActivity.this, DictionaryEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
            }

        });

        progressBar = findViewById(R.id.dictProgressBar);
    }

    private void refreshListAdapter(){
        ((DictionaryActivity.MyArrayAdapter) adt).notifyDataSetChanged();
    }

    /**
     * action taken whe reset button is pushed
     * delete all records from Database
     * clear word definitions and all items from the screen
     * delete searched word from shared preferences
     */
    private void dispatchResetAction() {
        txtLookupWord.setText("");
        datasource = new DictionaryDataSource(this);
        datasource.open();

        //delete all definitions from DB
        for (Dictionary definition : dictionaryList ) {
            datasource.deleteDefinition(definition.getId());
        }
        datasource.close();

        dictionaryList.clear();
        refreshListAdapter();
        Log.d("dictionaryList Size :" , String.valueOf(dictionaryList.size()));

        //clear shared preffs object
        removeSharedPreference(LOOKUP_WORD);

        hideKeyboard(DictionaryActivity.this);
        Toast.makeText(getApplicationContext(),  this.getResources().getString(R.string.reset_toast), Toast.LENGTH_SHORT).show();
    }

    /**
     * action is taken when search button is pressed
     * show progressbar,
     * retrieve relevant dictionary word and process the data by adding it to the list
     * update progress bar
     */
    private void dispatchSearchAction() {
        lookupWord = txtLookupWord.getText().toString();

        if (lookupWord.length() > 0) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressBar.setMax(100);

            hideKeyboard(DictionaryActivity.this);

            //save to shared prefs
            saveSharedPreference(LOOKUP_WORD, lookupWord);

            String dictionaryURL = serviceURL + lookupWord + "?key="+ API_KEY;

            DictionaryActivity.DictionaryQuery networkThread = new DictionaryActivity.DictionaryQuery();

            progress = 30; //assume dataService takes 30%
            networkThread.execute( dictionaryURL); //this starts doInBackground on other thread

        }
        Log.d("you clicked on :" , "Search Button");
    }

    /**
     * This function only gets called on the phone. The tablet never goes to a new activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra(ITEM_ID, 0);
                int position = data.getIntExtra(ITEM_POSITION, 0);
                deleteDictionaryId(id, position);
            }
        }
    }

    public void deleteDictionaryId(long id, int position)
    {
        Log.d("Deleting ID :" , " id="+ id + " at position= "+position);
        datasource.deleteDefinition(id);
        dictionaryList.remove(position);
        Log.d("dictionaryList Size :" , String.valueOf(dictionaryList.size()));
        refreshListAdapter();
    }

    /**
     * A copy of ArrayAdapter. You just give it an array and it will do the rest of the work.
     * taken from Android labs
     * @param <E>
     */
    protected class MyArrayAdapter<E> extends CommonAdapter<E> {

        private MyArrayAdapter(List<E> originalData) {
            super(originalData);
        }

        public View getView(int position, View old, ViewGroup parent)
        {
            //get an object to load a layout:
            LayoutInflater inflater = getLayoutInflater();

            Dictionary definition = (Dictionary) this.getItem(position);
            String definitionToShow = definition.getDictWord();
//            definitionToShow +="   [ " + flight.getAirportFrom() + "   -->   " + flight.getAirportTo() + " ]";

            View root = inflater.inflate(R.layout.dictionary_item, parent, false);

            TextView rowText = root.findViewById(R.id.dictTextOnRow);
            rowText.setText( definitionToShow );

            ImageButton dictBtnIcon = root.findViewById(R.id.dictBtnIcon);
            if(dictBtnIcon != null) dictBtnIcon.setFocusable(false);
            //Return the text view:
            return root;
        }

    }

    /**
     * a subclass of AsyncTask                  Type1    Type2    Type3
     * calls FlightTracker Apis in the background, and processes the data
     * if flights found it addesm to the flightList and shows on the screen
     */
    private class  DictionaryQuery extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                //get the string url
                String strUrlDefinitions = params[0];
//                String strUrlDep = params[0];
                Log.d("strUrlDefinitions:", strUrlDefinitions);
//                Log.d("strUrl depart:", strUrlDep);

                // try to load dictionary definitions
                //create the network connection:
                URL dictURL = new URL(strUrlDefinitions);
                HttpURLConnection urlConnection = (HttpURLConnection) dictURL.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                InputStream inStream = urlConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                urlConnection.disconnect();
                publishProgress(progress); //tell android to call onProgressUpdate with 3 as parameter

                JSONArray jsonArray = new JSONArray(result);
                Log.d("jsonArray arrive size:", String.valueOf(jsonArray.length()));

                if (jsonArray != null) {
                    processJsonArray(jsonArray);
                }
                publishProgress(progress); //tell android to call onProgressUpdate with 3 as parameter

            } catch (Exception ex) {
                Log.e("Crash!!", ex.getMessage());
            }

            //return type 3, which is String:
            return "Finished task";
        }

        private void processJsonArray(JSONArray jsonArray) throws JSONException {
            JSONObject jObject;
            String meta_id;

            for (int i = 0; i < jsonArray.length(); i++) {
                jObject = jsonArray.getJSONObject(i);
                Log.d("jObject :", jObject.toString(1));

                meta_id = jObject.getJSONObject("meta").getString("id");
//                latLon+=", " + jObject.getJSONObject("geography").getString("longitude");

                Dictionary definition = new Dictionary();
                definition.setId(0);
                definition.setDictWord(jObject.getJSONObject("meta").getString("id"));
                definition.setEntryNumber(jObject.getJSONObject("meta").getString("uuid"));
                definition.setWord(jObject.getJSONObject("meta").getString("id"));
                definition.setPartOfSpeech(jObject.getString("fl"));
                definition.setPronunciation(jObject.getJSONObject("hwi").getString("hw"));
                definition.setDefinitions(jObject.getString("shortdef"));

                dictionaryList.add(definition);
                publishProgress(progress++);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            Log.d("AsyncTaskExample", "update progress bar:" + values[0]);
        }

        /**
         * shows tosat to user telling if definitions were found
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            refreshListAdapter();
            if(dictionaryList.size() > 0)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_loaded_toast), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_data_found_toast), Toast.LENGTH_LONG).show();
        }

        /**
         * used to slowdown process, only to demonstrate progress bar
         */
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