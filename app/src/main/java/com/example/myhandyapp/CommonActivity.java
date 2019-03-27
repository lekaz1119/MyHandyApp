package com.example.myhandyapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.myhandyapp.listitems.ListItem;

import java.util.Arrays;
import java.util.List;

public abstract class CommonActivity extends AppCompatActivity {
    SharedPreferences sp;
    private int snackBarMessageID;
    private int dialogID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initilize shared preferences
        sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

    }

    protected static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //update menu to reflect current activity options
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            String currntActivity = this.getClass().getSimpleName();
            switch (currntActivity){
                case "FlightTrackerActivity":
                    menu.findItem(R.id.action_flight_tracker).setVisible(false);
                    menu.findItem(R.id.action_news).setVisible(true);
                    menu.findItem(R.id.action_nyt).setVisible(true);
                    menu.findItem(R.id.action_dictionary).setVisible(true);

                    snackBarMessageID=R.string.flight_tracker_about;
                    dialogID=R.layout.flight_tracker_dialog;
                    break;
                case "DictionaryActivity":
                    menu.findItem(R.id.action_flight_tracker).setVisible(true);
                    menu.findItem(R.id.action_news).setVisible(false);
                    menu.findItem(R.id.action_nyt).setVisible(true);
                    menu.findItem(R.id.action_dictionary).setVisible(true);

                    snackBarMessageID=R.string.dictionary_about;
                    dialogID=R.layout.dictionary_dialog;
                    break;
                case "NewsActivity":
                    menu.findItem(R.id.action_flight_tracker).setVisible(true);
                    menu.findItem(R.id.action_news).setVisible(false);
                    menu.findItem(R.id.action_nyt).setVisible(true);
                    menu.findItem(R.id.action_dictionary).setVisible(true);

                    snackBarMessageID=R.string.news_about;
                    dialogID=R.layout.news_dialog;
                    break;
                case "NYTActivity":
                    menu.findItem(R.id.action_flight_tracker).setVisible(true);
                    menu.findItem(R.id.action_news).setVisible(true);
                    menu.findItem(R.id.action_nyt).setVisible(false);
                    menu.findItem(R.id.action_dictionary).setVisible(true);

                    snackBarMessageID=R.string.nyt_about;
                    dialogID=R.layout.nyt_dialog;
                    break;
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                final Snackbar snackbar = getSnackbar();
                snackbar.setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if( snackbar != null && snackbar.isShown() ) {
                            snackbar.dismiss();
                        }
                    }
                });
                View snackbarView = snackbar.getView();
                TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(15);  // show multiple line
                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                break;
            case R.id.action_main_help:
                //Toast.makeText(getApplicationContext(), "change it as you need ", Toast.LENGTH_LONG).show();
                LayoutInflater inflater = this.getLayoutInflater();
                View v = inflater.inflate(dialogID,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(v);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
                break;
            case R.id.action_flight_tracker:
                //Give directions to go from this page, to flightTracker page
                Intent flightTrackerPage = new Intent(this, FlightTrackerActivity.class);
                //Now make the transition:
                startActivity( flightTrackerPage);
                break;
            case R.id.action_news:
                //Give directions to go from this page, to Dictionary page
                Intent newsPage = new Intent(this, NewsActivity.class);
                //Now make the transition:
                startActivity( newsPage);
                break;
            case R.id.action_nyt:
                //Give directions to go from this page, to flightTracker page
                Intent nytPage = new Intent(this, NYTActivity.class);
                //Now make the transition:
                startActivity( nytPage);
                break;
            case R.id.action_dictionary:
                //Give directions to go from this page, to Dictionary page
                Intent dictionaryPage = new Intent(this, DictionaryActivity.class);
                //Now make the transition:
                startActivity( dictionaryPage);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected Snackbar getSnackbar(){
        return  Snackbar.make(findViewById(android.R.id.content), this.getResources().getString(snackBarMessageID), Snackbar.LENGTH_LONG);
    }

    //clear shared prefs object
    protected void removeSharedPreference(String code){
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(code);
        editor.apply();
    }

    //add to shared prefs object
    protected void saveSharedPreference(String code, String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(code, value);
        editor.apply();
    }

    //A copy of ArrayAdapter. You just give it an array and it will do the rest of the work.
    protected abstract class CommonAdapter<E> extends BaseAdapter
    {
        protected List<E> dataCopy = null;

        //Keep a reference to the data:
        public CommonAdapter(List<E> originalData)
        {
            dataCopy = originalData;
        }

        //You can give it an array
        public CommonAdapter(E [] array)
        {
            dataCopy = Arrays.asList(array);
        }

        //Tells the list how many elements to display:
        public int getCount()
        {
            return dataCopy.size();
        }

        public E getItem(int position){
            return dataCopy.get(position);
        }

        //you need to implement this method individually, based on your list item
        public abstract View getView(int position, View old, ViewGroup parent);

        //Return DB id
        public long getItemId(int position)
        {
            return  ((ListItem)getItem(position)).getId();
        }
    }



}