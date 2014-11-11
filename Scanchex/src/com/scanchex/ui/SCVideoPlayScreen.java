package com.scanchex.ui;

import android.app.Activity;
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

public class SCVideoPlayScreen extends Activity {

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

}
