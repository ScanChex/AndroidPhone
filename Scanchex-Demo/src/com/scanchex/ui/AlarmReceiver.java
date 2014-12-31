package com.scanchex.ui;

import com.scanchex.utils.Resources;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
 
    private static final String DEBUG_TAG = "AlarmReceiver";
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(DEBUG_TAG, "Recurring alarm; receiver.");
        // start the download
//      Intent login = new Intent(context, SCLoginScreen.class);
//      login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//      
     // context.startService(login);
      Resources.getResources().setLaunchloginactivity(true);
    
    //  context.startActivity(login);
    }
 
}
