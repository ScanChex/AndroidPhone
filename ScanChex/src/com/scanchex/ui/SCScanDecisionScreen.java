package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCScanDecisionScreen extends Activity {

	private TextView employeeName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_scandecision_screen);
		employeeName = (TextView)findViewById(R.id.mainmenu_employeename_text);
		employeeName.setText(SCPreferences.getPreferences().getUserFullName(this));
	}

	public void onScanTagClick(View view) {
		Resources.getResources().setForDoubleScan(false);
		Intent i = new Intent(this, SCCameraPeviewScreen.class);
		startActivity(i);
	}

	public void onBackClick(View view) {
			
		this.finish();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}

}
