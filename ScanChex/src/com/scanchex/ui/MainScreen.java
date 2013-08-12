package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainScreen extends Activity {
//	PendingIntent pi;
//	AlarmManager am;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_main_screen);
//		am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
	
	public void onLoginClick(View view){
		Intent loging = new Intent(this, SCLoginScreen.class);
		startActivity(loging);
	}
	
	public void onAboutClick(View view){
		
		Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
//		scheduleInvokerAndroidSyncService();
	}
	
	public void onPrivacyClick(View view){
		Toast.makeText(this, "Privacy", Toast.LENGTH_SHORT).show();
//		removeSchedule();
	}
	
	public void onTermsClick(View view){
		Toast.makeText(this, "Terms of Use", Toast.LENGTH_SHORT).show();
	}
	
	public void onContactUsClick(View view){
		Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show();
	}
	
	
	
//	private void scheduleInvokerAndroidSyncService(){
//		
//		 pi = PendingIntent.getService(this, 1981, new Intent(this, AndroidSyncService.class), PendingIntent.FLAG_CANCEL_CURRENT);
//		 Resources.getResources().setpIntent(pi);
//		 am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+5000, 8*1000, pi);
//	 }
	
//	 private void removeSchedule(){
//		 if(Resources.getResources().getpIntent()!=null){
//			 Log.i("STOPPED", "STOPPED");
//			 am.cancel(Resources.getResources().getpIntent());
//		 }
//		 
//	 }

}
