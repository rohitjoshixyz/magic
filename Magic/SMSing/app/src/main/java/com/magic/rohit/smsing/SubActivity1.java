package com.magic.rohit.smsing;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.magic.rohit.smsing.MainActivity.getContactName;

public class SubActivity1 extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private static final String AUTHORITY ="com.magic.rohit.smsing.SmsDatabase";
    private static final String BASEPATH="SMS";





    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public static SubActivity1 inst2;
    SmsDatabase smsDatabase=new SmsDatabase(this);
    InboxActivity.SmsAdapter smsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        smsDatabase.getReadableDatabase();
        ArrayList<SmsModel>arrayList2;
        arrayList2=smsDatabase.getCategorizedMessages("carrier");
       // smsAdapter=new SmsAdapter(this,R.layout.smsmodel,arrayList2);




        //ArrayList<SmsModel> arrayList=new ArrayList<>();
        //arrayList2.add(new SmsModel("Rohit","8554801616","Hello,how are you?"));
        //arrayList.add(new SmsModel("Rohit","8554801616","Hello,how are you?"));
        //arrayList.add(new SmsModel("Rohit","8554801616","Hello,how are you?"));


        ListView listView=findViewById(R.id.smslist);
        listView.setAdapter(smsAdapter);


    }

    public void  deleteOldRecords()
    {smsDatabase.deleteOldRecord();}





    public static SubActivity1 instance2() {
        return inst2;
    }
    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        inst2 = this;
    }


    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }



    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
               return new CursorLoader(this, Uri.parse("content://com.magic.rohit.smsing/SmsDatabase"),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
    restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }




}
