package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;

public class SCDetailsFragmentScreen extends FragmentActivity{
	
	SCMapFragment mapFragment;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.sc_detailsfragment_screen);
		SCAssetsFragment subregionFragment = new SCAssetsFragment();
    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    	fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
//    	fragmentTransaction.addToBackStack(null);
    	fragmentTransaction.commit();
	}
	
	public void onCloseTicketClick(View view){
		showAlert();
	}
	
	public void onAssetsClick(View view){
		if(mapFragment!=null){
			mapFragment = null;
		}
		SCAssetsFragment subregionFragment = new SCAssetsFragment();
    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    	fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
    	fragmentTransaction.commit();
	}
	
	public void onQuestionsClick(View view){
		if(mapFragment!=null){
			mapFragment = null;
		}
		SCQuestionsFragment subregionFragment = new SCQuestionsFragment();
    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    	fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
    	fragmentTransaction.commit();
	}
	
	public void onDocumentsClick(View view){
		if(mapFragment!=null){
			mapFragment = null;
		}
		SCDocumentsFragment subregionFragment = new SCDocumentsFragment();
    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    	fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
    	fragmentTransaction.commit();
	}
	
	public void onHistoryClick(View view){
		if(mapFragment!=null){
			mapFragment = null;
		}
		SCHistoryFragment subregionFragment = new SCHistoryFragment();
    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    	fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
    	fragmentTransaction.commit();
	}
	
	public void onMapClick(View view){
		if(mapFragment!=null){
			mapFragment = null;
		}
		mapFragment = new SCMapFragment();
    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    	fragmentTransaction.replace(R.id.fragment_container, mapFragment);
    	fragmentTransaction.commit();
	}

	
	private void showAlert(){
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.message_info_icon)
		.setTitle("Info")
		.setMessage("Are you sure, you want to close a ticket?")
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
				if(Resources.getResources().isQuestionsSubmitted() || Resources.getResources().getQuestionsData()==null){
					if(tInfo.ticketNumberOfScans == Resources.getResources().getTotalScans()){
						
						new CloseTicketTask().execute(CONSTANTS.BASE_URL);
					}else{
						showOptionAlert();
					}
				}else{
					showInfoAlert("Info", "Please submit answers of all question first");
				}
			}
		}).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			showAlert();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void showOptionAlert(){
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.message_info_icon)
		.setTitle("Info")
		.setMessage("Double scan required before close ticket. Do you want another scan?")
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Resources.getResources().setForDoubleScan(true);
				Intent i = new Intent(SCDetailsFragmentScreen.this, SCCameraPeviewScreen.class);
				startActivity(i);
			}
		}).show();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(Resources.getResources().isForDoubleScan()){
			Resources.getResources().setForDoubleScan(false);
			Toast.makeText(this, "CLOSING TICKET {WORKING...}", Toast.LENGTH_SHORT).show();
			new CloseTicketTask().execute(CONSTANTS.BASE_URL);
			
		}
	}
	
	public void showInfoAlert(String title, String message){
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
	
	
	
	////////////////////Close Ticket//////////////////
	private class CloseTicketTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		private String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
		@Override
		protected Boolean doInBackground(String... params) {
	
			try {
				
				Log.i("Close Ticket URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("ticket_id", tInfo.ticketTableId));
				listParams.add(new BasicNameValuePair("history_id", Resources.getResources().getTicketHistoryId()));				
				listParams.add(new BasicNameValuePair("action", "close_scan_ticket"));
				response = new HttpWorker().getData(params[0], listParams);
				response = response.substring(3);
				Log.i("RESPONSE", "Close Ticket Resp>> " + response);
				JSONObject obj = new JSONObject(response);
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
			Intent intent = new Intent(getApplicationContext(), SCMainMenuScreen.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCDetailsFragmentScreen.this);
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Closing Ticket");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}	
	
	
	
	
}
