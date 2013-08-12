package com.scanchex.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class SCHistoryImageViewScreen extends Activity{
	
	private ImageView image;
	private String imageUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_history_image_screen);
		image = (ImageView)findViewById(R.id.image);
		imageUrl = getIntent().getExtras().getString("PATH");
		new HistoryImageTask().execute("");
	}
	
	public void onBackClick(View view){
		this.finish();
	}
	
	
	////////////////////ASYNC TASK//////////////////
	 private class HistoryImageTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		Bitmap historyPhoto;
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				historyPhoto = downloadFile(imageUrl);
				return true;
			} catch (Exception e) {
				Log.e("Exception", e.getMessage(), e);
			}
			return Boolean.FALSE;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pdialog.dismiss();
			image.setImageBitmap(historyPhoto);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCHistoryImageViewScreen.this);
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("History Image");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}
	 
	 private Bitmap downloadFile(String fileUrl) {
			
		 if(fileUrl.equals("null"))return null;
			Bitmap bmImg;
			URL myFileUrl = null;
			try {
				Log.i("File URL", "<>" + fileUrl);
				myFileUrl = new URL(fileUrl);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bmImg = BitmapFactory.decodeStream(is);
				return bmImg;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

}
