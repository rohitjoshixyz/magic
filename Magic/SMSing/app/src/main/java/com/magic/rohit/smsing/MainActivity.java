package com.magic.rohit.smsing;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.magic.rohit.smsing.SmsDatabase.COLUMN_DATE;
import static com.magic.rohit.smsing.SmsDatabase.COLUMN_DISPLAY_NAME;
import static com.magic.rohit.smsing.SmsDatabase.COLUMN_ID;
import static com.magic.rohit.smsing.SmsDatabase.COLUMN_SMS_BODY;
import static com.magic.rohit.smsing.SmsDatabase.COLUMN_SOURCE_NUMBER;
import static com.magic.rohit.smsing.SmsDatabase.COLUMN_TIME;
import static com.magic.rohit.smsing.SmsDatabase.TABLE_NAME;

public class MainActivity extends AppCompatActivity  {
    ArrayList<SmsModel> smsMessagesList = new ArrayList<>();
    ListView messages;




    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static MainActivity inst;
    EditText input;
    Uri uri=Uri.parse("content://sms/inbox");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages = findViewById(R.id.messages);



        //sendtoFirebase();


        input = (EditText) findViewById(R.id.input);
        getPermissionToReadSMS();

        String query="create table " + TABLE_NAME +
                " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DISPLAY_NAME + " VARCHAR, "
                + COLUMN_SOURCE_NUMBER + " VARCHAR, "
                + COLUMN_SMS_BODY + " VARCHAR, "
                + COLUMN_TIME + " VARCHAR, "
                + COLUMN_DATE + " VARCHAR );";
        Log.d("query",query);





      /* arrayAdapter = new SubActivity1.SmsAdapter(this,R.layout.smsmodel, smsMessagesList);
        messages.setAdapter(arrayAdapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            refreshSmsInbox();
        }*/
    }


    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS,Manifest.permission.READ_CONTACTS},READ_SMS_PERMISSIONS_REQUEST);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();


                //refreshSmsInbox();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    SmsManager smsManager = SmsManager.getDefault();

    public void onSendClick(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            EditText to = findViewById(R.id.to);
            String msg=input.getText().toString();
            if(!msg.isEmpty()) {
                smsManager.sendTextMessage(to.getText().toString(), null,msg , null, null);
                Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
            }
            else{

            }
        }
    }

    public void updateInbox(SmsModel s1) {
        //arrayAdapter.insert(s1, 0);
        //arrayAdapter.notifyDataSetChanged();
    }




    public static MainActivity instance() {
        return inst;
    }

    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        inst = this;
    }


    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public static String getContactName(Context context, String phoneNo) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNo));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phoneNo;
        }
        String Name = phoneNo;
        if (cursor.moveToFirst()) {
            Name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            Log.d("qwerty", "Contact Name: " + Name);
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return Name;

    }



    public void onNextClick(View view) {
        Intent intent = new Intent(this, InboxActivity.class);
        startActivity(intent);
    }

    public void load_inboxClicked(View view) {




    }


}



