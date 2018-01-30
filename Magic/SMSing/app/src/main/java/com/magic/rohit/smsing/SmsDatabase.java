package com.magic.rohit.smsing;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Switch;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.magic.rohit.smsing.MainActivity.getContactName;
import static java.sql.Types.VARCHAR;

/**
 * Created by rohit on 1/4/2018.
 */

public class SmsDatabase extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "SMS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_DISPLAY_NAME = "DISPLAY_NAME";
    public static final String COLUMN_SMS_BODY = "SMS_BODY";
    public static final String COLUMN_SOURCE_NUMBER = "SOURCE_NUMBER";
    //public static final String COLUMN_DESTINATION_NUMBER = "DESTINATION_NUMBER";
    public static final String COLUMN_TIME = "TIME";
    public static final String COLUMN_DATE = "DATE";

    private static SmsDatabase _smsDatabase = null;

    public static SmsDatabase getSmsDatabaseInstance() {
        if (_smsDatabase == null) {
            _smsDatabase = new SmsDatabase(MainActivity.instance());
        }

        return _smsDatabase;
    }


    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SmsDatabase.db";

    public SmsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table " + TABLE_NAME +
                    " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_DISPLAY_NAME + " VARCHAR, "
                    + COLUMN_SOURCE_NUMBER + " VARCHAR, "
                    + COLUMN_SMS_BODY + " VARCHAR, "
                    + COLUMN_TIME + " VARCHAR, "
                    + COLUMN_DATE + " VARCHAR);");

            //  db.rawQuery("create table SMS ( ID INTEGER PRIMARY KEY AUTOINCREMENT, DISPLAY_NAME VARCHAR, SOURCE_NUMBER VARCHAR, SMS_BODY VARCHAR, TIME VARCHAR, DATE VARCHAR );",null);

            Log.d("sql", "Database created succesfully by rohit");

        } catch (Exception e) {
            Log.d("sql", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private SQLiteDatabase database;

    public void insertRecord(SmsModel sms) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DISPLAY_NAME, sms.getName());
        contentValues.put(COLUMN_SOURCE_NUMBER, sms.getNumber());
        //contentValues.put(COLUMN_DESTINATION_NUMBER, sms.getNumber());
        contentValues.put(COLUMN_SMS_BODY, sms.getBody());
        contentValues.put(COLUMN_TIME, sms.getTime());
        contentValues.put(COLUMN_DATE, sms.getDate());


        database.insert(TABLE_NAME, null, contentValues);
        if (InboxActivity.getInboxActivityInstance() != null)
            InboxActivity.getInboxActivityInstance().refreshAdapters();
        database.close();

    }

    public void insertRecordAlternate(SmsModel sms) {
        database = this.getReadableDatabase();
        database.execSQL("INSERT INTO " + TABLE_NAME + "(" + COLUMN_DISPLAY_NAME + "," + COLUMN_SOURCE_NUMBER + "," + COLUMN_SMS_BODY + "," + COLUMN_TIME + "," + COLUMN_DATE + ") VALUES('" + sms.getName() + "','" + sms.getNumber() + "','" + sms.getBody() + "'" + ",'" + sms.getTime() + "'," + sms.getDate() + "')");
        database.close();

    }

    public void updateRecord(SmsModel sms) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DISPLAY_NAME, sms.getName());
        contentValues.put(COLUMN_SOURCE_NUMBER, sms.getNumber());
        contentValues.put(COLUMN_SMS_BODY, sms.getBody());
        //contentValues.put(COLUMN_DESTINATION_NUMBER,null);
        database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(sms.getId())});
        database.close();
    }

    /*
        public void updateRecordAlternate(SmsModel sms) {
            database = this.getReadableDatabase();
            database.execSQL("UPDATE " + TABLE_NAME + "SET (" + COLUMN_DISPLAY_NAME + "='" + sms.getName() + "'," + COLUMN_SOURCE_NUMBER + ") VALUES('" + sms.getName() + "','" + sms.getNumber() + "','"+sms.getBody()+"')");
            database.close();

        }
    incomplete
    */
    public void deleteRecord(SmsModel sms) {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(sms.getId())});
        database.close();

        Uri uri = Uri.parse("content://sms/inbox");
        ContentResolver contentResolver = InboxActivity.getInboxActivityInstance().getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(uri, null, "", null, null);
        contentResolver.delete(uri,"address=? , body=?",new String[]{sms.getNumber(),sms.getBody()});

    }



    public void deleteOldRecord() {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME,null,null);
        database.close();

    }


    public String OrLike(String[] strings) {
        String s = "";
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                s += "'" + strings[i] + "'";
            } else {
                s += " OR SMS_BODY LIKE '" + strings[i] + "'";
            }
        }
        Log.d("s=", s);
        return s;
    }


    public ArrayList<SmsModel> getCategorizedMessages(String SECTION) {
        database = this.getReadableDatabase();
        String[] filterBank = {"%visa%", "%bank%", "%transaction%", "%bill%", "%a/c%", "%balance%"};
        String[] filterOtp = {"%OTP%", "%verification%", "%code%", "%one time password%", "%verify%", "%password%"};
        String[] filterCarrier = {"%Idea%", "%vodafone%", "%%airtel%",};
        Cursor cursor = null;


        switch (SECTION) {
            case "otp": {

                cursor = fetchNamesByConstraint(OrLike(filterOtp));
                break;
            }

            case "carrier": {
                for (int i = 0; i < filterCarrier.length; i++) {
                    cursor = fetchNamesByConstraint(OrLike(filterCarrier));
                }
                break;
            }

            case "bank": {
                for (int i = 0; i < filterBank.length; i++) {
                    cursor = fetchNamesByConstraint(OrLike(filterBank));
                }
                break;
            }

            case "promo":
            case "": {
                return getAllRecords();
            }

        }


        ArrayList<SmsModel> smslist = new ArrayList<SmsModel>();
        SmsModel smsModel;

        if (cursor != null && cursor.getCount() > 0)

        {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                smsModel = new SmsModel();
                smsModel.setId(Integer.parseInt(cursor.getString(0)));
                smsModel.setName(cursor.getString(1));
                smsModel.setNumber(cursor.getString(2));
                smsModel.setBody(cursor.getString(3));
                smsModel.setTime(cursor.getString(4));
                smsModel.setDate(cursor.getString(5));
                smslist.add(0, smsModel);
            }
            cursor.close();
        }

        database.close();
        return smslist;
    }

    public void copyNewMessageToDatabase(SmsModel s1) {

        _smsDatabase.insertRecord(s1);


    }

    public Cursor fetchNamesByConstraint(String filter) {
        database = this.getReadableDatabase();
        /*Cursor cursor = database.query(true, TABLE_NAME, new String[]{COLUMN_ID, COLUMN_DISPLAY_NAME, COLUMN_SOURCE_NUMBER, COLUMN_SMS_BODY,COLUMN_TIME,COLUMN_DATE
                }, COLUMN_SMS_BODY + " LIKE "+filter,null
                , null, null, COLUMN_TIME,
                null);*/
        Cursor cursor = database.rawQuery("SELECT DISTINCT ID, DISPLAY_NAME, SOURCE_NUMBER, SMS_BODY, TIME, DATE FROM SMS WHERE SMS_BODY LIKE " + filter + " ORDER BY TIME ASC", null);


        return cursor;
    }

