package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.SCPreferences;

public class SCForgotPasswordScreen extends Activity{
	
	private EditText cId;
	private EditText username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_forgotpassword_screen);
		LinearLayout layout = (LinearLayout)findViewById(R.id.forgetScreen);
		layout.setBackgroundColor((SCPreferences.getColor(SCForgotPasswordScreen.this)));
		
		cId = (EditText)findViewById(R.id.resetpass_companyid_edittext);
		username = (EditText)findViewById(R.id.resetpass_username_edittext);
	}
	
	public void onResetPassClick(View view) {
		new ResetPassTask().execute(CONSTANTS.BASE_URL);
	}

	public void onBackClick(View view) {
		
		this.finish();

	}
	
	
	private class ResetPassTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		String message;
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("RESET PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("company_id", cId.getText().toString()));
				listParams.add(new BasicNameValuePair("username", username.getText().toString()));
				listParams.add(new BasicNameValuePair("action", "reset_password"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				message = obj.getString("error");
				
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
			Toast.makeText(SCForgotPasswordScreen.this, message, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCForgotPasswordScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Login");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}


}
