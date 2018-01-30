package com.magic.rohit.smsing;

/**
 * Created by rohit on 1/4/2018.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class QuickResponseService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        return super.onStartCommand(intent,flags,startID);
    }
}