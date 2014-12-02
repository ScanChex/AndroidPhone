package com.scanchex.ui;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCMainMenuScreen extends BaseActivity {

	private PendingIntent pi;
	private AlarmManager am;
	private TextView employeeName;

	private TextView noMessageText;
	private ImageView newMessageIcon, logo;
	LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_mainmenu_screen);
		Resources.getResources().setCurrentContext(SCMainMenuScreen.this);

		layout = (LinearLayout) findViewById(R.id.mainMenuScreen);
		layout.setBackgroundColor((SCPreferences
				.getColor(SCMainMenuScreen.this)));

		logo = (ImageView) findViewById(R.id.logo);
		noMessageText = (TextView) findViewById(R.id.nonew_message);
		newMessageIcon = (ImageView) findViewById(R.id.push_icon);
		employeeName = (TextView) findViewById(R.id.mainmenu_employeename_text);
		employeeName.setText(SCPreferences.getPreferences().getUserFullName(
				this));
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		scheduleInvokerAndroidSyncService();

		String url = SCPreferences.getPreferences().getClientLogo(
				SCMainMenuScreen.this);

		try{
		Picasso.with(this)
		.load(url) 
		.placeholder(R.drawable.scan_chexs_logo) 
		.error(R.drawable.app_icon) 
		.into(logo);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void onTicketsClick(View view) {

		Intent ticketView = new Intent(this, SCTicketViewScreen.class);
		startActivity(ticketView);
	}

	public void onViewMapClick(View view) {

		Intent intent = new Intent(this, SCViewMapScreen.class);
		startActivity(intent);
	}

	public void onCustomize(View v) {
		int color = 0xff000000;
		if (SCPreferences.getColor(SCMainMenuScreen.this) == 0) {
			color = 0xff000000;
		} else {
			color = SCPreferences.getColor(SCMainMenuScreen.this);
		}

		final AmbilWarnaDialog dialog = new AmbilWarnaDialog(
				SCMainMenuScreen.this, color, new OnAmbilWarnaListener() {

					@Override
					public void onOk(AmbilWarnaDialog arg0, int arg1) {

						SCPreferences.setColor(SCMainMenuScreen.this, arg1);
						layout.setBackgroundColor((SCPreferences
								.getColor(SCMainMenuScreen.this)));
					}

					@Override
					public void onCancel(AmbilWarnaDialog arg0) {

					}
				});

		dialog.show();
	}

	public void onClickCard(View v) {
		Intent card = new Intent(SCMainMenuScreen.this,SCCardWebView.class);
		startActivity(card);
	}

	public void onLogoutClick(View view) {
		removeUserPrefrencesData();
		removeSchedule();
		this.finish();
	}

	public void onNewMessageClick(View v) {

		newMessageIcon.setVisibility(View.GONE);
		noMessageText.setVisibility(View.VISIBLE);
		Intent ticketView = new Intent(this, SCMessageViewScreen.class);
		startActivity(ticketView);
	}

	public void onNoNewMessage(View v) {

		Intent ticketView = new Intent(this, SCMessageViewScreen.class);
		startActivity(ticketView);
	}

	private void removeSchedule() {
		if (Resources.getResources().getpIntent() != null) {

			Log.i("STOPPED Ping Service", "STOPPED Ping Service");
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			am.cancel(Resources.getResources().getpIntent());
		}

	}

	private void removeUserPrefrencesData() {

		// SCPreferences.getPreferences().setUserName(SCMainMenuScreen.this,
		// "");
		// SCPreferences.getPreferences().setCompanyId(SCMainMenuScreen.this,
		// "");
		SCPreferences.getPreferences().setUserMasterKey(SCMainMenuScreen.this,
				"");
		SCPreferences.getPreferences().setUserFullName(SCMainMenuScreen.this,
				"");
		SCPreferences.getPreferences().setClientLogo(SCMainMenuScreen.this, "");
		SCPreferences.setComapnyUserName(SCMainMenuScreen.this, "");
	}

	private void scheduleInvokerAndroidSyncService() {

		pi = PendingIntent.getService(this, 1981, new Intent(this,
				AndroidSyncService.class), PendingIntent.FLAG_CANCEL_CURRENT);
		Resources.getResources().setpIntent(pi);
		// am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		// SystemClock.elapsedRealtime()+5000, 30*1000, pi);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + 5000, 5 * 60 * 1000, pi);
	}

	public void showPushNotificationAlert(String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				newMessageIcon.setVisibility(View.VISIBLE);
				noMessageText.setVisibility(View.GONE);
			}
		});
		// noMessageText.setText(message);

	}

}
