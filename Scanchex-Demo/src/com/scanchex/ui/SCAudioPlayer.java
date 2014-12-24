package com.scanchex.ui;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.scanchex.utils.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class SCAudioPlayer extends BaseActivity implements OnCompletionListener,
		SeekBar.OnSeekBarChangeListener, OnBufferingUpdateListener {

	String link;
	private Context context;

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	TextView text;
	private SeekBar songProgressBar;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	String ticketid;
	private MediaPlayer mediaPlayer;
	private int mediaFileLengthInMilliseconds;
	private final Handler handler = new Handler();
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_audio_player);

		Log.v("tickect val in audio", "tickect val in audio" + ticketid);
		context = this;
		utils = new Utilities();
		link = this.getIntent().getExtras().getString("PATH");
		ticketid = this.getIntent().getExtras().getString("ticketid");
		initControls();

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

	private void initControls() {

		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		text = (TextView) findViewById(R.id.tickect_id);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

		songProgressBar.setOnSeekBarChangeListener(this);
		// songProgressBar.setMax(99); // It means 100% .0-99
		// songProgressBar.setOnTouchListener(this);
		text.setText(ticketid);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);

		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/**
				 * ImageButton onClick event handler. Method which start/pause
				 * mediaplayer playing
				 */
				try {
					mediaPlayer.setDataSource(link); // setup song from
					// http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3
					// URL to mediaplayer data source
					mediaPlayer.prepare();

				} catch (Exception e) {
					e.printStackTrace();
				}

				mediaFileLengthInMilliseconds = mediaPlayer.getDuration();

				if (!mediaPlayer.isPlaying()) {
					mediaPlayer.start();

					btnPlay.setImageResource(R.drawable.btn_pause);
				} else {
					mediaPlayer.pause();
					btnPlay.setImageResource(R.drawable.btn_play);
				}

				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);

				// Updating progress bar
				updateProgressBar();
				// primarySeekBarProgressUpdater();
			}
		});

		btnForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mediaPlayer.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if (currentPosition + seekForwardTime <= mediaPlayer
						.getDuration()) {
					// forward song
					mediaPlayer.seekTo(currentPosition + seekForwardTime);
				} else {
					// forward to end position
					mediaPlayer.seekTo(mediaPlayer.getDuration());
				}
			}
		});

		/**
		 * Backward button click event Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mediaPlayer.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if (currentPosition - seekBackwardTime >= 0) {
					// forward song
					mediaPlayer.seekTo(currentPosition - seekBackwardTime);
				} else {
					// backward to starting position
					mediaPlayer.seekTo(0);
				}

			}
		});
	}

	public void updateProgressBar() {
		handler.postDelayed(mUpdateTimeTask, 100);
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mediaPlayer.getDuration();
			long currentDuration = mediaPlayer.getCurrentPosition();

			// Displaying Total Duration time
			songTotalDurationLabel.setText(""
					+ utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			songCurrentDurationLabel.setText(""
					+ utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			// Log.d("Progress", ""+progress);
			songProgressBar.setProgress(progress);

			// Running this thread after 100 milliseconds
			handler.postDelayed(this, 100);
		}
	};

	/*
	 * private void primarySeekBarProgressUpdater() {
	 * 
	 * // This math construction give a percentage of //
	 * "was playing"/"song length"
	 * 
	 * songProgressBar.setProgress((int) (((float) mediaPlayer
	 * .getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
	 * 
	 * if (mediaPlayer.isPlaying()) { Runnable notification = new Runnable() {
	 * public void run() { primarySeekBarProgressUpdater(); } };
	 * handler.postDelayed(notification, 1000); } }
	 */

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		songProgressBar.setSecondaryProgress(percent);

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		btnPlay.setImageResource(R.drawable.btn_play);

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		handler.removeCallbacks(mUpdateTimeTask);

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		handler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mediaPlayer.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);

		// forward or backward to certain seconds
		mediaPlayer.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}

	/*
	 * @Override public boolean onTouch(View v, MotionEvent event) {
	 * 
	 * if (v.getId() == R.id.songProgressBar) {
	 *//**
	 * Seekbar onTouch event handler. Method which seeks MediaPlayer to
	 * seekBar primary progress position
	 */
	/*
	 * if (mediaPlayer.isPlaying()) { SeekBar sb = (SeekBar) v; int
	 * playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100)
	 * sb.getProgress(); mediaPlayer.seekTo(playPositionInMillisecconds); } }
	 * return false; }
	 */
}
