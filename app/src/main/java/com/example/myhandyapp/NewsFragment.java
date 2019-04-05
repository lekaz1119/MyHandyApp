package com.example.myhandyapp;

import android.app.Activity;
import android.content.Intent;
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

    public void setTablet(boolean tablet) { isTablet = tablet; }
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

        TextView location = (TextView)result.findViewById(R.id.url);
        location.setText(NewsActivity.NEWS_URL + dataFromActivity.getString(NewsActivity.NEWS_URL));

        //show the AIRPORT_TO
        TextView airportTo = (TextView)result.findViewById(R.id.article);
        airportTo.setText(dataFromActivity.getString(NewsActivity.NEWS_ARTICLE));






        // get the delete button, and add a click listener:
        Button deleteButton = (Button)result.findViewById(R.id.deleteButton);
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
                Intent backToChatRoomActivity = new Intent();
                backToChatRoomActivity.putExtra(NewsActivity.NEWS_ID, dataFromActivity.getLong(NewsActivity.NEWS_ID ));
                backToChatRoomActivity.putExtra(NewsActivity.NEWS_POSITION, dataFromActivity.getInt(NewsActivity.NEWS_POSITION ));
                parent.setResult(Activity.RESULT_OK, backToChatRoomActivity); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
        });

        Button saveButton = (Button)result.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(click->{});

        return result;
    }
}
