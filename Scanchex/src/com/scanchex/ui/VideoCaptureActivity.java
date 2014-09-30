package com.scanchex.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.utils.Network;
import com.scanchex.utils.WakeLocker;

public class VideoCaptureActivity extends Activity {

	Camera camera;
	ImageButton recordButton;
	ImageButton stopButton;
	FrameLayout cameraPreviewFrame;
	CameraPreview cameraPreview;
	MediaRecorder mediaRecorder; 
	File file;
	CountDownTimer c;
	Context context;
	View viewStop;;
	TextView timer;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle); 
		super.setContentView(R.layout.video_capture);
		context = this;
		this.cameraPreviewFrame = (FrameLayout) super
				.findViewById(R.id.camera_preview);
		this.recordButton = (ImageButton) super.findViewById(R.id.recordButton);
		this.stopButton = (ImageButton) super.findViewById(R.id.stopButton);
		timer = (TextView)findViewById(R.id.textViewTimer);
		this.toggleButtons(false);
		// we'll enable this button once the camera is ready
		this.recordButton.setEnabled(false);
		viewStop = stopButton;
	}

	void toggleButtons(boolean recording) {
		this.recordButton.setEnabled(!recording);
		this.recordButton.setVisibility(recording ? View.GONE : View.VISIBLE);
		this.stopButton.setEnabled(recording);
		this.stopButton.setVisibility(recording ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume(); 
		if (Network.haveNetworkConnection(VideoCaptureActivity.this) == true) {

			// initialize the camera in background, as this may take a while
			new AsyncTask<Void, Void, Camera>() {

				@SuppressLint("NewApi")
				@Override
				protected Camera doInBackground(Void... params) {
					try {
						Camera camera = Camera.open();
						return camera == null ? Camera.open(0) : camera;
					} catch (RuntimeException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(Camera camera) {
					if (camera == null) {
						Toast.makeText(VideoCaptureActivity.this, "Not Found",
								Toast.LENGTH_SHORT).show();
					} else {
						VideoCaptureActivity.this.initCamera(camera);
					}
				}
			}.execute();
		}
	}

	void initCamera(Camera camera) {
		// we now have the camera
		this.camera = camera;
		this.camera.setDisplayOrientation(90);
		// create a preview for our camera
		this.cameraPreview = new CameraPreview(VideoCaptureActivity.this,
				this.camera, null, null);

		// add the preview to our preview frame
		this.cameraPreviewFrame.addView(this.cameraPreview, 0);
		// enable just the record button
		this.recordButton.setEnabled(true);
	}

	void releaseCamera() {
		if (this.camera != null) {
			this.camera.lock(); // unnecessary in API >= 14
			this.camera.stopPreview();
			this.camera.release();
			this.camera = null;
			this.cameraPreviewFrame.removeView(this.cameraPreview);
		}
	}

	void releaseMediaRecorder() {
		if (this.mediaRecorder != null) {
			this.mediaRecorder.reset(); // clear configuration (optional here)
			this.mediaRecorder.release();
			this.mediaRecorder = null;
		}
	}

	void releaseResources() {
		this.releaseMediaRecorder();
		this.releaseCamera();
		stopRecording(viewStop);
	}

	@Override
	public void onPause() {
		super.onPause(); 

		this.releaseResources();
	}

	// gets called by the button press
	@SuppressLint("NewApi")
	public void startRecording(final View v) {

		WakeLocker.acquire(context);
		this.camera.unlock(); // unnecessary in API >= 14

		this.mediaRecorder = new MediaRecorder();
		this.mediaRecorder.setCamera(this.camera);
		this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		this.mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		this.mediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_LOW));
		this.mediaRecorder.setOutputFile(this.getOutputMediaFile().getAbsolutePath());
		this.mediaRecorder.setPreviewDisplay(this.cameraPreview.getHolder()
				.getSurface());

		this.mediaRecorder.setMaxDuration(180000);
		try {
			this.mediaRecorder.prepare();

			this.mediaRecorder.start();
			Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();
			this.toggleButtons(true);

			c = new CountDownTimer(30000, 1000) {
				int seconds = 0;
				public void onTick(long millisUntilFinished) {
					
					timer.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
					seconds++;
				}

				public void onFinish() {
					AlertDialogBox();
				}
			};

			c.start();
		} catch (Exception e) {

			Toast.makeText(this, "Not record", Toast.LENGTH_SHORT).show();
			this.releaseMediaRecorder();
		}
	}

	// gets called by the button press
	public void stopRecording(View v) {

		c.cancel();
		assert this.mediaRecorder != null;
		try {

			this.mediaRecorder.stop();
			Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
			this.toggleButtons(false);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return;
		} finally {
			this.releaseMediaRecorder();
		}
		if (this.file == null || !this.file.exists()) {
		} else {
 
			String path =getPath(Uri.fromFile(file));
		 
			Intent returnIntent = new Intent(VideoCaptureActivity.this,
					SCQuestionsFragment.class);
			returnIntent.putExtra("result", path); 
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}

	public void AlertDialogBox() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle("Time Up !!");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Your video recording time is 3 minute that is completed \n you want to upload ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								stopRecording(viewStop);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}
 
	private  File getOutputMediaFile() {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Scanchex");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Toast.makeText(VideoCaptureActivity.this, "Can't Record Video",
						Toast.LENGTH_SHORT).show();
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
	 
		 
		file = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".mp4");
		 

		return file;
	}
 

    protected void onStop(){
    	super.onStop();
    }
 
 
	
	public Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}
	
	
	public String getPath(Uri uri) {

		  String result;
		    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		    if (cursor == null) { // Source is Dropbox or other similar local file path
		        result = uri.getPath();
		    } else { 
		        cursor.moveToFirst(); 
		        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		        result = cursor.getString(idx);
		        cursor.close();
		    }
		    return result;
	}
}
