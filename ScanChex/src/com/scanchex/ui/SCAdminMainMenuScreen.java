package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.scanchex.utils.Resources;

public class SCAdminMainMenuScreen extends Activity{
	
	private TextView employeeName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_mainmenu_screen);
		employeeName = (TextView)findViewById(R.id.mainmenu_employeename_text);
		employeeName.setText(getIntent().getExtras().getString("NAME"));
	}
	
	
	public void onUploadImageClick(View view) {
		
		Resources.getResources().setFromAdminTakePicture(true);
		Intent intent = new Intent(this, SCAdminTapToScanScreen.class);
		startActivity(intent);
	}

	public void onLockLocationClick(View view) {
		
		Resources.getResources().setFromAdminTakePicture(false);
		Intent intent = new Intent(this, SCAdminTapToScanScreen.class);
		startActivity(intent);
	}
	
	public void onLogoutClick(View view) {
		
		this.finish();
	}

}
