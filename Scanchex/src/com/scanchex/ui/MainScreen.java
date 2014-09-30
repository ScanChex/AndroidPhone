package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class MainScreen extends Activity {
	
	String regId;
	private Typeface tf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_main_screen);
		this.tf = Typeface.createFromAsset(getBaseContext().getAssets(),
				"fonts/COPRGTB.TTF");
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.sc_mainScreenContainer);
		layout.setBackgroundColor(SCPreferences.getColor(MainScreen.this));
		TextView title = (TextView)findViewById(R.id.main_text);
		title.setTypeface(tf);
		
		 GCMRegistrar.checkDevice(this);
	     GCMRegistrar.checkManifest(this);
	     regId = GCMRegistrar.getRegistrationId(MainScreen.this);
	     Log.i("ID @ Main Screen", "Device regId = " + regId);
	     if (regId.equals("")) {
	    	 GCMRegistrar.register(MainScreen.this, GCMIntentService.senderId);
	     }else{
	    	 Resources.getResources().setPushNotificationId(regId);
	     }
	     
//		am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
	
	public void onLoginClick(View view){
		if(SCPreferences.getPreferences().getUserFullName(this).length()<=0){
			
			Intent loging = new Intent(this, SCLoginScreen.class);
			loging.putExtra("gcmRegId", regId);
			startActivity(loging);
		}else if(SCPreferences.getPreferences().getUserType(this) == CONSTANTS.USER_TYPE_EMPLOYEE){		
			
			Intent mainMenu = new Intent(this, SCMainMenuScreen.class);
			mainMenu.putExtra("NAME", SCPreferences.getPreferences().getUserFullName(this));
			startActivity(mainMenu);	
		}else{
			Intent mainMenu = new Intent(this, SCAdminMainMenuScreen.class);
			mainMenu.putExtra("NAME", SCPreferences.getPreferences().getUserFullName(this));
			startActivity(mainMenu);	
		}
	}
	
	public void onAboutClick(View view){
		
//		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://scanchex.com/about-scanchex/"));
//		startActivity(browserIntent);
		sendWebIntent("http://scanchex.com/about-scanchex/");
	}
	
	public void onPrivacyClick(View view){

//		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://scanchex.com/privacy-policy/"));
//		startActivity(browserIntent);
		sendWebIntent("http://scanchex.com/privacy-policy/");
	}
	
	public void onTermsClick(View view){

//		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://scanchex.com/terms-of-service/"));
//		startActivity(browserIntent);
		sendWebIntent("http://scanchex.com/terms-of-service/");
	}
	
	public void onContactUsClick(View view){

//		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://scanchex.com/contact-us/"));
//		startActivity(browserIntent);
		sendWebIntent("http://scanchex.com/contact-us/");
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
	
	public void sendWebIntent(String url) {
		Intent webActivity = new Intent(MainScreen.this,SCWebViewActivity.class);
		webActivity.putExtra("url", url);
		startActivity(webActivity);
	}

	
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.sc_mainScreenContainer);
		layout.setBackgroundColor(SCPreferences.getColor(MainScreen.this));
	}
}