/*public void deleteRecordAlternate(smsModel sms) {
    database = this.getReadableDatabase();
    database.execSQL("delete from " + TABLE_NAME + " where " + COLUMN_ID + " = '" + sms.getID() + "'");
    database.close();
}*/

    public ArrayList<SmsModel> getAllRecords() {
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM SMS ORDER BY TIME DESC;", null);
        ArrayList<SmsModel> smslist = new ArrayList<SmsModel>();
        SmsModel smsModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                smsModel = new SmsModel();
                smsModel.setId(Integer.parseInt(cursor.getString(0)));
                smsModel.setName(cursor.getString(1));
                smsModel.setNumber(cursor.getString(2));
                smsModel.setBody(cursor.getString(3));
                smsModel.setTime(cursor.getString(4));
                smsModel.setDate(cursor.getString(5));
                smslist.add(0, smsModel);
            }
        }
        cursor.close();
        database.close();
        return smslist;
    }

    public void abc() {
        SmsModel sms = new SmsModel();
        sms.setName("Rohit");
        sms.setNumber("8554801616");
        sms.setBody("Hello");
        insertRecord(sms);
    }


    public void deleteSMS(Context context, String message, String number) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(
                    uriSms,
                    new String[] { "_id", "thread_id", "address", "person",
                            "date", "body" }, "read=0", null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(3);
                    Log.e("log>>>",
                            "0>" + c.getString(0) + "1>" + c.getString(1)
                                    + "2>" + c.getString(2) + "<-1>"
                                    + c.getString(3) + "4>" + c.getString(4)
                                    + "5>" + c.getString(5));
                    Log.e("log>>>", "date" + c.getString(0));

                    if (message.equals(body) && address.equals(number)) {
                        // mLogger.logInfo("Deleting SMS with id: " + threadId);
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






}
