package com.scanchex.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class SCVideoPlayScreen extends Activity {

	private String path;	
	private VideoView video;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.sc_video_screen);
		
		video = (VideoView) findViewById(R.id.video);
		path = getIntent().getExtras().getString("PATH");
		Log.i("VID PATH", "<><>" + path);

		video.setVisibility(View.VISIBLE);
		MediaController mc = new MediaController(this);
		mc.setAnchorView(video);
		mc.setMediaPlayer(video);
		Uri uri = Uri.parse(path);
		video.setMediaController(mc);
		video.setVideoURI(uri);
		video.start();

	}

}
