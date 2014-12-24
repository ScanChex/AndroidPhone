package com.scanchex.ui;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class SCVideoPlayScreen extends BaseActivity {

	private String path;
	private VideoView videoview;
	ProgressBar progressBar1;
	String ticketid;
	TextView text;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.sc_video_screen);
		text = (TextView) findViewById(R.id.tickect_id);
		videoview = (VideoView) findViewById(R.id.video);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		path = getIntent().getExtras().getString("PATH");
		Log.i("VID PATH", "<><>" + path);
		ticketid = getIntent().getExtras().getString("tickectid");
		Log.v("tickect val in video", "tickect val in video" + ticketid);
		// videoview.setVideoPath(path);
		//text.setText(ticketid);
		// Start the MediaController
		MediaController mediacontroller = new MediaController(this);
		mediacontroller.setAnchorView(videoview);
		// Get the URL from String VideoURL
		Uri video = Uri.parse(path);
		videoview.setMediaController(mediacontroller);
		videoview.setVideoURI(video);

		videoview.requestFocus();
		videoview.setOnPreparedListener(new OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {
				progressBar1.setVisibility(View.GONE);
				videoview.start();
			}
		});

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
