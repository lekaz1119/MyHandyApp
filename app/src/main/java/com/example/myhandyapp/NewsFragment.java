package com.example.myhandyapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class NewsFragment extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    /**
     * method to set if the fragment loaded is on the tablet or phone
     * @param tablet
     */
    public void setTablet(boolean tablet) { isTablet = tablet; }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *
     * loads the fragment layout
     * shows the item information
     * if the user clicks the delete button,
     * the delete method from NewActivity is called
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(NewsActivity.NEWS_ID );

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.news_fragment, container, false);

        //show the news header
        TextView newsTitle = (TextView)result.findViewById(R.id.title);
        newsTitle.setText(dataFromActivity.getString(NewsActivity.NEWS_TITLE));

        String lbl;

        //show the AIRPORT_FROM
        TextView newsAuthor = (TextView)result.findViewById(R.id.author);
        lbl = newsAuthor.getText().toString();

        newsAuthor.setText(lbl+dataFromActivity.getString(NewsActivity.NEWS_AUTHOR));

        //to show the url
        TextView url = (TextView)result.findViewById(R.id.url);
        String hyperLink= NewsActivity.NEWS_URL + dataFromActivity.getString(NewsActivity.NEWS_URL);
        url.setText(hyperLink);

        //opens the browser on clicking the link
        url.setOnClickListener(click->{
            Intent browser= new Intent(Intent.ACTION_VIEW);
            browser.setData(Uri.parse(hyperLink));
            startActivity(browser);
        });

        //show the
        TextView newArticle = (TextView)result.findViewById(R.id.article);
        newArticle.setText(dataFromActivity.getString(NewsActivity.NEWS_ARTICLE));

        // get the delete button
        Button deleteButton = (Button)result.findViewById(R.id.deleteButton);
        //deletes the news article from the database
        deleteButton.setOnClickListener( clk -> {


            if(isTablet) { //both the list and details are on the screen:
                NewsActivity parent = (NewsActivity) getActivity();

                //this deletes the item and updates the list
                parent.deleteMessageId((long)id, dataFromActivity.getInt(NewsActivity.NEWS_POSITION));

                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                NewsEmptyActivity parent = (NewsEmptyActivity) getActivity();
                Intent backToNewsActivity = new Intent();
                backToNewsActivity.putExtra(NewsActivity.NEWS_ID, dataFromActivity.getLong(NewsActivity.NEWS_ID ));
                backToNewsActivity.putExtra(NewsActivity.NEWS_POSITION, dataFromActivity.getInt(NewsActivity.NEWS_POSITION ));
                parent.setResult(Activity.RESULT_CANCELED, backToNewsActivity); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
        });

        Button saveButton = (Button)result.findViewById(R.id.saveButton);

        //save the article in the database
        saveButton.setOnClickListener( clk -> {

            if(isTablet) { //both the list and details are on the screen:
                NewsActivity parent = (NewsActivity) getActivity();

                //this deletes the item and updates the list
                parent.saveMessageId(dataFromActivity.getString(NewsActivity.NEWS_TITLE ),
                        dataFromActivity.getString(NewsActivity.NEWS_AUTHOR ),dataFromActivity.getString(NewsActivity.NEWS_ARTICLE ),
                        dataFromActivity.getString(NewsActivity.NEWS_URL ));

                //now remove the fragment:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                NewsEmptyActivity parent = (NewsEmptyActivity) getActivity();
                Intent backToNewsActivity = new Intent();
                //sends each info that needs to be stored in the class database to the NewActivity which will call the save method

                backToNewsActivity.putExtra(NewsActivity.NEWS_TITLE, dataFromActivity.getString(NewsActivity.NEWS_TITLE ));
                backToNewsActivity.putExtra(NewsActivity.NEWS_AUTHOR, dataFromActivity.getString(NewsActivity.NEWS_AUTHOR ));
                backToNewsActivity.putExtra(NewsActivity.NEWS_ARTICLE, dataFromActivity.getString(NewsActivity.NEWS_ARTICLE ));
                backToNewsActivity.putExtra(NewsActivity.NEWS_URL, dataFromActivity.getString(NewsActivity.NEWS_URL ));

                parent.setResult(Activity.RESULT_OK, backToNewsActivity); //send data back NewsActivity in onActivityResult()
                parent.finish(); //go back
            }
        });




        return result;
    }
}
