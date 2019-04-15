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
import android.widget.Toast;

import com.example.myhandyapp.listitems.Dictionary;
import com.example.myhandyapp.sql.DictionaryDataSource;

public class DictionaryFragment extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(DictionaryActivity.ITEM_ID);

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.dictionary_fragment, container, false);

        //show the dictionary definition word
        TextView dictWord = (TextView) result.findViewById(R.id.dictWord);
        dictWord.setText(dataFromActivity.getString(DictionaryActivity.DICT_WORD));

        String lbl;
        //show the dictionary definition entry number_
        TextView entryNumber = (TextView) result.findViewById(R.id.entryNumber);
        lbl = entryNumber.getText().toString();
        entryNumber.setText(lbl + dataFromActivity.getString(DictionaryActivity.DICT_ENTRY_ID));

        //show the dictionary word again
        TextView word = (TextView) result.findViewById(R.id.word);
        lbl = word.getText().toString();
        word.setText(lbl + dataFromActivity.getString(DictionaryActivity.WORD));

        //show the part of speech for the selected word
        TextView partOfSpeech = (TextView) result.findViewById(R.id.partOfSpeech);
        lbl = partOfSpeech.getText().toString();
        partOfSpeech.setText(lbl + dataFromActivity.getString(DictionaryActivity.PART_OF_SPEECH));

        //show the pronunciation of the word
        TextView pronunciation = (TextView) result.findViewById(R.id.pronunciation);
        pronunciation.setText(dataFromActivity.getString(DictionaryActivity.PRONUNCIATION));

        //show the pronunciation of the word
        TextView definitions = (TextView) result.findViewById(R.id.definitions);
        definitions.setText(dataFromActivity.getString(DictionaryActivity.DEFINITIONS));

        Button dictSaveButton = (Button) result.findViewById(R.id.dictSaveButton);
        Button dictDeleteButton = (Button) result.findViewById(R.id.dictDeleteButton);

        // get the delete button, and add a click listener:
        dictDeleteButton.setOnClickListener(clk -> {

            if (isTablet) { //both the list and details are on the screen:
                DictionaryActivity parent = (DictionaryActivity) getActivity();

                //this deletes the item and updates the list
                parent.deleteDictionaryId((long) id, dataFromActivity.getInt(DictionaryActivity.ITEM_POSITION));

                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                DictionaryEmptyActivity parent = (DictionaryEmptyActivity) getActivity();
                Intent backToChatRoomActivity = new Intent();
                backToChatRoomActivity.putExtra(DictionaryActivity.ITEM_ID, dataFromActivity.getLong(DictionaryActivity.ITEM_ID));
                backToChatRoomActivity.putExtra(DictionaryActivity.ITEM_POSITION, dataFromActivity.getInt(DictionaryActivity.ITEM_POSITION));
                parent.setResult(Activity.RESULT_OK, backToChatRoomActivity); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
        });

        // get the save button, and add a click listener:
        dictSaveButton.setOnClickListener(clk -> {
            DictionaryDataSource datasource;
            datasource = new DictionaryDataSource(getActivity().getApplicationContext());
            datasource.open();

            Dictionary definition = datasource.createDefinition(
                    dataFromActivity.getString(DictionaryActivity.DICT_WORD),
                    dataFromActivity.getString(DictionaryActivity.DICT_ENTRY_ID),
                    dataFromActivity.getString(DictionaryActivity.WORD),
                    dataFromActivity.getString(DictionaryActivity.PART_OF_SPEECH),
                    dataFromActivity.getString(DictionaryActivity.PRONUNCIATION),
                    dataFromActivity.getString(DictionaryActivity.DEFINITIONS));
            datasource.close();

            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.data_saved_Dict_toast), Toast.LENGTH_SHORT).show();
        });

        if (id == 0) {
            dictDeleteButton.setVisibility(View.INVISIBLE);
            dictSaveButton.setVisibility(View.VISIBLE);
        } else {
            dictDeleteButton.setVisibility(View.VISIBLE);
            dictSaveButton.setVisibility(View.INVISIBLE);
        }
        return result;
    }
}