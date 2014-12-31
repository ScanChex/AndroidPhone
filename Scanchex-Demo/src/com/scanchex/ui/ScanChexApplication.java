package com.scanchex.ui;

import com.crittercism.app.Crittercism;

import android.app.Application;
import android.util.Log;

public class ScanChexApplication extends Application {

	  public static boolean isActivityVisible() {
	    return activityVisible;
	  }  

	  public static void activityResumed() {
	    activityVisible = true;
	  }

	  public static void activityPaused() {
	    activityVisible = false;
	  }

	  private static boolean activityVisible;
	  
	  @Override
		public void onCreate() {
			// TODO Auto-generated method stub
			super.onCreate();
		

			Crittercism.initialize(getApplicationContext(), "54a11d343cf56b9e0457cd33");

		}

	}
