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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhandyapp.listitems.NYT;
import com.example.myhandyapp.sql.NYTsDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * This is the main activity that inflates the main page and has methods to populate news list, save or delete news items from the
 * list and a class to get the data from the source page.
 */

public class NYTActivity extends CommonActivity {


    ProgressBar progressBar;
    private List<NYT> nytList;
    protected ListAdapter adt;
    private NYTsDataSource datasource;
    private  int progress=0;

    //image buttons below
    ImageButton btnSearch;
    ImageButton btnClear;
    EditText txtNYTSearch;

    public static final String ITEM_ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String LINK = "LINK";
    public static final String ARTICLE= "ARTICLE";

    public static final String ITEM_POSITION = "POSITION";
    public static final int NYT_EMPTY_ACTIVITY = 349;

    private static final String NYT_SEARCH_TERM = "NYT_SEARCH_TERM";
    private static final String API_KEY = "&api-key=OTyrA79aLgaH94RAznweFB3gUC7cdSbO";

    //this is used to slowdown so i can see progress bar
    private static final int pause = 1000;

    /**
     * This method gets the message from shared preference and showing on edit text
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nyt_activity);

        //check if the FrameLayout is loaded
        boolean isTablet = findViewById(R.id.fragmentNYT) != null;


        //shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String savedSearchTerm = sp.getString(NYT_SEARCH_TERM, "");



        txtNYTSearch = findViewById(R.id.txtNYTSearch);
        txtNYTSearch.setText(savedSearchTerm);

        /**
         * It is the search button. IT calls the search action if the search button gets clicked.
         */
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener( b -> searchAction());


        /**
         *  It is the clear button. It calls the resetAction if the clear button gets clicked.
         */
        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener( b -> resetAction());

        /**
         * It gets the data from datasource loading on listView
         */

        datasource = new NYTsDataSource(this);
        datasource.open();

        nytList = new ArrayList<NYT>();
        nytList = datasource.getAllNYTs();


        adt = new MyArrayAdapter(nytList);
        /**
         * Implements swipe-down functionality to refresh the list.
         * It is scroll the page. It is written base on the lab that we had
         */

        ListView theList = findViewById(R.id.nyt_list);
        SwipeRefreshLayout refresher = findViewById(R.id.refresher);
        refresher.setOnRefreshListener(()-> {
            refreshListAdapter();
            refresher.setRefreshing( false );
        });

