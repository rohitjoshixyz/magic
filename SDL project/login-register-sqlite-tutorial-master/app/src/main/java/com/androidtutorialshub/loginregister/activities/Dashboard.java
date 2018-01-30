package com.androidtutorialshub.loginregister.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;


import com.androidtutorialshub.loginregister.R;

public class Dashboard extends AppCompatActivity {
    private final AppCompatActivity activity = Dashboard.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView textView= (TextView) findViewById(R.id.link1);
        textView.setText(Html.fromHtml("<a href=\"http://www.sinhgad.edu\">Our website</a>"));

        textView.setMovementMethod(LinkMovementMethod.getInstance());

        }

    public void onCampusClicked(View v)
    {


        Intent intent=new Intent(activity,campus.class);
        startActivity(intent);
    }

        public void onNotificationsClicked(View v)
        {


            Intent intent=new Intent(activity,NotificationList.class);
            startActivity(intent);
        }

        public void onProfileClicked(View v)
        {
            Intent intent=new Intent(activity,UsersListActivity.class);
            startActivity(intent);
        }
















}
