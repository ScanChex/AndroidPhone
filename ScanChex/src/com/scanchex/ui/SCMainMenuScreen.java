package com.scanchex.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCMainMenuScreen extends Activity{
	
	private TextView employeeName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_mainmenu_screen);
		employeeName = (TextView)findViewById(R.id.mainmenu_employeename_text);
		employeeName.setText(SCPreferences.getPreferences().getUserFullName(this));
	}
	
	
	public void onTicketsClick(View view) {
		
		Intent ticketView = new Intent(this, SCTicketViewScreen.class);
		startActivity(ticketView);
		
	}

	public void onViewMapClick(View view) {
		
		Intent intent = new Intent(this, SCViewMapScreen.class);
		startActivity(intent);
	}
	
	public void onLogoutClick(View view) {
		removeSchedule();
		this.finish();
	}
	
	private void removeSchedule(){
		 if(Resources.getResources().getpIntent()!=null){
			 Log.i("STOPPED", "STOPPED");
			AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			 am.cancel(Resources.getResources().getpIntent());
		 }
		 
	 }

}
