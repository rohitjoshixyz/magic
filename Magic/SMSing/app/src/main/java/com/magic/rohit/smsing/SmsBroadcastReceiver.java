package com.magic.rohit.smsing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.magic.rohit.smsing.MainActivity.getContactName;
import static com.magic.rohit.smsing.SubActivity1.inst2;

/**
 * Created by rohit on 1/3/2018.
 */


public class SmsBroadcastReceiver extends BroadcastReceiver {


    public static String convertDate(long dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString();
    }

    public static final String SMS_BUNDLE = "pdus";
    SmsModel s1;

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                String smsBody = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();
                String contact = getContactName(context, address);
                String smsTime = convertDate(smsMessage.getTimestampMillis(),"hh:mm:ss");
                String smsDate = convertDate(smsMessage.getTimestampMillis(), "dd/MM/yyyy");

                String date=String.valueOf(smsMessage.getTimestampMillis());
                String time=String.valueOf(smsMessage.getTimestampMillis());
                Log.d("date", smsDate);
                Log.d("date", smsTime);


                s1 = new SmsModel(contact, address, smsBody, time, date);
            }


            Toast.makeText(context, "Message Received!", Toast.LENGTH_SHORT).show();


            SmsDatabase.getSmsDatabaseInstance().copyNewMessageToDatabase(s1);
            Intent i = new Intent(context, InboxActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);




        }


    }
}