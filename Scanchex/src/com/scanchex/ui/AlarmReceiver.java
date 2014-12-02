package com.scanchex.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
 
    private static final String DEBUG_TAG = "AlarmReceiver";
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(DEBUG_TAG, "Recurring alarm; requesting login service.");
        // start the download
        Intent login = new Intent(context, SCLoginScreen.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        
       // context.startService(login);
        
        context.startActivity(login);
    }
 
}
