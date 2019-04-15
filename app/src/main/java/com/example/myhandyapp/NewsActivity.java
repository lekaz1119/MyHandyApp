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

import com.example.myhandyapp.listitems.News;
import com.example.myhandyapp.sql.NewsDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class NewsActivity extends CommonActivity {
    ProgressBar progressBar;
    private NewsDataSource datasource;
    private List<News> newsList;
    protected ListAdapter adapter;

    private  int progress=0;
    //
    // private NewsDataSource dataSource;

    private static final String NEWS_CODE = "NewsCode";
    public static final int EMPTY_ACTIVITY = 345;
    public static final int SAVE_ACTIVITY = 300;

    public static final String NEWS_TITLE = "NewsTitle";
    public static final String NEWS_AUTHOR = "AUTHOR";
    public static final String NEWS_ARTICLE = "ARTICLE";
    public static final String NEWS_URL= "Link: ";
    public static final String NEWS_ID= "ID";
    public static final String NEWS_POSITION = "POSITION";
    public static final String API_TOKEN = "c2883701-aeb3-434c-b385-bccdefa5c806&";
    public static final String API_FORMAT = "format=json&";
    public static final String API_SEARCH = "sort=crawled&q=";


    String newsCode;
    private String serviceURL="http://webhose.io/filterWebContent?token="+ API_TOKEN + API_FORMAT +API_SEARCH;
    private static final int pause = 1800;
    EditText searchArticle;
    Button btnReset,btnSearch;


    /**
     *
     * @param savedInstanceState
     * method to that sets the news_activity layout
     * sets the adapter to the listview
     * provides functions for button clicks and also for clicking item on listview
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        Log.d("url",serviceURL);

        boolean isTablet = findViewById(R.id.frame_layout) != null;

        //initilize shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String savedNews = sp.getString(NEWS_CODE, "");

        searchArticle= findViewById(R.id.NewsCode);
        searchArticle.setText(savedNews);

        datasource = new NewsDataSource(this);
        datasource.open();

        newsList = datasource.getAllNews();


        btnReset= (Button)findViewById(R.id.btnReset);
        btnReset.setOnClickListener( b -> dispatchResetAction());

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener( b -> dispatchSearchAction());

        adapter = new MyArrayAdapter(newsList);
        ListView theList = findViewById(R.id.news_list);

        theList.setAdapter(adapter);

        SwipeRefreshLayout refresher = findViewById(R.id.refresh);
        refresher.setOnRefreshListener(()-> {
            refreshListAdapter();
            refresher.setRefreshing( false );
        });

        //show new item information including the link and the whole article
        theList.setOnItemClickListener(( list, item,  position,  id) -> {

            Log.d("you clicked on :" , "item "+ position + ", db_ID: " + id);

            News news = (News)adapter.getItem(position);
            Bundle dataToPass = new Bundle();

            dataToPass.putLong(NEWS_ID,news.getId());
            dataToPass.putString(NEWS_TITLE,news.getTitle());
            dataToPass.putString(NEWS_AUTHOR,news.getAuthor());
            dataToPass.putString(NEWS_ARTICLE,news.getNewsArticle());
            dataToPass.putString(NEWS_URL,news.getUrl());

            if (isTablet) {
                NewsFragment dFragment = new NewsFragment(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            } else //isPhone
            {
                Intent nextActivity = new Intent(NewsActivity.this, NewsEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
            }
        });

        progressBar = findViewById(R.id.newsProgressBar);


    }

    /**
     * method to refresh the adapter to notify changes in the listview
     */
    private void refreshListAdapter(){
        ((MyArrayAdapter) adapter).notifyDataSetChanged();
    }


    /**
     * method called when reset button is clicked
     * clears the list and removes the data from db
     */
    private void dispatchResetAction() {
        searchArticle.setText("");
        datasource = new NewsDataSource(this);
        datasource.open();

        //delete all news from DB
        for (News news : newsList ) {
            datasource.deleteNews(news.getId());
        }
        datasource.close();

        //clear the newslist
        newsList.clear();
        refreshListAdapter();

        Log.d("newsList Size :" , String.valueOf(newsList.size()));

        //clear shared preffs object
        removeSharedPreference(NEWS_CODE);

        //method called to hide the keyboard after the reset button is clicked
        hideKeyboard(NewsActivity.this);
        Toast.makeText(getApplicationContext(),  this.getResources().getString(R.string.reset_toast), Toast.LENGTH_SHORT).show();
    }

    /**
     * method executed when search button is pressed
     * it starts a new thread to load data for the searched request
     * also saves the searched word
     */
    private void dispatchSearchAction() {

        newsCode= searchArticle.getText().toString().trim();
        Log.d("code",newsCode);
        //show changes in the progress bar if news articles are found
        if (newsCode.length() > 0) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressBar.setMax(100);

            hideKeyboard(NewsActivity.this);

            //save to shared prefs
            saveSharedPreference(NEWS_CODE, newsCode);

            //string variable to store the url and the keyword typed by the user
            String serverUrl = serviceURL  + newsCode;
            Log.d("url",serverUrl);


            NewsQuery networkThread = new NewsQuery();

            progress = 30; //assume dataService takes 30%
            networkThread.execute( serverUrl); //this starts doInBackground on other thread

        }
        Log.d("you clicked on :" , "Button search");    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * method runs when the if the user clicks the delete button in the fragment
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if the results have been brought by newEmptyActivity
        if(requestCode == EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_CANCELED) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra(NEWS_ID, 0);
                int position = data.getIntExtra(NEWS_POSITION, 0);
                deleteMessageId(id, position);
            }
            //if save button is clicked
            else if (resultCode== RESULT_OK){

                String  title = data.getStringExtra(NEWS_TITLE);
                String  author = data.getStringExtra(NEWS_AUTHOR);
                String  article = data.getStringExtra(NEWS_ARTICLE);
                String  link = data.getStringExtra(NEWS_URL);

                saveMessageId(title,author,article,link);
            }
        }
    }

    /**
     *
     * @param id
     * @param position
     * method to delete the selected item on the list
     */
    public void deleteMessageId(long id, int position)
    {
        Log.d("Deleting ID :" , " id="+ id + " at position= "+position);

        if(newsList.size()==0){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_data_exist), Toast.LENGTH_SHORT).show();

        }
        //deletes the item from the db
        datasource.deleteNews(id);
        //removes the item from the list
        newsList.remove(position);
        Log.d("newsList Size :" , String.valueOf(newsList.size()));
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_deleted), Toast.LENGTH_SHORT).show();
        //notify the adapter about the changes
        refreshListAdapter();
    }

    //saves the message in the database when save button is clicked
    public void saveMessageId( String title,String author,String article, String url){
        Log.d("saving message",title);
        News news =datasource.createNewsArticle(title,author,article,url);
        newsList.add(0,news);
        Log.d("newsList Size :" , String.valueOf(newsList.size()));
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();

        refreshListAdapter();


    }

    /**
     *Adapter that extends Common Adapter
     * @param <E>
     *     the adapter that populates the listview
     */
    protected class MyArrayAdapter<E> extends CommonAdapter<E> {

        //List<E> newsArray = null;

        private MyArrayAdapter(List<E> originalData) {
            super(originalData);
        }


        /**
         *
         * @param position
         * @param old
         * @param parent
         * @return
         * function to determine what each row should look like
         */
        @Override
        public View getView(int position, View old, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            //get the item on the current position
            News news = (News) this.getItem(position);
            String newsToShow ="  " + news.getTitle() + "  \n Author:  " + news.getAuthor() + "";

            View root = inflater.inflate(R.layout.news_item, parent, false);

            //get the textview and set News title and author on it
            TextView rowText = root.findViewById(R.id.newsTitle);
            rowText.setText( newsToShow );

            ImageButton btnNews = root.findViewById(R.id.btnNews);
            if(btnNews != null) btnNews.setFocusable(false);
            //Return the text view:
            return root;

        }

    }

    /**
     * AsyncTask to handle the data loading task
     * reads the JSON and calls the method to store it in the database
     */
    private class NewsQuery extends AsyncTask<String,Integer,String>{

        protected String doInBackground(String ... params){
            try {

            String newsString = params[0];
            //establish the connection
            URL newsURL = new URL(newsString);
                HttpURLConnection urlConnection = (HttpURLConnection) newsURL.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                InputStream inStream = urlConnection.getInputStream();

                //preparing to read the JSON
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                //method that reads the JSON
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                urlConnection.disconnect();
                publishProgress(progress); //tell android to call onProgressUpdate with 3 as parameter

                //get the JSON object from the loaded page
                JSONObject json = new JSONObject(result);
                //get the JSON array from the loaded page
                JSONArray jsonArray = json.getJSONArray("posts");
                Log.d("jsonArray arrive size:", String.valueOf(jsonArray.length()));

                //if data is found in the JSON array then process it
                if(jsonArray != null) {
                    processJsonArray(jsonArray);
                }

                //exeptions are caught if the JSON is not handled correctly
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return "Finished Task";
        }

        /**
         *  @param jsonArray
         * @throws JSONException
         * method that stores the JSON array in the database and populates the listview
         */
        private void processJsonArray (JSONArray jsonArray) throws JSONException {
            JSONObject jObject;
            //loops through multiple JSON arrays for the desired JSON object and JSON array
            for (int i=0; i < jsonArray.length(); i++) {
                jObject = jsonArray.getJSONObject(i);
                Log.d("jObject :", jObject.toString(1));
                //populate the list
                newsList.add(new News(jObject.getString("title"),jObject.getString("author"),
                        jObject.getString("text"),jObject.getString("url")));
                publishProgress(progress++);

            }
        }

        /**
         * updates the progress bar
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            //increasing the progress bar value
            progressBar.setProgress(values[0]);
            Log.d("AsyncTaskExample", "update progress bar:" + values[0]);
        }

        /**
         *
         * @param s
         * method that runs after the data is loaded on screen
         * shows if the data was successfully loaded or not
         */
        @Override
        protected void onPostExecute(String s) {
            //after the doInBackground method is done
            progressBar.setVisibility(View.INVISIBLE);
            refreshListAdapter();
            if(newsList.size() > 0)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_loaded_toast), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_news_found_toast), Toast.LENGTH_LONG).show();
        }

        /**
         * method to pause the thread for a few seconds to show the progress bar
         */
        private void pause(){
            try {
                Log.d("Sleeping ", String.valueOf(pause));
                Thread.sleep(pause); //pause for few milliseconds to watch the progress bar update
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }


}