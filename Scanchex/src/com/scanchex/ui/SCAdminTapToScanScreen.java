package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.scanchex.utils.SCPreferences;

public class SCAdminTapToScanScreen extends BaseActivity{
	
	public static boolean isFromAssetDetail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_tapto_scan_screen);
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.adminTapScreen);
		layout.setBackgroundColor(SCPreferences.getColor(SCAdminTapToScanScreen.this));
		
	}
	
	public void onTaptoScanClick(View view){
		isFromAssetDetail = false;
		Intent intent = new Intent(this, SCAdminCameraPeviewScreen.class);
		startActivity(intent);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(isFromAssetDetail){
			isFromAssetDetail = false;
			this.finish();
		}
		
	}

}
