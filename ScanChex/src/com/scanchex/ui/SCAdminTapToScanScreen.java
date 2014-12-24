package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SCAdminTapToScanScreen extends Activity{
	
	public static boolean isFromAssetDetail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_tapto_scan_screen);
		
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
