package com.scanchex.ui;

import java.util.Calendar;
import java.util.TimeZone;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BaseActivity extends Activity {

    private static int sessionDepth = 0;
    
    
    private PendingIntent pendingIntent;
    
    final static private long ONE_SECOND = 1000;
    final static private long ONE_MINUTE = ONE_SECOND * 60;
    final static private long TEN_MINUTES = ONE_MINUTE * 10;



    @Override
    protected void onStart() {
        super.onStart();       
        sessionDepth++;
        if (Resources.getResources().isFromBackground()) {
//        	if ( SCPreferences.getPreferences().getUserFullName(this).length()>0) {
        		if (Resources.getResources().isActivityafter10mins())  {
//        	//	fireAlarm();
        			Resources.getResources().setFromBackground(false);
        			Resources.getResources().setLaunchloginactivity(true);
//        			Log.i("Base Activity", "App in foreground after 10 mins ");
//        			 Resources.getResources().setActivityafter10mins(false);
//        			Intent i = new Intent(this, SCLoginScreen.class);
//        			startActivity(i);
//        		   
//        		}
//        	    	
        		}
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionDepth > 0)
            sessionDepth--;
        if (sessionDepth == 0) {
            // app went to background
    		Log.i("Base Activity", "App in background ");
    		if ( SCPreferences.getPreferences().getUserFullName(this).length()>0) {
    		fireAlarm();
    		Resources.getResources().setFromBackground(true);
    //		    Resources.getResources().setActivityafter10mins(true);
    		}
    		
        }
    }
    
    public void fireAlarm() {
    	  /**
    	   * call broadcost reciver
    	   */
    	  Calendar calendar = Calendar.getInstance();
    	  calendar.setTimeInMillis(System.currentTimeMillis());
    	  
    	    /* Retrieve a PendingIntent that will perform a broadcast */
          Intent alarmIntent = new Intent(BaseActivity.this, AlarmReceiver.class);
          pendingIntent = PendingIntent.getBroadcast(BaseActivity.this, 0, alarmIntent, 0);
          
          AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
          

          int interval = 10;

       //   manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
          
          manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 
        		  TEN_MINUTES, pendingIntent );
        
    	  
    	  
    	 }

    	
   
}
