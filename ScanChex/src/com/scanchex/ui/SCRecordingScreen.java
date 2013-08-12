package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCRecordingScreen extends Activity{
	
	private TextView audioStatus;
	private String contentType;
	
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_recording_screen);
		audioStatus = (TextView)findViewById(R.id.audio_status);
		
	}
	
	
	public void onStartClick(View view) {
		
		startRecording();
	}

	public void onStopClick(View view) {
		
		stopRecording();
	}
	
	public void onBackClick(View view) {
		
		this.finish();
	}
	
	
	private void startRecording() {
    	audioStatus.setText("Recording...");
		AudioRecordTest();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        Log.i("FILE PATH", mFileName);
        mRecorder.setOutputFile(mFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }
	
	 private void stopRecording() {
		 audioStatus.setText("Stopped");
		 mRecorder.stop();
		 mRecorder.release();
		 mRecorder = null;
		 contentType =  getMimeType(mFileName);
	     new UploadTask().execute(CONSTANTS.BASE_URL);
	 }
	 
	 public void AudioRecordTest() {
		 mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		 mFileName += "/audiorecordtest.mp3";
	 }

	 public static String getMimeType(String url){
		 String type = null;
		 String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		 if (extension != null) {
			 MimeTypeMap mime = MimeTypeMap.getSingleton();
			 type = mime.getMimeTypeFromExtension(extension);
		 }
		 return type;
	 }
	 
	 @Override
	 public void onPause() {
		 super.onPause();
		 if (mRecorder != null) {
			 mRecorder.release();
			 mRecorder = null;
		 }

	 }
	 
	/////////////Uplaoding Voice///////////
	private class UploadTask extends AsyncTask<String, Void, Boolean> {
			
			private ProgressDialog pdialog;
			private String serverResp;

			private String status;
			@Override
			protected Boolean doInBackground(String... path) {
			
				 String url = path[0];
				 HttpClient httpClient = new DefaultHttpClient();
				 HttpContext localContext = new BasicHttpContext();
				 HttpPost httpPost = new HttpPost(url);
				 Log.i("URL", "<><><>"+url);
				 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				 nameValuePairs.add(new BasicNameValuePair("master_key", ""+SCPreferences.getPreferences().getUserMasterKey(SCRecordingScreen.this)));
				 nameValuePairs.add(new BasicNameValuePair("history_id", Resources.getResources().getTicketHistoryId()));
//				 nameValuePairs.add(new BasicNameValuePair("history_id", "97"));
				 nameValuePairs.add(new BasicNameValuePair("action", "upload"));
				 nameValuePairs.add(new BasicNameValuePair("type", "voice"));
				 nameValuePairs.add(new BasicNameValuePair("file", mFileName));
				 nameValuePairs.add(new BasicNameValuePair("file_name", "ScanCheXAudio"+new Date().getTime()));
				 Log.i("FILE PATH TO BE UPLOADED >CTYPE>"+contentType, "<<<<>>>>>"+mFileName);
				 
				 try {
					 MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					 
					 for(int index=0; index < nameValuePairs.size(); index++) {
				            if(nameValuePairs.get(index).getName().equalsIgnoreCase("file")) {
				                // If the key equals to "image", we use FileBody to transfer the data
				            
				                entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue()),contentType));
				            } else {
				                // Normal string data
				                entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
				            }
					 }

					 
				     httpPost.setEntity(entity);
				     HttpResponse response = httpClient.execute(httpPost, localContext);
					 StringBuilder sb=null;
					 String line = null;
					 if(response!=null){
						 InputStream in = response.getEntity().getContent();
						 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						 sb = new StringBuilder();
						 while((line = reader.readLine()) != null){
							 sb.append(line + "\n");
						 }
					 }
					 serverResp = sb.toString();
					 Log.i("SERVER RESP", "<><><>"+serverResp);	
					 JSONObject obj = new JSONObject(serverResp);
					 			
					 if(serverResp.contains("error")){
						 status = obj.getString("error");
						 return false;
					 }else{		
						 status = obj.getString("status");
						 return true;
					 }
				 }catch(Exception e){
					 e.printStackTrace();
				 }
		         return Boolean.FALSE;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				Log.i("DONE DONE", "DONE DONE");
				pdialog.dismiss();
				pdialog = null;
				if (result) {
					File file = new File(mFileName);
					boolean deleted = file.delete();
					Log.i("IS Deleted", "<><> "+deleted);
					showAlertDialog("Info", status);
				} else {
					showAlertDialog("Info", status);
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pdialog = new ProgressDialog(SCRecordingScreen.this);
				pdialog.setCancelable(false);
				pdialog.setTitle("Uploading Voice");
				pdialog.setMessage("Please Wait...");
				pdialog.show();

			}

			private void showAlertDialog(String title, String message) {
				new AlertDialog.Builder(SCRecordingScreen.this)
				.setIcon(R.drawable.info_icon)
				.setTitle(title)
				.setMessage(message)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							
					public void onClick(DialogInterface dialog, int which) {
						SCRecordingScreen.this.finish();
								
					}
				}).show();
			}
		}


}
