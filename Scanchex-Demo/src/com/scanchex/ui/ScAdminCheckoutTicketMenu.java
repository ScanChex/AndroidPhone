package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ScAdminCheckoutTicketMenu extends BaseActivity {

	Activity mActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_checkout_ticket_menu);
		mActivity = this;
	}

	public void onClickScanCode(View v) {
		Intent scanCode = new Intent(mActivity,SCAdminScanScreen.class);
		scanCode.putExtra("scanCheck", "scanCheckOutCode");
		startActivityForResult(scanCode, 100);
	}

	public void onClickManualLookUp(View v) {

		Intent manualLook = new Intent(mActivity,ScAdminManualLookUp.class);
		startActivity(manualLook);
	}

	public void onClickTodayCheckout(View v) {
		Intent todaycheckout = new Intent(mActivity, ScAdminTodaycheckout.class);
		todaycheckout.putExtra("title", "TODAY'S CHECKOUT");
		startActivity(todaycheckout);
	}

	public void onClickBack(View v) {
		finish();
	}
	
 
}
