package com.example.myhandyapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhandyapp.listitems.NYT;
import com.example.myhandyapp.sql.NYTsDataSource;


/**
 *  This class has the onCreateView that inside this method it inflates the layout for this fragment. shows the title, link and article.
 *   and get the delete button and then delete the article and update the list.
 *   After that it removes the fragment because it deleted it from the database.
 */
public class NYTFragment extends Fragment {
    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    /**
     * This method inflates the layout and shows the title, link article txt.
     * If it is on tablet both the list and details are on the screen.
     * If it is on the phone You are only looking at the details, you need to go back to the previous list page
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        dataFromActivity = getArguments();

        id = dataFromActivity.getLong(NYTActivity.ITEM_ID);

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.nyt_fragment, container, false);

        //show the title
        TextView title = (TextView) result.findViewById(R.id.nyt_title);
        title.setText(dataFromActivity.getString(NYTActivity.TITLE));

        //show the url link
        TextView link = (TextView) result.findViewById(R.id.nyt_link);

        //String url_link = "&lt;a href=\"" + dataFromActivity.getString(NYTActivity.LINK)  + "\"&lt;/a>URL</a> ";
        String url_link = dataFromActivity.getString(NYTActivity.LINK);
        link.setText(R.string.nyt_article_url);
        Log.d("URL txt: ",url_link);
        link.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url_link)));
        });

        //show the article text
        TextView article = (TextView) result.findViewById(R.id.nyt_article);
        article.setText(dataFromActivity.getString(NYTActivity.ARTICLE));


        // get the delete button, and add a click listener:
        Button deleteButton = (Button) result.findViewById(R.id.deleteNYT);
        deleteButton.setOnClickListener(clk -> {

            if (isTablet) { //both the list and details are on the screen:
                NYTActivity parent = (NYTActivity) getActivity();

                //this deletes the item and updates the list
                parent.deleteArticleId((long) id, dataFromActivity.getInt(NYTActivity.ITEM_POSITION));

                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                NYTEmptyActivity parent = (NYTEmptyActivity) getActivity();
                Intent nytActivity = new Intent();
                nytActivity.putExtra(NYTActivity.ITEM_ID, dataFromActivity.getLong(NYTActivity.ITEM_ID));
                nytActivity.putExtra(NYTActivity.ITEM_POSITION, dataFromActivity.getInt(NYTActivity.ITEM_POSITION));
                parent.setResult(Activity.RESULT_OK, nytActivity); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
        });


        // get the save button, and add a click listener:
        Button addButton = (Button) result.findViewById(R.id.addNYT);
            addButton.setOnClickListener( clk -> {
            NYTsDataSource datasource;
            datasource = new NYTsDataSource(getActivity().getApplicationContext());
            datasource.open();

            NYT nytItem = datasource.createNYT(
                    dataFromActivity.getString(NYTActivity.TITLE),
                    dataFromActivity.getString(NYTActivity.ARTICLE),
                    dataFromActivity.getString(NYTActivity.LINK));
            datasource.close();

                deleteButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.data_saved_NYT_toast), Toast.LENGTH_SHORT).show();

        });

        if(id == 0) {
            deleteButton.setVisibility(View.INVISIBLE);
            addButton.setVisibility(View.VISIBLE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.INVISIBLE);
        }
        return result;
    }
}
