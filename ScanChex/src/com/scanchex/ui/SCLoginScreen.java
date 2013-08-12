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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.SCPreferences;

public class SCLoginScreen extends Activity{


	private EditText cId;
	private EditText username;
	private EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_login_screen);
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
//		}else{
//			
//			Intent mainMenu = new Intent(SCLoginScreen.this, SCMainMenuScreen.class);
//			mainMenu.putExtra("NAME", SCPreferences.getPreferences().getUserFullName(this));
//			startActivity(mainMenu);
//			
//		}
	}
	

	public void onForgotPassClick(View view) {
		
		Intent fotgotPass = new Intent(this, SCForgotPasswordScreen.class);
		startActivity(fotgotPass);
	}
	
	
	
	private class LoginTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		
		String fullName;
		String logo;
		String masterKey;
		String passChange;
		String levelId;
		String exist;
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("LOGIN URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("company_id", cId.getText().toString()));
				listParams.add(new BasicNameValuePair("username", username.getText().toString()));
				listParams.add(new BasicNameValuePair("password", password.getText().toString()));
				listParams.add(new BasicNameValuePair("action", "login"));
				response = new HttpWorker().getData(params[0], listParams);
				response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				exist = obj.getString("exist");
				if(exist.equals("1")){
					fullName = obj.getString("full_name");
					logo = obj.getString("logo");
					masterKey = obj.getString("master_key");
					passChange = obj.getString("password_change");
					levelId = obj.getString("level_id");
					SCPreferences.getPreferences().setUserName(SCLoginScreen.this, username.getText().toString());
					SCPreferences.getPreferences().setCompanyId(SCLoginScreen.this, cId.getText().toString());
					SCPreferences.getPreferences().setUserMasterKey(SCLoginScreen.this, masterKey);
					SCPreferences.getPreferences().setUserFullName(SCLoginScreen.this, fullName);
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
			if(result){
				if(levelId.equalsIgnoreCase("Employee")){
					Intent mainMenu = new Intent(SCLoginScreen.this, SCMainMenuScreen.class);
					if(username.getText().toString().length()>0){
						mainMenu.putExtra("NAME", fullName);
					}else{
						mainMenu.putExtra("NAME", "Employee name");
					}
					startActivity(mainMenu);
				}else{
					
					Intent adminMainMenu = new Intent(SCLoginScreen.this, SCAdminMainMenuScreen.class);
					if(username.getText().toString().length()>0){
						adminMainMenu.putExtra("NAME", fullName);
					}else{
						adminMainMenu.putExtra("NAME", "Employer name");
					}
					startActivity(adminMainMenu);
				}
			}else{
				showAlert("Info", "Invalid username or password");
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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
				// TODO Auto-generated method stub
				
			}
		}).show();
	}

}
