package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint.Join;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCLoginScreen extends BaseActivity{


	private EditText cId;
	private EditText username;
	private EditText password;
	String IMEI ;
	
	String loginresponse ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_login_screen);
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.loginScreen);
		layout.setBackgroundColor(SCPreferences.getColor(SCLoginScreen.this));
		
		TelephonyManager telephonyManager = (TelephonyManager)SCLoginScreen.this.getSystemService(Context.TELEPHONY_SERVICE);
		 IMEI = telephonyManager.getDeviceId();
		cId = (EditText)findViewById(R.id.login_companyid_edittext);
		username = (EditText)findViewById(R.id.login_username_edittext);
		password = (EditText)findViewById(R.id.login_password_edittext);
		
		cId.setText(SCPreferences.getPreferences().getCompanyId(this));
		username.setText(SCPreferences.getPreferences().getUserName(this));
		
	}

	public void onLoginClick(View view) {
//		if(SCPreferences.getPreferences().getUserFullName(this).length()<=0){
		if(cId.getText().toString().length()>0){
			if(username.getText().toString().length()>0){
				if(password.getText().toString().length()>0){
					new LoginTask().execute(CONSTANTS.BASE_URL);
				}else{
					showAlert("Info", "Plase enter password first");
				}
			}else{
				showAlert("Info", "Plase enter username first");
			}
		}else {
			showAlert("Info", "Plase enter company id first");
		}
 
	}
	

	public void onForgotPassClick(View view) {
		
		Intent fotgotPass = new Intent(this, SCForgotPasswordScreen.class);
		startActivity(fotgotPass);
	}
	
	
	public void onAboutClick(View view){
		 
		sendWebIntent("http://scanchex.com/about-scanchex/");
	}
	
	public void onPrivacyClick(View view){

 
		sendWebIntent("http://scanchex.com/privacy-policy/");
	}
	
	public void onTermsClick(View view){
 
		sendWebIntent("http://scanchex.com/terms-of-service/");
	}
	
	public void onContactUsClick(View view){
 
		sendWebIntent("http://scanchex.com/contact-us/");
	}
	
 
	
	public void sendWebIntent(String url) {
		Intent webActivity = new Intent(SCLoginScreen.this,SCWebViewActivity.class);
		webActivity.putExtra("url", url);
		startActivity(webActivity);
	}
	
	private class LoginTask extends AsyncTask<String, Integer, Boolean> {
		String IMEI;
		private ProgressDialog pdialog;
		String response;
		
		private String fullName;
		private String logo;
		private String masterKey;
		private String passChange;
		private String levelId;
		private String exist;
		private String message;
		String company_user;
		String employee_card_id;
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {

				String phoneModel = android.os.Build.MODEL;
				String phoneNumber = getMy10DigitPhoneNumber();

				Log.i("LOGIN URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("company_id", cId.getText().toString()));
				listParams.add(new BasicNameValuePair("username", username.getText().toString()));
				listParams.add(new BasicNameValuePair("password", password.getText().toString()));
				listParams.add(new BasicNameValuePair("device_type", "android"));
				listParams.add(new BasicNameValuePair("device_token", Resources.getResources().getPushNotificationId()));
				listParams.add(new BasicNameValuePair("uuid", IMEI));
				listParams.add(new BasicNameValuePair("action", "login"));

				// new params model,phone
				listParams.add(new BasicNameValuePair("model", phoneModel));
				listParams.add(new BasicNameValuePair("phone", phoneNumber));
				String respvalus = "device_token \t"
						+ Resources.getResources().getPushNotificationId()
						+ " uuid \t" + IMEI + "model \t" + phoneModel
						+ "phone number\t" + phoneNumber;
				Log.i("RESPONSE VALUES ", "Resp values>> " + respvalus);
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				exist = obj.getString("exist");
				loginresponse = response;
				if(exist.equals("1")){
					fullName = obj.getString("full_name");
					logo = obj.getString("logo");
					masterKey = obj.getString("master_key");
					passChange = obj.getString("password_change");
					levelId = obj.getString("level_id");
					company_user= obj.getString("company_user");
					employee_card_id= obj.getString("employee_card_id");
					SCPreferences.getPreferences().setUserName(SCLoginScreen.this, username.getText().toString());
					SCPreferences.getPreferences().setCompanyId(SCLoginScreen.this, cId.getText().toString());
					SCPreferences.getPreferences().setClientLogo(SCLoginScreen.this, logo.toString());
					SCPreferences.getPreferences().setUserMasterKey(SCLoginScreen.this, masterKey);
					SCPreferences.getPreferences().setUserFullName(SCLoginScreen.this, fullName);
					SCPreferences.setComapnyUserName(SCLoginScreen.this, company_user);
					SCPreferences.setEmployeeCard(SCLoginScreen.this, employee_card_id);
					
				return true;
				}else{
					message = obj.getString("message");
				
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
		//	Toast.makeText(getApplicationContext(), 
	//			    "Login reponse: "+loginresponse, 160000).show();
			
			
//			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//					getApplicationContext());
//	 
//				// set title
//				alertDialogBuilder.setTitle("Login reponse: ");
//	 
//				// set dialog message
//				alertDialogBuilder
//					.setMessage(loginresponse)
//					.setCancelable(false)
//					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,int id) {
//							// if this button is clicked, close
//							// current activity
//							SCLoginScreen.this.finish();
//						}
//					  })
//					.setNegativeButton("No",new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,int id) {
//							// if this button is clicked, just close
//							// the dialog box and do nothing
//							dialog.cancel();
//						}
//					});
//	 
//					// create alert dialog
//					AlertDialog alertDialog = alertDialogBuilder.create();
//	 
//					// show it
//					alertDialog.show();
				
			
		//	showAlert("Login response", loginresponse);
			
//			try {
//	            Thread.sleep(60000);
//	        } catch (InterruptedException e) {
//	            e.printStackTrace();
//	        }
			
			
			if(result){
				if(levelId.equalsIgnoreCase("Employee")){
					SCPreferences.getPreferences().setUserType(SCLoginScreen.this, CONSTANTS.USER_TYPE_EMPLOYEE);
					Intent mainMenu = new Intent(SCLoginScreen.this, SCMainMenuScreen.class);
					if(username.getText().toString().length()>0){
						mainMenu.putExtra("NAME", fullName);
					}else{
						mainMenu.putExtra("NAME", "Employee name");
					}
					startActivity(mainMenu);
					SCLoginScreen.this.finish();
				}else{
					SCPreferences.getPreferences().setUserType(SCLoginScreen.this, CONSTANTS.USER_TYPE_ADMIN);
					Intent adminMainMenu = new Intent(SCLoginScreen.this, SCAdminMainMenuScreen.class);
					if(username.getText().toString().length()>0){
						adminMainMenu.putExtra("NAME", fullName);
					}else{
						adminMainMenu.putExtra("NAME", "Employer name");
					}
					startActivity(adminMainMenu);
					SCLoginScreen.this.finish();
				}
			}else{
				showAlert("Info", message);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			TelephonyManager telephonyManager = (TelephonyManager)SCLoginScreen.this.getSystemService(Context.TELEPHONY_SERVICE);
			 IMEI = telephonyManager.getDeviceId(); 
			pdialog = new ProgressDialog(SCLoginScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Login");
			pdialog.setMessage("Working...");
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
	
	 private String getMyPhoneNumber(){
		    TelephonyManager mTelephonyMgr;
		    mTelephonyMgr = (TelephonyManager)
		        getSystemService(Context.TELEPHONY_SERVICE); 
		    return mTelephonyMgr.getLine1Number();
		}

	private String getMy10DigitPhoneNumber() {
		String s = getMyPhoneNumber();
		return s;
	}

}