        theList.setAdapter(adt);
        /**
         * It connects the list to the database.It populate the listView with data. it cause the listview to draw items
         * This listens for items being clicked in the list view.
         */
        theList.setOnItemClickListener(( list, item,  position,  id) -> {

            Log.d("you clicked on :" , "item "+ position + ", db_ID: " + id);
            NYT nyt = (NYT) adt.getItem(position);
            Bundle dataToPass = new Bundle();

            dataToPass.putLong(ITEM_ID, nyt.getId());
            dataToPass.putString(TITLE, nyt.getTitle());
            dataToPass.putString(LINK, nyt.getLink());
            dataToPass.putString(ARTICLE, nyt.getArticle());
            dataToPass.putInt(ITEM_POSITION, position);

            /**
             * From the variable isTablet if the fragment is loaded tells the fragment that is it is running on a tablet and load the fragment.
             * If it is not on tablet and it is on phone it will go to the NYTEmptyActivity
             *
             */

            if(isTablet)
            {
                NYTFragment dFragment = new NYTFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentNYT, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(NYTActivity.this, NYTEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, NYT_EMPTY_ACTIVITY); //make the transition
            }

        });
        progressBar = findViewById(R.id.nytProgressBar);

    }

    private void refreshListAdapter() {
        ((NYTActivity.MyArrayAdapter) adt).notifyDataSetChanged();

    }

    /**
     * This method get called when btnClear get clicked.It will delete the articles from the DB.
     * It removes the shared preference , clear the list,notify the adapter and remove data form the database.
     */
    private void resetAction() {
        txtNYTSearch.setText("");
        datasource = new NYTsDataSource(this);
        datasource.open();

        //delete all articles from DB
        for (NYT nyt : nytList ) {
            datasource.deleteNYT(nyt.getId());
        }
        datasource.close();

        nytList.clear();
        refreshListAdapter();

        //clear shared preference object
        removeSharedPreference(NYT_SEARCH_TERM);

        hideKeyboard(NYTActivity.this);
        Toast.makeText(getApplicationContext(),  this.getResources().getString(R.string.reset_toast), Toast.LENGTH_SHORT).show();
    }
    /**
     * It concatenates the search word to the URL and execute the query
     */

        private void searchAction() {
            String API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=";
            String searchTerm = txtNYTSearch.getText().toString();

            if (searchTerm.length() > 0) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                progressBar.setMax(100);

                hideKeyboard(NYTActivity.this);

                //save to shared prefs
                saveSharedPreference(NYT_SEARCH_TERM, searchTerm);

                String searchURL = null;
                try {
                    searchURL = API_URL + URLEncoder.encode(searchTerm, "UTF-8") + API_KEY;
                    NYTActivity.NYTQuery networkThread = new NYTActivity.NYTQuery();

                    progress = 20; //assume dataService takes 20%
                    networkThread.execute(searchURL); //this starts doInBackground on other thread

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("you entered :" , searchURL);
            }
    }

    /**
     * This method is not for tablet. This is for phone only and will be called on phone.
     * If the delete button is pushed it will delete the article base on it's ID and position.
     * IT gets result form fragment. If save button clicked, save it to the database.
     * if delete button clicked, delete form list.
     */
    //This function only gets called on the phone. The tablet never goes to a new activity
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode == NYT_EMPTY_ACTIVITY)
            {
                if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
                {
                    long id = data.getLongExtra(ITEM_ID, 0);
                    int position = data.getIntExtra(ITEM_POSITION, 0);
                    deleteArticleId(id, position);
                }
            }
        }

    /**
     * It deletes the itme at the postion.
     * @param id
     * @param position
     */
        public void deleteArticleId(long id, int position){
            Log.d("Deleting ID :" , " id="+ id + " at position= "+position);
            datasource.deleteNYT(id);
            nytList.remove(position);
            Log.d("article Size :" , String.valueOf(nytList.size()));
            refreshListAdapter();
        }

    //A copy of ArrayAdapter. You just give it an array and it will do the rest of the work.
    protected class MyArrayAdapter<E> extends CommonAdapter<E>
    {

        private MyArrayAdapter(List<E> originalData) {
            super(originalData);
        }

        //you need to implement this method individually, based on your list item
        public View getView(int position, View old, ViewGroup parent)
        {
            //get an object to load a layout:
            LayoutInflater inflater = getLayoutInflater();

            NYT nyt = (NYT)this.getItem(position);

            View root = inflater.inflate(R.layout.nyt_item, parent, false);

            TextView rowText = root.findViewById(R.id.textOnRow);
            rowText.setText( nyt.getTitle() );

            //Return the text view:
            return root;
        }

    }

    /**
     * This class takes care of the thread synchronization issues. It is a subclass of AsyncTask .
     * In doInBackground function we do any long-lasting computations, network access, file writing
     * It tells android to call onProgressUpdate with 3 as parameter
     *
     */
    // a subclass of AsyncTask                  Type1    Type2    Type3
    private class  NYTQuery extends AsyncTask<String, Integer, String>
{
    @Override
    protected String doInBackground(String ... params) {
    try {
        //get the string url
        String strUrl = params[0];
        Log.d("strUrl:", strUrl);


        //create the network connection:
        URL apiURL = new URL(strUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) apiURL.openConnection();
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

        JSONObject jObject = new JSONObject(result);
        String responseCode = jObject.getString("status");
        if(responseCode.equalsIgnoreCase("OK")){
            JSONArray jsonArray = jObject.getJSONObject("response").getJSONArray("docs");

            Log.d("jsonArray arrive size:", String.valueOf(jsonArray.length()));

            if(jsonArray != null) {
                showDocs(jsonArray);
            }
        }

        publishProgress(progress); //tell android to call onProgressUpdate with 3 as parameter
        //pause();

    }catch (Exception ex)
    {
        Log.e("Crash!!", ex.getMessage() );
    }

    //return type 3, which is String:
    return "Finished task";
}

    private void showDocs (JSONArray jsonArray) throws JSONException {
    JSONObject jObject;

    for (int i=0; i < jsonArray.length(); i++) {
        jObject = jsonArray.getJSONObject(i);
        //Log.d("jObject :", jObject.toString(1));

        NYT nyt = new NYT();
        nyt.setId(0);
        nyt.setTitle(jObject.getJSONObject("headline").getString("main"));
        nyt.setArticle(jObject.getString("lead_paragraph"));
        nyt.setLink(jObject.getString("web_url"));

        nytList.add(nyt);
        publishProgress(progress++);

    }
}

    /**
     *    It updates a progress indicators or information on your GUI
     */
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            Log.d("AsyncTaskExample", "update progress bar:" + values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            refreshListAdapter();
            if(nytList.size() > 0)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nyt_articles_found_toast), Toast.LENGTH_SHORT).show();// It shows a message when article is found for the user's search
            else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nyt_articles_notfound_toast), Toast.LENGTH_LONG).show();// It shows a message when no article is found for the user's search
        }

        private void pause(){
            try {
                Log.d("Sleeping ", String.valueOf(pause));
                Thread.sleep(pause); //pause for # of milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * It saves the last topic that was searched to display the next time the application is launched.
     */
    @Override
    protected void onResume() {
        super.onResume();
        //shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String savedSearchTerm = sp.getString(NYT_SEARCH_TERM, "");
        txtNYTSearch.setText(savedSearchTerm);
    }
}