package com.rohit.smsing2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;

import static com.rohit.smsing2.MainActivity.getContactName;
import static com.rohit.smsing2.MmsBroadcastReceiver.SMS_BUNDLE;

/**
 * Created by admin on 1/15/2018.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public  static int newSmsCount=0;

    public static final String SMS_BUNDLE = "pdus";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras=intent.getExtras();

        newSmsCount++;

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                String smsBody = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();
                String contact = getContactName(context, address);

                //String smsTime = convertDate(smsMessage.getTimestampMillis(),"hh:mm:ss");
               // String smsDate = convertDate(smsMessage.getTimestampMillis(), "dd/MM/yyyy");


                String date=String.valueOf(smsMessage.getTimestampMillis());
                String time=String.valueOf(smsMessage.getTimestampMillis());
               // Log.d("date", smsDate);
                //Log.d("date", smsTime);


               SmsModel s1 = new SmsModel("",contact, address, smsBody, time, date,"0");

            }







         }
    }
}
