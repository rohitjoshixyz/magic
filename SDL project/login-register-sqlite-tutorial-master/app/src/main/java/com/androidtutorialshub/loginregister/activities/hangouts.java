package com.androidtutorialshub.loginregister.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidtutorialshub.loginregister.R;

import java.util.ArrayList;
import java.util.Arrays;

public class hangouts extends AppCompatActivity {
    private final AppCompatActivity activity =hangouts.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangouts);
        ListView mainListView = (ListView) findViewById( R.id.mainListView );

        String[] hangouts = new String[]{"Bedi's Naan house", "Lipton", "CCD"};
        ArrayList<String> hangoutsList = new ArrayList<>();
        hangoutsList.addAll(Arrays.asList(hangouts));

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.simplerow, R.id.rowTextView, hangoutsList);
        mainListView.setAdapter(listAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    Intent myIntent = new Intent(activity, bediNaan.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 1) {
                    Intent myIntent = new Intent(activity, Lipton.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 2) {
                    Intent myIntent = new Intent(activity, ccd.class);
                    startActivityForResult(myIntent, 0);
                }
            }
        });


    }
}
