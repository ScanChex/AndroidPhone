package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ScAdminCheckinCheckoutMenu extends Activity {

	Activity mActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_checkin_menu_screen);
		mActivity = this;
	}

	public void onClickCheckout(View v) {
		Intent checkOut = new Intent(mActivity,ScAdminCheckoutTicketMenu.class);
		startActivity(checkOut);
		finish();
	}

	public void onClickCheckin(View v) {
		Intent checkIn = new Intent(mActivity,SCAdminCheckinTicketViewScreen.class);
		startActivity(checkIn);
		finish();
		
	}
	
	public void onClickBack(View v) {
		finish();
	}
}
