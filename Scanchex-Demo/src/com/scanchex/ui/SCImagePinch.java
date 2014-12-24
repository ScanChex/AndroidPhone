package com.scanchex.ui;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.scanchex.utils.TouchImageView;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SCImagePinch extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_pinch);

		Intent intent = getIntent();
		String imageUrl = intent.getStringExtra("ImageUrl");

		TouchImageView imageView = (TouchImageView) findViewById(R.id.imageView);

		try {
			Picasso.with(this) //
					.load(imageUrl) //
					.placeholder(R.drawable.scan_chexs_logo) //
					.error(R.drawable.app_icon) //
					.into(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	  @Override
	    protected void onStart() {
	        super.onStart();       
	     
	        	if ( SCPreferences.getPreferences().getUserFullName(this).length()>0) {
	        		if (Resources.getResources().isLaunchloginactivity()  && Resources.getResources().isFromBackground())  {
	        	//	fireAlarm();
	        			Log.i("Base Activity", "App in foreground after 10 mins ");
	        			 Resources.getResources().setLaunchloginactivity(false);
	        			 Resources.getResources().setFromBackground(false);
	        			Intent i = new Intent(this, SCLoginScreen.class);
	        			startActivity(i);
	        		   
	        		}
	        	    	
	        		}
	      
	    }
}
