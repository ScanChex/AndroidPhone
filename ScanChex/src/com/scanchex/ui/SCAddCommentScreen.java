package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.SCPreferences;

public class SCAddCommentScreen extends Activity{
	
	
	private EditText commentText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_addcomment_screen);
		commentText = (EditText)findViewById(R.id.addcomment_edittext);
	}
	
	public void onDoneClick(View view){
		
	}

	public void onSubmitClick(View view){
		new UploadNotesTask().execute(CONSTANTS.BASE_URL);
	}
	
	public void onBackClick(View view){
		this.finish();
	}
	
	
	private class UploadNotesTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		
		String status;
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("LOGIN URL", "<><>" + params[0]);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("master_key", ""+SCPreferences.getPreferences().getUserMasterKey(SCAddCommentScreen.this)));
//				nameValuePairs.add(new BasicNameValuePair("history_id", Resources.getResources().getTicketHistoryId()));
				nameValuePairs.add(new BasicNameValuePair("history_id", "97"));
				nameValuePairs.add(new BasicNameValuePair("action", "upload"));
				nameValuePairs.add(new BasicNameValuePair("type", "notes"));
				nameValuePairs.add(new BasicNameValuePair("notes",  commentText.getText().toString()));;
				nameValuePairs.add(new BasicNameValuePair("file_name", "ScanCheX Doc"));
				response = new HttpWorker().getData(params[0], nameValuePairs);
				response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				if(response.contains("error")){
					 status = obj.getString("error");
					 return false;
				 }else{		
					 status = obj.getString("status");
					 return true;
				 }
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
				showAlertDialog("Info", status);
		
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCAddCommentScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Uploading Notes");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}
	
	private void showAlertDialog(String title, String message) {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.info_icon)
		.setTitle(title)
		.setMessage(message)
		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					
			public void onClick(DialogInterface dialog, int which) {
				SCAddCommentScreen.this.finish();
						
			}
		}).show();
	}
	
	
	
	
}
