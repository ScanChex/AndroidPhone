package com.scanchex.ui;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scanchex.bo.SCAdminAssetDetailsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;


public class SCAdminShowAssetDetailsScreen extends Activity implements OnClickListener{
	
	
	private ImageView assetImage;	
	private TextView assetId;
	private TextView assetDescription;
	private TextView serial;
	private TextView assetAddress;
	private TextView lastScan;
	private TextView employee;
	
	private Button takePhotoBtn;
	private Button menuBtn;
	private LocationManager locManager;
	private double longitude;
	private double latitude;
	    
	private String strLongitude;
	private String strLatitude;
	
	public static String selectedImagePath = null;
	public static String contentType = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_showasset_details_screen);
		SCAdminAssetDetailsInfo assetInfo = Resources.getResources().getAssetDetailInfo();
		
		assetImage = (ImageView)findViewById(R.id.asset_image_id);	
		assetId = (TextView)findViewById(R.id.asset_id);
		assetDescription = (TextView)findViewById(R.id.des_id);
		serial = (TextView)findViewById(R.id.serial_id);
		assetAddress = (TextView)findViewById(R.id.add_id);
		lastScan = (TextView)findViewById(R.id.lastscan_id);
		employee = (TextView)findViewById(R.id.employee_id);	
		takePhotoBtn = (Button)findViewById(R.id.take_picture_button);
		menuBtn = (Button)findViewById(R.id.menu_button);
		
		
		assetImage.setImageBitmap(assetInfo.assetImage);
		assetId.setText(assetInfo.assetId);
		assetDescription.setText(assetInfo.assetDescription);
		serial.setText(assetInfo.assetSerialNum);
		assetAddress.setText(assetInfo.street+"\n"+assetInfo.city+", "+assetInfo.state+" "+assetInfo.postalCode);
		lastScan.setText(assetInfo.assetLastScan);
		employee.setText(assetInfo.assetEmployee);
		
		if(Resources.getResources().isFromAdminTakePicture()){
			takePhotoBtn.setText("Take Photo");
		}else{
			takePhotoBtn.setText("Submit");
		}
		selectedImagePath = null;
		takePhotoBtn.setOnClickListener(this);
		menuBtn.setOnClickListener(this);
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        Criteria criteria = new Criteria();
        String provider = locManager.getBestProvider(criteria, false);
        Location location = locManager.getLastKnownLocation(provider);
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            strLatitude = ""+latitude;
            strLongitude = ""+longitude;
            Log.i("LOCATION LAT>>"+latitude,"Longitute" +longitude);
        }else{
        	strLatitude = "Not Found";
        	strLongitude = "Not Found";
        }  
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(selectedImagePath!=null && selectedImagePath.length()>0){
			takePhotoBtn.setText("Submit");
		}
	}


	@Override
	public void onClick(View v) {
		
		if(v==takePhotoBtn){
			if(Resources.getResources().isFromAdminTakePicture() && selectedImagePath == null){
				
				Intent intent = new Intent(this, SCAdminImageTakenScreen.class);
				startActivity(intent);
			}else if(selectedImagePath!=null && selectedImagePath.length()>0){
				new UploadTask().execute(CONSTANTS.BASE_URL);
			}else{
				new UpdateAdminLocationTask().execute(CONSTANTS.BASE_URL);
			}
		}else if(v==menuBtn) {
			this.finish();
		}
		
	}

	
	private class UploadTask extends AsyncTask<String, Void, Boolean> {
		
		private ProgressDialog pdialog;
		private String serverResp;
		SCAdminAssetDetailsInfo assetInfo = Resources.getResources().getAssetDetailInfo();

		private String status;
		private String message;
		
		@Override
		protected Boolean doInBackground(String... path) {
		
			 String url = path[0];
			 HttpClient httpClient = new DefaultHttpClient();
			 HttpContext localContext = new BasicHttpContext();
			 HttpPost httpPost = new HttpPost(url);
			 Log.i("URL", "<><><>"+url);
			 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			 nameValuePairs.add(new BasicNameValuePair("master_key", ""+SCPreferences.getPreferences().getUserMasterKey(SCAdminShowAssetDetailsScreen.this)));
			 nameValuePairs.add(new BasicNameValuePair("company_id", ""+SCPreferences.getPreferences().getCompanyId(SCAdminShowAssetDetailsScreen.this)));
			 nameValuePairs.add(new BasicNameValuePair("asset_id", assetInfo.assetScanningCode));
			 nameValuePairs.add(new BasicNameValuePair("latitude", strLatitude));
			 nameValuePairs.add(new BasicNameValuePair("longitude", strLongitude));
			 nameValuePairs.add(new BasicNameValuePair("action", "update_asset_location_photo"));
			 nameValuePairs.add(new BasicNameValuePair("type", "both"));
//			 nameValuePairs.add(new BasicNameValuePair("file_name", "ScanCheX"+new Date().getTime()));
			 nameValuePairs.add(new BasicNameValuePair("file", selectedImagePath));	 
			 Log.i("FILE PATH TO BE UPLOADED >CTYPE>"+contentType, "<<<<>>>>>"+selectedImagePath);
			 
			 try {
				 MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				 
				 for(int index=0; index < nameValuePairs.size(); index++) {
					 if(nameValuePairs.get(index).getName().equalsIgnoreCase("file")) {
						 // If the key equals to "image", we use FileBody to transfer the data			            
						 entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue()),"multipart/form-data"));
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
				File file = new File(SCAdminImageTakenScreen.selectedImagePath);
				boolean deleted = file.delete();
				showAlertDialog("Info", status);
			} else {
				showAlertDialog("Info", status);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCAdminShowAssetDetailsScreen.this);
			pdialog.setCancelable(false);
			pdialog.setTitle("Image & Location");
			pdialog.setMessage("Please Wait...");
			pdialog.show();

		}
	}
	
	private void showAlertDialog(String title, String message) {
		new AlertDialog.Builder(SCAdminShowAssetDetailsScreen.this)
		.setIcon(R.drawable.info_icon)
		.setTitle(title)
		.setMessage(message)
		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						
			public void onClick(DialogInterface dialog, int which) {
				SCAdminTapToScanScreen.isFromAssetDetail = true;
				SCAdminShowAssetDetailsScreen.this.finish();							
			}
		}).show();
	}
	
	
	
	 private class UpdateAdminLocationTask extends AsyncTask<String, Integer, Boolean> {

 		private ProgressDialog pdialog;
 		String response;
 		private String status;
 		SCAdminAssetDetailsInfo assetInfo = Resources.getResources().getAssetDetailInfo();
 		@Override
 		protected Boolean doInBackground(String... params) {
 			try {
 				
 				String url = params[0];
 				Log.i("URL", "<><><>"+url);
 				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
 				listParams.add(new BasicNameValuePair("master_key", ""+SCPreferences.getPreferences().getUserMasterKey(SCAdminShowAssetDetailsScreen.this)));
 				listParams.add(new BasicNameValuePair("company_id", ""+SCPreferences.getPreferences().getCompanyId(SCAdminShowAssetDetailsScreen.this)));
 				listParams.add(new BasicNameValuePair("asset_id", assetInfo.assetScanningCode));
 				listParams.add(new BasicNameValuePair("latitude", strLatitude));
 				listParams.add(new BasicNameValuePair("longitude", strLongitude));
 				listParams.add(new BasicNameValuePair("action", "update_asset_location_photo"));
 				listParams.add(new BasicNameValuePair("type", "location"));
 				response = new HttpWorker().getData(params[0], listParams);
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
			pdialog = null;
			showAlertDialog("Info", status);		
 		}

 		@Override
 		protected void onPreExecute() {
 			super.onPreExecute();
 			pdialog = new ProgressDialog(SCAdminShowAssetDetailsScreen.this);
 			pdialog.setIcon(R.drawable.info_icon);
 			pdialog.setTitle("Location");
 			pdialog.setMessage("Working...");
 			pdialog.show();
 		}
 	}

}
