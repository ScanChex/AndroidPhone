package com.scanchex.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.adapters.SCMessagesAdapter;
import com.scanchex.bo.SCMessageInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCMessageViewScreen extends ListActivity implements OnClickListener{
	
	TextView textViewDate;
	private SCMessagesAdapter adapter;
	String getMessage = "";
	ListView lv ;
	 String recieverId;
	 EditText messageText;
	 String days = "1";
	 TextView buttonToday,buttonLast7day,buttonLast30day,buttonLast60day;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_messageview_screen);
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.messageScreen);
		layout.setBackgroundColor(SCPreferences.getColor(SCMessageViewScreen.this));
		
		lv= new ListView(this);
		lv = getListView();
		recieverId = SCPreferences.getCompanyUserName(SCMessageViewScreen.this)+"";
		
		textViewDate = (TextView)findViewById(R.id.textViewDate);
		buttonToday = (TextView)findViewById(R.id.buttonToday);
		buttonLast7day = (TextView)findViewById(R.id.buttonLast7day);
		buttonLast30day = (TextView)findViewById(R.id.buttonlast30day);
		buttonLast60day = (TextView)findViewById(R.id.buttonlast60day);
		buttonToday.setOnClickListener(this);
		buttonLast7day.setOnClickListener(this);
		buttonLast30day.setOnClickListener(this);
		buttonLast60day.setOnClickListener(this);
		
		setDate();
		
		adapter = new SCMessagesAdapter(this, null);
		setListAdapter(adapter);
		new MessageTask().execute(CONSTANTS.BASE_URL);
	}

	
	public void onReplyClick(View view){
		
		int position = (Integer)view.getTag();
		SCMessageInfo tInfo = (SCMessageInfo)lv.getItemAtPosition(position);
		Resources.getResources().setMessageInfo(tInfo);
			
		showMessgaeDialog("Please enter your reply");
	}
	
	public void onNewMessgae(View v) {
		showMessgaeDialog("Please enter a new message for \n Admin");
	}
	private void showMessgaeDialog(String title) {
		final Dialog dialog = new Dialog(SCMessageViewScreen.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.sc_popup_sendmessage_dialog);
		TextView textViewPopUp = (TextView)dialog.findViewById(R.id.textViewPopUp);
		textViewPopUp.setText(title);
		messageText = (EditText)dialog.findViewById(R.id.editTextMessage);
		Button cancel = (Button)dialog.findViewById(R.id.buttonCancel);
		Button send = (Button)dialog.findViewById(R.id.buttonSend);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getMessage = messageText.getText().toString();
				new SendMessageTask().execute(CONSTANTS.BASE_URL);
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}


	public void onBackClick(View view) {
		
		this.finish();

	}
	
	private void setDate() {
		// Set Date
		Calendar cal = Calendar.getInstance();
		int dayofyear = cal.get(Calendar.DAY_OF_YEAR);
		int year = cal.get(Calendar.YEAR);
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
		int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
		SimpleDateFormat month_date = new SimpleDateFormat("MMM");
		String month_name = month_date.format(cal.getTime());
		textViewDate.setText(month_name + " " + dayofmonth+  ", "
				+ year);

	}

	
	
	private class MessageTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;

		private Vector<SCMessageInfo> vector;
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("MESSAGE URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("username", SCPreferences.getPreferences().getUserName(SCMessageViewScreen.this)));
				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(SCMessageViewScreen.this)));
				listParams.add(new BasicNameValuePair("period", days));
				
				listParams.add(new BasicNameValuePair("action", "messages"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				JSONArray arr  = obj.getJSONArray("msg");
				vector = new Vector<SCMessageInfo>();
				adapter.setAssetTicetData(vector);
				if(arr!=null && arr.length()>0){
				
					for(int i=0; i<arr.length(); i++){
						SCMessageInfo mInfo = new SCMessageInfo();
						JSONObject jObj = arr.getJSONObject(i);
						mInfo.messageId = jObj.getString("message_id");
						mInfo.message = jObj.getString("message");
						mInfo.senderId = jObj.getString("sender_id");
						mInfo.senderName = jObj.getString("sender_name");
						mInfo.senderPhoto = jObj.getString("sender_photo");						
						mInfo.receiverId = jObj.getString("receiver_id");
						mInfo.receiverName = jObj.getString("receiver_name");
						mInfo.receiverPhoto = jObj.getString("receiver_photo");
						mInfo.dateTime = jObj.getString("date_time");
						vector.add(mInfo);
						adapter.setAssetTicetData(vector);
					}
					return true;
				}else{
					return false;
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
			//if(result){
				adapter.notifyDataSetChanged();
			//}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCMessageViewScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Messages");
			pdialog.setMessage("Loading...");
			pdialog.show();
		}
	}
	
	
	private class SendMessageTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		String messgae;

		private Vector<SCMessageInfo> vector;
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("MESSAGE URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("sender_id", SCPreferences.getPreferences().getUserName(SCMessageViewScreen.this)));
				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(SCMessageViewScreen.this)+""));
				listParams.add(new BasicNameValuePair("receiver_id", recieverId));
				listParams.add(new BasicNameValuePair("message", getMessage));
				listParams.add(new BasicNameValuePair("action", "send_message"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				messgae = obj.getString("msg");
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
			if(result){
				Toast.makeText(getApplicationContext(), messgae, Toast.LENGTH_LONG).show();
				messageText.setText("");
				new MessageTask().execute(CONSTANTS.BASE_URL);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCMessageViewScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Message");
			pdialog.setMessage("Sending...");
			pdialog.show();
		}
	}
	
	private void showAlert(String title, String message){
		
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setIcon(R.drawable.info_icon)
		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		}).show();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonToday:
			days = "1";
			buttonToday.setTextColor(0xff000000);
			buttonLast7day.setTextColor(0xffCCCCCC);
			buttonLast30day.setTextColor(0xffCCCCCC);
			buttonLast60day.setTextColor(0xffCCCCCC);
			new MessageTask().execute(CONSTANTS.BASE_URL);
			break;
			
		case R.id.buttonLast7day:
			days = "7";
			buttonToday.setTextColor(0xffCCCCCC);
			buttonLast7day.setTextColor(0xff000000);
			buttonLast30day.setTextColor(0xffCCCCCC);
			buttonLast60day.setTextColor(0xffCCCCCC);
			new MessageTask().execute(CONSTANTS.BASE_URL);
			break;
			
		case R.id.buttonlast30day:
			days = "30";
			buttonToday.setTextColor(0xffCCCCCC);
			buttonLast7day.setTextColor(0xffCCCCCC);
			buttonLast30day.setTextColor(0xff000000);
			buttonLast60day.setTextColor(0xffCCCCCC);
			new MessageTask().execute(CONSTANTS.BASE_URL);
			break;
			
		case R.id.buttonlast60day:
			days = "60";
			buttonToday.setTextColor(0xffCCCCCC);
			buttonLast7day.setTextColor(0xffCCCCCC);
			buttonLast30day.setTextColor(0xffCCCCCC);
			buttonLast60day.setTextColor(0xff000000);
			new MessageTask().execute(CONSTANTS.BASE_URL);
			break;
			

		default:
			break;
		}
		
	}
}
